package org.example.entity.request;

public record SignInRequest(String username, String password, boolean remember) {
}
