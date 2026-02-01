package dev.otaviomoreira.compose.export

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform