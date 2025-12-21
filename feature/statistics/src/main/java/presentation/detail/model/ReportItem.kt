package presentation.detail.model

data class ReportItem(
    val typeName: String,
    val income: Double,
    val outcome: Double
) {
    val rest: Double
        get() = income - outcome
}



