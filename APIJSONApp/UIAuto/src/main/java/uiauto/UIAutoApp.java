/*Copyright ©2020 TommyLemon(https://github.com/TommyLemon/UIAuto)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package uiauto;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.SupportActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.Display;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.koushikdutta.async.http.Multimap;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.IFloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.ViewStateListener;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import unitauto.NotNull;
import unitauto.apk.UnitAutoApp;


/**Application
 * @author Lemon
 */
public class UIAutoApp extends Application {
  public static final String TAG = "UIAutoApp";

  private static final String SPLIT_X = "SPLIT_X";
  private static final String SPLIT_Y = "SPLIT_Y";
  private static final String SPLIT_X2 = "SPLIT_X2";
  private static final String SPLIT_Y2 = "SPLIT_Y2";
  private static final String SPLIT_HEIGHT = "SPLIT_HEIGHT";
  private static final String SPLIT_COLOR = "SPLIT_COLOR";

  private static float DENSITY = Resources.getSystem().getDisplayMetrics().density;


  private static UIAutoApp instance;
  public static UIAutoApp getInstance() {
    return instance;
  }

  private static Application APP;
  public static Application getApp() {
    return APP;
  }


  private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("mm:ss");

  private final Handler mainHandler = new Handler(Looper.getMainLooper());

  private Map<String, List<Node<InputEvent>>> waitMap = new LinkedHashMap<>();
  private Node<InputEvent> lastWaitNode = null;

