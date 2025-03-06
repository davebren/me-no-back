package org.eski.util

fun <T> Set<T>.containsOnly(item: T) = size == 1 && first() == item

fun <T> Set<T>.matches(other: Set<T>) = size == other.size && containsAll(other)

fun <T> List<T>.equalsIgnoreOrder(other: List<T>) = this.toSet().matches(other.toSet())
