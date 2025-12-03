package com.example.home.home

import androidx.fragment.app.viewModels
import base.BaseFragment
import com.example.home.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::inflate
) {

    private val viewModel: HomeViewModel by viewModels()

    override fun initListener() {
        binding.apply {
            cardEvent.setOnClickListener {
                viewModel.navigateToEventList()
            }
            cardAccount.setOnClickListener {
                viewModel.navigateToTransaction()
            }
        }
    }
}