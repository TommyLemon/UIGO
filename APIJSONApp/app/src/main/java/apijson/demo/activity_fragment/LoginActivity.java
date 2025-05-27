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

package apijson.demo.activity_fragment;

import zuo.biao.apijson.JSONResponse;
import zuo.biao.library.base.BaseActivity;
import zuo.biao.library.interfaces.OnBottomDragListener;
import zuo.biao.library.manager.HttpManager.OnHttpResponseListener;
import zuo.biao.library.ui.BottomMenuWindow;
import zuo.biao.library.ui.ServerSettingActivity;
import zuo.biao.library.ui.TextClearSuit;
import zuo.biao.library.util.EditTextUtil;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.SettingUtil;
import zuo.biao.library.util.StringUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import apijson.demo.R;
import apijson.demo.application.DemoApplication;
import apijson.demo.manager.DataManager;
import apijson.demo.model.User;
import apijson.demo.util.HttpRequest;
import apijson.demo.server.model.Login;

/**登录界面
 * @author Lemon
 */
public class LoginActivity extends BaseActivity implements OnClickListener, OnBottomDragListener{
	private static final String TAG = "LoginActivity";


	//启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	/**启动这个Activity的Intent
	 * @param context
	 * @return
	 */
	public static Intent createIntent(Context context) {
		return new Intent(context, LoginActivity.class);
	}

	//启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>



	@Override
	public Activity getActivity() {
		return this;
	}

