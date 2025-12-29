package ui

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.LifecycleOwner
import constants.FragmentResultKeys


// Extension function for setting result (Sender side)
fun Fragment.setSelectionResult(requestKey: String, bundle: Bundle) {
    setFragmentResult(requestKey, bundle)
}

// Extension function for listening to result (Receiver side)
fun Fragment.listenForSelectionResult(
    requestKey: String,
    lifecycleOwner: LifecycleOwner = viewLifecycleOwner,
    onResult: (Bundle) -> Unit
) {
    setFragmentResultListener(requestKey) { _, bundle ->
        onResult(bundle)
    }
}

// Generic ID-based selection result (can be used for any selection type)
fun Fragment.setIdSelectionResult(selectedId: Long) {
    setSelectionResult(
        FragmentResultKeys.REQUEST_SELECT_ID,
        bundleOf(FragmentResultKeys.RESULT_ID to selectedId)
    )
}

fun Fragment.setCategoryIdSelectionResult(selectedId: Long) {
    setSelectionResult(
        FragmentResultKeys.REQUEST_SELECT_CATEGORY_ID,
        bundleOf(FragmentResultKeys.RESULT_CATEGORY_ID to selectedId)
    )
}

fun Fragment.setEventIdSelectionResult(selectedId: Long) {
    setSelectionResult(
        FragmentResultKeys.REQUEST_SELECT_EVENT_ID,
        bundleOf(FragmentResultKeys.RESULT_EVENT_ID to selectedId)
    )
}

fun Fragment.setEventNameSelectionResult(selectedName: String) {
    setSelectionResult(
        FragmentResultKeys.REQUEST_SELECT_EVENT_NAME,
        bundleOf(FragmentResultKeys.RESULT_EVENT_NAME to selectedName)
    )
}

fun Fragment.setBorrowerNameSelectionResult(selectedName: String) {
    setSelectionResult(
        FragmentResultKeys.REQUEST_SELECT_BORROWER_NAME,
        bundleOf(FragmentResultKeys.RESULT_BORROWER_NAME to selectedName)
    )
}

fun Fragment.setAccountIdSelectionResult(selectedId: Long) {
    setSelectionResult(
        FragmentResultKeys.REQUEST_SELECT_ACCOUNT_ID,
        bundleOf(FragmentResultKeys.RESULT_ACCOUNT_ID to selectedId)
    )
}

fun Fragment.setPayeeIdsSelectionResult(selectedIds: LongArray) {
    setSelectionResult(
        FragmentResultKeys.REQUEST_SELECT_PAYEE_NAMES,
        bundleOf(FragmentResultKeys.RESULT_PAYEE_NAMES to selectedIds)
    )
}

fun Fragment.setLocationIdSelectionResult(selectedId: Long) {
    setSelectionResult(
        FragmentResultKeys.REQUEST_SELECT_LOCATION_ID,
        bundleOf(FragmentResultKeys.RESULT_LOCATION_ID to selectedId)
    )
}

fun Fragment.setLocationNameSelectionResult(selectedName: String) {
    setSelectionResult(
        FragmentResultKeys.REQUEST_SELECT_LOCATION_NAME,
        bundleOf(FragmentResultKeys.RESULT_LOCATION_NAME to selectedName)
    )
}

fun Fragment.setCategoryIdsSelectionResult(selectedIds: LongArray) {
    setSelectionResult(
        FragmentResultKeys.REQUEST_SELECT_CATEGORY_IDS,
        bundleOf(FragmentResultKeys.RESULT_CATEGORY_IDS to selectedIds)
    )
}

fun Fragment.setAccountIdsSelectionResult(selectedIds: LongArray) {
    setSelectionResult(
        FragmentResultKeys.REQUEST_SELECT_ACCOUNT_IDS,
        bundleOf(FragmentResultKeys.RESULT_ACCOUNT_IDS to selectedIds)
    )
}