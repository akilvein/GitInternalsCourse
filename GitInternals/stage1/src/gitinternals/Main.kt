package gitinternals

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.lang.IllegalArgumentException
import java.util.*
import java.util.zip.InflaterInputStream


sealed class GitObject {
    class Blob(val text: String) : GitObject() {
        override fun toString(): String = "Blob: $text"
    }

    class Commit(val tree: String,
                 val parents: Array<String>,
                 val author: String,
                 val committer: String,
                 val date: String,
                 val message: String) : GitObject() {
        override fun toString(): String =
                """ |tree: $tree
                    |parents: ${parents.joinToString()}
                    |author: $author
                    |committer: $committer
                    |date: $date
                    |message: 
                    |$message
                """.trimMargin()
    }

    class Tree() : GitObject() {
        override fun toString(): String = "Tree: NOT IMPLEMENTED"
    }

    companion object {
        fun parseFromFile(filepath: String): GitObject {
            val fis = FileInputStream(filepath)
            val iis = InflaterInputStream(fis)

            var actualLength = 0

            fun readOneLine(): String {
                val bos = ByteArrayOutputStream()
                while (true) {
                    val data = iis.read()
                    if (data >= 0) {
                        actualLength++
                    }
                    if (data > 0 && data != '\n'.toInt()) {
                        bos.write(data)
                    }
                    else {
                        break
                    }
                }
                return bos.toString(Charsets.UTF_8)
            }

            fun readRemainingLines(): String {
                val bos = ByteArrayOutputStream()
                while (true) {
                    val data = iis.read()
                    if (data >= 0) {
                        actualLength++
                    }
                    if (data > 0) {
                        bos.write(data)
                    }
                    else {
                        break
                    }
                }
                return bos.toString(Charsets.UTF_8)
            }

            val (type, declaredLength) = readOneLine().split(" ")
            actualLength = 0

            when (type) {
                "blob" -> {
                    val text = readRemainingLines()
                    require(declaredLength.toInt() == text.length) {
                        "Declared length ($declaredLength) does not match actual text length(${text.length})"
                    }
                    return Blob(text)
                }

                "commit" -> {
                    var line = readOneLine().trim()
                    val (tree, treeHash) = line.split(" ")
                    require(tree == "tree")
                    require(treeHash.length == 40)

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

                    val (committer, committerLine) = readOneLine().split(delimiters = *arrayOf(" "), limit = 2)
                    require(committer == "committer")

                    val message = readRemainingLines() + 1

                    require(actualLength == declaredLength.toInt()) {
                        "Declared length ($declaredLength) does not match actual read data size ($actualLength)"
                    }

                    return Commit(treeHash, parentHashes.toTypedArray(), authorLine, committerLine, "TODO", message)
                }

                "tree" -> {
                    return Tree()
                }
            }

            throw IllegalArgumentException("could not parse object file")
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






