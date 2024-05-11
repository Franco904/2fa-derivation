package utils

import java.io.File

val resourcesFolder = File("src/main/resources")

fun File.createIfNotExists() {
    if (!exists()) createNewFile()
}

fun File.getLine(text: String) = bufferedReader().readLines().find { text in it }

fun File.getFirstLineOrNull() = bufferedReader().readLines().firstOrNull()

fun File.hasLine(text: String) = bufferedReader().readLines().any { text in it }

fun File.putLine(text: String) = appendText(if (getFirstLineOrNull() == null) text else "\n$text")
