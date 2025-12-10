package datasource

import android.content.Context
import android.provider.ContactsContract
import contact.model.PhoneContact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PhoneContactDataSource(private val context: Context) {

    suspend fun getAllPhoneContacts(): List<PhoneContact> = withContext(Dispatchers.IO) {
        val contacts = mutableMapOf<Long, PhoneContact>()
        val contentResolver = context.contentResolver

        // 1. Query basic contacts (id + name)
        contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
            ),
            "${ContactsContract.Contacts.HAS_PHONE_NUMBER} = 1",
            null,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC"
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
            val nameIndex =
                cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex) ?: ""
                contacts[id] = PhoneContact(
                    id = id,
                    displayName = name
                )
            }
        }

        if (contacts.isEmpty()) return@withContext emptyList()

        contacts.values
            .sortedBy { it.displayName.lowercase() }
    }
}
