package org.eski.menoback

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val type: PlatformType = PlatformType.ios
}

actual fun getPlatform(): Platform = IOSPlatform()