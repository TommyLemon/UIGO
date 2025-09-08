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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;

import uigox.demo.R;
import uigox.demo.application.DemoApplication;
import apijson.JSON;
import zuo.biao.library.base.BaseActivity;
import zuo.biao.library.interfaces.OnBottomDragListener;

/**动态列表界面
 * @author Lemon
 * @use toActivity(MomentListActivity.createIntent(...));
 */
public class MomentListActivity extends BaseActivity implements OnBottomDragListener {
	//	private static final String TAG = "DemoFragmentActivity";

	//启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	public static final String INTENT_RANGE = "INTENT_RANGE";
	public static final String INTENT_SEARCH = "INTENT_SEARCH";
	public static final String INTENT_SHOW_SEARCH = "INTENT_SHOW_SEARCH";

	/**启动这个Activity的Intent
	 * @param context
	 * @return
	 */
	public static Intent createIntent(Context context) {
		return createIntent(context, MomentListFragment.RANGE_USER_CIRCLE
				, DemoApplication.getInstance().getCurrentUserId());
	}
	/**启动这个Activity的Intent
	 * @param context
	 * @param userId
	 * @return
	 */
	public static Intent createIntent(Context context, long userId) {
		return createIntent(context, MomentListFragment.RANGE_USER, userId);
	}
	/**启动这个Activity的Intent
	 * showSearch = true;
	 * @param context
	 * @param range
	 * @param id
	 * @return
	 */
	public static Intent createIntent(Context context, int range, long id) {
		return createIntent(context, range, id, null, true);
	}
	/**启动这个Activity的Intent
	 * showSearch = false;
	 * @param context
	 * @param search
	 * @return
	 */
	public static Intent createIntent(Context context, JSONObject search) {
		return createIntent(context, search, false);
	}
	/**启动这个Activity的Intent
	 * @param context
	 * @param search
	 * @param showSearch
	 * @return
	 */
	public static Intent createIntent(Context context, JSONObject search, boolean showSearch) {
		return createIntent(context, MomentListFragment.RANGE_ALL, 0, search, showSearch);
	}
	/**启动这个Activity的Intent
	 * @param context
	 * @param range
	 * @param id
	 * @param search
	 * @param showSearch
	 * @return
	 */
	public static Intent createIntent(Context context, int range, long id, JSONObject search, boolean showSearch) {
		return new Intent(context, MomentListActivity.class)
		.putExtra(INTENT_RANGE, range)
		.putExtra(INTENT_ID, id)
		.putExtra(INTENT_SEARCH, JSON.toJSONString(search))
		.putExtra(INTENT_SHOW_SEARCH, showSearch);
	}

	//启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	private int range = MomentListFragment.RANGE_ALL;
	private long id;
	private JSONObject search;
	private boolean showSearch;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moment_list_activity, this);

		range = getIntent().getIntExtra(INTENT_RANGE, range);
		id = getIntent().getLongExtra(INTENT_ID, id);
		search = JSON.parseObject(getIntent().getStringExtra(INTENT_SEARCH));
		showSearch = getIntent().getBooleanExtra(INTENT_SHOW_SEARCH, showSearch);

		//功能归类分区方法，必须调用<<<<<<<<<<
		initView();
		initData();
		initEvent();
		//功能归类分区方法，必须调用>>>>>>>>>>

	}


	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	private boolean isCurrentUser = false;
	
	private ImageView ivMomentListForward;
	private MomentListFragment fragment;
	@Override
	public void initView() {//必须在onCreate方法内调用
		ivMomentListForward = (ImageView) findViewById(R.id.ivMomentListForward);
		ivMomentListForward.setVisibility(showSearch ? View.VISIBLE : View.GONE);
		
		String title;
		switch (range) {
		case MomentListFragment.RANGE_ALL:
			title = "全部";
			break;
			//		case MomentListFragment.RANGE_SINGLE:
			//			title = "动态";
			//			break;
		case MomentListFragment.RANGE_USER:
			isCurrentUser = DemoApplication.getInstance().isCurrentUser(id);
			title = isCurrentUser ? "我的动态" : "TA的动态";
			if (isCurrentUser) {
				ivMomentListForward.setVisibility(View.VISIBLE);
				ivMomentListForward.setImageResource(zuo.biao.library.R.drawable.add);
			} else {
				ivMomentListForward.setVisibility(View.GONE);
			}
			break;
		case MomentListFragment.RANGE_USER_CIRCLE:
			title = "朋友圈";
			break;
		default:
			title = "动态";
			break;
		}
		tvBaseTitle.setText(title);
		autoSetTitle();


		fragment = MomentListFragment.createInstance(range, id, search);
		fragment.setIsAdd(isCurrentUser);

		fragmentManager
		.beginTransaction()
		.add(R.id.flMomentListContainer, fragment)
		.show(fragment)
		.commit();

	}



	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










	//Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void initData() {//必须在onCreate方法内调用

	}

	//Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








	//Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void initEvent() {//必须在onCreate方法内调用

	}


	@Override
	public void onDragBottom(boolean rightToLeft) {
		if (rightToLeft) {
			if (showSearch) {
				fragment.onDragBottom(rightToLeft);
			}

			return;
		}

		finish();
	}

	//系统自带监听方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



	//类相关监听<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<





	//类相关监听>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	//系统自带监听方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


	//类相关监听>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	//系统自带监听>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


	//Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








	//内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



	//内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}