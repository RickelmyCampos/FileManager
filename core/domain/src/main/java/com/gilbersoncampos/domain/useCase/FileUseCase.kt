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
            val result = dir.mkdirs()
            if (!result)
                throw Exception("não foi possível criar a pasta")
        }
    }

    fun deleteFile(file: FileModel, deleteChildren: Boolean = false) {
        val f = File(file.path)
        val result = if (deleteChildren) f.deleteRecursively() else f.delete()
        if (!result)
            throw Exception("não foi possível deletar")
    }

    fun renameFile(file: FileModel, newName: String) {
        val fileF = File(file.path)
        val newNamePath = file.path.replace(file.name, newName)
        val result = fileF.renameTo(File(newNamePath))
        if (!result)
            throw Exception("não foi possível renomear o Arquivo")
    }

    fun deleteFiles(files: List<FileModel>) {
        files.forEach { f ->
            deleteFile(f, true)
        }
    }

    fun createFile(path: String, name: String) {
        val dir = File(path, name)
        val result = dir.createNewFile()
        if (!result)
            throw Exception("não foi possível criar arquivo")
    }

    fun copyFileFolder(mFile: FileModel, targetPath: String) {
        val file = File(mFile.path)
        val fileTarget = File(targetPath)
        val result = file.copyRecursively(fileTarget) { _, _ ->
            OnErrorAction.TERMINATE
            //TODO Talvez pegar a exceção exata para validar
        }
        if (!result)
            //TODO Talvez verificar se tem vestigos na pasta copiada e remover os vestígios
            throw Exception("não foi possível copiar")

    }

    fun moveFileFolder(file: FileModel, targetPath: String) {
        copyFileFolder(file, targetPath)

        deleteFile(file, true)

    }

    fun moveFiles(files: List<FileModel>, targetPath: String) {
        files.forEach { f ->
            moveFileFolder(f, targetPath)
        }
    }
}



