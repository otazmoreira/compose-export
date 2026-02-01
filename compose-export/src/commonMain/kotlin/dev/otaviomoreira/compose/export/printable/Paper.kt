package dev.otaviomoreira.compose.export.printable

const val A4_WIDTH = 2480f
const val A4_HEIGHT = 3508f

/**
 * @param name of the paper
 * @param width in pixel
 * @param height in pixel
 * */
sealed class Paper(
    open val name: String,
    open val width: Float,
    open val height: Float,
) {
    data object A4 : Paper(name = "A4", width = A4_WIDTH, height = A4_HEIGHT)

    data class Custom(
        val customName: String,
        val customWidth: Float,
        val customHeight: Float
    ) : Paper(
        name = customName,
        width = customWidth,
        height = customHeight,
    )
}