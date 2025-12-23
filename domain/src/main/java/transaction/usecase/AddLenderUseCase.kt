package transaction.usecase

import transaction.model.Lender
import transaction.repository.LenderRepository
import javax.inject.Inject

class AddLenderUseCase @Inject constructor(
    private val repository: LenderRepository
) {
    suspend operator fun invoke(
        name: String,
        phoneNumber: String? = null,
        email: String? = null,
        accountId: Long,
        notes: String? = null
    ): Lender {
        // Validate name
        require(name.isNotBlank()) {
            "Lender name cannot be blank"
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

        // Check if lender with same name already exists for this account
        val existingLender = repository.getLenderByName(trimmedName, accountId)
        if (existingLender != null) {
            throw IllegalArgumentException("A lender with the name '$trimmedName' already exists")
        }

        // Sanitize and create lender
        val lender = Lender(
            name = trimmedName,
            phoneNumber = phoneNumber?.trim(),
            email = email?.trim(),
            accountId = accountId,
            notes = notes?.trim()
        )

        val id = repository.insertLender(lender)
        return lender.copy(id = id)
    }
}