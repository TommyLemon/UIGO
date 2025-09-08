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

package uigox.demo.activity_fragment;

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

import java.io.File;
import java.io.FileOutputStream;

import uigo.x.UIAutoApp;
import zuo.biao.library.R;
import zuo.biao.library.base.BaseActivity;
import zuo.biao.library.ui.BottomMenuWindow;
import zuo.biao.library.util.CommonUtil;
import zuo.biao.library.util.DataKeeper;

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
	 * 从图库获取图片 - 改进版，支持MIUI等系统
	 */
	public void selectPicFromLocal() {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		
		// 添加权限标志，提高兼容性
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}
		
		toActivity(intent, REQUEST_CODE_LOCAL);
	}

	public static final String RESULT_PICTURE_PATH = "RESULT_PICTURE_PATH";
	
	/**根据图库图片uri发送图片 - 重新实现，更好的兼容性
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
		if (selectedImage == null) {
			showShortToast("图片URI为空");
			return;
		}
		
		String imagePath = getImagePathFromUri(selectedImage);
		if (imagePath != null && !imagePath.isEmpty()) {
			picturePath = imagePath;
			setResult(RESULT_OK, new Intent().putExtra(RESULT_PICTURE_PATH, picturePath));
			return;
		}
		
		// 如果无法获取文件路径，尝试复制到私有目录
		copyImageToPrivateDir(selectedImage);
	}
	
	/**
	 * 从URI获取图片路径，支持多种兼容性处理
	 */
	private String getImagePathFromUri(Uri uri) {
		if (uri == null) return null;
		
		try {
			// 首先尝试获取持久化权限
			getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
		} catch (Exception e) {
			// 忽略权限获取失败的异常
		}
		
		// 方法1: 对于file:// URI，直接获取路径
		if ("file".equalsIgnoreCase(uri.getScheme())) {
			String path = uri.getPath();
			if (path != null && new File(path).exists()) {
				return path;
			}
		}
		
		// 方法2: 对于content:// URI，使用MediaStore查询
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getImagePathFromMediaStore(uri);
		}
		
		return null;
	}
	
	/**
	 * 从MediaStore获取图片路径
	 */
	private String getImagePathFromMediaStore(Uri uri) {
		String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
		Cursor cursor = null;
		
		try {
			cursor = getContentResolver().query(uri, projection, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
				if (columnIndex >= 0) {
					String path = cursor.getString(columnIndex);
					if (path != null && !path.isEmpty() && new File(path).exists()) {
						return path;
					}
				}
				
				// 如果DATA列不可用，尝试通过ID查询
				int idIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
				if (idIndex >= 0) {
					String id = cursor.getString(idIndex);
					return getImagePathById(id);
				}
			}
		} catch (SecurityException e) {
			// MIUI等系统可能抛出SecurityException，尝试降级方案
			return getImagePathByUriFallback(uri);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return null;
	}
	
	/**
	 * 通过图片ID获取路径
	 */
	private String getImagePathById(String imageId) {
		if (imageId == null) return null;
		
		Cursor cursor = null;
		try {
			cursor = getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				new String[]{MediaStore.Images.Media.DATA},
				MediaStore.Images.Media._ID + "=?",
				new String[]{imageId},
				null
			);
			
			if (cursor != null && cursor.moveToFirst()) {
				int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
				if (columnIndex >= 0) {
					String path = cursor.getString(columnIndex);
					if (path != null && new File(path).exists()) {
						return path;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return null;
	}
	
	/**
	 * MIUI等系统的降级方案
	 */
	private String getImagePathByUriFallback(Uri uri) {
		try {
//			// FIXME DocumentFile is not found 尝试通过DocumentFile获取
//			android.support.v4.provider.DocumentFile documentFile =
//				android.support.v4.provider.DocumentFile.fromSingleUri(this, uri);
//			if (documentFile != null && documentFile.exists()) {
//				// 对于无法直接访问的URI，我们可以尝试复制文件
//				return null; // 返回null触发复制方案
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 复制图片到私有目录（当无法获取原始路径时）
	 */
	private void copyImageToPrivateDir(Uri sourceUri) {
		try {
			// 创建私有目录
			File privateDir = new File(getFilesDir(), "images");
			if (!privateDir.exists()) {
				privateDir.mkdirs();
			}
			
			// 生成目标文件
			String fileName = "img_" + System.currentTimeMillis() + ".jpg";
			File destFile = new File(privateDir, fileName);
			
			// 复制文件
			try (java.io.InputStream inputStream = getContentResolver().openInputStream(sourceUri);
				 java.io.FileOutputStream outputStream = new FileOutputStream(destFile)) {
				
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				outputStream.flush();
				
				picturePath = destFile.getAbsolutePath();
				setResult(RESULT_OK, new Intent().putExtra(RESULT_PICTURE_PATH, picturePath));
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		showShortToast("无法访问图片，请重试");
	}

	/**
	 * 处理回放模式下的图片选择
	 */
	private void handleReplayMode(Uri selectedImage, UIAutoApp.Node<InputEvent> node) {
		try {
			// 在回放模式下，尝试从Intent中获取之前保存的图片路径
			if (node != null && node.intent != null) {
				String savedPath = node.intent.getStringExtra(RESULT_PICTURE_PATH);
				if (savedPath != null && !savedPath.isEmpty()) {
					File savedFile = new File(savedPath);
					if (savedFile.exists()) {
						picturePath = savedPath;
						setResult(RESULT_OK, new Intent().putExtra(RESULT_PICTURE_PATH, picturePath));
						return;
					}
				}
			}
			
			// 如果没有保存的路径，尝试从URI获取
			String path = getImagePathFromUri(selectedImage);
			if (path != null && !path.isEmpty()) {
				picturePath = path;
				setResult(RESULT_OK, new Intent().putExtra(RESULT_PICTURE_PATH, picturePath));
				return;
			}
			
			// 如果都失败了，使用默认的模拟图片
			createMockImage();
			
		} catch (Exception e) {
			e.printStackTrace();
			// 如果出现异常，也创建模拟图片
			createMockImage();
		}
	}
	
	/**
	 * 创建模拟图片用于回放模式
	 */
	private void createMockImage() {
		try {
			// 创建一个模拟的图片文件
			File mockDir = new File(getFilesDir(), "mock_images");
			if (!mockDir.exists()) {
				mockDir.mkdirs();
			}
			
			File mockImage = new File(mockDir, "mock_image.jpg");
			if (!mockImage.exists()) {
				// 创建一个简单的1x1像素的JPEG图片
				try (java.io.FileOutputStream fos = new java.io.FileOutputStream(mockImage)) {
					// 简单的JPEG头部数据
					byte[] jpegData = new byte[]{
						(byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0, 0x00, 0x10, 0x4A, 0x46,
						0x49, 0x46, 0x00, 0x01, 0x01, 0x01, 0x00, 0x48, 0x00, 0x48, 0x00, 0x00,
						(byte)0xFF, (byte)0xC0, 0x00, 0x11, 0x08, 0x00, 0x01, 0x00, 0x01, 0x01,
						0x01, 0x11, 0x00, 0x02, 0x11, 0x01, 0x03, 0x11, 0x01, (byte)0xFF, (byte)0xD9
					};
					fos.write(jpegData);
				}
			}
			
			picturePath = mockImage.getAbsolutePath();
			setResult(RESULT_OK, new Intent().putExtra(RESULT_PICTURE_PATH, picturePath));
			
		} catch (Exception e) {
			e.printStackTrace();
			showShortToast("回放模式创建模拟图片失败");
		}
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
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
//									app.sendActivityResult(node);
									onActivityResult(node.requestCode, node.resultCode, node.intent);
								}
							}, 1000);
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
				break;
			case REQUEST_CODE_LOCAL: //发送本地图片
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						// 检查是否为回放模式
						UIAutoApp app = UIAutoApp.getInstance();
						UIAutoApp.Node<InputEvent> node = app.isReplaying() ? app.getCurrentEventNode() : null;
						if (node != null && (node.mock == null || node.mock)) {
							// 在回放模式下，如果是mock操作，直接返回模拟结果
							handleReplayMode(selectedImage, node);
						} else {
							sendPicByUri(selectedImage);
						}
					}
				}
				break;
			default:
				break;
			}
		}

		finish();

		if (requestCode == REQUEST_CODE_CAMERA || requestCode == REQUEST_CODE_LOCAL) {
			UIAutoApp.getInstance().onUIAutoActivityResult(this, requestCode, resultCode, data, true);
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