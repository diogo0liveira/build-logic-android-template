package com.dao.android.module.jvm

import kotlin.test.assertEquals
import org.junit.Test

internal class GreetingRepositoryTest {
    private val repository: GreetingRepository = DefaultGreetingRepository()

    @Test
    fun `should return greeting`() {
        assertEquals("Android", repository.getGreeting())
    }
}
