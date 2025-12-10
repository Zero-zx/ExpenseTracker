package contact.usecase

import contact.model.PhoneContact
import contact.repository.PhoneContactRepository
import javax.inject.Inject

class GetAllPhoneContactsUseCase @Inject constructor(
    private val phoneContactRepository: PhoneContactRepository
) {
    suspend operator fun invoke(): List<PhoneContact> {
        return phoneContactRepository.getAllContacts()
    }
}