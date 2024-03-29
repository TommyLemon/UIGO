/*Copyright ©2016 TommyLemon(https://github.com/TommyLemon)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/


package apijson.demo.manager;

import static uiauto.HttpManager.getResponse;
import static uiauto.HttpManager.getResponseBody;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;

import uiauto.InputUtil;
import uiauto.UIAutoApp;
import zuo.biao.apijson.JSONRequest;
import zuo.biao.apijson.StringUtil;
import zuo.biao.library.manager.HttpManager.OnHttpResponseListener;
import zuo.biao.library.util.Log;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import apijson.demo.application.DemoApplication;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

/**HTTP请求管理类
 * @author Lemon
 * @use HttpManager.getInstance().get(...)或HttpManager.getInstance().post(...)  > 在回调方法onHttpRequestSuccess和onHttpRequestError处理HTTP请求结果
 * @must 解决getToken，getResponseCode，getResponseData中的TODO
 */
public class HttpManager {
	private static final String TAG = "HttpManager";


	private Context context;
	private static HttpManager instance;// 单例
	private HttpManager(Context context) {
		this.context = context;

	}

	public static HttpManager getInstance() {
		if (instance == null) {
			synchronized (HttpManager.class) {
				if (instance == null) {
					instance = new HttpManager(DemoApplication.getInstance());
				}
			}
		}
		return instance;
	}



	/**
	 * 列表首页页码。有些服务器设置为1，即列表页码从1开始
	 */
	public static final int PAGE_NUM_0 = 0;

	public static final String KEY_TOKEN = "token";
	public static final String KEY_COOKIE = "cookie";


	public static final MediaType TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

	/**POST请求
	 * @param url_ 接口url
	 * @param request 请求
	 * @param requestCode
	 *            请求码，类似onActivityResult中请求码，当同一activity中以实现接口方式发起多个网络请求时，请求结束后都会回调
	 *            {@link OnHttpResponseListener#onHttpResponse(int, String, Exception)}<br/>
	 *            在发起请求的类中可以用requestCode来区分各个请求
	 * @param listener
	 */
	public void post(final String url_, final com.alibaba.fastjson.JSONObject request
			, final int requestCode, final OnHttpResponseListener listener) {
		String tag = request == null ? null : request.getString(JSONRequest.KEY_TAG);

		new AsyncTask<Void, Void, Exception>() {

			String httpRequestString;

			int responseCode;
			String responseBody;
			Headers responseHeaders;
			@Override
			protected Exception doInBackground(Void... params) {
				try {
					String url = UIAutoApp.getInstance().getHttpUrl(url_);

					String token = getToken(url);

					OkHttpClient client = getHttpClient(url);
					if (client == null) {
						return new Exception(TAG + ".post  AsyncTask.doInBackground  client == null >> return;");
					}

					String reqStr = JSON.toJSONString(request);
					RequestBody requestBody = RequestBody.create(TYPE_JSON, reqStr);

					Request httpRequest = new Request.Builder()
							.addHeader(KEY_TOKEN, token).url(url)
							.post(requestBody).build();

					httpRequestString = reqStr;

					UIAutoApp.getInstance().post(new Runnable() {
						@Override
						public void run() {
							UIAutoApp.getInstance().onHTTPEvent(
									InputUtil.HTTP_ACTION_POST, "JSON"
									, "POST", "http://apijson.cn:8080", url_ + (tag == null ? "" : "/" + tag)
									, httpRequest == null ? null : httpRequest.headers().toString(), reqStr, null
									, getActivity(listener), getFragment(listener)
							);
						}
					});

					Response httpResponse = getResponse(client, httpRequest);
					responseBody = getResponseBody(httpResponse);
					responseCode = httpResponse.code();
					responseHeaders = httpResponse.headers();

				} catch (Exception e) {
					android.util.Log.e(TAG, "post  AsyncTask.doInBackground  try {  result = getResponseJson(..." +
							"} catch (Exception e) {\n" + e.getMessage());
					return e;
				}

				return null;
			}

			@Override
			protected void onPostExecute(Exception e) {
				super.onPostExecute(e);
				listener.onHttpResponse(requestCode, responseBody, e);

				UIAutoApp.getInstance().post(new Runnable() {
					@Override
					public void run() {
						UIAutoApp.getInstance().onHTTPEvent(
								- InputUtil.HTTP_ACTION_POST, e == null ? ("" + responseCode) : e.getClass().getSimpleName()
								, "POST", "http://apijson.cn:8080", url_ + (tag == null ? "" : "/" + tag)
								, responseHeaders == null ? null : responseHeaders.toString(), httpRequestString, responseBody
								, getActivity(listener), getFragment(listener)
						);
					}
				});
			}


		}.execute();
	}

