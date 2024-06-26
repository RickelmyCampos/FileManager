package br.com.gilbersoncampos.filemanager.data.repository

import android.util.Log
import br.com.gilbersoncampos.filemanager.data.mapper.toModel
import br.com.gilbersoncampos.filemanager.data.model.FileModel
import java.io.File

interface FileRepository{
    fun loadFile(path:String):List<FileModel>
    fun deleteFile(path:String)
    fun deleteFiles(listFiles:List<FileModel>)
    fun createFolder(path: String,name:String)
    fun renameFile(file:FileModel, newName:String)
}
class FileRepositoryImpl:FileRepository {
    override fun loadFile(path: String):List<FileModel> {
        val file = File(path)
        val list = file.listFiles()?.toList() ?: emptyList()
        return list.map { it.toModel() }
    }

    override fun deleteFile(path: String) {
        val f=File(path)
        f.delete()
    }

    override fun deleteFiles(listFiles: List<FileModel>) {
        listFiles.forEach { file->
            if(file.isSelected){
               deleteFile(file.path)
            }
        }
    }

    override fun createFolder(path: String,name:String) {
        val dir = File(path, name)
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    override fun renameFile(file: FileModel, newName: String) {

       val fileF=File(file.path)
        val newNamePath=file.path.replace(file.fileName,newName)
        val sucess=fileF.renameTo(File(newNamePath))
        Log.d("FileRepository","REnomeou $sucess\n OldPathFile: $file.parent+newName \n NewName: ${newNamePath}")
    }

}