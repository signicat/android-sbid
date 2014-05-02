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
public class HttpMethods {

    private ArrayList<Cookie> cookieList;
    private CookieStore cookieStore;
    private HttpContext httpContext;

    public HttpMethods(){
        cookieList = new ArrayList<Cookie>();
        cookieStore = new BasicCookieStore();
        httpContext = new BasicHttpContext();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    }

    public String SbidAuthenticateCall(String personalId) throws IOException {
        String url = "https://dev01.signicat.com/std/method/nbidmobile/?id=sbid2014::";

        HttpClient httpClient = _getNewHttpClient();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setEntity(new StringEntity("{ \"subject\": \""+personalId+"\" }"));

        HttpResponse httpResponse = PerformHttpPost(httpClient, httpPost, httpContext);

        if(cookieStore.getCookies().size() > 0){
            Log.d(Constants.TAG_SBID_AUTH, "Got some cookies!");
        }

        String result = HttpResponseToString(httpResponse);
        Log.d(Constants.TAG_SBID_AUTH, result);
        return result;
    }

    public String SbidCollectCall(String orderRef, String collectUrl) throws IOException {
        String url = collectUrl;

        HttpClient httpClient = _getNewHttpClient();

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setEntity(new StringEntity("{ \"orderRef\": \""+orderRef+"\" }"));

        HttpResponse httpResponse = PerformHttpPost(httpClient, httpPost, httpContext);
        if(cookieStore.getCookies().size() > 0){
            Log.d(Constants.TAG_SBID_AUTH, "Got some cookies!");
        }
        String result = HttpResponseToString(httpResponse);
        Log.d(Constants.TAG_SBID_AUTH, result);
        return result;
    }

    public String SbidGetCompleteCall(String completeUrl) throws IOException {
        String url = completeUrl;

        HttpClient httpClient = _getNewHttpClient();

        HttpGet httpGet = new HttpGet(url);

        HttpResponse httpResponse = PerformHttpGet(httpClient, httpGet, httpContext);
        String result = HttpResponseToString(httpResponse);
        Log.d(Constants.TAG_SBID_AUTH, result);
        return result;
    }

    public String SignicatVerifyCall(String samlString, String target) throws IOException {
        String url = "https://labs.signicat.com/catwalk/saml/getattributes";

        HttpClient httpClient = _getNewHttpClient();

        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("SAMLResponse", samlString));
        nameValuePairs.add(new BasicNameValuePair("target", target));
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        //httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        Log.d(Constants.TAG_SBID_AUTH + "****", httpClient.getParams().toString());

        HttpResponse httpResponse = PerformHttpPost(httpClient, httpPost, httpContext);
        if(cookieStore.getCookies().size() > 0){
            Log.d(Constants.TAG_SBID_AUTH, "Got some cookies!");
        }
        String result = HttpResponseToString(httpResponse);
        Log.d(Constants.TAG_SBID_AUTH, result);
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

//    private static HttpClient GetBasicHttpClient(){
//        HttpParams httpParams = new BasicHttpParams();
//        HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
//        HttpConnectionParams.setSoTimeout(httpParams, 30000);
//        HttpClient httpClient = new DefaultHttpClient(httpParams);
//        return httpClient;
//    }

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

//    private static HttpClient createHttpsClient()
//    {
//        HttpParams params = new BasicHttpParams();
//        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
//        HttpProtocolParams.setUseExpectContinue(params, true);
//
//        SchemeRegistry schReg = new SchemeRegistry();
//        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
//        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
//
//        return new DefaultHttpClient(conMgr, params);
//    }

    public static HttpClient _getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            DefaultHttpClient http = new DefaultHttpClient(ccm, params);
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("jk", "jk");
            AuthScope authScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT);
            http.getCredentialsProvider().setCredentials(authScope, credentials);

            return http;
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public static HttpClient getHttpClientForVerifyCall(String saml, String target) {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            params.setParameter("SAMLResponse", saml);
            params.setParameter("target", target);
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            DefaultHttpClient http = new DefaultHttpClient(ccm, params);
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("jk", "jk");
            AuthScope authScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT);
            http.getCredentialsProvider().setCredentials(authScope, credentials);

            return http;
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }
}
