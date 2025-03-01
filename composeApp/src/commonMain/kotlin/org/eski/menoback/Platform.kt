package org.eski.menoback

interface Platform {
  val name: String
}

expect fun getPlatform(): Platform