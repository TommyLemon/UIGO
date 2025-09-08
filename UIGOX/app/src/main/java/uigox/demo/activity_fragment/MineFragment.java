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

package uigox.demo.activity_fragment;

import java.io.File;

import apijson.JSONResponse;
import apijson.StringUtil;
import zuo.biao.library.base.BaseView.OnDataChangedListener;
import zuo.biao.library.interfaces.OnBottomDragListener;
import uigo.x.HttpManager.OnHttpResponseListener;
import zuo.biao.library.ui.AlertDialog;
import zuo.biao.library.ui.AlertDialog.OnDialogButtonClickListener;
import zuo.biao.library.ui.WebViewActivity;
import zuo.biao.library.util.CommonUtil;
import zuo.biao.library.util.DownloadUtil;
import zuo.biao.library.util.ImageLoaderUtil;
import zuo.biao.library.util.Log;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import uigox.demo.R;
import uigox.demo.application.DemoApplication;
import uigox.demo.base.BaseFragment;
import uigox.demo.interfaces.TopBarMenuCallback;
import uigox.demo.model.User;
import uigox.demo.util.HttpRequest;

/**设置fragment
 * @author Lemon
 * @use new MineFragment(),详细使用见.DemoFragmentActivity(initData方法内)
 */
public class MineFragment extends BaseFragment implements OnClickListener, OnDialogButtonClickListener
, OnHttpResponseListener, OnBottomDragListener, TopBarMenuCallback, OnDataChangedListener {
	private static final String TAG = "MineFragment";

	//与Activity通信<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	/**创建一个Fragment实例
	 * @return
	 */
	public static MineFragment createInstance() {
		return new MineFragment();
	}

	//与Activity通信>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>	



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//类相关初始化，必须使用<<<<<<<<<<<<<<<<
		super.onCreateView(inflater, container, savedInstanceState);
		setContentView(R.layout.mine_fragment);
		//类相关初始化，必须使用>>>>>>>>>>>>>>>>

		registerObserver(this);

		//功能归类分区方法，必须调用<<<<<<<<<<
		initView();
		initData();
		initEvent();
		//功能归类分区方法，必须调用>>>>>>>>>>

		return view;
	}



	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	private ImageView ivSettingHead;
	private TextView tvSettingName;
	@Override
	public void initView() {//必须调用

		ivSettingHead = findView(R.id.ivSettingHead);
		tvSettingName = findView(R.id.tvSettingName);
	}

	private User user;
	private void setUser(User user_) {
		this.user = user_;
		if (user == null) {
			user = new User();
		}
		runUiThread(new Runnable() {

			@Override
			public void run() {
				if (isLoggedIn) {
					ImageLoaderUtil.loadImage(ivSettingHead, user.getHead(), ImageLoaderUtil.TYPE_ROUND_CORNER);
					tvSettingName.setText(StringUtil.getTrimedString(user.getName()));		
				} else {
					ivSettingHead.setImageResource(R.drawable.ic_launcher);
					tvSettingName.setText("未登录");		
				}
			}
		});
	}


	private TextView leftMenu;
	@SuppressLint("InflateParams")
	@Override
	public View getLeftMenu(Activity activity) {
		if (leftMenu == null) {
			leftMenu = (TextView) LayoutInflater.from(activity).inflate(zuo.biao.library.R.layout.top_right_tv, null);
			leftMenu.setGravity(Gravity.CENTER);
			leftMenu.setText("扫一扫");
			leftMenu.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onDragBottom(false);
				}
			});
		}
		return leftMenu;
	}

	private TextView rightMenu;
	@SuppressLint("InflateParams")
	@Override
	public View getRightMenu(Activity activity) {
		if (rightMenu == null) {
			rightMenu = (TextView) LayoutInflater.from(activity).inflate(zuo.biao.library.R.layout.top_right_tv, null);
			rightMenu.setGravity(Gravity.CENTER);
			rightMenu.setText("设置");
			rightMenu.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onDragBottom(true);
				}
			});
		}
		return rightMenu;
	}

	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










	//Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void initData() {//必须调用
		super.initData();

	}

	@Override
	public void onDataChanged() {
		setUser(DemoApplication.getInstance().getCurrentUser());
	}


	/**下载应用
	 */
	private void downloadApp() {
		showProgressDialog("正在下载...");
		runThread(TAG + "downloadApp", new Runnable() {
			@Override
			public void run() {
				final File file = DownloadUtil.downLoadFile(context, "APIJSONTest", ".apk"
						, "http://files.cnblogs.com/files/tommylemon/APIJSONTest.apk");

				runUiThread(new Runnable() {

					@Override
					public void run() {
						dismissProgressDialog();
						DownloadUtil.openFile(context, file);
					}
				});
			}
		});
	}



	//Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








	//Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void initEvent() {//必须调用

		ivSettingHead.setOnClickListener(this);

		findView(R.id.llSettingInfo).setOnClickListener(this);
		findView(R.id.llSettingMoment).setOnClickListener(this);
		findView(R.id.llSettingWallet).setOnClickListener(this);

		findView(R.id.llSettingAbout).setOnClickListener(this);
		findView(R.id.llSettingTest).setOnClickListener(this);
		findView(R.id.llSettingLogout).setOnClickListener(this);
	}


	@Override
	public void onDragBottom(boolean rightToLeft) {
		if (isAlive() == false) {
			return;
		}

		if (rightToLeft) {

			toActivity(SettingActivity.createIntent(context));
			return;
		}

		startActivityForResult(ScanActivity.createIntent(context), REQUEST_TO_SCAN);
		context.overridePendingTransition(zuo.biao.library.R.anim.bottom_push_in, zuo.biao.library.R.anim.fade);
	}



	@Override
	public void onDialogButtonClick(int requestCode, boolean isPositive) {
		if (! isPositive) {
			return;
		}

		switch (requestCode) {
		case 0:
			HttpRequest.logout(HTTP_LOUOUT, this);
			DemoApplication.getInstance().logout();

			toActivity(MainTabActivity.createIntent(context).addFlags(
					Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

			context.finish();
			break;
		default:
			break;
		}
	}


	//系统自带监听方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void onClick(View v) {//直接调用不会显示v被点击效果
        int vId = v.getId();
        if (vId == R.id.llSettingAbout) {
            toActivity(AboutActivity.createIntent(context));
        } else if (vId == R.id.llSettingTest) {
            downloadApp();
        } else {
            if (verifyLogin() == false) {
                return;
            }
            int id = v.getId();
            if (id == R.id.ivSettingHead || id == R.id.llSettingInfo) {
                toActivity(UserActivity.createIntent(context, DemoApplication.getInstance().getCurrentUserId()));
            } else if (id == R.id.llSettingMoment) {
                toActivity(MomentListActivity.createIntent(context, DemoApplication.getInstance().getCurrentUserId()));
            } else if (id == R.id.llSettingWallet) {
                toActivity(WalletActivity.createIntent(context));
            } else if (id == R.id.llSettingLogout) {
                new AlertDialog(context, "退出登录", "确定退出登录？", true, 0, this).show();
            }
        }
	}


	public static final int HTTP_LOUOUT = 2;
	@Override
	public void onHttpResponse(int requestCode, String resultJson, Exception e) {
		Log.d(TAG, "onHttpResponse  requestCode = " + requestCode + "; resultJson = \n" + resultJson);
		if (e != null) {
			e.printStackTrace();
		}
		switch (requestCode) {
		case HTTP_LOUOUT:
			JSONResponse response = new JSONResponse(resultJson).getJSONResponse(User.class.getSimpleName());
			boolean succeed = JSONResponse.isSuccess(response);
			Log.d(TAG, succeed ? "服务端退出成功" : "服务端退出失败");
			showShortToast(succeed ? "服务端退出成功" : "服务端退出失败");
			break;
		default:
			break;
		}
	}



	//类相关监听<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	private static final int REQUEST_TO_SCAN = 1;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case REQUEST_TO_SCAN:
			String result = data == null ? null : data.getStringExtra(ScanActivity.RESULT_QRCODE_STRING);
			if (StringUtil.isEmpty(result, true) == false) {
				if (StringUtil.isUrl(result)) {
					int index = result.indexOf("{\"User\":{");
					if (index > 0) {
						JSONResponse response = new JSONResponse(result.substring(index));
						User user = response.getObject(User.class);
						long id = user == null ? 0 : user.getId();
						if (id > 0) {
							toActivity(UserActivity.createIntent(context, id));
							break;
						}
					}
					toActivity(WebViewActivity.createIntent(context, "扫描结果", result));
				} else {
					CommonUtil.copyText(context, result);
				}
			}
			break;
		default:
			break;
		}
	}



	@Override
	public void onDestroy() {
		if (leftMenu != null) {
			leftMenu.destroyDrawingCache();
			leftMenu = null;
		}
		if (rightMenu != null) {
			rightMenu.destroyDrawingCache();
			rightMenu = null;
		}

		super.onDestroy();
	}


	//类相关监听>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	//系统自带监听方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


	//Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>









	//内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



	//内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}