package com.nabeel.udhaarkhata.service;

import android.util.Log;

import com.nabeel.udhaarkhata.MyApplication;

import retrofit2.Call;
import retrofit2.Callback;

public abstract class CallbackWithRetry<T> implements Callback<T> {

    private static final int TOTAL_RETRIES = 3;
    private static final String TAG = "Retry";
    private final Call<T> call;
    private int retryCount = 0;

    public CallbackWithRetry(Call<T> call) {
        this.call = call;
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (t.getLocalizedMessage()!=null)
            Log.e(TAG, t.getLocalizedMessage());
        if (retryCount++ < TOTAL_RETRIES) {
            Log.e(TAG, "Retrying... (" + retryCount + " out of " + TOTAL_RETRIES + ")");
            retry();
        }
        else {
            MyApplication.getInstance().showToast("Server Problem");
        }
    }

    private void retry() {
        call.clone().enqueue(this);
    }
}
