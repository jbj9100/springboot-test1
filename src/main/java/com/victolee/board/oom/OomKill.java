package com.victolee.board.oom;

import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

public class OomKill {

    private List<byte[]> memoryLeakList = new ArrayList<>();

    @GetMapping("/oom")
    public String causeOOM() {
        try {
            while (true) {
                // 각각 1MB 크기의 바이트 배열을 계속 추가
                memoryLeakList.add(new byte[1024 * 1024]);
                Thread.sleep(10); // 너무 빠른 메모리 할당을 방지
            }
        } catch (OutOfMemoryError e) {
            return "OOM 발생!";
        } catch (InterruptedException e) {
            return "인터럽트 발생!";
        }
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }
}
