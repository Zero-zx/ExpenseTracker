package com.example.home.home

import androidx.lifecycle.ViewModel
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import navigation.Navigator
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val navigator: Navigator
) : BaseViewModel<Any>() {
    // TODO: Implement the ViewModel

    fun navigateToEventList() {
        navigator.navigateToEventList()
    }

    fun navigateToTransaction() {
        navigator.navigateToTransaction()
    }
}