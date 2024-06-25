@file:OptIn(ExperimentalFoundationApi::class)

package br.com.gilbersoncampos.filemanager.screen.homescreen

import androidx.annotation.ColorRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.gilbersoncampos.filemanager.data.model.FileModel
import br.com.gilbersoncampos.filemanager.ui.theme.FileManagerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeScreenViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var isGrade by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { }, actions = {
            var menuOptionsIsShow by remember {
                mutableStateOf(false)
            }
            IconButton(onClick = { menuOptionsIsShow = !menuOptionsIsShow }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
                DropdownMenu(
                    expanded = menuOptionsIsShow,
                    onDismissRequest = { menuOptionsIsShow = !menuOptionsIsShow }) {
                    DropdownMenuItem(text = { Text(text = "Deletar") }, onClick = { viewModel.deleteFolders() })
                }
            }
        })
        PathComponent(uiState.currentPath) {
            viewModel.loadFiles(it)
            viewModel.addInStackDirectory(it)
        }
        if (showDialog) {
            DialogFolder(onDismiss = { showDialog = false }) {
                viewModel.createFolder(it)
            }
        }
        Row {
            Button(onClick = {
                viewModel.backDirectory()
            }) {
                Text(text = "Voltar")
            }
            Button(onClick = {
                isGrade = !isGrade
            }) {
                Text(text = if (isGrade) "lista" else "Grade")
            }
            Button(onClick = { showDialog = true }) {
                Text(text = "Criar Pasta")
            }
        }
        if (isGrade) {
            LazyVerticalGrid(columns = GridCells.Fixed(if (isGrade) 2 else 1)) {
                items(uiState.listFiles) { file ->

                    FileGradeItem(file = file, onClick = {
                        viewModel.onClickFile(file)
                    }, onLongPress = {
                        viewModel.onLongPressFile(file)
                    })


                }
            }
        } else {
            LazyColumn {
                items(uiState.listFiles) { file ->

                    FileItem(file) {
                        if (file.isDirectory) {
                            viewModel.loadFiles(file.absolutePath)
                        } else {
                            // Adicionar ação para abrir arquivos se necessário
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun DialogFolder(onDismiss: () -> Unit, onCreate: (String) -> Unit) {
    var text by remember {
        mutableStateOf("")
    }
    Dialog(onDismissRequest = onDismiss) {
        Card() {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Insira o nome da Pasta")
                TextField(value = text, onValueChange = { text = it })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = onDismiss) {
                        Text(text = "Cancelar")
                    }
                    Button(onClick = {
                        onCreate(text)
                        onDismiss()
                    }) {
                        Text(text = "Criar")
                    }
                }
            }

        }
    }
}

@Composable
fun PathComponent(path: String, onClick: (String) -> Unit) {
    val listDirectory = path.split("/")
    Row {
        val first = listDirectory.size - 3

//        repeat(if(first>0)3 else listDirectory.size){
//            val directory=listDirectory[it+first]
//            if(it+first>0){
//                Text(text = directory + ">", modifier = Modifier.clickable {
//                    val newPath =  listDirectory.reducePathInToDirectory(directory)
//                    onClick(newPath)
//                })
//            }else{
//                Text(text = "Home")
//            }
//
//        }
        LazyRow {
            items(listDirectory) { directory ->

                Text(text = directory + ">", modifier = Modifier.clickable {
                    val newPath = listDirectory.reducePathInToDirectory(directory)
                    onClick(newPath)
                })

            }
        }
    }
}

fun List<String>.reducePathInToDirectory(directory: String): String {
    val subList = this.subList(0, this.indexOf(directory) + 1)
    return subList.reduce { acc, s -> "$acc/$s" }
}

@Composable
fun FileItem(file: FileModel, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {

        Icon(
            imageVector = if (file.isDirectory) Icons.Default.Email else Icons.Default.Info,
            contentDescription = null
        )
        Text(text = file.fileName)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileGradeItem(file: FileModel, onClick: () -> Unit, onLongPress: () -> Unit) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            Modifier
                .size(158.dp)
                .padding(8.dp)
        ) {
            if (file.isSelected) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .combinedClickable(
                            onClick = { onLongPress() },
                            onLongClick = { onLongPress() })
                        .background(Color.Red)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null
                    )

                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .combinedClickable(onClick = { onClick() }, onLongClick = { onLongPress() })

                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Icon(
                        imageVector = if (file.isDirectory) Icons.Default.Email else Icons.Default.Info,
                        contentDescription = null
                    )
                    Text(text = file.fileName)
                }
            }

        }
    }

}

@Composable
@Preview(showBackground = true)
fun FileGradeItemPreview() {
    val file = FileModel(
        fileName = "directory",
        isFile = false,
        isDirectory = true,
        path = "sgahdsg/asd/qwe",
        absolutePath = "sgahdsg/asd/qwe",

        )
    val file2 =
        FileModel(
            fileName = "file",
            isFile = true,
            isDirectory = false,
            path = "sgahdsg/asd/qwe",
            absolutePath = "sgahdsg/asd/qwe",
        )
    FileManagerTheme {
        Column {
            FileGradeItem(file, {}) {}
            FileGradeItem(file2, {}) {}
        }
    }
}

@Composable
@Preview(showBackground = true)
fun FileItemPreview() {
    val file = FileModel(
        fileName = "directory",
        isFile = false,
        isDirectory = true,
        path = "sgahdsg/asd/qwe", absolutePath = "sgahdsg/asd/qwe",
    )
    val file2 =
        FileModel(
            fileName = "file",
            isFile = true,
            isDirectory = false,
            path = "sgahdsg/asd/qwe",
            absolutePath = "sgahdsg/asd/qwe",
        )

    FileManagerTheme {
        Column {
            FileItem(file) {}
            FileItem(file2) {}
        }
    }
}

@Composable
@Preview
fun PathComponentPreview() {
    val path = "T/B/A/C"
    FileManagerTheme {
        PathComponent(path) {}
    }
}

@Composable
@Preview
fun DialogFolderPreview() {
    val path = "T/B/A/C"
    FileManagerTheme {
        DialogFolder({}, {})
    }
}
