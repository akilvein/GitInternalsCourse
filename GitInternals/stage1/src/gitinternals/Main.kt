package gitinternals

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import java.util.*
import java.util.zip.InflaterInputStream


sealed class GitObject {
    class Reader(filepath: String) {
        val fis = FileInputStream(filepath)
        val iis = InflaterInputStream(fis)

        fun readLines(): List<String> {
            val result = mutableListOf<String>()
            val bos = ByteArrayOutputStream()
            readLoop@ while (true) {
                val data = iis.read()
                when {
                    data < 0 -> {
                        result.add(bos.toString(Charsets.UTF_8))
                        break@readLoop
                    }
                    data == 0 || data == '\n'.toInt() -> {
                        result.add(bos.toString(Charsets.UTF_8))
                        bos.reset()
                    }
                    else -> bos.write(data)
                }
            }
            return result.filter { it.isNotEmpty() }
        }
    }

    companion object {
        fun parseFromFile(filepath: String) {
            val reader = Reader(filepath)
            val lines = reader.readLines()

            val (type, declaredLength) = lines[0].split(" ")

            for (line in lines) {
                println(line)
            }
        }
    }
}

fun printObject(fullFilePath: String) {
    println(fullFilePath)
    println("**************************************************************")

    try {
        println(GitObject.parseFromFile(fullFilePath).toString())
    }
    catch (e: Exception) {
        println(e.message)
    }
}

fun main(args: Array<String>) {
    //println("The current working directory is ${System.getProperty("user.dir")}")

    val scanner = Scanner(System.`in`)
    println("Enter git object location:")
    GitObject.parseFromFile(scanner.next())
}






