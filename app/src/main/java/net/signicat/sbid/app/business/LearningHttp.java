package net.signicat.sbid.app.business;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;

/**
 * Created by Martin on 02.05.14.
 */
public class LearningHttp {
        //    public static void DummyHttpPost() throws IOException {
        ////        String url = UrlEncodeStrings("http://posttestserver.com/post.php", "?dir=testytest");
        ////        Log.d(Constants.TAG_TESTING, url);
        //        String url = "http://posttestserver.com/post.php?dir=testytest";
        //
        //        HttpClient httpClient = GetBasicHttpClient();
        //        HttpPost httpPost = new HttpPost(url);
        //        httpPost.setHeader("Accept", "application/json");
        //        httpPost.setHeader("Content-type", "application/json");
        //        httpPost.setEntity(new StringEntity("{\"filters\":true}"));
        //
        //        HttpResponse httpResponse = PerformHttpPost(httpClient, httpPost);
        //        String result = HttpResponseToString(httpResponse);
        //        Log.d(Constants.TAG_TESTING, result);
        //    }

//    public static void DummyHttpGet() throws IOException {
//        String url = UrlEncodeStrings("http://www.atb.no/xmlhttprequest.php?service=routeplannerOracle.getOracleAnswer&question=", "Fra solsiden til sluppen");
//
//        Log.d(Constants.TAG_TESTING, url);
//
//        HttpClient httpClient = GetBasicHttpClient();
//        HttpGet httpGet = new HttpGet(url.toString());
//        HttpResponse httpResponse = PerformHttpGet(httpClient, httpGet);
//
//        String result = HttpResponseToString(httpResponse);
//
//        Log.d(Constants.TAG_TESTING, result);
//    }
}
