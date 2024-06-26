package br.com.gilbersoncampos.filemanager.screen.homescreen

import android.os.Environment
import androidx.lifecycle.ViewModel
import br.com.gilbersoncampos.filemanager.data.model.FileModel
import br.com.gilbersoncampos.filemanager.data.repository.FileRepository
import br.com.gilbersoncampos.filemanager.data.repository.FileRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeScreenViewModel(private val fileRepository: FileRepository = FileRepositoryImpl()) :
    ViewModel() {
    private val initialPath = Environment.getExternalStorageDirectory().absolutePath
    private val _uiState = MutableStateFlow(
        HomeUiState(
            currentPath = "",
            listFiles = listOf(),
            historicDirectory = listOf()
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val stackDirectories: MutableList<String> = mutableListOf(initialPath)

    init {
        loadFiles(initialPath)
    }

    fun loadFiles(path: String) {
        val listModel = fileRepository.loadFile(path)
        _uiState.value = _uiState.value.copy(
            listFiles = listModel,
            currentPath = path,
            historicDirectory = listOf()
        )
    }

    fun addInStackDirectory(path: String) {
        if (stackDirectories.last() != path) {
            stackDirectories.add(path)
        }
    }

    fun backDirectory() {
        if (stackDirectories.size > 1) {
            stackDirectories.removeLast()
            loadFiles(stackDirectories.last())
        }
    }

    fun onClickFile(file: FileModel) {
        if (file.isDirectory) {
            val path = file.absolutePath
            loadFiles(path)
            addInStackDirectory(path)
        } else {
            // Adicionar ação para abrir arquivos se necessário
        }
    }

    fun onLongPressFile(file: FileModel) {
        val index = _uiState.value.listFiles.indexOf(file)
        if (index != -1) {
            val mListFile = _uiState.value.listFiles.toMutableList()
            mListFile[index] = file.copy(isSelected = !file.isSelected)
            _uiState.value = _uiState.value.copy(listFiles = mListFile)

        }

    }

    fun deleteFolders() {
        //TODO não apaga se tiver algo dentro (abrir um popup ou detelar todos os filhos)
        fileRepository.deleteFiles(_uiState.value.listFiles)
        loadFiles(_uiState.value.currentPath)

    }

    fun createFolder(name: String) {
        fileRepository.createFolder(_uiState.value.currentPath, name)
        loadFiles(_uiState.value.currentPath)
    }
}

data class HomeUiState(
    val currentPath: String,
    val listFiles: List<FileModel>,
    val historicDirectory: List<String>
)