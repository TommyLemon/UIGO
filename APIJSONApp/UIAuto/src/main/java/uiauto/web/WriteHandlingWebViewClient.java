package uiauto.web;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import uiauto.InputUtil;
import uiauto.UIAutoApp;
import unitauto.Log;


public class WriteHandlingWebViewClient extends WebViewClient {

    private static final String TAG = "WriteHandlingWebViewClient";

    private final String MARKER = "AJAXINTERCEPT";
    private final WebView webView;
    private final Fragment fragment;
    private final Activity activity;
    private Map<String, String> ajaxRequestContents = new HashMap<>();


    public WriteHandlingWebViewClient(WebView webView, Activity activity, Fragment fragment) {
        this.webView = webView;
        this.fragment = fragment;
        this.activity = activity != null ? activity : (fragment != null ? fragment.getActivity() : (webView == null ? null : (Activity) webView.getContext()));

        AjaxInterceptJavascriptInterface ajaxInterface = new AjaxInterceptJavascriptInterface(this);
        webView.addJavascriptInterface(ajaxInterface , "interception");
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        UIAutoApp.getInstance().onUIEvent(InputUtil.UI_ACTION_CREATE, activity, activity, fragment, webView, url);
        inject();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        UIAutoApp.getInstance().onUIEvent(InputUtil.UI_ACTION_RESUME, activity, activity, fragment, webView, url);
//        inject();
    }

    @Override
    public void onPageCommitVisible(WebView view, String url) {
        super.onPageCommitVisible(view, url);
//				UIAutoApp.getInstance().onUIEvent(InputUtil.UI_ACTION_RESUME, activity, activity, fragment, webView, url);
    }

