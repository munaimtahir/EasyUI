package com.easyui.core.domain.rules

object ContactTileRules {
    fun validate(displayName: String, phoneNumber: String): String? {
        if (displayName.trim().isBlank()) {
            return "Enter a contact name."
        }
        val digits = phoneNumber.count { it.isDigit() }
        if (digits < 3) {
            return "Enter a phone number."
        }
        return null
    }

    fun initials(displayName: String): String =
        displayName
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
            .ifBlank { "?" }

    fun photoFallback(photoUri: String?, displayName: String): String =
        if (photoUri.isNullOrBlank()) initials(displayName) else initials(displayName)
}
