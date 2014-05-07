package net.signicat.sbid.app.data;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class CollectCallPojo {

    private String orderRef;
    private String apiKey;


    public CollectCallPojo(String orderRef, String apiKey){
        this.orderRef = orderRef;
        this.apiKey = apiKey;
    }

    public StringEntity getAsJsonAsStringEntity(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orderRef", orderRef);
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

    public String getOrderRef() {
        return orderRef;
    }

    public void setOrderRef(String orderRef) {
        this.orderRef = orderRef;
    }
}
