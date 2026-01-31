package com.vtempe.ui.state

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Error(val message: String? = null) : UiState<Nothing>
    data class Data<T>(val value: T) : UiState<T>
}

