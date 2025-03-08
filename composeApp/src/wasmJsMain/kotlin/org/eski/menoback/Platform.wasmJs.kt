package org.eski.menoback

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
    override val type: PlatformType = PlatformType.wasm
}

actual fun getPlatform(): Platform = WasmPlatform()