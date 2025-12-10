package contact.repository

import contact.model.PhoneContact

interface PhoneContactRepository {
    suspend fun getAllContacts(): List<PhoneContact>
}