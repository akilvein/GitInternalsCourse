package gitinternals

import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.util.*
import java.util.zip.InflaterInputStream


sealed class GitObject {
    class Reader(filepath: String) {
        private val fis = FileInputStream(filepath)
        private val iis = InflaterInputStream(fis)

        fun readFirstLine(): String {
            val bos = ByteArrayOutputStream()
            readLoop@while (true) {
                val data = iis.read()
                when {
                    data <= 0 -> break@readLoop
                    else -> bos.write(data)
                }
            }
            return bos.toString(Charsets.UTF_8)
        }
    }

    companion object {
        fun parseFromFile(filepath: String) {
            val reader = Reader(filepath)
            val header = reader.readFirstLine()

            val (type, declaredLength) = header.split(" ")
            println("type:$type length:$declaredLength")
        }
    }
}

fun main(args: Array<String>) {
    //println("The current working directory is ${System.getProperty("user.dir")}")

    val scanner = Scanner(System.`in`)
    println("Enter .git directory location:")
    val gitDir = scanner.next()
    println("Enter git object hash:")
    val hash = scanner.next()

    val objectPath = "$gitDir/objects/${hash.substring(0, 2)}/${hash.substring(2, 40)}"
    GitObject.parseFromFile(objectPath)
}





