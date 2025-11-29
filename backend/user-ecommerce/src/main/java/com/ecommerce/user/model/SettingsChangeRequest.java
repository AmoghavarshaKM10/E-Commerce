package com.ecommerce.user.model;

public record SettingsChangeRequest(ChangeType changeType,
        String newEmail,
        String currentPassword,
        String newPassword,
        String newAddress) {

}
