package org.eski.menoback

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val type: PlatformType = PlatformType.android
}

actual fun getPlatform(): Platform = AndroidPlatform()