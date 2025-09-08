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

package uigox.demo.base;

import uigox.demo.activity_fragment.LoginActivity;
import uigox.demo.util.ActionUtil;
import apijson.JSONResponse;
import zuo.biao.library.base.BaseBroadcastReceiver;
import zuo.biao.library.base.BaseView.OnDataChangedListener;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.StringUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import uigox.demo.application.DemoApplication;
import uigox.demo.model.User;

public abstract class BaseListActivity<T, LV extends AbsListView, BA extends BaseAdapter>
extends zuo.biao.library.base.BaseListActivity<T, LV, BA> {
	private static final String TAG = "BaseActivity";


	protected User currentUser;
	protected long currentUserId;
	protected boolean isLoggedIn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setCurrentUser();

	}

	private void setCurrentUser() {
		currentUser = DemoApplication.getInstance().getCurrentUser();
		currentUserId = currentUser == null ? 0 : currentUser.getId();
		isLoggedIn = isCurrentUserCorrect();
	}
	
	protected boolean verifyHttpLogin(int code) {
		if (isAlive() == false) {
			return true;
		}
		if (code == JSONResponse.CODE_NOT_LOGGED_IN) {
			DemoApplication.getInstance().logout();
			setCurrentUser();
		}
		return verifyLogin();
	}
	protected static boolean isCurrentUser(long userId) {
		return DemoApplication.getInstance().isCurrentUser(userId);
	}
	/**未登录会toLoginActivity();
	 * @return isLoggedIn
	 */
	protected boolean verifyLogin() {
		if (isLoggedIn == false) {
			showShortToast("请先登录");
			toLoginActivity();
		}
		return isLoggedIn;
	}
	protected void toLoginActivity() {
		startActivity(LoginActivity.createIntent(context));
		context.overridePendingTransition(zuo.biao.library.R.anim.bottom_push_in, zuo.biao.library.R.anim.hold);
	}

	@Override
	public void initData() {
		super.initData();
		loadAfterCorrect();
	}


	private boolean isDataChanged = false;
	/**
	 */
	protected void invalidate() {
		if (isRunning() == false) {
			isDataChanged = true;
			Log.w(TAG, "onDataChanged  isRunning() == false >> return;");
			return;
		}
		isDataChanged = false;

		setCurrentUser();
		loadAfterCorrect();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isDataChanged) {
			if (onDataChangedListener != null) {
				Log.d(TAG, "onResume  isDataChanged >> onDataChangedListener.onDataChanged();");
				onDataChangedListener.onDataChanged();
			}
		}
	}

	protected void loadAfterCorrect() {
		if (isCurrentUserCorrect() == false) {//请求currentUser都统一交给MainTabActivity，避免同时多次相同请求
			Log.e(TAG, "loadAfterCorrect  isCurrentUserCorrect() == false >> return;");
			return;
		}
		if (onDataChangedListener != null) {
			onDataChangedListener.onDataChanged();
		}
	}


	/**
	 * @return
	 */
	public boolean isCurrentUserCorrect() {
		return isUserCorrect(currentUser);
	}
	/**
	 * @param user
	 * @return
	 */
	public boolean isUserCorrect(User user) {
		return user != null && user.getId() > 0;
	}


	@Override
	protected void onDestroy() {
		unregisterObserver();
		super.onDestroy();
	}

	private OnDataChangedListener onDataChangedListener;
	protected void registerObserver(OnDataChangedListener onDataChangedListener) {
		this.onDataChangedListener = onDataChangedListener;
		BaseBroadcastReceiver.register(context, receiver, ActionUtil.ACTION_USER_CHANGED);
	}
	protected void unregisterObserver() {
		onDataChangedListener = null;
		BaseBroadcastReceiver.unregister(context, receiver);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent == null ? null : intent.getAction();
			if (isAlive() == false || StringUtil.isNotEmpty(action, true) == false) {
				Log.e(TAG, "receiver.onReceive  isAlive() == false" +
						" || StringUtil.isNotEmpty(action, true) == false >> return;");
				return;
			}

			if (ActionUtil.ACTION_USER_CHANGED.equals(action)) {
				if (isCurrentUser(intent.getLongExtra(INTENT_ID, 0))) {
					invalidate();
				}
			}
		}
	};

}