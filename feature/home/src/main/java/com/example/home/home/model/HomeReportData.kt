package com.example.home.home.model

import com.example.home.home.usecase.CategoryExpenseData

data class HomeReportData(
    val income: Double,
    val expense: Double,
    val difference: Double,
    val topCategories: List<CategoryExpenseData>,
    val hasData: Boolean
)