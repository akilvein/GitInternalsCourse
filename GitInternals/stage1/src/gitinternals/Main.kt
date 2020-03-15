package gitinternals

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream


fun printObject(fullFilePath: String) {
    println()
    println(fullFilePath)
    println("**************************************************************")
    println()

    val fis = FileInputStream(fullFilePath)
    val fos = ByteArrayOutputStream()
    val iis = InflaterInputStream(fis)

    var data: Int
    while (iis.read().also { data = it } != -1) {
        fos.write(data)
    }
    iis.close()

    println(fos.toString(Charsets.UTF_8))
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






