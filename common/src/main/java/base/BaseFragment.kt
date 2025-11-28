package base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


abstract class BaseFragment<VBinding : ViewBinding>(
    private val inflateMethod: (LayoutInflater, ViewGroup?, Boolean) -> VBinding
) : Fragment() {

    private var _binding: VBinding? = null
    val binding: VBinding get() = _binding!!

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

    fun <T> collectFlow(flow: StateFlow<UIState<T>>, action: (UIState<T>) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            flow.collect { state -> action(state) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}