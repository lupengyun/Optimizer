package com.lupy.optimizer


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

    @Input
    def manifestFile

    @Input
    def minSdk


    OptimizerTask(){
        group = 'optimizer'
        outputs.upToDateWhen {false}
        guetzliTools = OptimizerUtils.getGuetzliName(project)
        pngbrushTools = OptimizerUtils.getPngcrushName(project)
        cwebpTools = OptimizerUtils.getWebpToolsName(project)

    }

    @TaskAction
    def run(){

        



    }

}