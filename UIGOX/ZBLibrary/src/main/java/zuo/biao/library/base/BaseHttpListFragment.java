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

package zuo.biao.library.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.List;

import zuo.biao.library.R;
import zuo.biao.library.interfaces.OnHttpResponseListener;
import zuo.biao.library.interfaces.OnLoadListener;
import zuo.biao.library.interfaces.OnStopLoadListener;
import zuo.biao.library.util.Log;


/**基础http网络列表的Fragment
 * @author Lemon
 * @param <T> 数据模型(model/JavaBean)类
 * @param <LV> AbsListView的子类（ListView,GridView等）
 * @param <A> 管理LV的Adapter
 * @see #getListAsync(int)
 * @see #onHttpResponse(int, String, Exception)
 * @see
 *   <pre>
 *       基础使用：<br />
 *       extends BaseHttpListFragment 并在子类onCreate中srlBaseHttpList.autoRefresh(), 具体参考.DemoHttpListFragment
 *       <br /><br />
 *       列表数据加载及显示过程：<br />
 *       1.srlBaseHttpList.autoRefresh触发刷新 <br />
 *       2.getListAsync异步获取列表数据 <br />
 *       3.onHttpResponse处理获取数据的结果 <br />
 *       4.setList把列表数据绑定到adapter <br />
 *   </pre>
 */
public abstract class BaseHttpListFragment<T, LV extends AbsListView, A extends ListAdapter>
		extends BaseListFragment<T, LV, A>
		implements OnHttpResponseListener, OnStopLoadListener, OnRefreshListener, OnLoadMoreListener {
	private static final String TAG = "BaseHttpListFragment";





	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//类相关初始化，必须使用<<<<<<<<<<<<<<<<<<
		super.onCreateView(inflater, container, savedInstanceState);
		setContentView(R.layout.base_http_list_fragment);
		//类相关初始化，必须使用>>>>>>>>>>>>>>>>

		return view;
	}





	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	protected SmartRefreshLayout srlBaseHttpList;

	@Override
	public void initView() {
		super.initView();

		srlBaseHttpList = findView(R.id.srlBaseHttpList);

	}

	@Override
	public void setAdapter(A adapter) {
		if (adapter instanceof BaseAdapter) {
			((BaseAdapter) adapter).setOnLoadListener(new OnLoadListener() {
				@Override
				public void onRefresh() {
					srlBaseHttpList.autoRefresh();
				}

				@Override
				public void onLoadMore() {
					srlBaseHttpList.autoLoadMore();
				}
			});
		}
		super.setAdapter(adapter);
	}

	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










	//Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void initData() {
		super.initData();

	}

	/**
	 * @param page 用-page作为requestCode
	 */
	@Override
	public abstract void getListAsync(int page);

	/**
	 * 将JSON串转为List（已在非UI线程中）
	 * *直接JSON.parseArray(json, getCacheClass());可以省去这个方法，但由于可能json不完全符合parseArray条件，所以还是要保留。
	 * *比如json只有其中一部分能作为parseArray的字符串时，必须先提取出这段字符串再parseArray
	 */
	public abstract List<T> parseArray(String json);


	//Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










	//Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void initEvent() {//必须调用
		super.initEvent();
		setOnStopLoadListener(this);

		srlBaseHttpList.setOnRefreshListener(this);
		srlBaseHttpList.setOnLoadMoreListener(this);
	}


	/**重写后可自定义对这个事件的处理
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	}

	@Override
	public void onRefresh(RefreshLayout refreshlayout) {
		onRefresh();
	}

	@Override
	public void onLoadMore(RefreshLayout refreshlayout) {
		onLoadMore();
	}


	@Override
	public void onStopRefresh() {
		runUiThread(new Runnable() {

			@Override
			public void run() {
				srlBaseHttpList.finishLoadMore(0);
				srlBaseHttpList.finishRefresh();
			}
		});
	}
	@Override
	public void onStopLoadMore(final boolean isHaveMore) {
		runUiThread(new Runnable() {

			@Override
			public void run() {
				if (isHaveMore) {
					srlBaseHttpList.finishLoadMore();
				} else {
					srlBaseHttpList.finishLoadMoreWithNoMoreData();
				}
			}
		});
	}

	/**处理Http请求结果
	 * @param requestCode  = -page {@link #getListAsync(int)}
	 * @param resultJson
	 * @param e
	 */
	@Override
	public void onHttpResponse(final int requestCode, final String resultJson, final Exception e) {
		runThread(TAG + "onHttpResponse", new Runnable() {

			@Override
			public void run() {
				int page = 0;
				if (requestCode > 0) {
					Log.w(TAG, "requestCode > 0, 应该用BaseListFragment#getListAsync(int page)中的page的负数作为requestCode!");
				} else {
					page = - requestCode;
				}

				onResponse(page, parseArray(resultJson), e);
			}
		});
	}

	/**处理结果
	 * @param page
	 * @param list
	 * @param e
	 */
	public void onResponse(int page, List<T> list, Exception e) {
		if ((list == null || list.isEmpty()) && e != null) {
			onLoadFailed(page, e);
		} else {
			onLoadSucceed(page, list);
		}
	}


	//生命周期、onActivityResult<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



	//生命周期、onActivityResult>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


	//Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








	//内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



	//内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}