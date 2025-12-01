package base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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

    /**
     * Collect a StateFlow<UIState<T>> using lifecycle-aware repeatOnLifecycle(STARTED).
     * Use for UI state updates.
     */
    fun <T> collectFlow(flow: StateFlow<UIState<T>>, action: (UIState<T>) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect { state -> action(state) }
            }
        }
    }

    /**
     * Collect any StateFlow<T> using lifecycle-aware repeatOnLifecycle(STARTED).
     */
    fun <T> collectState(flow: StateFlow<T>, action: (T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect { value -> action(value) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}