package presentation.detail.model

data class AnalysisData(
    val totalAmount: Double,
    val averagePerMonth: Double,
    val monthlyData: List<MonthlyAnalysisItem>
)

data class MonthlyAnalysisItem(
    val monthLabel: String, // Format: "MM/yyyy" hoặc "MM"
    val amount: Double
)

