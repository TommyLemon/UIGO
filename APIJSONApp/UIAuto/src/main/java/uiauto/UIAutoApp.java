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

import static uiauto.InputUtil.GRAVITY_BOTTOM;
import static uiauto.InputUtil.GRAVITY_BOTTOM_LEFT;
import static uiauto.InputUtil.GRAVITY_BOTTOM_RIGHT;
import static uiauto.InputUtil.GRAVITY_CENTER;
import static uiauto.InputUtil.GRAVITY_LEFT;
import static uiauto.InputUtil.GRAVITY_RATIO;
import static uiauto.InputUtil.GRAVITY_RATIO_BOTTOM;
import static uiauto.InputUtil.GRAVITY_RATIO_LEFT;
import static uiauto.InputUtil.GRAVITY_RATIO_RIGHT;
import static uiauto.InputUtil.GRAVITY_RATIO_TOP;
import static uiauto.InputUtil.GRAVITY_RIGHT;
import static uiauto.InputUtil.GRAVITY_TOP;
import static uiauto.InputUtil.GRAVITY_TOP_LEFT;
import static uiauto.InputUtil.GRAVITY_TOP_RIGHT;
import static uiauto.InputUtil.X_GRAVITIES;
import static uiauto.InputUtil.Y_GRAVITIES;

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
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.IFloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.ViewStateListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
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

  private static final String BALL_GRAVITY = "BALL_GRAVITY";
  private static final String BALL_GRAVITY2 = "BALL_GRAVITY2";
  private static final String SPLIT_X = "SPLIT_X";
  private static final String SPLIT_Y = "SPLIT_Y";
  private static final String SPLIT_X2 = "SPLIT_X2";
  private static final String SPLIT_Y2 = "SPLIT_Y2";
  private static final String SPLIT_SIZE = "SPLIT_SIZE";
  private static final String SPLIT_COLOR = "SPLIT_COLOR";
  private static final String CLASS_BALL_CACHE_MAP = "CLASS_BALL_CACHE_MAP";

  private static double DENSITY = Resources.getSystem().getDisplayMetrics().density;


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

        boolean canRefreshUI = curNode == null || curNode.type != InputUtil.EVENT_TYPE_TOUCH || curNode.action != MotionEvent.ACTION_MOVE;

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
        if (curItem == null || (waitMap.isEmpty() == false && curNode.type == InputUtil.EVENT_TYPE_HTTP)) { // curNode.type == InputUtil.EVENT_TYPE_UI || curNode.type == InputUtil.EVENT_TYPE_HTTP) {
          return;
        }

        Node<InputEvent> prevNode = curNode.prev;
        if (prevNode != null) {
          if (canRefreshUI) {
            long duration = calcDuration(prevNode, curNode);
            tvControllerTime.setText(TIME_FORMAT.format(duration));
          }
        }

        if (canRefreshUI && curNode.type == InputUtil.EVENT_TYPE_TOUCH && curNode.action == MotionEvent.ACTION_DOWN) {
          isSplit2Showing = curNode.isSplit2Show;
          splitX = curNode.splitX;
          splitY = curNode.splitY;
          splitX2 = curNode.splitX2;
          splitY2 = curNode.splitY2;
          ballGravity = curNode.ballGravity;
          ballGravity2 = curNode.ballGravity2;
          gravityX = curNode.gravityX;
          gravityY = curNode.gravityY;

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
              floatBall = showSplit(floatBall, true, splitX, splitY, "floatBall", vFloatBall, floatSplitX, floatSplitY);
//            }

//            if (isSplit2Showing) {
              floatBall2 = showSplit(floatBall2, isSplit2Showing, splitX2, splitY2, "floatBall2", vFloatBall2, floatSplitX2, floatSplitY2);
//            }
//          }
        }

        // 分拆为下面两条，都放在 UI 操作后，减少延迟
        // dispatchEventToCurrentActivity(curItem, false);


        Node<InputEvent> nextNode = curNode.next;
//        long firstTime = nextNode == null ? 0 : nextNode.time;
//        while (nextNode != null && nextNode.disable) {
//          // if (nextNode.item != null) {
//          //   output(null, nextNode, activity);
//          // }
//
//          nextNode = nextNode.next;
//          step ++;
//        }
//        step = curNode == null ? step + 1 : curNode.step;
        // long lastTime = nextNode == null ? 0 : nextNode.time;

        waitMap = new LinkedHashMap<>();
//        int lastStep = step;
//        int lastWaitStep = 0;
        lastWaitNode = null;
        Node<InputEvent> lastNextNode = nextNode;

        Activity activity = getCurrentActivity();
        while (lastNextNode != null && (lastNextNode.disable || lastNextNode.item == null)
//                && (activity == null || Objects.equals(lastNextNode.activity, activity.getClass().getName()))
        ) {
          String url = lastNextNode.url;

          if (lastNextNode.disable) {
            nextNode = nextNode.next;
          }
          else if (lastNextNode.item == null && lastNextNode.disable == false
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
//            lastWaitStep = lastStep;
          }

          lastNextNode = lastNextNode.next;
          lastStep ++;
        }

        if (lastWaitNode != null) {
          nextNode = lastWaitNode;
//          step = lastWaitStep;
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

          dispatchEventToCurrentWindow(curNode, curItem, false);
          handleMessage(msg);
        }
        else {
          output(null, curNode, activity);
          dispatchEventToCurrentWindow(curNode, curItem, false);

          long duration = calcDuration(curNode, nextNode);

          if (duration <= 0) {
            handleMessage(msg);
//            dispatchEventToCurrentWindow(curItem, false);
          }
          else {
//            dispatchEventToCurrentWindow(curItem, false);
            sendMessageDelayed(msg, duration); // 相邻执行事件时间差本身就包含了  + (lastTime <= 0 || firstTime <= 0 ? 10 : lastTime - firstTime)  // 补偿 disable 项跳过的等待时间
          }
        }

      }
    }
  };

  private long calcDuration(Node<InputEvent> prevNode, Node<InputEvent> curNode) {
    // MotionEvent 是系统启动时间 326941454，UNKNOWN KeyEvent 是当前时间
    InputEvent prevItem = prevNode == null ? null : prevNode.item;
    InputEvent curItem = curNode == null ? null : curNode.item;

    long pet = prevItem == null ? 0 : prevItem.getEventTime();
    long cet = pet <= 0 || curItem == null ? 0 : curItem.getEventTime();
    long dur = cet - pet;
    long dur2 = curNode == null || prevNode == null ? 0 : curNode.time - prevNode.time;
    // dur = dur > 60*1000 ? 0 : dur;
    // dur2 = dur2 > 60*1000 ? 0 : dur2;
    long duration = dur <= 0 ? (dur2 <= 0 ? 0 : dur2) : (dur2 <= 0 || dur <= 60*1000 ? dur : Math.min(dur, dur2));

    return duration > 0 ? duration : (curItem == null ? 0 : 1);
  }

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

  double windowWidth;
  double windowHeight;
  double windowX;
  double windowY;

  int statusResourceId;
  int navigationResourceId;
  double statusHeight;
  double navigationHeight;
  boolean isNavigationShow = false;
  boolean isSeparatedStatus = false;
  double decorX;
  double decorY;
  double decorWidth;
  double decorHeight;
  double keyboardHeight;

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

  ViewGroup rlControllerGravity;
  TextView tvControllerGravityX;
  TextView tvControllerGravityY;
  TextView tvControllerGravityContainer;

  RecyclerView rvControllerTag;



  // 都取负数，表示相对于最右侧和最下方还差多少
  private int gravityX = GRAVITY_CENTER;
  private int gravityY = GRAVITY_CENTER;

  private int ballGravity = GRAVITY_BOTTOM_RIGHT;
  private int ballGravity2 = GRAVITY_TOP_LEFT;

  private double splitX, splitX2;
  private double splitY, splitY2;
  private double splitSize;
  private double splitRadius;
  private int splitColor;

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
    onUIAutoActivityCreate(activity, false);
  }
  public void onUIAutoActivityCreate(@NonNull Activity activity, boolean showToolBar) {
    onUIAutoWindowCreate(activity, activity.getWindow(), showToolBar);
  }

  public void onUIAutoWindowCreate(@NonNull Window.Callback callback, @NonNull Window window) {
    onUIAutoWindowCreate(callback, window, false);
  }
  public void onUIAutoWindowCreate(@NonNull Window.Callback callback, @NonNull Window window, boolean showToolBar) {
//    if (window == null) {
//      if (callback instanceof Activity) {
//        window = ((Activity) callback).getWindow();
//      }
//      else if (callback instanceof Dialog) {
//        window = ((Dialog) callback).getWindow();
//      }
//      else if (callback instanceof PopupWindow) {
//        window = ((uiauto.PopupWindow) callback).getWindow();
//      }
//    }

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
//        keyboardHeight = decorHeight - decorView.getHeight();
        decorHeight = decorView.getHeight();

        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);

        double wh = windowHeight;

        keyboardHeight = decorView.getHeight() - rect.bottom - (isNavigationShow ? navigationHeight : 0);

        windowWidth = rect.right - rect.left;
        windowHeight = rect.bottom - rect.top;

        if (isShowing && wh != windowHeight) {
          if (splitY < 0 || InputUtil.isBottom(ballGravity)) {
            splitY += (wh - windowHeight); // -= keyboardHeight;
            floatBall = showSplit(floatBall, isSplitShowing, splitX, splitY, "floatBall", vFloatBall, floatSplitX, floatSplitY);
          }

          if (splitY2 < 0 || InputUtil.isBottom(ballGravity2)) {
            splitY2 += (wh - windowHeight); // -= keyboardHeight;
            floatBall2 = showSplit(floatBall2, isSplitShowing && isSplit2Showing, splitX2, splitY2, "floatBall2", vFloatBall2, floatSplitX2, floatSplitY2);
          }
        }
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

    cache = cache != null ? cache : getSharedPreferences(TAG, Context.MODE_PRIVATE);

    splitSize = cache.getFloat(SPLIT_SIZE, 0);
    if (splitSize < 10) {
      splitSize = dip2px(45);
    }
    splitRadius = splitSize/2;

    BallPoint[] points = ballPositionMap.get(activity);
    if (points == null || points.length < 1) {
      points = classBallPositionMap.get(activity.getClass().getName());
    }
    BallPoint p = points == null || points.length < 1 ? null : points[0];
    if (p != null) {
      ballGravity = p.gravity;
      splitX = p.x;
      splitY = p.y;
    }
    else {
      ballGravity = cache.getInt(BALL_GRAVITY, GRAVITY_BOTTOM_RIGHT);
      splitX = cache.getFloat(SPLIT_X, 0);
      splitY = cache.getFloat(SPLIT_Y, 0);
    }

    if (splitX == 0 || Math.abs(splitX) >= windowWidth) { // decorWidth) {
      splitX = -splitSize - dip2px(30);
    }
    if (splitY == 0 || Math.abs(splitY) >= windowHeight) { // decorHeight) {
      splitY = -splitSize - dip2px(30);
    }

    if (points == null || points.length < 2) {
      points = classBallPositionMap.get(activity.getClass().getName());
    }
    BallPoint p2 = points == null || points.length < 2 ? null : points[1];
    isSplit2Showing = p2 != null;
    if (isSplit2Showing) {
      ballGravity2 = p2.gravity;
      splitX2 = p2.x;
      splitY2 = p2.y;
    }
    else {
      ballGravity2 = cache.getInt(BALL_GRAVITY2, GRAVITY_TOP_LEFT);
      splitX2 = cache.getFloat(SPLIT_X2, 0);
      splitY2 = cache.getFloat(SPLIT_Y2, 0);
    }

    if (splitX2 == 0 || Math.abs(splitX2) >= windowWidth) { // decorWidth) {
      splitX2 = splitSize + dip2px(30);
    }
    if (splitY2 == 0 || Math.abs(splitY2) >= windowHeight) { // decorHeight) {
      splitY2 = splitSize + dip2px(30);
    }

    if (showToolBar) {
      showCover(true);
    }

    rlControllerGravity.setVisibility(isSplit2Showing ? View.VISIBLE : View.GONE);

    // if (isSplitShowing) {
    floatBall = showSplit(floatBall, isSplitShowing, splitX, splitY, "floatBall", vFloatBall, floatSplitX, floatSplitY);
    // if (isSplit2Showing) {

    floatBall2 = showSplit(floatBall2, isSplitShowing && isSplit2Showing, splitX2, splitY2, "floatBall2", vFloatBall2, floatSplitX2, floatSplitY2);
    // }

    if (isSplit2Showing == false) {
//      if (floatBall2 != null) {
////        floatBall2.show();
//        floatBall2.hide(); // FIXME 无效，FloatWindow bug，切换 Activity 时 hide 无效
//      }
//      if (floatSplitX2 != null) {
////        floatSplitX2.show();
//        floatSplitX2.hide();
//      }
//      if (floatSplitY2 != null) {
////        floatSplitY2.show();
//        floatSplitY2.hide();
//      }

      // FIXME 导致副悬浮球的分割线一直不显示
      try {
        FloatWindow.destroy("floatBall2");
      } catch (Throwable e) {
        e.printStackTrace();
      }
      try {
        FloatWindow.destroy("floatSplitX2"); // 这个才生效
      } catch (Throwable e) {
        e.printStackTrace();
      }
      try {
        FloatWindow.destroy("floatSplitY2"); // 这个才生效
      } catch (Throwable e) {
        e.printStackTrace();
      }

      floatBall2 = null;
      floatSplitX2 = null;
      floatSplitY2 = null;
    }

    setSplit();

    // FIXME 导致意外显示双分割球
