package presentation.detail.model

data class FinancialStatementData(
    val totalAmount: Double, // Total assets
    val totalLiabilities: Double,
    val assets: List<AssetItem>,
    val liabilities: List<LiabilityItem>
) {
    val netWorth: Double
        get() = totalAmount - totalLiabilities
}

data class AssetItem(
    val id: Long,
    val name: String,
    val amount: Double,
    val iconRes: Int? = null
)

data class LiabilityItem(
    val id: Long,
    val name: String,
    val amount: Double,
    val iconRes: Int? = null
)

