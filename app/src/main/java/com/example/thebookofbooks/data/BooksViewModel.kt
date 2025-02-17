package com.example.thebookofbooks.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.network.HttpException
import com.example.thebookofbooks.BooksApplication
import com.example.thebookofbooks.model.BookDetailsItem
import com.example.thebookofbooks.model.Item
import com.example.thebookofbooks.ui.DetailsScreenUiState
import com.example.thebookofbooks.ui.ResultScreenUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException

class BooksViewModel(
    private val booksRepository: BooksRepository
) : ViewModel() {
    var query by mutableStateOf("")
    var bookId by mutableStateOf("")

    private val _resultScreenUiState = MutableStateFlow<ResultScreenUiState>(ResultScreenUiState.Loading())
    val resultScreenUiState = _resultScreenUiState.asStateFlow()

    private val _detailsScreenUiState = MutableStateFlow<DetailsScreenUiState>(DetailsScreenUiState.Loading())
    val detailsScreenUiState = _detailsScreenUiState.asStateFlow()

    // function to update query term
    fun updateQuery (newQuery : String) {
        query = newQuery
    }

    private var searchJob: Job? = null
    fun fetchResponse () {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _resultScreenUiState.value = ResultScreenUiState.Loading()
            try {
                delay(500)
                val response = booksRepository.getBooksList(query)
                _resultScreenUiState.value = if (response.items.isNullOrEmpty()) {
                    ResultScreenUiState.Empty()
                } else {
                    ResultScreenUiState.Success(response)
                }
            } catch (e : HttpException) {
                _resultScreenUiState.value = ResultScreenUiState.Error(e.message)
            } catch (e : IOException) {
                _resultScreenUiState.value = ResultScreenUiState.Error(e.message)
            } catch (e : Exception) {
                _resultScreenUiState.value = ResultScreenUiState.Error(e.message)
            }
        }
    }

    fun fetchBookDetails (id : String) {
        viewModelScope.launch {
            _detailsScreenUiState.value = DetailsScreenUiState.Loading()
            try {
                val bookDetails : BookDetailsItem = booksRepository.getBookDetails(id)
                _detailsScreenUiState.value = DetailsScreenUiState.Success(bookDetails)
            } catch (e : HttpException) {
                _detailsScreenUiState.value = DetailsScreenUiState.Error(e.message)
            } catch (e : IOException) {
                _detailsScreenUiState.value = DetailsScreenUiState.Error(e.message)
            } catch (e : Exception) {
                _detailsScreenUiState.value = DetailsScreenUiState.Error(e.message)
            }
        }
    }

    fun onImageClicked () {
        fetchBookDetails(bookId)
    }

    fun updateBookId(newBookId : String) {
        bookId = newBookId
    }


    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BooksApplication)
                val booksRepository = application.container.booksRepository
                BooksViewModel(booksRepository = booksRepository)
            }
        }
    }

}