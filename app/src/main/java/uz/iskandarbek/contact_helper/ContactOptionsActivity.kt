package uz.iskandarbek.contact_helper

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.net.Uri

class ContactOptionsActivity : AppCompatActivity() {

    private lateinit var contactName: String
    private lateinit var contactPhone: String
    private lateinit var messageEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_options)

        contactName = intent.getStringExtra("contactName") ?: ""
        contactPhone = intent.getStringExtra("contactPhone") ?: ""

        val contactNameTextView: TextView = findViewById(R.id.contactName)
        val smsButton: Button = findViewById(R.id.smsButton)
        val callButton: Button = findViewById(R.id.callButton)
        messageEditText = findViewById(R.id.messageEditText) // E'lon qilish

        contactNameTextView.text = contactName

        smsButton.setOnClickListener {
            val message = messageEditText.text.toString()
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                sendSms(contactPhone, message)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 2)
            }
        }

        callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$contactPhone")
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 3)
            }
        }
    }

    private fun sendSms(phoneNumber: String, message: String) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        Toast.makeText(this, "Xabar yuborildi", Toast.LENGTH_SHORT).show()
        messageEditText.text.clear()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            2 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSms(contactPhone, messageEditText.text.toString())
                } else {
                    Toast.makeText(this, "SMS yuborish uchun ruxsat kerak", Toast.LENGTH_SHORT).show()
                }
            }
            3 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(Intent.ACTION_CALL).apply {
                        data = Uri.parse("tel:$contactPhone")
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Qo'ng'iroq qilish uchun ruxsat kerak", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
