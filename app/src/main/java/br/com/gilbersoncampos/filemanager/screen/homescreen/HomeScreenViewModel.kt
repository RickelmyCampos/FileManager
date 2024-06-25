package br.com.gilbersoncampos.filemanager.screen.homescreen

import android.os.Environment
import androidx.lifecycle.ViewModel
import br.com.gilbersoncampos.filemanager.data.mapper.toModel
import br.com.gilbersoncampos.filemanager.data.model.FileModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class HomeScreenViewModel : ViewModel() {
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
        val file = File(path)
        val list = file.listFiles()?.toList() ?: emptyList()
        val listModel = list.map { it.toModel() }

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
    fun createFolder(name: String) {
        val diretorio = File(_uiState.value.currentPath, name)
        if (!diretorio.exists()) {
            diretorio.mkdirs()
            loadFiles(_uiState.value.currentPath)
        }
    }
}

data class HomeUiState(
    val currentPath: String,
    val listFiles: List<FileModel>,
    val historicDirectory: List<String>
)