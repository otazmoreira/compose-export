package dev.otaviomoreira.compose.export.printable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp as dpCompose
import androidx.compose.ui.unit.TextUnit
import dev.otaviomoreira.compose.ShareGraphicsLayer
import dev.otaviomoreira.compose.rememberShareGraphicsLayer

@Composable
fun rememberPrintableScope(): PrintableLayoutScope {
    val shareGraphicsLayer = rememberShareGraphicsLayer()
    return remember { PrintableLayoutScope(shareGraphicsLayer) }
}

class PrintableLayoutScope(
    private val shareGraphicsLayer: ShareGraphicsLayer,
) {
    internal var quality: PdfQuality by mutableStateOf(value = PdfQuality.SUPER)
    internal var graphicsLayer: GraphicsLayer? = null

    @Composable
    fun Float.textPixelSize(): TextUnit {
        val density = LocalDensity.current
        return with(density) { Dp(this@textPixelSize).toSp() }
    }

    @Stable
    inline val Int.dp: Dp
        @Composable
        get() = this.printablePixelAsDp()

    @Stable
    inline val Int.sp: TextUnit
        @Composable
        get() = this.printableTextPixelSize()

    @Composable
    fun Int.printablePixelAsDp(): Dp {
        val inspectionMode = LocalInspectionMode.current
        val density = LocalDensity.current

        return if (inspectionMode) {
            with(receiver = density) {
                this@printablePixelAsDp.toFloat().toDp()
            }
        } else {
            this@printablePixelAsDp.toFloat().printablePixelAsDp()
        }
    }

    @Composable
    fun Float.printablePixelAsDp(): Dp {
        val inspectionMode = LocalInspectionMode.current

        return if (inspectionMode) {
            this.dpCompose
        } else {
            Dp(value = this) * quality.percent
        }
    }

    @Composable
    fun Int.printableTextPixelSize(): TextUnit {
        val inspectionMode = LocalInspectionMode.current
        val density = LocalDensity.current

        return if (inspectionMode) {
            with(receiver = density) {
                this@printableTextPixelSize.toFloat().toSp()
            }
        } else {
            this.toFloat().printableTextPixelSize()
        }
    }

    @Composable
    fun Float.printableTextPixelSize(): TextUnit {
        return this.textPixelSize() * quality.percent
    }

    suspend fun sharePdf() {
        graphicsLayer?.let {
            shareGraphicsLayer.share(graphicsLayer = it)
        }
    }
}

