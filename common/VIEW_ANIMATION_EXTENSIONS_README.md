# View Animation Extensions

## Overview
Reusable extension functions for common view animations, particularly for chevron rotation in expandable/collapsible UI components.

## Location
`/common/src/main/java/ui/ViewAnimationExtensions.kt`

## Available Functions

### 1. `animateChevronRotation(isExpanded, duration, onAnimationEnd)`
Animate chevron rotation based on expansion state (0° ↔ 90°).

**Parameters:**
- `isExpanded: Boolean` - Current expansion state
- `duration: Long = 200L` - Animation duration in milliseconds
- `onAnimationEnd: (() -> Unit)? = null` - Callback when animation completes

**Usage:**
```kotlin
iconChevron.animateChevronRotation(isExpanded = true) {
    // Animation completed
}
```

---

### 2. `toggleChevronRotation(duration, onAnimationEnd)`
Toggle chevron rotation automatically (detects current state and rotates to opposite).

**Parameters:**
- `duration: Long = 200L` - Animation duration in milliseconds
- `onAnimationEnd: (() -> Unit)? = null` - Callback when animation completes

**Usage:**
```kotlin
iconChevron.toggleChevronRotation {
    toggleExpansion()
}
```

---

### 3. `rotateWithAnimation(targetRotation, duration, onAnimationEnd)`
Rotate view to any specific angle with animation.

**Parameters:**
- `targetRotation: Float` - Target rotation angle in degrees
- `duration: Long = 200L` - Animation duration in milliseconds
- `onAnimationEnd: (() -> Unit)? = null` - Callback when animation completes

**Usage:**
```kotlin
view.rotateWithAnimation(180f, duration = 300) {
    // Animation completed
}
```

---

### 4. `animateChevronRotation(isExpanded, collapsedAngle, expandedAngle, duration, onAnimationEnd)`
Animate chevron rotation with custom angles.

**Parameters:**
- `isExpanded: Boolean` - Current expansion state
- `collapsedAngle: Float = 0f` - Angle when collapsed
- `expandedAngle: Float = 90f` - Angle when expanded
- `duration: Long = 200L` - Animation duration in milliseconds
- `onAnimationEnd: (() -> Unit)? = null` - Callback when animation completes

**Usage:**
```kotlin
// For downward-facing chevrons (0° to 180°)
iconChevron.animateChevronRotation(
    isExpanded = true,
    collapsedAngle = 0f,
    expandedAngle = 180f
)
```

---

### 5. `setChevronRotation(isExpanded, collapsedAngle, expandedAngle)`
Set chevron rotation **without** animation (instant).

**Parameters:**
- `isExpanded: Boolean` - Current expansion state
- `collapsedAngle: Float = 0f` - Angle when collapsed
- `expandedAngle: Float = 90f` - Angle when expanded

**Usage:**
```kotlin
// Set initial rotation without animation
iconChevron.setChevronRotation(isExpanded = true)
```

---

### 6. `animateExpandCollapse(duration, onToggle)` ⭐ Most Used
Combines rotation animation with state toggle callback. **This is the function used in ExpandableCategoryAdapter.**

**Parameters:**
- `duration: Long = 200L` - Animation duration in milliseconds
- `onToggle: () -> Unit` - Callback to update your expansion state

**Usage:**
```kotlin
iconChevron.setOnClickListener {
    iconChevron.animateExpandCollapse {
        toggleExpansion()
    }
}
```

---

## Complete Example: ExpandableCategoryAdapter

### Before (inline animation):
```kotlin
iconChevron.rotation = if (isExpanded) 90f else 0f
iconChevron.setOnClickListener {
    val targetRotation = if (iconChevron.rotation == 0f) 90f else 0f
    iconChevron.animate()
        .rotation(targetRotation)
        .setDuration(200)
        .start()
    onToggle()
}
```

### After (using extension):
```kotlin
import ui.animateExpandCollapse
import ui.setChevronRotation

// Set initial rotation (no animation)
iconChevron.setChevronRotation(isExpanded)

// Animate on click
iconChevron.setOnClickListener {
    iconChevron.animateExpandCollapse {
        onToggle()
    }
}
```

---

## Common Use Cases

### 1. Expandable RecyclerView Item
```kotlin
fun bind(item: Item, isExpanded: Boolean) {
    // Set initial state
    chevron.setChevronRotation(isExpanded)
    
    // Animate on click
    chevron.setOnClickListener {
        chevron.animateExpandCollapse {
            expandItem(item.id)
        }
    }
}
```

### 2. Accordion View
```kotlin
headerView.setOnClickListener {
    chevron.toggleChevronRotation {
        if (isExpanded) {
            collapseContent()
        } else {
            expandContent()
        }
        isExpanded = !isExpanded
    }
}
```

### 3. Custom Angle Rotation
```kotlin
// For 180° rotation (upside down)
chevron.animateChevronRotation(
    isExpanded = true,
    collapsedAngle = 0f,
    expandedAngle = 180f,
    duration = 300
)
```

### 4. Dropdown Menu
```kotlin
dropdownIcon.setChevronRotation(isOpen)

dropdownIcon.setOnClickListener {
    dropdownIcon.animateExpandCollapse(duration = 150) {
        if (isOpen) {
            hideDropdown()
        } else {
            showDropdown()
        }
        isOpen = !isOpen
    }
}
```

---

## Integration

### Import
```kotlin
import ui.animateExpandCollapse
import ui.setChevronRotation
import ui.toggleChevronRotation
import ui.rotateWithAnimation
```

### Dependencies
- ✅ Android View framework (already included)
- ✅ No additional dependencies required

---

## Benefits

1. **Reusability:** Write once, use everywhere
2. **Consistency:** Same animation behavior across the app
3. **Readability:** Clean, declarative code
4. **Maintainability:** Easy to update animation duration/behavior globally
5. **Type Safety:** Kotlin extension functions with proper parameters
6. **Performance:** Uses View.animate() for hardware-accelerated animations

---

## Migration Guide

Replace any inline chevron rotation code:

**Find:**
```kotlin
val targetRotation = if (view.rotation == 0f) 90f else 0f
view.animate().rotation(targetRotation).setDuration(200).start()
```

**Replace with:**
```kotlin
view.animateExpandCollapse { /* your callback */ }
```

---

**Created:** December 17, 2025  
**Module:** common  
**Status:** ✅ Ready for Use

