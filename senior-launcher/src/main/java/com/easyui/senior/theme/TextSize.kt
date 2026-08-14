package com.easyui.senior.theme

enum class TextSize(val storageValue: String, val scaleFactor: Float) {
    Small("small", 0.90f),
    Normal("normal", 1.00f),
    Large("large", 1.20f),
    Larger("larger", 1.40f),
}

fun textSizeFromStorage(raw: String?): TextSize {
    val value = raw?.trim().orEmpty()
    return TextSize.entries.firstOrNull { it.storageValue == value } ?: TextSize.Normal
}
