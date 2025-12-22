package ui

import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.common.R
import com.example.common.databinding.CustomToastLayoutBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun Fragment.navigateBack() {
    findNavController().navigateUp()
}

/*
 * Show a success toast.
 */
fun Context.showSuccessToast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT
) {
    showCustomToast(
        message = message,
        iconRes = R.drawable.ic_success_sync_40dp,
        backgroundColor = R.color.bg_success,
        duration = duration
    )
}


/*
 * Show a warning toast.
 */
fun Context.showWarningToast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT
) {
    showCustomToast(
        message = message,
        iconRes = R.drawable.ic_warning,
        backgroundColor = R.color.bg_warning,
        duration = duration
    )
}

/**
 * Show a custom toast with an icon and background color.
 */

fun Context.showCustomToast(
    message: String,
    @DrawableRes iconRes: Int,
    @ColorInt backgroundColor: Int,
    duration: Int = Toast.LENGTH_SHORT
) {
    // 1. Initialize ViewBinding
    val binding = CustomToastLayoutBinding.inflate(LayoutInflater.from(this))

    // 2. Set dynamic properties
    binding.toastText.text = message
    binding.toastIcon.setImageResource(iconRes)

    // Change background color using tint
    binding.toastCard.background = getDrawable(R.drawable.rounded_background)
    binding.toastCard.setBackgroundColor(backgroundColor)

    // 3. Create and show the Toast
    Toast(applicationContext).apply {
        this.duration = duration
        setGravity(Gravity.TOP, 0, 120) // Force center screen
        view = binding.root
        show()
    }
}

/**
 * Create a slide-up animation for a view.
 */
fun createSlideUpAnimation(context: Context?, view: View): Animation {
    val slideUpAnimation =
        AnimationUtils.loadAnimation(context, com.example.common.R.anim.slide_up)
    slideUpAnimation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {}

        override fun onAnimationEnd(animation: Animation?) {
            view.visibility = View.GONE
        }

        override fun onAnimationRepeat(animation: Animation?) {}
    })

    return slideUpAnimation
}

fun createSlideDownAnimation(context: Context?): Animation {
    return AnimationUtils.loadAnimation(context, com.example.common.R.anim.slide_down)
}

/**
 * Open a date picker and write the selected date into [target] in format dd/MM/yyyy.
 * Calls [onSelected] with epoch millis at the start of the selected day (system zone).
 * Uses MaterialDatePicker under the hood.
 */
fun Fragment.openDatePicker(target: TextView, onSelected: (Long) -> Unit = {}) {
    val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
    datePickerBuilder.setTitleText("Select Date")

    if (target.text.toString().isNotEmpty()) {
        try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val parsedDate = try {
                dateFormat.parse(target.text.toString())
            } catch (_: Exception) {
                null
            }
            parsedDate?.let {
                datePickerBuilder.setSelection(it.time)
            }
        } catch (_: Exception) {
            // ignore
        }
    }

    val datePicker = datePickerBuilder.build()
    datePicker.addOnPositiveButtonClickListener { selection ->
        try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val formattedDate = dateFormat.format(Date(selection))

            // compute day label: "Today" if selected date is today, otherwise full day name (e.g., "Monday")
            val selCal = Calendar.getInstance().apply { timeInMillis = selection }
            val today = Calendar.getInstance()
            val dayLabel = if (selCal.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && selCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
            ) {
                "Today"
            } else {
                // full day name for selected date using locale
                SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(selection))
            }

            target.text = "$dayLabel - $formattedDate"

            // compute epoch millis at start of selected day in system zone and call callback
            try {
                val localDate =
                    Instant.ofEpochMilli(selection).atZone(ZoneId.systemDefault()).toLocalDate()
                val startOfDay =
                    localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                onSelected(startOfDay)
            } catch (_: Exception) {
                // fallback: use selection as-is
                onSelected(selection)
            }
        } catch (_: Exception) {
            // ignore
        }
    }
    datePicker.addOnNegativeButtonClickListener {
        target.clearFocus()
    }
    datePicker.addOnDismissListener {
        target.clearFocus()
    }
    datePicker.show(parentFragmentManager, "material_date_picker")
}

