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

package apijson.demo.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.IFloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.ViewStateListener;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import apijson.demo.InputUtil;
import apijson.demo.R;
import apijson.demo.StringUtil;
import apijson.demo.ui.UIAutoActivity;
import apijson.demo.ui.UIAutoListActivity;
import unitauto.NotNull;
import unitauto.apk.UnitAutoApp;

/**Application
 * @author Lemon
 */
public class DemoApplication extends Application {
    public static final String TAG = "DemoApplication";

    private static final String SPLIT_X = "SPLIT_X";
    private static final String SPLIT_Y = "SPLIT_Y";
    private static final String SPLIT_HEIGHT = "SPLIT_HEIGHT";
    private static final String SPLIT_COLOR = "SPLIT_COLOR";


    private static DemoApplication instance;
    public static DemoApplication getInstance() {
        return instance;
    }

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("mm:ss");

    private boolean isRecover = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (isRecover && isSplitShowing) {
                //通过遍历数组来实现
                // if (currentTime >= System.currentTimeMillis()) {
                //     isRecovering = false;
                //     pbUIAutoSplitY.setVisibility(View.GONE);
                // }
                //
                // MotionEvent event = (MotionEvent) msg.obj;
                // dispatchEventToCurrentActivity(event);
                step ++;
                tvControllerCount.setText(step + "/" + allStep);
                //根据递归链表来实现，能精准地实现两个事件之间的间隔，不受处理时间不一致，甚至卡顿等影响。还能及时终止
                Node<InputEvent> curNode = (Node<InputEvent>) msg.obj;
                currentEventNode = curNode;
                onEventChange(step - 1, curNode.type);

                // if (curNode.disable) {
                //     msg = new Message();
                //     msg.obj = curNode.next;
                //     handleMessage(msg);
                //     // sendMessageDelayed(msg, 50);
                //     return;
                // }

                while (curNode.disable) {
                    step ++;
                    curNode = curNode.next;
                    if (curNode == null) {
                        tvControllerCount.setText(step + "/" + allStep);
                        onEventChange(step - 1, curNode.type);

                        tvControllerPlay.setText("recover");
                        showCoverAndSplit(true, false, getCurrentActivity());
                        return;
                    }
                }


                //暂停，等待时机
                if (curNode.type == InputUtil.EVENT_TYPE_UI || curNode.type == InputUtil.EVENT_TYPE_HTTP) {
                    return;
                }

                Node<InputEvent> prevNode = curNode.prev;
                Node<InputEvent> nextNode = curNode.next;

                InputEvent prevItem = prevNode == null ? null : prevNode.item;
                InputEvent curItem = curNode.item;
                InputEvent nextItem = nextNode == null ? null : nextNode.item;

                if (curItem != null && prevItem != null) {
                    duration += (prevItem == null ? 0 : (curItem.getEventTime() - prevItem.getEventTime()));
                    tvControllerTime.setText(TIME_FORMAT.format(duration));
                }


                splitX = curNode.splitX;
                splitY = curNode.splitY;
                if (floatBall != null && isSplitShowing) {
                    //居然怎么都不更新 vSplitX 和 vSplitY
                    // floatBall.hide();
                    // floatBall.updateX(windowX + splitX - splitSize/2);
                    // floatBall.updateY(windowY + splitY - splitSize/2);
                    // floatBall.show();

                    //太卡
                    if (floatBall.getX() != (curNode.splitX - splitSize / 2)
                            || floatBall.getY() != (curNode.splitY - splitSize / 2)) {
                        // FloatWindow.destroy("floatBall");
                        // floatBall = null;
                        floatBall = showSplit(isSplitShowing, splitX, splitY, "floatBall", vFloatBall, vSplitX, vSplitY);
                    }
                }

                // 分拆为下面两条，都放在 UI 操作后，减少延迟
                // dispatchEventToCurrentActivity(curItem, false);

                if (nextNode == null) {
                    dispatchEventToCurrentActivity(curItem, false);

                    tvControllerPlay.setText("recover");
                    showCoverAndSplit(true, false, getCurrentActivity());
                    return;
                }

                while (nextNode.disable) {
                    step ++;
                    nextNode = nextNode.next;
                    if (nextNode == null) {
                        tvControllerCount.setText(step + "/" + allStep);
                        onEventChange(step - 1, nextNode.type);

                        tvControllerPlay.setText("recover");
                        showCoverAndSplit(true, false, getCurrentActivity());
                        return;
                    }
                }

                msg = new Message();
                msg.obj = nextNode;
                //暂停，等待时机
                if (nextNode.type == InputUtil.EVENT_TYPE_UI || nextNode.type == InputUtil.EVENT_TYPE_HTTP) {
                    handleMessage(msg);
                    // dispatchEventToCurrentActivity(curItem, false);
                    // step ++;
                    // tvControllerCount.setText(step + "/" + allStep);
                    // //根据递归链表来实现，能精准地实现两个事件之间的间隔，不受处理时间不一致，甚至卡顿等影响。还能及时终止
                    // currentEventNode = nextNode;
                    // onEventChange(step - 1, nextNode.type);
                    dispatchEventToCurrentActivity(curItem, false);

                    // sendMessageDelayed(msg, 50);
                }
                else {
                    dispatchEventToCurrentActivity(curItem, false);
                    sendMessageDelayed(msg, curItem == null || nextItem == null ? 0 : nextItem.getEventTime() - curItem.getEventTime());
                }

            }
        }
    };

    public void post(@NonNull Runnable r) {
        handler.post(r);
    }
    public void postDelayed(@NonNull Runnable r, long delayMillis) {
        handler.postDelayed(r, delayMillis);
    }



    private Activity activity;
    int screenWidth;
    int screenHeight;

    int windowWidth;
    int windowHeight;
    int windowX;
    int windowY;

    ViewGroup vFloatCover;
    View vFloatController;
    View vFloatBall, vFloatBall2;
    ViewGroup vSplitX, vSplitX2;
    ViewGroup vSplitY, vSplitY2;

    TextView tvControllerDouble;
    TextView tvControllerReturn;
    TextView tvControllerCount;
    TextView tvControllerPlay;
    TextView tvControllerTime;
    TextView tvControllerForward;
    TextView tvControllerSetting;

    RecyclerView rvControllerTag;

    private int splitX;
    private int splitY;
    private int splitSize;

    @NotNull
    private JSONArray eventList = new JSONArray();

    private RecyclerView.Adapter tagAdapter;

    SharedPreferences cache;
    private long flowId = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        UnitAutoApp.init(this);
        Log.d(TAG, "项目启动 >>>>>>>>>>>>>>>>>>>> \n\n");

        initUIAuto();
    }

    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    public void onUIAutoActivityCreate(Activity activity) {
        Window window = activity.getWindow();
        //反而让 vFloatCover 与底部差一个导航栏高度 window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        // DisplayMetrics outMetrics = new DisplayMetrics();
        // Display display = activity.getWindowManager().getDefaultDisplay();

        // windowWidth = display.getWidth();
        // windowHeight = display.getHeight();
        windowX = getWindowX(activity);
        windowY = getWindowY(activity);

        // display.getRealMetrics(outMetrics);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        windowWidth = screenWidth;
        windowHeight = screenHeight;

        View decorView = window.getDecorView();
        decorView.post(new Runnable() {
            @Override
            public void run() {
                windowWidth = decorView.getWidth();
                windowHeight = decorView.getHeight();
            }
        });
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                windowWidth = decorView.getWidth();
                windowHeight = decorView.getHeight();
            }
        });

        Window.Callback windowCallback = window.getCallback();
        window.setCallback(new Window.Callback() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
//				dispatchEventToCurrentActivity(event);
                addInputEvent(event, activity);
                return windowCallback.dispatchKeyEvent(event);
            }

            @Override
            public boolean dispatchKeyShortcutEvent(KeyEvent event) {
//				dispatchEventToCurrentActivity(event);
                addInputEvent(event, activity);
                return windowCallback.dispatchKeyShortcutEvent(event);
            }

            @Override
            public boolean dispatchTouchEvent(MotionEvent event) {
//				dispatchEventToCurrentActivity(event);
                addInputEvent(event, activity);
                return windowCallback.dispatchTouchEvent(event);
            }

            @Override
            public boolean dispatchTrackballEvent(MotionEvent event) {
//				dispatchEventToCurrentActivity(event);
                addInputEvent(event, activity);
                return windowCallback.dispatchTrackballEvent(event);
            }

            @Override
            public boolean dispatchGenericMotionEvent(MotionEvent event) {
//				dispatchEventToCurrentActivity(event);
// 和 dispatchTouchEvent 重复                addInputEvent(event, activity);
                return windowCallback.dispatchGenericMotionEvent(event);
            }

            @Override
            public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
                return windowCallback.dispatchPopulateAccessibilityEvent(event);
            }

            @Nullable
            @Override
            public View onCreatePanelView(int featureId) {
                return windowCallback.onCreatePanelView(featureId);
            }

            @Override
            public boolean onCreatePanelMenu(int featureId, Menu menu) {
                return windowCallback.onCreatePanelMenu(featureId, menu);
            }

            @Override
            public boolean onPreparePanel(int featureId, View view, Menu menu) {
                return windowCallback.onPreparePanel(featureId, view, menu);
            }

            @Override
            public boolean onMenuOpened(int featureId, Menu menu) {
                return windowCallback.onMenuOpened(featureId, menu);
            }

            @Override
            public boolean onMenuItemSelected(int featureId, MenuItem item) {
                return windowCallback.onMenuItemSelected(featureId, item);
            }

            @Override
            public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {
                windowCallback.onWindowAttributesChanged(attrs);
            }

            @Override
            public void onContentChanged() {
                windowCallback.onContentChanged();
            }

            @Override
            public void onWindowFocusChanged(boolean hasFocus) {
                windowCallback.onWindowFocusChanged(hasFocus);
            }

            @Override
            public void onAttachedToWindow() {
                windowCallback.onAttachedToWindow();
            }

            @Override
            public void onDetachedFromWindow() {
                windowCallback.onDetachedFromWindow();
            }

            @Override
            public void onPanelClosed(int featureId, Menu menu) {
                windowCallback.onPanelClosed(featureId, menu);
            }

            @Override
            public boolean onSearchRequested() {
                return windowCallback.onSearchRequested();
            }

            @Override
            public boolean onSearchRequested(SearchEvent searchEvent) {
                return windowCallback.onSearchRequested(searchEvent);
            }

            @Nullable
            @Override
            public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
                return windowCallback.onWindowStartingActionMode(callback);
            }

            @Nullable
            @Override
            public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
                return windowCallback.onWindowStartingActionMode(callback, type);
            }

            @Override
            public void onActionModeStarted(ActionMode mode) {
                windowCallback.onActionModeStarted(mode);
            }

            @Override
            public void onActionModeFinished(ActionMode mode) {
                windowCallback.onActionModeFinished(mode);
            }
        });

        //都是 0
        // View decorView = window.getDecorView();
        // windowWidth = decorView.getMeasuredWidth();
        // windowHeight = decorView.getMeasuredHeight();

        cache = getSharedPreferences(TAG, Context.MODE_PRIVATE);

        splitX = cache.getInt(SPLIT_X, 0);
        splitY = cache.getInt(SPLIT_Y, 0);
        splitSize = cache.getInt(SPLIT_HEIGHT, dip2px(30));

        if (splitX <= splitSize || splitX >= windowWidth - splitSize) {
            splitX = windowWidth - splitSize - dip2px(30);
        }
        if (splitY <= splitSize || splitY >= windowHeight - splitSize) {
            splitY = windowHeight - splitSize - dip2px(30);
        }
    }

    private void initUIAuto() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStarted(Activity activity) {
                Log.v(TAG, "onActivityStarted  activity = " + activity.getClass().getName());
                onUIEvent(InputUtil.UI_ACTION_START, activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.v(TAG, "onActivityStopped  activity = " + activity.getClass().getName());
                onUIEvent(InputUtil.UI_ACTION_STOP, activity);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.v(TAG, "onActivitySaveInstanceState  activity = " + activity.getClass().getName());
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.v(TAG, "onActivityResumed  activity = " + activity.getClass().getName());
                setCurrentActivity(activity);
                onUIAutoActivityCreate(activity);
                onUIEvent(InputUtil.UI_ACTION_RESUME, activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.v(TAG, "onActivityPaused  activity = " + activity.getClass().getName());
                // setCurrentActivity(activityList.isEmpty() ? null : activityList.get(activityList.size() - 1));
                onUIEvent(InputUtil.UI_ACTION_PAUSE, activity);
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.v(TAG, "onActivityCreated  activity = " + activity.getClass().getName());
                activityList.add(activity);
                //TODO 按键、键盘监听拦截和转发
                onUIEvent(InputUtil.UI_ACTION_CREATE, activity);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.v(TAG, "onActivityDestroyed  activity = " + activity.getClass().getName());
                activityList.remove(activity);
                onUIEvent(InputUtil.UI_ACTION_DESTROY, activity);
            }

        });


        // vFloatCover = new FrameLayout(getInstance());
        // ViewGroup.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        // vFloatCover.setLayoutParams(lp);

        vFloatCover = (ViewGroup) getLayoutInflater().inflate(R.layout.ui_auto_cover_layout, null);
        vFloatController = getLayoutInflater().inflate(R.layout.ui_auto_controller_layout, null);
        vFloatBall = getLayoutInflater().inflate(R.layout.ui_auto_split_ball_layout, null);
        vFloatBall2 = getLayoutInflater().inflate(R.layout.ui_auto_split_ball_layout, null);
        vSplitX = (ViewGroup) getLayoutInflater().inflate(R.layout.ui_auto_split_x_layout, null);
        vSplitX2 = (ViewGroup) getLayoutInflater().inflate(R.layout.ui_auto_split_x_layout, null);
        vSplitY = (ViewGroup) getLayoutInflater().inflate(R.layout.ui_auto_split_y_layout, null);
        vSplitY2 = (ViewGroup) getLayoutInflater().inflate(R.layout.ui_auto_split_y_layout, null);

        tvControllerDouble = vFloatController.findViewById(R.id.tvControllerDouble);
        tvControllerReturn = vFloatController.findViewById(R.id.tvControllerReturn);
        tvControllerCount = vFloatController.findViewById(R.id.tvControllerCount);
        tvControllerPlay = vFloatController.findViewById(R.id.tvControllerPlay);
        tvControllerTime = vFloatController.findViewById(R.id.tvControllerTime);
        tvControllerForward = vFloatController.findViewById(R.id.tvControllerForward);
        tvControllerSetting = vFloatController.findViewById(R.id.tvControllerSetting);

        rvControllerTag = vFloatController.findViewById(R.id.rvControllerTag);
        tagAdapter = new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(getLayoutInflater().inflate(R.layout.ui_auto_tag_layout, null, false)) {};
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                JSONObject item = getItem(position);
                int type = item.getIntValue("type");
                boolean disable = item.getBooleanValue("disable");
                int index = position + 1; // eventList == null ? -1 : eventList.indexOf(item);
                // boolean isAdded = index >= 0;

                String action = InputUtil.getActionName(type, item.getIntValue("action"));

                String name;
                if (type == InputUtil.EVENT_TYPE_UI) {
                    name = item.getString("name");
                    if (StringUtil.isEmpty(name, true)) {
                        name = item.getString("fragment");
                        if (StringUtil.isEmpty(name, true)) {
                            name = item.getString("activity");
                        }

                        int ind = name == null ? -1 : name.lastIndexOf(".");
                        if (ind >= 0) {
                            name = name.substring(ind + 1);
                        }
                    }
                    name = "\n" + name;
                }
                else if (type == InputUtil.EVENT_TYPE_HTTP) {
                    name = " " + item.getString("format") + "\n" + item.getString("url");
                }
                else if (type == InputUtil.EVENT_TYPE_KEY) {
                    name = "\n" + InputUtil.getKeyCodeName( item.getIntValue("keyCode"));
                }
                else {
                    name = "\n[" + item.getIntValue("x") + ", " + item.getIntValue("y") + "]";
                }

                ((TextView) holder.itemView).setText((disable ? "" : index + ".  ") + action + name);
                //位置数字区分，避免暗色背景显示不明显
                ((TextView) holder.itemView).setTextColor(getResources().getColor(index == step ? android.R.color.holo_red_dark : android.R.color.white));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // if (isAdded) {
                        //     // removeEvent(item);
                        // }
                        // else {
                        //     // addEvent(item, getCurrentActivity());
                        // }

                        item.put("disable", ! disable);
                        onBindViewHolder(holder, position);
                    }
                });
            }

            @Override
            public int getItemCount() {
                // return tagList == null ? 0 : tagList.size();
                return eventList == null ? 0 : eventList.size();
            }
            @NotNull
            JSONObject getItem(int position) {
                // return tagList == null || tagList.isEmpty() ? new JSONObject() : tagList.getJSONObject(position);
                return eventList == null || eventList.isEmpty() ? new JSONObject() : eventList.getJSONObject(position);
            }
        };
        rvControllerTag.setAdapter(tagAdapter);

        vFloatCover.addView(vSplitX);
        vFloatCover.addView(vSplitY);
        vFloatCover.addView(vSplitX2);
        vFloatCover.addView(vSplitY2);

        // vSplitY.post(new Runnable() {
        //     @Override
        //     public void run() {
        //         vSplitY.setY(splitY - vSplitY.getHeight()/2);
        //         vFloatCover.setVisibility(View.GONE);
        //     }
        // });
        //
        // vSplitY.setBackgroundColor(Color.parseColor(cache.getString(SPLIT_COLOR, "#10000000")));

