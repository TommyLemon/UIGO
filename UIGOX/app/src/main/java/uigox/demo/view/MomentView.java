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
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uigox.demo.activity_fragment.LoginActivity;
import uigox.demo.activity_fragment.MomentActivity;
import uigox.demo.activity_fragment.UserActivity;
import uigox.demo.activity_fragment.UserListFragment;
import uigox.demo.R;
import uigox.demo.activity_fragment.UserListActivity;
import uigox.demo.application.DemoApplication;
import uigox.demo.model.CommentItem;
import uigox.demo.model.Moment;
import uigox.demo.model.MomentItem;
import uigox.demo.model.User;
import uigox.demo.util.HttpRequest;
import apijson.JSONResponse;
import zuo.biao.library.base.BaseView;
import zuo.biao.library.manager.CacheManager;
import uigo.x.HttpManager.OnHttpResponseListener;
import zuo.biao.library.model.Entry;
import zuo.biao.library.ui.AlertDialog;
import zuo.biao.library.ui.AlertDialog.OnDialogButtonClickListener;
import zuo.biao.library.ui.GridAdapter;
import zuo.biao.library.ui.WebViewActivity;
import zuo.biao.library.util.ImageLoaderUtil;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.ScreenUtil;
import zuo.biao.library.util.StringUtil;
import zuo.biao.library.util.TimeUtil;

/**动态
 * @author Lemon
 * @use
 * <br> MomentView momentView = new MomentView(context);
 * <br> adapter中使用:[具体参考.DemoAdapter2(getView使用自定义View的写法)]
 * <br> convertView = momentView.createView();
 * <br> momentView.bindView(data, position, viewType);
 * <br> 或  其它类中使用:
 * <br> containerView.addView(momentView.createView());
 * <br> momentView.bindView(data);
 * <br> 然后
 * <br> momentView.setOnPictureClickListener(onPictureClickListener);//非必需
 * <br> momentView.setOnDataChangedListener(onDataChangedListener);  data = momentView.getData();//非必需
 * <br> momentView.setOnViewClickListener(onViewClickListener);//非必需
 * <br> ...
 */
