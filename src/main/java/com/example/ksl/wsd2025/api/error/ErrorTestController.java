
package com.example.ksl.wsd2025.api.error;

import com.example.ksl.wsd2025.api.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class ErrorTestController {

    // HTTP 500: 서버 내부 오류 상황 재현
    @GetMapping("/error/500")
    public ResponseEntity<?> testInternalServerError() {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        "테스트용 서버 내부 오류가 발생했습니다."
                ));
    }

    // HTTP 503: 서비스 일시 중단 상황 재현
    @GetMapping("/error/503")
    public ResponseEntity<?> testServiceUnavailable() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(
                        "현재 서비스를 일시적으로 이용할 수 없습니다."
                ));
    }
}
