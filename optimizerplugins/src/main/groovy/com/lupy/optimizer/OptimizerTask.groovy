package com.lupy.optimizer

import groovy.xml.Namespace
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * @author Lupy
 * @since 2019/2/25
 * @description 压缩图片任务
 */
class OptimizerTask extends DefaultTask {


    def guetzliTools
    def pngbrushTools
    def cwebpTools
    def icon
    def roundIcon

    @Input
    def manifestFile

    @Input
    def minSdk

    @Input
    File fileDir


    OptimizerTask() {
        group = 'optimizer'
        outputs.upToDateWhen { false }
        guetzliTools = OptimizerUtils.getGuetzliName(project)
        pngbrushTools = OptimizerUtils.getPngcrushName(project)
        cwebpTools = OptimizerUtils.getWebpToolsName(project)

    }

    @TaskAction
    def run() {
        println "=====================开始压缩图片======================"
        println "jpg压缩工具 ==》${guetzliTools}"
        println "png压缩工具 ==》 ${pngbrushTools}"
        println "wep转换工具 ==》 ${cwebpTools}"
        println "manifest ==> ${manifestFile}"
        println "fileDir ==> ${fileDir.absolutePath}"

        def ns = new Namespace("http://schemas.android.com/apk/res/android", "android")
        Node node = new XmlParser().parse(manifestFile)
        Node application = node.application[0]
        icon = application.attributes()[ns.icon]
        roundIcon = application.attributes()[ns.roundIcon]
        icon = icon.substring(icon.indexOf('/'))
        roundIcon = roundIcon.substring(roundIcon.indexOf('/'))
        println "icon ==> ${icon}"
        println "roundIcon ==> ${roundIcon}"

        //遍历文件
        println "==============>遍历文件"

        def jpgs = []
        def pngs = []
        fileDir.eachDir {
            if (it.name.startsWith("drawable") || it.name.startsWith("mipmap")) {
                it.eachFile {
                    if (OptimizerUtils.optimizerJpg(it)) {
                        jpgs << it
                    } else if (OptimizerUtils.optimizerPng(it) && !isIcon(it)) {
                        pngs << it
                    }

                }
            }
        }

        println "min sdk is ${minSdk}"

        if (minSdk > 14 && minSdk < 18) {
            def compress = []
            pngs.each {
                if (OptimizerUtils.isTransporent(it)) {
                    compress << it
                } else {
                    convertWep(it)
                }
            }

            compress.each {
                compressPng(it)
            }

            jpgs.each {
                convertWep(it)
            }

        } else if (minSdk >= 18) {
            pngs.each {
                convertWep(it)
            }

            jpgs.each {
                convertWep(it)
            }
        } else {
            pngs.each {
                compressPng(it)
            }

            jpgs.each {
                compressJpg(it)
            }
        }

        println "=====================压缩结束======================"

    }


    def compressPng(File file) {
        def out = new File(file.parent,"temp-preoptimizer-${file.name}")
        def result = "${pngTool} -brute -rem alla -reduce -q ${file.absolutePath} ${out.absolutePath}".execute()
        result.waitForProcessOutput()
        if (result.exitValue() == 0){
            file.delete()
            out.renameTo(file)
            println "compress png ${file.absolutePath} success"
        }else{
            println "compress png ${file.absolutePath} error"
        }
    }

    def compressJpg(File file) {
        def out = new File(file.parent,"temp-preoptimizer-${file.name}")
        def result = "${jpgTool} --quality 84 ${file.absolutePath} ${out.absolutePath}".execute()
        result.waitForProcessOutput()
        if (result.exitValue() == 0){
            file.delete()
            out.renameTo(file)
            println "compress png ${file.absolutePath} success"
        }else{
            println "compress png ${file.absolutePath} error"
        }
    }

    def convertWep(File file) {
        String name = file.name
        name = name.substring(0, name.lastIndexOf('.'))
        def result = "$cwebpTools -q 75 ${file.absolutePath} -o ${file.parentFile}/${name}.webp".execute()
        result.waitForProcessOutput()
        if (result.exitValue() == 0) {
            prilnt "${name} convert to webp success"
            file.delete()
        } else {
            prilnt "${name} convert to webp fails"
        }
    }


    def isIcon(File file) {
        return file.name.equals("${icon}.png") || file.name.equals("${roundIcon}.png")
    }
}