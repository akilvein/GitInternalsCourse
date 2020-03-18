package gitinternals

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.zip.InflaterInputStream
import kotlin.IllegalArgumentException


sealed class GitObject {
    class Blob(val text: String) : GitObject() {
        override fun toString(): String = "Blob: $text"
    }

    class Commit(val tree: String,
                 val parents: Array<String>,
                 val author: Person,
                 val committer: Person,
                 val message: String) : GitObject() {
        class Person(val name: String, val email: String, val date: String)
        override fun toString(): String =
                """ |Commit:
                    |tree: $tree
                    |parents: ${parents.joinToString()}
                    |author: ${author.name} ${author.email} ${author.date}
                    |committer: ${committer.name} ${committer.email} ${committer.date}
                    |message: 
                    |$message
                """.trimMargin()
    }

    class Tree(val items: Array<Item>) : GitObject() {
        class Item(val name: String, val hash: String, val permission: String)

        override fun toString(): String {
            var result = "Tree: \n"
            for (i in items) {
                result += "${i.name}: ${i.hash} (${i.permission})\n"
            }
            return result
        }
    }

    class Reader(filepath: String) {
        val fis = FileInputStream(filepath)
        val iis = InflaterInputStream(fis)
        var actualLength = 0
        var eofReached = false

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

        fun readOneLine(): String {
            val bos = ByteArrayOutputStream()
            readLoop@while (true) {
                val data = iis.read()
                when {
                    data < 0 -> {
                        eofReached = true
                        break@readLoop
                    }
                    data > 0 && data != '\n'.toInt() -> {
                        bos.write(data)
                        actualLength++
                    }
                    else -> {
                        actualLength++
                        break@readLoop
                    }
                }
            }
            return bos.toString(Charsets.UTF_8)
        }

        fun readRemainingLines(): String {
            val bos = ByteArrayOutputStream()
            readLoop@while (true) {
                val data = iis.read()
                when {
                    data < 0 -> {
                        eofReached = true
                        break@readLoop
                    }
                    data > 0 -> {
                        bos.write(data)
                        actualLength++
                    }
                    else -> actualLength++
                }
            }
            return bos.toString(Charsets.UTF_8)
        }

        fun parseBlobBody(declaredLength: Int): Blob {
            val text = readRemainingLines()
            require(declaredLength == text.length) {
                "Declared length ($declaredLength) does not match actual text length(${text.length})"
            }
            return Blob(text)
        }

        fun parseCommitBody(declaredLength: Int): Commit {
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

            val message = readRemainingLines()

            require(actualLength == declaredLength) {
                "Declared length ($declaredLength) does not match actual read data size ($actualLength)"
            }

            return Commit(
                    treeHash,
                    parentHashes.toTypedArray(),
                    Commit.Person(authorName, authorEmail, originalDate),
                    Commit.Person(committerName, committerEmail, commitDate),
                    message)
        }

        fun parseTreeBody(declaredLength: Int): Tree {
            val items = mutableListOf<Tree.Item>()
            while (!eofReached) {
                val line = readOneLine()
                if (line.isEmpty()) break
                val (permission, name) = line.split(" ")
                val hash = readBinaryHash()
                items.add(Tree.Item(name, hash, permission))
            }

            require(actualLength == declaredLength) {
                "Declared length ($declaredLength) does not match actual read data size ($actualLength)"
            }

            return Tree(items.toTypedArray())
        }
    }

    companion object {
        fun parseFromFile(filepath: String): GitObject {
            val reader = Reader(filepath)

            val (type, declaredLength) = reader.readOneLine().split(" ")
            reader.actualLength = 0

            return when (type) {
                "blob" -> reader.parseBlobBody(declaredLength.toInt())
                "commit" -> reader.parseCommitBody(declaredLength.toInt())
                "tree" -> reader.parseTreeBody(declaredLength.toInt())
                else -> throw IllegalArgumentException("Unknown object type $type")
            }
        }
    }
}


fun parseObject(filepath: String) {
    val fis = FileInputStream(filepath)
    val inStream = InflaterInputStream(fis)
    var i = -1
    while (inStream.read().also { i = it } != 0) {
        //First line
    }

    //Content data

    //Content data
    while (inStream.read().also { i = it } != -1) {
        while (inStream.read().also { i = it } != 0x20) { //0x20 is the space char
            //Permission bytes
        }

        //Filename: 0-terminated
        var filename = ""
        while (inStream.read().also { i = it } != 0) {
            filename += i.toChar()
        }

        //Hash: 20 byte long, can contain any value, the only way
        // to be sure is to count the bytes
        var hash: String? = ""
        for (count in 0..19) {
            i = inStream.read()
            hash += Integer.toHexString(i)
        }
    }
}


fun printObject(fullFilePath: String) {
    println()
    println(fullFilePath)
    println("**************************************************************")
    println()

    val o = GitObject.parseFromFile(fullFilePath)


    println(o.toString())
}

fun main(args: Array<String>) {
//    val scanner = Scanner(System.`in`)
//    println("Enter object hash:")
//    val hash = scanner.next()

    //C:\Users\akilvein\IdeaProjects\GitInternals\GitInternals\stage1\
    val objectDir = "C:\\Users\\akilvein\\IdeaProjects\\GitInternals\\GitInternals\\stage1\\repo\\objects\\"
//    val hash = "8d7b82203af8bdf491d68325a7f34ff8650f0df5"
//    val dir = hash.substring(0, 2) + "\\"
//    val filename = hash.substring(2, 40)
//    val fullFilePath = objectDir + dir + filename


    File(objectDir).walk().filter { it.isFile }.forEach { printObject(it.toString()) }


}






