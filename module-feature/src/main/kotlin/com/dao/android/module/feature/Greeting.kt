package com.dao.android.module.feature

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.dao.android.module.core.annotations.IgnoreGeneratedPreview
import com.dao.android.module.feature.theme.Purple40
import com.dao.android.module.feature.theme.TemplateTheme
import com.dao.android.module.feature.theme.Typography

@Composable
public fun Greeting(
    name: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "Hello $name!",
        fontFamily = Typography.headlineLarge.fontFamily,
        fontStyle = Typography.headlineLarge.fontStyle,
        fontWeight = FontWeight.Bold,
        color = Purple40,
        modifier = modifier,
    )
}

@Composable
@IgnoreGeneratedPreview
@Preview(showBackground = true)
private fun GreetingPreview() {
    TemplateTheme {
        Greeting("Android")
    }
}
