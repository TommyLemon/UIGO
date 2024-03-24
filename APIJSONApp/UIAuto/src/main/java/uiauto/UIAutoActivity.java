/*Copyright ©2020 TommyLemon(https://github.com/TommyLemon)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package uiauto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import unitauto.JSON;
import unitauto.apk.UnitAutoActivity;
import zuo.biao.apijson.JSONRequest;
import zuo.biao.apijson.JSONResponse;


/**自动 UI 测试，需要用 UIAuto 发请求到这个设备
 * https://github.com/TommyLemon/UIAuto
 * @author Lemon
 */
public class UIAutoActivity extends UnitAutoActivity {
    private static final String TAG = "UIAutoActivity";

    private static final String INTENT_FLOW_ID = "INTENT_FLOW_ID";
    private static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    private static final String KEY_PASSWORD = "KEY_PASSWORD";

    /**
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        return new Intent(context, UIAutoActivity.class); //.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    private Activity context;
    private long flowId = 0;

    @Override
    protected int getLayoutResId() {
        return R.layout.ui_auto_activity;
    }


    private TextView etUnitProxy;
    private ProgressBar pbUnitProxy;
    private TextView tvUnitProxy;

    private TextView etUIAccount;
    private TextView tvUISignIn;
    private TextView etUIPassword;

    private View pbUISignIn;
    private View pbUISignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        flowId = getIntent().getLongExtra(INTENT_FLOW_ID, flowId);
        isProxy = ! UIAutoApp.getInstance().isProxyEnabled();
        server = UIAutoApp.getInstance().getProxyServer();
        if (StringUtil.isEmpty(server, true)) {
            server = "http://apijson.cn:9090";
        }

        account = cache.getString(KEY_ACCOUNT, "");
        password = cache.getString(KEY_PASSWORD, "");

        etUnitProxy = findViewById(R.id.etUnitProxy);
        pbUnitProxy = findViewById(R.id.pbUnitProxy);
        tvUnitProxy = findViewById(R.id.tvUnitProxy);

        etUIAccount = findViewById(R.id.etUIAccount);
        pbUISignIn = findViewById(R.id.pbUISignIn);
        tvUISignIn = findViewById(R.id.tvUISignIn);

        etUIPassword = findViewById(R.id.etUIPassword);
        pbUISignUp = findViewById(R.id.pbUISignUp);

        pbUISignIn.setVisibility(isSignedIn ? View.VISIBLE : View.GONE);

        etUnitProxy.setText(server);
        etUIAccount.setText(account);
        tvUISignIn.setText(isSignedIn ? R.string.sign_out : R.string.sign_in);
        etUIPassword.setText(password);

        switchProxy(tvUnitProxy);

        tvUnitProxy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchProxy(v);
            }
        });

        if (isSignedIn == false) {
            signIn(tvUISignIn);
        }
    }


    public void onClick(View v) {
        Toast.makeText(context, "onClick BUTTON", Toast.LENGTH_SHORT).show();
        record(v);
//        finish();
    }

    public void toRemote(View v) {
        startActivity(UIAutoListActivity.createIntent(context, false));
    }

    public void toLocal(View v) {
        startActivity(UIAutoListActivity.createIntent(context, true));
    }

    public void admin(View v) {
        startActivity(UnitAutoActivity.createIntent(context));
    }

    protected boolean isProxy = false;
    protected String server = null;
    public void switchProxy(View v) {
        isProxy = ! isProxy;

        etUnitProxy.setEnabled(! isProxy);
        tvUnitProxy.setText(isProxy ? R.string.stop : R.string.start);
        pbUnitProxy.setVisibility(isProxy ? View.VISIBLE : View.GONE);

        server = StringUtil.getTrimedString(etUnitProxy);
        UIAutoApp.getInstance().setHttpProxy(isProxy, server);
    }

    public void record(View v) {
        flowId = - System.currentTimeMillis();

//        cover.setVisibility(View.VISIBLE);
//        showCover(true, context);
//        finish();

        UIAutoApp.getInstance().onUIAutoActivityCreate(this, true);
        UIAutoApp.getInstance().prepareRecord();
        finish();
    }


    private static boolean isSignedIn = false;
    public void signIn(View v) {
        signInOrUp(false, pbUISignIn);
    }

    public void signUp(View v) {
        signInOrUp(true, pbUISignUp);
    }


    private String account;
    private String password;
    private String verify;

    public void signInOrUp(boolean isSignUp, View pb) {
        pb.setVisibility(View.VISIBLE);

        server = StringUtil.getTrimedString(etUnitProxy);
        String act = StringUtil.getTrimedString(etUIAccount);
        String pwd = StringUtil.getTrimedString(etUIPassword);

        account = StringUtil.isEmpty(act, true) ? "13000082001" : act;
        password = StringUtil.isEmpty(pwd, true) ? "123456" : pwd;

        JSONRequest request = new JSONRequest();
        if (isSignUp) {
            if (StringUtil.isEmpty(verify, true)) {
                request.put("type", "1");
                request.put("phone", account);
                String req = request.toJSONString();
                tvUnitRequest.setText(req);

                HttpManager.getInstance().post(server + "/post/verify", req, new HttpManager.OnHttpResponseListener() {
                    @Override
                    public void onHttpResponse(int requestCode, String resultJson, Exception e) {
                        JSONResponse response = new JSONResponse(resultJson);
                        String json = JSON.format(resultJson);
                        JSONObject verifyObj = response.getJSONObject("verify");
                        verify = verifyObj == null ? null : verifyObj.getString("verify");
                        boolean isOk = StringUtil.isNotEmpty(verify, true);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Get verify code" + (isOk ? " succeed! " : " failed! "
                                                + response.getMsg()), Toast.LENGTH_LONG).show();
                                pb.setVisibility(View.GONE);
                                tvUnitResponse.setText(json);

                                if (isOk) {
                                    signInOrUp(isSignUp, pb);
                                }
                            }
                        });
                    }
                });

                return;
            }

            {   // Privacy <<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                JSONRequest privacy = new JSONRequest();
                privacy.put("phone", account);
                privacy.put("_password", password);
                request.put("Privacy", privacy);
            }   // Privacy >>>>>>>>>>>>>>>>>>>>>>>>>>>>>

            {   // User <<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                JSONRequest user = new JSONRequest();
                user.put("name", account);
                request.put("User", user);
            }   // User >>>>>>>>>>>>>>>>>>>>>>>>>>>>>

            request.put("verify", verify); // FIXME 调接口获取，弹窗输入
        }
        else if (isSignedIn == false) {
            request.put("phone", account);
            request.put("password", password);
        }

        String req = request.toString();

        tvUnitRequest.setText(req);

        HttpManager.getInstance().post(server + (isSignUp ? "/register" : (isSignedIn ? "/logout" : "/login")), req, new HttpManager.OnHttpResponseListener() {
            @Override
            public void onHttpResponse(int requestCode, String resultJson, Exception e) {
                JSONResponse response = new JSONResponse(resultJson);
                String json = JSON.format(resultJson);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isOk = response.isSuccess();
                        if (isOk) {
                            if (isSignUp == false) {
                                isSignedIn = ! isSignedIn;
                                tvUISignIn.setText(isSignedIn ? R.string.sign_out : R.string.sign_in);
                            }

                            cache.edit()
                                    .putString(KEY_ACCOUNT, account)
                                    .putString(KEY_PASSWORD, password)
                                    .commit();
                        }
                        else {
                            verify = null;
                        }

                        Toast.makeText(context, (isSignUp ? "Sign up" : "Sign in")
                                + (isOk ? " succeed! " : " failed! " + response.getMsg()), Toast.LENGTH_LONG)
                                .show();
                        pb.setVisibility(isSignedIn == false || isSignUp ? View.GONE : View.VISIBLE);
                        tvUnitResponse.setText(json);
                    }
                });
            }
        });

    }


    @Override
    protected void onDestroy() {
        cache.edit()
                .putBoolean(UIAutoApp.KEY_ENABLE_PROXY, isProxy)
                .putString(UIAutoApp.KEY_PROXY_SERVER, server)
                .commit();

        super.onDestroy();
    }

}

