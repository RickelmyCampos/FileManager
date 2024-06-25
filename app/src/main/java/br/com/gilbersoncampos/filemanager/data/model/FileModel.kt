package br.com.gilbersoncampos.filemanager.data.model

data class FileModel(
    val fileName: String,
    val isDirectory: Boolean,
    val isFile: Boolean,
    val path:String,
    val absolutePath:String,
    val isSelected:Boolean=false
)