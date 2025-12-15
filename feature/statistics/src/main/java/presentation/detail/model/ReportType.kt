package presentation.detail.model

import java.io.Serializable

enum class ReportType : Serializable {
    FINANCIAL_STATEMENT,
    EXPENSE_VS_INCOME,
    EXPENSE_ANALYSIS,
    INCOME_ANALYSIS,
    MONEY_LENT_BORROWED,
    PAYEE_PAYER,
    TRIP_EVENT,
    FINANCIAL_ANALYSIS;

    fun getDisplayName(): String {
        return when (this) {
            FINANCIAL_STATEMENT -> "Financial statement"
            EXPENSE_VS_INCOME -> "Expense vs Income"
            EXPENSE_ANALYSIS -> "Expense analysis"
            INCOME_ANALYSIS -> "Income analysis"
            MONEY_LENT_BORROWED -> "Money lent/borrowed"
            PAYEE_PAYER -> "Payee/Payer"
            TRIP_EVENT -> "Trip, event"
            FINANCIAL_ANALYSIS -> "Financial Analysis"
        }
    }

    companion object {
        /**
         * Safe parsing của enum value, trả về null nếu không hợp lệ
         */
        fun valueOfOrNull(value: String): ReportType? {
            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}