//    if (isSplit2Showing == false) {
//      new Handler().postDelayed(new Runnable() {
//        @Override
//        public void run() {
//          vFloatBall2.setVisibility(View.GONE);
//          vSplitX2.setVisibility(View.GONE);
//          vSplitY2.setVisibility(View.GONE);
//        }
//      }, 500);
//    }
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

    double sum = windowHeight + statusHeight + navigationHeight;
    if (sum > screenHeight) {
      windowHeight -= statusHeight;
    } else if (sum < screenHeight) {
      if (statusHeight <= 0) {
        statusHeight = screenHeight - sum;
      }
      else if (isNavigationShow == false) {
        windowHeight = screenHeight - statusHeight;
      }
      else if (navigationHeight <= 0) {
        navigationHeight = screenHeight - sum;
        isNavigationShow = true;
      }
    }

    windowX = getWindowX(activity);
    windowY = getWindowY(activity);

    if (decorView == null) {
      decorView = window.getDecorView(); // activity.findViewById(android.R.id.content);
    }

    decorX = decorView == null ? 0 : decorView.getX();
    decorY = decorView == null ? 0 : decorView.getY();
    decorWidth = decorView == null ? screenWidth : decorView.getWidth();
    decorHeight = decorView == null ? screenHeight : decorView.getHeight();
  }

  public static final String KEY_ENABLE_PROXY = "KEY_ENABLE_PROXY";
  public static final String KEY_PROXY_SERVER = "KEY_PROXY_SERVER";

  public void initUIAuto(Application app) {
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

    cache = getSharedPreferences(TAG, Context.MODE_PRIVATE);
    isProxy = cache.getBoolean(KEY_ENABLE_PROXY, false);
    proxyServer = cache.getString(KEY_PROXY_SERVER, null);

    try {
      Map<String, BallPoint[]> map = JSON.parseObject(cache.getString(CLASS_BALL_CACHE_MAP, "{}")
              , new TypeReference<Map<String, BallPoint[]>>() {}.getType());
      if (map != null && map.isEmpty() == false) {
        classBallPositionMap.putAll(map);
      }
    } catch (Throwable e) {
      e.printStackTrace();
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
        curFocusView = null;
        tvControllerGravityContainer.setText("");

        if (isShowing) {
          onUIAutoActivityCreate(activity);
        }
        setCurrentPopupWindow(popupWindowMap.get(activity), viewMap.get(activity), null, activity, null);
        onUIEvent(InputUtil.UI_ACTION_RESUME, activity, activity);
      }

      @Override
      public void onActivityPaused(Activity activity) {
        Log.v(TAG, "onActivityPaused  activity = " + activity.getClass().getName());
        // setCurrentActivity(activityList.isEmpty() ? null : activityList.get(activityList.size() - 1));
        onUIEvent(InputUtil.UI_ACTION_PAUSE, activity, activity);
        isSplit2Showing = floatBall2 != null && floatBall2.isShowing();
        curFocusView = null;

        setGravityText(tvControllerGravityX, false, gravityX);
        setGravityText(tvControllerGravityY, true, gravityY);
        BallPoint[] points = new BallPoint[] {
          new BallPoint(ballGravity, splitX, splitY)
            , isSplit2Showing == false ? null : new BallPoint(ballGravity2, splitX2, splitY2)
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

        setCurrentPopupWindow(null, null, null, activity, null);

        if (activityList == null || activityList.isEmpty()) { // Application.onTerminate 只在模拟器调用，真机不调用
          setGravityText(tvControllerGravityX, false, gravityX);
          setGravityText(tvControllerGravityY, true, gravityY);

          String s = JSON.toJSONString(classBallPositionMap);
          cache.edit()
                  .remove(SPLIT_SIZE).putFloat(SPLIT_SIZE, (float) splitSize)
                  .remove(SPLIT_COLOR).putInt(SPLIT_COLOR, splitColor)
                  .remove(BALL_GRAVITY).putInt(BALL_GRAVITY, ballGravity)
                  .remove(BALL_GRAVITY2).putInt(BALL_GRAVITY2, ballGravity2)
                  .remove(SPLIT_X).putFloat(SPLIT_X, (float) splitX)
                  .remove(SPLIT_Y).putFloat(SPLIT_Y, (float) splitY)
                  .remove(SPLIT_X2).putFloat(SPLIT_X2, (float) splitX2)
                  .remove(SPLIT_Y2).putFloat(SPLIT_Y2, (float) splitY2)
                  .remove(CLASS_BALL_CACHE_MAP).putString(CLASS_BALL_CACHE_MAP, s)
                  .commit();
//                  .apply();
        }
      }

    });


    isShowing = false;

    isSeparatedStatus = RomUtil.checkIsMiuiRom();
    statusHeight = DisplayUtil.getStatusBarHeight(getApp());
    if (statusHeight <= 0) {
      statusResourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
      if (statusResourceId > 0) {
        statusHeight = getResources().getDimensionPixelSize(statusResourceId);
      }
    }

    isNavigationShow = DisplayUtil.hasNavigationBar(getApp());
    if (isNavigationShow) {
      navigationHeight = DisplayUtil.getNavigationBarHeight(getApp());
      if (navigationHeight <= 0) {
        navigationResourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (navigationResourceId > 0) {
          navigationHeight = getResources().getDimensionPixelSize(navigationResourceId);
        }
      }
    } else {
      navigationHeight = 0;
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

    rlControllerGravity = vFloatController.findViewById(R.id.rlControllerGravity);
    rlControllerGravity.setVisibility(View.GONE);
    tvControllerGravityX = vFloatController.findViewById(R.id.tvControllerGravityX);
    tvControllerGravityY = vFloatController.findViewById(R.id.tvControllerGravityY);
    tvControllerGravityContainer = vFloatController.findViewById(R.id.tvControllerGravityContainer);

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
        rlControllerGravity.setVisibility(isSplit2Showing ? View.VISIBLE : View.GONE);
        gravityX = GRAVITY_CENTER;
        gravityY = GRAVITY_CENTER;

        // FloatWindow.destroy("floatBall2");
        // floatBall2 = null;
        // if (isSplit2Showing) {
        floatBall2 = showSplit(floatBall2, isSplit2Showing,
                floatBall.getX() + splitRadius - dip2px(0.5)
                , floatBall.getY() + splitRadius - dip2px(0.5) // - (isSeparatedStatus ? statusHeight : 0)
                , "floatBall2", vFloatBall2, floatSplitX2, floatSplitY2
        );
        // }

        setSplit();

        if (isSplit2Showing) {
          tvControllerGravityContainer.performClick();
        }
        else if (floatBall2 != null) {
          floatBall = showSplit(floatBall, isSplitShowing,
                  (floatBall.getX() + floatBall2.getX())/2 + splitRadius
                  , (floatBall.getY() + floatBall2.getY())/2 + splitRadius
                  , "floatBall", vFloatBall, floatSplitX, floatSplitY
          );
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

        Collection<List<Node<InputEvent>>> values = waitMap.values();
        for (List<Node<InputEvent>> list : values) {
          if (list == null || list.isEmpty()) {
            continue;
          }

          for (Node<InputEvent> node : list) {
            if (node == null) {
              continue;
            }

            node.disable = true;
          }
        }

        waitMap = new LinkedHashMap<>();

        Node<InputEvent> node = currentEventNode == null ? null : currentEventNode.next;
        if (node != null) {
          node.disable = true;
        }

        Message msg = handler.obtainMessage();
        msg.obj = node;
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

    setGravity(tvControllerGravityX, false);
    setGravity(tvControllerGravityY, true);

    tvControllerGravityContainer.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        curFocusView = null;
        tvControllerGravityContainer.setText("");
        return true;
      }
    });

    tvControllerGravityContainer.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ViewParent vp = curFocusView == null ? null : curFocusView.getParent();
        curFocusView = vp instanceof View ? (View) vp : null;
        if (curFocusView == null) {
          double focusX = (isSplit2Showing ? (floatBall.getX() + floatBall2.getX())/2 : floatBall.getX()) + splitRadius;
          double focusY = (isSplit2Showing ? (floatBall.getY() + floatBall2.getY())/2 : floatBall.getY()) + splitRadius + (isSeparatedStatus ? statusHeight : 0);
          curFocusView = findViewByPoint(getCurrentDecorView(), null, focusX, focusY, false);
        }
        if (curFocusView == null) {
          tvControllerGravityContainer.setText("");
          return;
        }

        String in = getResIdName(curFocusView);
        tvControllerGravityContainer.setText("[]: " + curFocusView.getClass().getSimpleName() + "/" + (StringUtil.isEmpty(in, true) ? curFocusView.getId() : in));

        int[] loc = new int[2];
        curFocusView.getLocationOnScreen(loc);

        splitX2 = loc[0] + curFocusView.getPaddingLeft();
        splitY2 = loc[1] + curFocusView.getPaddingTop() - statusHeight; //(isSeparatedStatus ? statusHeight : 0);

        splitX = loc[0] + curFocusView.getWidth() - curFocusView.getPaddingRight(); // - windowWidth;
        splitY = loc[1] + curFocusView.getHeight() - curFocusView.getPaddingBottom() - statusHeight; // (isSeparatedStatus ? statusHeight : 0); // - windowHeight;

        floatBall.updateX((int) Math.round(splitX - splitRadius));
        floatBall.updateY((int) Math.round(splitY - splitRadius));

        floatBall2.updateX((int) Math.round(splitX2 - splitRadius));
        floatBall2.updateY((int) Math.round(splitY2 - splitRadius));

