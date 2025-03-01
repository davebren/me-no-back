package org.eski.util

fun Array<IntArray>.deepCopy() = Array(size) { get(it).copyOf() }
