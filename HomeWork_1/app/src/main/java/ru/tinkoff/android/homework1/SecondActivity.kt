package ru.tinkoff.android.homework1

import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class SecondActivity : AppCompatActivity() {

    lateinit var contactListService: GetContactListService
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
            contactListService = (binder as GetContactListService.ContactListBinder).getContactList()
        }

        override fun onServiceDisconnected(name: ComponentName) { }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val contactsReceiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val contactList = intent?.getSerializableExtra("contactList") as ArrayList<*>?
                val firstActivityIntent = Intent(context, FirstActivity::class.java)
                firstActivityIntent.putExtra("contactList", contactList)
                startActivity(firstActivityIntent)
                finish()
            }
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
            contactsReceiver, IntentFilter(CONTACT_READ)
        )
    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(this, PERMISSION_READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(PERMISSION_READ_CONTACTS),
                PERMISSION_CODE_READ_CONTACTS
            )
        } else {
            val intent = Intent(this, GetContactListService::class.java)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE_READ_CONTACTS -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    val intent = Intent(this, GetContactListService::class.java)
                    bindService(intent, connection, Context.BIND_AUTO_CREATE)
                }
            }
        }
    }

    companion object {
        const val PERMISSION_READ_CONTACTS = android.Manifest.permission.READ_CONTACTS
        const val PERMISSION_CODE_READ_CONTACTS = 698
        const val CONTACT_READ = "SINGLE_CONTACT_READ"
    }

}