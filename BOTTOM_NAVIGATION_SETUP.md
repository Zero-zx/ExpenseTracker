# Bottom Navigation Setup Guide - Simplified

## Overview
This is a simple implementation of bottom navigation in the ExpenseTracker app. Each bottom navigation item shows its own navigation graph, and pressing back from any root screen exits the app.

## Key Components

### 1. Navigation Graph Structure

```xml
<!-- nav_graph.xml -->
<navigation 
    android:id="@+id/nav_graph"
    app:startDestination="@id/home_nav_graph">
    
    <include app:graph="@navigation/home_nav_graph" />
    <include app:graph="@navigation/account_nav_graph" />
    <include app:graph="@navigation/transaction_nav_graph" />
    <include app:graph="@navigation/statistics_nav_graph" />
</navigation>
```

**Important**: Start destination is a **graph** (not a fragment).

### 2. Bottom Navigation Menu

```xml
<!-- bottom_nav_menu.xml -->
<menu>
    <item android:id="@+id/home_nav_graph" ... />
    <item android:id="@+id/account_nav_graph" ... />
    <item android:id="@+id/transaction_nav_graph" ... />
    <item android:id="@+id/statistics_nav_graph" ... />
</menu>
```

Menu item IDs match the navigation graph IDs.

## Implementation

### Setup Navigation (MainActivity)

```kotlin
private fun setupNavigation() {
    navController = navHostFragment.navController
    
    // Use default setup
    binding.bottomNavigationView.setupWithNavController(navController)
    
    // Override to add proper back stack behavior
    binding.bottomNavigationView.setOnItemSelectedListener { item ->
        if (item.itemId != binding.bottomNavigationView.selectedItemId) {
            val options = NavOptions.Builder()
                .setPopUpTo(navController.graph.startDestinationId, false, true)
                .setLaunchSingleTop(true)
                .setRestoreState(true)
                .build()
            navController.navigate(item.itemId, null, options)
        }
        true
    }
}
```

**What each option does:**
- `setPopUpTo(startDestination, false, true)` - Clear back stack to start, save state
- `setLaunchSingleTop(true)` - Don't create duplicates
- `setRestoreState(true)` - Restore state when returning to a graph

### Back Button Behavior

```kotlin
private fun setupBackPress() {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val isStartDestination = navController.currentDestination?.id == 
                navController.currentDestination?.parent?.startDestinationId
            
            if (isStartDestination) {
                finish() // Exit app
            } else if (!navController.popBackStack()) {
                finish() // Nothing to pop, exit app
            }
        }
    })
}
```

Simple logic: On start destination → exit. Otherwise → go back.

### Hide Bottom Bar (Optional)

```kotlin
navController.addOnDestinationChangedListener { _, destination, _ ->
    val hideBottomBar = destination.label?.toString()?.contains("Select") ?: false
    
    if (hideBottomBar) {
        binding.bottomNavigationView.gone()
    } else {
        binding.bottomNavigationView.visible()
    }
}
```

Hides bottom nav for any fragment with "Select" in its label.

## How It Works

1. **Click bottom nav item** → Navigate to that graph's start destination
2. **Click same item** → Do nothing (avoid re-navigation)
3. **Navigate within graph** → Normal navigation
4. **Back on root screen** → Exit app
5. **Back on nested screen** → Go back in current graph
6. **Switch graphs** → State is saved and restored

## That's It!

Just 3 simple methods:
- `setupNavigation()` - Configure bottom nav
- `setupBackPress()` - Handle back button
- Destination listener (optional) - Hide/show bottom bar

No complex state management, no manual synchronization. The Navigation Component handles everything.


