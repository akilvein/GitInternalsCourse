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
    abstract fun print()

    class Blob(val text: String) : GitObject() {
        override fun print() {
            println("*BLOB*")
            println(text)
        }
    }

    class Commit(val tree: String,
                 val parents: String,
                 val author: String,
                 val committer: String,
                 val message: String) : GitObject() {

        override fun print() {
            println("*COMMIT*")
            println("tree: $tree")
            println("parents: $parents")
            println("author: $author")
            println("committer: $committer")
            println("commit message:")
            println(message)
        }
    }

    class Tree(val items: List<Item>) : GitObject() {
        class Item(val name: String, val hash: String, val permission: String)

        override fun print() {
            println("*TREE*")
            for (i in items) {
                println("${i.permission} ${i.hash} ${i.name}")
            }
        }
    }

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

        fun parseBlobBody() : Blob {
            val text = readRemainingLines()

            require(length == text.length) {
                "Declared length ($length) does not match actual text length(${text.length})"
            }

            return Blob(text)
        }

        private fun parseDateTime(sinceEpoch: String): String {
            val (millis, zone) = sinceEpoch.split(" ")
            val dateTime = Instant.ofEpochSecond(millis.toLong()).atZone(ZoneId.of(zone))
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").format(dateTime)
        }

        fun parseCommitBody() : Commit {
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

            return Commit(
                    tree = treeHash,
                    parents = parentHashes.joinToString(" "),
                    author = "$authorName $authorEmail original timestamp: ${parseDateTime(originalDate)}",
                    committer = "$committerName $committerEmail commit timestamp: ${parseDateTime(commitDate)}",
                    message = message)
        }

        fun parseTreeBody() : Tree {

            val items = mutableListOf<Tree.Item>()

            while (!eofReached) {
                val line = readOneLine()
                if (line.isEmpty()) break
                val (permission, name) = line.split(" ")
                val hash = readBinaryHash()
                items.add(Tree.Item(name, hash, permission))
            }

            require(actualLength == length) {
                "Declared length ($length) does not match actual read data size ($actualLength)"
            }

            return Tree(items)
        }
    }

    companion object {
        fun parseFromFile(filepath: String) : GitObject {
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

fun showLog(gitDir: String, branchName: String) {
    var hash = File("$gitDir/refs/heads/$branchName").readText().trim()
    require(hash.length == 40)

    while (!hash.isNullOrEmpty()) {
        println("Commit: $hash")
        val commit = GitObject.parseFromFile("$gitDir/objects/${hash.substring(0, 2)}/${hash.substring(2, 40)}")

        if (commit is GitObject.Commit) {
            println(commit.committer)
            println(commit.message)

            hash = commit.parents.split(" ").first()
        }
        else {
            require(false)
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
            GitObject.parseFromFile(objectPath).print()
        }

        "list-branches" -> {
            listBranches(gitDir)
        }

        "log" -> {
            println("Enter branch name:")
            val branchName = scanner.next()
            showLog(gitDir, branchName)
        }

        else -> println("unknown command '$command'")
    }
}