//        floatBall = showSplit(true, splitX, splitY, "floatBall", vFloatBall, floatSplitX, floatSplitY);
//        floatBall2 = showSplit(true, splitX2, splitY2, "floatBall2", vFloatBall2, floatSplitX2, floatSplitY2);

        onUpdateBallPosition(floatBall, vFloatBall, floatSplitX, floatSplitY, false, floatBall.getX(), floatBall.getY());
        onUpdateBallPosition(floatBall2, vFloatBall2, floatSplitX2, floatSplitY2, true, floatBall2.getX(), floatBall2.getY());
      }
    });
  }

  private void setSplit() {
    setGravityImageAndText(vFloatBall, ballGravity2, tvControllerGravityX, false, gravityX);
    setGravityImageAndText(vFloatBall, ballGravity2, tvControllerGravityY, true, gravityY);
    setGravityImageAndText(vFloatBall, ballGravity, tvControllerGravityX, false, gravityX);
    setGravityImageAndText(vFloatBall, ballGravity, tvControllerGravityY, true, gravityY);
  }

  private View curFocusView;

  private void setGravity(TextView tv, boolean isY) {
    setGravityText(tv, isY, isY ? gravityY : gravityX);
    tv.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (isY) {
          gravityY = (gravityY + 1)%Y_GRAVITIES.length;
          setGravityText(tv, isY, gravityY);
        } else {
          gravityX = (gravityX + 1)%X_GRAVITIES.length;
          setGravityText(tv, isY, gravityX);
        }
      }
    });
  }

  private void setGravityText(TextView tv, boolean isY, int gravity) {
    if (isY) {
      double sy = floatBall == null ? splitY : floatBall.getY() + splitRadius - windowHeight;
      boolean isBottom = InputUtil.isBottom(ballGravity);
      Double ratioY = null;
      if (InputUtil.isRatio(ballGravity)) {
        ratioY = sy/windowHeight;
      }
      else if (sy >= 0 && isBottom) {
        sy -= windowHeight;
      }
      else if (sy < 0 && InputUtil.isTop(ballGravity)) {
        sy += windowHeight;
      }

      double sy2 = floatBall2 == null ? splitY2 : floatBall2.getY() + splitRadius - windowHeight;
      boolean isBottom2 = InputUtil.isBottom(ballGravity2);
      Double ratioY2 = null;
      if (InputUtil.isRatio(ballGravity2)) {
        ratioY2 = sy2/windowHeight;
      }
      else if (sy2 >= 0 && isBottom2) {
        sy2 -= windowHeight;
      }
      else if (sy2 < 0 && InputUtil.isTop(ballGravity2)) {
        sy2 += windowHeight;
      }

      splitY = sy;
      splitY2 = sy2;

      long rsy = Math.round(sy);
      long rsy2 = Math.round(sy2);

      tv.setText("Y: " + (ratioY2 == null ? (isBottom2 && rsy2 == 0 ? "-" : "") + rsy2 : DECIMAL_FORMAT.format(ratioY2*100) + "%")
              + "/" + (ratioY == null ? (isBottom && rsy == 0 ? "-" : "") + rsy : DECIMAL_FORMAT.format(ratioY*100) + "%")
              + ", " + (gravity == GRAVITY_RATIO ? "ratio" : (gravity == GRAVITY_TOP ? "top" : (gravity == GRAVITY_BOTTOM ? "bottom" : "center"))));
    } else {
      double sx = floatBall == null ? splitX : floatBall.getX() + splitRadius;
      boolean isRight = InputUtil.isRight(ballGravity);
      Double ratioX = null;
      if (InputUtil.isRatio(ballGravity)) {
        ratioX = sx/windowWidth;
      }
      else if (sx >= 0 && isRight) {
        sx -= windowWidth;
      }
      else if (sx < 0 && InputUtil.isLeft(ballGravity)) {
        sx += windowWidth;
      }

      double sx2 = floatBall2 == null ? splitX2 : floatBall2.getX() + splitRadius;
      boolean isRight2 = InputUtil.isRight(ballGravity2);
      Double ratioX2 = null;
      if (InputUtil.isRatio(ballGravity2)) {
        ratioX2 = sx2/windowWidth;
      }
      else if (sx2 >= 0 && isRight2) {
        sx2 -= windowWidth;
      }
      else if (sx2 < 0 && InputUtil.isLeft(ballGravity2)) {
        sx2 += windowWidth;
      }

      splitX = sx;
      splitX2 = sx2;

      long rsx = Math.round(sx);
      long rsx2 = Math.round(sx2);

      tv.setText("X: " + (ratioX2 == null ? (isRight2 && rsx2 == 0 ? "-" : "") + rsx2 : DECIMAL_FORMAT.format(ratioX2*100) + "%")
              + "/" +  (ratioX == null ? (isRight && rsx == 0 ? "-" : "") + rsx : DECIMAL_FORMAT.format(ratioX*100) + "%")
              + ", " + (gravity == GRAVITY_RATIO ? "ratio" : (gravity == GRAVITY_LEFT ? "left" : (gravity == GRAVITY_RIGHT ? "right" : "center"))));
    }
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
    floatBall = showSplit(floatBall, isSplitShowing, splitX, splitY, "floatBall", vFloatBall, floatSplitX, floatSplitY);
    floatBall2 = showSplit(floatBall2, isSplitShowing && isSplit2Showing, splitX2, splitY2, "floatBall2", vFloatBall2, floatSplitX2, floatSplitY2);

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

    this.popupWindow = null;
    this.view = null;
  }


  public void onUIAutoActivityDestroy(Window.Callback callback, Activity activity) {
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


  private Map<Object, PopupWindow> popupWindowMap = new LinkedHashMap<>();
  private PopupWindow popupWindow;
  public void setCurrentPopupWindow(PopupWindow pw, View v, Window.Callback callback, @NotNull Activity activity, Fragment fragment) {
    this.popupWindow = pw;

//    if (activity == null) {
//      activity = fragment == null ? getCurrentActivity() : fragment.getActivity();
//    }

    if (pw == null) {
      popupWindowMap.remove(activity);
    }
    else {
      popupWindowMap.put(activity, pw);

      pw.setTouchInterceptor(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
          addInputEvent(event, callback, activity, fragment);
//          pw.dismiss();

//          if (event.getAction() == MotionEvent.ACTION_UP) {
//            setCurrentPopupWindow(null, null, callback, activity, fragment);
//          }
          return false;
        }
      });
    }

    setCurrentView(v, callback, activity, fragment);
  }


  private Map<Object, View> viewMap = new LinkedHashMap<>();
  private View view;
  public void setCurrentView(View v, Window.Callback callback, Activity activity, Fragment fragment) {
    this.view = v;

    if (v == null) {
      viewMap.remove(activity);
    }
    else {
      if (v instanceof WebView == false) {
        viewMap.put(activity, v);
      }

      v.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
          if (popupWindow != null) {
            return false;
          }

          addInputEvent(event, callback, activity, fragment);

//          if (event.getAction() == MotionEvent.ACTION_UP) {
//            setCurrentPopupWindow(null, null, callback, activity, fragment);
//          }
          return false;
        }
      });
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
  private final Map<Object, BallPoint[]> ballPositionMap = new HashMap<>();
  private final Map<String, BallPoint[]> classBallPositionMap = new HashMap<>();
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
            floatBall = showSplit(floatBall, isSplitShowing, splitX, splitY, "floatBall", vFloatBall, floatSplitX, floatSplitY);
            // if (isSplit2Showing) {
              floatBall2 = showSplit(floatBall2, isSplitShowing && isSplit2Showing, splitX2, splitY2, "floatBall2", vFloatBall2, floatSplitX2, floatSplitY);

        }
      }
    }, 1000);
  }

  private void showCoverAndSplit(boolean showCover, boolean showSplit) {
    showCover(showCover);
    floatBall = showSplit(floatBall, showSplit, splitX, splitY, "floatBall", vFloatBall, floatSplitX, floatSplitY);
    floatBall2 = showSplit(floatBall2, showSplit && isSplit2Showing, splitX2, splitY2, "floatBall2", vFloatBall2, floatSplitX2, floatSplitY2);
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



    floatSplitX = getSplitX(false);
    // floatSplitX.show();

    floatSplitY = getSplitY(false);
    // floatSplitY.show();

    floatSplitX2 = getSplitX(true);
    // floatSplitX2.show();

    floatSplitY2 = getSplitY(true);
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
    if (floatSplitX2 != null) {
      floatSplitX2.hide();
    }
    floatSplitY.hide();
    if (floatSplitY2 != null) {
      floatSplitY2.hide();
    }
  }


  private IFloatWindow getSplitX(boolean isVice) {
    return getSplitX(isVice ? "floatSplitX2" : "floatSplitX", isVice ? vSplitX2 : vSplitX);
  }
  private IFloatWindow getSplitX(String name, View vSplitX) {
    IFloatWindow floatSplitX = FloatWindow.get(name);
    if (floatSplitX == null) {
      FloatWindow
              .with(getApplicationContext())
              .setTag(name)
              .setView(vSplitX)
              .setHeight(ViewGroup.LayoutParams.MATCH_PARENT)                    //设置控件宽高
              .setMoveType(MoveType.inactive)
              .setDesktopShow(true) //必须为 true，否则切换 Activity 就会自动隐藏                        //桌面显示
              .build();

      floatSplitX = FloatWindow.get(name);
    }
    return floatSplitX;
  }

  private IFloatWindow getSplitY(boolean isVice) {
    return getSplitY(isVice ? "floatSplitY2" : "floatSplitY", isVice ? vSplitY2 : vSplitY);
  }
  private IFloatWindow getSplitY(String name, View vSplitY) {
    IFloatWindow floatSplitY = FloatWindow.get(name);
    if (floatSplitY == null) {
      FloatWindow
              .with(getApplicationContext())
              .setTag(name)
              .setView(vSplitY)
              .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)                    //设置控件宽高
              .setMoveType(MoveType.inactive)
              .setDesktopShow(true) //必须为 true，否则切换 Activity 就会自动隐藏                        //桌面显示
              .build();

      floatSplitY = FloatWindow.get(name);
    }
    return floatSplitY;
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

//  private MotionEvent lastDownEvent, lastUpEvent;
  private float lastBallDownX, lastBallDownY, lastBallUpX, lastBallUpY;

  private boolean isSplitShowing, isSplit2Showing;
  private IFloatWindow showSplit(IFloatWindow floatBall_, boolean show, double splitX, double splitY, String ballName, FloatBallView vFloatBall, IFloatWindow floatSplitX_, IFloatWindow floatSplitY_) {
    // vSplitX.setVisibility(View.GONE);
    // vSplitY.setVisibility(View.GONE);
    // vSplitX2.setVisibility(View.GONE);
    // vSplitY2.setVisibility(View.GONE);

    // floatCover.hide();

    // showFloatView(true, "splitX", vSplitX_, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, splitX, 0, MoveType.inactive);
    // showFloatView(true, "splitY", vSplitY_, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0, splitY, MoveType.inactive);
    // showFloatView(true, "splitX2", vSplitX2, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, splitX2, 0, MoveType.inactive);
    // showFloatView(true, "splitY2", vSplitY2, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, splitY2, MoveType.inactive);

    boolean isBall2 = "floatBall2".equals(ballName);
    setGravityImage(vFloatBall, isBall2 ? ballGravity2 : ballGravity);

    int x = (int) Math.round(splitX - splitRadius + (splitX > 0 ? 0 : windowWidth)); // 只有贴边才会自动处理 decorWidth); // 已被 FloatWindow 处理 windowX + decorX
    int y = (int) Math.round(splitY - splitRadius + (splitY > 0 ? 0 : windowHeight)); // - (isSeparatedStatus ? 0 : statusHeight))); // + navigationHeight)); // 只有贴边才会自动处理  decorHeight); // 已被 FloatWindow 处理 windowY + decorY
    if (floatSplitX_ != null) {
      try {
        floatSplitX_.updateX((int) Math.round(x + splitRadius - dip2px(0.5f)));
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
    if (floatSplitY_ != null) {
      try {
        floatSplitY_.updateY((int) Math.round(y + splitRadius - dip2px(0.5f)));
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
    vSplitX.setVisibility(View.GONE);
    vSplitY.setVisibility(View.GONE);
    vSplitX2.setVisibility(View.GONE);
    vSplitY2.setVisibility(View.GONE);
    if (floatBall2 != null) {
//      floatBall2.show();
      floatBall2.hide();
    }
    IFloatWindow ball = floatBall_ != null ? floatBall_ : FloatWindow.get(ballName);
    if (show == false) {
      if (ball != null) {
//        ball.show();
        ball.hide();
      }
      return ball;
    }

    if (ball == null) {
      vFloatBall.setExtraOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
          if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            lastDownEvent = event;
            lastBallDownX = event.getX();
            lastBallDownY = event.getY();

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

//            floatSplitX2 = getSplitX(true);
//            floatSplitX2.hide();
//            floatSplitX2.show();
//            floatSplitY2 = getSplitY(true);
//            floatSplitY2.hide();
//            floatSplitY2.show();
          }
          else if (event.getAction() == MotionEvent.ACTION_UP) {
//            lastUpEvent = event;
            lastBallUpX = event.getX();
            lastBallUpY = event.getY();

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
//          lastDownEvent = null;
//          lastUpEvent = null;
          lastBallUpX = lastBallDownX = lastBallUpY = lastBallDownY = 0;

          if (isSplit2Showing == false) {
            v.performLongClick();
            return;
          }

          int[] gravities = InputUtil.BALL_GRAVITIES;
          if (isBall2) {
            ballGravity2 = (ballGravity2 + 1)%gravities.length;
            setGravityImageAndText(vFloatBall, ballGravity2, tvControllerGravityX, false, gravityX);
            setGravityImageAndText(vFloatBall, ballGravity2, tvControllerGravityY, true, gravityY);
          } else {
            ballGravity = (ballGravity + 1)%gravities.length;
            setGravityImageAndText(vFloatBall, ballGravity, tvControllerGravityX, false, gravityX);
            setGravityImageAndText(vFloatBall, ballGravity, tvControllerGravityY, true, gravityY);
          }
        }
      });
      vFloatBall.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
          double dx = lastBallUpX - lastBallDownX; // lastUpEvent == null ? 0 : lastUpEvent.getX() - (lastDownEvent == null ? lastUpEvent.getRawX() : lastDownEvent.getX());
          double dy = lastBallUpY - lastBallDownY; // lastUpEvent == null ? 0 : lastUpEvent.getY() - (lastDownEvent == null ? lastUpEvent.getRawY() : lastDownEvent.getY());
//          lastDownEvent = null;
//          lastUpEvent = null;

          lastBallUpX = lastBallDownX = lastBallUpY = lastBallDownY = 0;

          if (Math.pow(dx, 2) + Math.pow(dy, 2) > Math.pow(dip2px(8), 2)) {
            return true;
          }

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

          return true;
        }
      });

      int size = (int) Math.round(splitSize);

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
            IFloatWindow fb = floatBall_ != null ? floatBall_ : FloatWindow.get(ballName);
            onUpdateBallPosition(fb, vFloatBall, floatSplitX_, floatSplitY_, isBall2, x, y);

