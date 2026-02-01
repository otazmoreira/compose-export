package dev.otaviomoreira.compose

import androidx.compose.runtime.Composable
//import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
//import kotlin.js.ExperimentalWasmJsInterop

@Composable
actual fun rememberShareGraphicsLayer(): ShareGraphicsLayer {
    return object : ShareGraphicsLayer {
        override suspend fun share(graphicsLayer: GraphicsLayer) {

//            val imageBitmap = graphicsLayer.toImageBitmap()
//
//            val pixels = imageBitmap.toPixelArray()
//
//            generatePdf(
//                width = imageBitmap.width,
//                height = imageBitmap.height,
//                pixels = pixels
//            )
        }
    }
}
//
//@OptIn(ExperimentalWasmJsInterop::class)
//@JsFun(
//    """
//    (width, height, pixels) => {
//        const canvas = document.createElement("canvas");
//        canvas.width = width;
//        canvas.height = height;
//
//        const ctx = canvas.getContext("2d");
//
//        const imageData = new ImageData(
//            new Uint8ClampedArray(pixels),
//            width,
//            height
//        );
//
//        ctx.putImageData(imageData, 0, 0);
//
//        const { jsPDF } = window.jspdf;
//        const pdf = new jsPDF({
//            orientation: "portrait",
//            unit: "px",
//            format: [width, height]
//        });
//
//        const imgData = canvas.toDataURL("image/png");
//        pdf.addImage(imgData, "PNG", 0, 0, width, height);
//        pdf.save("export.pdf");
//    }
//    """
//)
//external fun generatePdf(
//    width: Int,
//    height: Int,
//    pixels: IntArray
//)
//
//fun ImageBitmap.toPixelArray(): IntArray {
//    val buffer = IntArray(width * height * 4)
//    readPixels(buffer)
//    return buffer
//}