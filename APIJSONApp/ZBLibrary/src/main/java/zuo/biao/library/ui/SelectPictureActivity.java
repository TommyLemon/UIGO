/*Copyright ©2015 TommyLemon(https://github.com/TommyLemon)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package zuo.biao.library.ui;

import java.io.File;

import uiauto.UIAutoApp;
import zuo.biao.library.R;
import zuo.biao.library.base.BaseActivity;
import zuo.biao.library.util.CommonUtil;
import zuo.biao.library.util.DataKeeper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.InputEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**通用选择单张照片Activity,已自带选择弹窗
 * @author Lemon
 * @use
 * <br> toActivity或startActivityForResult (SelectPictureActivity.createIntent(...), requestCode);
 * <br> 然后在onActivityResult方法内
 * <br> data.getStringExtra(SelectPictureActivity.RESULT_PICTURE_PATH); 可得到图片存储路径
 */
public class SelectPictureActivity extends BaseActivity implements OnClickListener {
	@SuppressWarnings("unused")
	private static final String TAG = "SelectPictureActivity";

	/**
	 * @param context
	 * @return
	 */
	public static Intent createIntent(Context context) {
		return new Intent(context, SelectPictureActivity.class);
	}
	
	@Override
	public Activity getActivity() {
		return this;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_picture_activity);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			try {
				StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
				builder.detectFileUriExposure();
				StrictMode.setVmPolicy(builder.build());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		//功能归类分区方法，必须调用<<<<<<<<<<
		initView();
		initData();
		initEvent();
		//功能归类分区方法，必须调用>>>>>>>>>>

	}


	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void initView() {//必须调用
		
	}


	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










	//Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	private String picturePath = "";
	@Override
	public void initData() {//必须调用
		
	}

	private File cameraFile;
	/**
	 * 照相获取图片
	 */
	public void selectPicFromCamera() {
		if (! CommonUtil.isExitsSdcard()) {
			showShortToast("SD卡不存在，不能拍照");
			return;
		}

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 指定调用相机拍照后照片的储存路径
		cameraFile = new File(DataKeeper.imagePath, "photo" + System.currentTimeMillis() + ".jpg");
		cameraFile.getParentFile().mkdirs();
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}

		toActivity(intent, REQUEST_CODE_CAMERA);
	}


	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}

		toActivity(intent, REQUEST_CODE_LOCAL);
	}

	public static final String RESULT_PICTURE_PATH = "RESULT_PICTURE_PATH";
	/**根据图库图片uri发送图片
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
		try {
			Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex("_data");
				picturePath = cursor.getString(columnIndex);
				cursor.close();

				if (picturePath == null || picturePath.equals("null")) {
					Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
			} else {
				File file = new File(selectedImage.getPath());
				if (!file.exists()) {
					Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;

				}
				picturePath = file.getAbsolutePath();
			}
		} catch (SecurityException e) {
			// 处理MIUI等系统的权限拒绝异常
			e.printStackTrace();
			try {
				// 尝试使用MediaStore API获取图片路径
				Cursor cursor = getContentResolver().query(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						new String[]{MediaStore.Images.Media.DATA},
						MediaStore.Images.Media._ID + "=?",
						new String[]{selectedImage.getLastPathSegment()},
						null
				);

				if (cursor != null && cursor.moveToFirst()) {
					int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
					picturePath = cursor.getString(columnIndex);
					cursor.close();

					if (picturePath == null || picturePath.equals("null")) {
						Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						return;
					}
				} else {
					Toast toast = Toast.makeText(this, "无法访问图片，请尝试其他图片", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				Toast toast = Toast.makeText(this, "图片访问失败，请重试", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
		}

		setResult(RESULT_OK, new Intent().putExtra(RESULT_PICTURE_PATH, picturePath));
	}

	//Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








	//Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void initEvent() {//必须调用
		
		findViewById(R.id.llSelectPictureBg).setOnClickListener(this);

		toActivity(new Intent(context, BottomMenuWindow.class)
		.putExtra(BottomMenuWindow.INTENT_TITLE, "选择图片")
		.putExtra(BottomMenuWindow.INTENT_ITEMS, new String[]{"拍照", "图库"})
		, REQUEST_TO_BOTTOM_MENU, false);
	}

	//系统自带监听方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.llSelectPictureBg) {
			finish();
		}
	}



	//类相关监听<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	public static final int REQUEST_TO_BOTTOM_MENU = 10;
	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_TO_BOTTOM_MENU:
				if (data != null) {
					int menu = data.getIntExtra(BottomMenuWindow.RESULT_ITEM_ID, -1);

					UIAutoApp app = UIAutoApp.getInstance();
					if ((menu == 0 || menu == 1) && app.isReplaying()) {
						UIAutoApp.Node<InputEvent> node = app.getCurrentEventNode();
						if (node.mock == null || node.mock) {
	//						new Handler().postDelayed(new Runnable() {
	//							@Override
	//							public void run() {
//								app.sendActivityResult(node);
								onActivityResult(node.requestCode, node.resultCode, node.intent);
	//							}
	//						}, 1000);
							return;
						}
					}

					switch (menu) {
					case 0:
						selectPicFromCamera();//照相
						return;
					case 1:
						selectPicFromLocal();//从图库筛选
						return;
					default:
						break;
					}
				}
				break;
			case REQUEST_CODE_CAMERA: //发送照片
				if (cameraFile != null && cameraFile.exists()) {
					picturePath = cameraFile.getAbsolutePath();
					setResult(RESULT_OK, new Intent().putExtra(RESULT_PICTURE_PATH, picturePath));
				}
			case REQUEST_CODE_LOCAL: //发送本地图片
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						sendPicByUri(selectedImage);
					}
				}
				break;
			default:
				break;
			}
		}

//		UIAutoApp.getInstance().onUIAutoActivityResult(this, requestCode, resultCode, data == null ? null : (Intent) data.clone(), true);

		finish();

		if (requestCode == REQUEST_CODE_CAMERA || requestCode == REQUEST_CODE_LOCAL) {
			UIAutoApp.getInstance().onUIAutoActivityResult(this, requestCode, resultCode, data, true);
//			UIAutoApp.getInstance().onUIAutoActivityResult(this, requestCode, resultCode, new Intent(data), true);
		}
	}

	@Override
	public void finish() {
		exitAnim = enterAnim = R.anim.null_anim;
		super.finish();
	}


	//类相关监听>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	//系统自带监听方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


	//Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








	//内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



	//内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}