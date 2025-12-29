package presentation.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.statistics.R
import com.example.statistics.databinding.FragmentTabContentBinding

class TabContentFragment : Fragment(R.layout.fragment_tab_content) {

    private var _binding: FragmentTabContentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTabContentBinding.bind(view)

        val tabNumber = arguments?.getInt(ARG_TAB_NUMBER, 1) ?: 1
        binding.textViewTabNumber.text = tabNumber.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TAB_NUMBER = "tab_number"

        fun newInstance(tabNumber: Int): TabContentFragment {
            return TabContentFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TAB_NUMBER, tabNumber)
                }
            }
        }
    }
}