  private boolean isReplay = false;
  @SuppressLint("HandlerLeak")
  private final Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);

      if (isReplay && isSplitShowing) {
        //通过遍历数组来实现
        // if (currentTime >= System.currentTimeMillis()) {
        //     isReplaying = false;
        //     pbUIAutoSplitY.setVisibility(View.GONE);
        // }
        //
        // MotionEvent event = (MotionEvent) msg.obj;
        // dispatchEventToCurrentActivity(event);

        //根据递归链表来实现，能精准地实现两个事件之间的间隔，不受处理时间不一致，甚至卡顿等影响。还能及时终止

        Node<InputEvent> curNode = (Node<InputEvent>) msg.obj;

        while (curNode != null && curNode.disable) { // (curNode.disable || curNode.item == null)) {
          currentEventNode = curNode = curNode.next;
          step ++;

          // if (curNode != null && curNode.item != null) {
          //   output(null, curNode, activity);
          // }
        }

        currentEventNode = curNode;
        step = curNode == null ? step + 1 : curNode.step;  // step ++;

        // output(null, curNode, activity);

        boolean canRefreshUI = true; //FIXME 还原  curNode == null || curNode.type != InputUtil.EVENT_TYPE_TOUCH || curNode.action != MotionEvent.ACTION_MOVE;

        if (canRefreshUI) {
          tvControllerCount.setText(step + "/" + allStep);
          onEventChange(step - 1, curNode == null ? 0 : curNode.type);  // move 时刷新容易卡顿
        }

        if (step > allStep || curNode == null) {
          tvControllerCount.setText(step + "/" + allStep);
          tvControllerPlay.setText(R.string.replay);
          showCoverAndSplit(true, false);
          isSplitShowing = false;
          isSplit2Showing = false;
          waitMap = new LinkedHashMap<>();
          return;
        }


        InputEvent curItem = curNode.item;
        //暂停，等待时机
        if (curItem == null || waitMap.isEmpty() == false) { // curNode.type == InputUtil.EVENT_TYPE_UI || curNode.type == InputUtil.EVENT_TYPE_HTTP) {
          return;
        }

        Node<InputEvent> prevNode = curNode.prev;
        InputEvent prevItem = prevNode == null ? null : prevNode.item;

        if (prevNode != null) {
          duration += (prevItem == null || curItem == null
            ? (curNode.time - prevNode.time)
            : (curItem.getEventTime() - prevItem.getEventTime())
          );

          if (canRefreshUI) {
            tvControllerTime.setText(TIME_FORMAT.format(duration));
          }
        }

        if (canRefreshUI && curNode.type == InputUtil.EVENT_TYPE_TOUCH && curNode.action == MotionEvent.ACTION_DOWN) {
          splitX = curNode.splitX;
          splitY = curNode.splitY;
          splitX2 = curNode.splitX2;
          splitY2 = curNode.splitY2;
//          if (isSplitShowing) { // && floatBall != null) {
            //居然怎么都不更新 vSplitX 和 vSplitY
            // floatBall.hide();
            // floatBall.updateX(windowX + splitX - splitRadius);
            // floatBall.updateY(screenY + splitY - splitRadius);
            // floatBall.show();

            //太卡  FIXME 改了之后还是这样吗？
//            if (floatBall.getX() != (curNode.splitX - splitRadius + windowWidth)
//              || floatBall.getY() != (curNode.splitY - splitRadius + windowHeight)) {
              // FloatWindow.destroy("floatBall");
              // floatBall = null;
              floatBall = showSplit(true, splitX, splitY, "floatBall", vFloatBall, floatSplitX, floatSplitY);
//            }

//            if (isSplit2Showing) {
              floatBall2 = showSplit(splitX2 > 0 && splitY2 > 0, splitX2, splitY2, "floatBall2", vFloatBall2, floatSplitX2, floatSplitY2);
//            }
//          }
        }

        // 分拆为下面两条，都放在 UI 操作后，减少延迟
        // dispatchEventToCurrentActivity(curItem, false);


        Node<InputEvent> nextNode = curNode.next;
//        long firstTime = nextNode == null ? 0 : nextNode.time;
        while (nextNode != null && nextNode.disable) {
          // if (nextNode.item != null) {
          //   output(null, nextNode, activity);
          // }

          nextNode = nextNode.next;
          step ++;
        }
        // long lastTime = nextNode == null ? 0 : nextNode.time;

        waitMap = new LinkedHashMap<>();
        int lastStep = step;
        int lastWaitStep = 0;
        lastWaitNode = null;
        Node<InputEvent> lastNextNode = nextNode;

        Activity activity = getCurrentActivity();
        while (lastNextNode != null && (lastNextNode.disable || lastNextNode.item == null)
//                && (activity == null || Objects.equals(lastNextNode.activity, activity.getClass().getName()))
        ) {
          String url = lastNextNode.url;
          if (lastNextNode.item == null && lastNextNode.disable == false
//                  && Objects.equals(lastNextNode.fragment, fragment == null ? null : fragment.getClass().getName())
                  && StringUtil.isNotEmpty(url, true)
          ) {

            String key = getWaitKey(lastNextNode);
            List<Node<InputEvent>> list = waitMap.get(key);
            if (list == null) {
              list = new ArrayList<>();
            }
            list.add(lastNextNode);
            waitMap.put(key, list);

            lastWaitNode = lastNextNode;
            lastWaitStep = lastStep;
          }

          lastNextNode = lastNextNode.next;
          lastStep ++;
        }

        if (lastWaitNode != null) {
          nextNode = lastWaitNode;
          step = lastWaitStep;
        }

        msg = new Message();
        msg.obj = nextNode;

        InputEvent nextItem = nextNode == null ? null : nextNode.item;
        //暂停，等待时机
        if (nextNode != null && nextItem == null) { // (nextNode.type == InputUtil.EVENT_TYPE_UI || nextNode.type == InputUtil.EVENT_TYPE_HTTP)) {
          // step --;

//          if (lastWaitStep > 0) {
//            step = lastWaitStep - 1;
//          }
// 导致重复添加到 waitMap          handleMessage(msg);

          dispatchEventToCurrentWindow(curNode.item, false);
          handleMessage(msg);
        }
        else {
          output(null, curNode, activity);
          dispatchEventToCurrentWindow(curNode.item, false);

          long duration = nextNode == null ? 0 : nextItem.getEventTime() - curItem.getEventTime();
          if (duration <= 0) {
            handleMessage(msg);
          }
          else {
            sendMessageDelayed(msg, duration); // 相邻执行事件时间差本身就包含了  + (lastTime <= 0 || firstTime <= 0 ? 10 : lastTime - firstTime)  // 补偿 disable 项跳过的等待时间
          }
        }

      }
    }
  };

  private String getWaitKey(Node<InputEvent> node) {
    return getWaitKey(node.type, node.action, node.method, node.host, node.url);
  }
  private String getWaitKey(int type, int action, String method, String host, String url) {
    return type + ":" + action + ": " + method + " " + url;
  }

  public void post(@NonNull Runnable r) {
    handler.post(r);
  }
  public void postDelayed(@NonNull Runnable r, long delayMillis) {
    handler.postDelayed(r, delayMillis);
  }



  private Activity activity;
  private Fragment fragment;
  int screenWidth;
  int screenHeight;

  Window.Callback callback;
  Window window;
  View decorView;

  float windowWidth;
  float windowHeight;
  float windowX;
  float windowY;

  int statusResourceId;
  int statusHeight;
  float decorX;
  float decorY;
  float decorWidth;
  float decorHeight;

  ViewGroup vFloatCover;
  View vFloatController;
  FloatBallView vFloatBall, vFloatBall2;
  ViewGroup vSplitX, vSplitX2;
  ViewGroup vSplitY, vSplitY2;

  TextView tvControllerX;
  TextView tvControllerDouble;
  TextView tvControllerReturn;
  TextView tvControllerCount;
  TextView tvControllerPlay;
  TextView tvControllerTime;
  TextView tvControllerForward;
  TextView tvControllerSetting;
  TextView tvControllerY;

  RecyclerView rvControllerTag;

  // 都取负数，表示相对于最右侧和最下方还差多少
  private float splitX, splitX2;
  private float splitY, splitY2;
  private float splitSize;
  private float splitRadius;

  @NotNull
  private JSONArray eventList = new JSONArray();

  private RecyclerView.Adapter tagAdapter;

  SharedPreferences cache;
  private long flowId = 0;

  File parentDirectory;
  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;
    initUIAuto(this);
  }


  public SharedPreferences getSharedPreferences() {
    return getSharedPreferences(TAG, Context.MODE_PRIVATE);
  }



  public void onUIAutoActivityCreate() {
    onUIAutoActivityCreate(getCurrentActivity());
  }

  private Map<FragmentManager, Boolean> fragmentWatchedMap = new HashMap<>();
  public void onUIAutoActivityCreate(@NonNull Activity activity) {
    onUIAutoWindowCreate(activity, activity.getWindow());
  }

  public void onUIAutoWindowCreate(@NonNull Window.Callback callback, @NonNull Window window) {
//    if (callback instanceof Dialog) {
////      onUIAutoActivityDestroy(activity, activity);
//    }
//    if (this.window != null) {
//      this.window.setCallback(this.callback);
//    }
//    onUIAutoWindowDestroy(this.callback, this.window);

    //反而让 vFloatCover 与底部差一个导航栏高度 window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    decorView = window.getDecorView(); // activity.findViewById(android.R.id.content);  // decorView = window.getContentView();
    decorView.post(new Runnable() {
      @Override
      public void run() {
        decorWidth = decorView.getWidth();
        decorHeight = decorView.getHeight();
      }
    });
    decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        decorWidth = decorView.getWidth();
        decorHeight = decorView.getHeight();
      }
    });

    addTextChangedListener(decorView);

    Window.Callback windowCallback = window.getCallback();

    this.window = window;
    this.callback = windowCallback;
    window.setCallback(new Window.Callback() {
      @Override
      public boolean dispatchKeyEvent(KeyEvent event) {
//				dispatchEventToCurrentActivity(event);
        addInputEvent(event, callback, activity, fragment);
        return windowCallback != null && windowCallback.dispatchKeyEvent(event);
      }

      @Override
      public boolean dispatchKeyShortcutEvent(KeyEvent event) {
//				dispatchEventToCurrentActivity(event);
        addInputEvent(event, callback, activity, fragment);
        return windowCallback != null && windowCallback.dispatchKeyShortcutEvent(event);
      }

      @Override
      public boolean dispatchTouchEvent(MotionEvent event) {
//				dispatchEventToCurrentActivity(event);
        addInputEvent(event, callback, activity, fragment);
        return windowCallback != null && windowCallback.dispatchTouchEvent(event);
      }

      @Override
      public boolean dispatchTrackballEvent(MotionEvent event) {
//				dispatchEventToCurrentActivity(event);
        addInputEvent(event, callback, activity, fragment);
        return windowCallback != null && windowCallback.dispatchTrackballEvent(event);
      }

      @Override
      public boolean dispatchGenericMotionEvent(MotionEvent event) {
//				dispatchEventToCurrentActivity(event);
// 和 dispatchTouchEvent 重复                addInputEvent(event, activity);
        return windowCallback != null && windowCallback.dispatchGenericMotionEvent(event);
      }

      @Override
      public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return windowCallback != null && windowCallback.dispatchPopulateAccessibilityEvent(event);
      }

      @Nullable
      @Override
      public View onCreatePanelView(int featureId) {
        return windowCallback == null ? null : windowCallback.onCreatePanelView(featureId);
      }

      @Override
      public boolean onCreatePanelMenu(int featureId, Menu menu) {
        return windowCallback != null && windowCallback.onCreatePanelMenu(featureId, menu);
      }

      @Override
      public boolean onPreparePanel(int featureId, View view, Menu menu) {
        return windowCallback != null && windowCallback.onPreparePanel(featureId, view, menu);
      }

      @Override
      public boolean onMenuOpened(int featureId, Menu menu) {
        return windowCallback != null && windowCallback.onMenuOpened(featureId, menu);
      }

      @Override
      public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return windowCallback != null && windowCallback.onMenuItemSelected(featureId, item);
      }

      @Override
      public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {
        if (windowCallback == null) {
          return;
        }
        windowCallback.onWindowAttributesChanged(attrs);
      }

      @Override
      public void onContentChanged() {
        if (windowCallback == null) {
          return;
        }
        windowCallback.onContentChanged();
      }

      @Override
      public void onWindowFocusChanged(boolean hasFocus) {
        if (windowCallback == null) {
          return;
        }
        windowCallback.onWindowFocusChanged(hasFocus);
      }

      @Override
      public void onAttachedToWindow() {
        if (windowCallback == null) {
          return;
        }
        windowCallback.onAttachedToWindow();
      }

      @Override
      public void onDetachedFromWindow() {
        if (windowCallback == null) {
          return;
        }
        windowCallback.onDetachedFromWindow();
      }

      @Override
      public void onPanelClosed(int featureId, Menu menu) {
        if (windowCallback == null) {
          return;
        }
        windowCallback.onPanelClosed(featureId, menu);
      }

      @Override
      public boolean onSearchRequested() {
        return windowCallback != null && windowCallback.onSearchRequested();
      }

      @RequiresApi(api = Build.VERSION_CODES.M)
      @Override
      public boolean onSearchRequested(SearchEvent searchEvent) {
        return windowCallback != null && windowCallback.onSearchRequested(searchEvent);
      }

      @Nullable
      @Override
      public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return windowCallback == null ? null : windowCallback.onWindowStartingActionMode(callback);
      }

      @RequiresApi(api = Build.VERSION_CODES.M)
      @Nullable
      @Override
      public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
        return windowCallback == null ? null : windowCallback.onWindowStartingActionMode(callback, type);
      }

      @Override
      public void onActionModeStarted(ActionMode mode) {
        if (windowCallback == null) {
          return;
        }
        windowCallback.onActionModeStarted(mode);
      }

      @Override
      public void onActionModeFinished(ActionMode mode) {
        if (windowCallback == null) {
          return;
        }
        windowCallback.onActionModeFinished(mode);
      }
    });

    updateScreenWindowContentSize();

    cache = getSharedPreferences(TAG, Context.MODE_PRIVATE);


    splitSize = cache.getInt(SPLIT_HEIGHT, Math.round(dip2px(36)));
    splitRadius = splitSize/2;

    Point[] points = ballPositionMap.get(activity);
    if (points == null || points.length < 1) {
      points = classBallPositionMap.get(activity.getClass().getName());
    }
    Point p = points == null || points.length < 1 ? null : points[0];
    splitX = p != null && p.x != 0 ? p.x : cache.getInt(SPLIT_X, 0);
    splitY = p != null && p.y != 0 ? p.y : cache.getInt(SPLIT_Y, 0);
    if (splitX >= 0 || Math.abs(splitX) >= windowWidth) { // decorWidth) {
      splitX = Math.round(- splitSize - dip2px(30));
    }
    if (splitY >= 0 || Math.abs(splitY) >= windowHeight) { // decorHeight) {
      splitY = Math.round(- splitSize - dip2px(30));
    }

    // splitX2 = 0;
    // splitY2 = 0;
    // isSplit2Showing = false;

    if (points == null || points.length < 2) {
      points = classBallPositionMap.get(activity.getClass().getName());
    }
    Point p2 = points == null || points.length < 2 ? null : points[1];
    splitX2 = p2 == null ? 0 : p2.x;
    splitY2 = p2 == null ? 0 : p2.y;
    showCover(true);
    // if (isSplitShowing) {
      floatBall = showSplit(isSplitShowing, splitX, splitY, "floatBall", vFloatBall, floatSplitX, floatSplitY);
      // if (isSplit2Showing) {
        floatBall2 = showSplit(isSplitShowing && splitX2 > 0 && splitY2 > 0, splitX2, splitY2, "floatBall2", vFloatBall2, floatSplitX2, floatSplitY2);
      // }
  }

  private Map<EditText, Boolean> editTextWatchedMap = new HashMap<>();
  public void addTextChangedListener(View view) {
    if (view instanceof EditText) {
      EditText et = (EditText) view;
      Boolean watched = editTextWatchedMap.get(et);
      if (watched == null || watched == false) {
        editTextWatchedMap.put(et, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          et.addOnUnhandledKeyEventListener(new View.OnUnhandledKeyEventListener() {
            @Override
            public boolean onUnhandledKeyEvent(View v, KeyEvent event) {
              return false;
            }
          });
        }

        et.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (isSplitShowing == false || isReplay) {
              return;
            }

            InputEvent ie = new EditTextEvent(KeyEvent.ACTION_UP, 0, et, EditTextEvent.WHEN_BEFORE
                    , StringUtil.getString(et.getText()), et.getSelectionStart(), et.getSelectionEnd(), s, start, count, after);
            addInputEvent(ie, callback, activity, fragment);
          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isSplitShowing == false || isReplay) {
              return;
            }

            InputEvent ie = new EditTextEvent(KeyEvent.ACTION_UP, 0, et, EditTextEvent.WHEN_ON
                    , StringUtil.getString(et.getText()), et.getSelectionStart(), et.getSelectionEnd(), s, start, count);
            addInputEvent(ie, callback, activity, fragment);
          }

          @Override
          public void afterTextChanged(Editable s) {
            if (isSplitShowing == false || isReplay) {
              return;
            }

            InputEvent ie = new EditTextEvent(KeyEvent.ACTION_UP, 0, et, EditTextEvent.WHEN_AFTER
                    , StringUtil.getString(et.getText()), et.getSelectionStart(), et.getSelectionEnd(),s);
            addInputEvent(ie, callback, activity, fragment);
          }
        });
      }
    }

    if (view instanceof ViewGroup) {
      ViewGroup vg = (ViewGroup) view;
      for (int i = 0; i < vg.getChildCount(); i++) {
        View cv = vg.getChildAt(i);
        addTextChangedListener(cv);
      }
    }
  }


  private void updateScreenWindowContentSize() {
    DisplayMetrics dm = getResources().getDisplayMetrics();
    DENSITY = dm.density;

    // WindowManager windowManager = window.getWindowManager();
    // Point point = new Point();
    // windowManager.getDefaultDisplay().getRealSize(point);

    activity = getCurrentActivity();
    if (window == null) { // 可能是弹窗的
      window = activity.getWindow();
    }

    DisplayMetrics metric = new DisplayMetrics();
    Display display = activity.getWindowManager().getDefaultDisplay();
    display.getRealMetrics(metric);

    boolean isLand = activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    // 居然是相对屏幕方向的
    screenWidth = metric.widthPixels; // isLand ? metric.widthPixels : metric.heightPixels; // 宽度（PX）
    screenHeight = metric.heightPixels; // isLand ? metric.heightPixels : metric.widthPixels; // 高度（PX）

    // 保持和 FloatWindow 内 Util.getScreenWidth, Util.getScreenHeight 一致
    WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
    Point point = new Point();
    wm.getDefaultDisplay().getSize(point);

    // 居然是绝对的，不是相对屏幕方向的
    windowWidth = point.x;
    windowHeight = point.y;
    if (windowWidth <= 0) {
      windowWidth = metric.widthPixels;
    }
    if (windowHeight <= 0) {
      windowHeight = metric.heightPixels;
    }

    windowX = getWindowX(activity);
    windowY = getWindowY(activity);

    if (decorView == null) {
      decorView = window.getDecorView(); // activity.findViewById(android.R.id.content);
    }

    decorX = decorView == null ? 0 : decorView.getX();
    decorY = decorView == null ? 0 : decorView.getY();
    decorWidth = decorView == null ? windowWidth : decorView.getWidth();
    decorHeight = decorView == null ? windowHeight : decorView.getHeight();
  }

  private void initUIAuto(Application app) {
    APP = app;
    UnitAutoApp.init(app);
    Log.d(TAG, "项目启动 >>>>>>>>>>>>>>>>>>>> \n\n");

    parentDirectory = app.getExternalFilesDir(Environment.DIRECTORY_PICTURES); // new File(screenshotDirPath);
    if (parentDirectory.exists() == false) {
      try {
        parentDirectory.mkdir();
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }

    app.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

      @Override
      public void onActivityStarted(Activity activity) {
        Log.v(TAG, "onActivityStarted  activity = " + activity.getClass().getName());
        onUIEvent(InputUtil.UI_ACTION_START, activity, activity);
      }

      @Override
      public void onActivityStopped(Activity activity) {
        Log.v(TAG, "onActivityStopped  activity = " + activity.getClass().getName());
        onUIEvent(InputUtil.UI_ACTION_STOP, activity, activity);
      }

      @Override
      public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.v(TAG, "onActivitySaveInstanceState  activity = " + activity.getClass().getName());
      }

      @Override
      public void onActivityResumed(Activity activity) {
        Log.v(TAG, "onActivityResumed  activity = " + activity.getClass().getName());
        setCurrentActivity(activity);

        if (isShowing) {
          onUIAutoActivityCreate(activity);
        }
        onUIEvent(InputUtil.UI_ACTION_RESUME, activity, activity);
      }

      @Override
      public void onActivityPaused(Activity activity) {
        Log.v(TAG, "onActivityPaused  activity = " + activity.getClass().getName());
        // setCurrentActivity(activityList.isEmpty() ? null : activityList.get(activityList.size() - 1));
        onUIEvent(InputUtil.UI_ACTION_PAUSE, activity, activity);
        isSplit2Showing = floatBall2 != null && floatBall2.isShowing();
        Point[] points = new Point[]{
                new Point(
                        floatBall == null ? (int) splitX : (int) (floatBall.getX() + splitRadius - windowWidth)
                        , floatBall == null ? (int) splitY : (int) (floatBall.getY() + splitRadius - windowHeight)
                )
                , isSplit2Showing == false ? null : new Point((int) floatBall2.getX(), (int) floatBall2.getY())
        };
        ballPositionMap.put(activity, points);
        classBallPositionMap.put(activity.getClass().getName(), points);
      }

      @Override
      public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.v(TAG, "onActivityCreated  activity = " + activity.getClass().getName());
        activityList.add(activity);
        //TODO 按键、键盘监听拦截和转发
        onUIEvent(InputUtil.UI_ACTION_CREATE, activity, activity);

        onUIAutoFragmentCreate(activity);
      }

      @Override
      public void onActivityDestroyed(Activity activity) {
        Log.v(TAG, "onActivityDestroyed  activity = " + activity.getClass().getName());
        activityList.remove(activity);
        onUIEvent(InputUtil.UI_ACTION_DESTROY, activity, activity);
        ballPositionMap.remove(activity);
      }

    });


    isShowing = false;

    statusResourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (statusResourceId > 0) {
      statusHeight = getResources().getDimensionPixelSize(statusResourceId);
    }
    if (statusHeight <= 0) {
//      id = getResources().getIdentifier("status_bar_height", "dimen", "android");
//      window.findViewById(R.id.)
    }

    // vFloatCover = new FrameLayout(getInstance());
    // ViewGroup.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    // vFloatCover.setLayoutParams(lp);

    vFloatCover = (ViewGroup) getLayoutInflater().inflate(R.layout.ui_auto_cover_layout, null);
    vFloatController = getLayoutInflater().inflate(R.layout.ui_auto_controller_layout, null);
    vFloatBall = (FloatBallView) getLayoutInflater().inflate(R.layout.ui_auto_split_ball_layout, null);
    vFloatBall2 = (FloatBallView) getLayoutInflater().inflate(R.layout.ui_auto_split_ball_layout, null);
    vSplitX = (ViewGroup) getLayoutInflater().inflate(R.layout.ui_auto_split_x_layout, null);
    vSplitX2 = (ViewGroup) getLayoutInflater().inflate(R.layout.ui_auto_split_x_layout, null);
    vSplitY = (ViewGroup) getLayoutInflater().inflate(R.layout.ui_auto_split_y_layout, null);
    vSplitY2 = (ViewGroup) getLayoutInflater().inflate(R.layout.ui_auto_split_y_layout, null);

    tvControllerX = vFloatController.findViewById(R.id.tvControllerX);
    tvControllerDouble = vFloatController.findViewById(R.id.tvControllerDouble);
    tvControllerReturn = vFloatController.findViewById(R.id.tvControllerReturn);
    tvControllerCount = vFloatController.findViewById(R.id.tvControllerCount);
    tvControllerPlay = vFloatController.findViewById(R.id.tvControllerPlay);
    tvControllerTime = vFloatController.findViewById(R.id.tvControllerTime);
    tvControllerForward = vFloatController.findViewById(R.id.tvControllerForward);
    tvControllerSetting = vFloatController.findViewById(R.id.tvControllerSetting);
    tvControllerY = vFloatController.findViewById(R.id.tvControllerY);

    rvControllerTag = vFloatController.findViewById(R.id.rvControllerTag);
    tagAdapter = new RecyclerView.Adapter() {
      @Override
      public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(getLayoutInflater().inflate(R.layout.ui_auto_tag_layout, null, false)) {};
      }

      @Override
      public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // final 问题 position = holder.getAdapterPosition();  // IDE 警告用这个方法替代参数

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
          if (item.getBooleanValue("edit")) {
            action = "EDIT " + EditTextEvent.getWhenName(item.getIntValue("when"))
                    + " [" + item.getIntValue("selectStart") + ", " + item.getIntValue("selectEnd") + "]";
            String s = StringUtil.getString(item.getString("text"));
            int l = s.length();
            if (l > 20) {
              int m = l/2;
              s = s.substring(0, 7) + "..." + s.substring(m - 3, m + 3) + "..." + s.substring(l - 7);
            }
            name = "\n" + s;
          } else {
            name = "\n" + InputUtil.getKeyCodeName(item.getIntValue("keyCode"));
          }
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

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
          @Override
          public boolean onLongClick(View v) {
            int index = position + 1;
            Node<InputEvent> curNode = firstEventNode;
            for (int i = 0; i < index; i++) {
              if (curNode == null) {
                break;
              }

              curNode = curNode.next;
            }

            currentEventNode = curNode;
            step = index;

            tvControllerCount.setText(step + "/" + allStep);
            onEventChange(step - 1, curNode == null ? 0 : curNode.type);  // move 时刷新容易卡顿
            return true;
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

    // vFloatCover.addView(vSplitX);
    // vFloatCover.addView(vSplitY);
    // vFloatCover.addView(vSplitX2);
    // vFloatCover.addView(vSplitY2);

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

    // vFloatBall.setOnLongClickListener(new View.OnLongClickListener() {
    // 	@Override
    // 	public boolean onLongClick(View v) {
    //
    // 		return true;
    // 	}
    // });

    // vFloatBall2.setOnLongClickListener(new View.OnLongClickListener() {
    // 	@Override
    // 	public boolean onLongClick(View v) {
    // 		return vFloatBall.performLongClick();
    // 	}
    // });
    //
    // vFloatBall.setOnTouchListener(new View.OnTouchListener() {
    //   @Override
    //   public boolean onTouch(View v, MotionEvent event) {
    //     // 都不动了 if (event.getY() - event.getRawY() >= 10) {
    //     // if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_HOVER_MOVE) {
    //     // 	moved = true;
    //     // 	vSplitY.setY(event.getY());
    //     //       // vSplitY.invalidate();
    //     // } else {
    //     // 	if (event.getAction() == MotionEvent.ACTION_DOWN) {
    //     // 		moved = false;
    //     // 	}
    //     // 	else if (event.getAction() == MotionEvent.ACTION_UP) {
    //     // 		if (! moved) {
    //     // 			ivUIAutoSplitY.performClick();
    //     // 		}
    //     // 	}
    //     // }
    //     //   // }
    //     // return true;
    //
    //
    //     if (event.getAction() == MotionEvent.ACTION_DOWN) {
    //       vSplitX.setVisibility(View.VISIBLE);
    //       vSplitY.setVisibility(View.VISIBLE);
    //       vSplitX2.setVisibility(vFloatBall2.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
    //       vSplitY2.setVisibility(vFloatBall2.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
    //     }
    //     else if (event.getAction() == MotionEvent.ACTION_UP) {
    //       vSplitX.setVisibility(View.GONE);
    //       vSplitY.setVisibility(View.GONE);
    //       vSplitX2.setVisibility(View.GONE);
    //       vSplitY2.setVisibility(View.GONE);
    //     }
    //     return false;
    //   }
    // });

    // vFloatBall2.setOnTouchListener(new View.OnTouchListener() {
    //   @Override
    //   public boolean onTouch(View v, MotionEvent event) {
    //     if (event.getAction() == MotionEvent.ACTION_DOWN) {
    //       vSplitX.setVisibility(vFloatBall.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
    //       vSplitY.setVisibility(vFloatBall.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
    //       vSplitX2.setVisibility(View.VISIBLE);
    //       vSplitY2.setVisibility(View.VISIBLE);
    //     }
    //     else if (event.getAction() == MotionEvent.ACTION_UP) {
    //       vSplitX2.setVisibility(View.GONE);
    //       vSplitY2.setVisibility(View.GONE);
    //       vSplitX2.setVisibility(View.GONE);
    //       vSplitY2.setVisibility(View.GONE);
    //     }
    //     return false;
    //   }
    // });

    // ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    // root.addView(vFloatCover, lp);


    tvControllerDouble.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (isSplitShowing == false || floatBall == null) {
          Toast.makeText(getCurrentActivity(), R.string.please_firstly_record_or_replay, Toast.LENGTH_SHORT).show();
          return;
        }

        isSplit2Showing = floatBall2 != null && floatBall2.isShowing();
        isSplit2Showing = ! isSplit2Showing;

        // FloatWindow.destroy("floatBall2");
        // floatBall2 = null;
        // if (isSplit2Showing) {
          floatBall2 = showSplit(isSplit2Showing, - floatBall.getX() - splitRadius, - floatBall.getY() - splitSize, "floatBall2", vFloatBall2, floatSplitX2, floatSplitY2);
        // }
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
    tvControllerReturn.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        handler.removeMessages(0);
        if (step != 0) {
          step = 0;
          tvControllerCount.setText(step + "/" + allStep);
          onEventChange(0, 0L);
        }
        return true;
      }
    });

    // tvControllerCount.setOnClickListener(new View.OnClickListener() {
    //     @Override
    //     public void onClick(View v) {
    //         startActivity(UIAutoListActivity.createIntent(getApp(), flowId));
    //     }
    // });

    tvControllerPlay.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onClickPlay();
      }
    });

    // tvControllerTime.setOnClickListener(new View.OnClickListener() {
    // @Override
    //     public void onClick(View v) {
    //         startActivity(UIAutoListActivity.createIntent(getApp(), true));
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
    tvControllerForward.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        handler.removeMessages(0);
        if (step != allStep + 1) {
          step = allStep + 1;
          tvControllerCount.setText(step + "/" + allStep);
          onEventChange(allStep - 1, 0L);
        }
        return true;
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

  private void onUIAutoFragmentCreate(Activity activity) {
    if (activity instanceof FragmentActivity) {
      FragmentActivity fa = (FragmentActivity) activity;
      FragmentManager sfm = fa.getSupportFragmentManager();

      Boolean watched = fragmentWatchedMap.get(sfm);
      if (watched == null || watched == false) {
        fragmentWatchedMap.put(sfm, true);

        sfm.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
          @Override
          public void onFragmentPreAttached(FragmentManager fm, Fragment f, Context context) {
            super.onFragmentPreAttached(fm, f, context);
            Log.v(TAG, "onFragmentPreAttached  fragment = " + f.getClass().getName());
            onUIEvent(InputUtil.UI_ACTION_PREATTACH, f.getActivity(), f);
          }

          @Override
          public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
            super.onFragmentAttached(fm, f, context);
            Log.v(TAG, "onFragmentAttached  fragment = " + f.getClass().getName());
            onUIEvent(InputUtil.UI_ACTION_ATTACH, f.getActivity(), f);
          }

          @Override
          public void onFragmentPreCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            super.onFragmentPreCreated(fm, f, savedInstanceState);
            Log.v(TAG, "onFragmentPreCreated  fragment = " + f.getClass().getName());
            onUIEvent(InputUtil.UI_ACTION_PRECREATE, f.getActivity(), f);
          }

          @Override
          public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            super.onFragmentCreated(fm, f, savedInstanceState);
            Log.v(TAG, "onFragmentCreated  fragment = " + f.getClass().getName());
            onUIEvent(InputUtil.UI_ACTION_CREATE, f.getActivity(), f);
          }

          @Override
          public void onFragmentActivityCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            super.onFragmentActivityCreated(fm, f, savedInstanceState);
          }

          @Override
          public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState);
            Log.v(TAG, "onFragmentViewCreated  fragment = " + f.getClass().getName());
            onUIEvent(InputUtil.UI_ACTION_CREATE_VIEW, f.getActivity(), f);
          }

          @Override
          public void onFragmentStarted(FragmentManager fm, Fragment f) {
            super.onFragmentStarted(fm, f);
            Log.v(TAG, "onFragmentStarted  fragment = " + f.getClass().getName());
            onUIEvent(InputUtil.UI_ACTION_START, f.getActivity(), f);
          }

          @Override
          public void onFragmentResumed(FragmentManager fm, Fragment f) {
            super.onFragmentResumed(fm, f);
            setCurrentFragment(f);
            Log.v(TAG, "onFragmentResumed  fragment = " + f.getClass().getName());
            onUIEvent(InputUtil.UI_ACTION_RESUME, f.getActivity(), f);
          }

          @Override
          public void onFragmentPaused(FragmentManager fm, Fragment f) {
            super.onFragmentPaused(fm, f);
            Log.v(TAG, "onFragmentPaused  fragment = " + f.getClass().getName());
            onUIEvent(InputUtil.UI_ACTION_PAUSE, f.getActivity(), f);
            setCurrentFragment(null);
          }

          @Override
          public void onFragmentStopped(FragmentManager fm, Fragment f) {
            super.onFragmentStopped(fm, f);
            Log.v(TAG, "onFragmentStopped  fragment = " + f.getClass().getName());
            onUIEvent(InputUtil.UI_ACTION_STOP, f.getActivity(), f);
          }

          @Override
          public void onFragmentSaveInstanceState(FragmentManager fm, Fragment f, Bundle outState) {
            super.onFragmentSaveInstanceState(fm, f, outState);
          }

          @Override
          public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
            super.onFragmentViewDestroyed(fm, f);
            Log.v(TAG, "onFragmentViewDestroyed  fragment = " + f.getClass().getName());
            onUIEvent(InputUtil.UI_ACTION_DESTROY_VIEW, f.getActivity(), f);
          }

          @Override
          public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
            super.onFragmentDestroyed(fm, f);
            Log.v(TAG, "onFragmentDestroyed  fragment = " + f.getClass().getName());
            onUIEvent(InputUtil.UI_ACTION_DESTROY, f.getActivity(), f);
          }

          @Override
          public void onFragmentDetached(FragmentManager fm, Fragment f) {
            super.onFragmentDetached(fm, f);
            Log.v(TAG, "onFragmentDetached  fragment = " + f.getClass().getName());
            onUIEvent(InputUtil.UI_ACTION_DETACH, f.getActivity(), f);
          }
        }, true);
      }

      // TODO deprecated     android.app.FragmentManager fm = fa.getFragmentManager();
    }

  }

  public void onClickPlay() {
    isSplitShowing = ! isSplitShowing;
    tvControllerPlay.setText(isReplay ? (isSplitShowing ? R.string.replaying : R.string.replay) : (isSplitShowing ? R.string.recording : R.string.record));
    floatBall = showSplit(isSplitShowing, splitX, splitY, "floatBall", vFloatBall, floatSplitX, floatSplitY);
    floatBall2 = showSplit(isSplitShowing && splitX2 > 0 && splitY2 > 0, splitX2, splitY2, "floatBall2", vFloatBall2, floatSplitX2, floatSplitY2);

    // FloatWindow.destroy("floatBall2");
    // floatBall2 = null;

    currentTime = System.currentTimeMillis();

    if (isSplitShowing) {
      if (isReplay) {
        replay();
      }
      else {
        record();
      }
    }

  }

  private void dismiss() {
    count = 0;

    isShowing = false;
    isSplitShowing = false;
    // ((ViewGroup) v.getParent()).removeView(v);
    tvControllerPlay.setText(isReplay ? (isSplitShowing ? R.string.replaying : R.string.replay) : (isSplitShowing ? R.string.recording : R.string.record));

    floatCover = null;
    floatController = null;
    floatBall = null;
    floatBall2 = null;
    FloatWindow.destroy("floatCover");
    FloatWindow.destroy("floatController");
    FloatWindow.destroy("floatBall");
    FloatWindow.destroy("floatBall2");

    try {
      FloatWindow.destroy("floatSplitX");
      FloatWindow.destroy("floatSplitY");
      FloatWindow.destroy("floatSplitX2");
      FloatWindow.destroy("floatSplitY2");
    }
    catch (Throwable e) {
      e.printStackTrace();
    }
  }


  public void onUIAutoWindowDestroy(Window.Callback callback, Window window) {
    if (activity == null) {
      activity = getCurrentActivity();
    }

    if (callback instanceof Dialog) {
      if (activity == null) {
        activity = ((Dialog) callback).getOwnerActivity();
        setCurrentActivity(activity);
      }
      onUIAutoActivityCreate(activity);
    }
    else {
      this.window = activity == null ? null : activity.getWindow();
    }
  }


  public void onUIAutoActivityDestroy(Window.Callback callback, Activity activity) {
    cache.edit()
      .remove(SPLIT_X)
      // .putInt(SPLIT_X, Math.round(vSplitX.getX() + vSplitX.getWidth()/2 - windowWidth))
      .putInt(SPLIT_X, Math.round(floatSplitX.getX() + vSplitX.getWidth()/2 - windowWidth))
      .remove(SPLIT_Y)
      // .putInt(SPLIT_Y, Math.round(vSplitY.getY() + vSplitY.getHeight()/2 - windowHeight))
      .putInt(SPLIT_Y, Math.round(floatSplitY.getY() + vSplitY.getHeight()/2 - windowHeight))
      .apply();

    onUIAutoWindowDestroy(callback, activity == null ? null : activity.getWindow());
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


  private List<Activity> activityList = new LinkedList<>();

  private WeakReference<Activity> currentActivityWeakRef;
  public Activity getCurrentActivity() {
    return currentActivityWeakRef == null ? null : currentActivityWeakRef.get();
  }

  public void setCurrentActivity(Activity activity) {
    this.activity = activity;
    if (currentActivityWeakRef == null || ! activity.equals(currentActivityWeakRef.get())) {
      currentActivityWeakRef = new WeakReference<>(activity);
    }

    UnitAutoApp.setCurrentActivity(activity);
  }

  private WeakReference<Fragment> currentFragmentWeakRef;
  public Fragment getCurrentFragment() {
    return currentFragmentWeakRef == null ? null : currentFragmentWeakRef.get();
  }
  public void setCurrentFragment(Fragment fragment) {
    this.fragment = fragment;
    if (fragment != null && (currentFragmentWeakRef == null || ! fragment.equals(currentFragmentWeakRef.get()))) {
      currentFragmentWeakRef = new WeakReference<>(fragment);
    }
  }





//  public boolean onTouchEvent(@NotNull MotionEvent event, @NotNull Activity activity) {
//    return onTouchEvent(event, activity, null);
//  }
//  public boolean onTouchEvent(@NotNull MotionEvent event, @NotNull Fragment fragment) {
//    return onTouchEvent(event, fragment.getActivity(), fragment);
//  }
  public boolean onTouchEvent(@NotNull MotionEvent event, @NotNull Activity activity, Fragment fragment) {
    addInputEvent(event, activity, activity, fragment);
    return true;
  }
  public boolean onKeyDown(int keyCode, @NotNull KeyEvent event, @NotNull Activity activity, Fragment fragment) {
    addInputEvent(event, activity, activity, fragment);
    return true;
  }
  public boolean onKeyUp(int keyCode, @NotNull KeyEvent event, @NotNull Activity activity, Fragment fragment) {
    addInputEvent(event, activity, activity, fragment);
    return true;
  }

  public void record() {
    showCoverAndSplit(true, true);
  }


  private int lastOrientation;
  // LifecycleOwner 只覆盖 Activity, Fragment, 而 Window.Callback 只覆盖 Activity, Dialog
  private final Map<Object, Point[]> ballPositionMap = new HashMap<>();
  private final Map<String, Point[]> classBallPositionMap = new HashMap<>();
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    if (newConfig == null || lastOrientation == newConfig.orientation) {
      return;
    }
    lastOrientation = newConfig.orientation;
    updateScreenWindowContentSize();

    postDelayed(new Runnable() {
      @Override
      public void run() {
        updateScreenWindowContentSize();

        if (isShowing) {
//          FloatWindow.destroy("floatBall");
//          FloatWindow.destroy("floatBall2");
//          FloatWindow.destroy("floatCover");
//          FloatWindow.destroy("floatController");
//          try {
//            FloatWindow.destroy("floatSplitX");
//            FloatWindow.destroy("floatSplitY");
//            FloatWindow.destroy("floatSplitX2");
//            FloatWindow.destroy("floatSplitY2");
//          }
//          catch (Throwable e) {
//            e.printStackTrace();
//          }

          showCover(true);
          // if (isSplitShowing) {
            floatBall = showSplit(isSplitShowing, splitX, splitY, "floatBall", vFloatBall, floatSplitX, floatSplitY);
            // if (isSplit2Showing) {
              floatBall2 = showSplit(isSplitShowing && splitX2 > 0 && splitY2 > 0, splitX2, splitY2, "floatBall2", vFloatBall2, floatSplitX2, floatSplitY);


        }
      }
    }, 1000);
  }

  private void showCoverAndSplit(boolean showCover, boolean showSplit) {
    showCover(showCover);
    floatBall = showSplit(showSplit, splitX, splitY, "floatBall", vFloatBall, floatSplitX, floatSplitY);
    floatBall2 = showSplit(showSplit && splitX2 > 0 && splitY2 > 0, splitX2, splitY2, "floatBall2", vFloatBall2, floatSplitX2, floatSplitY2);
  }

  //TODO 仅在触摸 ball 时显示分割线，重写 onTouchEvent
  private IFloatWindow floatCover;
  private IFloatWindow floatController;
  private IFloatWindow floatBall, floatBall2;
  private IFloatWindow floatSplitX;
  private IFloatWindow floatSplitY;
  private IFloatWindow floatSplitX2;
  private IFloatWindow floatSplitY2;

  private boolean isShowing = false;
  public void showCover(boolean show) {
    isShowing = show;

//    导致遮挡触摸，试了几个方法都不能很好地解决，还不如 4 条分割线单独放 FloatWindow
//     floatCover = FloatWindow.get("floatCover");
//     if (floatCover == null) {
//       FloatWindow
//         .with(getApplicationContext())
//         .setTag("floatCover")
//         .setView(vFloatCover)
//         .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)      //设置控件宽高
//         .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
//         // .setX(windowX)                                   //设置控件初始位置
//         // .setY(windowY)
//         .setMoveType(MoveType.inactive)
//         .setDesktopShow(true) //必须为 true，否则切换 Activity 就会自动隐藏                        //桌面显示
// //                .setViewStateListener(mViewStateListener)    //监听悬浮控件状态改变
// //                .setPermissionListener(mPermissionListener)  //监听权限申请结果
//         .build();
//
//       floatCover = FloatWindow.get("floatCover");
//     }
//     floatCover.show();
//     floatCover.hide();

    floatController = FloatWindow.get("floatController");
    if (floatController == null) {
      FloatWindow
        .with(getApplicationContext())
        .setTag("floatController")
        .setView(vFloatController)
        .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // windowWidth - windowX)                               //设置控件宽高
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



    floatSplitX = FloatWindow.get("floatSplitX");
    if (floatSplitX == null) {
      FloatWindow
        .with(getApplicationContext())
        .setTag("floatSplitX")
        .setView(vSplitX)
        .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)                    //设置控件宽高
        .setMoveType(MoveType.inactive)
        .setDesktopShow(true) //必须为 true，否则切换 Activity 就会自动隐藏                        //桌面显示
        .build();

      floatSplitX = FloatWindow.get("floatSplitX");
    }
    // floatSplitX.show();

    floatSplitY = FloatWindow.get("floatSplitY");
    if (floatSplitY == null) {
      FloatWindow
        .with(getApplicationContext())
        .setTag("floatSplitY")
        .setView(vSplitY)
        .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)                    //设置控件宽高
        .setMoveType(MoveType.inactive)
        .setDesktopShow(true) //必须为 true，否则切换 Activity 就会自动隐藏                        //桌面显示
        .build();

      floatSplitY = FloatWindow.get("floatSplitY");
    }
    // floatSplitY.show();

    floatSplitX2 = FloatWindow.get("floatSplitX2");
    if (floatSplitX2 == null) {
      FloatWindow
        .with(getApplicationContext())
        .setTag("floatSplitX2")
        .setView(vSplitX2)
        .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)                    //设置控件宽高
        .setMoveType(MoveType.inactive)
        .setDesktopShow(true) //必须为 true，否则切换 Activity 就会自动隐藏                        //桌面显示
        .build();

      floatSplitX2 = FloatWindow.get("floatSplitX2");
    }
    // floatSplitX2.show();

    floatSplitY2 = FloatWindow.get("floatSplitY2");
    if (floatSplitY2 == null) {
      FloatWindow
        .with(getApplicationContext())
        .setTag("floatSplitY2")
        .setView(vSplitY2)
        .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)                    //设置控件宽高
        .setMoveType(MoveType.inactive)
        .setDesktopShow(true) //必须为 true，否则切换 Activity 就会自动隐藏                        //桌面显示
        .build();

      floatSplitY2 = FloatWindow.get("floatSplitY2");
    }
    // floatSplitY2.show();


    if (show) {
      floatController.show();
//      floatSplitX.show();
//      floatSplitX2.show();
//      floatSplitY.show();
//      floatSplitY2.show();
    } else {
      floatController.hide();
    }

