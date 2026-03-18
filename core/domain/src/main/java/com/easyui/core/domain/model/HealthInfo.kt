package com.easyui.core.domain.model

data class HealthInfo(
    val fullName: String = "",
    val age: String = "",
    val bloodGroup: String = "",
    val allergies: String = "",
    val medicalConditions: String = "",
    val medicines: String = "",
    val doctorOrEmergencyContact: String = "",
    val notes: String = "",
) {
    fun hasAnyValue(): Boolean =
        fullName.isNotBlank() ||
            age.isNotBlank() ||
            bloodGroup.isNotBlank() ||
            allergies.isNotBlank() ||
            medicalConditions.isNotBlank() ||
            medicines.isNotBlank() ||
            doctorOrEmergencyContact.isNotBlank() ||
            notes.isNotBlank()
}
