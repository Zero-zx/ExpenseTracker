package ui

import android.health.connect.datatypes.DataOrigin
import android.view.View

/**
 * Extension functions for View animations
 */

/**
 * Animate chevron rotation (0° <-> 90°) for expandable/collapsible items
 *
 * @param isExpanded Current expansion state
 * @param duration Animation duration in milliseconds (default 200ms)
 * @param onAnimationEnd Callback invoked when animation completes
 *
 */
fun View.animateChevronRotation(
    isExpanded: Boolean,
    duration: Long = 200L,
    origin: Float = 0f,
    angle: Float = 90f,
    onAnimationEnd: (() -> Unit)? = null
) {
    val targetRotation = if (isExpanded) angle else origin
    animate()
        .rotation(targetRotation)
        .setDuration(duration)
        .withEndAction {
            onAnimationEnd?.invoke()
        }
        .start()
}

/**
 * Toggle chevron rotation based on current rotation state
 * Automatically detects if chevron is at 0° or 90° and rotates to the opposite
 *
 * @param duration Animation duration in milliseconds (default 200ms)
 * @param onAnimationEnd Callback invoked when animation completes
 *
 * Usage:
 * ```
 * iconChevron.toggleChevronRotation {
 *     toggleExpansion()
 * }
 * ```
 */
fun View.toggleChevronRotation(
    duration: Long = 200L,
    onAnimationEnd: (() -> Unit)? = null
) {
    val targetRotation = if (rotation == 0f) 90f else 0f
    animate()
        .rotation(targetRotation)
        .setDuration(duration)
        .withEndAction {
            onAnimationEnd?.invoke()
        }
        .start()
}

/**
 * Rotate view to a specific angle with animation
 *
 * @param targetRotation Target rotation angle in degrees
 * @param duration Animation duration in milliseconds (default 200ms)
 * @param onAnimationEnd Callback invoked when animation completes
 *
 * Usage:
 * ```
 * view.rotateWithAnimation(180f, duration = 300) {
 *     // Animation completed
 * }
 * ```
 */
fun View.rotateWithAnimation(
    targetRotation: Float,
    duration: Long = 200L,
    onAnimationEnd: (() -> Unit)? = null
) {
    animate()
        .rotation(targetRotation)
        .setDuration(duration)
        .withEndAction {
            onAnimationEnd?.invoke()
        }
        .start()
}

/**
 * Animate chevron rotation with custom angles
 *
 * @param isExpanded Current expansion state
 * @param collapsedAngle Angle when collapsed (default 0°)
 * @param expandedAngle Angle when expanded (default 90°)
 * @param duration Animation duration in milliseconds (default 200ms)
 * @param onAnimationEnd Callback invoked when animation completes
 *
 * Usage:
 * ```
 * // For downward-facing chevrons (0° to 180°)
 * iconChevron.animateChevronRotation(
 *     isExpanded = true,
 *     collapsedAngle = 0f,
 *     expandedAngle = 180f
 * )
 * ```
 */
fun View.animateChevronRotation(
    isExpanded: Boolean,
    collapsedAngle: Float = 0f,
    expandedAngle: Float = 90f,
    duration: Long = 200L,
    onAnimationEnd: (() -> Unit)? = null
) {
    val targetRotation = if (isExpanded) expandedAngle else collapsedAngle
    animate()
        .rotation(targetRotation)
        .setDuration(duration)
        .withEndAction {
            onAnimationEnd?.invoke()
        }
        .start()
}

/**
 * Set chevron rotation without animation
 *
 * @param isExpanded Current expansion state
 * @param collapsedAngle Angle when collapsed (default 0°)
 * @param expandedAngle Angle when expanded (default 90°)
 *
 * Usage:
 * ```
 * iconChevron.setChevronRotation(isExpanded = true)
 * ```
 */
fun View.setChevronRotation(
    isExpanded: Boolean,
    collapsedAngle: Float = 0f,
    expandedAngle: Float = 90f
) {
    rotation = if (isExpanded) expandedAngle else collapsedAngle
}

/**
 * Animate expand/collapse with rotation and callback
 * Combines rotation animation with state toggle callback
 *
 * @param duration Animation duration in milliseconds (default 200ms)
 * @param onToggle Callback invoked when animation completes (use this to update your state)
 *
 * Usage:
 * ```
 * iconChevron.animateExpandCollapse {
 *     toggleExpansion()
 * }
 * ```
 */
fun View.animateExpandCollapse(
    duration: Long = 200L,
    onToggle: () -> Unit
) {
    val targetRotation = if (rotation == 0f) 90f else 0f
    animate()
        .rotation(targetRotation)
        .setDuration(duration)
        .withEndAction {
            onToggle()
        }
        .start()
}


