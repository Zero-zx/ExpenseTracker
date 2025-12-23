package transaction.usecase

import transaction.model.Borrower
import transaction.repository.BorrowerRepository
import javax.inject.Inject

class UpdateBorrowerUseCase @Inject constructor(
    private val repository: BorrowerRepository
) {
    suspend operator fun invoke(borrower: Borrower) {
        require(borrower.name.isNotBlank()) {
            "Borrower name cannot be blank"
        }

        // Validate email format if provided
        if (!borrower.email.isNullOrBlank()) {
            require(android.util.Patterns.EMAIL_ADDRESS.matcher(borrower.email).matches()) {
                "Invalid email format"
            }
        }

        // Validate phone number format if provided
        if (!borrower.phoneNumber.isNullOrBlank()) {
            require(borrower.phoneNumber.length >= 10) {
                "Phone number must be at least 10 digits"
            }
        }

        repository.updateBorrower(borrower)
    }
}

