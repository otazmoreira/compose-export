package dev.otaviomoreira.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.layer.GraphicsLayer

interface ShareGraphicsLayer {
    suspend fun share(graphicsLayer: GraphicsLayer)
}

@Composable
expect fun rememberShareGraphicsLayer(): ShareGraphicsLayer