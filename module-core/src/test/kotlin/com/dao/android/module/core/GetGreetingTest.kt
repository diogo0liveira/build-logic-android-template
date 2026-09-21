package com.dao.android.module.core

import com.dao.android.module.jvm.GreetingRepository
import com.dao.android.module.jvm.TestGreetingRepository
import kotlin.test.assertEquals
import org.junit.Test

class GetGreetingTest {
    private val repository: GreetingRepository = TestGreetingRepository()
    private val getGreeting = GetGreeting(repository)

    @Test
    fun `should return greeting`() {
        assertEquals("Android", getGreeting())
    }
}
