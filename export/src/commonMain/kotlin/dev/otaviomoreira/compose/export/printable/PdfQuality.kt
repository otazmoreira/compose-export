package dev.otaviomoreira.compose.export.printable

enum class PdfQuality(val percent: Float) {
    LOW(.05f),
    MEDIUM(.1f),
    NORMAL(.3f),
    HIGH(.5f),
    SUPER(1f);

    val width = A4_WIDTH * percent
    val height = A4_HEIGHT * percent
}