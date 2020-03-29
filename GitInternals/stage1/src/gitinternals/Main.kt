package gitinternals

import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.util.*
import java.util.zip.InflaterInputStream


sealed class GitObject {
    class Reader(filepath: String) {
        private val fis = FileInputStream(filepath)
        private val iis = InflaterInputStream(fis)

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

            for (line in lines) {
                println(line)
            }
        }
    }
}

fun main() {
    //println("The current working directory is ${System.getProperty("user.dir")}")

    val scanner = Scanner(System.`in`)
    println("Enter git object location:")
    GitObject.parseFromFile(scanner.next())
}






