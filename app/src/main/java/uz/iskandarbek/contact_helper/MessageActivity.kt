package uz.iskandarbek.contact_helper

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MessageActivity : AppCompatActivity() {

    private lateinit var contactName: String
    private lateinit var contactPhone: String
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var textView: TextView

    companion object {
        private const val REQUEST_CODE_SMS_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        contactName = intent.getStringExtra("contactName") ?: ""
        contactPhone = intent.getStringExtra("contactPhone") ?: ""

        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        textView = findViewById(R.id.textView)

        sendButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                showSimCardSelectionDialog()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), REQUEST_CODE_SMS_PERMISSION)
            }
        }
    }

    private fun showSimCardSelectionDialog() {
        val simCardOptions = arrayOf("SIM 1", "SIM 2") // SIM kartalarni tanlash ro'yxati
        val builder = AlertDialog.Builder(this)
        builder.setTitle("SIM kartani tanlang")
        builder.setItems(simCardOptions) { _, which ->
            val simCard = when (which) {
                0 -> "SIM 1"
                1 -> "SIM 2"
                else -> "SIM 1"
            }
            sendSms(simCard)
        }
        builder.show()
    }

    private fun sendSms(simCard: String) {
        val message = messageEditText.text.toString()
        if (message.isNotEmpty()) {
            try {
                SmsManager.getDefault().sendTextMessage(contactPhone, null, message, null, null)
                textView.text = "SMS yuborildi: $simCard"
            } catch (e: Exception) {
                Toast.makeText(this, "Xato: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Xabar matnini kiriting", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_SMS_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSimCardSelectionDialog()
            } else {
                Toast.makeText(this, "SMS yuborish uchun ruxsat kerak", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
