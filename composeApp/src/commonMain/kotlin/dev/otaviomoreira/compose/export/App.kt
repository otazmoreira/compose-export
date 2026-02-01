package dev.otaviomoreira.compose.export

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.otaviomoreira.compose.export.printable.PdfQuality
import dev.otaviomoreira.compose.export.printable.PrintableCard
import dev.otaviomoreira.compose.export.printable.rememberPrintableScope
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    val printableScope = rememberPrintableScope()
    val coroutineScope = rememberCoroutineScope()

    MaterialTheme {
        PrintableCard(
            scope = printableScope,
            quality = PdfQuality.SUPER,
        ) {
            Text(
                text = "Testing PrintableCard in CMP",
                fontSize = 100.sp,
            )
        }

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Export Composable"
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        printableScope.sharePdf()
                    }
                }
            ) {
                Text(
                    text = "Share"
                )
            }
        }
    }
}