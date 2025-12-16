package com.example.expensetracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import account.usecase.InitializeAdminUseCase
import session.usecase.InitializeSessionUseCase
import transaction.usecase.InitializeCategoriesUseCase
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
    @Inject
    lateinit var initializeSessionUseCase: InitializeSessionUseCase

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
                // Initialize user and admin account
                initializeAdminUseCase()
                // Then initialize session with first account
                initializeSessionUseCase()
            } catch (e: Exception) {
                // Log error
                e.printStackTrace()
            }
        }
    }
}