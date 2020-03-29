package com.nabeel.udhaarkhata.pref;

public interface PreferencesHelper {
    public String getAccessToken();
    public void setAccessToken(String accessToken);

    public String getFcmToken();
    public void setFcmToken(String fcmToken);

    public String getInstallationId();
    public void setInstallationId(String installationId);

    public String getAppLangName();
    public void setAppLangName(String lang);

    public int getAppLangValue();
    public void setAppLangValue(int lang);

    public void clearSharedPref();

    public void setAppLanguageFirstTime();
}
