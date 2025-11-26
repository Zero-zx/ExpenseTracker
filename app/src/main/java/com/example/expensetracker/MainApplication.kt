package com.example.expensetracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import domain.usecase.InitializeCategoriesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {
    @Inject
    lateinit var initializeCategoriesUseCase: InitializeCategoriesUseCase
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        // Initialize predefined categories ONCE for ALL accounts
        applicationScope.launch {
            try {
                // This will check if categories exist
                // If empty -> insert predefined categories
                // If exist -> do nothing
                initializeCategoriesUseCase()
            } catch (e: Exception) {
                // Log error
                e.printStackTrace()
            }
        }
    }
}