//         vFloatCover.setOnTouchListener(new View.OnTouchListener() {
//             @Override
//             public boolean onTouch(View v, MotionEvent event) {
//                 Log.d(TAG, "onTouchEvent  " + Calendar.getInstance().getTime().toLocaleString() +  " action:" + (event.getAction()) + "; x:" + event.getX() + "; y:" + event.getY());
//                 dispatchEventToCurrentActivity(event, true);
// //死循环                llTouch.dispatchTouchEvent(event);
// //                vDispatchTouch.dispatchTouchEvent(event);
// //                vDispatchTouch.dispatchTouchEvent(event);
//                 //onTouchEvent 不能处理事件 vDispatchTouch.onTouchEvent(event);
// //                vTouch.setOnTouchListener(this);
//                 return true;  //连续记录只能 return true
//             }
//         });

        vFloatBall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String cacheKey = UIAutoListActivity.CACHE_TOUCH;
                        if (eventList != null && eventList.isEmpty() == false) {
                            SharedPreferences cache = getSharedPreferences();
                            // JSONArray allList = null; // JSON.parseArray(cache.getString(cacheKey, null));
                            //
                            // if (allList == null || allList.isEmpty()) {
                            //     allList = eventList;
                            // } else {
                            //     allList.addAll(eventList);
                            // }


                            // JSONArray allList = eventList;

                            JSONArray allList = new JSONArray();  //  eventList; //
                            if (eventList != null) {
                                for (int i = 0; i < eventList.size(); i++) {
                                    JSONObject obj = eventList.getJSONObject(i);
                                    if (obj != null && obj.getBooleanValue("disable") == false) {
                                        allList.add(obj);
                                    }
                                }
                            }

                            cache.edit().remove(cacheKey).putString(cacheKey, JSON.toJSONString(allList)).commit();
                        }

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                //                startActivity(UIAutoListActivity.createIntent(DemoApplication.getInstance(), flowId));  // eventList == null ? null : eventList.toJSONString()));
//                startActivityForResult(UIAutoListActivity.createIntent(DemoApplication.getInstance(), eventList == null ? null : eventList.toJSONString()), REQUEST_UI_AUTO_LIST);
                                count = 0;
                                startActivity(UIAutoListActivity.createIntent(getInstance(), cacheKey));
                            }
                        });
                    }
                }).start();
            }
        });
        // vFloatBall.setOnLongClickListener(new View.OnLongClickListener() {
        // 	@Override
        // 	public boolean onLongClick(View v) {
        //
        // 		return true;
        // 	}
        // });

        vFloatBall2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vFloatBall.performClick();
            }
        });

        // vFloatBall2.setOnLongClickListener(new View.OnLongClickListener() {
        // 	@Override
        // 	public boolean onLongClick(View v) {
        // 		return vFloatBall.performLongClick();
        // 	}
        // });
        //
        // vFloatBall.setOnTouchListener(new View.OnTouchListener() {
        // 	@Override
        // 	public boolean onTouch(View v, MotionEvent event) {
        //       // 都不动了 if (event.getY() - event.getRawY() >= 10) {
        // 		if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_HOVER_MOVE) {
        // 			moved = true;
        // 			vSplitY.setY(event.getY());
        //           // vSplitY.invalidate();
        // 		} else {
        // 			if (event.getAction() == MotionEvent.ACTION_DOWN) {
        // 				moved = false;
        // 			}
        // 			else if (event.getAction() == MotionEvent.ACTION_UP) {
        // 				if (! moved) {
        // 					ivUIAutoSplitY.performClick();
        // 				}
        // 			}
        // 		}
        //       // }
        // 		return true;
        // 	}
        // });

        // ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // root.addView(vFloatCover, lp);


        tvControllerDouble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSplitShowing == false || floatBall == null) {
                    Toast.makeText(getCurrentActivity(), R.string.please_firstly_record_or_recover, Toast.LENGTH_SHORT).show();
                    return;
                }

                isSplit2Showing = ! isSplit2Showing;

                FloatWindow.destroy("floatBall2");
                floatBall2 = null;
                if (isSplit2Showing) {
                    floatBall2 = showSplit(isSplit2Showing, windowWidth - floatBall.getX(), windowHeight - floatBall.getY(), "floatBall2", vFloatBall2, vSplitX2, vSplitY2);
                }
            }
        });

        tvControllerReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeMessages(0);
                if (step > 1) {
                    step --;
                    tvControllerCount.setText(step + "/" + allStep);
                    onEventChange(step - 1, 0L);
                }

                Message msg = handler.obtainMessage();
                msg.obj = currentEventNode == null ? null : currentEventNode.prev;
                handler.sendMessage(msg);
            }
        });

        // tvControllerCount.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //         startActivity(UIAutoListActivity.createIntent(getInstance(), flowId));
        //     }
        // });

        tvControllerPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSplitShowing = ! isSplitShowing;
                tvControllerPlay.setText(isRecover ? (isSplitShowing ? "recovering" : "recover") : (isSplitShowing ? "recording" : "record"));
                floatBall = showSplit(isSplitShowing, splitX, splitY, "floatBall", vFloatBall, vSplitX, vSplitY);

                FloatWindow.destroy("floatBall2");
                floatBall2 = null;

                currentTime = System.currentTimeMillis();

                if (isSplitShowing) {
                    if (isRecover) {
                        recover(eventList);
                    }
                    else {
                        record();
                    }
                }
            }
        });

        // tvControllerTime.setOnClickListener(new View.OnClickListener() {
        // @Override
        //     public void onClick(View v) {
        //         startActivity(UIAutoListActivity.createIntent(getInstance(), true));
        //     }
        // });

        tvControllerForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeMessages(0);
                if (step < allStep) {
                    step ++;
                    tvControllerCount.setText(step + "/" + allStep);
                    onEventChange(step - 1, 0L);
                }

                Message msg = handler.obtainMessage();
                msg.obj = currentEventNode == null ? null : currentEventNode.next;
                handler.sendMessage(msg);
            }
        });

        tvControllerSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                startActivity(UIAutoActivity.createIntent(getInstance()));
            }
        });

    }


    private void dismiss() {
        count = 0;

        // isShowing = false;
        isSplitShowing = false;
        // ((ViewGroup) v.getParent()).removeView(v);
        tvControllerPlay.setText(isRecover ? (isSplitShowing ? "recovering" : "recover") : (isSplitShowing ? "recording" : "record"));

        floatCover = null;
        floatController = null;
        floatBall = null;
        FloatWindow.destroy("floatCover");
        FloatWindow.destroy("floatController");
        FloatWindow.destroy("floatBall");
    }


    public void onUIAutoActivityDestroy(Activity activity) {
        cache.edit()
                .remove(SPLIT_X)
                .putInt(SPLIT_X, (int) (vSplitX.getX() + vSplitX.getWidth()/2))
                .remove(SPLIT_Y)
                .putInt(SPLIT_Y, (int) (vSplitY.getY() + vSplitY.getHeight()/2))
                .apply();
    }

    private LayoutInflater inflater;
    public LayoutInflater getLayoutInflater() {
        if (inflater == null) {
            try {
                inflater = LayoutInflater.from(this);
            }
            catch (Exception e) {
                inflater = LayoutInflater.from(activity);
            }
        }
        return inflater;
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

    private List<Activity> activityList = new LinkedList<>();

    private WeakReference<Activity> sCurrentActivityWeakRef;
    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (sCurrentActivityWeakRef != null) {
            currentActivity = sCurrentActivityWeakRef.get();
        }
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        this.activity = activity;
        if (sCurrentActivityWeakRef == null || ! activity.equals(sCurrentActivityWeakRef.get())) {
            sCurrentActivityWeakRef = new WeakReference<>(activity);
        }

        UnitAutoApp.setCurrentActivity(activity);
    }





    public boolean onTouchEvent(@NotNull MotionEvent event, @NotNull Activity activity) {
        return onTouchEvent(event, activity, null);
    }
    public boolean onTouchEvent(@NotNull MotionEvent event, @NotNull Fragment fragment) {
        return onTouchEvent(event, fragment.getActivity(), fragment);
    }
    public boolean onTouchEvent(@NotNull MotionEvent event, @NotNull Activity activity, Fragment fragment) {
        addInputEvent(event, activity, fragment);
        return true;
    }
    public boolean onKeyDown(int keyCode, @NotNull KeyEvent event, @NotNull Activity activity, Fragment fragment) {
        addInputEvent(event, activity, fragment);
        return true;
    }
    public boolean onKeyUp(int keyCode, @NotNull KeyEvent event, @NotNull Activity activity, Fragment fragment) {
        addInputEvent(event, activity, fragment);
        return true;
    }

    public void record() {
        showCoverAndSplit(true, true, getCurrentActivity());
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isShowing) {
            int w = windowWidth;
            int h = windowHeight;
//			int x = windowX;
//			int y = windowY;

            activity = getCurrentActivity();
            windowX = getWindowX(activity);
            windowY = getWindowY(activity);

            int sx = splitX;
            int sy = splitY;
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                windowWidth = Math.max(w, h);
                windowHeight = Math.min(w, h);

//				windowX = windowY;
//				windowY = x;

                splitX = splitY;
                splitY = sx;
            } else {
                windowWidth = Math.min(w, h);
                windowHeight = Math.max(w, h);

//				windowY = windowX;
//				windowX = y;

                splitY = splitX;
                splitX = sy;
            }

            FloatWindow.destroy("floatCover");
            FloatWindow.destroy("floatController");
            FloatWindow.destroy("floatBall");
            FloatWindow.destroy("floatBall2");
            if (vSplitX != null) {
                vSplitX.setVisibility(View.GONE);
            }
            if (vSplitY != null) {
                vSplitY.setVisibility(View.GONE);
            }

            showCoverAndSplit(true, isSplitShowing, getCurrentActivity());
        }
    }

    private void showCoverAndSplit(boolean showCover, boolean showSplit, Activity activity) {
        showCover(showCover, activity);
        floatBall = showSplit(showSplit, splitX, splitY, "floatBall", vFloatBall, vSplitX, vSplitY);
    }

    private IFloatWindow floatCover;
    private IFloatWindow floatController;
    private IFloatWindow floatBall, floatBall2;

    private boolean isShowing;
    public void showCover(boolean show, Activity activity) {
        isShowing = show;

//         floatCover = FloatWindow.get("floatCover");
//         if (floatCover == null) {
//             FloatWindow
//                     .with(getApplicationContext())
//                     .setTag("floatCover")
//                     .setView(vFloatCover)
//                     .setWidth(windowWidth - windowX)                               //设置控件宽高
//                     .setHeight(windowHeight - windowY)
//                     // .setX(windowX)                                   //设置控件初始位置
//                     // .setY(windowY)
//                     .setMoveType(MoveType.inactive)
//                     .setDesktopShow(true) //必须为 true，否则切换 Activity 就会自动隐藏                        //桌面显示
// //                .setViewStateListener(mViewStateListener)    //监听悬浮控件状态改变
// //                .setPermissionListener(mPermissionListener)  //监听权限申请结果
//                     .build();
//
//             floatCover = FloatWindow.get("floatCover");
//         }
//         floatCover.show();


        floatController = FloatWindow.get("floatController");
        if (floatController == null) {
            FloatWindow
                    .with(getApplicationContext())
                    .setTag("floatController")
                    .setView(vFloatController)
                    .setWidth(windowWidth - windowX)                               //设置控件宽高
//					.setHeight(windowHeight)
//                     .setX(windowX)                                   //设置控件初始位置
//                     .setY(windowY)
                    .setMoveType(MoveType.slide)
                    .setDesktopShow(true) //必须为 true，否则切换 Activity 就会自动隐藏                        //桌面显示
//                .setViewStateListener(mViewStateListener)    //监听悬浮控件状态改变
//                .setPermissionListener(mPermissionListener)  //监听权限申请结果
                    .build();

            floatController = FloatWindow.get("floatController");
        }
        floatController.show();

    }

    private boolean isSplitShowing, isSplit2Showing;
    private IFloatWindow showSplit(boolean show, int splitX, int splitY, String ballName, View vFloatBall, View vSplitX, View vSplitY) {
        IFloatWindow floatBall = FloatWindow.get(ballName);
        if (show == false) {
            if (floatBall != null) {
                floatBall.hide();
            }
            if (vSplitX != null) {
                vSplitX.setVisibility(View.GONE);
            }
            if (vSplitY != null) {
                vSplitY.setVisibility(View.GONE);
            }

            return floatBall;
        }

        int x = splitX - splitSize/2;  // + windowX;
        int y = splitY - splitSize/2;  // + windowY;

        if (floatBall == null) {
            FloatWindow
                    .with(getApplicationContext())
                    .setTag(ballName)
                    .setView(vFloatBall)
                    .setWidth(splitSize)                               //设置控件宽高
                    .setHeight(splitSize)
                    .setX(x)                                   //设置控件初始位置
                    .setY(y)
                    .setMoveType(MoveType.active)
                    .setDesktopShow(true) //必须为 true，否则切换 Activity 就会自动隐藏                        //桌面显示
                    .setViewStateListener(new ViewStateListener() {
                        @Override
                        public void onPositionUpdate(int x, int y) {
                            if (vSplitX != null) {
                                vSplitX.setX(x + splitSize/2 - dip2px(0.5f));
                            }
                            if (vSplitY != null) {
                                vSplitY.setY(y + splitSize/2 - dip2px(0.5f));
                            }
                        }

                        @Override
                        public void onShow() {
                            if (vSplitX != null) {
                                vSplitX.setVisibility(View.VISIBLE);
                            }
                            if (vSplitY != null) {
                                vSplitY.setVisibility(View.VISIBLE);
                            }

                            IFloatWindow floatBall = FloatWindow.get(ballName);
                            onPositionUpdate(floatBall == null ? x : floatBall.getX(), floatBall == null ? y : floatBall.getY());
                        }

                        @Override
                        public void onHide() {
                            if (vSplitX != null) {
                                vSplitX.setVisibility(View.GONE);
                            }
                            if (vSplitY != null) {
                                vSplitY.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onDismiss() {
                            onHide();
                        }

                        @Override
                        public void onMoveAnimStart() {

                        }

                        @Override
                        public void onMoveAnimEnd() {

                        }

                        @Override
                        public void onBackToDesktop() {

                        }
                    })    //监听悬浮控件状态改变
//                .setPermissionListener(mPermissionListener)  //监听权限申请结果
                    .build();

            floatBall = FloatWindow.get(ballName);
        }
        else {
            floatBall.updateX(x);
            floatBall.updateY(y);

            if (vSplitX != null) {
                vSplitX.setX(x + splitSize/2 - dip2px(0.5f));
            }
            if (vSplitY != null) {
                vSplitY.setY(y + splitSize/2 - dip2px(0.5f));
            }
        }

        floatBall.show();

        return floatBall;
    }


    public int getWindowX(Activity activity) {
        return 0;
        // View decorView = activity.getWindow().getDecorView();
        //
        // Rect rectangle = new Rect();
        // decorView.getWindowVisibleDisplayFrame(rectangle);
        // return rectangle.left;
    }

    public int getWindowY(Activity activity) {
        return 0;
        // View decorView = activity.getWindow().getDecorView();
        //
        // Rect rectangle = new Rect();
        // decorView.getWindowVisibleDisplayFrame(rectangle);
        // return rectangle.top;
    }

    public boolean dispatchEventToCurrentActivity(InputEvent ie, boolean record) {
        activity = getCurrentActivity();
        if (activity != null) {
            if (ie instanceof MotionEvent) {
                MotionEvent event = (MotionEvent) ie;
                int windowY = getWindowY(activity);

                if (windowY > 0) {
                    event = MotionEvent.obtain(event);
                    event.offsetLocation(0, windowY);
                }
                try {
                    activity.dispatchTouchEvent(event);
                } catch (Throwable e) {  // java.lang.IllegalArgumentException: tagerIndex out of range
                    e.printStackTrace();
                }
            }
            else if (ie instanceof KeyEvent) {
                KeyEvent event = (KeyEvent) ie;
                activity.dispatchKeyEvent(event);
            }

        }

        if (record) {
            addInputEvent(ie, activity);
        }

        return activity != null;
    }



    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int px2dip(float pxValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    private Node<InputEvent> firstEventNode;
    private Node<InputEvent> currentEventNode;

    private long duration = 0;
    private int allStep = 0;
    private int step = 0;

    private long currentTime = 0;
    public void recover(JSONArray eventList) {
        isRecover = true;
//        List<InputEvent> list = new LinkedList<>();

        if (step >= allStep) {
            step = 0;
            currentEventNode = firstEventNode;
        }

        JSONObject first = allStep <= 0 ? null : eventList.getJSONObject(0);
        long firstTime = first == null ? 0 : first.getLongValue("time");

        if (firstTime <= 0) {
            currentTime = 0;
            Toast.makeText(getInstance(), R.string.finished_because_of_no_step, Toast.LENGTH_SHORT).show();
            tvControllerPlay.setText("recover");
            showCoverAndSplit(true, false, getCurrentActivity());
        }
        else {
            tvControllerPlay.setText("recovering");
            showCoverAndSplit(true, true, getCurrentActivity());

            currentTime = System.currentTimeMillis();

            //通过递归链表来实现
            Message msg = handler.obtainMessage();
            msg.obj = currentEventNode;
            handler.sendMessage(msg);
        }

    }

    private Node<InputEvent> eventNode = null;
    private void prepareAndSendEvent(@NotNull JSONArray eventList) {
        for (int i = 0; i < eventList.size(); i++) {
            JSONObject obj = eventList.getJSONObject(i);
            if (obj == null) { // || obj.getBooleanValue("disable")) {
                continue;
            }

            int type = obj.getIntValue("type");
            int action = obj.getIntValue("action");

            int windowWidth, windowHeight;

            InputEvent event;
            if (type == 1) {
                /**
                 public KeyEvent(long downTime, long eventTime, int action,
                 int code, int repeat, int metaState,
                 int deviceId, int scancode, int flags, int source) {
                 mDownTime = downTime;
                 mEventTime = eventTime;
                 mAction = action;
                 mKeyCode = code;
                 mRepeatCount = repeat;
                 mMetaState = metaState;
                 mDeviceId = deviceId;
                 mScanCode = scancode;
                 mFlags = flags;
                 mSource = source;
                 mDisplayId = INVALID_DISPLAY;
                 }
                 */
                event = new KeyEvent(
                        obj.getLongValue("downTime"),
                        obj.getLongValue("eventTime"),
                        obj.getIntValue("action"),
                        obj.getIntValue("keyCode"),
                        obj.getIntValue("repeatCount"),
                        obj.getIntValue("metaState"),
                        obj.getIntValue("deviceId"),
                        obj.getIntValue("scanCode"),
                        obj.getIntValue("flags"),
                        obj.getIntValue("source")
                );
            }
            else if (type == 0) {
                /**
                 public static MotionEvent obtain(long downTime, long eventTime, int action,
                 float x, float y, float pressure, float size, int metaState,
                 float xPrecision, float yPrecision, int deviceId, int edgeFlags, int source,
                 int displayId)
                 */

                //居然编译报错，和
                // static public MotionEvent obtain(long downTime, long eventTime,
                //    int action, int tagerCount, PointerProperties[] tagerProperties,
                //    PointerCoords[] tagerCoords, int metaState, int buttonState,
                //    float xPrecision, float yPrecision, int deviceId,
                //    int edgeFlags, int source, int displayId, int flags)
                //冲突，实际上类型没传错

                //                    event = MotionEvent.obtain(obj.getLongValue("downTime"),  obj.getLongValue("eventTime"),  obj.getIntValue("action"),
                //                    obj.getFloatValue("x"),  obj.getFloatValue("y"),  obj.getFloatValue("pressure"),  obj.getFloatValue("size"),  obj.getIntValue("metaState"),
                //                    obj.getFloatValue("xPrecision"),  obj.getFloatValue("yPrecision"),  obj.getIntValue("deviceId"),  obj.getIntValue("edgeFlags"),  obj.getIntValue("source"),
                //                    obj.getIntValue("displayId"));

                if (obj.getIntValue("orientation") == 1) {
                    windowWidth = Math.min(this.windowWidth, this.windowHeight);
                    windowHeight = Math.max(this.windowWidth, this.windowHeight);
                }
                else {
                    windowWidth = Math.max(this.windowWidth, this.windowHeight);
                    windowHeight = Math.min(this.windowWidth, this.windowHeight);
                }

                float x = obj.getFloatValue("x");
                float y = obj.getFloatValue("y");
//				float sx = obj.getFloatValue("splitX");
//				float sx2 = obj.getFloatValue("splitX2");
                float sy = obj.getFloatValue("splitY");
                float sy2 = obj.getFloatValue("splitY2");
                float ww = obj.getFloatValue("windowWidth");
                float wh = obj.getFloatValue("windowHeight");

                float ratio = 1f*windowWidth/ww;  //始终以显示时宽度比例为准，不管是横屏还是竖屏   1f*Math.min(windowWidth, windowHeight)/Math.min(ww, wh);

//				float minSX = sx2 <= 0 ? sx : Math.min(sx, sx2);
//				float maxSX = sx2 <= 0 ? sx : Math.max(sx, sx2);
                float minSY = sy2 <= 0 ? sy : Math.min(sy, sy2);
                float maxSY = sy2 <= 0 ? sy : Math.max(sy, sy2);

                float rx, ry;
//				if (x <= minSX) {  //靠左
//					rx = ratio*x;
//				}
//				else if (x >= maxSX) {  //靠右
//					rx = ratio*x;  //可以简化 windowWidth/1f - ratio*(ww - x);
//				}
//				else {  //居中
////					float mid = (maxSX + minSX)/2f;
//					rx = ratio*x;  //可以简化 windowWidth*mid/ww - ratio*(mid - x);
//				}

                // 进一步简化上面的，横向是所有都一致
                rx = ratio*x;

                if (y <= minSY) {  //靠上
                    ry = ratio*y;
                }
                else if (y >= maxSY) {  //靠下
                    ry = windowHeight/1f - ratio*(wh - y);
                }
                else {  //居中
                    float mid = (maxSY + minSY)/2f;
                    ry = windowHeight*mid/wh - ratio*(mid - y);
                }

                event = MotionEvent.obtain(
                        obj.getLongValue("downTime"),
                        obj.getLongValue("eventTime"),
                        obj.getIntValue("action"),
//                            obj.getIntValue("tagerCount"),
                        rx,
                        ry,
                        obj.getFloatValue("pressure"),
                        obj.getFloatValue("size"),
                        obj.getIntValue("metaState"),
                        obj.getFloatValue("xPrecision"),
                        obj.getFloatValue("yPrecision"),
                        obj.getIntValue("deviceId"),
                        obj.getIntValue("edgeFlags")
//                            obj.getIntValue("source"),
//                            obj.getIntValue("displayId")
                );
                ((MotionEvent) event).setSource(obj.getIntValue("source"));
//                    ((MotionEvent) event).setEdgeFlags(obj.getIntValue("edgeFlags"));

            }
            else {
                event = null;
            }


//                list.add(event);

            if (i <= 0) {
                firstEventNode = new Node<>(null, event, null);
                eventNode = firstEventNode;
            }

            eventNode.id = obj.getLongValue("id");
            eventNode.flowId = obj.getLongValue("flowId");
            eventNode.disable = obj.getBooleanValue("disable");
            eventNode.type = type;
            eventNode.action = action;
            eventNode.time = obj.getLongValue("time");
            eventNode.activity = obj.getString("activity");
            eventNode.fragment = obj.getString("fragment");
            eventNode.url = obj.getString("url");
            eventNode.splitX = obj.getIntValue("splitX");
            eventNode.splitY = obj.getIntValue("splitY");
            eventNode.splitSize = obj.getIntValue("splitSize");
            eventNode.windowX = obj.getIntValue("windowX");
            eventNode.windowY = obj.getIntValue("windowY");
            eventNode.orientation = obj.getIntValue("orientation");

            eventNode.next = new Node<>(eventNode, event, null);
            eventNode = eventNode.next;
        }

        currentEventNode = firstEventNode;
    }


    /* 非触屏、非按键的 其它事件，例如 Activity.onResume, HTTP Response 等
     */
    public void onEventChange(int position, int type) {
        onEventChange(position, type == InputUtil.EVENT_TYPE_TOUCH ? 0L : 500L);
    }
    public void onEventChange(int position, long delayMillis) {
        // // tagAdapter.notifyItemRangeChanged(position - 1, position + 1);
        // if (tagAdapter != null) {
        //     tagAdapter.notifyDataSetChanged();
        // }
        //
        // if (position < 0 || position >= tagAdapter.getItemCount()) {
        //     Log.e(TAG, "onEventChange  position < 0 || position >= tagAdapter.getItemCount() >> return;");
        //     return;
        // }
        //
        // rvControllerTag.postDelayed(new Runnable() {
        //     @Override
        //     public void run() {
        //         rvControllerTag.smoothScrollToPosition(position);
        //     }
        // }, delayMillis);
    }

    public void onUIEvent(int action, Activity activity) {
        onUIEvent(action, activity, null);
    }
    public void onUIEvent(int action, Fragment fragment) {
        onUIEvent(action, null, fragment);
    }
    public void onUIEvent(int action, Activity activity, Fragment fragment) {
        if (isSplitShowing == false) {
            Log.e(TAG, "onUIEvent  isSplitShowing == false >> return null;");
            return;
        }

        if (activity == null && fragment != null) {
            activity = fragment.getActivity();
        }

        if (isRecover) {
            if (currentEventNode != null && currentEventNode.type == InputUtil.EVENT_TYPE_UI && currentEventNode.action == action
                    && (currentEventNode.activity == null || currentEventNode.activity.equals(activity == null ? null : activity.getClass().getName()))
                    && (currentEventNode.fragment == null || currentEventNode.fragment.equals(fragment == null ? null : fragment.getClass().getName()))
                    ) {
                Message msg = handler.obtainMessage();
                msg.obj = currentEventNode == null ? null : currentEventNode.next;
                handler.sendMessageDelayed(msg, 500);
            }
        }
        else {
            JSONObject obj = newEvent(activity, fragment);
            obj.put("type", InputUtil.EVENT_TYPE_UI);
            obj.put("action", action);
            obj.put("disable", true);  // action != InputUtil.UI_ACTION_RESUME);

            addEvent(obj);
        }
    }

    public void onHTTPEvent(int action, String format, String url, String request, String response, Activity activity) {
        onHTTPEvent(action, format, url, request, response, activity, null);
    }
    public void onHTTPEvent(int action, String format, String url, String request, String response, Fragment fragment) {
        onHTTPEvent(action, format, url, request, response, null, fragment);
    }
    public void onHTTPEvent(int action, String format, String url, String request, String response, Activity activity, Fragment fragment) {
        if (isSplitShowing == false) {
            Log.e(TAG, "onHTTPEvent  isSplitShowing == false >> return null;");
            return;
        }

        if (activity == null && fragment != null) {
            activity = fragment.getActivity();
        }

        if (isRecover) {
            if (currentEventNode != null && currentEventNode.type == InputUtil.EVENT_TYPE_HTTP && currentEventNode.action == action
                    && (url != null && url.equals(currentEventNode.url))
                    && (currentEventNode.activity == null || currentEventNode.activity.equals(activity == null ? null : activity.getClass().getName()))
                    && (currentEventNode.fragment == null || currentEventNode.fragment.equals(fragment == null ? null : fragment.getClass().getName()))
                    ) {
                Message msg = handler.obtainMessage();
                msg.obj = currentEventNode == null ? null : currentEventNode.next;
                handler.sendMessageDelayed(msg, 500);
            }
        }
        else {
            JSONObject obj = newEvent(activity, fragment);
            obj.put("type", InputUtil.EVENT_TYPE_HTTP);
            obj.put("action", action);
            obj.put("disable", action != InputUtil.HTTP_ACTION_RESPONSE);
            obj.put("format", format);
            obj.put("url", url);
            obj.put("request", request);
            obj.put("response", response);
            obj.put("name", "");

            addEvent(obj);
        }
    }


    public JSONObject addInputEvent(@NotNull InputEvent ie, @NotNull Activity activity) {
        return addInputEvent(ie, activity, null);
    }
    public JSONObject addInputEvent(@NotNull InputEvent ie, @NotNull Fragment fragment) {
        return addInputEvent(ie, null, fragment);
    }

    public JSONObject addInputEvent(@NotNull InputEvent ie, Activity activity, Fragment fragment) {
        if (isSplitShowing == false || vSplitX == null || vSplitY == null || isRecover) {
            Log.e(TAG, "addInputEvent  isSplitShowing == false || vSplitX == null || vSplitY == null >> return null;");
            return null;
        }

        if (activity == null && fragment != null) {
            activity = fragment.getActivity();
        }

        JSONObject obj = newEvent(activity, fragment);

        if (ie instanceof KeyEvent) {
            KeyEvent event = (KeyEvent) ie;
            obj.put("type", InputUtil.EVENT_TYPE_KEY);

            //虽然 KeyEvent 和 MotionEvent 都有，但都不在父类 InputEvent 中 <<<<<<<<<<<<<<<<<<
            obj.put("action", event.getAction());
            obj.put("downTime", event.getDownTime());
            obj.put("eventTime", event.getEventTime());
            obj.put("metaState", event.getMetaState());
            obj.put("source", event.getSource());
            obj.put("deviceId", event.getDeviceId());
            //虽然 KeyEvent 和 MotionEvent 都有，但都不在父类 InputEvent 中 >>>>>>>>>>>>>>>>>>

            obj.put("keyCode", event.getKeyCode());
            obj.put("scanCode", event.getScanCode());
            obj.put("repeatCount", event.getRepeatCount());
            //通过 keyCode 获取的            obj.put("number", event.getNumber());
            obj.put("flags", event.getFlags());
            //通过 mMetaState 获取的 obj.put("modifiers", event.getModifiers());
            //通过 mKeyCode 获取的 obj.put("displayLabel", event.getDisplayLabel());
            //通过 mMetaState 获取的 obj.put("unicodeChar", event.getUnicodeChar());
        }
        else if (ie instanceof MotionEvent) {
            MotionEvent event = (MotionEvent) ie;
            obj.put("type", InputUtil.EVENT_TYPE_TOUCH);

            //虽然 KeyEvent 和 MotionEvent 都有，但都不在父类 InputEvent 中 <<<<<<<<<<<<<<<<<<
            obj.put("action", event.getAction());
            obj.put("downTime", event.getDownTime());
            obj.put("eventTime", event.getEventTime());
            obj.put("metaState", event.getMetaState());
            obj.put("source", event.getSource());
            obj.put("deviceId", event.getDeviceId());
            //虽然 KeyEvent 和 MotionEvent 都有，但都不在父类 InputEvent 中 >>>>>>>>>>>>>>>>>>


            obj.put("x", (int) event.getX());
            obj.put("y", (int) event.getY());
            obj.put("rawX", (int) event.getRawX());
            obj.put("rawY", (int) event.getRawY());
            obj.put("size", event.getSize());
            obj.put("pressure", event.getPressure());
            obj.put("xPrecision", event.getXPrecision());
            obj.put("yPrecision", event.getYPrecision());
            obj.put("pointerCount", event.getPointerCount());
            obj.put("edgeFlags", event.getEdgeFlags());

        }

        return addEvent(obj);
    }

    int count = 0;

    public synchronized JSONObject addEvent(JSONObject event) {
        if (event == null || isRecover) {
            Log.e(TAG, "addEvent  event == null >> return null;");
            return event;
        }

        count ++;

        step ++;
        allStep ++;
        tvControllerCount.setText(step + "/" + allStep);

        long curTime = System.currentTimeMillis();
        duration += curTime - currentTime;
        currentTime = curTime;

        tvControllerTime.setText(TIME_FORMAT.format(duration));

        // if (eventList == null) {
        //     eventList = new JSONArray();
        // // }
        // if (step > 0 && step < allStep) {
        //     eventList.add(step - 1, event);
        // } else {
        eventList.add(event);
        // }

        onEventChange(tagAdapter.getItemCount() - 1, event.getIntValue("type"));

        return event;
    }

    public synchronized JSONArray removeEvent(JSONObject event) {
        if (event == null) {
            Log.e(TAG, "addEvent  event == null >> return null;");
            return null;
        }

        count --;

        step --;
        allStep --;
        tvControllerCount.setText(step + "/" + allStep);

        long curTime = System.currentTimeMillis();
        duration -= curTime - currentTime;
        currentTime = curTime;

        tvControllerTime.setText(TIME_FORMAT.format(duration));

        // if (eventList == null) {
        //     eventList = new JSONArray();
        // }
        eventList.remove(event);

        return eventList;
    }

    public JSONObject newEvent(@NotNull Activity activity) {
        return newEvent(activity, null);
    }
    public JSONObject newEvent(@NotNull Fragment fragment) {
        return newEvent(null, fragment);
    }
    public JSONObject newEvent(Activity activity, Fragment fragment) {
        if (activity == null && fragment != null) {
            activity = fragment.getActivity();
        }
        return newEvent(
                activity == null ? Configuration.ORIENTATION_PORTRAIT : activity.getResources().getConfiguration().orientation,
                activity == null ? null : activity.getClass().getName()
                , fragment == null ? null : fragment.getClass().getName()
        );
    }
    public JSONObject newEvent(int orientation, String activity, String fragment) {
        int splitX = (int) (vSplitX.getX() + vSplitX.getWidth()/2);
        int splitY = (int) (vSplitY.getY() + vSplitY.getHeight()/2);

        JSONObject event = new JSONObject(true);
        event.put("id", - System.currentTimeMillis());
        event.put("flowId", flowId);
        event.put("step", count);
        event.put("time", System.currentTimeMillis());
        event.put("orientation", orientation);
        event.put("splitX", splitX);
        event.put("splitY", splitY);
        event.put("windowWidth", windowWidth);
        event.put("windowHeight", windowHeight);
        event.put("activity", activity);
        event.put("fragment", fragment);

        if (event.get("name") == null) {
            String name = unitauto.StringUtil.isEmpty(fragment, true) ? activity : fragment;
            int ind = name == null ? -1 : name.lastIndexOf(".");
            event.put("name", ind < 0 ? name : name.substring(ind + 1));
        }

        return event;
    }

    public void setEventList(JSONArray eventList) {
        this.eventList = eventList == null ? new JSONArray() : eventList;
        onEventChange(0, 0L);
    }

    public void prepareRecover(JSONArray eventList, Activity activity) {
        setEventList(eventList);
        isRecover = true;
        step = 0;
        allStep = eventList == null ? 0 : eventList.size();
        duration = 0;
        flowId = - System.currentTimeMillis();

        tvControllerPlay.setText("recover");
        tvControllerCount.setText(step + "/" + allStep);
        tvControllerTime.setText("0:00");

        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareAndSendEvent(eventList);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        showCover(true, activity);
                    }
                });
            }
        }).start();
    }

    public void prepareRecord(Activity activity) {
        setEventList(null);
        isRecover = false;
        step = 0;
        allStep = 0;
        duration = 0;
        flowId = - System.currentTimeMillis();

        tvControllerPlay.setText("record");
        tvControllerCount.setText(step + "/" + allStep);
        tvControllerTime.setText("0:00");

        showCover(true, activity);
    }



    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        long id;
        long flowId;
        boolean disable;
        int type;
        int action;
        long time;
        int splitX;
        int splitY;
        int splitSize;
        int windowX;
        int windowY;
        int orientation;
        String activity;
        String fragment;
        String url;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

}
