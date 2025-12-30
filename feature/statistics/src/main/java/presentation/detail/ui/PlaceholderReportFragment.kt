package presentation.detail.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.statistics.R

class PlaceholderReportFragment : Fragment(R.layout.fragment_placeholder_report) {

    private var reportTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reportTitle = arguments?.getString(ARG_TITLE) ?: "Report"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.text_view_title).text = reportTitle
    }

    companion object {
        private const val ARG_TITLE = "title"

        fun newInstance(title: String): PlaceholderReportFragment {
            return PlaceholderReportFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                }
            }
        }
    }
}

