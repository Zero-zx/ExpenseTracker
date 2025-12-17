# CustomAlertDialog - Reusable Alert Dialog Component

## Overview
`CustomAlertDialog` is a fully customizable, reusable alert dialog component located in the `common` module. It provides a consistent and beautiful dialog experience across all modules of the application.

## Features
- ✅ Fully customizable title, message, and icon
- ✅ Support for positive and negative buttons
- ✅ Optional close button
- ✅ Customizable colors for all components
- ✅ Adjustable text sizes
- ✅ Icon tinting support
- ✅ Cancelable/non-cancelable options
- ✅ Builder pattern for easy construction
- ✅ Material Design 3 styling
- ✅ Rounded corners and elevation
- ✅ Responsive layout

## Basic Usage

### Simple Alert
```kotlin
CustomAlertDialog.Builder(context)
    .setTitle("Alert")
    .setMessage("This is a basic alert message")
    .setPositiveButton("OK") { dialog ->
        dialog.dismiss()
    }
    .show()
```

### Confirmation Dialog (Yes/No)
```kotlin
CustomAlertDialog.Builder(context)
    .setTitle("Attention!")
    .setMessage("Are you sure you want to proceed?")
    .setIcon(R.drawable.ic_warning)
    .setPositiveButton("Yes") { dialog ->
        // Handle confirmation
        dialog.dismiss()
    }
    .setNegativeButton("No") { dialog ->
        dialog.dismiss()
    }
    .show()
```

## Customization Options

### Title Customization
```kotlin
.setTitle("My Title")                              // Set title text
.setTitleColor(Color.BLUE)                         // Set title color (ColorInt)
.setTitleColorRes(R.color.blue_primary)            // Set title color (Resource)
.setTitleSize(22f)                                 // Set title text size (SP)
```

### Message Customization
```kotlin
.setMessage("My message")                          // Set message text
.setMessageColor(Color.GRAY)                       // Set message color (ColorInt)
.setMessageColorRes(R.color.gray_text)             // Set message color (Resource)
.setMessageSize(16f)                               // Set message text size (SP)
```

### Icon Customization
```kotlin
.setIcon(R.drawable.ic_warning)                    // Set icon from drawable resource
.setIcon(myDrawable)                               // Set icon from Drawable object
.setIconTint(Color.RED)                            // Set icon tint (ColorInt)
.setIconTintRes(R.color.red_expense)               // Set icon tint (Resource)
```

### Button Customization
```kotlin
// Positive Button
.setPositiveButton("OK") { dialog ->               // Set text and click listener
    dialog.dismiss()
}
.setPositiveButtonTextColor(Color.WHITE)           // Set text color
.setPositiveButtonBackgroundColor(Color.BLUE)      // Set background color

// Negative Button
.setNegativeButton("Cancel") { dialog ->           // Set text and click listener
    dialog.dismiss()
}
.setNegativeButtonTextColor(Color.GRAY)            // Set text color
.setNegativeButtonBackgroundColor(Color.WHITE)     // Set background color
```

### Close Button Customization
```kotlin
.setShowCloseButton(true)                          // Show/hide close button
.setOnCloseClickListener { dialog ->               // Custom close action
    // Handle close button click
    dialog.dismiss()
}
```

### Dialog Behavior
```kotlin
.setCancelable(false)                              // Prevent back button dismissal
.setCanceledOnTouchOutside(false)                  // Prevent touch outside dismissal
```

## Complete Example

### Financial Warning Dialog (Matching Screenshot)
```kotlin
CustomAlertDialog.Builder(context)
    .setTitle("Attention!")
    .setMessage("These records will not be listed in any reports (except the Financial Statement report). Are you sure?")
    .setIcon(R.drawable.ic_warning)
    .setPositiveButton("Yes") { dialog ->
        // Handle confirmation
        saveTransaction()
        dialog.dismiss()
    }
    .setNegativeButton("No") { dialog ->
        dialog.dismiss()
    }
    .setCancelable(true)
    .setCanceledOnTouchOutside(true)
    .show()
```

### Delete Confirmation Dialog
```kotlin
CustomAlertDialog.Builder(context)
    .setTitle("Delete Item")
    .setMessage("Are you sure you want to delete this item? This action cannot be undone.")
    .setIcon(R.drawable.ic_warning)
    .setIconTintRes(R.color.red_expense)
    .setTitleColorRes(R.color.red_expense)
    .setPositiveButton("Delete") { dialog ->
        deleteItem()
        dialog.dismiss()
    }
    .setPositiveButtonBackgroundColor(Color.parseColor("#EF4444"))
    .setNegativeButton("Cancel") { dialog ->
        dialog.dismiss()
    }
    .show()
```

