package com.dao.convention.services.coverage

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CoverageCounterTest {
    @Test
    fun `total calculates correctly`() {
        val counter = CoverageCounter(missed = 10L, covered = 20L)
        assertEquals(30L, counter.total)
    }

    @Test
    fun `percentage calculates correctly when total is greater than zero`() {
        val counter = CoverageCounter(missed = 25L, covered = 75L)
        assertEquals(75.0, counter.percentage)
    }

    @Test
    fun `percentage is null when total is zero`() {
        val counter = CoverageCounter(missed = 0L, covered = 0L)
        assertNull(counter.percentage)
    }

    @Test
    fun `formattedPercentage returns formatted string when total is greater than zero`() {
        val counter = CoverageCounter(missed = 1L, covered = 2L)
        // 2 / 3 = 66.666...
        assertEquals("66.67", counter.formattedPercentage)
    }

    @Test
    fun `formattedPercentage returns na when total is zero`() {
        val counter = CoverageCounter(missed = 0L, covered = 0L)
        assertEquals("n/a", counter.formattedPercentage)
    }

    @Test
    fun `summary returns formatted string with absolute count when total is greater than zero`() {
        val counter = CoverageCounter(missed = 1L, covered = 2L)
        assertEquals("66.67 (2/3)", counter.summary)
    }

    @Test
    fun `summary returns na when total is zero`() {
        val counter = CoverageCounter(missed = 0L, covered = 0L)
        assertEquals("n/a", counter.summary)
    }

    @Test
    fun `plus operator adds missed and covered correctly`() {
        val counter1 = CoverageCounter(missed = 10L, covered = 20L)
        val counter2 = CoverageCounter(missed = 5L, covered = 15L)
        val result = counter1 + counter2

        assertEquals(15L, result.missed)
        assertEquals(35L, result.covered)
    }

    @Test
    fun `ZERO constant has zero missed and zero covered`() {
        assertEquals(0L, CoverageCounter.ZERO.missed)
        assertEquals(0L, CoverageCounter.ZERO.covered)
        assertEquals(0L, CoverageCounter.ZERO.total)
    }
}
