package com.acv.mvp.ui.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acv.mvp.presentation.*
import com.acv.mvp.ui.compose.theme.MvpTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val store: FormStore = viewModel<FormStore>("saf", ScoreViewModelFactory(Repository()))
            store.action(LoadForm)
            MvpTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val state by store.state.collectAsState()
                    if (state.isLoading)
                        Text("Loading")
                    else
                        FormScreen(
                            state.name,
                            { store.action(ChangeName(it)) },
                            state.phone,
                            { store.action(ChangePhone(it)) },
                            state.mail,
                            { store.action(ChangeMail(it)) },
                        )
                }
            }
        }
    }

    class ScoreViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FormStore::class.java)) {
                return FormStore(repository = repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

@Composable
fun FormScreen(
    name: String,
    onNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    mail: String,
    onMailChange: (String) -> Unit,
) {
    Column {
        TextField(
            value = name,
            onValueChange = { onNameChange(it) },
        )
        TextField(
            value = phone,
            onValueChange = { onPhoneChange(it) },
        )
        TextField(
            value = mail,
            onValueChange = { onMailChange(it) },
        )
    }
}

@Composable
fun Greeting(name: String) {
    var text by remember { mutableStateOf("") }
    TextField(
        value = text,
        onValueChange = { text = it }
    )
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MvpTheme {
        Greeting("Android")
    }
}