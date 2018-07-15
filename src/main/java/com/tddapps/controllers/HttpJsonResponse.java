package com.tddapps.controllers;

import com.tddapps.actions.response.TextMessage;

import java.util.Objects;

public class HttpJsonResponse<T> {
    private final int statusCode;
    private final T body;

    public HttpJsonResponse(int statusCode, T body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public T getBody() {
        return body;
    }

    public static <T> HttpJsonResponse Success(T body){
        return new HttpJsonResponse<>(200, body);
    }

    public static <T> HttpJsonResponse BadRequest(T body) {
        return new HttpJsonResponse<>(400, body);
    }

    public static HttpJsonResponse BadRequestWithMessage(String message){
        return BadRequest(TextMessage.create(message));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HttpJsonResponse)){
            return false;
        }

        HttpJsonResponse that = (HttpJsonResponse)obj;

        if (this.statusCode != that.statusCode){
            return false;
        }

        if (this.body == null){
            return that.body == null;
        }

        return this.body.equals(that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode, body);
    }
}
