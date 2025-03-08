package org.eski.menoback

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val type: PlatformType = PlatformType.desktop
}

actual fun getPlatform(): Platform = JVMPlatform()