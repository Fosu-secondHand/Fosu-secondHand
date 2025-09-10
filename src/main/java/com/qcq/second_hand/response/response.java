package com.qcq.second_hand.response;

public class response<T> {
    private int code;
    private String message;
    private T data;

    public static <T> response<T> success(T data) {
        return new response<>(200, "success", data);
    }

    public response(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
