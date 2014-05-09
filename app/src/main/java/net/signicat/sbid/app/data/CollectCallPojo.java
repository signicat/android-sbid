package net.signicat.sbid.app.data;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class CollectCallPojo {

    private String orderRef;


    public CollectCallPojo(String orderRef){
        this.orderRef = orderRef;
    }

    public StringEntity getAsJsonAsStringEntity(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orderRef", orderRef);
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

    public String getOrderRef() {
        return orderRef;
    }

    public void setOrderRef(String orderRef) {
        this.orderRef = orderRef;
    }
}
