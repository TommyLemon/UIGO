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

import java.util.List;

import apijson.JSONResponse;
import zuo.biao.library.base.BaseView;
import zuo.biao.library.base.BaseView.OnDataChangedListener;
import zuo.biao.library.interfaces.AdapterCallBack;
import zuo.biao.library.interfaces.CacheCallBack;
import zuo.biao.library.interfaces.OnBottomDragListener;
import zuo.biao.library.manager.CacheManager;
import uigo.x.HttpManager.OnHttpResponseListener;
import zuo.biao.library.ui.AlertDialog;
import zuo.biao.library.ui.AlertDialog.OnDialogButtonClickListener;
import zuo.biao.library.util.CommonUtil;
import zuo.biao.library.util.EditTextUtil;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.SettingUtil;
import zuo.biao.library.util.StringUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;

import uigox.demo.R;
import uigox.demo.adapter.CommentAdapter;
import uigox.demo.application.DemoApplication;
import uigox.demo.base.BaseHttpListActivity;
import uigox.demo.manager.HttpManager;
import uigox.demo.model.Comment;
import uigox.demo.model.CommentItem;
import uigox.demo.model.MomentItem;
import uigox.demo.model.User;
import uigox.demo.util.CommentUtil;
import uigox.demo.util.HttpRequest;
import uigox.demo.view.CommentView.OnCommentClickListener;
import uigox.demo.view.MomentView;
import uigox.demo.server.model.BaseModel;

/**用户列表界面fragment
 * @author Lemon
 * @use new CommentListFragment(),详细使用见.DemoFragmentActivity(initData方法内)
 * @must 查看 .HttpManager 中的@must和@warn
 *       查看 .SettingUtil 中的@must和@warn
 */
