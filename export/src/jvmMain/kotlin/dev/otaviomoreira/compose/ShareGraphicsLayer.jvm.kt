package dev.otaviomoreira.compose

import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.toAwtImage
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory
import java.awt.FileDialog
import java.awt.Frame
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@androidx.compose.runtime.Composable
actual fun rememberShareGraphicsLayer(): ShareGraphicsLayer {
    return object : ShareGraphicsLayer {
        override suspend fun share(graphicsLayer: GraphicsLayer) {
            val outputFile = chooseSaveFile(defaultName = "cmp.pdf") ?: throw RuntimeException("Can't open share file")

            imageToPdf(
                image = graphicsLayer.toImageBitmap().toAwtImage(),
                output = outputFile,
            )

            saveToDownloads(
                bytes = outputFile.readBytes(),
                fileName = "cmp.pdf",
            )
        }

    }
}

fun imageToPdf(
    image: BufferedImage,
    output: File
) {
    PDDocument().use { document ->
        val page = PDPage(
            PDRectangle(
                image.width.toFloat(),
                image.height.toFloat()
            )
        )

        document.addPage(page)

        val pdfImage = LosslessFactory.createFromImage(document, image)

        PDPageContentStream(document, page).use { content ->
            content.drawImage(
                pdfImage,
                0f,
                0f,
                image.width.toFloat(),
                image.height.toFloat()
            )
        }

        document.save(output)
    }
}

fun userDownloadsDir(): Path {
    val home = System.getProperty("user.home")
    return Paths.get(home, "Downloads")
}

fun saveToDownloads(
    bytes: ByteArray,
    fileName: String
) {
    val downloads = userDownloadsDir()
    Files.createDirectories(downloads)

    val target = downloads.resolve(fileName)
    Files.write(target, bytes)
}

fun chooseSaveFile(defaultName: String): File? {
    val dialog = FileDialog(null as Frame?, "Save file", FileDialog.SAVE)
    dialog.file = defaultName
    dialog.isVisible = true

    return if (dialog.file != null) {
        File(dialog.directory, dialog.file)
    } else {
        null
    }
}