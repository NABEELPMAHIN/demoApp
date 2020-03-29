package com.nabeel.udhaarkhata.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.nabeel.udhaarkhata.MyApplication;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;

import java.util.Locale;
import java.util.UUID;

import static com.nabeel.udhaarkhata.Constants.FCM_TOKEN;
import static com.nabeel.udhaarkhata.Constants.INSTALLATION_ID_PREF;
import static com.nabeel.udhaarkhata.Constants.LANG_NAME_PREF;
import static com.nabeel.udhaarkhata.Constants.LANG_VALUE_PREF;
import static com.nabeel.udhaarkhata.Constants.MyPREFERENCES;
import static com.nabeel.udhaarkhata.Constants.TOKEN;
import static com.nabeel.udhaarkhata.utils.SpAndroidUtils.setLocale;

public class AppPreferencesHelper implements PreferencesHelper{

    private Context context = MyApplication.getAppContext();

    private SharedPreferences mPrefs = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    private EncryptedPreferences mEncryptedPrefs = EncryptedPreferences.getSingletonInstance();


    @Override
    public String getAccessToken() {
        return mEncryptedPrefs.getString(TOKEN, "");
    }

    @Override
    public void setAccessToken(String accessToken) {
        mEncryptedPrefs.edit().putString(TOKEN, accessToken).apply();
    }

    @Override
    public String getFcmToken() {
        return mEncryptedPrefs.getString(FCM_TOKEN, "");
    }

    @Override
    public void setFcmToken(String fcmToken) {
        mEncryptedPrefs.edit().putString(FCM_TOKEN, fcmToken).apply();
    }

    @Override
    public String getInstallationId() {
        String myInstallationId = mPrefs.getString(INSTALLATION_ID_PREF, "");
        if (myInstallationId.isEmpty()) {
            myInstallationId = UUID.randomUUID().toString();
            mPrefs.edit().putString(INSTALLATION_ID_PREF, myInstallationId).apply();
        }
        return myInstallationId;
    }

    @Override
    public void setInstallationId(String installationId) {

    }

    @Override
    public String getAppLangName() {
        return mPrefs.getString(LANG_NAME_PREF, "");
    }

    @Override
    public void setAppLangName(String lang) {
        String  currentLang = mPrefs.getString(LANG_NAME_PREF, "");
        if (!lang.equals(currentLang)) {
            mPrefs.edit().putString(LANG_NAME_PREF, lang).apply();
            setLocale(context, lang);
        }
    }

    @Override
    public int getAppLangValue() {

        return mPrefs.getInt(LANG_VALUE_PREF, 99);
    }

    @Override
    public void setAppLangValue(int lang) {
        mPrefs.edit().putInt(LANG_VALUE_PREF, lang).apply();
    }

    @Override
    public void clearSharedPref() {
        mPrefs.edit().clear().apply();
        mEncryptedPrefs.edit().clear().apply();
        //App.serviceGenerator.changeApiBaseUrl(STAGING_BASE_URL)
    }

    @Override
    public void setAppLanguageFirstTime() {
        String lang = getAppLangName();
        if (lang.equals("")) {
            if (Locale.getDefault().getLanguage().equals("ar")) {
                mPrefs.edit().putString(LANG_NAME_PREF, "ar").apply();
                mPrefs.edit().putInt(LANG_VALUE_PREF, 1).apply();
                setLocale(context, "ar");
            } else if (Locale.getDefault().getLanguage().equals("en")) {
                mPrefs.edit().putString(LANG_NAME_PREF, "en").apply();
                mPrefs.edit().putInt(LANG_VALUE_PREF, 2).apply();
                setLocale(context, "en");
            }
        } else {
            mPrefs.edit().putString(LANG_NAME_PREF, "en").apply();
            mPrefs.edit().putInt(LANG_VALUE_PREF, 2).apply();
            setLocale(context, "en");
        }
    }


}
