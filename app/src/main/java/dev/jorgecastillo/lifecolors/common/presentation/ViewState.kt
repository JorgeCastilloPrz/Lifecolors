package dev.jorgecastillo.lifecolors.common.presentation

sealed class ViewState<out T> {
    object Loading : ViewState<Nothing>()
    data class Content<T>(val t: T) : ViewState<T>()
    object Error : ViewState<Nothing>()
}
