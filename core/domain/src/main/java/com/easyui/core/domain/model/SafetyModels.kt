package com.easyui.core.domain.model

data class CaregiverContact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val photoUri: String? = null,
)

data class EmergencyNumber(
    val label: String,
    val phoneNumber: String,
)
