package ru.tinkoff.android.homework1

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.database.Cursor
import android.os.Binder
import android.os.IBinder
import android.provider.ContactsContract
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ru.tinkoff.android.homework1.SecondActivity.Companion.CONTACT_READ

class GetContactListService : Service() {

    val binder = ContactListBinder()

    override fun onCreate() {
        super.onCreate()
        this.readContacts()
    }

    override fun onBind(intent: Intent): IBinder = binder

    @SuppressLint("Range")
    private fun readContacts() {
        var contact: Contact
        val contactList = ArrayList<String>()
        val cursor: Cursor? = contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )

        if (cursor?.getCount()!! > 0) {
            while (cursor.moveToNext()) {
                contact = Contact()
                val id: String = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts._ID)
                )
                contact.id = id

                val name: String = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                )
                contact.name = name

                val has_phone: String = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                )
                if (has_phone.toInt() > 0) {
                    contact.phone = this.getContactPhone(id, contact)
                }
                val phoneStr = if (contact.phone.isBlank()) "" else ", ${contact.phone}"
                contactList.add(contact.name + phoneStr)
            }
        }
        cursor.close()
        val intent = Intent(CONTACT_READ)
        intent.putExtra("contactList", contactList)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    @SuppressLint("Range")
    private fun getContactPhone(id: String, contact: Contact): String {
        val phoneCursor: Cursor? = contentResolver?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            arrayOf(id),
            null
        )
        var phone = ""
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                phone = phoneCursor.getString(
                    phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                )
            }
        }
        phoneCursor?.close()
        return phone
    }

    inner class ContactListBinder: Binder() {
        fun getContactList(): GetContactListService = this@GetContactListService
    }

}