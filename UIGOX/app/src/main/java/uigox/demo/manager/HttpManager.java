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


package uigox.demo.manager;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import apijson.JSONRequest;
import okhttp3.ResponseBody;
import uigo.x.HttpManager.OnHttpResponseListener;

import javax.net.ssl.SSLSocketFactory;

import apijson.JSON;
import apijson.Log;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import uigo.x.InputUtil;
import uigo.x.SSLUtil;
import uigo.x.UIAutoApp;
import uigox.demo.application.DemoApplication;
import zuo.biao.library.util.StringUtil;

/**HTTP请求管理类
 * @author Lemon
 * @use HttpManager.getInstance().get(...)或HttpManager.getInstance().post(...)  > 在回调方法onHttpRequestSuccess和onHttpRequestError处理HTTP请求结果
 * @must 解决getToken，getResponseCode，getResponseData中的TODO
 */
public class HttpManager {
	private static final String TAG = "HttpManager";

	private Context context;
	private SSLSocketFactory socketFactory;// 单例
	private HttpManager(Context context) {
		this.context = context;

		try {
			socketFactory = SSLUtil.getSSLSocketFactory(context.getAssets().open("demo.cer"));
		} catch (Exception e) {
			Log.e(TAG, "HttpManager  try {" +
					"  socketFactory = SSLUtil.getSSLSocketFactory(context.getAssets().open(\"demo.cer\"));\n" +
					"\t\t} catch (Exception e) {\n" + e.getMessage());
		}
	}

	private static HttpManager instance = null;
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
		if (StringUtil.isEmpty(url)) {
			Log.e(TAG, "getHttpClient  StringUtil.isEmpty(url) >> return null;");
			return null;
		}

		OkHttpClient.Builder builder = new OkHttpClient.Builder()
				.connectTimeout(15, TimeUnit.SECONDS)
				.writeTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS)
				.cookieJar(new CookieJar() {
					@Override
					public List<Cookie> loadForRequest(HttpUrl url) {
						String host = url == null ? null : url.host();
						Map<String, List<String>> map = host == null ? null : JSON.parseObject(getCookie(host), HashMap.class);
						if (map == null) {
							map = new HashMap<>();
						}
						List<String> idList = new ArrayList<String>();
						idList.add(UIAutoApp.getInstance().getDelegateId());
						map.put("Apijson-Delegate-Id", idList);

						List<Cookie> list = new ArrayList<>();

						Set<Map.Entry<String, List<String>>> set = map == null ? null : map.entrySet();
						if (set != null) {
							for (Map.Entry<String, List<String>> entry : set) {
								String key = entry == null ? null : entry.getKey();
								List<String> hList = key == null ? null : entry.getValue();
								if (hList != null && ! hList.isEmpty()) {
									for (String h : hList) {
										list.add(new Cookie.Builder().domain(host).name(key).value(h).build());
									}
								}
							}
						}

						return list;
					}

					@Override
					public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
						Map<String, List<String>> map = new LinkedHashMap<>();
						if (cookies != null) {
							for (Cookie c : cookies) {
								String cookie = c == null || c.name() == null ? null : c.value();
								if (! TextUtils.isEmpty(cookie)) {
									List<String> cList = new ArrayList<String>();
									cList.add(cookie);
									map.put(c.name(), cList);
								}
							}

							List<String> idList = map.get("Apijson-Delegate-Id");
							if (idList != null && idList.isEmpty() == false) {
								String id = idList.get(0);
								int start = id.indexOf("JSESSIONID=");
								int end = id.indexOf(";");
								id = id.substring((start < 0 ? 0 : start) + "JSESSIONID=".length(), end < 0 ? id.length() : end);
								UIAutoApp.getInstance().setDelegateId(StringUtil.getTrimedString(id));
							}
						}
						saveCookie(url == null ? null : url.host(), JSON.toJSONString(map));//default constructor not found  cookies));
					}

				});

		//添加信任https证书,用于自签名,不需要可删除
		if (url.startsWith(StringUtil.URL_PREFIXs) && socketFactory != null) {
			builder.sslSocketFactory(socketFactory);
		}

		return builder.build();
	}


	/**
	 * @param key
	 * @must demo_***改为服务器设定值
	 * @return
	 */
	public String getToken(String key) {
		String k = KEY_COOKIE + ":" + key;
		return context.getSharedPreferences(KEY_TOKEN, Context.MODE_PRIVATE).getString(k, "");
	}
	/**
	 * @param key
	 * @param value
	 */
	public void saveToken(String key, String value) {
		String k = KEY_COOKIE + ":" + key;
		context.getSharedPreferences(KEY_TOKEN, Context.MODE_PRIVATE)
				.edit()
				.remove(k)
				.putString(k, value)
				.commit();
	}


	/**
	 * @return
	 */
	public String getCookie(String key) {
		String k = KEY_COOKIE + ":" + key;
		return context.getSharedPreferences(KEY_COOKIE, Context.MODE_PRIVATE).getString(k, "");
	}
	/**
	 * @param value
	 */
	public void saveCookie(String key, String value) {
		String k = KEY_COOKIE + ":" + key;
		context.getSharedPreferences(KEY_COOKIE, Context.MODE_PRIVATE)
				.edit()
				.remove(k)
				.putString(k, value)
				.commit();
	}

	/**
	 * @param client
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private Response getResponse(OkHttpClient client, Request request) throws Exception {
		if (client == null || request == null) {
			Log.e(TAG, "getResponseJson  client == null || request == null >> return null;");
			return null;
		}
		return client.newCall(request).execute();
	}

	/**
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private String getResponseBody(Response response) throws Exception {
		ResponseBody body = response == null || ! response.isSuccessful() ? null : response.body();
		return body == null ? null : body.string();
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


}