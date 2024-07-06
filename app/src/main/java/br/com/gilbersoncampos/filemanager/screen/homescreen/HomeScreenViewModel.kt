package br.com.gilbersoncampos.filemanager.screen.homescreen

import android.os.Environment
import androidx.lifecycle.ViewModel
import com.gilbersoncampos.domain.model.File
import com.gilbersoncampos.domain.useCase.FileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeScreenViewModel(private val fileUseCase: FileUseCase = FileUseCase()) :
    ViewModel() {
    private val initialPath = Environment.getExternalStorageDirectory().absolutePath
    private val _uiState = MutableStateFlow(
        HomeUiState(
            currentPath = "",
            listFiles = listOf(),
            historicDirectory = listOf(),

            listSelected = listOf()
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val stackDirectories: MutableList<String> = mutableListOf(initialPath)

    init {
        loadFiles(initialPath)
    }

    fun loadFiles(path: String) {
        val listModel = fileUseCase.getListFiles(path)
        _uiState.value = _uiState.value.copy(
            listFiles = listModel,
            currentPath = path,
            historicDirectory = listOf()
        )
        clearListSelected()
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

    fun onClickFile(file: File) {
        if (file.isDirectory) {
            val path = file.absolutePath
            loadFiles(path)
            addInStackDirectory(path)
        } else {
            // Adicionar ação para abrir arquivos se necessário
        }
    }

    fun onLongPressFile(file: File) {
        val index = _uiState.value.listFiles.indexOf(file)
        val mListSelected = _uiState.value.listSelected.toMutableList()
        if (index != -1) {
            if (mListSelected.contains(file)) {
                mListSelected.remove(file)
            } else {
                mListSelected.add(file)
            }
            _uiState.value =
                _uiState.value.copy(listSelected = mListSelected)
        }
    }


    fun deleteFolders() {
        //TODO não apaga se tiver algo dentro (abrir um popup ou detelar todos os filhos)
        fileUseCase.deleteFiles(_uiState.value.listFiles)
        loadFiles(_uiState.value.currentPath)

    }

    fun renameFile(file: File, newName: String) {
        fileUseCase.renameFile(file, newName)

        loadFiles(_uiState.value.currentPath)

    }

    fun createFolder(name: String) {
        fileUseCase.createFolder(_uiState.value.currentPath, name)
        loadFiles(_uiState.value.currentPath)
    }

    private fun clearListSelected() {
        val mListSelected = _uiState.value.listSelected.toMutableList()
        mListSelected.clear()
        _uiState.value = _uiState.value.copy(listSelected = mListSelected)
    }
}

data class HomeUiState(
    val currentPath: String,
    val listFiles: List<File>,
    val historicDirectory: List<String>,
    val listSelected: List<File>,

    )