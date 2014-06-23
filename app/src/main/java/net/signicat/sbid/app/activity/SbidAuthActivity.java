package net.signicat.sbid.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.signicat.sbid.app.R;
import net.signicat.sbid.app.business.Constants;
import net.signicat.sbid.app.business.HttpsMethods;
import net.signicat.sbid.app.data.ErrorCodes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SbidAuthActivity extends Activity {

    private EditText personalIdEditText;
    private HttpsMethods httpsMethods;
    private ProgressDialog progressDialog;

    private JSONObject authCallResponseObject;

    private boolean sbidClientStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sbid_auth);

        personalIdEditText = (EditText) findViewById(R.id.personalIdEditText);
        httpsMethods = new HttpsMethods();
    }

    public void startAuthCall(View target) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.progress_dialog_authenticating_title));
        progressDialog.setMessage(getString(R.string.progress_dialog_authenticating_message));
        progressDialog.show();

        AsyncTask<Void, Void, String> makeHttpCallTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String answer = null;
                try {
                    answer = httpsMethods.SbidAuthenticateCall(personalIdEditText.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
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

            if (errorMessage == null || errorMessage.contentEquals("null")) {
                createAndStartSbidIntent(authCallResponseObject);
            } else {
                progressDialog.dismiss();
                handleErrorMessage(errorMessage);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(Constants.TAG_SBID_ERROR, e.getMessage());
            Log.d(Constants.TAG_SBID_ERROR, authResponse);
            progressDialog.dismiss();
            showToast("Error on auth response");
        }
    }

    private void startCollectCall() throws JSONException {
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
                    progressDialog.dismiss();
                    //Todo show error to user
                }

                return answer;
            }

            @Override
            protected void onPostExecute(String ans) {
                try {
                    handleCollectCallResponse(ans);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(Constants.TAG_SBID_ERROR, e.getMessage());
                    Log.d(Constants.TAG_SBID_ERROR, ans);
                    progressDialog.dismiss();
                    showToast("Error on handling collectcall response");
                }
            }
        };
        makeHttpCallTask.execute();
    }

    private void handleCollectCallResponse(String answer) throws JSONException {
        JSONObject collectResponseObject = new JSONObject(answer);
        startCompleteCall(collectResponseObject);
    }

    private void startCompleteCall(JSONObject collectResponseObject) throws JSONException {

        String complete = collectResponseObject.getString("progressStatus"); //Todo remove since we don't use it?
        final String completeUrl = collectResponseObject.getString("completeUrl");
        AsyncTask<Void, Void, String> makeHttpCallTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String answer = null;
                try {
                    answer = httpsMethods.SbidGetCompleteCall(completeUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    //Todo show error to user
                }

                return answer;
            }

            @Override
            protected void onPostExecute(String ans) {
                try {
                    handleCompleteCall(ans);
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    //Todo show error to user
                }
            }
        };
        makeHttpCallTask.execute();
    }

    private void handleCompleteCall(String answer) throws JSONException {
        JSONObject jsonObject = new JSONObject(answer);
        String saml = jsonObject.getString("SAMLResponse");
        String target = jsonObject.getString("target");
        startVerifySamlResponseCall(saml, target);
    }

    private void startVerifySamlResponseCall(String saml, final String target){
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
                    progressDialog.dismiss();
                    //Todo show error to user
                }

                return answer;
            }

            @Override
            protected void onPostExecute(String ans) {
                handleVerifyResponse(ans);
            }
        };
        makeHttpCallTask.execute();
    }

    private void handleVerifyResponse(String ans){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        showToast("Success");
        finish();
    }

    private void createAndStartSbidIntent(JSONObject jsonObject) throws JSONException {

        String autoStartToken = jsonObject.getString("autoStartToken");

        Intent intent = new Intent();
        intent.setPackage("com.bankid.bus");
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setType("bankid");
        intent.setData(Uri.parse("bankid://autostarttoken=" + autoStartToken + "&redirect=null "));
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

    private void handleErrorMessage(String errorMessage){
        String rfaMessage = ErrorCodes.errorHashMap.get(errorMessage);
        if(rfaMessage != null){
            showToast(rfaMessage);
        }
    }

}
