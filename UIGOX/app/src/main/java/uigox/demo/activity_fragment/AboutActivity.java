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

import apijson.JSONRequest;
import zuo.biao.library.base.BaseActivity;
import zuo.biao.library.interfaces.OnBottomDragListener;
import zuo.biao.library.ui.WebViewActivity;
import zuo.biao.library.util.CommonUtil;
import zuo.biao.library.util.DownloadUtil;
import zuo.biao.library.util.JSON;
import zuo.biao.library.util.SettingUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import uigox.demo.R;
import uigox.demo.application.DemoApplication;
import uigox.demo.util.Constant;
import uigox.demo.util.HttpRequest;

import com.google.zxing.WriterException;
import com.king.zxing.util.CodeUtils;
//import com.ericssonlabs.encoding.EncodingHandler;

/**关于界面
 * @author Lemon
 */
public class AboutActivity extends BaseActivity implements OnClickListener, OnLongClickListener, OnBottomDragListener {
	private static final String TAG = "AboutActivity";

	//启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


	/**启动这个Activity的Intent
	 * @param context
	 * @return
	 */
	public static Intent createIntent(Context context) {
		return new Intent(context, AboutActivity.class);
	}

	//启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity, this);

		//功能归类分区方法，必须调用<<<<<<<<<<
		initView();
		initData();
		initEvent();
		//功能归类分区方法，必须调用>>>>>>>>>>

		if (SettingUtil.isOnTestMode) {
			showShortToast("测试服务器\n" + HttpRequest.URL_BASE);
		}
	}

	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	private ImageView ivAboutGesture;

	private TextView tvAboutAppInfo;

	private ImageView ivAboutQRCode;
	@Override
	public void initView() {
		
		ivAboutGesture = (ImageView) findViewById(R.id.ivAboutGesture);
		ivAboutGesture.setVisibility(SettingUtil.isFirstStart ? View.VISIBLE : View.GONE);
		if (SettingUtil.isFirstStart) {
			ivAboutGesture.setImageResource(R.drawable.gesture_left);
		}

		tvAboutAppInfo = (TextView) findViewById(R.id.tvAboutAppInfo);

		ivAboutQRCode = findViewById(R.id.ivAboutQRCode, this);
	}


	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










	//Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void initData() {
		
		tvAboutAppInfo.setText(DemoApplication.getInstance().getAppName()
				+ "\n" + DemoApplication.getInstance().getAppVersion());

		setQRCode();
	}


	private Bitmap qRCodeBitmap;
	/**显示二维码
	 */
	protected void setQRCode() {
		runThread(TAG + "setQRCode", new Runnable() {

			@Override
			public void run() {

				try {
					//不能暴露用户隐私
					qRCodeBitmap = CodeUtils.createQRCode(Constant.APP_DOWNLOAD_WEBSITE
							, (int) (2 * getResources().getDimension(R.dimen.qrcode_size)));
//			qRCodeBitmap = EncodingHandler.createQRCode(Constant.APP_DOWNLOAD_WEBSITE
//					, (int) (2 * getResources().getDimension(R.dimen.qrcode_size)));
				} catch (Throwable e) {
					e.printStackTrace();
					zuo.biao.library.util.Log.e(TAG, "initData  try {Bitmap qrcode = EncodingHandler.createQRCode(Constant.APP_DOWNLOAD_WEBSITE, ivQRCodeCode.getWidth());" +
							" >> } catch (Throwable e) {" + e.getMessage());
				}

				runUiThread(new Runnable() {
					@Override
					public void run() {
						ivAboutQRCode.setImageBitmap(qRCodeBitmap);
					}
				});		
			}
		});
	}

	/**下载应用
	 */
	private void downloadApp() {
		showProgressDialog("正在下载...");
		runThread(TAG + "downloadApp", new Runnable() {
			@Override
			public void run() {
				File file = DownloadUtil.downLoadFile(context, "APIJSONApp", ".apk", Constant.APP_DOWNLOAD_WEBSITE);
				dismissProgressDialog();
				DownloadUtil.openFile(context, file);
			}
		});
	}

	//Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








	//Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void initEvent() {
		
		findViewById(R.id.llAboutUpdate).setOnClickListener(this);
		findViewById(R.id.llAboutShare).setOnClickListener(this);
		findViewById(R.id.llAboutComment).setOnClickListener(this);

		findViewById(R.id.llAboutDeveloper, this).setOnLongClickListener(this);
		findViewById(R.id.llAboutWeibo, this).setOnLongClickListener(this);
		findViewById(R.id.llAboutContactUs, this).setOnLongClickListener(this);
	}

	//系统自带监听方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void onDragBottom(boolean rightToLeft) {
		if (rightToLeft) {
			toActivity(WebViewActivity.createIntent(context, "博客", Constant.APP_OFFICIAL_BLOG));

			ivAboutGesture.setImageResource(R.drawable.gesture_right);
			return;
		}

		if (SettingUtil.isFirstStart) {
			runThread(TAG + "onDragBottom", new Runnable() {
				@Override
				public void run() {
					Log.i(TAG, "onDragBottom  >> SettingUtil.putBoolean(context, SettingUtil.KEY_IS_FIRST_IN, false);");
					SettingUtil.putBoolean(SettingUtil.KEY_IS_FIRST_START, false);
				}
			});
		}

		finish();
	}

	@Override
	public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.llAboutUpdate) {
            toActivity(WebViewActivity.createIntent(context, "更新日志", Constant.UPDATE_LOG_WEBSITE));
        } else if (id == R.id.llAboutShare) {
            CommonUtil.shareInfo(context, getString(zuo.biao.library.R.string.share_app)
                    + "\n 点击链接直接下载体验APIJSON\n" + Constant.APP_DOWNLOAD_WEBSITE);
        } else if (id == R.id.llAboutComment) {
            showShortToast("应用未上线不能查看");
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=" + getPackageName())));
        } else if (id == R.id.llAboutDeveloper) {
            toActivity(WebViewActivity.createIntent(context, "开发者", Constant.APP_DEVELOPER_WEBSITE));
        } else if (id == R.id.llAboutWeibo) {
            toActivity(WebViewActivity.createIntent(context, "博客", Constant.APP_OFFICIAL_BLOG));
        } else if (id == R.id.llAboutContactUs) {
            CommonUtil.sendEmail(context, Constant.APP_OFFICIAL_EMAIL);
        } else if (id == R.id.ivAboutQRCode) {
            downloadApp();
        }
	}

	@Override
	public boolean onLongClick(View v) {
        int id = v.getId();
        if (id == R.id.llAboutDeveloper) {
            CommonUtil.copyText(context, Constant.APP_DEVELOPER_WEBSITE);
            return true;
        }
		if (id == R.id.llAboutWeibo) {
            CommonUtil.copyText(context, Constant.APP_OFFICIAL_BLOG);
            return true;
        }
		if (id == R.id.llAboutContactUs) {
            CommonUtil.copyText(context, Constant.APP_OFFICIAL_EMAIL);
            return true;
        }
		return false;
	}



	//系统自带监听方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


	//Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








	//内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



	//内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}
