package net.signicat.sbid.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.signicat.sbid.app.R;
import net.signicat.sbid.app.business.Constants;
import net.signicat.sbid.app.business.HttpsMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SbidAuthActivity extends Activity {

    private EditText personalIdEditText;
    private HttpsMethods httpsMethods;

    //Todo these should be stored and retrieved in a proper and safe manner
    private JSONObject authCallResponseObject;

    //Todo this is very ugly and should be replaced with fancy intent messaging!
    private boolean sbidClientStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sbid_auth);

        personalIdEditText = (EditText) findViewById(R.id.personalIdEditText);
        httpsMethods = new HttpsMethods();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sbid_auth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startAuthCall(View target) {

        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Authenticating");
        progress.setMessage("Please wait...");
        progress.show();

        AsyncTask<Void, Void, String> makeHttpCallTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String answer = null;
                try {
                    answer = httpsMethods.SbidAuthenticateCall(personalIdEditText.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return answer;
            }

            @Override
            protected void onPostExecute(String ans) {
                handleAuthResponse(ans);
            }
        };
        makeHttpCallTask.execute();
    }

    private void handleAuthResponse(String authResponse) {
        try {
            authCallResponseObject = new JSONObject(authResponse);
            String errorMessage = null;
            if (authCallResponseObject.has("error")) {
                errorMessage = authCallResponseObject.getString("error");
                Log.d(Constants.TAG_SBID_AUTH, errorMessage);
            }

            if (errorMessage == null || errorMessage == "null") {
                createAndStartSbidIntent(authCallResponseObject);
            } else if (errorMessage == Constants.ErrorMessageFromServer.ALREADY_IN_PROGRESS.getValue()) {
                showMessageToUser(Constants.ErrorMessageToUser.RFA3.getMessage());
                //Todo handle case correctly
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startCollectCall() throws JSONException {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Collecting the call");
        progress.setMessage("Please wait...");
        progress.show();

        final String orderRef = authCallResponseObject.getString("orderRef");
        final String collectUrl = authCallResponseObject.getString("collectUrl");

        AsyncTask<Void, Void, String> makeHttpCallTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String answer = null;
                try {
                    answer = httpsMethods.SbidCollectCall(orderRef, collectUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return answer;
            }

            @Override
            protected void onPostExecute(String ans) {
                try {
                    handleCollectCallResponse(ans);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progress.dismiss();
            }
        };
        makeHttpCallTask.execute();
    }

    private void handleCollectCallResponse(String answer) throws JSONException {
        showToast("handleCollectCallResponse");
        JSONObject collectResponseObject = new JSONObject(answer);
        startCompleteCall(collectResponseObject);
    }

    private void startCompleteCall(JSONObject collectResponseObject) throws JSONException {

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Completing the call");
        progress.setMessage("Please wait...");
        progress.show();

        String complete = collectResponseObject.getString("progressStatus");
        final String completeUrl = collectResponseObject.getString("completeUrl");
        AsyncTask<Void, Void, String> makeHttpCallTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String answer = null;
                try {
                    answer = httpsMethods.SbidGetCompleteCall(completeUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return answer;
            }

            @Override
            protected void onPostExecute(String ans) {
                try {
                    handleCompleteCall(ans);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progress.dismiss();
            }
        };
        makeHttpCallTask.execute();
    }

    private void handleCompleteCall(String answer) throws JSONException {
        showToast("HandleCompleteCall");
        Log.d(Constants.TAG_SBID_AUTH, answer);
        JSONObject jsonObject = new JSONObject(answer);
        String saml = jsonObject.getString("SAMLResponse");
        String target = jsonObject.getString("target");
        startVerifySamlResponseCall(saml, target);
    }

    private void startVerifySamlResponseCall(String saml, final String target){
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Verifying the SAML!");
        progress.setMessage("Please wait...");
        progress.show();

        final String samlFinal = saml;
        final String targetFinal = target;

        AsyncTask<Void, Void, String> makeHttpCallTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String answer = null;
                try {
                    answer = httpsMethods.SignicatVerifyCall(samlFinal, targetFinal);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return answer;
            }

            @Override
            protected void onPostExecute(String ans) {

                handleVerifyResponse(ans);
                progress.dismiss();
            }
        };
        makeHttpCallTask.execute();
    }

    private void handleVerifyResponse(String ans){
        showToast("handleVerifyResponse");
        Log.d(Constants.TAG_SBID_AUTH, ans);
        startActivity(new Intent(SbidAuthActivity.this, SuccessActivity.class));
    }

    private void showMessageToUser(String message) {
        //Todo show message to user;
    }

    private void createAndStartSbidIntent(JSONObject jsonObject) throws JSONException {

        String autoStartToken = jsonObject.getString("autoStartToken");

        Intent intent = new Intent();
        intent.setPackage("com.bankid.bus");
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE); //optional intent.addCategory(Intent.CATEGORY_DEFAULT); //optional
        intent.setType("bankid");
        intent.setData(Uri.parse("bankid://autostarttoken=<" + autoStartToken + ">&redirect=null "));
        startActivityForResult(intent, 0);

        sbidClientStarted = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sbidClientStarted && authCallResponseObject != null) {
            try {
                startCollectCall();
                sbidClientStarted = false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showToast(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }

}
