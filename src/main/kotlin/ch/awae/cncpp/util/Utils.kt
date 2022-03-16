package ch.awae.cncpp.util

fun <T> java.util.Optional<T>.unwrap(): T? = orElse(null)

fun String.splitLines() : List<String> = this.split("\n")
