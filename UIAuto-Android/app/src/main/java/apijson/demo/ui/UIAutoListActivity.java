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

package apijson.demo.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apijson.demo.HttpManager;
import apijson.demo.InputUtil;
import apijson.demo.R;
import apijson.demo.StringUtil;
import apijson.demo.application.DemoApplication;
import zuo.biao.apijson.JSON;
import zuo.biao.apijson.JSONRequest;
import zuo.biao.apijson.JSONResponse;


/** 操作流程 Flow /操作步骤 Input 列表
 * https://github.com/TommyLemon/UIAuto
 * @author Lemon
 */
public class UIAutoListActivity extends Activity implements HttpManager.OnHttpResponseListener {
    public static final String TAG = "UIAutoListActivity";

    public static final String INTENT_IS_LOCAL = "INTENT_IS_LOCAL";
    public static final String INTENT_FLOW_ID = "INTENT_FLOW_ID";
    public static final String INTENT_EVENT_LIST = "INTENT_EVENT_LIST";
    public static final String INTENT_TEMP_KEY = "INTENT_TEMP_KEY";

    public static final String RESULT_LIST = "RESULT_LIST";

    /**
     * @param context
     * @return
     */
    public static Intent createIntent(Context context, boolean isLocal) {
        return new Intent(context, UIAutoListActivity.class).putExtra(INTENT_IS_LOCAL, isLocal);
    }

    /**
     * @param context
     * @return
     */
    public static Intent createIntent(Context context, long flowId) {
        return new Intent(context, UIAutoListActivity.class).putExtra(INTENT_FLOW_ID, flowId);
    }

    /**
     * @param context
     * @return
     */
    public static Intent createIntent(Context context, String tempKey) {
        return createIntent(context, true).putExtra(INTENT_TEMP_KEY, tempKey);
    }

    public static final String CACHE_FLOW = "CACHE_FLOW";
    public static final String CACHE_TOUCH = "KEY_TOUCH";


    private Activity context;

    private long deviceId = 0;
    private long systemId = 0;
    private long flowId = 0;
    private boolean isTouch = false;
    private boolean isLocal = false;
    private boolean hasTempTouchList = false;
    private JSONArray eventList = null;

    private EditText etUIAutoListName;
    private TextView tvUIAutoListCount;
    private ListView lvUIAutoList;
    // private View llUIAutoListBar;

    private View btnUIAutoListReplay;
    private ProgressBar pbUIAutoList;
    private EditText etUIAutoListUrl;
    private Button btnUIAutoListGet;

    SharedPreferences cache;
    String cacheKey;
    String tempKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_auto_list_activity);

        context = this;
        cache = DemoApplication.getInstance().getSharedPreferences();

        isLocal = getIntent().getBooleanExtra(INTENT_IS_LOCAL, isLocal);
        flowId = getIntent().getLongExtra(INTENT_FLOW_ID, flowId);
        tempKey = getIntent().getStringExtra(INTENT_TEMP_KEY);
        if (StringUtil.isNotEmpty(tempKey, true)) {
            eventList = JSON.parseArray(cache.getString(tempKey, null));
        }

        hasTempTouchList = eventList != null && eventList.isEmpty() == false;
        isTouch = flowId > 0 || hasTempTouchList;

        cacheKey = isTouch ? CACHE_TOUCH : CACHE_FLOW;
        if (isLocal) {
            JSONArray allList = JSON.parseArray(cache.getString(cacheKey, null));

            if (hasTempTouchList) {
                if (allList == null || allList.isEmpty()) {
                    allList = eventList;
                }
                else {
                    allList.addAll(eventList);
                }
                cache.edit().remove(cacheKey).putString(cacheKey, JSON.toJSONString(allList)).apply();
            }
            else {
                hasTempTouchList = true;
                if (flowId == 0) {
                    eventList = allList;
                } else {
                    eventList = new JSONArray();
                    if (allList != null) {
                        for (int i = 0; i < allList.size(); i++) {
                            JSONObject obj = allList.getJSONObject(i);
                            if (obj != null && obj.getLongValue("flowId") == flowId) {
                                eventList.add(obj);
                            }
                        }
                    }
                }
            }
        }


        etUIAutoListName = findViewById(R.id.etUIAutoListName);
        tvUIAutoListCount = findViewById(R.id.tvUIAutoListCount);
        lvUIAutoList = findViewById(R.id.lvUIAutoList);
        // llUIAutoListBar = findViewById(R.id.llUIAutoListBar);

        btnUIAutoListReplay = findViewById(R.id.btnUIAutoListReplay);
        pbUIAutoList = findViewById(R.id.pbUIAutoList);
        etUIAutoListUrl = findViewById(R.id.etUIAutoListUrl);
        btnUIAutoListGet = findViewById(R.id.btnUIAutoListGet);

        btnUIAutoListReplay.setVisibility(isTouch ? View.VISIBLE : View.GONE);
        etUIAutoListName.setVisibility(isTouch ? View.VISIBLE : View.GONE);
        etUIAutoListName.setEnabled(isLocal || hasTempTouchList);
