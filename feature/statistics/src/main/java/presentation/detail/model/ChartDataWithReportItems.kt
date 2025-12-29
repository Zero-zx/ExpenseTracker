package presentation.detail.model

data class ChartDataWithReportItems(
    val chartData: ChartData,
    val reportItems: List<ReportItem>
) {
    data class ChartData(
        val labels: List<String>,
        val data: List<IncomeExpenseData>
    )

    data class IncomeExpenseData(
        val income: Double,
        val expense: Double
    )
}



