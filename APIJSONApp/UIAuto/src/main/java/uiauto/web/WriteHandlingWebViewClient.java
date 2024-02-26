package uiauto.web;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uiauto.InputUtil;
import uiauto.StringUtil;
import uiauto.UIAutoApp;
import unitauto.JSON;


public class WriteHandlingWebViewClient extends WebViewClient {

    private static final String TAG = "WriteHandlingWebViewClient";

    private static UIAutoApp APP = UIAutoApp.getInstance();

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
        webView.addJavascriptInterface(ajaxInterface, "interception");
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
//        Map<String, List<JSONObject>> reqMap = dataReqMap.get(url);
//        if (reqMap == null || reqMap.isEmpty()) {
//            return;
//        }
//
//        APP.onUIEvent(InputUtil.UI_ACTION_CREATE, activity, activity, fragment, webView, url);
        initWeb(url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Map<String, List<JSONObject>> reqMap = dataReqMap.get(url);
        Set<Map.Entry<String, List<JSONObject>>> set = reqMap == null || reqMap.isEmpty() ? null : reqMap.entrySet();
        if (set == null) { // || set.isEmpty()) {
            APP.onUIEvent(InputUtil.UI_ACTION_RESUME, activity, activity, fragment, null, webView, url);
//        inject();
            return;
        }

        List<String> toRemoveList = new ArrayList<>();

        for (Map.Entry<String, List<JSONObject>> ety : set) {
            String key = ety == null ? null : ety.getKey();
            List<JSONObject> list = key == null ? null : ety.getValue();

            JSONObject first = null;
            String url_ = null;
            while (list != null && list.isEmpty() == false) {
                first = list.remove(0);
                url_ = first == null || first.isEmpty() ? null : first.getString("url");
                if (StringUtil.isNotEmpty(url_, true)) {
                    break;
                }
            }

            if (StringUtil.isEmpty(url_, true)) {
                toRemoveList.add(key);
                continue;
            }

            String method = first.getString("method");
//            String format = first.getString("format");
            String host = first.getString("host");
            String path = first.getString("path");
            String finalHeaders = first.getString("header");
            String finalRequestBody = first.getString("body");

//            APP.post(new Runnable() {
//                @Override
//                public void run() {
//                    APP.onHTTPEvent(
//                            InputUtil.getHTTPActionCode(method), "200"
//                            , method, host, path
//                            , finalHeaders, finalRequestBody, null
//                            , activity, fragment
//                    );
//                }
//            });

            break;
        }

//        if (toRemoveList.size() >= reqMap.size()) {
//            reqMap = null;
//        } else {
            for (String k : toRemoveList) {
                reqMap.remove(k);
            }
//        }

//        if (reqMap == null || reqMap.isEmpty()) {
//            dataReqMap.remove(url);
//        }

    }

    @Override
    public void onPageCommitVisible(WebView view, String url) {
        super.onPageCommitVisible(view, url);
//				APP.onUIEvent(InputUtil.UI_ACTION_RESUME, activity, activity, fragment, webView, url);
    }

    public void initWeb(String webUrl) {
        APP.initWeb(activity, fragment, webView, webUrl);
    }

    /*
            This here is the "fixed" shouldInterceptRequest method that you should override.
            It receives a WriteHandlingWebResourceRequest instead of a WebResourceRequest.
             */
    public WebResourceResponse shouldInterceptRequest(WebView view, WriteHandlingWebResourceRequest request){
        return null;
    }

