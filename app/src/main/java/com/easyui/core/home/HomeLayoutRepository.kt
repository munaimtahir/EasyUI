package com.easyui.core.home

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.easyui.core.storage.coreDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class HomeLayoutRepository(
    private val context: Context,
    private val fallbackSpec: HomeGridSpec = HomeGridSpec(),
) {
    private val pageCountKey = intPreferencesKey("home_page_count")
    private val gridColumnsKey = intPreferencesKey("home_grid_columns")
    private val gridRowsKey = intPreferencesKey("home_grid_rows")

    private fun keyFor(slot: HomeSlotId): Preferences.Key<String> =
        stringPreferencesKey("home_slot_${slot.pageIndex}_${slot.slotIndex}")

    private val slotKeyRegex = Regex("^home_slot_(\\d+)_(\\d+)$")

    private fun parseSlotKey(name: String): HomeSlotId? {
        val match = slotKeyRegex.matchEntire(name) ?: return null
        val (page, slot) = match.destructured
        return HomeSlotId(page.toInt(), slot.toInt())
    }

    /**
     * Collects existing home-slot entries in visual (pageIndex, slotIndex) order — DataStore's
     * own map iteration order does not match that order, so reading it unsorted would scramble
     * item positions whenever the page count or grid size changes.
     */
    private fun collectItemsInVisualOrder(prefs: Preferences): List<HomeTileContent> =
        prefs.asMap().entries
            .mapNotNull { (key, value) ->
                val slotId = parseSlotKey(key.name) ?: return@mapNotNull null
                val content = (value as? String)?.let { homeTileContentFromStorageString(it) } ?: return@mapNotNull null
                slotId to content
            }
            .sortedWith(compareBy({ it.first.pageIndex }, { it.first.slotIndex }))
            .map { it.second }

    val layoutFlow: Flow<HomeLayout> = context.coreDataStore.data.map { prefs ->
        val storedPageCount = prefs[pageCountKey]?.coerceIn(1, 9) ?: fallbackSpec.pageCount
        val storedColumns = prefs[gridColumnsKey] ?: fallbackSpec.columns
        val storedRows = prefs[gridRowsKey] ?: fallbackSpec.rows

        // Ensure grid size is valid (2x2, 3x3, 4x4)
        val validColumns = if (storedColumns in listOf(2, 3, 4)) storedColumns else 3
        val validRows = if (storedRows in listOf(2, 3, 4)) storedRows else 3

        val currentSpec = fallbackSpec.copy(
            pageCount = storedPageCount,
            columns = validColumns,
            rows = validRows
        )

        val pages = List(currentSpec.pageCount) { pageIndex ->
            List(currentSpec.slotsPerPage) { slotIndex ->
                val raw = prefs[keyFor(HomeSlotId(pageIndex, slotIndex))]
                homeTileContentFromStorageString(raw)
            }
        }
        HomeLayout(spec = currentSpec, pages = pages)
    }

    suspend fun setSlot(slot: HomeSlotId, content: HomeTileContent?) {
        context.coreDataStore.edit { prefs ->
            val key = keyFor(slot)
            if (content == null) prefs.remove(key) else prefs[key] = content.toStorageString()
        }
    }

    suspend fun setPageCount(pageCount: Int) {
        val clamped = pageCount.coerceIn(1, 9)
        context.coreDataStore.edit { prefs ->
            val oldPageCount = prefs[pageCountKey]?.coerceIn(1, 9) ?: fallbackSpec.pageCount
            if (oldPageCount == clamped) return@edit

            if (clamped < oldPageCount) {
                // Collect and re-assign apps (Option A)
                val columns = prefs[gridColumnsKey] ?: fallbackSpec.columns
                val rows = prefs[gridRowsKey] ?: fallbackSpec.rows
                val slotsPerPage = columns * rows

                val allItems = collectItemsInVisualOrder(prefs)
                val keysToRemove = prefs.asMap().keys.filter { it.name.startsWith("home_slot_") }
                keysToRemove.forEach { prefs.remove(it) }
                prefs[pageCountKey] = clamped

                allItems.take(slotsPerPage * clamped).forEachIndexed { index, content ->
                    val p = index / slotsPerPage
                    val s = index % slotsPerPage
                    val key = stringPreferencesKey("home_slot_${p}_${s}")
                    prefs[key] = content.toStorageString()
                }
            } else {
                prefs[pageCountKey] = clamped
            }
        }
    }

    suspend fun setGridSize(columns: Int, rows: Int) {
        val validColumns = if (columns in listOf(2, 3, 4)) columns else 3
        val validRows = if (rows in listOf(2, 3, 4)) rows else 3
        context.coreDataStore.edit { prefs ->
            val oldColumns = prefs[gridColumnsKey] ?: fallbackSpec.columns
            val oldRows = prefs[gridRowsKey] ?: fallbackSpec.rows

            if (oldColumns == validColumns && oldRows == validRows) return@edit

            // Collect all assigned items (Option A: Move to valid slots)
            val allItems = collectItemsInVisualOrder(prefs)

            // Remove all old slots
            val keysToRemove = prefs.asMap().keys.filter { it.name.startsWith("home_slot_") }
            keysToRemove.forEach { prefs.remove(it) }

            // Update grid size
            prefs[gridColumnsKey] = validColumns
            prefs[gridRowsKey] = validRows

            // Re-assign items to new slots
            val newSlotsPerPage = validColumns * validRows
            val pageCount = prefs[pageCountKey]?.coerceIn(1, 9) ?: fallbackSpec.pageCount

            allItems.take(newSlotsPerPage * pageCount).forEachIndexed { index, content ->
                val pageIndex = index / newSlotsPerPage
                val slotIndex = index % newSlotsPerPage
                val newKey = stringPreferencesKey("home_slot_${pageIndex}_${slotIndex}")
                prefs[newKey] = content.toStorageString()
            }
        }
    }

    suspend fun increasePageCount() {
        val current = currentPageCount()
        setPageCount(current + 1)
    }

    suspend fun decreasePageCount() {
        val current = currentPageCount()
        setPageCount(current - 1)
    }

    suspend fun resetHome() {
        context.coreDataStore.edit { prefs ->
            val keysToRemove = prefs.asMap().keys.filter { it.name.startsWith("home_slot_") }
            keysToRemove.forEach { prefs.remove(it) }
        }
    }

    private suspend fun currentPageCount(): Int {
        return context.coreDataStore.data.map { prefs ->
            prefs[pageCountKey]?.coerceIn(1, 9) ?: fallbackSpec.pageCount
        }.first()
    }
}
