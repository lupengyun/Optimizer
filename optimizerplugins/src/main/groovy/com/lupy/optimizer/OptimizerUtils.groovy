package com.lupy.optimizer

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.GradleException
import org.gradle.api.Project

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 * @author Lupy
 * @since 2019/2/25
 * @description 压缩工具操作类
 */
class OptimizerUtils {

    def static final PNG = ".png"
    def static final JPG = ".jpg"
    def static final JPEG = ".jpeg"
    def static final WEBP = ".webp"
    def static final PNG9 = ".9.png"

    /**
     * 是否含有alpha通道
     * @param file
     * @return
     */
    def static isTransporent(File file) {
        def read = ImageIO.read(file)
        return read.colorModel.hasAlpha()
    }

    def static optimizerPng(File file) {
        return (file.name.endsWith(PNG) || file.name.endsWith(PNG.toUpperCase())) &&
                !file.name.endsWith(PNG9) && !file.name.endsWith(PNG9.toUpperCase())
    }

    def static optimizerJpg(File file) {
        return file.name.endsWith(JPG) ||
                file.name.endsWith(JPG.toUpperCase()) ||
                file.name.endsWith(JPEG) ||
                file.name.endsWith(JPEG.toUpperCase())
    }

    /**
     * 获取web生成工具名字
     * @return
     */
    def static getWebpToolsName(Project project) {
        return getToolsName(project, "cwebp")
    }
    /**
     * 获取png压缩工具
     * @return
     */
    def static getPngcrushName(Project project) {
        return getToolsName(project, "pngcrush")
    }

    /**
     * 获取jpg压缩工具
     * @return
     */
    def static getGuetzliName(Project project) {
        return getToolsName(project, "guetzli")
    }

    def static getToolsName(Project project, String name) {
        def fileName

        if (Os.isFamily(Os.FAMILY_MAC)) {
            fileName = "${name}_darwin"
        } else if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            fileName = "${name}_win.exe"
        } else {
            fileName = "${name}_linux"
        }

        def toolsName = "${project.buildDir}/tools/${fileName}"
        def file = new File(toolsName)
        if (file.exists()) {
            return file
        } else {
            file.parentFile.mkdirs()
            new FileOutputStream(file).withStream {
                def is = OptimizerUtils.class.getResourceAsStream("/$name/${fileName}")
                it.write(is.bytes)
                is.close()
            }
            if (file.exists() && file.setExecutable(true)) {
                return file
            } else {
                throw new GradleException("工具不存在")
            }

        }
    }
}