### Success Dialog
```kotlin
CustomAlertDialog.Builder(context)
    .setTitle("Success!")
    .setMessage("Your transaction has been saved successfully.")
    .setIcon(R.drawable.ic_check) // Use success icon
    .setIconTintRes(R.color.green_income)
    .setPositiveButton("OK") { dialog ->
        dialog.dismiss()
    }
    .setShowCloseButton(false)
    .show()
```

## Advanced Usage

### Reusable Dialog Instance
```kotlin
// Create dialog instance
val dialog = CustomAlertDialog.Builder(context)
    .setTitle("Loading")
    .setMessage("Please wait...")
    .setCancelable(false)
    .build()

// Show dialog
dialog.show()

// Later, dismiss dialog
dialog.dismiss()

// Check if showing
if (dialog.isShowing()) {
    dialog.dismiss()
}
```

### Non-Cancelable Progress Dialog
```kotlin
val progressDialog = CustomAlertDialog.Builder(context)
    .setTitle("Processing")
    .setMessage("Please wait while we process your request...")
    .setCancelable(false)
    .setCanceledOnTouchOutside(false)
    .setShowCloseButton(false)
    .build()

progressDialog.show()

// Dismiss after operation completes
viewModel.operation.observe(this) { result ->
    progressDialog.dismiss()
}
```

## Builder Methods Reference

| Method | Description |
|--------|-------------|
| `setTitle(String)` | Set dialog title |
| `setTitleColor(Int)` | Set title color (ColorInt) |
| `setTitleColorRes(Int)` | Set title color from resource |
| `setTitleSize(Float)` | Set title text size in SP |
| `setMessage(String)` | Set dialog message |
| `setMessageColor(Int)` | Set message color (ColorInt) |
| `setMessageColorRes(Int)` | Set message color from resource |
| `setMessageSize(Float)` | Set message text size in SP |
| `setIcon(Drawable)` | Set icon from Drawable |
| `setIcon(Int)` | Set icon from drawable resource |
| `setIconTint(Int)` | Set icon tint color (ColorInt) |
| `setIconTintRes(Int)` | Set icon tint from resource |
| `setPositiveButton(String, Listener)` | Set positive button |
| `setPositiveButtonTextColor(Int)` | Set positive button text color |
| `setPositiveButtonBackgroundColor(Int)` | Set positive button background |
| `setNegativeButton(String, Listener)` | Set negative button |
| `setNegativeButtonTextColor(Int)` | Set negative button text color |
| `setNegativeButtonBackgroundColor(Int)` | Set negative button background |
| `setShowCloseButton(Boolean)` | Show/hide close button |
| `setOnCloseClickListener(Listener)` | Set close button click listener |
| `setCancelable(Boolean)` | Set dialog cancelable state |
| `setCanceledOnTouchOutside(Boolean)` | Set touch outside dismiss |
| `build()` | Build and return dialog instance |
| `show()` | Build and show dialog immediately |

## Files Structure

```
common/
├── src/main/
│   ├── java/ui/
│   │   ├── CustomAlertDialog.kt              # Main dialog class
│   │   └── CustomAlertDialogExamples.kt      # Usage examples
│   └── res/
│       ├── layout/
│       │   └── dialog_custom_alert.xml       # Dialog layout
│       ├── drawable/
│       │   ├── ic_warning.xml                # Warning icon
│       │   └── ic_close.xml                  # Close icon
│       └── values/
│           └── strings.xml                   # Dialog strings
```

## Dependencies
The dialog uses:
- Material Components (already in the project)
- ViewBinding (already in the project)
- AndroidX Core library (already in the project)

No additional dependencies required.

## Notes
- The dialog automatically handles visibility of components (if you don't set a title, it won't show)
- If both buttons are not set, the button container is hidden
- The dialog uses Material Design 3 styling for consistency
- All text sizes are in SP units
- All colors can be set via ColorInt or resource ID

## Migration from Standard AlertDialog

**Before (Standard AlertDialog):**
```kotlin
AlertDialog.Builder(context)
    .setTitle("Alert")
    .setMessage("Message")
    .setPositiveButton("OK") { dialog, _ ->
        dialog.dismiss()
    }
    .show()
```

**After (CustomAlertDialog):**
```kotlin
CustomAlertDialog.Builder(context)
    .setTitle("Alert")
    .setMessage("Message")
    .setPositiveButton("OK") { dialog ->
        dialog.dismiss()
    }
    .show()
```

## License
Part of the Expense Tracker application.

