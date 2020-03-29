package com.nabeel.udhaarkhata.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.nabeel.udhaarkhata.R.layout.activity_login)

        btnLogin.setOnClickListener {
            val phoneNo=phoneNum.text.toString();
            //Toast.makeText(applicationContext,phoneNo,Toast.LENGTH_LONG).show();
            if (phoneNo.length<10)
                phoneNum.error="Invalid Phone Number"
            else
                startActivity(Intent(this,OTPActivity::class.java).putExtra("phoneNo",phoneNo))
        }
        requestHint();
    }

    private val CREDENTIAL_PICKER_REQUEST = 1  // Set to an unused request code

    // Construct a request for phone numbers and show the picker
    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val credentialsClient = Credentials.getClient(this)
        val intent = credentialsClient.getHintPickerIntent(hintRequest)
        startIntentSenderForResult(
            intent.intentSender,
            CREDENTIAL_PICKER_REQUEST,
            null, 0, 0, 0
        )
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CREDENTIAL_PICKER_REQUEST ->
                // Obtain the phone number from the result
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val credential = data.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                    phoneNum.setText(credential.id.replace("+91",""));
                    // credential.getId();  <-- will need to process phone number string
                }
            // ...
        }
    }

}
