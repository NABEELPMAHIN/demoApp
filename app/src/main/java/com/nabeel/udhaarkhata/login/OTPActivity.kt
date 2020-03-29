package com.nabeel.udhaarkhata.login

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.nabeel.udhaarkhata.R
import com.nabeel.udhaarkhata.main.MainActivity
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.*

class OTPActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        val phoneNo=intent.getStringExtra("phoneNo")

        verificationCodeDesc.text="Please type verification code sent \n to +91 "+phoneNo

        et1.addTextChangedListener(GenericTextWatcher(et1))
        et2.addTextChangedListener(GenericTextWatcher(et2))
        et3.addTextChangedListener(GenericTextWatcher(et3))
        et4.addTextChangedListener(GenericTextWatcher(et4))
        et5.addTextChangedListener(GenericTextWatcher(et5))
        et6.addTextChangedListener(GenericTextWatcher(et6))

        btnBack.setOnClickListener {
            finish()
        }

        checkAndRequestPermissions()
    }

    inner class GenericTextWatcher(private val view: View) : TextWatcher {

        override fun afterTextChanged(editable: Editable) {
            // TODO Auto-generated method stub
            val text = editable.toString()

        }

        override fun beforeTextChanged(s: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
            // TODO Auto-generated method stub
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // TODO Auto-generated method stub

            //Put your code here.
            //Runs when delete/backspace pressed on soft key (tested on htc m8)
            //You can use EditText.getText().length() to make if statements here
            if (count == 0) {
                when (view.id) {

                    R.id.et1 -> {
                    }
                    R.id.et2 -> et1.requestFocus()
                    R.id.et3 -> et2.requestFocus()
                    R.id.et4 -> et3.requestFocus()
                    R.id.et5 -> et4.requestFocus()
                    R.id.et6 -> et5.requestFocus()
                }
            }
            if (count == 1) {
                when (view.id) {

                    R.id.et1 -> et2.requestFocus()
                    R.id.et2 -> et3.requestFocus()
                    R.id.et3 -> et4.requestFocus()
                    R.id.et4 -> et5.requestFocus()
                    R.id.et5 -> et6.requestFocus()
                    R.id.et6 -> {
                        val mpin = et1.getText().toString() +
                                et2.getText().toString() +
                                et3.getText().toString() +
                                et4.getText().toString() +
                                et5.getText().toString() +
                                et6.getText().toString()

                        if (mpin.equals("123456")) {
                            Toast.makeText(applicationContext, "Success", Toast.LENGTH_LONG).show()
                            startActivity(Intent(applicationContext,MainActivity::class.java))
                        }
                        else
                            Toast.makeText(applicationContext,"Invalid OTP",Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
    }


    val REQUEST_SMS_PERMISSIONS = 1
    private fun checkAndRequestPermissions(): Boolean {
        val permissionSendMessage = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.SEND_SMS
        )
        val receiveSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
        val readSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
        val listPermissionsNeeded = ArrayList<String>()
        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_MMS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.RECEIVE_MMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.RECEIVE_MMS),
                    REQUEST_SMS_PERMISSIONS
                )
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            }
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_SMS),
                    REQUEST_SMS_PERMISSIONS
                )
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            }
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.SEND_SMS),
                    REQUEST_SMS_PERMISSIONS
                )
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_SMS_PERMISSIONS
            )
            return false
        }



        return true
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action!!.equals("otp", ignoreCase = true)) {
                val message = intent.getStringExtra("message")
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                if (message!!.length == 6) {
                    et1.setText(message[0] + "")
                    et2.setText(message[1] + "")
                    et3.setText(message[2] + "")
                    et4.setText(message[3] + "")
                    et5.setText(message[4] + "")
                    et6.setText(message[5] + "")
                }

                //Do whatever you want with the code here
            }
        }
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter("otp"));
        super.onResume()
    }

    public override fun onPause() {
        super.onPause()
         LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}
