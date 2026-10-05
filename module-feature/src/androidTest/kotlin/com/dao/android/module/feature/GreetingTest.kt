package com.dao.android.module.feature

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class GreetingTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun greeting_shouldDisplayHelloName_withDefaultModifier() {
        composeTestRule.setContent {
            Greeting(name = "Android")
        }
        composeTestRule.onNodeWithText("Hello Android!").assertIsDisplayed()
    }

    @Test
    fun greeting_shouldDisplayHelloName_withCustomModifier() {
        composeTestRule.setContent {
            Greeting(name = "Android", modifier = Modifier.Companion)
        }
        composeTestRule.onNodeWithText("Hello Android!").assertIsDisplayed()
    }
}
