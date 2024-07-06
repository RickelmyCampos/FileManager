package com.gilbersoncampos.domain.extensions

import java.io.File
import com.gilbersoncampos.domain.model.File as FileModel

 fun File.toModel(): FileModel =
     FileModel(
        name = name,
        path = path,
        absolutePath = absolutePath,
        isFile = isFile,
        isDirectory = isDirectory,
        isHidden = isHidden
    )