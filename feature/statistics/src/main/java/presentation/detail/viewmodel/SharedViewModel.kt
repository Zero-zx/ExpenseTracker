package presentation.detail.viewmodel

import account.usecase.GetAccountsUseCase
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase
) : BaseViewModel<Any>() {

    private val _selectedIds = MutableStateFlow<List<Long>>(emptyList())
    val selectedIds: StateFlow<List<Long>> = _selectedIds

    fun updateSelectedIds(ids: List<Long>) {
        _selectedIds.value = ids
    }
}
