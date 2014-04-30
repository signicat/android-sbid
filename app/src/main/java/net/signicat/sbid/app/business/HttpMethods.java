package net.signicat.sbid.app.business;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Martin on 30.04.14.
 */
public class HttpMethods {

    public static void DummyAtbCallForTesting(){
        String query = "Fra solsiden til sluppen";
        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        URL url = null;
        try {
            url = new URL("http://www.atb.no/xmlhttprequest.php?service=routeplannerOracle.getOracleAnswer&question="+ query);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.d(Constants.TAG_TESTING, url.toString());

        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
        HttpConnectionParams.setSoTimeout(httpParams, 30000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        HttpGet httpGet = new HttpGet(url.toString());
        HttpResponse httpResponse;

        String result = "";

        try {
            httpResponse = httpClient.execute(httpGet);
            InputStream data = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(data));
            String line;
            while ((line = reader.readLine()) != null){
                result += line;
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(Constants.TAG_TESTING, result);
    }
}
