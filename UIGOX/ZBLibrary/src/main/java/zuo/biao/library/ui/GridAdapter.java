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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;

import zuo.biao.library.R;
import zuo.biao.library.model.Entry;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.StringUtil;


/**通用网格Adapter(url, name)
 * *适用于gridView
 * @author Lemon
 * @use new GridAdapter(...); 具体参考.DemoAdapter
 */
public class GridAdapter extends BaseAdapter {
	private static final String TAG = "GridAdapter";

	public GridAdapter(Activity context) {
		this(context, R.layout.grid_item);
	}
	private final Activity context;
	private final LayoutInflater inflater;
	public GridAdapter(Activity context, int layoutRes) {
		this.context = context;
		this.inflater = context.getLayoutInflater();
		setLayoutRes(layoutRes);
	}



	private int layoutRes;//item视图资源
	public void setLayoutRes(int layoutRes) {
		this.layoutRes = layoutRes;
	}
	private boolean hasName = true;//是否显示名字
	public GridAdapter setHasName(boolean hasName) {
		this.hasName = hasName;
		return this;
	}
	private boolean hasCheck = false;//是否使用标记功能
	public GridAdapter setHasCheck(boolean hasCheck) {
		this.hasCheck = hasCheck;
		return this;
	}

	//item标记功能，不需要可以删除<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	private HashMap<Integer, Boolean> hashMap;//实现选中标记的列表，不需要可以删除。这里可用List<Integer> checkedList代替
	public boolean getItemChecked(int position) {
		if (hasCheck == false) {
			Log.e(TAG, "<<< !!! hasCheck == false  >>>>> ");
			return false;
		}
		return hashMap.get(position);
	}
	public void setItemChecked(int position, boolean isChecked) {
		if (hasCheck == false) {
			Log.e(TAG, "<<< !!! hasCheck == false >>>>> ");
			return;
		}
		hashMap.put(position, isChecked);
		notifyDataSetChanged();
	}
	//item标记功能，不需要可以删除>>>>>>>>>>>>>>>>>>>>>>>>>>>>>



	private List<Entry<String, String>> list;
	/**刷新列表
	 * @param list
	 */
	public synchronized void refresh(List<Entry<String, String>> list) {
		if (list != null && list.size() > 0) {
			initList(list);
		}
		if (hasCheck) {
			selectedCount = 0;
			for (int i = 0; i < this.list.size(); i++) {
				if (getItemChecked(i) == true) {
					selectedCount ++;
				}
			}
		}
		notifyDataSetChanged();
	}

	/**标记List<String>中的值是否已被选中。
	 * 不需要可以删除，但“this.list = list;”这句
	 * 要放到constructor【这个adapter只有ModleAdapter(Context context, List<Object> list)这一个constructor】里去
	 * @param list
	 * @return
	 */
	@SuppressLint("UseSparseArrays")
	private void initList(List<Entry<String, String>> list) {
		this.list = list;
		if (hasCheck) {
			hashMap = new HashMap<Integer, Boolean>();
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					hashMap.put(i, false);
				}
			}
		}
	}


	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Entry<String, String> getItem(int position) {
		return list.get(position);
	}
	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}




	public int selectedCount = 0;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = convertView == null ? null : (ViewHolder) convertView.getTag();
		if (holder == null) {
			convertView = inflater.inflate(layoutRes, parent, false);

			holder = new ViewHolder();
			holder.ivGridItemHead = convertView.findViewById(R.id.ivGridItemHead);
			if (hasName) {
				holder.tvGridItemName = convertView.findViewById(R.id.tvGridItemName);
			}
			if (hasCheck) {
				holder.ivGridItemCheck = convertView.findViewById(R.id.ivGridItemCheck);
			}

			convertView.setTag(holder);
		}

		final Entry<String, String> kvb = getItem(position);
		final String name = kvb.getValue();

		Glide.with(context).load(kvb.getKey()).into(holder.ivGridItemHead);

		if (hasName) {
			holder.tvGridItemName.setVisibility(View.VISIBLE);
			holder.tvGridItemName.setText(StringUtil.getTrimedString(name));
		}

		if (hasCheck) {
			holder.ivGridItemCheck.setVisibility(View.VISIBLE);

			holder.ivGridItemCheck.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setItemChecked(position, !getItemChecked(position));
					Log.i(TAG, (getItemChecked(position) ? "" : "取消") + "选择第 " + position + " 个item name=" + name);
				}
			});
		}

		return convertView;
	}

	static class ViewHolder {
		public ImageView ivGridItemHead;
		public TextView tvGridItemName;
		public ImageView ivGridItemCheck;
	}


}
