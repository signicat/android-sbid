package net.signicat.sbid.app.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.signicat.sbid.app.R;
import net.signicat.sbid.app.SignicatSbid;
import net.signicat.sbid.app.business.ConfigConstants;
import net.signicat.sbid.app.business.Constants;

public class SettingsActivity extends Activity {

    private EditText urlSettingsEditText;
    private EditText apiKeySettingsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        urlSettingsEditText = (EditText)findViewById(R.id.authUriSettingEditText);
        urlSettingsEditText.setText(ConfigConstants.RP_AUTH_URL);

        apiKeySettingsEditText = (EditText)findViewById(R.id.apiKeySettingEditText);
        apiKeySettingsEditText.setText(ConfigConstants.SIGNICAT_API_KEY);

    }

    private void saveAndUpdateSettings(){
        String newAuthUrl = urlSettingsEditText.getText().toString();
        String newApiKey = apiKeySettingsEditText.getText().toString();

        ConfigConstants.RP_AUTH_URL = newAuthUrl;
        ConfigConstants.SIGNICAT_API_KEY = newApiKey;
        SharedPreferences.Editor editor = SignicatSbid.sharedPreferences.edit();
        editor.putString(Constants.SIGNICAT_URL_PREF_KEY, newAuthUrl);
        editor.putString(Constants.SIGNICAT_API_KEY_PREF_KEY, newApiKey);
        editor.commit();
    }

    public void saveSettingsClick(View target){
        saveAndUpdateSettings();
        showToast("Sattings saved");
        finish();
    }

    private void showToast(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }
}
