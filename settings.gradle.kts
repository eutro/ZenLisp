rootProject.name = "ZenLisp"
include("ZenCode")

val excludedProjects = setOf(
        "ModuleDeserializer",
        "ModuleSerializationShared",
        "ModuleSerializer",
        "JavaSource",
        "JavaSourceCompiler",
        "IDE",
        "Constructor",
        "DrawableGui",
        "DrawableGuiIconConverter",
        "CompilerShared"
)

val zenCodeDir = rootDir.resolve("ZenCode")

zenCodeDir.listFiles { file ->
    file.isDirectory && !excludedProjects.contains(file.name) &&
            file.resolve("build.gradle").isFile
}!!.forEach { dir ->
    include(":${dir.name}")
    project(":${dir.name}").projectDir = zenCodeDir.resolve(dir.name)
}
include("ZenLispMod")