//            IFloatWindow floatBall = FloatWindow.get(ballName);
//            if (x != floatBall.getX() || y != floatBall.getY()) {
            curFocusView = null;
            tvControllerGravityContainer.setText("");
//            }

          }

          @Override
          public void onShow() {
            IFloatWindow fb = floatBall_ != null ? floatBall_ : FloatWindow.get(ballName);
            onPositionUpdate(fb == null ? x : fb.getX(), fb == null ? y : fb.getY());
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
    tvControllerX.setText(splitX + "\n" + DECIMAL_FORMAT.format(splitX/windowWidth) + "%");
    tvControllerY.setText(splitY + "\n" + DECIMAL_FORMAT.format(splitY/windowHeight) + "%");

    if (floatSplitX_ != null && floatSplitX_.isShowing()) {
      floatSplitX_.updateX((int) (x + Math.round(splitRadius) - dip2px(0.5f)));
      floatSplitX_.hide();
    }
    if (floatSplitY_ != null && floatSplitY_.isShowing()) {
      floatSplitY_.updateY((int) (y + Math.round(splitRadius) - dip2px(0.5f)));
      floatSplitY_.hide();
    }

    return ball;
  }

  private void onUpdateBallPosition(IFloatWindow floatBall, FloatBallView vFloatBall
          , IFloatWindow floatSplitX_, IFloatWindow floatSplitY_, boolean isBall2, int x, int y) {
    double splitX = x + splitRadius;
    double splitY =y + splitRadius;

    boolean out = false;
    if (splitX > windowWidth) {
      splitX = windowWidth;
      out = true;
    }
    else if (splitX < 0) {
      splitX = 0;
      out = true;
    }

    if (splitY > windowHeight) {
      splitY = windowHeight;
      out = true;
    }
    else if (splitY < 0) {
      splitY = 0;
      out = true;
    }

    if (out) {
      if (floatBall != null) {
        floatBall.updateX((int) Math.round(splitX - splitRadius));
        floatBall.updateY((int) Math.round(splitY - splitRadius));
      }
      return;
    }

    if (floatSplitX_ != null) { //  && floatSplitX_.isShowing()) {
      try {
        floatSplitX_.updateX((int) Math.round(splitX - dip2px(0.5f)));
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
    if (floatSplitY_ != null) { //   && floatSplitY_.isShowing()) {
      try {
        floatSplitY_.updateY((int) Math.round(splitY - dip2px(0.5f)));
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }

    double xr = 100f*splitX/windowWidth;
//            double yr = 100f*(splitY + (isSeparatedStatus ? 0 : statusHeight))/windowHeight;
    double yr = 100f*splitY/windowHeight;

    tvControllerX.setText(DECIMAL_FORMAT.format(xr) + "%" + "\n" + DECIMAL_FORMAT.format(yr) + "%");
    tvControllerY.setText(Math.round(splitX) + "\n" + Math.round(splitY));

    int bg = isBall2 ? ballGravity2 : ballGravity;
    if (xr <= 50) {
      if (bg == GRAVITY_TOP_RIGHT) {
        bg = GRAVITY_TOP_LEFT;
      } else if (bg == GRAVITY_BOTTOM_RIGHT) {
        bg = GRAVITY_BOTTOM_LEFT;
      } else if (bg == GRAVITY_RATIO_RIGHT) {
        bg = GRAVITY_RATIO_LEFT;
      }
    } else {
      if (bg == GRAVITY_TOP_LEFT) {
        bg = GRAVITY_TOP_RIGHT;
      } else if (bg == GRAVITY_BOTTOM_LEFT) {
        bg = GRAVITY_BOTTOM_RIGHT;
      } else if (bg == GRAVITY_RATIO_LEFT) {
        bg = GRAVITY_RATIO_RIGHT;
      }
    }

    if (yr <= 50) {
      if (bg == GRAVITY_BOTTOM_LEFT) {
        bg = GRAVITY_TOP_LEFT;
      } else if (bg == GRAVITY_BOTTOM_RIGHT) {
        bg = GRAVITY_TOP_RIGHT;
      } else if (bg == GRAVITY_RATIO_BOTTOM) {
        bg = GRAVITY_RATIO_TOP;
      }
    } else {
      if (bg == GRAVITY_TOP_LEFT) {
        bg = GRAVITY_BOTTOM_LEFT;
      } else if (bg == GRAVITY_TOP_RIGHT) {
        bg = GRAVITY_BOTTOM_RIGHT;
      } else if (bg == GRAVITY_RATIO_TOP) {
        bg = GRAVITY_RATIO_BOTTOM;
      }
    }

    if (isBall2) {
      ballGravity2 = bg;
    } else {
      ballGravity = bg;
    }

    setGravityImageAndText(vFloatBall, bg, tvControllerGravityX, false, gravityX);
    setGravityImageAndText(vFloatBall, bg, tvControllerGravityY, true, gravityY);
  }

  private void setGravityImageAndText(FloatBallView vFloatBall, int ballGravity, TextView tv, boolean isY, int gravity) {
    setGravityImage(vFloatBall, ballGravity);
    setGravityText(tv, isY, gravity);
  }

  private void setGravityImage(FloatBallView vFloatBall, int ballGravity) {
    vFloatBall.setImageResource(InputUtil.getBallGravityImageResource(isSplit2Showing ? ballGravity : -1));
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


  public boolean dispatchEventToCurrentWindow(Node<InputEvent> node, InputEvent ie, boolean record) {
    if (ie == null) {
      ie = node == null ? null : node.item;
    }
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

    if (callback != null || view != null) {
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
          MotionEvent viewEvent = event;
          int gy = node == null ? -1 : node.gravityY;
          double y = node == null ? 0 : node.y;
          if (gy < 0 && y < 0) {
            gy = GRAVITY_BOTTOM;
          }

          JSONObject obj = node == null ? null : node.obj;
          boolean isKeyboardChange = keyboardHeight > 0 && gy >= 0 && gy != GRAVITY_TOP;
          boolean isPopupWindow = view != null && popupWindow != null && popupWindow.isShowing();

          if (obj == null || obj.isEmpty()) {
            // FIXME Dialog/PopupWindow 内输入?
            if (isKeyboardChange) { // 重新算能兼容 && (callback instanceof Dialog == false)) {
              // FIXME 不完全一样，要考虑悬浮球位置
              double ny = gy == GRAVITY_BOTTOM ? (y <= 0 ? y : y - node.windowHeight)*node.ratio + windowHeight // 重新计算比这样更可靠  - keyboardHeight
                      : (gy == GRAVITY_RATIO ? windowHeight*(y >= 0 ? y : node.windowHeight + y)/node.windowHeight
                      : (gy == GRAVITY_CENTER ? windowHeight/2 : 0));
              if (ny > 0) { // 基本不会出现键盘把目标位置顶出屏幕的情况
                event = MotionEvent.obtain(event);
                event.offsetLocation(0f, (float) (ny + (isSeparatedStatus ? statusHeight : 0) - event.getY()));
              }
            }
          }
          else if (isKeyboardChange) {
            node = obj2EventNode(obj, node, node.step);
            if (node.item instanceof MotionEvent) {
              event = (MotionEvent) node.item;
            }
          }

          if (isPopupWindow) {
            viewEvent = MotionEvent.obtain(event);
            viewEvent.offsetLocation(0f, - (float) statusHeight);
          }

          if ((view == null || (view.dispatchTouchEvent(viewEvent) == false)) && callback != null) {
            callback.dispatchTouchEvent(event);
          }
        } catch (Throwable e) {  // java.lang.IllegalArgumentException: tagerIndex out of range
          e.printStackTrace();
        }
      }
      else if (ie instanceof KeyEvent) {
        if (ie instanceof EditTextEvent) {
          EditTextEvent ete = (EditTextEvent) ie;
          if (ete.getWhen() == EditTextEvent.WHEN_ON) {
            EditText target = ete.getTarget();
            if (target != null) {
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

            String targetWebId = ete.getTargetWebId();
            if (webView != null && (StringUtil.isNotEmpty(targetWebId, true) || (ete.getX() != null && ete.getY() != null))) {
              String script = "" + // ""(function() {\n" +
                      "  var map = document.uiautoEditTextMap || {};\n" +
                      "  var targetWebId = '" + targetWebId + "';\n" +
                      "  var et = map[targetWebId] || document.getElementById(targetWebId);\n" +
                      "  var ae = document.activeElement;\n" +
                      "  if (et == null /* && (ae instanceof HTMLInputElement || ae instanceof HTMLTextAreaElement) */ && ['input', 'textarea'].indexOf(ae.localName) >= 0 && ['INPUT', 'TEXTAREA'].indexOf(ae.tagName) >= 0) {\n" +
                      "    et = ae;\n" +
                      "  }\n" +
                      "  var x = " + ete.getX() + ";\n" +
                      "  var y = " + ete.getY() + ";\n" +
                      "  if (et == null) {\n" +
                      "    et = map[x + ',' + y];\n" +
                      "  }\n" +
                      "  if (et == null) {\n" +
                      "    function findEditText(x, y) {\n" +
                      "\n" +
                      "      var inputs = document.getElementsByTagName('input');\n" +
                      "      var textareas = document.getElementsByTagName('textarea');\n" +
                      "\n" +
                      "      function getZIndex(e) {\n" +
                      "        if (e instanceof HTMLElement == false) {\n" +
                      "          return null;\n" +
                      "        }\n" +
                      "\n" +
                      "        var style = document.defaultView.getComputedStyle(e);\n" +
                      "        var z = style == null ? null : style.getPropertyValue('z-index');\n" +
                      "        return z == null || Number.isNaN(z) ? getZIndex(e.parentNode) : z;\n" +
                      "      }\n" +
                      "\n" +
                      "      function findItem(editTexts, target) {\n" +
                      "        if (editTexts == null || editTexts.length <= 0) {\n" +
                      "          return target;\n" +
                      "        }\n" +
                      "\n" +
                      "        var tz = getZIndex(target);\n" +
                      "        for (var i = 0; i < editTexts.length; i ++) {\n" +
                      "          var et = editTexts.item(i);\n" +
                      "\n" +
                      "          var rect = et == null || et.disabled /* || (ae instanceof HTMLInputElement == false && ae instanceof HTMLTextAreaElement == false) */ || ['INPUT', 'TEXTAREA'].indexOf(et.tagName) < 0 ? null : et.getBoundingClientRect();\n" +
                      "          var left = rect == null ? null : rect.left;\n" +
                      "          var right = left == null ? null : rect.right;\n" +
                      "          var top = right == null ? null : rect.top;\n" +
                      "          var bottom = top == null ? null : rect.bottom;\n" +
                      "          if (bottom == null || x < left || x > right || y < top || y > bottom) {\n" +
                      "            continue;\n" +
                      "          }\n" +
                      "\n" +
                      "          if (target == null) {\n" +
                      "            target = et;\n" +
                      "            continue;\n" +
                      "          }\n" +
                      "\n" +
                      "          var z = getZIndex(et);\n" +
                      "          if (tz == null || (z != null && z > tz)) {\n" +
                      "            target = et;\n" +
                      "            tz = z;\n" +
                      "          }\n" +
                      "        }\n" +
                      "\n" +
                      "        return target;\n" +
                      "      }\n" +
                      "\n" +
//                      "      var target = findItem(inputs, null, true);\n" +
//                      "      if (target instanceof HTMLElement) {\n" +
//                      "        return target;\n" +
//                      "      }\n" +
//                      "\n" +
//                      "      var target2 = findItem(textareas, null, true);\n" +
//                      "      if (target2 instanceof HTMLElement) {\n" +
//                      "        return target2;\n" +
//                      "      }\n" +
                      "\n" +
                      "      var target = findItem(inputs, null);\n" +
                      "      var target2 = findItem(textareas, target);\n" +
                      "\n" +
                      "      console.log(\"findViewByPoint(\" + x + \", \" + y + \") = \" + (target2 == null ? null : target2.id));\n" +
//                      "      alert(\"findViewByPoint(\" + x + \", \" + y + \") = \" + (target2 == null ? null : target2.id));\n" +
                      "      return target2;\n" +
                      "    }\n" +
                      "    \n" +
                      "    et = findEditText(x, y);\n" +
                      "    map[x + ',' + y] = et;\n" +
                      "  }\n" +
                      "  \n" +
                      "  if (et == null) {\n" +
                      "    et = inputs == null || inputs.length <= 0 ? null : inputs.item(0);\n" +
                      "  }\n" +
                      "  if (et == null) {\n" +
                      "    et = textareas == null || textareas.length <= 0 ? null : textareas.item(0);\n" +
                      "  }\n" +
                      "  try {\n" +
                      "    et.value = '" + StringUtil.getString(ete.getText()).replaceAll("'", "\\'") + "';\n" +
                      "    et.focus();\n" +
                      "  } catch (e) {\n" +
                      "    console.log(e);\n" +
                      "  }\n" +
//                      "})();\n" +
//                      "var ret = 'document.uiautoEditTextMap = ' + JSON.stringify(document.uiautoEditTextMap);\n" +
                      "  et";
              webView.evaluateJavascript(script , new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                  Log.d(TAG, "dispatchEventToCurrentWindow webView.evaluateJavascript >> onReceiveValue value = " + value);
                }
              });
            }
          }
        }
        else if ((view == null || view.dispatchKeyEvent((KeyEvent) ie) == false) && callback != null) {
          callback.dispatchKeyEvent((KeyEvent) ie);
        }
      }
    }

    if (record) {
      addInputEvent(ie, callback, activity, fragment);
    }

    return callback != null || view != null;
  }



  /**
   * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
   */
  public int dip2px(double dpValue) {
    final double scale = DENSITY;
    return (int) Math.round(dpValue*scale);  // + 0.5f 是为了让结果四舍五入
  }

  /**
   * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
   */
  public int px2dip(double pxValue) {
    final double scale = DENSITY;
    return (int) Math.round(pxValue/scale);  // + 0.5f 是为了让结果四舍五入
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
      tvControllerPlay.setText(R.string.replay);
      showCoverAndSplit(true, false);
    }
    else {
      tvControllerPlay.setText(R.string.replaying);
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
      eventNode = obj2EventNode(obj, eventNode, i + 1);
      if (eventNode == null) { // || obj.getBooleanValue("disable")) {
        continue;
      }

      // if (i <= 0) {
      //   firstEventNode = new Node<>(null, null, null);
      //   eventNode = firstEventNode;
      // }

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

  public Node<InputEvent> obj2EventNode(JSONObject obj, Node<InputEvent> eventNode, int step) {
    if (eventNode == null) {
      eventNode = new Node<>(null, null, null);
    }

    int type = obj.getIntValue("type");
    int action = obj.getIntValue("action");

    InputEvent event;
    if (type == InputUtil.EVENT_TYPE_KEY) {
      if (obj.getBooleanValue("edit")) {
        EditTextEvent ete = new EditTextEvent(
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
        ete.setTargetWebId(obj.getString("targetWebId"));
        ete.setX(obj.getInteger("x"));
        ete.setY(obj.getInteger("y"));
        event = ete;
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
       double x, double y, double pressure, double size, int metaState,
       double xPrecision, double yPrecision, int deviceId, int edgeFlags, int source,
       int displayId)
       */

      //居然编译报错，和
      // static public MotionEvent obtain(long downTime, long eventTime,
      //    int action, int tagerCount, PointerProperties[] tagerProperties,
      //    PointerCoords[] tagerCoords, int metaState, int buttonState,
      //    double xPrecision, double yPrecision, int deviceId,
      //    int edgeFlags, int source, int displayId, int flags)
      //冲突，实际上类型没传错

      //                    event = MotionEvent.obtain(obj.getLongValue("downTime"),  obj.getLongValue("eventTime"),  obj.getIntValue("action"),
      //                    obj.getDoubleValue("x"),  obj.getDoubleValue("y"),  obj.getDoubleValue("pressure"),  obj.getDoubleValue("size"),  obj.getIntValue("metaState"),
      //                    obj.getDoubleValue("xPrecision"),  obj.getDoubleValue("yPrecision"),  obj.getIntValue("deviceId"),  obj.getIntValue("edgeFlags"),  obj.getIntValue("source"),
      //                    obj.getIntValue("displayId"));

      eventNode.splitSize = splitSize;  // 只是本地显示  Math.round(obj.getIntValue("splitSize")*ratio);
      eventNode.orientation = obj.getIntValue("orientation");

      int layoutType = obj.getIntValue("layoutType");
      double density = obj.getDoubleValue("density");

      double ww = obj.getDoubleValue("windowWidth");
      double wh = obj.getDoubleValue("windowHeight");

      double sh = obj.getDoubleValue("statusHeight");
      double kh = obj.getDoubleValue("keyboardHeight");
      double nh = obj.getDoubleValue("navigationHeight");

      double cw = ww; // obj.getDoubleValue("decorWidth");
      double ch = wh; // - sh; // obj.getDoubleValue("decorHeight") - sh - nh;
      if (cw <= 100) {
        cw = ww;
      }
      if (ch <= 100) {
        ch = wh; // - sh; // - nh;
      }

      double ratio = getScale(cw, ch, layoutType, density);
      if (ratio <= 0.1) {
        ratio = 1;
      }

      eventNode.ratio = ratio;
      eventNode.windowWidth = wh;
      eventNode.windowHeight = windowHeight;
      eventNode.keyboardHeight = ratio*kh;

      boolean isSplit2Show = obj.getBooleanValue("isSplit2Show");
      Integer gravityViewId = obj.getInteger("gravityViewId");
      String gravityViewIdName = obj.getString("gravityViewIdName");

      Integer gravityX = obj.getInteger("gravityX"); // 数据库字段默认值设置为 null // - 1;
      Integer gravityY = obj.getInteger("gravityY"); // 数据库字段默认值设置为 null // - 1;
      Integer ballGravity = obj.getInteger("ballGravity"); // 数据库字段默认值设置为 null // - 1;
      Integer ballGravity2 = obj.getInteger("ballGravity2"); // 数据库字段默认值设置为 null // - 1;
      if (gravityViewId == null) {
        gravityViewId = -1;
      }
      if (gravityX == null) {
        gravityX = -1;
      }
      if (gravityY == null) {
        gravityY = -1;
      }
      if (ballGravity == null) {
        ballGravity = -1;
      }
      if (ballGravity2 == null) {
        ballGravity2 = -1;
      }

      int pc = obj.getIntValue("pointerCount");
      double x = obj.getDoubleValue("x");
      double y = obj.getDoubleValue("y");
      double x2 = obj.getDoubleValue("x2");
      double y2 = obj.getDoubleValue("y2");

      boolean isCur = isSplit2Show && (step == this.step || Objects.equals(getCurrentActivity().getClass().getName(), obj.getString("activity")));
      int id = isCur ? getResId(gravityViewIdName) : 0;
      View curView = isCur ? findView(id > 0 ? id : gravityViewId) : null;
//      Rect curRect = curView == null ? null : new Rect();

      int[] loc = curView == null ? null : new int[2];
      if (loc != null) {
        // 居然不是 curView，而是窗口的 0, 81 - 1080, 2400  curView.getWindowVisibleDisplayFrame(curRect);
        curView.getLocationOnScreen(loc);
      }

      double sx = 0; // loc == null ? obj.getDoubleValue("splitX") : loc[0] + curFocusView.getWidth() - curFocusView.getPaddingRight(); //curRect.right - curView.getPaddingRight();
      double sy = 0; // = loc == null ? obj.getDoubleValue("splitY") : loc[1] + curFocusView.getHeight() - curFocusView.getPaddingBottom() - statusHeight; //curRect.bottom - curView.getPaddingBottom();
      double sx2 = 0; // = loc == null ? obj.getDoubleValue("splitX2") : loc[0] + curFocusView.getPaddingLeft(); // curRect.left + curView.getPaddingLeft();
      double sy2 = 0; // = loc == null ? obj.getDoubleValue("splitY2") : loc[1] + curFocusView.getPaddingTop() - statusHeight; // curRect.top + curView.getPaddingTop();
      if (loc != null) {
        sx = loc[0] + curView.getWidth() - curView.getPaddingRight(); //curRect.right - curView.getPaddingRight();
        sy = loc[1] + curView.getHeight() - curView.getPaddingBottom() - statusHeight; //curRect.bottom - curView.getPaddingBottom();
        sx2 = loc[0] + curView.getPaddingLeft(); // curRect.left + curView.getPaddingLeft();
        sy2 = loc[1] + curView.getPaddingTop() - statusHeight; // curRect.top + curView.getPaddingTop();
      }

      if (sx2 <= 0 || sx >= windowWidth || sy2 <= 0 || sy >= windowHeight || Math.abs(sx - sx2) < 30 || Math.abs(sy - sy2) < 30) {
        sx = transSplitX(obj.getDoubleValue("splitX"), cw, ballGravity, ratio);
        sy = transSplitY(obj.getDoubleValue("splitY"), ch, ballGravity, ratio);
        sx2 = transSplitX(obj.getDoubleValue("splitX2"), cw, ballGravity2, ratio);
        sy2 = transSplitY(obj.getDoubleValue("splitY2"), ch, ballGravity2, ratio);
      }

      eventNode.x = x;
      eventNode.y = y;
      eventNode.x2 = x2;
      eventNode.y2 = y2;
      eventNode.isSplit2Show = isSplit2Show;
      eventNode.splitX = sx;
      eventNode.splitY = sy;
      eventNode.splitX2 = sx2;
      eventNode.splitY2 = sy2;
      eventNode.gravityX = gravityX;
      eventNode.gravityY = gravityY;

      // double ratio = getScale(ww, ) //  1f*windowWidth/ww;  //始终以显示时宽度比例为准，不管是横屏还是竖屏   1f*Math.min(windowWidth, windowHeight)/Math.min(ww, wh);

      // 既然已经存了 上下 绝对坐标、屏幕像素 等完整信息，没必要用负值？负值保证稳定，因为 18:9 和 16:9 的分割线高度不一样
      sx = sx > 0 ? sx : ww + sx; // 转为正数
      double minSX = sx2 <= 0 ? sx : Math.min(sx, sx2);
      double maxSX = sx2 <= 0 ? sx : Math.max(sx, sx2);

      sy = sy > 0 ? sy : wh + sy; // 转为正数
      double minSY = sy2 <= 0 ? sy : Math.min(sy, sy2);
      double maxSY = sy2 <= 0 ? sy : Math.max(sy, sy2);

      double rx;
      if (gravityX == GRAVITY_RATIO) {
        double maxSX2 = windowWidth + ratio*(maxSX - ww);
        rx = minSX + (maxSX2 - ratio*minSX)*(x - minSX)/(maxSX - minSX);
      }
      else if (gravityX == GRAVITY_CENTER || (gravityX < 0 && x > minSX && x < maxSX)) { //居中，一般是弹窗
        double mid = (minSX + maxSX)/2f; // minSX + (maxSX2 - minSX)*(x - minSX)/(maxSX - minSX)
//          rx = x < mid ? ratio*x : decorWidth*mid/cw + ratio*(x - maxSX); // 居中靠左/靠右，例如关闭按钮
//        rx = windowWidth*mid/cw + ratio*(x - mid); // 居中靠左/靠右，例如关闭按钮

        double maxSX2 = windowWidth + ratio*(maxSX - ww);
        double mid2 = (ratio*minSX + maxSX2)/2f;
        rx = mid2 + ratio*(x - mid); // 居中靠上/靠下，例如 取消、确定 按钮
      }
      else if (gravityX == GRAVITY_RIGHT || (gravityX < 0 && (x < 0 || x >= maxSX))) { // 靠右，例如列表项右侧标记已读、添加、删除、数量输入框等按钮
        rx = windowWidth + ratio*(x < 0 ? x : x - cw);
      }
      else { // if (gravityX == GRAVITY_LEFT || (x >= 0 && x <= minSX)) { // 靠左
        rx = ratio*x;
      }


      // 不一定这样，例如 小米 12 Pro 因为有摄像头挖孔所以横屏过来会默认不显示左侧摄像头占的宽度 // 进一步简化上面的，横向是所有都一致 rx = ratio*x + decorView.getX();

      double ry;
      if (gravityY == GRAVITY_RATIO) {
        double maxSY2 = windowHeight + ratio*(maxSY - wh);
        ry = minSY + (maxSY2 - ratio*minSY)*(y - minSY)/(maxSY - minSY);
      }
      else if (gravityY == GRAVITY_CENTER || (gravityY < 0 && y > minSY && y < maxSY)) { //居中，一般是弹窗
        double mid = (minSY + maxSY)/2f;
//        ry = (windowHeight /* - (isSeparatedStatus ? 0 : statusHeight) */)*mid/ch + ratio*(y - mid); // 居中靠上/靠下，例如 取消、确定 按钮

        double maxSY2 = windowHeight + ratio*(maxSY - wh);
        double mid2 = (ratio*minSY + maxSY2)/2f;
        ry = mid2 + ratio*(y - mid); // 居中靠上/靠下，例如 取消、确定 按钮
      }
      else if (gravityY == GRAVITY_BOTTOM || (gravityY < 0 && (y < 0 || y >= maxSY))) { // 靠下，例如底部 tab、菜单按钮、悬浮按钮等
        ry = windowHeight /* - (isSeparatedStatus ? 0 : statusHeight) */ + ratio*(y < 0 ? y : y - ch); // decorHeight + ratio*(y < 0 ? y : y - ch);
      }
      else { // if (gravityY == GRAVITY_TOP || (y >= 0 && y <= minSY)) { // 靠上
        ry = ratio*y;
      }

      rx += windowX + decorX;
      ry += windowY + decorY + statusHeight; // 此时不能确定 (view != null && popupWindow != null && popupWindow.isShowing() ? 0 : statusHeight); // + (isSeparatedStatus ? statusHeight : 0);

      if (x2 == 0 || y2 == 0) {
        event = MotionEvent.obtain(
                obj.getLongValue("downTime"),
                obj.getLongValue("eventTime"),
                obj.getIntValue("action"),
//                            obj.getIntValue("targetCount"),
                (float) rx,
                (float) ry,
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
        MotionEvent.PointerCoords p1 = new MotionEvent.PointerCoords();
        p1.x = (float) rx;
        p1.y = (float) ry;

        MotionEvent.PointerCoords p2 = new MotionEvent.PointerCoords();
        p2.x = (float) (rx + x2 - x);
        p2.y = (float) (ry + y2 - y);

        event = MotionEvent.obtain(
                obj.getLongValue("downTime"),
                obj.getLongValue("eventTime"),
                obj.getIntValue("action"),
                pc,
                obj.getObject("pointerIds", int[].class),
                new MotionEvent.PointerCoords[] {p1, p2},
                obj.getIntValue("metaState"),
//                obj.getIntValue("buttonState"),
                obj.getFloatValue("xPrecision"),
                obj.getFloatValue("yPrecision"),
                obj.getIntValue("deviceId"),
                obj.getIntValue("edgeFlags"),
                obj.getIntValue("source"),
                obj.getIntValue("flags")
        );
      }

    }
    else {
      event = null;
    }


//                list.add(event);

    eventNode.step = step;
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
//      eventNode.header = obj.getString("header");
//      eventNode.request = obj.getString("request");
//      eventNode.response = obj.getString("response");

    eventNode.windowX = obj.getIntValue("windowX");
    eventNode.windowY = obj.getIntValue("windowY");
    eventNode.decorX = obj.getDoubleValue("decorX");
    eventNode.decorY = obj.getDoubleValue("decorY");

    eventNode.item = event;
    eventNode.obj = obj;

    return eventNode;
  }


  private double transSplitX(double sx, double cw, int ballGravity, double ratio) {
    if (Math.abs(sx) > cw) {
      sx = cw;
    }

    if (ballGravity == GRAVITY_TOP_RIGHT || ballGravity == GRAVITY_BOTTOM_RIGHT) {
      sx = sx < 0 ? sx : sx - cw;
    }
    else if (ballGravity == GRAVITY_TOP_LEFT || ballGravity == GRAVITY_BOTTOM_LEFT) {
      sx = sx < 0 ? sx + cw : sx;
    }

    Double ratioX = null;
    if (ballGravity == GRAVITY_RATIO || ballGravity == GRAVITY_RATIO_TOP || ballGravity == GRAVITY_RATIO_BOTTOM) {
      ratioX = sx/cw;
    }

    sx = ratioX == null ? sx*ratio : ratioX*windowWidth;

    return sx;
  }
  private double transSplitY(double sy, double ch, int ballGravity, double ratio) {
    if (Math.abs(sy) > ch) {
      sy = ch;
    }

    if (ballGravity == GRAVITY_BOTTOM_LEFT || ballGravity == GRAVITY_BOTTOM_RIGHT) {
      sy = sy < 0 ? sy : sy - ch;
    }
    else if (ballGravity == GRAVITY_TOP_LEFT || ballGravity == GRAVITY_TOP_RIGHT) {
      sy = sy < 0 ? sy + ch : sy;
    }

    Double ratioY = null;
    if (ballGravity == GRAVITY_RATIO || ballGravity == GRAVITY_RATIO_LEFT || ballGravity == GRAVITY_RATIO_RIGHT) {
      ratioY = sy/ch;
    }

    sy = ratioY == null ? sy*ratio : ratioY*windowHeight;

    return sy;
  }

  private double getScale(double ww, double wh, int layoutType, double density) {
//    if (decorWidth <= 0) {
      if (windowWidth <= 0) {
        windowWidth = screenWidth;
      }
//      decorWidth = windowWidth;
//    }
//    if (decorHeight <= 0) {
      if (windowHeight <= 0) {
        windowHeight = screenHeight;
      }
//      decorHeight = windowHeight;
//    }

    double curWW = Math.min(windowWidth, windowHeight); //  - (isSeparatedStatus ? 0 : statusHeight)); // decorWidth, decorHeight - statusHeight - navigationHeight);
    double targetWw = Math.min(ww, wh);
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
  public synchronized void onUIEvent(int action, Window.Callback callback, Activity activity, Fragment fragment) {
    onUIEvent(action, callback, null, fragment, null, null);
  }


  public synchronized void onUIEvent(int action, Window.Callback callback, Activity activity, Fragment fragment, WebView webView, String url) {
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

    output(null, currentEventNode, activity);
    if (isReplay) {
      Node<InputEvent> curNode = currentEventNode;
      if (curNode == null || curNode.disable || (curNode.type == InputUtil.EVENT_TYPE_UI && curNode.action == action
              && ((activity == null || Objects.equals(curNode.activity, activity.getClass().getName()))
//                && (Objects.equals(curNode.fragment, fragment == null ? null : fragment.getClass().getName()))
      ))) {
//        waitMap = new LinkedHashMap<>();
        Node<InputEvent> nextNode = curNode == null ? null : curNode.next;
        long duration = calcDuration(curNode, nextNode);

        Message msg = handler.obtainMessage();
        msg.obj = nextNode;
        handler.sendMessageDelayed(msg, duration);
      }
    }
    else {
      JSONObject obj = newEvent(callback, activity, fragment);
      obj.put("type", InputUtil.EVENT_TYPE_UI);
      obj.put("action", action);
      // 总是导致停止后续动作，尤其是返回键相关的事件  obj.put("disable", action != InputUtil.UI_ACTION_RESUME);
      obj.put("disable", action != InputUtil.UI_ACTION_RESUME || webView == null || StringUtil.isEmpty(url, true));
      obj.put("format", "WEB");
      obj.put("name", StringUtil.isEmpty(url, true) ? "" : (url.length() <= 50 ? url : url.substring(0, 30) + " ... " + url.substring(url.length() - 20)));
      obj.put("url", url);

      addEvent(obj);
    }
  }

//  public void onHTTPEvent(int action, String format, String url, String request, String response, Activity activity) {
//    onHTTPEvent(action, format, url, request, response, activity, null);
//  }
//  public void onHTTPEvent(int action, String format, String url, String request, String response, Fragment fragment) {
//    onHTTPEvent(action, format, url, request, response, null, fragment);
//  }
  public synchronized void onHTTPEvent(int action, String format, String method, String host, String url, String header, String request, String response, Activity activity, Fragment fragment) {
    if (isSplitShowing == false) {
      Log.e(TAG, "onHTTPEvent  isSplitShowing == false >> return null;");
      return;
    }

    if (activity == null && fragment != null) {
      activity = fragment.getActivity();
    }

    output(null, currentEventNode, activity);
    if (isReplay) {
      Node<InputEvent> curNode = lastWaitNode == null ? currentEventNode : lastWaitNode;
      if (curNode == null || curNode.disable || /** ((activity == null || Objects.equals(curNode.activity, activity.getClass().getName()))
//                && (Objects.equals(curNode.fragment, fragment == null ? null : fragment.getClass().getName()))
              && */ (StringUtil.isNotEmpty(url, true) && ! waitMap.isEmpty()) // ) // 避免过多调用
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
        if (curNode == null || curNode.disable || waitMap.isEmpty()) {
          lastWaitNode = null;

          InputEvent curItem = curNode == null || curNode.disable ? null : curNode.item;
          Node<InputEvent> nextNode = curNode == null ? null : curNode.next;
          long duration = calcDuration(curNode, nextNode);

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
      obj.put("disable", action >= 0 && action != InputUtil.HTTP_ACTION_RESPONSE);
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
        obj.put("time", System.currentTimeMillis());  // TODO 如果有录屏，则不需要截屏，只需要记录时间点
        if (node.disable == false) {

          if (window != null && (node.item == null || node.action == MotionEvent.ACTION_DOWN)) {
            // 同步或用协程来上传图片
//            obj.put("screenshotUrl", screenshot(directory == null || directory.exists() == false ? parentDirectory : directory, window, inputId, toInputId, node.orientation));
          }
        }
        if (outputList == null) {
          outputList = new JSONArray();
        }
        synchronized (outputList) { // 居然出现 java.lang.ArrayIndexOutOfBoundsException: length=49; index=49
        	outputList.add(obj);
        }
//      }
//    });
  }

  /**屏幕截图
   * @return
   */
  public String screenshot(File directory, Window window, Long inputId, Long toInputId, int orientation) {
    View decorView = window == null ? null : window.getDecorView();
    if (decorView == null) {
      return null;
    }

    String filePath = null;
    try {
      Bitmap bitmap; // 截屏等记录下来

      synchronized (decorView) { // 必须，且只能是 Window，用 Activity 或 decorView 都不行 解决某些界面会报错 cannot find container of decorView
        decorView.setDrawingCacheEnabled(true);
        // decorView.buildDrawingCache(true);
        bitmap = decorView.getDrawingCache();
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(w <= h ? 0 : -90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, false);

        // 宽居然不是和高一样等比缩放，貌似没缩放
        // double scale = 720f/w;
        // int nw = 720;
        // int nh = Math.round(h*scale);
        // matrix.postScale(scale, scale);
        // bitmap = Bitmap.createBitmap(bitmap, 0, 0, nw, nh, matrix, false);

        decorView.destroyDrawingCache();
        decorView.setDrawingCacheEnabled(false);
      }

      // 保存图片
      File file = File.createTempFile("uiauto_screenshot_inputId_" + Math.abs(inputId) + "_time_" + System.currentTimeMillis(), ".png", directory);
      filePath = file.getAbsolutePath();

      Bitmap finalBitmap = bitmap;
      String finalFilePath = filePath;
      executorService.execute(new Runnable() {
          @Override
          public void run() {
            FileOutputStream fos = null;
            try {
              fos = new FileOutputStream(finalFilePath);
              finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }
            catch (Throwable e) {
              Log.e(TAG, "screenshot 截屏异常：" + e.getMessage());
            }
            finally {
              if (finalBitmap != null) {
                try {
                  finalBitmap.recycle();
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
          }
      });

      return file.getAbsolutePath(); // filePath = directory.getName() + "/" + file.getName();  // 返回相对路径
    }
    catch (Throwable e) {
      Log.e(TAG, "screenshot 截屏异常：" + e.getMessage());
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

    long eventTime = ie.getEventTime();
    if (eventTime <= 0) {
      eventTime = SystemClock.uptimeMillis(); // System.currentTimeMillis();
    }

    JSONObject lastItem = eventList == null || eventList.isEmpty() ? null : eventList.getJSONObject(eventList.size() - 1); ; // currentEventNode == null ? null : currentEventNode.item;

    if (ie instanceof KeyEvent) {
      KeyEvent event = (KeyEvent) ie;
      type = InputUtil.EVENT_TYPE_KEY;
      action = event.getAction();
      obj.put("type", type);

      //虽然 KeyEvent 和 MotionEvent 都有，但都不在父类 InputEvent 中 <<<<<<<<<<<<<<<<<<
      obj.put("action", action);
      long downTime = event.getDownTime();
      if (downTime <= 0) {
        downTime = eventTime;
      }

      obj.put("downTime", downTime);
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
//        if (mke.getWhen() != EditTextEvent.WHEN_ON) {
//          return null;
//        }

        obj.put("disable", mke.getWhen() != EditTextEvent.WHEN_ON);
        obj.put("edit", true);
        obj.put("target", mke.getTarget());
        obj.put("targetId", mke.getTargetId());
        obj.put("targetWebId", mke.getTargetWebId());
        obj.put("when", mke.getWhen());
        obj.put("s", mke.getS());
        obj.put("text", mke.getText());
        obj.put("start", mke.getStart());
        obj.put("count", mke.getCount());
        obj.put("after", mke.getAfter());

        Node<InputEvent> prevNode = webView == null || currentEventNode == null || (mke.getX() != null && mke.getY() != null) ? null : currentEventNode.prev;
        InputEvent prevItem = prevNode == null ? null : prevNode.item;
        if (prevItem instanceof MotionEvent) {
          double ratio = 980f/webView.getWidth();

          int[] loc = new int[2];
          webView.getLocationOnScreen(loc);

          obj.put("x", ratio*((MotionEvent) prevItem).getX());
          obj.put("y", ratio*(((MotionEvent) prevItem).getY() - loc[1]));
        }
      }
      else { // 解决录制网页的一次返回按键等录到连续的返回键 DOWN, DOWN, UP, UP
        if (lastItem != null) {
          if (Objects.equals(lastItem.getInteger("action"), action) && Objects.equals(lastItem.getIntValue("keyCode"), event.getKeyCode())) {
            return null;
          }
        }
      }
    }
    else if (ie instanceof MotionEvent) {
      MotionEvent event = (MotionEvent) ie;

      type = InputUtil.EVENT_TYPE_TOUCH;
      action = event.getAction();

      double x = event.getX();
      double y = event.getY();

      int pc = event.getPointerCount();
      Float x2 = pc < 2 ? null : event.getX(1);
      Float y2 = pc < 2 ? null : event.getY(1);

      obj.put("pointerCount", pc);
      obj.put("pointerIds", pc < 2 ? new int[] {event.getPointerId(0)} : new int[] {event.getPointerId(0), event.getPointerId(1)});
      if (view != null && webView == null) { // PopupWindow 等小窗口不需要分割？
        obj.put("x", x);
        obj.put("y", y);
        obj.put("x2", x2);
        obj.put("y2", y2);
      }
      else { // FIXME 根据 ballGravity, ballGravity2 和 gravityX, gravityY 计算
        double rx = x - windowX - decorX;
        double ry = y - windowY - decorY - (popupWindow != null && popupWindow.isShowing() ? 0 : statusHeight); // (isSeparatedStatus ? 0 : statusHeight);

        if (callback instanceof Dialog) {
          Dialog dialog = (Dialog) callback;
          // TODO
        }

        View decorView = window.getDecorView();
        double dx = decorView.getX();
        double dy = decorView.getY();
        double dw = decorView.getWidth();
        double dh = decorView.getHeight();

        // 只在回放前一处处理逻辑
        isSplit2Showing = floatBall2 != null && floatBall2.isShowing();
//      double minX = (isSplit2Showing ? Math.min(floatBall.getX(), floatBall2.getX()) : floatBall.getX()) - splitRadius;
        double maxX = (isSplit2Showing ? Math.max(floatBall.getX(), floatBall2.getX()) : floatBall.getX()) + splitRadius;
//      double avgX = (minX + maxX)/2;
//      double minY = (isSplit2Showing ? Math.min(floatBall.getY(), floatBall2.getY()) : floatBall.getY()) - splitRadius;
        double maxY = (isSplit2Showing ? Math.max(floatBall.getY(), floatBall2.getY()) : floatBall.getY()) + splitRadius;
//      double avgY = (minY + maxY)/2;

        rx = rx < maxX ? rx : rx - windowWidth; // dw + dx); // Math.round(x - windowX - decorX - (x < avgX ? 0 : decorWidth)));
        ry = ry < maxY ? ry : ry - windowHeight; // + (isSeparatedStatus ? 0 : statusHeight)); // dh + dy + statusHeight + navigationHeight); // Math.round(y - windowY - decorY - (y < avgY ? 0 : decorHeight)));

        obj.put("x", rx);
        obj.put("y", ry);
        obj.put("x2", x2 == null ? null : rx + x2 - x);
        obj.put("y2", y2 == null ? null : ry + y2 - y);
      }

      obj.put("rawX", event.getRawX());
      obj.put("rawY", event.getRawY());
      if (pc >= 2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        obj.put("rawX2", event.getRawX(1));
        obj.put("rawY2", event.getRawY(1));
      }

      // 导致录制不到最初的下拉刷新？
      if (lastItem != null) { // 避免重复，尤其是 ACTION_UP
        if (Objects.equals(lastItem.get("x"), obj.get("x")) && Objects.equals(lastItem.get("y"), obj.get("y"))
                && Objects.equals(lastItem.getInteger("action"), action)) {
          return null;
        }
      }

      obj.put("type", type);
      //虽然 KeyEvent 和 MotionEvent 都有，但都不在父类 InputEvent 中 <<<<<<<<<<<<<<<<<<
      obj.put("action", action);
      obj.put("downTime", event.getDownTime());
      obj.put("metaState", event.getMetaState());
      obj.put("source", event.getSource());
      obj.put("deviceId", event.getDeviceId());
      //虽然 KeyEvent 和 MotionEvent 都有，但都不在父类 InputEvent 中 >>>>>>>>>>>>>>>>>>

      obj.put("size", event.getSize());
      obj.put("pressure", event.getPressure());
      obj.put("xPrecision", event.getXPrecision());
      obj.put("yPrecision", event.getYPrecision());
//      obj.put("pointerCount", event.getPointerCount());
      obj.put("edgeFlags", event.getEdgeFlags());
    }

    obj.put("eventTime", eventTime);

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
  private boolean isAlignLeft(double x) {
    return ! isAlignRight(x);
  }

  private boolean isAlignRight(MotionEvent event) {
    return event != null && isAlignRight(event.getX());
  }
  private boolean isAlignRight(double x) {
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
  private boolean isAlignTop(double y) {
    return ! isAlignBottom(y);
  }

  private boolean isAlignBottom(MotionEvent event) {
    return event != null && isAlignBottom(event.getY());
  }
  private boolean isAlignBottom(double y) {
    if (floatSplitY == null) {
      return isFloatBallShowing() ? floatBall.getX() != 0 && floatBall.getY() != 0 && y > floatBall.getY() + splitSize/2 : false;
    }
    return floatSplitY != null && floatSplitY.getY() != 0 && y > floatSplitY.getY();
  }


  public <V extends View> V findView(@IdRes int id) {
    return getCurrentWindow().findViewById(id);
  }
  public <V extends View> V findView(String id) {
    String url = webUrl;
    if (StringUtil.isEmpty(url, true)) {
      url = webView == null ? null : webView.getUrl();
    }
    Map<String, EditText> map = editTextMap.get(url);
    EditText et = map == null ? null : map.get(id);

    return (V) et;
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

  public <V extends View> V findViewByPoint(View view, Class<V> clazz, double x, double y, boolean onlyFocusable) {
//    if (view == null || x < view.getX() || x > view.getX() + view.getWidth()
//            || y < view.getY() || y > view.getY() + view.getHeight()) {
//      return null;
//    }

    int[] loc = view == null ? null : new int[2];
    if (loc == null) {
      return null;
    }

    view.getLocationOnScreen(loc); // view.getLocationInWindow(loc);

    if (x < loc[0] || x > loc[0] + view.getWidth()
            || y < loc[1] || y > loc[1] + view.getHeight()) {
      return null;
    }

    if (view instanceof ViewGroup) {
      ViewGroup vg = (ViewGroup) view;

      for (int i = vg.getChildCount() - 1; i >= 0; i--) {
        View v = findViewByPoint(vg.getChildAt(i), clazz, x, y, onlyFocusable);
        if (v != null && (onlyFocusable == false || view.isFocusable() || view.isFocusableInTouchMode())
                && (clazz == null || clazz.isAssignableFrom(view.getClass()))) {
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

    if (currentEventNode != null) {
      Node<InputEvent> node = obj2EventNode(event, currentEventNode, step);
      currentEventNode = node.next = new Node<>(node, null, null);
    }

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
    splitY = Math.round(floatBall.getY() + splitRadius - windowHeight); //  + (isSeparatedStatus ? 0 : statusHeight)); // + navigationHeight); // decorHeight); // - decorY

    isSplit2Showing = floatBall2 != null && floatBall2.isShowing();
    splitX2 = isSplit2Showing ? Math.round(floatBall2.getX() + splitRadius) : 0; // decorWidth) : 0; //  - decorX - decorWidth) : 0;
    splitY2 = isSplit2Showing ? Math.round(floatBall2.getY() + splitRadius) : 0; // decorHeight) : 0; // - decorY - decorHeight) : 0;

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
    if (isSplit2Showing) {
      event.put("isSplit2Show", 1);
      event.put("gravityViewId", curFocusView == null ? null : curFocusView.getId());
      event.put("gravityViewIdName", getResIdName(curFocusView));

      event.put("gravityX", gravityX);
      event.put("gravityY", gravityY);
      event.put("ballGravity", ballGravity);
      event.put("ballGravity2", ballGravity2);
    }
    event.put("windowX", windowX);
    event.put("windowY", windowY);
    event.put("windowWidth", windowWidth);
    event.put("windowHeight", windowHeight); // - (isSeparatedStatus ? 0 : statusHeight));
    event.put("statusHeight", statusHeight);
    event.put("keyboardHeight", keyboardHeight);
    event.put("navigationHeight", navigationHeight);
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

  private int getResId(String gravityViewIdName) {
    try {
      return getResources().getIdentifier(gravityViewIdName, "id", getPackageName());
    }
    catch (Throwable e) {
      e.printStackTrace();
    }
    return 0;
  }
  private String getResIdName(View v) {
    return v == null ? null : getResIdName(v.getId());
  }
  private String getResIdName(@IdRes int id) {
    try {
      return getResources().getResourceEntryName(id);
    }
    catch (Throwable e) {
      e.printStackTrace();
    }
    return null;
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

    tvControllerPlay.setText(R.string.replay);
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
    Node<InputEvent> eventNode = new Node<>(null, null, null);
    if (clear) {
	    setEventList(null);
	    firstEventNode = currentEventNode = eventNode;
	    step = 0;
	    allStep = 0;
	    duration = 0;
	    flowId = - System.currentTimeMillis();
        tvControllerTime.setText("0:00");
    }
    else {
      if (currentEventNode == null) {
        currentEventNode = firstEventNode;
      }
      eventNode.prev = currentEventNode;
      if (currentEventNode != null) {
        currentEventNode.next = eventNode;
      }
    }

    tvControllerPlay.setText(R.string.record);
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

  private boolean isProxy = false;
  private String proxyServer = "";
  public void setHttpProxy(boolean isProxy, String server) {
    this.isProxy = isProxy;
    this.proxyServer = server;
  }

  public boolean isProxyEnabled() {
    return isProxy;
  }
  public String getProxyServer() {
    return proxyServer;
  }

  private String delegateId = "";
  public String getDelegateId() {
    return delegateId;
  }
  public UIAutoApp setDelegateId(String delegateId) {
    this.delegateId = delegateId;
    return this;
  }

  public String getHttpUrl(String url_) throws UnsupportedEncodingException {
    String url = StringUtil.getNoBlankString(url_);
    String proxyServer = isProxyEnabled() ? getProxyServer() : null;

    if (StringUtil.isNotEmpty(proxyServer, true)) {
      String delegateId = getDelegateId();
      url = proxyServer + "/delegate?" + (isShowing && isSplitShowing ? ("$_record=" + (isReplay ? -1 : 1) + "&") : "")
              + (StringUtil.isEmpty(delegateId, true) ? "" : "$_delegate_id=" + delegateId + '&')
              + "$_delegate_url=" + URLEncoder.encode(url, "UTF-8");
    }

    return url;
  }

  protected WebView webView;
  public void setCurrentWebView(WebView webView, @NotNull Activity activity, @Nullable Fragment fragment) {
    this.webView = webView;
    setCurrentView(view, callback, activity, fragment);
  }
  protected String webUrl;
  public void setCurrentWebUrl(String webUrl) {
    this.webUrl = webUrl;
  }

  public Map<String, Map<String, EditText>> editTextMap = new LinkedHashMap<>();
  public Map<String, Map<Integer, String>> editTextIdMap = new LinkedHashMap<>();
  public JSONObject addWebEditTextEvent(@NotNull Activity activity, @Nullable Fragment fragment, @NotNull WebView webView
          , @NotNull String id, int selectionStart, int selectionEnd, String text, Integer touchX, Integer touchY) {
    if (isShowing == false || isSplitShowing == false || isReplay) {
      return null;
    }

    text = StringUtil.getString(text);

    String url = webUrl;
    if (StringUtil.isEmpty(url, true)) {
      url = webView == null ? null : webView.getUrl();
    }
    Map<String, EditText> map = editTextMap.get(url);

    EditText et = map == null ? null : map.get(id);
    if (et == null) {
      et = new EditText(activity);
      et.setId(View.generateViewId());

      if (map == null) {
        map = new LinkedHashMap<>();
        editTextMap.put(url, map);
      }
      map.put(id, et);

      Map<Integer, String> idMap = editTextIdMap.get(url);
      if (idMap == null) {
        idMap = new LinkedHashMap<>();
        editTextIdMap.put(url, idMap);
      }
      idMap.put(et.getId(), id);
    }

    et.setText(text);
    et.setSelection(selectionStart, selectionEnd);

    InputEvent ie = new EditTextEvent(KeyEvent.ACTION_UP, 0, et, EditTextEvent.WHEN_ON
            , text, selectionStart, selectionEnd, text)
            .setTargetWebId(id).setX(touchX).setY(touchY);
    return addInputEvent(ie, activity.getWindow().getCallback(), activity, fragment);
  }


  public String readAssetsText(String fileName) {
    try {
      byte[] buffer;
      try (InputStream is = getAssets().open(fileName)) {
        int length = is.available();
        buffer = new byte[length];
        is.read(buffer);
      } // FIXME FileNotFound
      String result = new String(buffer, "utf8");
      return result;
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  public void initWeb(@NotNull Activity activity, @Nullable Fragment fragment, @NotNull WebView webView, String webUrl) {
    setCurrentWebView(webView, activity, fragment);
    this.webUrl = webUrl;
//    editTextMap = new LinkedHashMap<>();
    if (isReplay == false && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      String script = "" +
              "    function generateRandom() {\n" +
              "      return Math.floor((1 + Math.random()) * 0x10000)\n" +
              "        .toString(16)\n" +
              "        .substring(1);\n" +
              "    }\n" +
              "    " +
//              "    (function f() {\n" +
//              "      if (window.VConsole == null) {\n" +
//              "        return;" +
//              "      }\n" +
//              "      var vConsole = new window.VConsole();\n" +
//              "      var pluginList = vConsole.pluginList;\n" +
//              "      var network = pluginList.network;\n" +
//              "      var exporter = network.exporter;\n" +
//              "      var model = exporter.model;\n" +
//              "      var updateRequest = model.updateRequest;\n" +
//              "      model.updateRequest = function(id, item) {\n" +
//              "          item = item || {};\n" +
//              "          if (item.status == 0 && [null, undefined, ''].indexOf(item.statusText) >= 0) {\n" +
//              "              interception.onHttpEvent(0, id, JSON.stringify(item));\n" +
//              "          }\n" +
//              "          else if (item.status >= 200 && [null, undefined, '', 'Pending', 'Loading'].indexOf(item.statusText) < 0) {\n" +
//              "              interception.onHttpEvent(1, id, JSON.stringify(item));\n" +
//              "          }\n" +
//              "          return updateRequest.apply(this, arguments);\n" +
//              "      }\n" +
//              "    })();\n" +
//                    "    JSON.stringify(document);\n" +
//              "    var onTouchEventCallback = function(event) {\n" +
//              "        var target = event.target;\n" +
//              "        if (target == null || ['input', 'textarea'].indexOf(target.localName) < 0 || ['INPUT', 'TEXTAREA'].indexOf(target.tagName) < 0) {\n" +
//              "            return;\n" +
//              "        }\n" +
//              "        var id = target.id;\n" +
//              "        if (id == null || id.trim().length <= 0) {\n" +
//              "            /* target.id = */ id = generateRandom();\n" +
//              "            var map = document.uiautoEditTextMap || {};\n" +
//              "            map[id] = target;\n" +
//              "            document.uiautoEditTextMap = map;\n" +
//              "        }\n" +
//              "        var touches = event.touches;\n" +
//              "        var touch = touches == null ? null : touches[0];\n" +
//              "        interception.onTouchEvent(id, touch == null ? null : touch.pageX, touch == null ? null : touch.pageY); \n" +
//              "    }\n" +
//              "    document.addEventListener('touchstart', onTouchEventCallback);\n" +
              "    var onEditEventCallback = function(event) {\n" +
              "        var target = event.target;\n" +
              "        if (target == null || ['input', 'textarea'].indexOf(target.localName) < 0 || ['INPUT', 'TEXTAREA'].indexOf(target.tagName) < 0) {\n" +
              "            return;\n" +
              "        }\n" +
              "        var id = target.id;\n" +
              "        if (id == null || id.trim().length <= 0) {\n" +
              "            /* target.id = */ id = generateRandom();\n" +
              "            var map = document.uiautoEditTextMap || {};\n" +
              "            map[id] = target;\n" +
              "            document.uiautoEditTextMap = map;\n" +
              "        }\n" +
              "        interception.onEditEvent(id, target.selectionStart, target.selectionEnd, target.value); \n" +
              "    };\n" +
//              "    document.addEventListener('onporpertychange', onEditEventCallback);\n" +
//              "    document.addEventListener('change', onEditEventCallback);\n" +
              "    document.addEventListener('input', onEditEventCallback);\n" +
              "    var onKeyEventCallback = function(event) {\n" +
              "        var target = event.target;\n" +
              "        var id = target.id;\n" +
              "        if (id == null || id.trim().length <= 0) {\n" +
              "            /* target.id = */ id = generateRandom();\n" +
              "            var map = document.uiautoEditTextMap || {};\n" +
              "            map[id] = target;\n" +
              "            document.uiautoEditTextMap = map;\n" +
              "        }\n" +
              "        interception.onKeyEvent('', event.type == 'keyup' ? 1 : 0, event.key, event.code || event.keyCode); \n" +
              "    };\n" +
              "    document.addEventListener('keydown', onKeyEventCallback);\n" +
              "    document.addEventListener('keyup', onKeyEventCallback);\n" +
//              "    document.addEventListener('keypress', onKeyEventCallback);\n" +
              "    var ret = 'document.uiautoEditTextMap = ' + JSON.stringify(document.uiautoEditTextMap);\n" +
              "    ret";
      webView.evaluateJavascript(script, new ValueCallback<String>() {
        @Override
        public void onReceiveValue(String value) {
          unitauto.Log.d(TAG, "wvWebView.evaluateJavascript value = " + value);
        }
      });
    }
  }

  public static class Node<E> {
    E item;
    Node<E> next;
    Node<E> prev;
    JSONObject obj;

    int step;

    long id;
    long flowId;
    boolean disable;
    int type;
    int action;
    long time;
    boolean isSplit2Show;
    double splitX, splitX2;
    double splitY, splitY2;
    double splitSize;
    double windowX;
    double windowY;
    double decorX;
    double decorY;
    double ratio;
    double windowWidth, windowHeight;
    double keyboardHeight;
    int orientation;
    int gravityX, gravityY;
    int ballGravity, ballGravity2;
    double x, y, x2, y2;

    String activity;
    String fragment;
    String method;
//    String header;
    String host;
    String url;
//    String request;
//    String response;

    public Node(Node<E> prev, E element, Node<E> next) {
      this.item = element;
      this.next = next;
      this.prev = prev;
    }

//    public Node(int ballGravity, double splitX, double splitY) {
//      this.ballGravity = ballGravity;
//      this.splitX = splitX;
//      this.splitY = splitY;
//    }
//
//    public Node(int ballGravity, double splitX, double splitY, int ballGravity2, double splitX2, double splitY2) {
//      this(ballGravity, splitX, splitY);
//      this.ballGravity2 = ballGravity2;
//      this.splitX2 = splitX2;
//      this.splitY2 = splitY2;
//    }

  }

  public static class BallPoint {
    int gravity;
    double x;
    double y;

    public BallPoint() {
    }

    public BallPoint(int gravity, double x, double y) {
      this.gravity = gravity;
      this.x = x;
      this.y = y;
    }

    public int getGravity() {
      return gravity;
    }
    public void setGravity(int gravity) {
      this.gravity = gravity;
    }

    public double getX() {
      return x;
    }
    public void setX(double x) {
      this.x = x;
    }

    public double getY() {
      return y;
    }
    public void setY(double y) {
      this.y = y;
    }
  }

}
