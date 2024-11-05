package org.example.entity.request;

public record SignInRequest(String email, String password, boolean remember) {
}
