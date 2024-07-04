package com.victolee.board.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@RestController
public class ReadyHealthIndicator implements HealthIndicator {

    @GetMapping("/ready")
    @Override
    public Health health() {
        boolean isReady = checkIfReady();
        System.out.println("health checking");
        if (isReady) {
            return Health.up().withDetail("ready", "The application is ready to serve requests.").build();
        } else {
            return Health.down().withDetail("ready", "The application is not ready yet.").build();
        }
    }

    private boolean checkIfReady() {
        String url = "http://localhost:8080/was"; // 확인하고자 하는 서비스의 URL

        RestTemplate restTemplate = new RestTemplate();

        try {
            // /was 경로로 GET 요청을 보내고 응답 상태 코드를 확인
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // HTTP 상태 코드가 200 (OK)이면 서비스가 정상으로 간주
            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpStatusCodeException ex) {
            // 서비스가 비정상적이거나 응답하지 않으면 false 반환
            return false;
        } catch (Exception ex) {
            // 기타 예외 발생 시도 false 반환
            return false;
        }
    }
}
