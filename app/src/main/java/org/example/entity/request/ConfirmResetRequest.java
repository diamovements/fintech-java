package org.example.entity.request;

public record ConfirmResetRequest(String username, String code, String newPassword) {
}
