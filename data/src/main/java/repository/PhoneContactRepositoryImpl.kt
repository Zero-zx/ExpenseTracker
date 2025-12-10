package repository

import contact.model.PhoneContact
import contact.repository.PhoneContactRepository
import datasource.PhoneContactDataSource
import javax.inject.Inject

class PhoneContactRepositoryImpl @Inject constructor(
    private val phoneContactDataSource: PhoneContactDataSource
) : PhoneContactRepository {
    override suspend fun getAllContacts(): List<PhoneContact>{
        return phoneContactDataSource.getAllPhoneContacts()
    }
}