/**
 * Open a time picker and write the selected time into [target] in format HH:mm or hh:mm a depending on 24h setting.
 * Calls [onSelected] with milliseconds offset from midnight for the selected time (hour*3600_000 + minute*60_000).
 */
fun Fragment.openTimePicker(target: TextView, onSelected: (Long) -> Unit = {}) {
    val now = Calendar.getInstance()
    var hour = now.get(Calendar.HOUR_OF_DAY)
    var minute = now.get(Calendar.MINUTE)

    if (target.text.toString().isNotEmpty()) {
        try {
            // Try parse as 24-hour first
            val sdf24 = SimpleDateFormat("HH:mm", Locale.getDefault())
            val parsed = try {
                sdf24.parse(target.text.toString())
            } catch (_: Exception) {
                null
            }
            parsed?.let {
                val cal = Calendar.getInstance().apply { time = it }
                hour = cal.get(Calendar.HOUR_OF_DAY)
                minute = cal.get(Calendar.MINUTE)
            }
        } catch (_: Exception) {
            // ignore
        }
    }

    val is24Hour = DateFormat.is24HourFormat(requireContext())
    val listener = TimePickerDialog.OnTimeSetListener { _, h, m ->
        try {
            val formatted = if (is24Hour) {
                String.format(Locale.getDefault(), "%02d:%02d", h, m)
            } else {
                // 12-hour format with AM/PM
                val cal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, h)
                    set(Calendar.MINUTE, m)
                }
                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
            }
            target.text = formatted

            // return offset millis from midnight
            val offsetMillis = h * 3_600_000L + m * 60_000L
            onSelected(offsetMillis)
        } catch (_: Exception) {
            // ignore
        }
    }

    val dialog = TimePickerDialog(requireContext(), listener, hour, minute, is24Hour)
    dialog.setOnCancelListener { target.clearFocus() }
    dialog.setOnDismissListener { target.clearFocus() }
    dialog.show()
}

/**
 * Open a month picker and write the selected month into [target] in format MM/yyyy.
 * Calls [onSelected] with epoch millis at the start of the selected month (system zone).
 * Uses MaterialDatePicker month picker under the hood.
 */
fun Fragment.openMonthPicker(target: TextView, onSelected: (Long) -> Unit = {}) {
    val monthPickerBuilder = MaterialDatePicker.Builder.datePicker()
    monthPickerBuilder.setTitleText("Select Month")
    monthPickerBuilder.setSelection(MaterialDatePicker.todayInUtcMilliseconds())

    if (target.text.toString().isNotEmpty()) {
        try {
            val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val parsedDate = try {
                dateFormat.parse(target.text.toString())
            } catch (_: Exception) {
                null
            }
            parsedDate?.let {
                monthPickerBuilder.setSelection(it.time)
            }
        } catch (_: Exception) {
            // ignore
        }
    }

    val monthPicker = monthPickerBuilder.build()
    monthPicker.addOnPositiveButtonClickListener { selection ->
        try {
            val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val formattedDate = dateFormat.format(Date(selection))
            target.text = formattedDate

            // compute epoch millis at start of selected month in system zone
            try {
                val localDate =
                    Instant.ofEpochMilli(selection).atZone(ZoneId.systemDefault()).toLocalDate()
                val startOfMonth =
                    localDate.withDayOfMonth(1)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                onSelected(startOfMonth)
            } catch (_: Exception) {
                // fallback: use selection as-is
                onSelected(selection)
            }
        } catch (_: Exception) {
            // ignore
        }
    }
    monthPicker.addOnNegativeButtonClickListener {
        target.clearFocus()
    }
    monthPicker.addOnDismissListener {
        target.clearFocus()
    }
    monthPicker.show(parentFragmentManager, "material_month_picker")
}