public class MomentActivity extends BaseHttpListActivity<CommentItem, ListView, CommentAdapter>
implements CacheCallBack<CommentItem>, OnHttpResponseListener, OnCommentClickListener
, OnBottomDragListener, OnClickListener, OnDialogButtonClickListener {
	private static final String TAG = "MomentActivity";

	//启动方法<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	public static final String INTENT_MOMENT_ID = "INTENT_MOMENT_ID";
	public static final String INTENT_SHOW_KEYBOARD = "INTENT_SHOW_KEYBOARD";
	public static final String INTENT_TO_COMMENT_ID = "INTENT_TO_COMMENT_ID";
	public static final String INTENT_TO_USER_ID = "INTENT_TO_USER_ID";
	public static final String INTENT_TO_USER_NAME = "INTENT_TO_USER_NAME";

	/**启动这个Activity的Intent
	 * @param context
	 * @param momentId
	 * @param showKeyboard
	 * @return
	 */
	public static Intent createIntent(Context context, long momentId, boolean showKeyboard) {
		return createIntent(context, momentId, showKeyboard, 0, 0, null);
	}
	/**启动这个Activity的Intent
	 * @param context
	 * @param momentId
	 * @param toCommentId
	 * @param toUserId
	 * @param toUserName
	 * @return
	 */
	public static Intent createIntent(Context context, long momentId, long toCommentId, long toUserId, String toUserName) {
		return createIntent(context, momentId, toCommentId > 0, toCommentId, toUserId, toUserName);
	}
	/**启动这个Activity的Intent
	 * @param context
	 * @param momentId
	 * @param showKeyboard
	 * @param toCommentId
	 * @param toUserId
	 * @param toUserName
	 * @return
	 */
	public static Intent createIntent(Context context, long momentId, boolean showKeyboard
			, long toCommentId, long toUserId, String toUserName) {
		return new Intent(context, MomentActivity.class).
				putExtra(INTENT_MOMENT_ID, momentId).
				putExtra(INTENT_SHOW_KEYBOARD, showKeyboard).
				putExtra(INTENT_TO_COMMENT_ID, toCommentId).
				putExtra(INTENT_TO_USER_NAME, toUserName);
	}



	//启动方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	private long momentId;
	private boolean showKeyboard;
	private long toCommentId;//列表可能不包含toCommentId
	private String toUserName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moment_activity, this);

		intent = getIntent();
		momentId = getIntent().getLongExtra(INTENT_MOMENT_ID, momentId);
		if (momentId <= 0) {
			finishWithError("动态不存在！");
			return;
		}
		showKeyboard = intent.getBooleanExtra(INTENT_SHOW_KEYBOARD, showKeyboard);
		toCommentId = intent.getLongExtra(INTENT_TO_COMMENT_ID, toCommentId);
		toUserName = intent.getStringExtra(INTENT_TO_USER_NAME);


		initCache(this);

		//功能归类分区方法，必须调用<<<<<<<<<<
		initView();
		initData();
		initEvent();
		//功能归类分区方法，必须调用>>>>>>>>>>

		srlBaseHttpList.autoRefresh();
	}


	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	private EditText etMomentInput;

	private MomentView momentView;
	@Override
	public void initView() {//必须调用
		super.initView();

		etMomentInput = (EditText) findViewById(R.id.etMomentInput);

		momentView = new MomentView(context);
		lvBaseList.addHeaderView(momentView.createView());
		momentView.setShowComment(false);
	}

	private MomentItem momentItem;
	/**
	 * 内部转到UI线程
	 * @param momentItem_
	 */
	private void setHead(MomentItem momentItem_) {
		this.momentItem = momentItem_;
		if (momentItem == null) {
			momentItem = new MomentItem(momentId);
		}
		runUiThread(new Runnable() {

			@Override
			public void run() {
				momentView.bindView(momentItem);

				if (showKeyboard) {//在etMomentInput被绑定前调用showInput崩溃 //{
					showKeyboard = false;

					Comment comment = new Comment(toCommentId);
					User user = new User();
					user.setName(toUserName);
					showInput(new CommentItem().setComment(comment).setUser(user));
				}
			}
		});
	}

	@Override
	public void setList(final List<CommentItem> list) {
		runThread(TAG + "setList", new Runnable() {

			@Override
			public void run() {
				final List<CommentItem> list_ = CommentUtil.toDoubleLevelList(list);

				runUiThread(new Runnable() {

					@Override
					public void run() {
						setList(new AdapterCallBack<CommentAdapter>() {

							@Override
							public CommentAdapter createAdapter() {
								return new CommentAdapter(context, MomentActivity.this);
							}

							@Override
							public void refreshAdapter() {
								//	adapter.setShowAll(true);
								adapter.refresh(list_);
							}
						});
					}
				});
			}
		});
	}


	/**显示输入评论
	 * @param toCommentItem_
	 */
	public void showInput(CommentItem toCommentItem_) {
		showInput(toCommentItem_, -1, -1);
	}
	/**显示输入评论
	 * @param toCommentItem_
	 * @param position
	 */
	public void showInput(CommentItem toCommentItem_, final int position, final int index) {
		this.toCommentItem = toCommentItem_;
		final long toCommentId = toCommentItem == null ? 0 : toCommentItem.getComment().getId();
		runUiThread(new Runnable() {

			@Override
			public void run() {
				if (toCommentId <= 0 || toCommentItem == null) {
					etMomentInput.setHint("评论");
				} else {
					etMomentInput.setHint("回复：" + StringUtil.getTrimedString(toCommentItem.getUser().getName()));
				}
				EditTextUtil.showKeyboard(context, etMomentInput);//, toGetWindowTokenView);

				if (position >= 0) {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							if (isAlive()) {
								lvBaseList.setSelection(position + lvBaseList.getHeaderViewsCount());
							}
						}
					}, 500);
				}
			}
		});
	}

	private void hideKeyboard() {
		toCommentItem = null;
		runUiThread(new Runnable() {

			@Override
			public void run() {
				etMomentInput.setHint("评论");
				EditTextUtil.hideKeyboard(context, etMomentInput);
			}
		});
	}

	//UI显示区(操作UI，但不存在数据获取或处理代码，也不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>










	//Data数据区(存在数据获取或处理代码，但不存在事件监听代码)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void initData() {//必须调用
		super.initData();

		runThread(TAG + "initData", new Runnable() {

			@Override
			public void run() {

				if (SettingUtil.cache) {
					momentItem = CacheManager.getInstance().get(MomentItem.class, "" + momentId);
				}
				final List<CommentItem> list = CacheManager.getInstance().getList(
						getCacheClass(), getCacheGroup(), 0, getCacheCount());
				runUiThread(new Runnable() {

					@Override
					public void run() {
						setHead(momentItem);
						setList(list);
					}
				});
			}
		});

	}


	private CommentItem toCommentItem;
	/**发送评论(回复)
	 */
	public void sendComment() {
		String content = StringUtil.getTrimedString(etMomentInput);
		if (StringUtil.isNotEmpty(content, true) == false) {
			showShortToast("请先输入评论");
			return;
		}
		long toCommentId = toCommentItem == null ? 0 : toCommentItem.getId();
		long toUserId = toCommentId <= 0 ? 0 : toCommentItem.getUserId();

		HttpRequest.addComment(momentId, toCommentId, toUserId, content, toCommentId <= 0 ? HTTP_COMMENT : HTTP_REPLY, this);

		hideKeyboard();
	}


	/**删除评论
	 * @param commentItem
	 */
	private void deleteComment(CommentItem commentItem) {
		long id = commentItem == null ? 0 : commentItem.getId();
		if (id <= 0) {
			Log.e(TAG, "deleteComment  id <= 0 >> return;");
			return;
		}
		HttpRequest.deleteComment(id, BaseModel.value(commentItem.getUserId()), HTTP_DELETE, this);
	}



	private boolean loadHead = true;
	@Override
	public void getListAsync(final int page) {
		if (loadHead && page <= HttpManager.PAGE_NUM_0) {
			HttpRequest.getMoment(momentId, HTTP_GET_MOMENT, MomentActivity.this);
		}
		HttpRequest.getCommentList(momentId, 0, page, -page, this);
	}

	@Override
	public List<CommentItem> parseArray(String json) {
		return new JSONResponse(json).getList(CommentItem.class);
	}

	@Override
	public Class<CommentItem> getCacheClass() {
		return CommentItem.class;
	}
	@Override
	public String getCacheGroup() {
		return "momentId=" + momentId;
	}
	@Override
	public String getCacheId(CommentItem data) {
		return data == null ? null : "" + data.getId();
	}
	@Override
	public int getCacheCount() {
		return 10;
	}


	//Data数据区(存在数据获取或处理代码，但不存在事件监听代码)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








	//Event事件区(只要存在事件监听代码就是)<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


	@Override
	public void initEvent() {//必须调用
		super.initEvent();

		findViewById(R.id.tvMomentSend).setOnClickListener(this);


		momentView.setOnViewClickListener(new BaseView.OnViewClickListener() {

			@Override
			public void onViewClick(BaseView bv, View v) {
                int id = v.getId();
                if (id == R.id.tvMomentViewContent) {
                    if (momentItem != null) {
                        CommonUtil.copyText(context, momentItem.getMoment().getContent());
                    }
                } else if (id == R.id.ivMomentViewComment) {
                    showInput(null);
                } else {
                    momentView.onClick(v);
                }
			}
		});
		momentView.setOnDataChangedListener(new OnDataChangedListener() {

			@Override
			public void onDataChanged() {
				if (momentView.getStatus() == MomentItem.STATUS_DELETED) {
					finish();
				} else {
					setHead(momentView.getData());
				}
			}
		});


		//setOnItemClickListener会让ItemView内所有View显示onTouch background
		lvBaseList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onComemntItemClick(position, false);
			}
		});

		lvBaseList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				onComemntItemClick(position, true);
				return true;
			}
		});
	}

	private void onComemntItemClick(int position, boolean isLong) {
		if (adapter == null){
			return;
		}
		position = position - lvBaseList.getHeaderViewsCount();
		if (position < 0 || position >= adapter.getCount()) {
			return;
		}

		onCommentClick(adapter.getItem(position), position, -1, isLong);
	}

	@Override
	public void onCommentClick(CommentItem item, int position, int index, boolean isLong) {
		if (isLong) {
			if (item == null || momentItem == null) {
				return;
			}
			if (DemoApplication.getInstance().isCurrentUser(momentItem.getUserId()) == false
					&& DemoApplication.getInstance().isCurrentUser(item.getUserId()) == false) {
				showShortToast("只能删除自己的或自己的动态下的评论");
				return;
			}
			toCommentItem = item;
			new AlertDialog(context, null, "删除这条评论?", true, DIALOG_DELETE_COMMENT, MomentActivity.this).show();
		} else {
			showInput(item, position, index);
		}
	}

	private static final int DIALOG_DELETE_COMMENT = 1;

	@Override
	public void onDialogButtonClick(int requestCode, boolean isPositive) {
		if (isPositive) {
			deleteComment(toCommentItem);
		}
	}

	private static final int HTTP_GET_MOMENT = 1;
	private static final int HTTP_COMMENT = 2;
	private static final int HTTP_REPLY = 3;
	private static final int HTTP_DELETE = 4;
	@Override
	public void onHttpResponse(final int requestCode, final String resultJson, final Exception e) {
		runThread(TAG + "onHttpResponse", new Runnable() {

			@Override
			public void run() {

				JSONResponse response = new JSONResponse(resultJson);
				if (requestCode <= 0) {
					if (requestCode == 0 && momentItem != null) {
						setHead(momentItem.setCommentCount(response.getTotal()));
					}
					MomentActivity.super.onHttpResponse(requestCode, resultJson, e);
					return;
				}

				if (requestCode == HTTP_GET_MOMENT) {
					MomentItem data = JSONResponse.toObject(response, MomentItem.class);
					if (data == null || data.getId() <= 0) {
						if (JSONResponse.isSuccess(response)) {
							showShortToast("动态不存在");
							MomentActivity.super.finish();//需要动画，且不需要保存缓存
							return;
						}
						showShortToast("获取动态失败，请检查网络后重试");
						return;
					}
					setHead(data);
					return;
				}

				if (verifyHttpLogin(response.getCode()) == false) {
					return;
				}

				JSONResponse comment = response.getJSONResponse(Comment.class.getSimpleName());
				final boolean succeed = JSONResponse.isSuccess(comment);
				String operation = "操作";
				switch (requestCode) {
				case HTTP_COMMENT: // 新增评论
					operation = "评论";
					break;
				case HTTP_REPLY:// 回复
					operation = "回复";
					break;
				case HTTP_DELETE:// 删除
					operation = "删除";
					if (succeed) {//MomentItem中仍然存有Comment，可重写saveCache，单独存里面的Moment和Comment等
						CacheManager.getInstance().remove(getCacheClass(), comment == null ? "0" : "" + comment.getId());
					}
					break;
				default:
					return;
				}
				showShortToast(operation + (succeed ? "成功" : "失败，请检查网络后重试"));

				runUiThread(new Runnable() {
					
					@Override
					public void run() {
						if (succeed) {
							etMomentInput.setText("");

							loadHead = false;
							MomentActivity.super.onRefresh();
						}
					}
				});
			}
		});
	}


	@Override
	public void onDragBottom(boolean rightToLeft) {
		if (rightToLeft) {

			return;
		}

		finish();
	}


	@Override
	public void onRefresh() {
		loadHead = true;
		super.onRefresh();
	}

	//系统自带监听方法 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void onClick(View v) {
        if (v.getId() == R.id.tvMomentSend) {
            sendComment();
        }
	}

	//类相关监听<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


	//类相关监听>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>



	//系统自带监听方法>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


	//Event事件区(只要存在事件监听代码就是)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>








	//内部类,尽量少用<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


	//内部类,尽量少用>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


}