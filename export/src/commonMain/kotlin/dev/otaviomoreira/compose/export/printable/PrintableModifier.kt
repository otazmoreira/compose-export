package dev.otaviomoreira.compose.export.printable

import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private fun Modifier.unboundedSize(): Modifier = this.then(
    other = layout { measurable, _ ->
        val placeable = measurable.measure(
            constraints = Constraints(
                maxWidth = Int.MAX_VALUE,
                maxHeight = Int.MAX_VALUE,
            )
        )

        layout(placeable.width, placeable.height) {
            placeable.placeRelative(x = 0, y = 0)
        }
    }
)

@Composable
private fun Modifier.drawPrintablePDF(
    graphicsLayer: GraphicsLayer,
    quality: PdfQuality
): Modifier {
    val windowInfo = LocalWindowInfo.current
    val inspectionMode = LocalInspectionMode.current

    return this.then(
        other = Modifier
            .run {
                if (inspectionMode) {
                    this
                } else {
                    unboundedSize()
                }
            }
            .size(
                width = Dp(quality.width),
                height = Dp(quality.height)
            )
            .run {
                if (inspectionMode) {
                    this
                } else {
                    absoluteOffset(
                        y = (Dp(value = quality.height) + windowInfo.containerSize.height.dp) * -1,
                        x = (Dp(value = quality.width) + windowInfo.containerSize.width.dp) * -1
                    )
                }
            }
            .drawWithContent {
                // call record to capture the content in the graphics layer
                graphicsLayer.record {
                    // draw the contents of the composable into the graphics layer
                    this@drawWithContent.drawContent()
                }
                // draw the graphics layer on the visible canvas
                drawLayer(graphicsLayer)
            }
    )
}

@Composable
internal fun Modifier.drawLayoutInA4Paper(
    graphicsLayer: GraphicsLayer,
    quality: PdfQuality,
    paper: Paper,
): Modifier {
    val inspectionMode = LocalInspectionMode.current

    return this
        .width(width = Dp(value = paper.width * quality.percent))
        .height(height = Dp(value = paper.height * quality.percent))
        .aspectRatio(
            ratio = paper.width / paper.height,
            matchHeightConstraintsFirst = true,
        )
        .run {
            if (inspectionMode) {
                this
            } else {
                absoluteOffset(
                    x = Dp(value = -(paper.width * quality.percent)),
                    y = Dp(value = -(paper.height * quality.percent))
                )
            }
        }
        .drawPrintablePDF(
            graphicsLayer = graphicsLayer,
            quality = quality,
        )
}