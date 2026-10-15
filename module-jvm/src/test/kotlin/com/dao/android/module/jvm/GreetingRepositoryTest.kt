package com.dao.android.module.jvm

import kotlin.test.Test
import kotlin.test.assertEquals

internal class GreetingRepositoryTest {
    private val repository: GreetingRepository = DefaultGreetingRepository()

    @Test
    fun `should return greeting`() {
        assertEquals("Android", repository.getGreeting())
    }
}
