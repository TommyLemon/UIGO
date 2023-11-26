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

import unitauto.apk.UnitAutoActivity;


/**自动 UI 测试，需要用 UIAuto 发请求到这个设备
 * https://github.com/TommyLemon/UIAuto
 * @author Lemon
 */
public class UIAutoActivity extends UnitAutoActivity {
    private static final String TAG = "UIAutoActivity";

    private static final String INTENT_FLOW_ID = "INTENT_FLOW_ID";

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

        etUnitProxy = findViewById(R.id.etUnitProxy);
        pbUnitProxy = findViewById(R.id.pbUnitProxy);
        tvUnitProxy = findViewById(R.id.tvUnitProxy);

        etUnitProxy.setText(server);
        switchProxy(tvUnitProxy);

        tvUnitProxy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchProxy(v);
            }
        });
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

        UIAutoApp.getInstance().onUIAutoActivityCreate(this);
        UIAutoApp.getInstance().prepareRecord();
        finish();
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

