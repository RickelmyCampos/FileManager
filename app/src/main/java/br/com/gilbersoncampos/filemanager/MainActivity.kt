package br.com.gilbersoncampos.filemanager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import br.com.gilbersoncampos.filemanager.ui.theme.FileManagerTheme
import java.io.File

const val REQUEST_CODE = 1

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FileManagerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {

                        App()
                    }
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                //intent.data = Uri.parse("package:" + applicationContext.packageName)
                startActivity(intent)
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permissão concedida, continuar a operação
            } else {
                Toast.makeText(this, "Permissão necessária para acessar arquivos.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun App() {

    val context = LocalContext.current
    var currentPath by remember { mutableStateOf(Environment.getExternalStorageDirectory().absolutePath) }
    val historic = remember {
        mutableListOf<String>()
    }
    val files = remember(currentPath) { File(currentPath).listFiles()?.toList() ?: emptyList() }




    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "$historic")
        if (historic.size > 0) {
            Button(onClick = {
                currentPath = historic.last()
                historic.removeLast()
            }) {
                Text(text = "Voltar")
            }
        }

        LazyColumn {
            items(files) {
                FileItem(it) {
                    if (it.isDirectory) {
                        historic.add(currentPath)
                        currentPath = it.absolutePath
                    } else {
                        // Adicionar ação para abrir arquivos se necessário
                    }
                }
            }
        }
    }
}

@Composable
fun FileItem(file: File, onClick: () -> Unit) {
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
        Text(text = file.name)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FileManagerTheme {
        // Greeting("Android")
    }
}