
package com.example.ksl.wsd2025.api.response;

public class ApiResponse<T> {

    private String status;
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(String status, T data) {
        this.status = status;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", data);
    }

    public static ApiResponse<ErrorData> error(String message) {
        return new ApiResponse<>(
                "error",
                new ErrorData(message)
        );
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static class ErrorData {

        private String message;

        public ErrorData() {
        }

        public ErrorData(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
