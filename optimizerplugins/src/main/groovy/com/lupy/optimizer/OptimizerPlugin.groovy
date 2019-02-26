package com.lupy.optimizer

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Lupy
 * @since 2019/2/25
 * @description 压缩图片插件
 */
class OptimizerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        if (!project.plugins.hasPlugin(AppPlugin)) {
            throw new GradleException("只能在Application工程中使用")
        }


        project.afterEvaluate {
            project.android.applicationVariants.all {
                BaseVariant variant ->
                    project.tasks.create("optimizerTask${baseVariant.name}", OptimizerTask){
                        manifestFile = variant.outputs.first().processManifest.aaptFriendlyManifestOutputFile
                        minSdk = variant.mergeResources.minSdk
                    }
            }
        }
    }
}