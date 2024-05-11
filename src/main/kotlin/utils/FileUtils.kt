package utils

import java.io.File

val resourcesFolder = File("src/main/resources")

fun File.createIfNotExists() {
    if (!exists()) createNewFile()
}

fun File.getLine(text: String) = bufferedReader().readLines().find { text in it }

fun File.getFirstLine() = bufferedReader().readLines().first()

fun File.getFirstLineOrNull() = bufferedReader().readLines().firstOrNull()

fun File.hasLine(text: String) = bufferedReader().readLines().any { text in it }

fun File.putLine(text: String) = appendText(if (getFirstLineOrNull() == null) text else "\n$text")

fun File.removeLine(text: String) {
    val linesToKeep = bufferedReader().readLines().filterNot { text in it }
    writeText(linesToKeep.joinToString("\n"))
}

fun File.removeAllLines() = writeText("")
