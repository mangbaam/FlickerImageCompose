package com.mangbaam.flickerimagecompose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mangbaam.flickerimagecompose.datasource.FlickerRemoteDataSource
import com.mangbaam.flickerimagecompose.network.FlickerService
import com.mangbaam.flickerimagecompose.repository.FlickerRepositoryImpl
import com.mangbaam.flickerimagecompose.ui.main.MainScreen
import com.mangbaam.flickerimagecompose.ui.theme.FlickerImageComposeTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scrollBehavior =
                TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

            val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)
            val viewModel: MainViewModel = viewModel(viewModelStoreOwner) {
                val flickerService = FlickerService.flickerService
                val dataSource = FlickerRemoteDataSource(flickerService)
                val repository = FlickerRepositoryImpl(dataSource)
                MainViewModel(repository)
            }
            val errorMessage = viewModel.errorMessage.collectAsStateWithLifecycle()

            FlickerImageComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Scaffold(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    ) { paddingValues ->
                        MainScreen(
                            modifier = Modifier.padding(
                                bottom = paddingValues.calculateBottomPadding(),
                                top = paddingValues.calculateTopPadding(),
                            ),
                            viewModel = viewModel,
                        ) { photo ->
                            // TODO Download Image
                            Toast.makeText(
                                this,
                                "Download ${photo.loadUrlOriginal}",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                        if (errorMessage.value.isNotBlank()) {
                            Snackbar {
                                Text(text = errorMessage.value)
                            }
                        }
                    }
                }
            }
        }
    }
}