    public void inject() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript("function generateRandom() {\n" +
                    "      return Math.floor((1 + Math.random()) * 0x10000)\n" +
                    "        .toString(16)\n" +
                    "        .substring(1);\n" +
                    "    }\n" +
                    "\n" +
                    "\n" +
                    "    // This only works if `open` and `send` are called in a synchronous way\n" +
                    "    // That is, after calling `open`, there must be no other call to `open` or\n" +
                    "    // `send` from another place of the code until the matching `send` is called.\n" +
                    "    requestID = null;\n" +
                    "    XMLHttpRequest.prototype.reallyOpen = XMLHttpRequest.prototype.open;\n" +
                    "    XMLHttpRequest.prototype.open = function(method, url, async, user, password) {\n" +
                    "        requestID = generateRandom()\n" +
                    "        var signed_url = url + \"AJAXINTERCEPT\" + requestID;\n" +
                    "        this.reallyOpen(method, signed_url , async, user, password);\n" +
                    "    };\n" +
                    "    XMLHttpRequest.prototype.reallySend = XMLHttpRequest.prototype.send;\n" +
                    "    XMLHttpRequest.prototype.send = function(body) {\n" +
                    "        interception.customAjax(requestID, body);\n" +
                    "        this.reallySend(body);\n" +
                    "    };" +
                    "    JSON.stringify(document);", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    Log.d(TAG, "wvWebView.evaluateJavascript value = " + value);
                }
            });
        }
    }

    /*
            This here is the "fixed" shouldInterceptRequest method that you should override.
            It receives a WriteHandlingWebResourceRequest instead of a WebResourceRequest.
             */
    public WebResourceResponse shouldInterceptRequest(WebView view, WriteHandlingWebResourceRequest request){
        return null;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(final WebView view, WebResourceRequest request) {
        String requestBody = null;
        Uri uri = request.getUrl();
        if (isAjaxRequest(request)){
            try {
                requestBody = getRequestBody(request);
                uri = getOriginalRequestUri(request, MARKER);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        WebResourceResponse webResourceResponse = shouldInterceptRequest(
                view,
                new WriteHandlingWebResourceRequest(request, requestBody, uri)
        );

        if (request != null) {
            Map<String, String> headerMap = request == null ? null : request.getRequestHeaders();
            if (headerMap != null && headerMap.isEmpty() == false) {
                String method = request.getMethod();
                String contentType = null;
                String headers = null;

                StringBuilder sb = new StringBuilder();
                boolean isJson = false;
                Set<Map.Entry<String, String>> set = headerMap.entrySet();
                for (Map.Entry<String, String> ety : set) {
                    String k = ety.getKey();
                    String v = ety.getValue();
                    if ("accept".equalsIgnoreCase(k)) {
                        if ("GET".equalsIgnoreCase(method) && (v == null || v.toLowerCase().contains("json") == false)) {
                            break;
                        }
                        isJson = true;
                    } else if ("content-type".equalsIgnoreCase(k)) {
                        contentType = v == null ? null : v.toLowerCase();
                    }
                    sb.append(k).append(": ").append(v).append("\n");
                }

                if (isJson == false) {
                    if (webResourceResponse == null) {
                        return webResourceResponse;
                    } else {
                        return injectIntercept(webResourceResponse, view.getContext());
                    }
                }

                headers = sb.toString();

                int port = uri.getPort();
                String host = uri.getScheme() + "://" + uri.getHost() + port;
                String path = uri.getPath();

                String format = contentType == null ? null : (contentType.contains("application/json")
                        ? "JSON" : (contentType.contains("form-data") ? "DATA" : (contentType.contains("form") ? "FORM" : "PARAM"))
                );

                String finalHeaders = headers;
                String finalRequestBody = requestBody;
                UIAutoApp.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        UIAutoApp.getInstance().onHTTPEvent(
                                InputUtil.getHTTPActionCode(method), format
                                , method, host, path
                                , finalHeaders, finalRequestBody, null
                                , activity, fragment
                        );
                    }
                });

                //				UIAutoApp.getInstance().post(new Runnable() {
                //					@Override
                //					public void run() {
                //						UIAutoApp.getInstance().onHTTPEvent(
                //								InputUtil.HTTP_ACTION_RESPONSE, ("" + responseCode)
                //								, "POST", "http://apijson.cn:8080", url_
                //								, responseHeaders == null ? null : responseHeaders.toString(), httpRequestString, responseBody
                //								, getActivity(listener), getFragment(listener)
                //						);
                //					}
                //				});

            }
        }

        if (webResourceResponse == null){
            return webResourceResponse;
        } else {
            return injectIntercept(webResourceResponse, view.getContext());
        }
    }

    void addAjaxRequest(String id, String body){
        ajaxRequestContents.put(id, body);
    }

    private String getRequestBody(WebResourceRequest request){
        String requestID = getAjaxRequestID(request);
        return  getAjaxRequestBodyByID(requestID);
    }

    private boolean isAjaxRequest(WebResourceRequest request){
        return true; // request.getUrl().toString().contains(MARKER);
    }

    private String[] getUrlSegments(WebResourceRequest request, String divider){
        String urlString = request.getUrl().toString();
        return urlString.split(divider);
    }


    private String getAjaxRequestID(WebResourceRequest request) {
        String[] segs = getUrlSegments(request, MARKER);
        return segs == null || segs.length <= 1 ? null : segs[1];
    }

    private Uri getOriginalRequestUri(WebResourceRequest request, String marker){
        String[] segs = getUrlSegments(request, marker);
        String urlString = segs == null || segs.length <= 0 ? null : segs[0];
        return Uri.parse(urlString);
    }

    private String getAjaxRequestBodyByID(String requestID){
        String body = ajaxRequestContents.get(requestID);
        ajaxRequestContents.remove(requestID);
        return body;
    }

    private WebResourceResponse injectIntercept(WebResourceResponse response, Context context){
        String encoding = response.getEncoding();
        String mime = response.getMimeType();
        InputStream responseData = response.getData();
        InputStream injectedResponseData = injectInterceptToStream(
                context,
                responseData,
                mime,
                encoding
        );
        return new WebResourceResponse(mime, encoding, injectedResponseData);
    }

    private InputStream injectInterceptToStream(
            Context context,
            InputStream is,
            String mime,
            String charset
    ) {
        try {
            byte[] pageContents = Utils.consumeInputStream(is);
            if (mime.equals("text/html")) {
                pageContents = AjaxInterceptJavascriptInterface
                        .enableIntercept(context, pageContents)
                        .getBytes(charset);
            }

            return new ByteArrayInputStream(pageContents);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}