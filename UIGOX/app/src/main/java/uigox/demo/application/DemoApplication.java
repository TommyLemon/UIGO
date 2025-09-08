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

package uigox.demo.application;

import uigox.demo.manager.DataManager;
import uigox.demo.model.User;

import android.content.res.Configuration;
import androidx.annotation.NonNull;
//import uigox.demo.BuildConfig;
import uigo.x.UIAutoApp;
import zuo.biao.library.base.BaseApplication;
import zuo.biao.library.util.StringUtil;
import android.util.Log;

import com.scwang.smart.refresh.layout.kernel.BuildConfig;

/**Application
 * @author Lemon
 */
public class DemoApplication extends BaseApplication {
	private static final String TAG = "DemoApplication";

	private static DemoApplication context;
	public static DemoApplication getInstance() {
		return context;
	}
	
	// 暂时以继承方式实现，后续改为支持静态调用（需要把 UIAutoApp 成员变量全改为 static）
	
	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		
		UIAutoApp.getInstance().initUIAuto(this);
	
		Thread.UncaughtExceptionHandler handler = Thread.currentThread().getUncaughtExceptionHandler();
		Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
				if (BuildConfig.DEBUG) {
					if (handler != null) {
						handler.uncaughtException(t, e);
					} else {
//						t.stop();
//						t.stop(e);
						throw new RuntimeException(e);
					}
				} else {
					e.printStackTrace();
					// TODO 上传到 Bugly 等日志平台
				}
			}
		});
	}

//	public static List<Object> getOutputList(int limit, int offset) {
//		return getOutputList(UIAutoApp.getInstance(), limit, offset);
//	}
//	public static List<Object> getOutputList(DemoApplication app, int limit, int offset) {
//		return UIAutoApp.getOutputList(UIAutoApp.getInstance(), limit, offset);
//	}
//
//	public static List<Object> getOutputList(UIAutoApp app, int limit, int offset) {
//		if (app == null) {
//			app = UIAutoApp.getInstance();
//		}
//		return UIAutoApp.getOutputList(app, limit, offset);
//	}
//
//	public static void prepareReplay(JSONArray eventList) {
//		UIAutoApp.getInstance().prepareReplay(eventList);
//	}

	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		UIAutoApp.getInstance().onConfigurationChanged(newConfig);
	}

	/**获取当前用户id
	 * @return
	 */
	public long getCurrentUserId() {
		currentUser = getCurrentUser();
		Log.d(TAG, "getCurrentUserId  currentUserId = " + (currentUser == null ? "null" : currentUser.getId()));
		return currentUser == null ? 0 : currentUser.getId();
	}
	/**获取当前用户phone
	 * @return
	 */
	public String getCurrentUserPhone() {
		currentUser = getCurrentUser();
		return currentUser == null ? null : currentUser.getPhone();
	}


	private static User currentUser = null;
	public User getCurrentUser() {
		if (currentUser == null) {
			currentUser = DataManager.getInstance().getCurrentUser();
		}
		return currentUser;
	}

	public void saveCurrentUser(User user) {
		if (user == null) {
			Log.e(TAG, "saveCurrentUser  currentUser == null >> return;");
			return;
		}
		if (user.getId() <= 0 && StringUtil.isNotEmpty(user.getName(), true) == false) {
			Log.e(TAG, "saveCurrentUser  user.getId() <= 0" +
					" && StringUtil.isNotEmpty(user.getName(), true) == false >> return;");
			return;
		}

		if (currentUser != null && user.getId().equals(currentUser.getId())
				&& StringUtil.isNotEmpty(user.getPhone(), true) == false) {
			user.setPhone(currentUser.getPhone());
		}
		currentUser = user;
		DataManager.getInstance().saveCurrentUser(currentUser);
	}

	public void logout() {
		currentUser = null;
		DataManager.getInstance().saveCurrentUser(currentUser);
	}
	
	/**判断是否为当前用户
	 * @param userId
	 * @return
	 */
	public boolean isCurrentUser(long userId) {
		return DataManager.getInstance().isCurrentUser(userId);
	}

	public boolean isLoggedIn() {
		return getCurrentUserId() > 0;
	}



}
