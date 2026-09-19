package com.golash.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golash.app.R
import com.golash.app.domain.model.Product
import com.golash.app.data.repository.product.MockProductRepository
import com.golash.app.domain.repository.ProductRepository
import com.golash.app.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val products: List<Product>) : HomeUiState()
    data class Error(val message: UiText) : HomeUiState()
}


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                _uiState.value = HomeUiState.Loading
                val products = repository.getProducts()
                _uiState.value = HomeUiState.Success(products)
            } catch (e: Exception) {
                val errorMessage = e.localizedMessage?.let {
                    UiText.DynamicString(it)
                }  ?: UiText.StringResource(R.string.unknown_error)
                _uiState.value = HomeUiState.Error(errorMessage)
            }
        }
    }

    fun refresh() {
        loadProducts()
    }
}