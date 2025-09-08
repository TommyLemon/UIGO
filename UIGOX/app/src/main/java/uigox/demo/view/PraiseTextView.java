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

import android.app.Activity;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import java.util.ArrayList;
import java.util.List;

import uigox.demo.activity_fragment.UserActivity;
import uigox.demo.model.User;
import zuo.biao.library.util.CommonUtil;

/**点赞用户列表
 */
public class PraiseTextView extends AppCompatTextView {

	private CommentTextView.OnNameClickListener listener;
	public void setOnNameClickListener(CommentTextView.OnNameClickListener l) {
		this.listener = l;
	}


	public PraiseTextView(Context context) {
		super(context);
	}

	public PraiseTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PraiseTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public static final int DIVIDER_LENGTH = "、".length();
	/**设置View
	 * @param list
	 */
	public void setView(List<User> list) {
		if (list == null || list.isEmpty()) {
			setText("");
			return;
		}
		//		//影响列表滚动流畅度，去除无效User
		//		List<User> list = new ArrayList<User>();
		//		for (User user: list_) {
		//			if (user != null && StringUtil.isNotEmpty(user.getName(), true)) {
		//				list.add(user);
		//			}
		//		}
		int count = list == null ? 0 : list.size();
		if (count > 9) {
			list = list.subList(0, 9);
		}


		//拼接字符串
		List<Integer> dividerIndexes = new ArrayList<Integer>();
		String content = "";
		User user;
		for (int i = 0; i < list.size(); i++) {
			user = list.get(i);
			dividerIndexes.add(content.length());
			content += (i <= 0 ? "" : "、") + (user == null ? " " : user.getName());
		}
		dividerIndexes.add(content.length());//最后一个

		//空格保证多行时不会点击空白处总是响应最后一个
		SpannableString msp = new SpannableString(content + (count <= 9 ? " " : " 等觉得很赞"));//" + count + "人觉得很赞"));

		//设置可点击名称
		for (int i = 0; i < dividerIndexes.size() - 1; i++) {
			setSpan(msp, i, dividerIndexes.get(i) + (i <= 0 ? 0 : DIVIDER_LENGTH), dividerIndexes.get(i + 1), list.get(i));
		}

		setText(msp);
		setMovementMethod(LinkMovementMethod.getInstance());
	}

	private void setSpan(SpannableString msp, final int index, int start, int end, final User user) {
		msp.setSpan(new ClickableSpan() {

			@Override
			public void onClick(View widget) {
				onNameClick(index, widget, user);
			}
		}, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	}


	public void onNameClick(int index, View widget, User user) {
		if (listener != null) {
			listener.onNameClick(index, widget, user);
		} else {
			CommonUtil.toActivity((Activity) getContext(), UserActivity.createIntent(
					getContext(), user == null ? 0 : user.getId()));
		}
	}

	public abstract class ClickableSpan extends android.text.style.ClickableSpan {
		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(getResources().getColor(zuo.biao.library.R.color.blue)); // 设置超链接颜色
			ds.setUnderlineText(false); // 超链接下划线
		}
	}
}