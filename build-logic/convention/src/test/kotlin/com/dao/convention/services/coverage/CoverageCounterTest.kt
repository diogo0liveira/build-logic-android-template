package com.dao.convention.services.coverage

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CoverageCounterTest {
    @Test
    fun `should calculate total correctly`() {
        val counter = CoverageCounter(missed = 10L, covered = 20L)
        assertEquals(30L, counter.total)
    }

    @Test
    fun `should calculate percentage when total is greater than zero`() {
        val counter = CoverageCounter(missed = 25L, covered = 75L)
        assertEquals(75.0, counter.percentage)
    }

    @Test
    fun `should return null percentage when total is zero`() {
        val counter = CoverageCounter(missed = 0L, covered = 0L)
        assertNull(counter.percentage)
    }

    @Test
    fun `should return formatted percentage when total is greater than zero`() {
        val counter = CoverageCounter(missed = 1L, covered = 2L)
        assertEquals("66.67", counter.formattedPercentage)
    }

    @Test
    fun `should return na for formatted percentage when total is zero`() {
        val counter = CoverageCounter(missed = 0L, covered = 0L)
        assertEquals("n/a", counter.formattedPercentage)
    }

    @Test
    fun `should return formatted summary when total is greater than zero`() {
        val counter = CoverageCounter(missed = 1L, covered = 2L)
        assertEquals("66.67 (2/3)", counter.summary)
    }

    @Test
    fun `should return na for summary when total is zero`() {
        val counter = CoverageCounter(missed = 0L, covered = 0L)
        assertEquals("n/a", counter.summary)
    }

    @Test
    fun `should add missed and covered correctly on plus operator`() {
        val counter1 = CoverageCounter(missed = 10L, covered = 20L)
        val counter2 = CoverageCounter(missed = 5L, covered = 15L)
        val result = counter1 + counter2

        assertEquals(15L, result.missed)
        assertEquals(35L, result.covered)
    }

    @Test
    fun `should have zero missed and zero covered for zero constant`() {
        assertEquals(0L, CoverageCounter.ZERO.missed)
        assertEquals(0L, CoverageCounter.ZERO.covered)
        assertEquals(0L, CoverageCounter.ZERO.total)
    }
}
