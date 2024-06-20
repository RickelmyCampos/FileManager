package br.com.gilbersoncampos.filemanager.screen.homescreen

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.gilbersoncampos.filemanager.data.model.FileModel
import br.com.gilbersoncampos.filemanager.ui.theme.FileManagerTheme

@Composable
fun HomeScreen(viewModel: HomScreenViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var isGrade by remember { mutableStateOf(true) }
    Column(modifier = Modifier.fillMaxSize()) {

        PathComponent(uiState.currentPath){
            viewModel.loadFiles(it)
            viewModel.addInStackDirectory(it)
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
        }
        if (isGrade) {
            LazyVerticalGrid(columns = GridCells.Fixed(if (isGrade) 2 else 1)) {
                items(uiState.listFiles) { file ->

                    FileGradeItem(file = file) {
                        if (file.isDirectory) {
                            val path = file.absolutePath
                            viewModel.loadFiles(path)
                            viewModel.addInStackDirectory(path)
                        } else {
                            // Adicionar ação para abrir arquivos se necessário
                        }
                    }


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
fun PathComponent(path: String, onClick: (String) -> Unit) {
    val listDirectory = path.split("/")
    Row {
        val first =listDirectory.size-3

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
            items(listDirectory) {
                 directory->

                    Text(text = directory + ">", modifier = Modifier.clickable {
                        val newPath =  listDirectory.reducePathInToDirectory(directory)
                        onClick(newPath)
                    })

            }
        }
    }
}
fun List<String>.reducePathInToDirectory(directory: String):String{
    val subList = this.subList(0, this.indexOf(directory)+1)
    return   subList.reduce { acc, s -> "$acc/$s" }
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

@Composable
fun FileGradeItem(file: FileModel, onClick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            Modifier
                .size(158.dp)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onClick() }
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
            FileGradeItem(file) {}
            FileGradeItem(file2) {}
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
