package com.victolee.board.oom;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@RestController
public class OomKill {


    private List<byte[]> memoryLeakList = new ArrayList<>();
    private volatile boolean isRunning = false;

    @GetMapping(value = "/oom", produces = "text/event-stream")
    public SseEmitter causeOOM() {
        SseEmitter emitter = new SseEmitter();
        isRunning = true;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                long sequence = 0;
                while (isRunning) {
                    try {
                        memoryLeakList.add(new byte[2048 * 2048]); // 메모리 할당
                        sequence++;
                        emitter.send("data: 메모리 할당 중... 현재 크기: " + sequence * 4 + "MB\n\n");
                        Thread.sleep(100); // 짧은 대기
                    } catch (OutOfMemoryError e) {
                        isRunning = false;
                        emitter.send("data: OOM 발생!\n\n");
                    } catch (Exception e) {
                        emitter.send("data: 에러 발생: " + e.getMessage() + "\n\n");
                        break;
                    }
                }
                if (!isRunning) {
                    emitter.send("data: 종료됨\n\n");
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            } finally {
                emitter.complete();
                executor.shutdown();
            }
        });

        return emitter;
    }

    @GetMapping("/stop")
    public String stopOOM() {
        isRunning = false;
        return "OOM 프로세스가 중지되었습니다.";
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }
}
