/*Copyright ©2020 TommyLemon(https://github.com/TommyLemon/UIAuto)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/


package apijson.demo.application;

import android.app.Application;
import android.support.annotation.NonNull;

import apijson.demo.BuildConfig;
import apijson.demo.R;
import uiauto.UIAutoApp;

/**Base Application，用法类似 MultiDexApplication。
 * 可在被测 Module 的 Application 的 onCreate 中调用 UnitAutoApp.init(this)；
 * 或者如果项目简单（没有方法签名冲突），可以直接用 被测 Module 的 Application 继承 UnitAutoApp。
 * @author Lemon
 * @see #init(Application)
 */
public class DemoApplication extends UIAutoApp {
	private static final String TAG = "DemoApplication";

	// 暂时以继承方式实现，后续改为支持静态调用（需要把 UIAutoApp 成员变量全改为 static）
	@Override
	public void onCreate() {
		super.onCreate();
//		UIAutoApp.init(this);
		Thread.UncaughtExceptionHandler handler = Thread.currentThread().getUncaughtExceptionHandler();
		Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
				if (BuildConfig.DEBUG) {
					if (handler != null) {
						handler.uncaughtException(t, e);
					} else {
						t.stop(e);
					}
				} else {
					e.printStackTrace();
					// TODO 上传到 Bugly 等日志平台
				}
			}
		});
	}

	/**获取应用名
	 * @return
	 */
	public String getAppName() {
		return getResources().getString(R.string.app_name);
	}
	/**获取应用版本名(显示给用户看的)
	 * @return
	 */
	public String getAppVersion() {
		return getResources().getString(R.string.app_version);
	}

}
