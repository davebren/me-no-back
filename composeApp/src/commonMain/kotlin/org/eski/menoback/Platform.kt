package org.eski.menoback

interface Platform {
  val name: String
  val type: PlatformType
}
enum class PlatformType {
  android, desktop, ios, wasm;
}

expect fun getPlatform(): Platform