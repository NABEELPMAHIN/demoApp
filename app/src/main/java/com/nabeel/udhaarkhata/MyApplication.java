package com.nabeel.udhaarkhata;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;
import android.widget.Toast;

import androidx.multidex.MultiDexApplication;

import com.microsoft.appcenter.utils.HandlerUtils;
import com.nabeel.udhaarkhata.pref.AppPreferencesHelper;
import com.nabeel.udhaarkhata.response.ErrorResponse;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.nabeel.udhaarkhata.Constants.BASE_URL_PREF;
import static com.nabeel.udhaarkhata.Constants.MyPREFERENCES;
import static com.nabeel.udhaarkhata.Constants.PASSWORD;
import static com.nabeel.udhaarkhata.Constants.STAGING_BASE_URL;
import static com.nabeel.udhaarkhata.utils.SpAndroidUtils.isConnected;

public class MyApplication extends MultiDexApplication {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    private static Context context;
    private static MyApplication mInstance;
    private String token;
    String BASE_URL="";
    Toast toast;

    public static ServiceGenerator serviceGenerator;
    static SharedPreferences mPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        context = getApplicationContext();
        mInstance = this;
        new EncryptedPreferences.Builder(context).withEncryptionPassword(PASSWORD).withSaveAsSingleton(true).build();
        mPrefs = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        toast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_LONG);
        BASE_URL = mPrefs.getString(BASE_URL_PREF, STAGING_BASE_URL);
        serviceGenerator = new ServiceGenerator();

    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static synchronized Context getAppContext() {
        return context;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public class ServiceGenerator {
        private int cacheSize = 10 * 1024 * 1024; // 10 MB
        private Cache cache = new Cache(context.getCacheDir(), cacheSize);
        private Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        private Retrofit retrofit = builder.build();
        private HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        private OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder().cache(cache);

        @SuppressLint("HardwareIds")
        private Interceptor basicHeaderInterceptor=new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                AppPreferencesHelper helper=new AppPreferencesHelper();
                token = helper.getAccessToken();
                Request original=chain.request();
                Request.Builder requestBuilder = original.newBuilder();
                if (original.header("AppInternal-NoAuth") == null)
                {
                    requestBuilder
                            .addHeader("Authorization", "Bearer "+token);
                } else {
                    requestBuilder
                            .removeHeader("AppInternal-NoAuth");
                }

                return chain.proceed(requestBuilder.build());
            }
        };

        private Interceptor errorInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                final Response response = chain.proceed(request);

                // todo deal with the issues the way you need to
                if (!response.isSuccessful()) {
                    HandlerUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Stuff that updates the UI
                            //showToast("Error code: "+response.code());
                        }
                    });

                }

                return response;
            }
        };

        void changeApiBaseUrl(String newApiBaseUrl) {
            BASE_URL = newApiBaseUrl;
            getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
                    .edit()
                    .putString(BASE_URL_PREF, BASE_URL)
                    .apply();

            builder = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL);

            Log.i("BaseUrl", "BASE_URL Changed to : "+newApiBaseUrl);
        }

        //        val offlineInterceptor = offlineCacheInterceptor()
        public   <S> S createService(Class<S> serviceClass,Boolean cancel) {
            if (!isConnected(context)) {
                getInstance().showToast("No Internet");
            }
//            val refreshToken = PreferenceManager.getDefaultSharedPreferences(context).getString(USER_PREF_REFRESH_TOKEN_KEY, "")
//            val authenticator = RefreshTokenAuthenticator(okHttpClient, refreshToken)
//            okHttpClient.authenticator(authenticator)
            if (!okHttpClient.interceptors().contains(loggingInterceptor)) {
                if (BuildConfig.DEBUG) {
                    loggingInterceptor.setLevel( HttpLoggingInterceptor.Level.BODY);
                } else {
                    loggingInterceptor.setLevel( HttpLoggingInterceptor.Level.NONE);
                }
//                loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
                okHttpClient.addInterceptor(loggingInterceptor);
            }
            if (!okHttpClient.interceptors().contains(basicHeaderInterceptor)) {
                okHttpClient.addInterceptor(basicHeaderInterceptor);
            }
            if (!okHttpClient.interceptors().contains(errorInterceptor)) {
                okHttpClient.addInterceptor(errorInterceptor);
            }
//            if (!okHttpClient.interceptors().contains(offlineInterceptor)) {
//                okHttpClient.addInterceptor(offlineCacheInterceptor())
//            }
            OkHttpClient okBuilder = okHttpClient.build();
            builder.client(okBuilder);
            if (cancel) {
                okBuilder.dispatcher().cancelAll();
            }
            retrofit = builder.build();
            return retrofit.create(serviceClass);
        }

        ErrorResponse parseError(retrofit2.Response response, Call retryCall) {
            Converter<ResponseBody,ErrorResponse> converter = MyApplication.serviceGenerator.retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
            ErrorResponse error;
            try {
                error = (ErrorResponse) converter.convert(response.errorBody());
            } catch (Exception e) {
                return new ErrorResponse();
            }
            return error;
        }

    }

    public void showToast(String s) {
        toast.setText(s);
        toast.show();
    }
}
