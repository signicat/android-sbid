package net.signicat.sbid.app.business;

import android.util.Log;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
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

    public static void DummyHttpGet() throws IOException {
        String url = UrlEncodeStrings("http://www.atb.no/xmlhttprequest.php?service=routeplannerOracle.getOracleAnswer&question=", "Fra solsiden til sluppen");

        Log.d(Constants.TAG_TESTING, url);

        HttpClient httpClient = GetBasicHttpClient();
        HttpGet httpGet = new HttpGet(url.toString());
        HttpResponse httpResponse = PerformHttpGet(httpClient, httpGet);

        String result = HttpResponseToString(httpResponse);

        Log.d(Constants.TAG_TESTING, result);
    }

    public static void DummyHttpPost() throws IOException {
//        String url = UrlEncodeStrings("http://posttestserver.com/post.php", "?dir=testytest");
//        Log.d(Constants.TAG_TESTING, url);
        String url = "http://posttestserver.com/post.php?dir=testytest";

        HttpClient httpClient = GetBasicHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setEntity(new StringEntity("{\"filters\":true}"));

        HttpResponse httpResponse = PerformHttpPost(httpClient, httpPost);
        String result = HttpResponseToString(httpResponse);
        Log.d(Constants.TAG_TESTING, result);
    }

    public static String SbidAuthenticateCall(){
        return null;
    }

    private static String UrlEncodeStrings(String urlString, String queryString){
        try {
            queryString = URLEncoder.encode(queryString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        URL url = null;
        try {
            url = new URL(urlString+ queryString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url.toString();
    }

    private static HttpClient GetBasicHttpClient(){
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
        HttpConnectionParams.setSoTimeout(httpParams, 30000);
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        return httpClient;
    }

    private static HttpResponse PerformHttpGet(HttpClient httpClient, HttpGet httpRequest) throws IOException {
        HttpResponse httpResponse = httpClient.execute(httpRequest);
        return  httpResponse;
    }

    private static HttpResponse PerformHttpPost(HttpClient httpClient, HttpPost httpPost) throws IOException {
        HttpResponse httpResponse = httpClient.execute(httpPost);
        return httpResponse;
    }

    private static String HttpResponseToString(HttpResponse httpResponse) throws IOException {
        String result = "";
        InputStream data = httpResponse.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(data));
        String line;
        while ((line = reader.readLine()) != null){
            result += line;
        }
        return result;
    }
}