//    if (floatBall != null) {
//      floatSplitX.updateX(floatBall.getX() - splitRadius);
//      floatSplitY.updateY(floatBall.getY() - splitRadius);
//    }
//    if (floatBall2 != null) {
//      floatSplitX2.updateX(floatBall2.getX() - splitRadius);
//      floatSplitY2.updateY(floatBall2.getY() - splitRadius);
//    }
    floatSplitX.hide();
    floatSplitX2.hide();
    floatSplitY.hide();
    floatSplitY2.hide();
  }

  private IFloatWindow showFloatView(boolean show, String tag, View view, int width, int height, int x, int y, int moveType) {
    IFloatWindow fw = FloatWindow.get(tag);
    if (show == false) {
      if (fw != null) {
        fw.hide();
      }
      return fw;
    }

    if (fw == null) {
      FloatWindow
        .with(getApplicationContext())
        .setTag(tag)
        .setView(view)
        .setWidth(width)                               //设置控件宽高
        .setHeight(height)
        .setX(x)                                   //设置控件初始位置
        .setY(y)
        .setMoveType(moveType)
        .setDesktopShow(true) //必须为 true，否则切换 Activity 就会自动隐藏                        //桌面显示
//                .setViewStateListener(mViewStateListener)    //监听悬浮控件状态改变
//                .setPermissionListener(mPermissionListener)  //监听权限申请结果
        .build();

      fw = FloatWindow.get(tag);
    }
    fw.show();

    return fw;
  }

  private boolean isSplitShowing, isSplit2Showing;
  private IFloatWindow showSplit(boolean show, float splitX, float splitY, String ballName, FloatBallView vFloatBall, IFloatWindow floatSplitX_, IFloatWindow floatSplitY_) {
    // vSplitX.setVisibility(View.GONE);
    // vSplitY.setVisibility(View.GONE);
    // vSplitX2.setVisibility(View.GONE);
    // vSplitY2.setVisibility(View.GONE);

    // floatCover.hide();

    // showFloatView(true, "splitX", vSplitX_, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, splitX, 0, MoveType.inactive);
    // showFloatView(true, "splitY", vSplitY_, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0, splitY, MoveType.inactive);
    // showFloatView(true, "splitX2", vSplitX2, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, splitX2, 0, MoveType.inactive);
    // showFloatView(true, "splitY2", vSplitY2, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, splitY2, MoveType.inactive);

    int x = Math.round(splitX - splitRadius + (splitX > 0 ? 0 : windowWidth)); // 只有贴边才会自动处理 decorWidth); // 已被 FloatWindow 处理 windowX + decorX
    int y = Math.round(splitY - splitRadius + (splitX > 0 ? 0 : windowHeight)); // 只有贴边才会自动处理  decorHeight); // 已被 FloatWindow 处理 windowY + decorY
    if (floatSplitX_ != null) {
      try {
        floatSplitX_.updateX(x + Math.round(splitRadius) - dip2px(0.5f));
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
    if (floatSplitY_ != null) {
      try {
        floatSplitY_.updateY(y + Math.round(splitRadius) - dip2px(0.5f));
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
    vSplitX.setVisibility(View.GONE);
    vSplitY.setVisibility(View.GONE);
    vSplitX2.setVisibility(View.GONE);
    vSplitY2.setVisibility(View.GONE);
    if (floatBall2 != null) {
      floatBall2.hide();
    }
    IFloatWindow ball = FloatWindow.get(ballName);
    if (show == false) {
      if (ball != null) {
        ball.hide();
      }
      return ball;
    }

    if (ball == null) {
      vFloatBall.setExtraOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
          if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 虽然也能实现，但线条区域拦截了触摸事件
            vSplitX.setVisibility(floatBall != null && floatBall.isShowing() ? vFloatBall.getVisibility() : View.GONE);
            vSplitY.setVisibility(vSplitX.getVisibility());
            vSplitX2.setVisibility(floatBall2 != null && floatBall2.isShowing() ? vFloatBall2.getVisibility() : View.GONE);
            vSplitY2.setVisibility(vSplitX2.getVisibility());

            tvControllerX.setVisibility(v.getVisibility());
            tvControllerY.setVisibility(v.getVisibility());

            // floatCover.show();

            // 太卡了 // 避免线条区域拦截了触摸事件
            // if (vFloatBall.getVisibility() == View.VISIBLE) {
            //   if (floatSplitX != null) {
            //     floatSplitX.show();
            //   }
            //   if (floatSplitY != null) {
            //     floatSplitY.show();
            //   }
            // } else {
            //   if (floatSplitX != null) {
            //     floatSplitX.hide();
            //   }
            //   if (floatSplitY != null) {
            //     floatSplitY.hide();
            //   }
            // }
            //
            // if (vFloatBall2.getVisibility() == View.VISIBLE) {
            //   if (floatSplitX2 != null) {
            //     floatSplitX2.show();
            //   }
            //   if (floatSplitY != null) {
            //     floatSplitY2.show();
            //   }
            // } else {
            //   if (floatSplitX != null) {
            //     floatSplitX2.hide();
            //   }
            //   if (floatSplitY != null) {
            //     floatSplitY2.hide();
            //   }
            // }
          }
          else if (event.getAction() == MotionEvent.ACTION_UP) {
            // floatCover.hide();

            // 虽然也能实现，但线条区域拦截了触摸事件
            vSplitX.setVisibility(View.GONE);
            vSplitY.setVisibility(View.GONE);
            vSplitX2.setVisibility(View.GONE);
            vSplitY2.setVisibility(View.GONE);

            tvControllerX.setVisibility(View.GONE);
            tvControllerY.setVisibility(View.GONE);

            // 太卡了 // 避免线条区域拦截了触摸事件
            // if (floatSplitX != null) {
            //   floatSplitX.hide();
            // }
            // if (floatSplitY != null) {
            //   floatSplitY.hide();
            // }
            //
            // if (floatSplitX2 != null) {
            //   floatSplitX2.hide();
            // }
            // if (floatSplitY2 != null) {
            //   floatSplitY2.hide();
            // }
          }

          return false;
        }
      });

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


                JSONArray allList = eventList;

                // JSONArray allList = new JSONArray();  //  eventList; //
                // if (eventList != null) {
                //     for (int i = 0; i < eventList.size(); i++) {
                //         JSONObject obj = eventList.getJSONObject(i);
                //         if (obj != null && obj.getBooleanValue("disable") == false) {
                //             allList.add(obj);
                //         }
                //     }
                // }

                cache.edit().remove(cacheKey).putString(cacheKey, toJSONString(allList)).commit();
              }

              mainHandler.post(new Runnable() {
                @Override
                public void run() {
                  //                startActivity(UIAutoListActivity.createIntent(DemoApplication.getApp(), flowId));  // eventList == null ? null : eventList.toJSONString()));
//                startActivityForResult(UIAutoListActivity.createIntent(DemoApplication.getApp(), eventList == null ? null : eventList.toJSONString()), REQUEST_UI_AUTO_LIST);
                  count = 0;
                  startActivity(UIAutoListActivity.createIntent(getApp(), cacheKey));
                }
              });
            }
          }).start();
        }
      });

      int size = Math.round(splitSize);

      FloatWindow
        .with(getApplicationContext())
        .setTag(ballName)
        .setView(vFloatBall)
        .setWidth(size)                       //设置控件宽高
        .setHeight(size)
        .setX(x)                                   //设置控件初始位置
        .setY(y)
        .setMoveType(MoveType.active)
        .setDesktopShow(true) //必须为 true，否则切换 Activity 就会自动隐藏 //桌面显示
        .setViewStateListener(new ViewStateListener() {
          @Override
          public void onPositionUpdate(int x, int y) {
            int splitX = x + Math.round(splitRadius);
            int splitY = y + Math.round(splitRadius);

            if (floatSplitX_ != null) { //  && floatSplitX_.isShowing()) {
              try {
              floatSplitX_.updateX(splitX - dip2px(0.5f));
              } catch (Throwable e) {
                e.printStackTrace();
              }
            }
            if (floatSplitY_ != null) { //   && floatSplitY_.isShowing()) {
              try {
              floatSplitY_.updateY(splitY - dip2px(0.5f));
              } catch (Throwable e) {
                e.printStackTrace();
              }
            }

            double xr = 100f*splitX/windowWidth;
            double yr = 100f*splitY/windowHeight;

            tvControllerX.setText(DECIMAL_FORMAT.format(xr) + "%" + "\n" + DECIMAL_FORMAT.format(yr) + "%");
            tvControllerY.setText(splitX + "\n" + splitY);
          }

          @Override
          public void onShow() {
            IFloatWindow floatBall = FloatWindow.get(ballName);
            onPositionUpdate(floatBall == null ? x : floatBall.getX(), floatBall == null ? y : floatBall.getY());
          }

          @Override
          public void onHide() {
            if (floatSplitX_ != null) {
              floatSplitX_.hide();
            }
            if (floatSplitY_ != null) {
              floatSplitY_.hide();
            }
          }

          @Override
          public void onDismiss() {
            onHide();
          }

          @Override
          public void onMoveAnimStart() { }
          @Override
          public void onMoveAnimEnd() { }
          @Override
          public void onBackToDesktop() { }
        })    //监听悬浮控件状态改变
