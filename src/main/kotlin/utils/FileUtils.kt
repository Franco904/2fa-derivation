package utils

import java.io.File

fun File.getLine(text: String) = bufferedReader().readLines().find { text in it }

fun File.getFirstLine() = bufferedReader().readLines().first()

fun File.hasLine(text: String) = bufferedReader().readLines().any { text in it }

fun File.putLine(text: String) = appendText(text)

fun File.removeLine(text: String) {
    val linesToKeep = bufferedReader().readLines().filterNot { text in it }
    writeText(linesToKeep.joinToString("\n"))
}

fun File.removeAllLines() = writeText("")
