package com.smartsite.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.smartsite.app.ui.theme.Orange
import com.smartsite.app.ui.theme.OrangeTint
import com.smartsite.app.ui.theme.SmartSiteTheme

/**
 * SmartSite branding: orange rounded square with white "SS" + "SmartSite" wordmark
 * ("Site" tinted #FFD4AB).
 */
@Composable
fun BrandLogo(
    modifier: Modifier = Modifier,
    tileSize: Dp = 36.dp,
    showWordmark: Boolean = true,
    wordmarkColor: Color = Color.White
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(tileSize)
                .clip(RoundedCornerShape(8.dp))
                .background(Orange),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "SS",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        if (showWordmark) {
            Spacer(Modifier.width(8.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = wordmarkColor, fontWeight = FontWeight.Bold)) {
                        append("Smart")
                    }
                    withStyle(SpanStyle(color = OrangeTint, fontWeight = FontWeight.Bold)) {
                        append("Site")
                    }
                },
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF6B8F7E)
@Composable
private fun BrandLogoPreview() {
    SmartSiteTheme { BrandLogo() }
}