//                .setPermissionListener(mPermissionListener)  //监听权限申请结果
        .build();

      ball = FloatWindow.get(ballName);
    }
    else {
      ball.updateX(x);
      ball.updateY(y);
    }

    ball.show();
    tvControllerX.setText(splitX + "\n" + (splitX/windowWidth) + "%");
    tvControllerY.setText(splitY + "\n" + (splitY/windowHeight) + "%");

    if (floatSplitX_ != null && floatSplitX_.isShowing()) {
      floatSplitX_.updateX(x + Math.round(splitRadius) - dip2px(0.5f));
      floatSplitX_.hide();
    }
    if (floatSplitY_ != null && floatSplitY_.isShowing()) {
      floatSplitY_.updateY(y + Math.round(splitRadius) - dip2px(0.5f));
      floatSplitY_.hide();
    }

    return ball;
  }

  public static String toJSONString(Object obj) {
    return JSON.toJSONString(obj, new PropertyFilter() {
      @Override
      public boolean apply(Object object, String name, Object value) {
        if (value == null) {
          return true;
        }

        if (value instanceof Context
                || value instanceof Fragment
                || value instanceof android.app.Fragment
                || value instanceof Annotation  // Android 客户端中 fastjon 怎么都不支持 Annotation
                || value instanceof WindowManager
                || value instanceof PowerManager
                || value instanceof View
                || value instanceof ViewParent
                || value instanceof Drawable
                || value instanceof Bitmap
        ) {
          return false;
        }

        return Modifier.isPublic(value.getClass().getModifiers());
      }
    });
  }

  public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");


  public int getWindowX(Activity activity) {
    return 0;
    // View decorView = activity.getWindow().getContentView();
    //
    // Rect rectangle = new Rect();
    // decorView.getWindowVisibleDisplayFrame(rectangle);
    // return rectangle.left;
  }

  public int getWindowY(Activity activity) {
    return 0;
    // View decorView = activity.getWindow().getContentView();
    //
    // Rect rectangle = new Rect();
    // decorView.getWindowVisibleDisplayFrame(rectangle);
    // return rectangle.top;
  }


  public boolean dispatchEventToCurrentWindow(InputEvent ie, boolean record) {
    if (ie == null) {
      return false;
    }

//    activity = getCurrentActivity();
//    if (activity != null) {
//      if (ie instanceof MotionEvent) {
//        MotionEvent event = (MotionEvent) ie;
////        int windowX = getWindowX(activity);
////        int windowY = getWindowY(activity) + statusHeight;
////
////        if (windowX > 0 || windowY > 0) {
////          event = MotionEvent.obtain(event);
////          event.offsetLocation(windowX, windowY);
////        }
//
//        try {
//          activity.dispatchTouchEvent(event);
//        } catch (Throwable e) {  // java.lang.IllegalArgumentException: tagerIndex out of range
//          e.printStackTrace();
//        }
//      }
//      else if (ie instanceof KeyEvent) {
//        KeyEvent event = (KeyEvent) ie;
//        activity.dispatchKeyEvent(event);
//      }
//    }

    if (callback != null) {
      if (ie instanceof MotionEvent) {
        MotionEvent event = (MotionEvent) ie;
//        int windowX = getWindowX(activity);
//        int windowY = getWindowY(activity) + statusHeight;
//
//        if (windowX > 0 || windowY > 0) {
//          event = MotionEvent.obtain(event);
//          event.offsetLocation(windowX, windowY);
//        }
        try {
          callback.dispatchTouchEvent(event);
        } catch (Throwable e) {  // java.lang.IllegalArgumentException: tagerIndex out of range
          e.printStackTrace();
        }
      }
      else if (ie instanceof KeyEvent) {
        if (ie instanceof EditTextEvent) {
          EditTextEvent ete = (EditTextEvent) ie;
          if (ete.getWhen() == EditTextEvent.WHEN_ON) {
            EditText target = ete.getTarget();
            if (target == null || target.isAttachedToWindow() == false) {
              target = findView(ete.getTargetId());
            }
            if (target == null) {
              target = findViewByFocus(getCurrentDecorView(), EditText.class);
            }

            if (target.hasFocus() == false) {
              target.requestFocus();
            }

            String text = StringUtil.getString(target.getText());
            int l = text.length();
            int start = Math.min(l, Math.max(0, ete.getSelectStart()));
            int end = Math.min(l, Math.max(0, ete.getSelectEnd()));
            target.setSelection(start, end);

            target.setText(ete.getText());

            String text2 = StringUtil.getString(target.getText());
            int l2 = text2.length();
            int start2 = Math.min(l2, Math.max(0, ete.getSelectStart()));
            int end2 = Math.min(l2, Math.max(0, ete.getSelectEnd()));
            if (end2 <= 0) {
              start2 = end2 = l2;
            }

            target.setSelection(start2, end2);
          }
        }
        else {
          KeyEvent event = (KeyEvent) ie;
          callback.dispatchKeyEvent(event);
        }
      }
    }

    if (record) {
      addInputEvent(ie, callback, activity, fragment);
    }

    return callback != null;
  }



  /**
   * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
   */
  public int dip2px(float dpValue) {
    final float scale = DENSITY;
    return (int) (dpValue * scale + 0.5f);  // + 0.5f 是为了让结果四舍五入
  }

  /**
   * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
   */
  public int px2dip(float pxValue) {
    final float scale = DENSITY;
    return (int) (pxValue / scale + 0.5f);  // + 0.5f 是为了让结果四舍五入
  }


  private Node<InputEvent> firstEventNode;
  private Node<InputEvent> currentEventNode;

  private long duration = 0;
  private int allStep = 0;
  private int step = 0;
  private int lastStep = 0;
