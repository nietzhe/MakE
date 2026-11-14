package com.enigma.kitchenorders.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import com.enigma.kitchenorders.ui.components.HeroCollapseState
import org.junit.Assert.assertEquals
import org.junit.Test

class HeroCollapseStateTest {

    private val state = HeroCollapseState(expandedPx = 190f, collapsedPx = 64f)
    private val connection = state.nestedScrollConnection

    @Test
    fun scrolling_content_up_collapses_the_hero_before_the_list_scrolls() {
        val consumed = connection.onPreScroll(Offset(0f, -50f), NestedScrollSource.Drag)

        assertEquals(-50f, consumed.y, 0.001f)
        assertEquals(140f, state.heightPx, 0.001f)
    }

    @Test
    fun hero_never_shrinks_below_the_compact_bar_and_hands_the_rest_to_the_list() {
        val consumed = connection.onPreScroll(Offset(0f, -500f), NestedScrollSource.Drag)

        assertEquals(-126f, consumed.y, 0.001f)
        assertEquals(64f, state.heightPx, 0.001f)
        assertEquals(1f, state.fraction, 0.001f)
    }

    @Test
    fun pulling_down_at_the_top_of_the_list_expands_the_hero_again() {
        connection.onPreScroll(Offset(0f, -500f), NestedScrollSource.Drag)

        val consumed = connection.onPostScroll(Offset.Zero, Offset(0f, 40f), NestedScrollSource.Drag)

        assertEquals(40f, consumed.y, 0.001f)
        assertEquals(104f, state.heightPx, 0.001f)
    }

    @Test
    fun pulling_down_does_not_expand_the_hero_before_the_list_is_back_at_its_top() {
        connection.onPreScroll(Offset(0f, -500f), NestedScrollSource.Drag)

        val consumed = connection.onPreScroll(Offset(0f, 40f), NestedScrollSource.Drag)

        assertEquals(0f, consumed.y, 0.001f)
        assertEquals(64f, state.heightPx, 0.001f)
    }

    @Test
    fun expand_restores_the_full_height() {
        connection.onPreScroll(Offset(0f, -500f), NestedScrollSource.Drag)

        state.expand()

        assertEquals(190f, state.heightPx, 0.001f)
        assertEquals(0f, state.fraction, 0.001f)
    }
}
