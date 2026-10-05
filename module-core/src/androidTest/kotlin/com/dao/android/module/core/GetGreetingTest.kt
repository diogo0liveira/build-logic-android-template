package com.dao.android.module.core

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GetGreetingTest {
    @Test
    fun shouldUseAppContext() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.dao.android.module.core.test", context.packageName)
    }
}