//  private int lastWaitStep = 0;

  private long currentTime = 0;
  public void replay() {
    replay(0);
  }
  public void replay(int step) {
    isReplay = true;
//        List<InputEvent> list = new LinkedList<>();
    if (step >= allStep) {
      step = 0;
      currentEventNode = firstEventNode;
    }
    else {
      Node<InputEvent> curNode = firstEventNode;
      for (int i = 0; i < step; i++) {
        curNode = curNode == null ? null : curNode.next;
        if (curNode == null) {
          curNode = firstEventNode;
          step = 0;
          break;
        }
      }

      currentEventNode = curNode;
    }

    this.step = step;

    JSONObject first = allStep <= 0 ? null : eventList.getJSONObject(0);
    long firstTime = first == null ? 0 : first.getLongValue("time");

    if (firstTime <= 0) {
      currentTime = 0;
      Toast.makeText(getApp(), R.string.finished_because_of_no_step, Toast.LENGTH_SHORT).show();
      tvControllerPlay.setText("replay");
      showCoverAndSplit(true, false);
    }
    else {
      tvControllerPlay.setText("replaying");
      showCoverAndSplit(true, true);

      currentTime = System.currentTimeMillis();

      //通过递归链表来实现
      Message msg = handler.obtainMessage();
      msg.obj = currentEventNode;
      handler.sendMessage(msg);
    }

  }

  private Node<InputEvent> eventNode = null;
  public void prepareAndSendEvent(@NotNull JSONArray eventList) {
    prepareAndSendEvent(eventList, 0);
  }
  public void prepareAndSendEvent(@NotNull JSONArray eventList, int step) {
    currentEventNode = null;
    Node<InputEvent> eventNode = new Node<>(null, null, null);
    for (int i = 0; i < eventList.size(); i++) {
      JSONObject obj = eventList.getJSONObject(i);
      if (obj == null) { // || obj.getBooleanValue("disable")) {
        continue;
      }

      // if (i <= 0) {
      //   firstEventNode = new Node<>(null, null, null);
      //   eventNode = firstEventNode;
      // }

      int type = obj.getIntValue("type");
      int action = obj.getIntValue("action");

      InputEvent event;
      if (type == InputUtil.EVENT_TYPE_KEY) {
        if (obj.getBooleanValue("edit")) {
          event = new EditTextEvent(
                  obj.getLongValue("downTime"),
                  obj.getLongValue("eventTime"),
                  obj.getIntValue("action"),
                  obj.getIntValue("keyCode"),
                  obj.getIntValue("repeatCount"),
                  obj.getIntValue("metaState"),
                  obj.getIntValue("deviceId"),
                  obj.getIntValue("scanCode"),
                  obj.getIntValue("flags"),
                  obj.getIntValue("source"),
                  activity.findViewById(obj.getIntValue("targetId")),
                  obj.getIntValue("when"),
                  obj.getString("text"),
                  obj.getIntValue("selectStart"),
                  obj.getIntValue("selectEnd"),
                  obj.getString("s"),
                  obj.getIntValue("start"),
                  obj.getIntValue("count"),
                  obj.getIntValue("after")
          );
        } else {
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
      }
      else if (type == InputUtil.EVENT_TYPE_TOUCH) {
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

        eventNode.splitSize = splitSize;  // 只是本地显示  Math.round(obj.getIntValue("splitSize")*ratio);
        eventNode.orientation = obj.getIntValue("orientation");

        int layoutType = obj.getIntValue("layoutType");
        float density = obj.getFloatValue("density");

        float ww = obj.getFloatValue("windowWidth");
        float wh = obj.getFloatValue("windowHeight");

        float sh = obj.getFloatValue("statusHeight");
        float cw = obj.getFloatValue("decorWidth");
        float ch = obj.getFloatValue("decorHeight") - sh;
        if (cw <= 100) {
          cw = ww;
        }
        if (ch <= 100) {
          ch = wh - sh;
        }

        float ratio = getScale(cw, ch, layoutType, density);
        if (ratio <= 0.1) {
          ratio = 1;
        }

        float x = obj.getFloatValue("x");
        float y = obj.getFloatValue("y");
        float sx = obj.getFloatValue("splitX");
        float sx2 = obj.getFloatValue("splitX2");
        float sy = obj.getFloatValue("splitY");
        float sy2 = obj.getFloatValue("splitY2");

        if (sx == 0 || Math.abs(sx) > cw) {
          sx = (sx < 0 ? 0 : cw)/ratio;
        }
        else if (sx > 0) {
          sx -= ww;
        }
        if (sy == 0 || Math.abs(sy) > ch) {
          sy = (sy < 0 ? 0 : ch)/ratio;
        }
        else if (sy > 0) {
          sy -= wh;
        }

        eventNode.splitX = Math.round(sx*ratio);
        eventNode.splitY = Math.round(sy*ratio);
        eventNode.splitX2 = Math.round(sx2*ratio);
        eventNode.splitY2 = Math.round(sy2*ratio);

        // float ratio = getScale(ww, ) //  1f*windowWidth/ww;  //始终以显示时宽度比例为准，不管是横屏还是竖屏   1f*Math.min(windowWidth, windowHeight)/Math.min(ww, wh);

        // 既然已经存了 上下 绝对坐标、屏幕像素 等完整信息，没必要用负值？负值保证稳定，因为 18:9 和 16:9 的分割线高度不一样
        sx = sx > 0 ? sx : ww + sx; // 转为正数
        float minSX = sx2 <= 0 ? sx : Math.min(sx, sx2);
        float maxSX = sx2 <= 0 ? sx : Math.max(sx, sx2);

        sy = sy > 0 ? sy : wh + sy; // 转为正数
        float minSY = sy2 <= 0 ? sy : Math.min(sy, sy2);
        float maxSY = sy2 <= 0 ? sy : Math.max(sy, sy2);

        float rx;
        if (x >= 0 && x <= minSX) {  //靠左
          rx = ratio*x;
        }
        else if (x < 0 || x >= maxSX) {  //靠右，例如列表项右侧标记已读、添加、删除、数量输入框等按钮
          rx = decorWidth + ratio*(x < 0 ? x : x - cw);
        }
        else {  //居中，一般是弹窗
          float mid = (maxSX + minSX)/2f;
//          rx = x < mid ? ratio*x : decorWidth*mid/cw + ratio*(x - maxSX); // 居中靠左/靠右，例如关闭按钮
          rx = decorWidth*mid/cw + ratio*(x - mid); // 居中靠左/靠右，例如关闭按钮
        }

        // 不一定这样，例如 小米 12 Pro 因为有摄像头挖孔所以横屏过来会默认不显示左侧摄像头占的宽度 // 进一步简化上面的，横向是所有都一致 rx = ratio*x + decorView.getX();

        float ry;
        if (y >= 0 && y <= minSY) {  //靠上
          ry = ratio*y;
        }
        else if (y < 0 || y >= maxSY) {  //靠下，例如底部 tab、菜单按钮、悬浮按钮等
          ry = decorHeight - statusHeight + ratio*(y < 0 ? y : y - ch); // decorHeight + ratio*(y < 0 ? y : y - ch);
        }
        else {  //居中，一般是弹窗
          float mid = (maxSY + minSY)/2f;
          ry = (decorHeight - statusHeight)*mid/ch + ratio*(y - mid); // 居中靠上/靠下，例如 取消、确定 按钮
        }

        rx += windowX + decorX;
        ry += windowY + decorY + statusHeight;

        event = MotionEvent.obtain(
          obj.getLongValue("downTime"),
          obj.getLongValue("eventTime"),
          obj.getIntValue("action"),
//                            obj.getIntValue("targetCount"),
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

      eventNode.step = i + 1;
      eventNode.id = obj.getLongValue("id");
      eventNode.flowId = obj.getLongValue("flowId");
      eventNode.disable = obj.getBooleanValue("disable");
      eventNode.type = type;
      eventNode.action = action;
      eventNode.time = obj.getLongValue("time");
      eventNode.activity = obj.getString("activity");
      eventNode.fragment = obj.getString("fragment");
      eventNode.method = obj.getString("method");
      eventNode.host = obj.getString("host");
      eventNode.url = obj.getString("url");
      eventNode.header = obj.getString("header");
      eventNode.request = obj.getString("request");
      eventNode.response = obj.getString("response");

      eventNode.windowX = obj.getIntValue("windowX");
      eventNode.windowY = obj.getIntValue("windowY");
      eventNode.decorX = obj.getFloatValue("decorX");
      eventNode.decorY = obj.getFloatValue("decorY");

      eventNode.item = event;

      eventNode.next = new Node<>(eventNode, null, null);
      if (i <= 0) {
        firstEventNode = eventNode;
      }
      if (i == step - 1) {
        currentEventNode = eventNode;
      }

      eventNode = eventNode.next;
    }

    if (currentEventNode == null) {
      currentEventNode = firstEventNode;
    }
  }

  private float getScale(float ww, float wh, int layoutType, float density) {
    if (decorWidth <= 0) {
      if (windowWidth <= 0) {
        windowWidth = screenWidth;
      }
      decorWidth = windowWidth;
    }
    if (windowHeight <= 0) {
      if (windowHeight <= 0) {
        windowHeight = screenHeight;
      }
      decorHeight = windowHeight;
    }

    float curWW = Math.min(decorWidth, decorHeight - statusHeight);
    float targetWw = Math.min(ww, wh);
    if (curWW == targetWw || layoutType == InputUtil.LAYOUT_TYPE_ABSOLUTE) {  // 同宽像素或绝对位置
      return 1.0f;
    }

    if (density > 0.1 && layoutType == InputUtil.LAYOUT_TYPE_DENSITY) {  // 默认，相对位置像素密度比
      return DENSITY/density;
    }

    if (layoutType == InputUtil.LAYOUT_TYPE_RATIO) {  // 相对位置宽度比
      return curWW/targetWw;
    }

    return 1.0f;
  }


  /* 非触屏、非按键的 其它事件，例如 Activity.onResume, HTTP Response 等
   */
  public void onEventChange(int position, int type) {
    onEventChange(position, type == InputUtil.EVENT_TYPE_TOUCH ? 0L : 500L);
  }
  public void onEventChange(int position, long delayMillis) {
    if (tagAdapter != null) {
      // tagAdapter.notifyItemRangeChanged(position - 1, position + 1);
      tagAdapter.notifyDataSetChanged();
    }

    if (position < 0 || position >= tagAdapter.getItemCount()) {
      Log.e(TAG, "onEventChange  position < 0 || position >= tagAdapter.getItemCount() >> return;");
      return;
    }

    rvControllerTag.postDelayed(new Runnable() {
      @Override
      public void run() {
        rvControllerTag.smoothScrollToPosition(position);
      }
    }, delayMillis);
  }

  public void onUIEvent(int action, Window.Callback callback, Activity activity) {
    onUIEvent(action, callback, activity, null);
  }
  public void onUIEvent(int action, Window.Callback callback, Fragment fragment) {
    onUIEvent(action, callback, null, fragment);
  }
  public void onUIEvent(int action, Window.Callback callback, Activity activity, Fragment fragment) {
    if (activity != null && activity.isFinishing() == false
            && activity.isDestroyed() == false && activity.getWindow() != null) {
      window = activity.getWindow();
    }

    if (isSplitShowing == false) {
      Log.e(TAG, "onUIEvent  isSplitShowing == false >> return null;");
      return;
    }

    if (fragment != null && "com.bumptech.glide.manager.SupportRequestManagerFragment".equals(fragment.getClass().getName())) {
      if (activity == null) {
        return;
      }
      fragment = null;
    }

    if (activity == null && fragment != null) {
      activity = fragment.getActivity();
    }

    if (isReplay) {
      Node<InputEvent> curNode = currentEventNode;
      output(null, curNode, activity);

      if (curNode == null || (curNode.type == InputUtil.EVENT_TYPE_UI && curNode.action == action
              && ((activity == null || Objects.equals(curNode.activity, activity.getClass().getName()))
//                && (Objects.equals(curNode.fragment, fragment == null ? null : fragment.getClass().getName()))
      ))) {
//        waitMap = new LinkedHashMap<>();
        InputEvent curItem = curNode == null ? null : curNode.item;

        Node<InputEvent> nextNode = curNode == null ? null : curNode.next;
        InputEvent nextItem = nextNode == null ? null : nextNode.item;

        long duration = curItem == null || nextItem == null ? (curNode == null || nextNode == null ? 0 : nextNode.time - curNode.time) : nextItem.getEventTime() - curItem.getEventTime();

        Message msg = handler.obtainMessage();
        msg.obj = nextNode;
        handler.sendMessageDelayed(msg, duration);
      }
    }
    else {
      JSONObject obj = newEvent(callback, activity, fragment);
      obj.put("type", InputUtil.EVENT_TYPE_UI);
      obj.put("action", action);
      obj.put("disable", true);  //总是导致停止后续动作，尤其是返回键相关的事件  action != InputUtil.UI_ACTION_RESUME);

      addEvent(obj);
    }
  }

//  public void onHTTPEvent(int action, String format, String url, String request, String response, Activity activity) {
//    onHTTPEvent(action, format, url, request, response, activity, null);
//  }
//  public void onHTTPEvent(int action, String format, String url, String request, String response, Fragment fragment) {
//    onHTTPEvent(action, format, url, request, response, null, fragment);
//  }
  public void onHTTPEvent(int action, String format, String method, String host, String url, String header, String request, String response, Activity activity, Fragment fragment) {
    if (isSplitShowing == false) {
      Log.e(TAG, "onHTTPEvent  isSplitShowing == false >> return null;");
      return;
    }

    if (activity == null && fragment != null) {
      activity = fragment.getActivity();
    }

    if (isReplay) {
      output(null, currentEventNode, activity);

      Node<InputEvent> curNode = lastWaitNode == null ? currentEventNode : lastWaitNode;

      if (curNode == null || /** ((activity == null || Objects.equals(curNode.activity, activity.getClass().getName()))
//                && (Objects.equals(curNode.fragment, fragment == null ? null : fragment.getClass().getName()))
              && */ StringUtil.isNotEmpty(url, true) // )
      ) {
        String key = getWaitKey(InputUtil.EVENT_TYPE_HTTP, action, method, host, url);
        List<Node<InputEvent>> list = waitMap.get(key);
        if (list != null && list.isEmpty() == false) {
          list.remove(0);
        }
        if (list == null || list.isEmpty()) {
          waitMap.remove(key);
//          step = lastWaitStep;
        }

        // if (curNode != null // && curNode.type == InputUtil.EVENT_TYPE_HTTP && curNode.action == action
//        && (url != null && url.equals(curNode.url))
        if (curNode == null || waitMap.isEmpty()) {
          lastWaitNode = null;

          InputEvent curItem = curNode == null ? null : curNode.item;

          Node<InputEvent> nextNode = curNode == null ? null : curNode.next;
          InputEvent nextItem = nextNode == null ? null : nextNode.item;

          long duration = curItem == null || nextItem == null ? (curNode == null || nextNode == null ? 0 : nextNode.time - curNode.time) : nextItem.getEventTime() - curItem.getEventTime();

          Message msg = handler.obtainMessage();
          msg.obj = curNode == null ? null : (curItem != null ? curNode : nextNode);
          handler.sendMessageDelayed(msg, duration);
        }
      }
    }
    else {
      JSONObject obj = newEvent(activity, fragment);
      obj.put("type", InputUtil.EVENT_TYPE_HTTP);
      obj.put("action", action);
      obj.put("disable", action != InputUtil.HTTP_ACTION_RESPONSE);
      obj.put("format", format);
      obj.put("method", method);
      obj.put("host", host);
      obj.put("url", url);
      obj.put("header", header);
      obj.put("request", request);
      obj.put("response", response);
      obj.put("name", "");

      addEvent(obj);
    }
  }

  private JSONArray outputList = new JSONArray();
  public JSONArray getOutputList() {
    return outputList;
  }
  public static List<Object> getOutputList(UIAutoApp app, int limit, int offset) {
    JSONArray outputList = app.getOutputList();
    int size = outputList == null ? 0 : outputList.size();
    if (size <= 0) {
      return app.isSplitShowing ? new JSONArray() : null;
    }

    if (offset >= size) {
      return app.isSplitShowing ? new JSONArray() : null;
    }

    return outputList.subList(offset, Math.min(offset + limit, size));
  }

  protected ExecutorService executorService = Executors.newSingleThreadExecutor();

  public void output(JSONObject out, Node<?> eventNode, Activity activity) {
    if (eventNode == null) {
      return;
    }

    Window window = this.window != null ? this.window : (activity == null ? null : activity.getWindow());

//    executorService.execute(new Runnable() {
//      @Override
//      public void run() { //TODO 截屏等记录下来
        Node<?> node = eventNode;

        Long inputId;
        Long toInputId;
        // if (eventNode.item == null) {  // 自动触发
        inputId = node.id;
        toInputId = node.prev == null || node.prev.disable ? null : node.prev.id;
        // }
        // else {  // 手动触发
        //   inputId = eventNode == null || (eventNode.prev) == null ? null : eventNode.prev.id;
        //   toInputId = eventNode == null ? null : eventNode.id;
        // }

        JSONObject obj = out != null ? out : new JSONObject(true);
        obj.put("inputId", inputId);
        obj.put("toInputId", toInputId);
        obj.put("orientation", node.orientation);
        if (node.disable == false) {
          obj.put("time", System.currentTimeMillis());  // TODO 如果有录屏，则不需要截屏，只需要记录时间点

          if (window != null && (node.item == null || node.action == MotionEvent.ACTION_DOWN)) {
            // TODO 同步或用协程来上传图片
            obj.put("screenshotUrl", screenshot(directory == null || directory.exists() == false ? parentDirectory : directory, window, inputId, toInputId, node.orientation));
          }
        }
        if (outputList == null) {
          outputList = new JSONArray();
        }
        synchronized (outputList) { // 居然出现 java.lang.ArrayIndexOutOfBoundsException: length=49; index=49
        	outputList.add(obj);
        }
//        }
//    });
  }

  /**屏幕截图
   * @return
   */
  public static String screenshot(File directory, Window window, Long inputId, Long toInputId, int orientation) {
    if (window == null) {
      return null;
    }

    Bitmap bitmap = null;
    FileOutputStream fos = null;
    String filePath = null;
    try {
      synchronized (window) {  //必须，且只能是 Window，用 Activity 或 decorView 都不行 解决某些界面会报错 cannot find container of decorView
        View decorView = window.getDecorView();
        decorView.setDrawingCacheEnabled(true);
        // decorView.buildDrawingCache(true);
        bitmap = decorView.getDrawingCache();
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(w <= h ? 0 : -90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, false);

        // 宽居然不是和高一样等比缩放，貌似没缩放
        // float scale = 720f/w;
        // int nw = 720;
        // int nh = Math.round(h*scale);
        // matrix.postScale(scale, scale);
        // bitmap = Bitmap.createBitmap(bitmap, 0, 0, nw, nh, matrix, false);

        decorView.destroyDrawingCache();
        decorView.setDrawingCacheEnabled(false);
      }

      //保存图片
      File file = File.createTempFile("uiauto_screenshot_inputId_" + Math.abs(inputId) + "_time_" + System.currentTimeMillis(), ".png", directory);
      filePath = file.getAbsolutePath();
      fos = new FileOutputStream(filePath);
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

      return file.getAbsolutePath(); // filePath = directory.getName() + "/" + file.getName();  // 返回相对路径
    }
    catch (Throwable e) {
      Log.e(TAG, "screenshot 截屏异常：" + e.toString());
    }
    finally {
      if (bitmap != null) {
        try {
          bitmap.recycle();
        } catch (Throwable e) {
          e.printStackTrace();
        }
      }

      if (fos != null) {
        try {
          fos.flush();
        } catch (Throwable e) {
          e.printStackTrace();
        }
        try {
          fos.close();
        } catch (Throwable e) {
          e.printStackTrace();
        }
      }
    }

    return filePath;
  }


//  public JSONObject addInputEvent(@NotNull InputEvent ie, @NotNull Window.Callback callback, @NotNull Activity activity) {
//    return addInputEvent(ie, callback, activity, null);
//  }
//  public JSONObject addInputEvent(@NotNull InputEvent ie, @NotNull Window.Callback callback, @NotNull Fragment fragment) {
//    return addInputEvent(ie, callback, null, fragment);
//  }

  public JSONObject addInputEvent(@NotNull InputEvent ie, @NotNull Window.Callback callback, Activity activity, Fragment fragment) {
    if (isSplitShowing == false || vSplitX == null || vSplitY == null || isReplay) {
      Log.e(TAG, "addInputEvent  isSplitShowing == false || vSplitX == null || vSplitY == null || isReplay >> return null;");
      return null;
    }

    if (fragment != null && "com.bumptech.glide.manager.SupportRequestManagerFragment".equals(fragment.getClass().getName())) {
      if (activity == null) {
        return null;
      }
      fragment = null;
    }

    // 直接在上面判断会导致少录制触屏事件
    if (activity == null && fragment != null) {
      activity = fragment.getActivity();
    }

    JSONObject obj = newEvent(callback, activity, fragment);

    int type = 0;
    int action = 0;

    if (ie instanceof KeyEvent) {
      KeyEvent event = (KeyEvent) ie;
      type = InputUtil.EVENT_TYPE_KEY;
      action = event.getAction();

      obj.put("type", type);

      //虽然 KeyEvent 和 MotionEvent 都有，但都不在父类 InputEvent 中 <<<<<<<<<<<<<<<<<<
      obj.put("action", action);
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
      if (ie instanceof EditTextEvent) {
        EditTextEvent mke = (EditTextEvent) ie;
//        obj.put("disable", mke.getWhen() != EditTextEvent.WHEN_ON);
        obj.put("edit", true);
        obj.put("target", mke.getTarget());
        obj.put("targetId", mke.getTargetId());
        obj.put("when", mke.getWhen());
        obj.put("s", mke.getS());
        obj.put("text", mke.getText());
        obj.put("start", mke.getStart());
        obj.put("count", mke.getCount());
        obj.put("after", mke.getAfter());
      }
    }
    else if (ie instanceof MotionEvent) {
      MotionEvent event = (MotionEvent) ie;

      type = InputUtil.EVENT_TYPE_TOUCH;
      action = event.getAction();

      obj.put("type", type);

      //虽然 KeyEvent 和 MotionEvent 都有，但都不在父类 InputEvent 中 <<<<<<<<<<<<<<<<<<
      obj.put("action", action);
      obj.put("downTime", event.getDownTime());
      obj.put("eventTime", event.getEventTime());
      obj.put("metaState", event.getMetaState());
      obj.put("source", event.getSource());
      obj.put("deviceId", event.getDeviceId());
      //虽然 KeyEvent 和 MotionEvent 都有，但都不在父类 InputEvent 中 >>>>>>>>>>>>>>>>>>

      float x = event.getX();
      float y = event.getY();
      float rx = x - windowX - decorX;
      float ry = y - windowY - decorY - statusHeight;

      if (callback instanceof Dialog) {
        Dialog dialog = (Dialog) callback;
      }

      View decorView = window.getDecorView();
      float dx = decorView.getX();
      float dy = decorView.getY();
      float dw = decorView.getWidth();
      float dh = decorView.getHeight();

      // 只在回放前一处处理逻辑
      isSplit2Showing = floatBall2 != null && floatBall2.isShowing();
//      float minX = (isSplit2Showing ? Math.min(floatBall.getX(), floatBall2.getX()) : floatBall.getX()) - splitRadius;
      float maxX = (isSplit2Showing ? Math.max(floatBall.getX(), floatBall2.getX()) : floatBall.getX()) + splitRadius;
//      float avgX = (minX + maxX)/2;
//      float minY = (isSplit2Showing ? Math.min(floatBall.getY(), floatBall2.getY()) : floatBall.getY()) - splitRadius;
      float maxY = (isSplit2Showing ? Math.max(floatBall.getY(), floatBall2.getY()) : floatBall.getY()) + splitRadius;
//      float avgY = (minY + maxY)/2;

      obj.put("x", rx < maxX ? rx : rx - dw + dx); // Math.round(x - windowX - decorX - (x < avgX ? 0 : decorWidth)));
      obj.put("y", ry < maxY ? ry : ry - dh + dy + statusHeight); // Math.round(y - windowY - decorY - (y < avgY ? 0 : decorHeight)));
      obj.put("rawX", event.getRawX());
      obj.put("rawY", event.getRawY());
      obj.put("size", event.getSize());
      obj.put("pressure", event.getPressure());
      obj.put("xPrecision", event.getXPrecision());
      obj.put("yPrecision", event.getYPrecision());
      obj.put("pointerCount", event.getPointerCount());
      obj.put("edgeFlags", event.getEdgeFlags());
    }

    return addEvent(obj, type != InputUtil.EVENT_TYPE_TOUCH || action != MotionEvent.ACTION_MOVE);
  }


  public Window getCurrentWindow() {
    if (window == null) {
      window = getCurrentActivity().getWindow();
    }
    return window;
  }

  public View getCurrentDecorView() {
    if (decorView == null) {
      decorView = getCurrentWindow().getDecorView();
    }
    return decorView;
  }

  private boolean isAlignLeft(MotionEvent event) {
    return ! isAlignRight(event);
  }
  private boolean isAlignLeft(float x) {
    return ! isAlignRight(x);
  }

  private boolean isAlignRight(MotionEvent event) {
    return event != null && isAlignRight(event.getX());
  }
  private boolean isAlignRight(float x) {
    if (floatSplitX == null) {
      return isFloatBallShowing() ? floatBall.getX() != 0 && floatBall.getY() != 0 && x > floatBall.getX() + splitSize/2 : false;
    }
    return floatSplitX.getX() != 0 && x > floatSplitX.getX();
  }
  private boolean isFloatBallShowing() {
    return floatBall != null && floatBall.isShowing();
  }

  private boolean isAlignTop(MotionEvent event) {
    return ! isAlignBottom(event);
  }
  private boolean isAlignTop(float y) {
    return ! isAlignBottom(y);
  }

  private boolean isAlignBottom(MotionEvent event) {
    return event != null && isAlignBottom(event.getY());
  }
  private boolean isAlignBottom(float y) {
    if (floatSplitY == null) {
      return isFloatBallShowing() ? floatBall.getX() != 0 && floatBall.getY() != 0 && y > floatBall.getY() + splitSize/2 : false;
    }
    return floatSplitY != null && floatSplitY.getY() != 0 && y > floatSplitY.getY();
  }

  public <V extends View> V findView(@IdRes int id) {
    return getCurrentWindow().findViewById(id);
  }

  public <V extends View> V findViewByFocus(View view, Class<V> clazz) {
    if (view == null) {
      return null;
    }

    if (view instanceof ViewGroup) {
      ViewGroup vg = (ViewGroup) view;

      for (int i = vg.getChildCount() - 1; i >= 0; i--) {
        View v = findViewByFocus(vg.getChildAt(i), clazz);
        if (v != null) {
          return (V) v;
        }
      }
    }

    return view.hasFocus() && (clazz == null || clazz.isAssignableFrom(view.getClass())) ? (V) view : null;
  }

  public <V extends View> V findViewByPoint(View view, Class<V> clazz, float x, float y, boolean onlyFocusable) {
    if (view == null || x < view.getX() || x > view.getX() + view.getWidth()
            || y < view.getY() || y > view.getY() + view.getHeight()) {
      return null;
    }

    if (view instanceof ViewGroup) {
      ViewGroup vg = (ViewGroup) view;

      for (int i = vg.getChildCount() - 1; i >= 0; i--) {
        View v = findViewByPoint(vg.getChildAt(i), clazz, x, y, onlyFocusable);
        if (v != null && (onlyFocusable == false || view.isFocusable() || view.isFocusableInTouchMode())) {
          return (V) v;
        }
      }
    }

    return (onlyFocusable == false || view.isFocusable() || view.isFocusableInTouchMode())
            && (clazz == null || clazz.isAssignableFrom(view.getClass()))
            ? (V) view : null;
  }

  int count = 0;

  public synchronized JSONObject addEvent(JSONObject event) {
    return addEvent(event, true);
  }
  public synchronized JSONObject addEvent(JSONObject event, boolean refreshUI) {
    if (event == null || isReplay) {
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

    if (refreshUI) {
      onEventChange(tagAdapter.getItemCount() - 1, event.getIntValue("type"));
    }

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

  public JSONObject newEvent(@NotNull Window.Callback callback, @NotNull Activity activity) {
    return newEvent(callback, activity, null);
  }
  public JSONObject newEvent(@NotNull Window.Callback callback, @NotNull Fragment fragment) {
    return newEvent(callback, null, fragment);
  }
  public JSONObject newEvent(@NotNull Window.Callback callback, Activity activity, Fragment fragment) {
    if (activity == null && fragment != null) {
      activity = fragment.getActivity();
    }
    return newEvent(
            activity == null ? Configuration.ORIENTATION_PORTRAIT : activity.getResources().getConfiguration().orientation,
            callback,
            activity == null ? null : activity.getClass().getName()
            , fragment == null ? null : fragment.getClass().getName()
    );
  }

  private long lastId = 0;
  public JSONObject newEvent(int orientation, @NotNull Window.Callback callback, String activity, String fragment) {
    decorX = decorView == null ? 0 : decorView.getX();
    decorY = decorView == null ? 0 : decorView.getY();
    decorWidth = decorView == null ? windowWidth : decorView.getWidth();
    decorHeight = decorView == null ? windowHeight : decorView.getHeight();

    splitX = Math.round(floatBall.getX() + splitRadius - windowWidth); // decorWidth); // - decorX
    splitY = Math.round(floatBall.getY() + splitRadius - windowHeight); // decorHeight); // - decorY

    isSplit2Showing = floatBall2 != null && floatBall2.isShowing();
    splitX2 = isSplit2Showing ? Math.round(floatBall2.getX() + splitRadius - windowWidth) : 0; // decorWidth) : 0; //  - decorX - decorWidth) : 0;
    splitY2 = isSplit2Showing ? Math.round(floatBall2.getY() + splitRadius - windowHeight) : 0; // decorHeight) : 0; // - decorY - decorHeight) : 0;

    long time = System.currentTimeMillis();
    if (lastId < time) {
      lastId = time;
    } else {
      lastId ++;
    }

    JSONObject event = new JSONObject(true);
    event.put("id", - lastId);
    event.put("flowId", flowId);
    event.put("step", count);
    event.put("time", time);
    event.put("orientation", orientation);
    event.put("splitX", splitX);
    event.put("splitY", splitY);
    event.put("splitX2", splitX2);
    event.put("splitY2", splitY2);
    event.put("windowX", windowX);
    event.put("windowY", windowY);
    event.put("windowWidth", windowWidth);
    event.put("windowHeight", windowHeight);
    event.put("statusHeight", statusHeight);
    event.put("decorX", decorX);
    event.put("decorY", decorY);
    event.put("decorWidth", decorWidth);
    event.put("decorHeight", decorHeight);
    event.put("activity", activity);
    event.put("fragment", fragment);

    if (event.get("name") == null) {
      String name = StringUtil.isEmpty(fragment, true) ? activity : fragment;
      int ind = name == null ? -1 : name.lastIndexOf(".");
      event.put("name", ind < 0 ? name : name.substring(ind + 1));
    }

    return event;
  }

  public void setEventList(JSONArray eventList) {
    setEventList(eventList, 0);
  }
  public void setEventList(JSONArray eventList, int step) {
    this.eventList = eventList == null ? new JSONArray() : eventList;
    onEventChange(step, 0L);
  }

  private File directory;
  public void prepareReplay(JSONArray eventList) {
    prepareReplay(eventList, 0, false);
  }
  public void prepareReplay(JSONArray eventList, int step, boolean start) {
    setEventList(eventList, step);
    isShowing = true;
    isReplay = true;
    this.step = step;
    allStep = eventList == null ? 0 : eventList.size();
    duration = 0;
    flowId = - System.currentTimeMillis();

    tvControllerPlay.setText("replay");
    tvControllerCount.setText(step + "/" + allStep);
    tvControllerTime.setText("0:00");

    waitMap = new LinkedHashMap<>();

    new Thread(new Runnable() {
      @Override
      public void run() {
        prepareAndSendEvent(eventList, step);

        try {
          directory = new File(parentDirectory.getAbsolutePath() + "/flowId_" + Math.abs(flowId));
          if (directory.exists() == false || directory.isDirectory() == false) {
            directory.delete();
          }
          directory.mkdir();
        } catch (Throwable e) {
          e.printStackTrace();
        }

        mainHandler.post(new Runnable() {
          @Override
          public void run() {
            showCover(true);

            if (start) {
              replay(step);
            }
          }
        });
      }
    }).start();
  }

  public void prepareRecord() {
    prepareRecord(true);
  }
  public void prepareRecord(boolean clear) {

    isShowing = true;
    isReplay = false;
	if (clear) {
	    setEventList(null);
	    step = 0;
	    allStep = 0;
	    duration = 0;
	    flowId = - System.currentTimeMillis();
        tvControllerTime.setText("0:00");
    }

    tvControllerPlay.setText("record");
    tvControllerCount.setText(step + "/" + allStep);

    showCover(true);
  }



  public void startUIAutoActivity() {
    startActivity(UIAutoActivity.createIntent(getCurrentActivity()));
  }
  @Override
  public void startActivity(Intent intent) {
    getCurrentActivity().startActivity(intent);
  }
  @Override
  public void startActivity(Intent intent, Bundle options) {
    getCurrentActivity().startActivity(intent, options);
  }



  private static class Node<E> {
    E item;
    Node<E> next;
    Node<E> prev;

    int step;

    long id;
    long flowId;
    boolean disable;
    int type;
    int action;
    long time;
    float splitX, splitX2;
    float splitY, splitY2;
    float splitSize;
    float windowX;
    float windowY;
    float decorX;
    float decorY;
    int orientation;
    String activity;
    String fragment;
    String method;
    String header;
    String host;
    String url;
    String request;
    String response;

    Node(Node<E> prev, E element, Node<E> next) {
      this.item = element;
      this.next = next;
      this.prev = prev;
    }
  }

}