    Map<String, Map<String, List<JSONObject>>> dataReqMap = new LinkedHashMap<>();
    @Override
    public WebResourceResponse shouldInterceptRequest(final WebView view, WebResourceRequest request) {
        String requestBody = null;
        Uri uri = request.getUrl();

        String requestID = isAjaxRequest(request) ? getAjaxRequestID(request) : null;
        if (StringUtil.isNotEmpty(requestID, true)){
            try {
                requestBody = getAjaxRequestBodyByID(requestID);
                uri = getOriginalRequestUri(request, MARKER);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        int port = uri.getPort();
        String host = uri.getScheme() + "://" + uri.getHost() + port;
        String path = uri.getPath();

        String url = host + (path.startsWith("/") ? path : "/" + path);

        boolean isPage = url.endsWith(".htm") || url.endsWith(".html") || url.endsWith(".xml")
                || url.endsWith(".php") || url.endsWith(".jsp") || url.endsWith(".asp")
                || path.lastIndexOf(".") <= path.lastIndexOf("/");
        boolean isJson = false;
        if (StringUtil.isNotEmpty(requestID, true)) {
            Map<String, String> headerMap = request == null ? null : request.getRequestHeaders();
            if (headerMap != null && headerMap.isEmpty() == false) {
                String method = request.getMethod();
                String contentType = null;
                String headers = null;

                StringBuilder sb = new StringBuilder();
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

                if (isJson) {
                    isPage = false;

                    headers = sb.toString();

                    String format = contentType == null ? null : (contentType.contains("application/json")
                            ? "JSON" : (contentType.contains("form-data") ? "DATA" : (contentType.contains("form") ? "FORM" : "PARAM"))
                    );

                    Map<String, List<JSONObject>> reqMap = dataReqMap.get(url);
                    if (reqMap == null) {
                        reqMap = new LinkedHashMap<>();
                    }
                    List<JSONObject> reqList = reqMap.get(requestID);
                    if (reqList == null) {
                        reqList = new ArrayList<>();
                    }
                    JSONObject req = new JSONObject(true);
                    req.put("id", requestID);
                    req.put("method", method);
                    req.put("format", format);
                    req.put("host", host);
                    req.put("path", path);
                    req.put("url", url);
                    req.put("header", headers);
                    req.put("body", requestBody);

                    reqList.add(req);
                    reqMap.put(requestID, reqList);
                    dataReqMap.put(url, reqMap);

                    String finalHeaders = headers;
                    String finalRequestBody = requestBody;
                    APP.post(new Runnable() {
                        @Override
                        public void run() {
                            APP.onHTTPEvent(
                                    InputUtil.getHTTPActionCode(method), format
                                    , method, host, path
                                    , finalHeaders, finalRequestBody, null
                                    , activity, fragment
                            );
                        }
                    });

                    //				APP.post(new Runnable() {
                    //					@Override
                    //					public void run() {
                    //						APP.onHTTPEvent(
                    //								InputUtil.HTTP_ACTION_RESPONSE, ("" + responseCode)
                    //								, "POST", "http://apijson.cn:8080", url_
                    //								, responseHeaders == null ? null : responseHeaders.toString(), httpRequestString, responseBody
                    //								, getActivity(listener), getFragment(listener)
                    //						);
                    //					}
                    //				});

                }
            }
        }

        if (isPage) {
            Map<String, List<JSONObject>> reqMap = dataReqMap.get(url);
            if (reqMap == null) { // || reqMap.isEmpty()) {
//                APP.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        APP.onUIEvent(InputUtil.UI_ACTION_CREATE, activity, activity, fragment, webView, url);
//                    }
//                });
            }

            WebResourceResponse webResourceResponse = shouldInterceptRequest(
                    view,
                    new WriteHandlingWebResourceRequest(request, requestBody, uri)
            );
            return webResourceResponse == null ? null : injectIntercept(webResourceResponse, view.getContext());

        }

        return super.shouldInterceptRequest(webView, request);
    }

    public void onHttpEvent(int action, String id, String item) {
        JSONObject req = JSON.parseObject(item);
        String method = req.getString("method");
        String format = req.getString("format");
        String status = req.getString("status");
        String url = req.getString("url");

        Uri uri = Uri.parse(url);

        int port = uri.getPort();
        String host = uri.getScheme() + "://" + uri.getHost() + port;
        String path = uri.getPath();

        String reqHeader = req.getString("requestHeader");
        String reqBody = req.getString("postData");

        String resHeader = req.getString("responseHeader");
        String resBody = req.getString("response");

        boolean isRes = action > 0 && action != InputUtil.HTTP_ACTION_RESPONSE;

        APP.post(new Runnable() {
            @Override
            public void run() {
                APP.onHTTPEvent(
                        (isRes ? -1 : 1)*InputUtil.getHTTPActionCode(method), isRes ? format : status
                        , method, host, path
                        , reqHeader, reqBody, resBody
                        , activity, fragment
                );
            }
        });
    }

    public void onEditEvent(String id, int selectionStart, int selectionEnd, String text) {
        APP.addWebEditTextEvent(activity, fragment, webView, id, selectionStart, selectionEnd, text, touchX, touchY);
    }
    public void onKeyEvent(String id, int action, String key, int keyCode) { // 能拦截到 ENTER, TAB, ESC 等
        long time = SystemClock.uptimeMillis();
        if (keyCode == 13 || "Enter".equalsIgnoreCase(key)) {
            keyCode = KeyEvent.KEYCODE_ENTER;
        }
        else if (keyCode == 9 || "Tab".equalsIgnoreCase(key)) {
            keyCode = KeyEvent.KEYCODE_TAB;
        }
        else if (keyCode == 27 || "Escape".equalsIgnoreCase(key)) {
            keyCode = KeyEvent.KEYCODE_ESCAPE;
        }
        else {
            return;
        }

        KeyEvent event = new KeyEvent(time, time, action, keyCode, 1);

        if (action == KeyEvent.ACTION_DOWN) {
            UIAutoApp.getInstance().onKeyDown(keyCode, event, activity, fragment);
        }
        else if (action == KeyEvent.ACTION_UP) {
            UIAutoApp.getInstance().onKeyUp(keyCode, event, activity, fragment);
        }
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) { // 能拦截到 BACK, MENU, HOME 等
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            UIAutoApp.getInstance().onKeyDown(event.getKeyCode(), event, activity, fragment);
        }
        else if (event.getAction() == KeyEvent.ACTION_UP) {
            UIAutoApp.getInstance().onKeyUp(event.getKeyCode(), event, activity, fragment);
        }
        return super.shouldOverrideKeyEvent(view, event);
    }

    String touchId;
    Integer touchX;
    Integer touchY;
    public void onTouchEvent(String id, Integer touchX, Integer touchY) {
        this.touchId = id;
        this.touchX = touchX;
        this.touchY = touchY;
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
        String encoding = response == null ? "UTF-8" : response.getEncoding();
        String mime = response == null ? "text/html" : response.getMimeType();
        InputStream responseData = response == null ? null : response.getData();
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