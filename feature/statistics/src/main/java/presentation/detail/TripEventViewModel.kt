package presentation.detail

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import presentation.detail.model.TripEventData
import transaction.model.CategoryType
import transaction.model.Event
import transaction.model.Transaction
import transaction.usecase.GetEventsByAccountUseCase
import transaction.usecase.GetTransactionsByDateRangeUseCase
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TripEventViewModel @Inject constructor(
    private val getEventsByAccountUseCase: GetEventsByAccountUseCase,
    private val getTransactionsByDateRangeUseCase: GetTransactionsByDateRangeUseCase
) : BaseViewModel<List<TripEventData>>() {

    companion object {
        private const val ACCOUNT_ID = 1L
    }

    fun loadTripEvents(isActive: Boolean) {
        setLoading()

        // Load all events and transactions
        combine(
            getEventsByAccountUseCase(ACCOUNT_ID),
            getAllTransactions(ACCOUNT_ID)
        ) { events, transactions ->
            // Filter events by active status
            val filteredEvents = events.filter { it.isActive == isActive }
            
            // Calculate total expense for each event
            val tripEventData = filteredEvents.map { event ->
                val eventTransactions = transactions.filter { transaction ->
                    transaction.event?.id == event.id
                }
                
                // Calculate total expense (only EXPENSE and BORROWING)
                val totalExpense = eventTransactions.sumOf { transaction ->
                    when (transaction.category.type) {
                        CategoryType.EXPENSE, CategoryType.BORROWING -> transaction.amount
                        else -> 0.0
                    }
                }
                
                TripEventData(
                    event = event,
                    totalAmount = totalExpense
                )
            }
            
            tripEventData
        }
        .catch { exception ->
            setError(exception.message ?: "Unknown error")
        }
        .onEach { tripEventData ->
            setSuccess(tripEventData)
        }
        .launchIn(viewModelScope)
    }

    private fun getAllTransactions(accountId: Long): kotlinx.coroutines.flow.Flow<List<Transaction>> {
        // Get transactions from a wide date range (last 2 years)
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis
        
        calendar.add(Calendar.YEAR, -2)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis
        
        return getTransactionsByDateRangeUseCase(startDate, endDate)
    }
}


