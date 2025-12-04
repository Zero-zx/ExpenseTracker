package presentation

import androidx.fragment.app.viewModels
import base.BaseFragment
import com.example.statistics.databinding.FragmentStatisticsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportsFragment : BaseFragment<FragmentStatisticsBinding>(
    FragmentStatisticsBinding::inflate
) {
    private val viewModel: ReportsViewModel by viewModels()

    override fun initListener() {

    }

    override fun observeData() {


    }

}

