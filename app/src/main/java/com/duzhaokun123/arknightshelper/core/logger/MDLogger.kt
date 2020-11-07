package com.duzhaokun123.arknightshelper.core.logger

import android.os.Environment
import com.duzhaokun123.arknightshelper.application
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.Util
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import kotlin.random.Random

class MDLogger private constructor(private val path: String, name: String) : Logger {

    constructor(name: String, cache: Boolean = true) : this(
        File(
            if (cache) {
                application.externalCacheDir
            } else {
                application.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            }, "$name${System.currentTimeMillis()}"
        ).also {
            if (it.exists().not()) {
                it.mkdirs()
            }
        }.path, "Log.md"
    )

    private val outputStreamWriter = OutputStreamWriter(FileOutputStream(File(path, name)))

    override fun logImg(
        tag: String,
        img: Mat,
        func: String,
        title: String,
        level: Int
    ) {
        val name = "$title${Random.nextInt()}.png"
        val file = File(path, name).path
        Imgcodecs.imwrite(file, img)
        writeFile("$func: ![$title](./$name)\n\n", level)
    }

    override fun logH1(tag: String, func: String, msg: String, level: Int) {
        writeFile("# $msg\n\n")
    }

    override fun logH2(tag: String, func: String, msg: String, level: Int) {
        writeFile("## $msg\n\n")
    }

    override fun logH3(tag: String, func: String, msg: String, level: Int) {
        writeFile("### $msg\n\n")
    }

    override fun logText(tag: String, func: String, msg: String, level: Int) {
        writeFile("$msg\n\n", level)
    }

    override fun logDivider(tag: String, func: String, level: Int) {
        writeFile("---\n\n")
    }

    override fun getChild(name: String): MDLogger {
        writeFile("child log: [$name]($name)\n\n")

        return MDLogger(path, "$name${System.currentTimeMillis()}.md")
    }

    override fun close() {
        outputStreamWriter.close()
    }

    private fun writeFile(string: String, level: Int = -1) {
        if (level != -1) {
            outputStreamWriter.write("${Util.getLogLevelString(level)}/")
        }
        outputStreamWriter.write(string)
        outputStreamWriter.flush()
    }
}