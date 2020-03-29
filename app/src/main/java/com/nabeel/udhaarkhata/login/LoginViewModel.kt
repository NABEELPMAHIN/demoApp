package com.nabeel.udhaarkhata.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nabeel.udhaarkhata.response.DefaultResponse
import com.nabeel.udhaarkhata.service.LoginListner
import com.nabeel.udhaarkhata.service.ServiceInteractor

class LoginViewModel : ViewModel() {
    val otpResponse = MutableLiveData<DefaultResponse>()

    public fun getOTP(context: Context, phoneNo:String) {
        val interacter = ServiceInteractor(context)

        interacter.getOTP(phoneNo,LoginListner{
            otpResponse.value=it;
        })
        // Do an asynchronous operation to fetch users.
    }
}