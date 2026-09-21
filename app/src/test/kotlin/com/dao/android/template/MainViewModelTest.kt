package com.dao.android.template

import com.dao.android.module.core.GetGreeting
import com.dao.android.module.jvm.TestGreetingRepository
import kotlin.test.assertEquals
import org.junit.Test

class MainViewModelTest {
    private val repository = TestGreetingRepository()
    private val getGreeting = GetGreeting(repository)

    private val viewModel = MainViewModel(getGreeting)

    @Test
    fun `should return greeting`() {
        assertEquals("Android", viewModel.greeting.value)
    }
}
