package dev.jorgecastillo.lifecolors.common.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.jorgecastillo.lifecolors.dashboard.presentation.DashboardViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * ViewModelFactory which uses Dagger to create the instances.
 */
@ExperimentalCoroutinesApi
object DashboardViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = DashboardViewModel() as T
}
