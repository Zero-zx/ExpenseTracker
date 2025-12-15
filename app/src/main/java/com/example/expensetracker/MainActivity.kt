package com.example.expensetracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
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

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CalculatorProvider {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var navigator: Navigator
    private val destinationWithoutBottomBar = setOf(
        R.id.eventSelectFragment,
        R.id.categorySelectFragment,
        R.id.accountSelectFragment,
        R.id.payeeSelectFragment,
        R.id.locationSelectFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController

        // Set NavController to Navigator
        (navigator as NavigatorImpl).setNavController(navController)
        binding.bottomNavigationView.setupWithNavController(navController)

        setUpHideShowBottomBar(navController)
    }

    private fun setUpHideShowBottomBar(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destinationWithoutBottomBar.contains(destination.id)) {
                binding.bottomNavigationView.gone()
            } else {
                binding.bottomNavigationView.visible()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navigator.navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * Implementation of CalculatorProvider interface
     * Provides calculator view to feature modules
     */
    override fun getCalculatorView(): CalculatorView {
        return binding.calculatorView
    }
}