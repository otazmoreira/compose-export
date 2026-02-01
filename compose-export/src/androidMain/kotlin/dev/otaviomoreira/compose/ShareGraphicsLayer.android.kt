package dev.otaviomoreira.compose

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.platform.LocalContext
import dev.otaviomoreira.compose.export.printable.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.scale

internal class ShareGraphicsLayerImpl(
    private val context: Context,
) : ShareGraphicsLayer {
    override suspend fun share(graphicsLayer: GraphicsLayer) {
        saveAndSharePictureAsPdf(
            bitmap = graphicsLayer.toImageBitmap(),
            context = context,
        )
    }
}

@Composable
actual fun rememberShareGraphicsLayer(): ShareGraphicsLayer {
    val context = LocalContext.current

    return remember(key1 = context) { ShareGraphicsLayerImpl(context) }
}

suspend fun saveAndSharePictureAsPdf(
    bitmap: ImageBitmap,
    context: Context,
) {
    withContext(context = Dispatchers.Default) {
        val pdfFile = createPdfFromBitmap(context, bitmap)
        shareFile(context, pdfFile, type = "application/pdf", title = "Share PDF")
    }
}

private fun createPdfFromBitmap(
    context: Context,
    bitmap: ImageBitmap
): File {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(
        Paper.A4.width.toInt(),
        Paper.A4.height.toInt(),
        1
    ).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    // Redimensiona o bitmap para uma densidade de pixels maior
    val scaledBitmap = bitmap
        .asAndroidBitmap()
        .copy(Bitmap.Config.ARGB_8888, false)
        .scale(Paper.A4.width.toInt(), Paper.A4.height.toInt())

    canvas.drawBitmap(scaledBitmap, 0f, 0f, null)
    pdfDocument.finishPage(page)

    val newPDFName = "lineup.pdf"
    val dir = getLineupDir(context)
    val file = File(dir, newPDFName)
    if (file.exists().not()) {
        file.createNewFile()
    } else {
        file.delete()
        file.createNewFile()
    }
    FileOutputStream(file).use { pdfDocument.writeTo(it) }
    pdfDocument.close()

    return file
}

private fun getLineupDir(context: Context): File {
    val dir = File(context.cacheDir, "lineup")
    dir.deleteRecursively()
    dir.mkdirs()
    return dir
}

private fun shareFile(
    context: Context,
    file: File,
    type: String,
    title: String
) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")

            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            this.type = type
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION +
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            )
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(Intent.createChooser(intent, title))
    }
}