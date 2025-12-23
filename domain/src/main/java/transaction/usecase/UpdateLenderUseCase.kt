package transaction.usecase

import transaction.model.Lender
import transaction.repository.LenderRepository
import javax.inject.Inject

class UpdateLenderUseCase @Inject constructor(
    private val repository: LenderRepository
) {
    suspend operator fun invoke(lender: Lender) {
        require(lender.name.isNotBlank()) {
            "Lender name cannot be blank"
        }

        // Validate email format if provided
        if (!lender.email.isNullOrBlank()) {
            require(android.util.Patterns.EMAIL_ADDRESS.matcher(lender.email).matches()) {
                "Invalid email format"
            }
        }

        // Validate phone number format if provided
        if (!lender.phoneNumber.isNullOrBlank()) {
            require(lender.phoneNumber.length >= 10) {
                "Phone number must be at least 10 digits"
            }
        }

        repository.updateLender(lender)
    }
}

