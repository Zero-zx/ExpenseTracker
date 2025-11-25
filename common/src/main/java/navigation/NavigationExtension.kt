package navigation

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions

fun NavController.navigateWithAnim(
    resId: String,
    args: Bundle? = null,
) {
    val options = NavOptions.Builder()
        .setEnterAnim(android.R.anim.slide_in_left)
        .setExitAnim(android.R.anim.slide_out_right)
        .setPopEnterAnim(android.R.anim.fade_in)
        .setPopExitAnim(android.R.anim.fade_out)
        .build()

    navigate(resId, options)
}