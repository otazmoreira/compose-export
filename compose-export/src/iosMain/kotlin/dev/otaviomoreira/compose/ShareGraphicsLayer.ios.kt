package dev.otaviomoreira.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGImageRef
import platform.CoreGraphics.CGDataProviderCreateWithData
import platform.CoreGraphics.CGImageCreate
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGColorRenderingIntent
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.writeToFile
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIGraphicsPDFRenderer
import platform.UIKit.UIImage

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberShareGraphicsLayer(): ShareGraphicsLayer {
    return object : ShareGraphicsLayer {
        override suspend fun share(graphicsLayer: GraphicsLayer) {
            // 1️⃣ Render GraphicsLayer -> UIImage
            val image = graphicsLayer.toUIImage()

            // 2️⃣ Create PDF
            val pdfData = createPdf(image)

            // 3️⃣ Write temp file
            val fileUrl = writeTempPdf(pdfData)

            // 4️⃣ Share
            shareFile(fileUrl)
        }

    }
}

@OptIn(ExperimentalForeignApi::class)
private suspend fun GraphicsLayer.toUIImage(): UIImage {
    val skiaBitmap = this.toImageBitmap().asSkiaBitmap()
    val cgImage = skiaBitmap.toCGImage()

    return UIImage.imageWithCGImage(cgImage)
}

@OptIn(ExperimentalForeignApi::class)
private fun org.jetbrains.skia.Bitmap.toCGImage(): CGImageRef {

    val info = imageInfo

    val pixels = readPixels(
        dstInfo = info,
        dstRowBytes = rowBytes,
        srcX = 0,
        srcY = 0
    ) ?: error("Failed to read pixels from Skia bitmap")

    return pixels.usePinned { pinned ->

        val provider = CGDataProviderCreateWithData(
            info = null,
            data = pinned.addressOf(0),
            size = pixels.size.toULong(),
            releaseData = null
        )

        CGImageCreate(
            width = info.width.toULong(),
            height = info.height.toULong(),
            bitsPerComponent = 8.toULong(),
            bitsPerPixel = 32.toULong(),
            bytesPerRow = rowBytes.toULong(),
            space = CGColorSpaceCreateDeviceRGB(),
            bitmapInfo = CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value,
            provider = provider,
            decode = null,
            shouldInterpolate = false,
            intent = CGColorRenderingIntent.kCGRenderingIntentDefault
        )!!
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun createPdf(image: UIImage): NSData {

    val bounds = image.size.useContents {
        CGRectMake(0.0, 0.0, width, height)
    }

    val renderer = UIGraphicsPDFRenderer(bounds)

    return renderer.PDFDataWithActions { context ->
        context?.beginPage()
        image.drawInRect(bounds)
    }
}

private fun writeTempPdf(data: NSData): NSURL {
    val path = NSTemporaryDirectory() + "/export.pdf"
    data.writeToFile(path, true)
    return NSURL.fileURLWithPath(path)
}

private fun shareFile(url: NSURL) {
    val controller = UIActivityViewController(
        activityItems = listOf(url),
        applicationActivities = null
    )

    UIApplication.sharedApplication
        .keyWindow
        ?.rootViewController
        ?.presentViewController(controller, true, null)
}