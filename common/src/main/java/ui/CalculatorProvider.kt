package ui

/**
 * Interface for providing calculator view access
 * This allows feature modules to access the calculator without depending on the app module
 */
interface CalculatorProvider {
    /**
     * Get the calculator view instance
     * @return CalculatorView or null if not available
     */
    fun getCalculatorView(): CalculatorView?
}