//        llUIAutoListBar.setVisibility(isLocal ? View.GONE : View.VISIBLE);

        int count = eventList == null ? 0 : eventList.size();
        tvUIAutoListCount.setText((isLocal ? "0" : count + "/") + count);
        btnUIAutoListGet.setText(isLocal ? "post" : "get");

        lvUIAutoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (array != null) {
                    JSONObject obj = array.getJSONObject(position);
                    if (isTouch) {
//                        setResult(RESULT_OK, new Intent().putExtra(RESULT_LIST, JSON.toJSONString(obj)));
//                        finish();
                    }
                    else {
                        startActivityForResult(UIAutoListActivity.createIntent(context, obj == null ? 0 : obj.getLongValue("id")), REQUEST_EVENT_LIST);
                    }
                }
            }
        });



        if (hasTempTouchList) {
            showList(eventList);
        } else {
            send(btnUIAutoListGet);
        }
    }


    private ArrayAdapter<String> adapter;
    /** 示例方法 ：显示列表内容
     * @param list
     */
    private void setList(List<String> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int allCount = list == null ? null : list.size();

                tvUIAutoListCount.setText((isLocal ? remoteCount + "/" : allCount + "/") + allCount);
                pbUIAutoList.setVisibility(View.GONE);
                if (adapter == null) {
                    adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, list);
                    lvUIAutoList.setAdapter(adapter);
                } else {
                    adapter.clear();
                    adapter.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void showList(JSONArray array) {
        this.array = array;
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> list = new ArrayList<>();
                if (array != null) {
                    for (int i = 0; i < array.size(); i++) {

                        JSONObject obj = array.getJSONObject(i);
                        if (obj == null) {
                            obj = new JSONObject();
                        }

                        String state = statueMap.get(obj);
                        if (StringUtil.isEmpty(state, true)) {
                            state = "Local";
                        }

                        if (isTouch) {
                            int type = obj.getIntValue("type");
                            int action = obj.getIntValue("action");

                            if (type == InputUtil.EVENT_TYPE_TOUCH) {
                                list.add("[" + state + "]  " + new Date(obj.getLongValue("time")).toLocaleString() + "    " + InputUtil.getTouchActionName(action)
                                        + "\npointerCount: " + obj.getString("pointerCount") + ",        x: " + obj.getString("x") + ", y: " + obj.getString("y")
                                        + "\nsplitX: " + obj.getString("splitX") + ", splitY: " + obj.getString("splitY") + "           " + InputUtil.getOrientationName(obj.getIntValue("orientation"))
                                );
                            }
                            else if (type == InputUtil.EVENT_TYPE_KEY) {
                                list.add("[" + state + "]  " + new Date(obj.getLongValue("time")).toLocaleString() + "    " + InputUtil.getKeyActionName(action)
                                        + "\nrepeatCount: " + obj.getString("repeatCount") + ", scanCode: " + InputUtil.getScanCodeName(obj.getIntValue("scanCode")) + "         " + InputUtil.getKeyCodeName(obj.getIntValue("keyCode"))
                                );
                            }
                            else if (type == InputUtil.EVENT_TYPE_UI) {
                                String fragment = obj.getString("fragment");

                                list.add("[" + state + "]  " + new Date(obj.getLongValue("time")).toLocaleString() + "    " + InputUtil.getUIActionName(action)
                                        + "\nactivity: " + obj.getString("activity") + (StringUtil.isEmpty(fragment, true) ? "" : "\nfragment: " + fragment)
                                );
                            }
                            else if (type == InputUtil.EVENT_TYPE_HTTP) {
                                list.add("[" + state + "]  " + new Date(obj.getLongValue("time")).toLocaleString() + "    " + InputUtil.getHTTPActionName(action)
                                        + "\nURL: " + obj.getString("url")
                                        + "\n\nREQUEST: \n" + obj.getString("request")
                                        + (action != InputUtil.HTTP_ACTION_RESPONSE ? "" : "\n\n\nRESPONSE: \n" + obj.getString("response"))
                                        + "\n"
                                );
                            }
                            else {
                                list.add("[" + state + "]" + " " + new Date(obj.getLongValue("time")).toLocaleString() + "UNKNOWN !!!");
                            }
                        } else {
                            list.add("[" + state + "]" + " " + new Date(obj.getLongValue("time")).toLocaleString() + "\n" + obj.getString("name"));
                        }
                    }
                }

                setList(list);
            }
        }).start();
    }



    private int remoteCount = 0;
    private Map<JSONObject, String> statueMap = new HashMap<JSONObject, String>();

    public void send(View v) {
        needLoading = true;

        final String fullUrl = StringUtil.getTrimedString(etUIAutoListUrl) + StringUtil.getString((TextView) v).toLowerCase();

        pbUIAutoList.setVisibility(View.VISIBLE);

        if (hasTempTouchList == false) {
            hasTempTouchList = true;
            cache.edit().remove(cacheKey).putString(cacheKey, JSON.toJSONString(eventList)).apply();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (isLocal) {
                    if (deviceId <= 0 || systemId <= 0 || flowId <= 0) {
                        JSONRequest request = new JSONRequest();

                        if (deviceId <= 0) {
                            {   // Flow <<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                                JSONRequest device = new JSONRequest();
                                device.put("width", 1080);
                                device.put("height", 1920);
                                device.put("brand", "Xiaomi");
                                device.put("model", "MI 8");
                                request.put("Device", device);
                            }   // Flow >>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                            request.setTag("Device");
                        }
                        else if (systemId <= 0) {
                            {   // Flow <<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                                JSONRequest system = new JSONRequest();
                                system.put("type", 0);
                                system.put("brand", "Xiaomi MIUI");
                                system.put("versionCode", 14);
                                system.put("versionName", "14.0");
                                request.put("System", system);
                            }   // Flow >>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                            request.setTag("System");
                        }
                        else {
                            {   // Flow <<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                                JSONRequest flow = new JSONRequest();
                                flow.put("deviceId", 0);
                                flow.put("systemId", 0);
                                flow.put("name", StringUtil.getTrimedString(etUIAutoListName));
                                request.put("Flow", flow);
                            }   // Flow >>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                            request.setTag("Flow");
                        }


                        HttpManager.getInstance().post(fullUrl, request.toString(), new HttpManager.OnHttpResponseListener() {
                            @Override
                            public void onHttpResponse(int requestCode, String resultJson, Exception e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pbUIAutoList.setVisibility(View.GONE);
                                    }
                                });

                                JSONResponse response = new JSONResponse(resultJson);
                                if (response.isSuccess()) {

                                    if (deviceId <= 0) {
                                        JSONResponse resp = response.getJSONResponse("Device");
                                        deviceId = resp == null ? 0 : resp.getId();
                                        if (deviceId > 0) {
                                            send(v);
                                        }
                                    }
                                    else if (systemId <= 0) {
                                        JSONResponse resp = response.getJSONResponse("System");
                                        systemId = resp == null ? 0 : resp.getId();
                                        if (systemId > 0) {
                                            send(v);
                                        }
                                    }
                                    else {
                                        JSONResponse resp = response.getJSONResponse("Flow");
                                        flowId = resp == null ? 0 : resp.getId();
                                        if (flowId > 0) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    etUIAutoListName.setEnabled(false);
                                                    send(v);
                                                }
                                            });
                                        }
                                    }
                                }
                                else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context, "Upload Device/System/Flow failed! " + response.getMsg(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        });

                        return;
                    }


                    if (eventList == null || eventList.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pbUIAutoList.setVisibility(View.GONE);
                                Toast.makeText(context, "All is uploaded!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        for (int i = 0; i < eventList.size(); i++) {
                            JSONObject input = eventList.getJSONObject(i);
                            if (input == null || input.getLongValue("id") > 0) {
                                continue;
                            }

                            String state = statueMap.get(input);
                            if ("Remote".equals(state) || "Uploading".equals(state)) {
                                continue;
                            }

                            statueMap.put(input, "Uploading");

                            JSONObject obj = JSON.parseObject(JSON.toJSONString(input));
                            obj.remove("id");
                            obj.put("flowId", flowId);

                            if (obj.get("deviceId") == null) {
                                obj.put("deviceId", 1);
                            }
                            if (obj.get("x") == null) {
                                obj.put("x", 0);
                            }
                            if (obj.get("y") == null) {
                                obj.put("y", 0);
                            }

                            JSONRequest request = new JSONRequest();
                            {   // Input <<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                                request.put("Input", obj);
                            }   // Input >>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                            request.setTag("Input");


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pbUIAutoList.setVisibility(View.VISIBLE);
                                }
                            });
                            HttpManager.getInstance().post(fullUrl, request.toString(), new HttpManager.OnHttpResponseListener() {
                                @Override
                                public void onHttpResponse(int requestCode, String resultJson, Exception e) {
                                    JSONResponse response = new JSONResponse(resultJson);
                                    if (response.isSuccess()) {
                                        remoteCount ++;

                                        JSONResponse resp = response.getJSONResponse("Input");
                                        input.put("id", resp == null ? 0 : resp.getId());
                                        statueMap.put(input, "Remote");
                                    }
                                    else {
                                        statueMap.put(input, "Local");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Toast.makeText(context, "Upload Input failed! "
                                                            + (StringUtil.isEmpty(response.getMsg(), true)
                                                            ? (e == null ? "" : e.getMessage())
                                                            : response.getMsg()
                                                    ), Toast.LENGTH_LONG).show();
                                                }
                                                catch (Throwable e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                    showList(array);
                                }
                            });
                        }

                    }
                }
                else {
                    JSONRequest request = new JSONRequest();

                    if (isTouch) {
                        {   // Input[] <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                            JSONRequest touchItem = new JSONRequest();
                            {   // Input <<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                                JSONRequest input = new JSONRequest();
                                input.put("@order", "step+,time+,downTime+,eventTime+");
                                if (flowId > 0) {
                                    input.put("flowId", flowId);
                                }
                                touchItem.put("Input", input);
                            }   // Input >>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                            request.putAll(touchItem.toArray(0, 0, "Input"));
                        }   // Input[] >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                    }
                    else {
                        {   // Flow[] <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                            JSONRequest flowItem = new JSONRequest();
                            {   // Flow <<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                                JSONRequest flow = new JSONRequest();
                                flow.put("@order", "time-");
                                flowItem.put("Flow", flow);
                            }   // Flow >>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                            request.putAll(flowItem.toArray(0, 0, "Flow"));
                        }   // Flow[] >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                    }

                    HttpManager.getInstance().post(fullUrl, request.toString(), UIAutoListActivity.this);
                }

            }
        }).start();
    }

    public void replay(View v) {
        if (isTouch) {
            replay(array);
        } else {
            setResult(RESULT_OK, new Intent().putExtra(RESULT_LIST, JSON.toJSONString(array)));
            finish();
        }
    }

    public void replay(JSONArray eventList) {
        finish();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
              DemoApplication.getInstance().onUIAutoActivityCreate();
              DemoApplication.getInstance().prepareReplay(eventList);
            }
        }, 1000);
    }




    private JSONArray array;
    @Override
    public void onHttpResponse(int requestCode, String resultJson, Exception e) {
        Log.d(TAG, "onHttpResponse  resultJson = " + resultJson);
        if (e != null) {
            Log.e(TAG, "onHttpResponse e = " + e.getMessage());
        }
        JSONResponse response = new JSONResponse(resultJson);
        array = response.getArray(isTouch ? "Input[]" : "Flow[]");
        if (array == null) {
            array = new JSONArray();
        }
        statueMap = new HashMap<>();
        for (int i = 0; i < array.size(); i++) {
            statueMap.put(array.getJSONObject(i), "Remote");
        }

        showList(array);
    }


    private boolean needLoading = false;
    @Override
    public void onBackPressed() {
        int size = eventList == null ? 0 : eventList.size();
        if (needLoading && size > 0) {

            for (int i = 0; i < size; i++) {
                JSONObject obj = eventList.getJSONObject(i);
                if ("Remote".equals(statueMap.get(obj)) == false) {
                    Toast.makeText(this, R.string.remains_step_needs_uploading, Toast.LENGTH_SHORT).show();

                    if (size >= 50 && i < size / 2) {
                        lvUIAutoList.smoothScrollToPositionFromTop(i, 0);
                    } else {
                        lvUIAutoList.smoothScrollToPosition(i);
                    }
                    return;
                }
            }
        }

        super.onBackPressed();
    }

    private static final int REQUEST_EVENT_LIST = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_EVENT_LIST) {
            replay(data == null ? null : JSON.parseArray(data.getStringExtra(UIAutoListActivity.RESULT_LIST)));
        }
    }


    @Override
    protected void onDestroy() {
        cache.edit().remove(tempKey).apply();
        super.onDestroy();
    }
}
