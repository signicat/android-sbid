package net.signicat.sbid.app.data;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class AuthenticatePojo {

    private String subject;
    private String apiKey;


    public AuthenticatePojo(String subject, String apiKey){
        this.subject = subject;
        this.apiKey = apiKey;
    }

    public StringEntity getAsJsonAsStringEntity(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("subject", subject);
            jsonObject.put("apiKey", apiKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            return new StringEntity(jsonObject.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
