package net.signicat.sbid.app.business;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

/**
 * Created by Martin on 30.04.14.
 */
public class HttpsMethods {

    private CookieStore cookieStore;
    private HttpContext httpContext;

    public HttpsMethods(){
        cookieStore = new BasicCookieStore();
        httpContext = new BasicHttpContext();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    }

    public String SbidAuthenticateCall(String personalId) throws IOException {
        // The target parameter is used to restrict the "audience"
        // of the SAML response, i.e. the intended recipient of the
        // SAML assertion. In a browser SAML 1.1 POST/Redirect, the
        // SAML response is HTTP POSTed to the target URL, which
        // validates the SAML -- including the audience.
        // In this case, there is no browser, but the app sets the
        // target anyway so that it may still be validated on the
        // server along with the SAML assertion.
        String target = URLEncoder.encode(Constants.TARGET, "UTF-8");
        String url = ConfigConstants.RP_AUTH_URL + target;

        HttpClient httpClient = getNewHttpClient();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setEntity(new StringEntity("{ \"subject\": \""+personalId+"\" }"));
        httpPost.setEntity(new StringEntity("{ \"apiKey\": \""+ConfigConstants.SIGNICAT_API_KEY+"\" }"));

        HttpResponse httpResponse = PerformHttpPost(httpClient, httpPost, httpContext);

        String result = HttpResponseToString(httpResponse);
        return result;
    }

    public String SbidCollectCall(String orderRef, String collectUrl) throws IOException {
        String url = collectUrl;

        HttpClient httpClient = getNewHttpClient();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setEntity(new StringEntity("{ \"orderRef\": \""+orderRef+"\" }"));

        HttpResponse httpResponse = PerformHttpPost(httpClient, httpPost, httpContext);
        String result = HttpResponseToString(httpResponse);
        return result;
    }

    public String SbidGetCompleteCall(String completeUrl) throws IOException {
        String url = completeUrl;

        HttpClient httpClient = getNewHttpClient();

        HttpGet httpGet = new HttpGet(url);

        HttpResponse httpResponse = PerformHttpGet(httpClient, httpGet, httpContext);
        String result = HttpResponseToString(httpResponse);
        return result;
    }

    public String SignicatVerifyCall(String samlString, String target) throws IOException {
        String url = ConfigConstants.RP_VALIDATE_SAML_URL;

        HttpClient httpClient = getNewHttpClient();

        HttpPost httpPost = new HttpPost(Constants.TARGET);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("SAMLResponse", samlString));
        nameValuePairs.add(new BasicNameValuePair("target", target));
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse httpResponse = PerformHttpPost(httpClient, httpPost, httpContext);
        String result = HttpResponseToString(httpResponse);
        return result;
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

    private static HttpResponse PerformHttpGet(HttpClient httpClient, HttpGet httpRequest, HttpContext httpContext) throws IOException {
        HttpResponse httpResponse = httpClient.execute(httpRequest, httpContext);
        return  httpResponse;
    }

    private static HttpResponse PerformHttpPost(HttpClient httpClient, HttpPost httpPost, HttpContext httpContext) throws IOException {
        HttpResponse httpResponse = httpClient.execute(httpPost, httpContext);
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

    public static HttpClient getNewHttpClient() {
        try {
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("https",
                    SSLSocketFactory.getSocketFactory(), 443));

            HttpParams params = new BasicHttpParams();

            SingleClientConnManager mgr = new SingleClientConnManager(params, schemeRegistry);

            HttpClient httpClient = new DefaultHttpClient(mgr, params);

            return httpClient;
        } catch (Exception e) {
            Log.e(Constants.TAG_SBID_AUTH, e.toString());
            return new DefaultHttpClient();
        }
    }
}
