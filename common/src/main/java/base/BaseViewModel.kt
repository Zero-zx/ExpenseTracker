package base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<T> : ViewModel() {
    private val _uiState = MutableStateFlow<UIState<T>>(UIState.Idle)
    val uiState: StateFlow<UIState<T>> = _uiState.asStateFlow()

    private fun setState(state: UIState<T>) {
        _uiState.value = state
    }

    protected fun setLoading() = setState(UIState.Loading)
    protected fun setSuccess(data: T) = setState(UIState.Success(data))
    protected fun setError(message: String) = setState(UIState.Error(message))

    fun resetState() {
        _uiState.value = UIState.Idle
    }
}