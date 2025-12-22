# RoundedCombinedBarChart Y-Axis Scaling Fix

## Issue
The bars in `RoundedCombinedBarChart` were not displaying at their full height (100% of combined expense + income values) because the Y-axis wasn't properly scaled to accommodate the sum of both datasets.

## Root Cause
When using `RoundedCombinedBarChart` with two datasets:
- Dataset 0 (Expense): Bottom portion of the bar
- Dataset 1 (Income): Top portion of the bar

The chart renderer combines these vertically, so the **total bar height = expense + income**.

However, if the Y-axis maximum is not configured, MPAndroidChart might only scale based on the individual dataset maxima, not their sum, causing bars to be cut off or not reach their full proportional height.

## Solution
Calculate the maximum combined value (expense + income) across all data points and explicitly set the Y-axis maximum:

```kotlin
var maxTotal = 0.0

chartData.monthlyData.forEachIndexed { index, data ->
    expenseEntries.add(BarEntry(index.toFloat(), data.expense.toFloat()))
    incomeEntries.add(BarEntry(index.toFloat(), data.income.toFloat()))
    
    // Track the maximum combined value for Y-axis scaling
    val total = data.expense + data.income
    if (total > maxTotal) maxTotal = total
}

// Configure Y-axis with proper maximum
val leftAxis = barChart.axisLeft
leftAxis.axisMinimum = 0f
leftAxis.axisMaximum = (maxTotal * 1.1).toFloat() // Add 10% padding at top
```

## Why This Works

### Before Fix:
```
Y-axis Max: Auto-calculated based on individual datasets
Result: Bars may be clipped or not fill the full height

Example:
- Month 1: Expense=300k, Income=700k, Total=1000k
- Month 2: Expense=400k, Income=800k, Total=1200k
- Auto Y-Max might be: 800k (max of any single dataset)
- But actual max needed: 1200k (sum of both)
- Result: Month 2 bar is clipped at 800k, doesn't show full 1200k
```

### After Fix:
```
Y-axis Max: Explicitly set to (max combined total * 1.1)
Result: All bars scale properly to show full combined height

Example:
- Month 1: Total=1000k
- Month 2: Total=1200k
- Y-Max set to: 1320k (1200k * 1.1)
- Result: Both bars display at correct proportional heights
```

## Visual Impact

### Before:
```
Y=800k  ┤
        │  ╔═══╗ <- Bar cut off!
        │  ║░░░║
Y=400k  │  ║███║ ╔═══╗
        │  ║███║ ║░░░║
Y=0     └──╚═══╝─╚═══╝──
         Month1  Month2
```

### After:
```
Y=1320k ┤
        │        ╔═══╗
        │        ║░░░║
Y=800k  │  ╔═══╗ ║░░░║
        │  ║░░░║ ║░░░║
Y=400k  │  ║███║ ║███║
        │  ║███║ ║███║
Y=0     └──╚═══╝─╚═══╝──
         Month1  Month2
```

Each bar now shows:
- Full height proportional to its total (expense + income)
- Proper ratio between bottom (expense) and top (income)
- Correct relative heights between different months

## Implementation Details

### Key Changes in `setupBarChart()`:

1. **Track Maximum Total**:
```kotlin
var maxTotal = 0.0

chartData.monthlyData.forEachIndexed { index, data ->
    // ... create entries ...
    val total = data.expense + data.income
    if (total > maxTotal) maxTotal = total
}
```

2. **Set Y-Axis Maximum**:
```kotlin
leftAxis.axisMaximum = (maxTotal * 1.1).toFloat() // 10% padding
```

3. **Why 10% Padding?**:
- Prevents tallest bar from touching the top edge
- Provides visual breathing room
- Standard practice for chart displays

## Testing Verification

To verify the fix works correctly:

1. **Check Bar Heights**: Each bar should reach a height proportional to its total value
2. **Check Ratios**: Within each bar, the expense:income ratio should be visually correct
3. **Check Scaling**: Different months with different totals should have different bar heights
4. **Check Clipping**: No bars should be cut off at the top

### Example Test Data:
```kotlin
Month 1: Expense=100k, Income=200k → Total=300k → Bar height = 25% of max
Month 2: Expense=300k, Income=500k → Total=800k → Bar height = 67% of max
Month 3: Expense=400k, Income=800k → Total=1200k → Bar height = 100% of max
```

## Benefits

✅ Bars display at correct proportional heights  
✅ Full range of data is visible  
✅ Chart auto-scales properly  
✅ No visual clipping or cutoff  
✅ Professional, accurate data representation  

## Related Code

- **File**: `ReportsFragment.kt`
- **Method**: `setupBarChart()`
- **Lines**: Y-axis configuration section
- **Chart Type**: `RoundedCombinedBarChart`

## Notes

- This fix is specific to combined charts where multiple datasets are stacked vertically
- Single dataset charts don't need this fix (auto-scaling works fine)
- The 1.1 multiplier (10% padding) can be adjusted based on visual preference
- Consider using `setFitBars(true)` if you want the chart to auto-calculate X-axis spacing

