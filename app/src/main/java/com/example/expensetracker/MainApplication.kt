package com.example.expensetracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import domain.usecase.InitializeAdminUseCase
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
    @Inject
    lateinit var initializeAdminUseCase: InitializeAdminUseCase

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        applicationScope.launch {
            try {
                initializeCategoriesUseCase()
            } catch (e: Exception) {
                // Log error
                e.printStackTrace()
            }
        }

        applicationScope.launch {
            try {
                initializeAdminUseCase()
            } catch (e: Exception) {
                // Log error
                e.printStackTrace()
            }
        }
    }
}