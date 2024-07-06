package com.gilbersoncampos.domain.model

data class File(
    val name: String,
    val path: String,
    val isFile: Boolean,
    val isDirectory: Boolean,
    val isHidden: Boolean,
    val absolutePath: String
)
