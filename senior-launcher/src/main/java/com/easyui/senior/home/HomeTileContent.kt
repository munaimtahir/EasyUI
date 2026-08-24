package com.easyui.senior.home

import com.easyui.senior.apps.LaunchableApp

sealed interface HomeTileContent {
    data class App(val ref: AppComponentRef) : HomeTileContent
    data class Contact(val name: String, val phone: String, val action: ContactAction) : HomeTileContent
    data class Widget(val type: LocalWidgetType) : HomeTileContent
}

enum class ContactAction { Dial, SMS }
enum class LocalWidgetType { Clock, Date, Note }

/** Escapes '\' and '|' so a literal '|' in [value] can never be mistaken for the field delimiter. */
private fun escapePipeField(value: String): String =
    value.replace("\\", "\\\\").replace("|", "\\|")

/** Splits [value] on '|' that isn't escaped, unescaping "\|" and "\\" back to their literal form. */
private fun splitEscapedPipeFields(value: String): List<String> {
    val parts = mutableListOf<String>()
    val current = StringBuilder()
    var i = 0
    while (i < value.length) {
        val c = value[i]
        if (c == '\\' && i + 1 < value.length) {
            current.append(value[i + 1])
            i += 2
        } else if (c == '|') {
            parts.add(current.toString())
            current.clear()
            i++
        } else {
            current.append(c)
            i++
        }
    }
    parts.add(current.toString())
    return parts
}

fun HomeTileContent.toStorageString(): String = when (this) {
    is HomeTileContent.App -> "app:${ref.toStorageString()}"
    is HomeTileContent.Contact -> "contact:${escapePipeField(name)}|${escapePipeField(phone)}|${action.name}"
    is HomeTileContent.Widget -> "widget:${type.name}"
}

fun homeTileContentFromStorageString(raw: String?): HomeTileContent? {
    val value = raw?.trim().orEmpty()
    if (value.isEmpty()) return null

    if (!value.contains(":")) {
        // Backward compatibility: assume App
        return appComponentRefFromStorageString(value)?.let { HomeTileContent.App(it) }
    }

    val parts = value.split(":", limit = 2)
    val type = parts[0]
    val data = parts[1]

    return when (type) {
        "app" -> appComponentRefFromStorageString(data)?.let { HomeTileContent.App(it) }
        "contact" -> {
            val contactParts = splitEscapedPipeFields(data)
            if (contactParts.size >= 3) {
                val action = try { ContactAction.valueOf(contactParts[2]) } catch (e: Exception) { ContactAction.Dial }
                HomeTileContent.Contact(contactParts[0], contactParts[1], action)
            } else null
        }
        "widget" -> {
            val widgetType = try { LocalWidgetType.valueOf(data) } catch (e: Exception) { null }
            widgetType?.let { HomeTileContent.Widget(it) }
        }
        else -> null
    }
}
