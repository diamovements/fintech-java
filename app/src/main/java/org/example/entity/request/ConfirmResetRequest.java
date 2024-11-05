package org.example.entity.request;

public record ConfirmResetRequest(String email, String code, String newPassword) {
}
