/*Copyright ©2016 TommyLemon(https://github.com/TommyLemon/APIJSON)

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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import uigox.demo.activity_fragment.UserActivity;
import uigox.demo.R;
import uigox.demo.model.CommentItem;
import zuo.biao.library.base.BaseView;
import zuo.biao.library.util.ImageLoaderUtil;
import zuo.biao.library.util.StringUtil;
import zuo.biao.library.util.TimeUtil;

/**评论
 * @author Lemon
 * @use
 * <br> CommentView commentView = new CommentView(context);
 * <br> adapter中使用:[具体参考.DemoAdapter2(getView使用自定义View的写法)]
 * <br> convertView = commentView.createView();
 * <br> commentView.bindView(data, position, viewType);
 * <br> 或  其它类中使用:
 * <br> containerView.addView(commentView.createView());
 * <br> commentView.bindView(data);
 * <br> 然后
 * <br> commentView.OnCommentClickListener(onCommentClickListener);//非必需
 * <br> commentView.OnShowAllListener(onShowAllListener);//非必需
 * <br> commentView.setOnDataChangedListener(onDataChangedListener);  data = commentView.getData();//非必需
 * <br> commentView.setOnViewClickListener(onViewClickListener);//非必需
 * <br> ...
 */
public class CommentView extends BaseView<CommentItem> implements View.OnClickListener {

	/**点击评论监听回调
	 */
	public interface OnCommentClickListener {
		void onCommentClick(CommentItem item, int position, int index, boolean isLong);
	}

	/**显示更多监听回调
	 * @author Lemon
	 */
	public interface OnShowAllListener {
		void onShowAll(int position, CommentView bv, boolean show);
	}

	private OnCommentClickListener onCommentClickListener;
	public CommentView setOnCommentClickListener(OnCommentClickListener onCommentClickListener) {
		this.onCommentClickListener = onCommentClickListener;
		return this;
	}

	private OnShowAllListener onShowAllListener;
	public CommentView setOnShowAllListener(OnShowAllListener onShowAllListener) {
		this.onShowAllListener = onShowAllListener;
		return this;
	}



	public CommentView(Activity context) {
		super(context, R.layout.comment_view);
	}

	private LayoutInflater inflater;

	public ImageView ivCommentHead;
	public TextView tvCommentName;
	public TextView tvCommentContent;
	public TextView tvCommentTime;


	public LinearLayout llCommentContainer;

	@Override
	public View createView() {
		ivCommentHead = findViewById(R.id.ivCommentHead, this);

		tvCommentName = findViewById(R.id.tvCommentName, this);
		tvCommentContent = findViewById(R.id.tvCommentContent);
		tvCommentTime = findViewById(R.id.tvCommentTime);

		llCommentContainer = findViewById(R.id.llCommentContainer);

		return super.createView();
	}


	private boolean showAll = false;
	public void setShowAll(Boolean showAll) {
		this.showAll = showAll == null ? false : showAll;
	}

	@Override
	public void bindView(CommentItem data){
		this.data = data;

		String name = StringUtil.getTrimedString(data.getUser().getName());
		String content = StringUtil.getTrimedString(data.getComment().getContent());

		tvCommentName.setText("" + name);
		tvCommentContent.setText("" + content);
		tvCommentTime.setText("" + TimeUtil.getSmartDate(data.getDate()));
		ImageLoaderUtil.loadImage(ivCommentHead, data.getUser().getHead(), ImageLoaderUtil.TYPE_OVAL);

		setChildComment();
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.ivCommentHead || id == R.id.tvCommentName) {
			toActivity(UserActivity.createIntent(context, data.getUser().getId()));
		} else if (id == R.id.tvCommentContainerViewMore) {
			if (onShowAllListener != null) {
				onShowAllListener.onShowAll(position, this, true);
			}
		}
	}

	public CommentContainerView commentContainerView;
	/**显示子评论
	 */
	public void setChildComment() {
		if (commentContainerView == null) {
			commentContainerView = new CommentContainerView(context);
			llCommentContainer.removeAllViews();
			llCommentContainer.addView(commentContainerView.createView());

			commentContainerView.setOnCommentClickListener(onCommentClickListener);
			commentContainerView.tvCommentContainerViewMore.setOnClickListener(this);
		}

		commentContainerView.setMaxShowCount(showAll ? 0 : 3);
		commentContainerView.bindView(data.getChildList());
	}

}
