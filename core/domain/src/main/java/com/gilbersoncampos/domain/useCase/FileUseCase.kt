package com.gilbersoncampos.domain.useCase

import com.gilbersoncampos.domain.extensions.toModel
import java.io.File
import com.gilbersoncampos.domain.model.File as FileModel

class FileUseCase {
    fun getListFiles(path: String): List<FileModel> {
        val file = File(path)
        val list = file.listFiles()?.toList() ?: emptyList()
        return list.map { it.toModel() }
    }

    fun createFolder(path: String, name: String) {
        val dir = File(path, name)
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    fun deleteFile(file: FileModel) {
        val f = File(file.path)
        f.delete()
    }

    fun renameFile(file: FileModel, newName: String): Boolean {
        val fileF = File(file.path)
        val newNamePath = file.path.replace(file.name, newName)
        return fileF.renameTo(File(newNamePath))
    }

    fun deleteFiles(files: List<FileModel>) {
        files.forEach { f ->
            deleteFile(f)
        }
    }
}



