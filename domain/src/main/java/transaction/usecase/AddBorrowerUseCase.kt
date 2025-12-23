package transaction.usecase

import transaction.model.Borrower
import transaction.repository.BorrowerRepository
import javax.inject.Inject

class AddBorrowerUseCase @Inject constructor(
    private val repository: BorrowerRepository
) {
    suspend operator fun invoke(
        name: String,
        phoneNumber: String? = null,
        email: String? = null,
        accountId: Long,
        notes: String? = null
    ): Borrower {
        // Validate name
        require(name.isNotBlank()) {
            "Borrower name cannot be blank"
        }

        // Validate email format if provided
        if (!email.isNullOrBlank()) {
            require(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                "Invalid email format"
            }
        }

        // Validate phone number format if provided
        if (!phoneNumber.isNullOrBlank()) {
            require(phoneNumber.length >= 10) {
                "Phone number must be at least 10 digits"
            }
        }

        val trimmedName = name.trim()

        // Check if borrower with same name already exists for this account
        val existingBorrower = repository.getBorrowerByName(trimmedName, accountId)
        if (existingBorrower != null) {
            throw IllegalArgumentException("A borrower with the name '$trimmedName' already exists")
        }

        // Sanitize and create borrower
        val borrower = Borrower(
            name = trimmedName,
            phoneNumber = phoneNumber?.trim(),
            email = email?.trim(),
            accountId = accountId,
            notes = notes?.trim()
        )

        val id = repository.insertBorrower(borrower)
        return borrower.copy(id = id)
    }
}
