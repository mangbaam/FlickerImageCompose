package com.mangbaam.flickerimagecompose.ui.main

import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mangbaam.flickerimagecompose.MainViewModel
import com.mangbaam.flickerimagecompose.model.PhotoUIModel
import com.mangbaam.flickerimagecompose.model.SearchDisplay
import com.mangbaam.flickerimagecompose.state.rememberSearchState

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onClick: (PhotoUIModel) -> Unit,
) {
    Column(modifier = modifier.widthIn(min = 100.dp, max = 500.dp)) {
        val context = LocalContext.current
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current

        val searchHistory = viewModel.searchHistory.collectAsStateWithLifecycle()
        val photoList = viewModel.photoList.collectAsStateWithLifecycle()

        val state = rememberSearchState(
            searchHistory = searchHistory.value,
            searchResult = photoList.value,
        )

        val dispatcher: OnBackPressedDispatcher =
            LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

        val backCallback = remember {
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!state.focused) {
                        isEnabled = false
                        Toast.makeText(context, "Back", Toast.LENGTH_SHORT).show()
                        dispatcher.onBackPressed()
                    } else {
                        state.query = TextFieldValue("")
                        state.focused = false
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                }
            }
        }

        DisposableEffect(dispatcher) { // dispose/relaunch if dispatcher changes
            dispatcher.addCallback(backCallback)
            onDispose {
                backCallback.remove() // avoid leaks!
            }
        }

        SearchBar(
            query = state.query,
            onQueryChange = { state.query = it },
            onSearch = { viewModel.search(it) },
            onSearchFocusChange = { state.focused = it },
            onClearQuery = { state.query = TextFieldValue("") },
            onBack = { state.query = TextFieldValue("") },
            searching = state.searching,
            focused = state.focused,
            modifier = modifier,
        )

        when (state.searchDisplay) {
            SearchDisplay.Initial -> {
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "???????????? ??? ????????? ????????????!",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            SearchDisplay.NoResults -> {
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("??? ?????? ????????? ????????????!", fontSize = 24.sp, color = Color(0xffDD2C00))
                }
            }

            SearchDisplay.SearchHistory -> {
                SearchHistory(histories = state.searchHistory) {
                    var text = state.query.text
                    if (text.isEmpty()) text = it else text += " $it"
                    text.trim()
                    state.query = TextFieldValue(text, TextRange(text.length))
                }
            }

            SearchDisplay.Results -> {
                Log.d("??????", "MainScreen: Result mode")
                PhotoListContent(
                    modifier = modifier,
                    photoList = state.searchResult,
                    onClick = onClick,
                    endOfList = { viewModel.search(query = state.query.text) },
                )
            }

            SearchDisplay.SearchInProgress -> {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxSize(),
                )
            }
        }
    }
}
