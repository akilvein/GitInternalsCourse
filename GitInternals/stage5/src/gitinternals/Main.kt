package gitinternals

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.zip.InflaterInputStream


sealed class GitObject {
    class Reader(filepath: String) {
        private val fis = FileInputStream(filepath)
        private val iis = InflaterInputStream(fis)

        private val header = readFirstLine().split(" ")
        private val length = header[1].toInt()
        val type = header[0]

        var actualLength = 0
        var eofReached = false

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

        fun readRemainingLines(): String {
            val bos = ByteArrayOutputStream()
            readLoop@while (true) {
                val data = iis.read()
                if (data <= 0) {
                    eofReached = true
                    break@readLoop
                }
                else {
                    bos.write(data)
                    actualLength++
                }
            }
            return bos.toString(Charsets.UTF_8)
        }

        fun readOneLine(): String {
            val bos = ByteArrayOutputStream()
            readLoop@while (true) {
                val data = iis.read()
                when {
                    data < 0 -> {
                        eofReached = true
                        break@readLoop
                    }
                    data == 0 || data == '\n'.toInt() -> {
                        actualLength++
                        break@readLoop
                    }
                    else -> {
                        bos.write(data)
                        actualLength++
                    }
                }
            }
            return bos.toString(Charsets.UTF_8)
        }

        fun readBinaryHash(): String {
            var result = ""
            repeat(20) {
                val data = iis.read()
                require(data >= 0) { "EOF while reading hash" }
                result += Integer.toHexString(data);
            }
            actualLength += 20
            return result
        }

        fun parseBlobBody() {
            val text = readRemainingLines()

            require(length == text.length) {
                "Declared length ($length) does not match actual text length(${text.length})"
            }

            println("*BLOB*")
            println(text)
        }

        private fun parseDateTime(sinceEpoch: String): String {
            val (millis, zone) = sinceEpoch.split(" ")
            val dateTime = Instant.ofEpochSecond(millis.toLong()).atZone(ZoneId.of(zone))
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").format(dateTime)
        }

        fun parseCommitBody() {
            var line = readOneLine().trim()
            val (tree, treeHash) = line.split(" ")
            require(tree == "tree")
            require(treeHash.length == 40) { "hash size is ${treeHash.length}. (${treeHash})" }

            val parentHashes = mutableListOf<String>()
            while (true) {
                line = readOneLine()
                val (parent, parentHash) = line.split(" ")
                if (parent == "parent") {
                    require(parentHash.length == 40)
                    parentHashes.add(parentHash)
                }
                else {
                    break
                }
            }

            val (author, authorLine) = line.split(delimiters = *arrayOf(" "), limit = 2)
            require(author == "author")
            val (authorName, authorEmail, originalDate) = authorLine.split(" <", "> ")

            val (committer, committerLine) = readOneLine().split(delimiters = *arrayOf(" "), limit = 2)
            require(committer == "committer")
            val (committerName, committerEmail, commitDate) = committerLine.split(" <", "> ")

            val message = readRemainingLines().replaceFirst("\n", "")

            require(actualLength == length) {
                "Declared length ($length) does not match actual read data size ($actualLength)"
            }

            println("*COMMIT*")

            println("tree: $treeHash")

            println("parents: ${parentHashes.joinToString(" ")}")

            println("author: $authorName $authorEmail original timestamp: ${parseDateTime(originalDate)}")

            println("committer: $committerName $committerEmail commit timestamp: ${parseDateTime(commitDate)}")

            println("commit message:")
            println(message)
        }

        fun parseTreeBody() {
            class Item(val name: String, val hash: String, val permission: String)
            val items = mutableListOf<Item>()

            while (!eofReached) {
                val line = readOneLine()
                if (line.isEmpty()) break
                val (permission, name) = line.split(" ")
                val hash = readBinaryHash()
                items.add(Item(name, hash, permission))
            }

            require(actualLength == length) {
                "Declared length ($length) does not match actual read data size ($actualLength)"
            }

            println("*TREE*")
            for (i in items) {
                println("${i.permission} ${i.hash} ${i.name}")
            }
        }
    }

    companion object {
        fun parseFromFile(filepath: String) {
            val reader = Reader(filepath)
            return when (reader.type) {
                "blob" -> reader.parseBlobBody()
                "commit" -> reader.parseCommitBody()
                "tree" -> reader.parseTreeBody()
                else -> throw IllegalArgumentException("Unknown object type ${reader.type}")
            }
        }
    }
}

fun listBranches(gitDir: String) {
    val head = File("$gitDir/HEAD").readText().trim().substringAfter(" ")

    val heads = File("$gitDir/refs/heads")
    val shortNames = heads.list()
    val longNames = heads.listFiles()

    for (i in longNames.indices) {
        val longName = longNames[i].toString().replace("\\", "/")
        if (longName.contains(head)) {
            println("* ${shortNames[i]}")
        } else {
            println("  ${shortNames[i]}")
        }
    }
}

fun main(args: Array<String>) {
    //println("The current working directory is ${System.getProperty("user.dir")}")

    val scanner = Scanner(System.`in`)

    println("Enter .git directory location:")
    val gitDir = scanner.next()

    println("Enter command:")
    when (val command = scanner.next()) {
        "cat-file" -> {
            println("Enter git object hash:")
            val hash = scanner.next()
            val objectPath = "$gitDir/objects/${hash.substring(0, 2)}/${hash.substring(2, 40)}"
            GitObject.parseFromFile(objectPath)
        }

        "list-branches" -> {
            listBranches(gitDir)
        }

        else -> println("unknown command '$command'")
    }
}