	private String toHttpJSONString(String headers, String body) {
		return headers + "Content: \n" + body;
	}

	private Activity getActivity(OnHttpResponseListener listener) {
		if (listener instanceof Activity) {
			return  (Activity)listener;
		}

		if (listener instanceof Fragment) {
			Fragment fragment = (Fragment) listener;
			return fragment.getActivity();
		}

		return null;
	}

	private Fragment getFragment(OnHttpResponseListener listener) {
		return listener instanceof Fragment ? (Fragment)listener : null;
	}

	//httpGet/httpPost 内调用方法 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	/**
	 * @param url
	 * @return
	 */
	private OkHttpClient getHttpClient(String url) {
		Log.i(TAG, "getHttpClient  url = " + url);
		if (StringUtil.isNotEmpty(url, true) == false) {
			Log.e(TAG, "getHttpClient  StringUtil.isNotEmpty(url, true) == false >> return null;");
			return null;
		}

		OkHttpClient client = new OkHttpClient();
		client.setCookieHandler(new HttpHead());
		client.setConnectTimeout(15, TimeUnit.SECONDS);
		client.setWriteTimeout(10, TimeUnit.SECONDS);
		client.setReadTimeout(10, TimeUnit.SECONDS);

		return client;
	}

	/**
	 * @param tag
	 * @must demo_***改为服务器设定值
	 * @return
	 */
	public String getToken(String tag) {
		return context.getSharedPreferences(KEY_TOKEN, Context.MODE_PRIVATE).getString(KEY_TOKEN + tag, "");
	}
	/**
	 * @param tag
	 * @param value
	 */
	public void saveToken(String tag, String value) {
		context.getSharedPreferences(KEY_TOKEN, Context.MODE_PRIVATE)
				.edit()
				.remove(KEY_TOKEN + tag)
				.putString(KEY_TOKEN + tag, value)
				.commit();
	}


	/**
	 * @return
	 */
	public String getCookie() {
		return context.getSharedPreferences(KEY_COOKIE, Context.MODE_PRIVATE).getString(KEY_COOKIE, "");
	}
	/**
	 * @param value
	 */
	public void saveCookie(String value) {
		context.getSharedPreferences(KEY_COOKIE, Context.MODE_PRIVATE)
				.edit()
				.remove(KEY_COOKIE)
				.putString(KEY_COOKIE, value)
				.commit();
	}


	/**
	 * @param client
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private String getResponseJson(OkHttpClient client, Request request) throws Exception {
		if (client == null || request == null) {
			Log.e(TAG, "getResponseJson  client == null || request == null >> return null;");
			return null;
		}
		Response response = client.newCall(request).execute();
		return response.isSuccessful() ? response.body().string() : null;
	}

	/**从object中获取key对应的值
	 * *获取如果T是基本类型容易崩溃，所以需要try-catch
	 * @param json
	 * @param key
	 * @return
	 * @throws JSONException
	 */
	public <T> T getValue(String json, String key) throws JSONException {
		return getValue(JSON.parseObject(json), key);
	}
	/**从object中获取key对应的值
	 * *获取如果T是基本类型容易崩溃，所以需要try-catch
	 * @param object
	 * @param key
	 * @return
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue(JSONObject object, String key) throws JSONException {
		return (T) object.get(key);
	}

	//httpGet/httpPost 内调用方法 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>




	/**http请求头
	 */
	public class HttpHead extends CookieHandler {
		public HttpHead() {
		}

		@Override
		public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) throws IOException {
			String cookie = getCookie();
			Map<String, List<String>> map = new HashMap<String, List<String>>();
			map.putAll(requestHeaders);
			if (!TextUtils.isEmpty(cookie)) {
				List<String> cList = new ArrayList<String>();
				cList.add(cookie);
				map.put("Cookie", cList);

				List<String> idList = new ArrayList<String>();
				idList.add(UIAutoApp.getInstance().getDelegateId());
				map.put("Apijson-Delegate-Id", idList);
			}
			return map;
		}

		@Override
		public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {
			List<String> list = responseHeaders.get("Set-Cookie");
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					String cookie = list.get(i);
					if (cookie.startsWith("JSESSIONID")) {
						saveCookie(cookie);
						break;
					}
				}
			}

			List<String> idList = responseHeaders.get("Apijson-Delegate-Id");
			if (idList != null && idList.isEmpty() == false) {
				String id = idList.get(0);
				int start = id.indexOf("JSESSIONID=");
				int end = id.indexOf(";");
				id = id.substring((start < 0 ? 0 : start) + "JSESSIONID=".length(), end < 0 ? id.length() : end);
				UIAutoApp.getInstance().setDelegateId(StringUtil.getTrimedString(id));
			}
		}

	}


}