	public static final int RESULT_LOGIN = 41;
	public static final String RESULT_LOGGED_IN = "RESULT_LOGGED_IN";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity, this);

		//必须调用<<<<<<<<<<<
		initView();
		initData();
		initEvent();
		//必须调用>>>>>>>>>>

	}


	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	private EditText etLoginPhone;
	private EditText etLoginPassword;
	@Override
	public void initView() {//必须调用
		exitAnim = R.anim.bottom_push_out;//退出动画

		etLoginPhone = (EditText) findViewById(R.id.etLoginPhone);
		etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);

	}

	private void onLoginSucceed() {
		runUiThread(new Runnable() {

			@Override
			public void run() {
				intent = MainTabActivity.createIntent(context);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				toActivity(intent);
				enterAnim = exitAnim = R.anim.null_anim;
				finish();	
			}
		});
	}

	private void toPassword(int type, int requestCode) {
		toActivity(PasswordActivity.createIntent(context, type, StringUtil.getTrimedString(etLoginPhone)
				, StringUtil.getString(etLoginPassword)), requestCode);
	}

	private void showForget() {
		toActivity(BottomMenuWindow.createIntent(context, new String[]{"重置密码", "验证码登录"})
				.putExtra(INTENT_TITLE, "忘记密码")
				, REQUEST_TO_BOTTOM_MUNU, false);		
	}

	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










	//data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	private String phone;
	private String password;

	@Override
	public void initData() {//必须调用

		phone = DataManager.getInstance().getLastUserPhone();
		if(StringUtil.isPhone(phone)) {
			etLoginPhone.setText("" + phone);
			etLoginPassword.requestFocus();
		}

	}


	private void login(int type) {
		if (EditTextUtil.isInputedCorrect(context, etLoginPhone, EditTextUtil.TYPE_PHONE) == false
				|| EditTextUtil.isInputedCorrect(context, etLoginPassword, type == Login.TYPE_VERIFY 
				? EditTextUtil.TYPE_VERIFY : EditTextUtil.TYPE_PASSWORD) == false) {
			return;
		}
		EditTextUtil.hideKeyboard(context, etLoginPassword);

		showProgressDialog("正在登录，请稍后...");


		phone = StringUtil.getTrimedString(etLoginPhone);
		password = StringUtil.getString(etLoginPassword);

		//登录请求
		HttpRequest.login(phone, password, type, type, new OnHttpResponseListener() {
			@Override
			public void onHttpResponse(int requestCode, String resultJson, Exception e) {
				dismissProgressDialog();
				JSONResponse response = new JSONResponse(resultJson);
				User user = response.getObject(User.class);

				if (response.isSuccess() == false) {
					switch (response.getCode()) {
					case JSONResponse.CODE_NOT_FOUND:
						showShortToast("账号不存在，请先注册");
						onDragBottom(true);
						break;
					case JSONResponse.CODE_ILLEGAL_ARGUMENT:
						showShortToast("账号或密码不合法！");
						break;
					case JSONResponse.CODE_CONDITION_ERROR:
						showShortToast("账号或密码错误！");
						showForget();
						break;
					default:
						showShortToast(R.string.login_faild);
						break;
					}
				} else {
					user.setPhone(phone);
					DemoApplication.getInstance().saveCurrentUser(user);
					if (DemoApplication.getInstance().isLoggedIn() == false) {
						showShortToast((requestCode == Login.TYPE_PASSWORD ? "密码" : "验证码") + "错误");
						return;
					}

					onLoginSucceed();
				}
			}
		});

	}


	//Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>









	//Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void initEvent() {//必须调用
		// TODO
		//		if (BuildConfig.DEBUG) {
			tvBaseTitle.setOnClickListener(this);
		//		}

		findViewById(R.id.tvLoginForget).setOnClickListener(this);
		findViewById(R.id.tvLoginLogin).setOnClickListener(this);

		new TextClearSuit().addClearListener(etLoginPhone, findViewById(R.id.ivLoginPhoneClear));
		new TextClearSuit().addClearListener(etLoginPassword, findViewById(R.id.ivLoginPasswordClear));
	}


	@Override
	public void onDragBottom(boolean rightToLeft) {
		if (rightToLeft) {

			toPassword(PasswordActivity.TYPE_REGISTER, REQUEST_TO_REGISTER);
			return;
		}

		finish();
	}

	//系统自带监听方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tvBaseTitle:
				toActivity(ServerSettingActivity.createIntent(context
								, SettingUtil.getServerAddress(false), SettingUtil.getServerAddress(true)
								, SettingUtil.APP_SETTING, Context.MODE_PRIVATE
								, SettingUtil.KEY_SERVER_ADDRESS_NORMAL, SettingUtil.KEY_SERVER_ADDRESS_TEST)
						, REQUEST_TO_SERVER_SETTING);
				break;
			case R.id.tvLoginForget:
				showForget();
				break;
			case R.id.tvLoginLogin:
				login(Login.TYPE_PASSWORD);
				break;
			default:
				break;
		}
	}


	//类相关监听<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


	@Override
	public void finish() {
		if (DemoApplication.getInstance().isLoggedIn() == false) {
			showShortToast("未登录，有些内容会加载不出来~");
		}

		setResult(RESULT_OK, new Intent().putExtra(RESULT_LOGGED_IN, DemoApplication.getInstance().isLoggedIn()));
		super.finish();
	}


	public static final int REQUEST_TO_BOTTOM_MUNU = 1;
	public static final int REQUEST_TO_SERVER_SETTING = 2;
	public static final int REQUEST_TO_REGISTER = 3;
	public static final int REQUEST_TO_VERIFY = 4;
	public static final int REQUEST_TO_RESET = 5;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case REQUEST_TO_SERVER_SETTING:
			sendBroadcast(new Intent(ACTION_EXIT_APP));
			break;
		case REQUEST_TO_BOTTOM_MUNU:
			if (data != null) {
				switch (data.getIntExtra(BottomMenuWindow.RESULT_ITEM_ID, -1)) {
				case 0:
					toPassword(PasswordActivity.TYPE_RESET, REQUEST_TO_RESET);
					break;
				case 1:
					toPassword(PasswordActivity.TYPE_VERIFY, REQUEST_TO_VERIFY);
					break;
				default:
					break;
				}
			}
			break;
		case REQUEST_TO_RESET:
		case REQUEST_TO_VERIFY:
		case REQUEST_TO_REGISTER:
			if (data != null) {
				String phone = data.getStringExtra(RESULT_PHONE);
				String password = data.getStringExtra(requestCode == REQUEST_TO_VERIFY
						? RESULT_VERIFY : RESULT_PASSWORD);
				if (StringUtil.isPhone(phone)) {
					etLoginPhone.setText(phone);
				}
				if (StringUtil.isNotEmpty(password, true)) {
					etLoginPassword.setText(password);
				}

				login(requestCode == REQUEST_TO_VERIFY ? Login.TYPE_VERIFY : Login.TYPE_PASSWORD);
			}
			break;
		default:
			break;
		}
	}

	//类相关监听>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	//系统自带监听方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


	//Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








	//内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


}