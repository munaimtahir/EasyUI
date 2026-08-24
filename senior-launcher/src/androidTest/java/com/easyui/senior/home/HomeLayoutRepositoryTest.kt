package com.easyui.senior.home

import androidx.datastore.preferences.core.edit
import androidx.test.platform.app.InstrumentationRegistry
import com.easyui.senior.storage.coreDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Regression coverage for setPageCount()/setGridSize(): they used to collect existing
 * items via DataStore's raw map iteration order rather than visual (pageIndex, slotIndex)
 * order, so a page-count or grid-size change could silently scramble icon positions.
 */
class HomeLayoutRepositoryTest {

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var repo: HomeLayoutRepository

    @Before
    fun setUp() = runBlocking {
        context.coreDataStore.edit { prefs ->
            prefs.asMap().keys
                .filter {
                    it.name.startsWith("home_slot_") ||
                        it.name in setOf("home_page_count", "home_grid_columns", "home_grid_rows")
                }
                .forEach { prefs.remove(it) }
        }
    }

    private fun app(id: String) = HomeTileContent.App(AppComponentRef(id, "MainActivity"))

    @Test
    fun setGridSize_preservesVisualOrder_regardlessOfInsertionOrder() = runBlocking {
        // 12 slots/page, so slot index 10 exists alongside slot index 2 — a naive
        // string-sorted fix would place "home_slot_0_10" before "home_slot_0_2".
        repo = HomeLayoutRepository(context, HomeGridSpec(pageCount = 1, columns = 4, rows = 3))

        val early = app("early") // occupies the earlier visual slot
        val late = app("late")   // occupies the later visual slot, but is written FIRST

        repo.setSlot(HomeSlotId(pageIndex = 0, slotIndex = 10), late)
        repo.setSlot(HomeSlotId(pageIndex = 0, slotIndex = 2), early)

        repo.setGridSize(columns = 2, rows = 2) // shrinks to 4 slots/page, forces reassignment

        val layout = repo.layoutFlow.first()
        assertEquals(early, layout.get(HomeSlotId(0, 0)))
        assertEquals(late, layout.get(HomeSlotId(0, 1)))
    }

    @Test
    fun setPageCount_preservesVisualOrder_whenShrinkingPages() = runBlocking {
        repo = HomeLayoutRepository(context, HomeGridSpec(pageCount = 2, columns = 2, rows = 2))

        val page0Item = app("page0")
        val page1Item = app("page1")

        // Insert the later page's item first.
        repo.setSlot(HomeSlotId(pageIndex = 1, slotIndex = 0), page1Item)
        repo.setSlot(HomeSlotId(pageIndex = 0, slotIndex = 0), page0Item)

        repo.setPageCount(1) // shrinks pages, forces reassignment onto page 0's 4 slots

        val layout = repo.layoutFlow.first()
        assertEquals(page0Item, layout.get(HomeSlotId(0, 0)))
        assertEquals(page1Item, layout.get(HomeSlotId(0, 1)))
    }
}
