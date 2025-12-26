package com.example.expensetracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.expensetracker.databinding.ActivityMainBinding
import com.example.expensetracker.navigation.NavigatorImpl
import dagger.hilt.android.AndroidEntryPoint
import navigation.Navigator
import ui.CalculatorProvider
import ui.CalculatorView
import ui.gone
import ui.visible
import javax.inject.Inject
import com.example.common.R as CommonR
import com.example.expensetracker.R as MainR

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CalculatorProvider {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var navigator: Navigator

    private var backPressedTime: Long = 0
    private val backPressInterval: Long = 2000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupBackPress()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(MainR.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController
        (navigator as NavigatorImpl).setNavController(navController)

        // Setup bottom navigation
        binding.bottomNavigationView.setupWithNavController(navController)

        // Override default behavior to clear back stack and save state
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId != binding.bottomNavigationView.selectedItemId) {
                val options = NavOptions.Builder()
                    .setPopUpTo(navController.graph.startDestinationId,
                        inclusive = false,
                        saveState = true
                    )
                    .setLaunchSingleTop(true)
                    .setRestoreState(true)
                    .setEnterAnim(android.R.anim.fade_in)
                    .build()
                navController.navigate(item.itemId, null, options)
            }
            true
        }

        // Hide bottom bar for specific fragments
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideBottomBar = destination.label?.toString()?.contains("Select") ?: false

            if (hideBottomBar) {
                binding.bottomNavigationView.gone()
            } else {
                binding.bottomNavigationView.visible()
            }
        }
    }

    private fun setupBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val canPopBack = navController.popBackStack()

                if (!canPopBack) {
                    // On root destination - check for double back press
                    val currentTime = System.currentTimeMillis()

                    if (currentTime - backPressedTime < backPressInterval) {
                        finish()
                    } else {
                        backPressedTime = currentTime
                        Toast.makeText(
                            this@MainActivity,
                            CommonR.string.text_press_back_again_to_exit,
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    if (!navController.popBackStack()) {
                        finish()
                    }
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return navigator.navigateUp() || super.onSupportNavigateUp()
    }

    override fun getCalculatorView(): CalculatorView {
        return binding.calculatorView
    }

    override fun onBackPressed() {
        navigator.navigateUp()
    }
}