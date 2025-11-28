package base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


abstract class BaseBottomSheet<VBinding : ViewBinding>(
    private val inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> VBinding
) : BottomSheetDialogFragment() {

    private var _binding: VBinding? = null
    val binding: VBinding get() = _binding!!

    open fun VBinding.initialize() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateMethod.invoke(inflater, container, false)
        return binding.root
    }

    open fun initView() {}
    open fun initListener() {}
    open fun observeData() {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        initListener()
        observeData()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}