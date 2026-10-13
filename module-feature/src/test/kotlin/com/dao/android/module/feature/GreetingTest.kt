package com.dao.android.module.feature

import android.os.Build
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
@LooperMode(LooperMode.Mode.PAUSED)
class GreetingTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when greeting is rendered then the correct text is displayed`() {
        composeTestRule.setContent {
            Greeting(name = "Android")
        }

        composeTestRule.onNodeWithText("Hello Android!").assertIsDisplayed()
    }
}
