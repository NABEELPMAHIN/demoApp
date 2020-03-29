package com.nabeel.udhaarkhata.service;

import android.content.Context;
import android.widget.Toast;

import com.nabeel.udhaarkhata.MyApplication;
import com.nabeel.udhaarkhata.response.DefaultResponse;

import retrofit2.Call;
import retrofit2.Response;

public class ServiceInteractor {

    private Context context;
    private Service service;

    public ServiceInteractor(Context context) {
        this.context = context;
        this.service = MyApplication.serviceGenerator.createService(Service.class, false);
    }



    public void getOTP(String phoneNo, final LoginListner listener) {
        Call<DefaultResponse> call = service.getOTP(phoneNo);
        call.enqueue(new CallbackWithRetry<DefaultResponse>(call) {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {

                if (response.isSuccessful()) {
                    listener.getOTPResult(response.body());
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                super.onFailure(call, t);
            }
        });
    }

}
