package br.com.gilbersoncampos.filemanager.data.mapper

import br.com.gilbersoncampos.filemanager.data.model.FileModel
import java.io.File

 fun File.toModel(): FileModel =
    FileModel(fileName = name, isFile = isFile, isDirectory = isDirectory, path = path, absolutePath = absolutePath)