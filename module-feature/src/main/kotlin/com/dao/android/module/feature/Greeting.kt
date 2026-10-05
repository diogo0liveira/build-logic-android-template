package com.dao.android.module.feature

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dao.android.module.core.annotations.IgnoreGeneratedPreview

@Composable
public fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier,
    )
}

@Composable
@IgnoreGeneratedPreview
@Preview(showBackground = true)
private fun GreetingPreview() {
    MaterialTheme {
        Greeting("Android")
    }
}