public class MomentView extends BaseView<MomentItem> implements OnClickListener
, OnHttpResponseListener, OnDialogButtonClickListener, OnItemClickListener {
	private static final String TAG = "MomentView";

	public interface OnPictureClickListener {
		void onClickPicture(int momentPosition, MomentView momentView, int pictureIndex);
	}

	private OnPictureClickListener onPictureClickListener;
	/**设置点击图片监听
	 * @param onPictureClickListener
	 */
	public void setOnPictureClickListener(OnPictureClickListener onPictureClickListener) {
		this.onPictureClickListener = onPictureClickListener;
	}

	public MomentView(Activity context) {
		super(context, R.layout.moment_view);
	}


	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	public View llMomentViewContainer;

	public ImageView ivMomentViewHead;

	public TextView tvMomentViewName;
	public TextView tvMomentViewStatus;

	public TextView tvMomentViewContent;

	public GridView gvMomentView;

	public TextView tvMomentViewDate;
	public ImageView ivMomentViewPraise;
	public ImageView ivMomentViewComment;

	public ViewGroup llMomentViewPraise;
	public PraiseTextView tvMomentViewPraise;

	public View vMomentViewDivider;

	public ViewGroup llMomentViewCommentContainer;
	@Override
	public View createView() {
		llMomentViewContainer = findViewById(R.id.llMomentViewContainer);

		ivMomentViewHead = findViewById(R.id.ivMomentViewHead, this);

		tvMomentViewName = findViewById(R.id.tvMomentViewName, this);
		tvMomentViewStatus = findViewById(R.id.tvMomentViewStatus, this);

		tvMomentViewContent = findViewById(R.id.tvMomentViewContent, this);

		gvMomentView = findViewById(R.id.gvMomentView);

		tvMomentViewDate = findViewById(R.id.tvMomentViewDate);
		ivMomentViewPraise = findViewById(R.id.ivMomentViewPraise, this);
		ivMomentViewComment = findViewById(R.id.ivMomentViewComment, this);

		llMomentViewPraise = findViewById(R.id.llMomentViewPraise, this);
		tvMomentViewPraise = findViewById(R.id.tvMomentViewPraise, this);

		vMomentViewDivider = findViewById(R.id.vMomentViewDivider);

		llMomentViewCommentContainer = findViewById(R.id.llMomentViewCommentContainer);

		return super.createView();
	}


	private User user;
	private Moment moment;
	private long momentId;
	private long userId;

	private boolean isCurrentUser;
	private int status;
	public int getStatus() {
		return status;
	}
	@Override
	public void bindView(MomentItem data_){
		this.data = data_;
		llMomentViewContainer.setVisibility(data == null ? View.GONE : View.VISIBLE);
		if (data == null) {
			Log.w(TAG, "bindView data == null >> return;");
			return;
		}
		this.user = data.getUser();
		this.moment = data.getMoment();
		this.momentId = moment.getId();
		this.userId = moment.getUserId();
		this.isCurrentUser = DemoApplication.getInstance().isCurrentUser(moment.getUserId());
		this.status = data.getMyStatus();

		ImageLoaderUtil.loadImage(ivMomentViewHead, user.getHead());

		tvMomentViewName.setText(StringUtil.getTrimedString(user.getName()));
		tvMomentViewStatus.setText(StringUtil.getTrimedString(data.getStatusString()));
		tvMomentViewStatus.setVisibility(isCurrentUser ? View.VISIBLE : View.GONE);

		tvMomentViewContent.setVisibility(StringUtil.isNotEmpty(moment.getContent(), true) ? View.VISIBLE : View.GONE);
		tvMomentViewContent.setText(StringUtil.getTrimedString(moment.getContent()));

		tvMomentViewDate.setText(TimeUtil.getSmartDate(moment.getDate()) );
		//仅供测试			+ "    P:" + data.getPraiseCount() + "  C:" + data.getCommentCount());

		// 图片
		setPicture(moment.getPictureList());
		// 点赞
		setPraise(data.getIsPraised(), data.getUserList());
		// 评论
		setComment(data.getCommentItemList());

		vMomentViewDivider.setVisibility(llMomentViewPraise.getVisibility() == View.VISIBLE
				&& llMomentViewCommentContainer.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);

	}


	/**设置点赞
	 * @param joined
	 * @param list
	 */
	private void setPraise(boolean joined, List<User> list) {
		ivMomentViewPraise.setImageResource(joined ? R.drawable.praised : R.drawable.praise);
		llMomentViewPraise.setVisibility(list == null || list.isEmpty() ? View.GONE : View.VISIBLE);
		if (llMomentViewPraise.getVisibility() == View.VISIBLE) {
			tvMomentViewPraise.setView(list);
		}
	}

	private boolean showComment = true;
	public void setShowComment(boolean showComment) {
		this.showComment = showComment;
	}
	public boolean getShowComment() {
		return showComment;
	}


	public CommentContainerView commentContainerView;
	/**设置评论
	 * @param list
	 */
	public void setComment(List<CommentItem> list) {
		llMomentViewCommentContainer.setVisibility(showComment == false || list == null || list.isEmpty()
				? View.GONE : View.VISIBLE);

		if (llMomentViewCommentContainer.getVisibility() != View.VISIBLE) {
			Log.i(TAG, "setComment  llMomentViewCommentContainer.getVisibility() != View.VISIBLE >> return;");
			return;
		}

		if (commentContainerView == null) {
			commentContainerView = new CommentContainerView(context);
			llMomentViewCommentContainer.removeAllViews();
			llMomentViewCommentContainer.addView(commentContainerView.createView());

			commentContainerView.setOnCommentClickListener(new CommentView.OnCommentClickListener() {

				@Override
				public void onCommentClick(CommentItem item, int position, int index, boolean isLong) {
					toComment(item, true);
				}
			});
			commentContainerView.tvCommentContainerViewMore.setOnClickListener(this);

			commentContainerView.setMaxShowCount(5);
		}

		commentContainerView.bindView(list);
	}

	private GridAdapter adapter;
	/**设置图片
	 * @param pictureList
	 */
	private void setPicture(List<String> pictureList) {
		List<Entry<String, String>> keyValueList = new ArrayList<Entry<String, String>>();
		if (pictureList != null) {
			for (String picture : pictureList) {
				keyValueList.add(new Entry<String, String>(picture, null));
			}
		}
		int pictureNum = keyValueList.size();
		gvMomentView.setVisibility(pictureNum <= 0 ? View.GONE : View.VISIBLE);
		if (pictureNum <= 0) {
			Log.i(TAG, "setList pictureNum <= 0 >> return;");
			return;
		}

		gvMomentView.setNumColumns(pictureNum <= 1 ? 1 : 3);
		if (adapter == null) {
			adapter = new GridAdapter(context).setHasName(false);
			gvMomentView.setAdapter(adapter);
		}
		adapter.refresh(keyValueList);
		gvMomentView.setOnItemClickListener(this);

		final int gridViewHeight = (int) (ScreenUtil.getScreenSize(context)[0]
				- itemView.getPaddingLeft() - itemView.getPaddingRight()
				- getDimension(R.dimen.moment_view_head_width));
		try {
			if (pictureNum >= 7) {
				gvMomentView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, gridViewHeight));
			} else if (pictureNum >= 4) {
				gvMomentView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, (gridViewHeight*2)/3));
			} else if (pictureNum >= 2) {
				gvMomentView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, gridViewHeight / 3));
			} else {
				gvMomentView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			}
		} catch (Exception e) {
			Log.e(TAG, " setPictureGrid  try int gridViewHeight;...>> catch" + e.getMessage());
		}
	}



	/**跳转到所有评论界面
	 * @param isToComment
	 */
	private void toComment(boolean isToComment) {
		toComment(null, isToComment);
	}
	/**跳转到所有评论界面
	 * @param commentItem
	 * @param isToComment comment有效时为true
	 */
	private void toComment(CommentItem commentItem, boolean isToComment) {
		if (commentItem == null) {
			commentItem = new CommentItem();
		}
		toActivity(MomentActivity.createIntent(context, momentId, isToComment
				, commentItem.getId(), commentItem.getUser().getId(), commentItem.getUser().getName()));
	}

	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










	//Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


	@Override
	public MomentItem getData() {//bindView(null)不会使data == null
		return llMomentViewContainer.getVisibility() == View.VISIBLE ? data : null;
	}


	/**判断是否已登录，如果未登录则弹出登录界面
	 * @return
	 */
	private boolean isLoggedIn() {
		boolean isLoggedIn = DemoApplication.getInstance().isLoggedIn();
		if (isLoggedIn == false) {
			context.startActivity(LoginActivity.createIntent(context));
			context.overridePendingTransition(zuo.biao.library.R.anim.bottom_push_in, zuo.biao.library.R.anim.hold);
		}
		return isLoggedIn;
	}


	/**点赞
	 * @param toPraise
	 */
	public void praise(boolean toPraise) {
		if (data == null || toPraise == data.getIsPraised()) {
			Log.e(TAG, "praiseWork  toPraise == moment.getIsPraise() >> return;");
			return;
		}
		//		setPraise(toPraise, data.getPraiseCount() + (toPraise ? 1 : -1));
		HttpRequest.praiseMoment(momentId, toPraise, toPraise ? HTTP_PRAISE : HTTP_CANCEL_PRAISE, this);
	}

	//Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








	//Event事件监听区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


	@Override
	public void onDialogButtonClick(int requestCode, boolean isPositive) {
		if (isPositive && data != null) {
			data.setMyStatus(MomentItem.STATUS_DELETING);
			bindView(data);
			HttpRequest.deleteMoment(moment.getId(), HTTP_DELETE, this);
		}
	}



	public static final int HTTP_PRAISE = 1;
	public static final int HTTP_CANCEL_PRAISE = 2;
	public static final int HTTP_DELETE = 3;
	@Override
	public void onHttpResponse(int requestCode, String result, Exception e) {
		if (data == null) {
			Log.e(TAG, "onHttpResponse  data == null  >> return;");
			return;
		}
		JSONResponse response = new JSONResponse(result).getJSONResponse(Moment.class.getSimpleName());
		boolean isSucceed = JSONResponse.isSuccess(response);

		boolean refresh = false;
		switch (requestCode) {
		case HTTP_PRAISE:
		case HTTP_CANCEL_PRAISE:
			if (isSucceed) {
				data.setIsPraised(requestCode == HTTP_PRAISE);
				refresh = true;
			} else {
				showShortToast((requestCode == HTTP_PRAISE ? "点赞" : "取消点赞") + "失败，请检查网络后重试");
			}
			break;
		case HTTP_DELETE:
			showShortToast(isSucceed ? zuo.biao.library.R.string.delete_succeed : zuo.biao.library.R.string.delete_failed);
			//只对adapter.getCount()有影响。目前是隐藏的，不需要通知，也不需要刷新adapter，用户手动刷新后自然就更新了。
			if (isSucceed) {
				bindView(null);
				status = MomentItem.STATUS_DELETED;
				CacheManager.getInstance().remove(MomentItem.class, "" + momentId);
			} else {
				status = data.getMyStatus();
				bindView(data);//不需要adapter内更新list    refresh = true;
			}
			break;
		}

		if (refresh) {
			if (onDataChangedListener != null) {
				onDataChangedListener.onDataChanged();
			} else {
				bindView(data);
			}
		}
	}


	@Override
	public void onClick(View v) {
		if (data == null) {
			return;
		}
		if (status == MomentItem.STATUS_PUBLISHING) {
			showShortToast(R.string.publishing);
			return;
		}
		int id = v.getId();

		if (id == R.id.ivMomentViewHead || id == R.id.tvMomentViewName) {
			toActivity(UserActivity.createIntent(context, userId));
		} else if (id == R.id.tvMomentViewStatus) {
			if (status == MomentItem.STATUS_NORMAL) {
				new AlertDialog(context, "", "删除动态", true, 0, this).show();
			}
		} else if (id == R.id.tvMomentViewContent || id == R.id.tvCommentContainerViewMore) {
			toComment(false);
		} else if (id == R.id.tvMomentViewPraise || id == R.id.llMomentViewPraise) {
			toActivity(UserListActivity.createIntent(context, UserListFragment.RANGE_MOMENT, data.getId())
					.putExtra(UserListActivity.INTENT_TITLE, "点赞的人"));
		} else {
			if (isLoggedIn() == false) {
				return;
			}

			if (id == R.id.ivMomentViewPraise) {
				praise(!data.getIsPraised());
			} else if (id == R.id.ivMomentViewComment) {
				toComment(true);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (status == MomentItem.STATUS_PUBLISHING) {
			showShortToast(R.string.publishing);
			return;
		}
		if (onPictureClickListener != null) {
			onPictureClickListener.onClickPicture(this.position, this, position);
		} else {
			toActivity(WebViewActivity.createIntent(context, null
					, adapter == null ? null : adapter.getItem(position).getKey()));
		}
	}

	//Event事件监听区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

}