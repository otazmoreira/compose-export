package dev.otaviomoreira.compose.export.printable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PrintableCard(
    scope: PrintableLayoutScope,
    quality: PdfQuality,
    modifier: Modifier = Modifier,
    paper: Paper = Paper.A4,
    content: @Composable PrintableLayoutScope.() -> Unit,
) {
    val graphicsLayer: GraphicsLayer = rememberGraphicsLayer()

    Content(
        graphicsLayer = graphicsLayer,
        quality = quality,
        modifier = modifier,
        scope = scope,
        content = content,
        paper = paper,
    )

    LaunchedEffect(key1 = quality) {
        scope.quality = quality
        scope.graphicsLayer = graphicsLayer
    }
}

@Composable
private fun Content(
    graphicsLayer: GraphicsLayer,
    quality: PdfQuality,
    modifier: Modifier = Modifier,
    paper: Paper = Paper.A4,
    scope: PrintableLayoutScope,
    content: @Composable PrintableLayoutScope.() -> Unit,
) {
    Surface {
        Box(
            modifier = modifier.drawLayoutInA4Paper(
                graphicsLayer = graphicsLayer,
                quality = quality,
                paper = paper,
            ),
        ) {
            with(receiver = scope) {
                Surface(
                    modifier = Modifier,
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                horizontal = 200.dp,
                                vertical = 50.dp,
                            ),
                        horizontalAlignment = Alignment.Start
                    ) {
                        content(scope)
                    }
                }

//                    Image(
//                        modifier = Modifier
//                            .align(Alignment.BottomStart)
//                            .width(width = 600.dp)
//                            .aspectRatio(ratio = 81 / 17.91f)
//                            .offset(x = 140.dp, y = (-80).dp),
//                        painter = R.drawable.ic_raptor_powered.getPainter(),
//                        contentDescription = null,
//                        contentScale = ContentScale.Fit
//                    )
            }
        }
    }
}

@Preview(device = PrintableDevices.PAPER_A4)
@Composable
private fun PrintableCardContentPreview() {
    Content(
        graphicsLayer = rememberGraphicsLayer(),
        quality = PdfQuality.SUPER,
        paper = Paper.A4,
        scope = rememberPrintableScope(),
        content = {
            Column {
                Text(text = "Testing", color = Color.Black, fontSize = 100.sp)
                Text(text = "Testing", color = Color.Black, fontSize = 100.sp)
//                Icon(
//                    modifier = Modifier.size(size = 100.dp),
//                    painter = painterResource(id = R.drawable.img_home),
//                    contentDescription = null,
//                    tint = Color.Red,
//                )
                Text(text = "Testing", color = Color.Black, fontSize = 100.sp)
                Text(text = "Testing", color = Color.Black, fontSize = 100.sp)
                Box(modifier = Modifier.size(size = 100.dp).background(color = Color.Green))
                Text(text = "Testing", color = Color.Black, fontSize = 100.sp)
            }
        }
    )
}

//const val A4_WIDTH = 2480f
//const val A4_HEIGHT = 3508f