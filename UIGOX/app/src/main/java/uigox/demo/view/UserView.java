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

package uigox.demo.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import uigox.demo.activity_fragment.UserActivity;
import uigox.demo.R;
import uigox.demo.model.User;
import zuo.biao.library.base.BaseView;
import zuo.biao.library.util.ImageLoaderUtil;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.StringUtil;

/**用户
 * @author Lemon
 * @use
 * <br> UserView userView = new UserView(context);
 * <br> adapter中使用:[具体参考.DemoAdapter2(getView使用自定义View的写法)]
 * <br> convertView = userView.createView(inflater, position, viewType);
 * <br> userView.bindView(data, position, viewType);
 * <br> 或  其它类中使用:
 * <br> containerView.addView(userView.createView());
 * <br> userView.bindView(data);
 * <br> 然后
 * <br> userView.setOnDataChangedListener(onDataChangedListener);data = userView.getData();//非必需
 * <br> userView.setOnClickListener(onClickListener);//非必需
 * <br> ...
 */
public class UserView extends BaseView<User> implements OnClickListener {
	private static final String TAG = "UserView";

	public UserView(Activity context) {
		super(context, R.layout.user_view);
	}

	public ImageView ivUserViewHead;

	public TextView tvUserViewSex;

	public TextView tvUserViewName;
	public TextView tvUserViewId;
	public TextView tvUserViewTag;
	@Override
	public View createView() {
		ivUserViewHead = findViewById(R.id.ivUserViewHead, this);

		tvUserViewSex = findViewById(R.id.tvUserViewSex, this);

		tvUserViewName = findViewById(R.id.tvUserViewName, this);
		tvUserViewId = findViewById(R.id.tvUserViewId);
		tvUserViewTag = findViewById(R.id.tvUserViewTag, this);

		return super.createView();
	}

	@Override
	public void bindView(User data){
		if (data == null) {
			Log.e(TAG, "bindView data == null >> data = new User(); ");
			data = new User();
		}
		this.data = data;

		ImageLoaderUtil.loadImage(ivUserViewHead, data.getHead(), ImageLoaderUtil.TYPE_OVAL);

		tvUserViewSex.setBackgroundResource(data.getSex() == User.SEX_FEMALE
				? zuo.biao.library.R.drawable.circle_pink : zuo.biao.library.R.drawable.circle_blue);
		tvUserViewSex.setText(data.getSex() == User.SEX_FEMALE ?  "女" : "男");
		tvUserViewSex.setTextColor(getColor(data.getSex() == User.SEX_FEMALE ? zuo.biao.library.R.color.pink : zuo.biao.library.R.color.blue));

		tvUserViewName.setText(StringUtil.getTrimedString(data.getName()));
		tvUserViewId.setText("ID:" + data.getId());
		tvUserViewTag.setText("Tag:" + StringUtil.getTrimedString(data.getTag()));
	}

	@Override
	public void onClick(View v) {
		if (data == null) {
			return;
		}
		toActivity(UserActivity.createIntent(context, data.getId()));
	}
}