package presentation.detail.viewmodel

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import presentation.detail.model.AssetItem
import presentation.detail.model.FinancialStatementData
import presentation.detail.model.LiabilityItem
import account.usecase.GetAccountsUseCase
import account.model.Account
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class FinancialStatementViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase
) : BaseViewModel<FinancialStatementData>() {

    init {
        loadFinancialStatement()
    }

    private fun loadFinancialStatement() {
        setLoading()

        getAccountsUseCase()
            .onEach { accounts ->
                val financialStatementData = processAccounts(accounts)
                setSuccess(financialStatementData)
            }
            .catch { exception ->
                setError(exception.message ?: "Unknown error")
            }
            .launchIn(viewModelScope)
    }

    private fun processAccounts(accounts: List<Account>): FinancialStatementData {
        val (assets, liabilities) = accounts.partition { it.balance >= 0 }

        val assetItems = assets.map { account ->
            AssetItem(
                id = account.id,
                name = account.username,
                amount = account.balance,
                iconRes = account.type.iconRes
            )
        }

        val liabilityItems = liabilities.map { account ->
            LiabilityItem(
                id = account.id,
                name = account.username,
                amount = abs(account.balance), // Store as positive for display
                iconRes = account.type.iconRes
            )
        }

        val totalAmount = assetItems.sumOf { it.amount }
        val totalLiabilities = liabilityItems.sumOf { it.amount }

        return FinancialStatementData(
            totalAmount = totalAmount,
            totalLiabilities = totalLiabilities,
            assets = assetItems,
            liabilities = liabilityItems
        )
    }
}
