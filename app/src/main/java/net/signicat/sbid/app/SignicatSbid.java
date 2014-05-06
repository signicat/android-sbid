package net.signicat.sbid.app;

import android.app.Application;
import android.content.SharedPreferences;

import net.signicat.sbid.app.business.ConfigConstants;
import net.signicat.sbid.app.business.Constants;

/**
 * Created by Martin on 06.05.14.
 */
public class SignicatSbid extends Application {

    public static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        sharedPreferences = getSharedPreferences(Constants.PREF_KEY, MODE_PRIVATE);
        LoadSettings();
    }

    private void LoadSettings(){
        if(sharedPreferences.contains(Constants.SIGNICAT_URL_PREF_KEY)){
            ConfigConstants.RP_AUTH_URL = sharedPreferences.getString(Constants.SIGNICAT_URL_PREF_KEY, ConfigConstants.DEFAULT_AUTH_URL);
        }
        if(sharedPreferences.contains(Constants.SIGNICAT_API_KEY_PREF_KEY)){
            ConfigConstants.SIGNICAT_API_KEY = sharedPreferences.getString(Constants.SIGNICAT_API_KEY_PREF_KEY, ConfigConstants.DEFAULT_API_KEY);
        }
    }
}
