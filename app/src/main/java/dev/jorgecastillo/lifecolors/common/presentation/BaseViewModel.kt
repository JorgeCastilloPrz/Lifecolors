package dev.jorgecastillo.lifecolors.common.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dev.jorgecastillo.lifecolors.common.view.NonNullMutableLiveData

abstract class BaseViewModel<Content> : ViewModel() {

    private val _viewState = NonNullMutableLiveData<ViewState<Content>>(ViewState.Loading)
    val viewState: LiveData<ViewState<Content>> = _viewState

    protected fun updateViewState(transform: (ViewState<Content>) -> ViewState<Content>) {
        _viewState.postValue(transform(_viewState.value))
    }
}
