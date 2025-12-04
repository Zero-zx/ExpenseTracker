package presentation

import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import transaction.model.Transaction
import usecase.GetTransactionsByDateRangeUseCase
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getTransactionsByDateRangeUseCase: GetTransactionsByDateRangeUseCase
) : BaseViewModel<List<Transaction>>() {

}

