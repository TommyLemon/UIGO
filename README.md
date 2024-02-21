<h1 align="center" style="text-align:center;">
  UIGO
</h1>
 
<p align="center">📱 零代码快准稳 UI 智能录制回放平台 🚀</p>
<p align="center">自动兼容任意宽高比分辨率屏幕，自动精准等待网络请求，录制回放快、准、稳！</p>
<p align="center" >
  <a href="https://github.com/TommyLemon/UIGO/tree/master/UIAuto-Android"><img src="https://img.shields.io/badge/Demo-Android26%2B-brightgreen.svg?style=flat"></a>
  <a href="https://github.com/TommyLemon/UIGO/tree/master/APIJSONApp"><img src="https://img.shields.io/badge/App-Android26%2B-brightgreen.svg?style=flat"></a>
  <a href="https://github.com/APIJSON/APIJSON-Demo/tree/master/APIJSON-Java-Server/APIJSONBoot"><img src="https://img.shields.io/badge/Server-Java1.8%2B-brightgreen.svg?style=flat"></a>
  <a href="https://github.com/TommyLemon/UIGO/tree/master/MySQL"><img src="https://img.shields.io/badge/MySQL-5.7%2B-brightgreen.svg?style=flat"></a>
</p>
<p align="center" >
  <a href="https://github.com/TommyLemon/UIGO#%E7%A4%BA%E4%BE%8B%E9%A1%B9%E7%9B%AE">录制回放</a>
  <a href="https://github.com/TommyLemon/UIGO#%E5%BF%AB%E9%80%9F%E4%B8%8A%E6%89%8B">快速上手</a>
  <a href="http://apijson.cn/ui">在线工具</a>
</p>

<p align="center" >
  <img src="https://raw.githubusercontent.com/TommyLemon/UIGO/master/logo.png" />
</p>

---
<br />

<p align="center" >
<img width="240" src="https://github.com/TommyLemon/UIGO/assets/5738175/ef03bb7d-f3a5-497f-8087-fbc1ee7594f9" /><img width="240" src="https://github.com/TommyLemon/UIGO/assets/5738175/d3ffbdaa-1b35-426a-9c4d-6124a2e63550" /><img width="240" src="https://github.com/TommyLemon/UIGO/assets/5738175/e994d333-05a1-4102-b0dc-e1671ecde2e1" />
</p>

<img width="1280" src="https://github.com/TommyLemon/UIGO/assets/5738175/54cf82d0-99b0-4085-ab89-d54ff95ef4c4" href="https://www.bilibili.com/video/BV1wA4m137ha" />

## UIGO - 📱 零代码快准稳 UI 智能录制回放平台 🚀 
**自动兼容任意宽高比分辨率屏幕，自动精准等待网络请求，录制回放快、准、稳！**<br />
适用于 大量重复操作界面、一次录制到处回放、App UI/功能 自动化测试、<br />
帮助开发快速复现和排查 bug、方便判断 bug 原因出在前端还是后端 等，<br />
大量减少耗时费力又无聊的重复手工操作，大幅提高手工和自动化测试效率，<br />
强力杜绝 测试和开发、前端和后端 关于缺陷单踢皮球等各种低效扯皮内耗！<br />

**用户包含腾讯，应微信团队邀请分享了 零代码测试工具与实践(API•单元•UI)**

### 支持功能
* **零代码 录制和回放 触屏、按键、键盘、数据 等**
* **支持 16:9 标准屏、19.5:9 全面屏等各种设备屏幕**
* **支持原生页面、内置 H5 网页、浏览器加载网页等**
* 支持 Android 真机、Studio/Genymotion 等模拟器
* **单双指点击、长按、滑动、缩放各种像素级精细操作**
* **自动精准等待、模拟 HTTP API 的请求和响应数据**
* **可从任意界面开始和停止录制、回放，绕过登录问题**
* 可自动对关键步骤截屏，方便对比回放与录制差异
* **可自动和手动选择 View 及触摸区域、贴靠方式等**
* 可保存录制步骤相关数据到后端数据库及从后端下载
* 可用管理端网页浏览检索用例和远程控制录制回放
* 附带 UnitAuto-机器学习零代码自动化单元测试
* 中文和英语双语文案，根据系统语言设置自动切换

### 特点优势
相比各种 UI 录制回放/自动化测试 的 其它平台/工具/框架：<br /><br />
1.它们录制过程各种别扭难用反人类，甚至还需要开发/维护用例脚本、每个用例都写一大堆代码频繁部署等；<br />
**UIGO 不需要写任何代码，录制几乎是按和人正常操作完全一样的方式，操作简单易用，录制回放快、准、稳！**<br />
<br />
2.它们很难兼容各种不同宽高比分辨率屏幕，16:9 屏幕录制最多只能较好地在 16:9 屏幕回放，即便手写代码也很难在列表项 ID 重复控件精准定位；<br />
**UIGO 则能很好地支持 16:9, 19.5:9 等各种不同屏幕录制，然后在 720P, 1080P, 1080X2340, 1440X2560, 1440X3200 等各种屏幕很好地精准回放。**<br />
<br />
3.它们要到处人为设置/调整操作步骤等待时间，还总是要么等太久、要么还没返回就过早执行下一步导致出错，因为很难保证网络请求在精准时间范围内返回结果；<br />
**UIGO 则会自动精准等待 App 发送的各种 HTTP API 网路请求，像专业的测试工程师一样精准高效地等待后端接口响应并执行 点击、长按、滑动、缩放 等每一步触屏操作！**<br />
<br />

### 原理说明
被测项目不需要写任何用例脚本代码(逻辑代码、注解代码、配置代码等全都不要)，<br />
UIGO 会自动录制 UI 触屏操作、虚拟+实体按键操作、HTTP API 网络请求与响应、<br />
Activity, Fragment, Dialog, PopupWindow 等各种组件(控件)元素的生命周期 等，<br />
回放时根据录制触摸点所在被分割球划分的 上、下、左、右、居中、等比 等区域<br />
以及 屏幕分辨率、状态栏高度、导航栏高度、键盘高度 等来自动计算出回放触摸点，<br />
再加上 id(如果有) 相同且距离最近的 View 区域来辅助微调，高度精准回放触屏操作！<br />
对 返回按键、键盘按键 甚至 输入框编辑过程的每个变化的字符 也都能精准无误地还原！<br />

<br />

### 示例项目
[UIGO Android 简单测试 App](https://github.com/TommyLemon/UIGO/tree/main/UIAuto-Android)    直接 [下载](https://github.com/TommyLemon/UIGO/releases/download/0.9.0/UIAuto.apk) （第一次可能失败，返回报错 JSON，一般重试一次就可以）<br />
[UIGO Android 复杂客户端 App](https://github.com/TommyLemon/UIGO/tree/main/APIJSONApp)    直接 [下载](https://github.com/TommyLemon/UIGO/releases/download/0.9.0/APIJSONApp.apk) （第一次可能失败，返回报错 JSON，一般重试一次就可以）

<br />

#### 早期零代码单机录制不同分辨率双机同时回放视频
https://www.bilibili.com/video/BV1CK4218788 <br />
<img width="1280" src="https://github.com/TommyLemon/UIGO/assets/5738175/3bb97384-72d9-4b45-ab2c-b0916291ef9f" href="https://www.bilibili.com/video/BV1CK4218788" />

#### 早期管理端网页工具零代码远程控制手机录制回放视频
https://www.bilibili.com/video/BV1wA4m137ha
<img width="1280" src="https://github.com/TommyLemon/UIGO/assets/5738175/e50d00a1-22e8-4908-9d88-579a178965f1" href="https://www.bilibili.com/video/BV1wA4m137ha" />

#### 早期仿微信朋友圈复杂 App 录制回放，弹窗、输入、网页、滑动、点击等
https://www.bilibili.com/video/BV1fH4y1E7gD
<img width="1280" src="https://github.com/TommyLemon/UIGO/assets/5738175/bed421fa-f1a9-47ea-a265-e34853b2d1c8" href="https://www.bilibili.com/video/BV1fH4y1E7gD" />

### 录制用例
1.按业务 App 提供的方式打开 UIAuto 管理首页，例如 APIJSONApp 是登录后点击首页标题，UIAuto-Android 是点击首页 [自动 UI 测试] 按钮 <br />

2.点击 Record 录制按钮 > 点击顶部悬浮长条中间的 Record 录制 按钮开始录制 > 正常操作 App > 完成一个用例过程后，点击半透明圆形 〇 悬浮球完成录制 <br />

3.(可选)点击右下角 post 按钮上传录制的操作和数据等到后端数据库，可先编辑底部的后端服务器 HTTP URL Host 地址为你自己部署的 APIJSON 后端服务 <br />

### 回放用例
1.参考 录制用例 1 来打开 UIAuto 管理首页 > 点击左下角 Remote 共享列表 按钮 > 点击打开其中一个和 App 及账号对应的用例，或者 录制用例 后直接进入用例详情界面 <br />

2.点击用例详情界面左下角 Replay 回放按钮，返回到用例对应开始录制的业务 App 界面，保证状态一致(都是刚进入后没有操作的界面，或者分页列表都下拉刷新过等) <br />

3.点击顶部悬浮长条中间的 Replay 回放 按钮开始回放，观察每步操作前后，App 的 UI 展示、界面数据、界面跳转、弹窗显示、键盘输入等是否符合预期(和录制时表现一样) <br />

<br />


### 快速上手

可先跳过这个步骤，先下载体验 App 安装包，安装后 按以下 录制用例、回放用例 文档来操作

#### 集成到被测项目 Android 客户端 App
##### 1.依赖 UnitAuto-Apk
把 [UnitAuto-Apk](https://github.com/TommyLemon/UIGO/tree/master/APIJSONApp/UnitAuto-Apk) 导入到你项目 [app moudule 所在目录](https://github.com/TommyLemon/UIGO/tree/master/UIAuto-Android)，[settings.gradle](https://github.com/TommyLemon/UIGO/tree/master/APIJSONApp/settings.gradle) 中
```groovy
include ':UnitAuto-Apk'
```
[app moudule 目录](https://github.com/TommyLemon/UIGO/tree/master/APIJSONApp/app)，[build.gradle](https://github.com/TommyLemon/UIGO/tree/master/APIJSONApp/app/build.gradle) 中
```groovy
dependencies {
    api project(':UnitAuto-Apk')
}
```
<br />

##### 2.依赖 UIAuto
把 [UIAuto](https://github.com/TommyLemon/UIGO/tree/master/APIJSONApp/UIAuto) 导入到你项目 [app moudule 所在目录](https://github.com/TommyLemon/UIGO/tree/master/APIJSONApp)，[settings.gradle](https://github.com/TommyLemon/UIGO/tree/master/APIJSONApp/settings.gradle) 中
```groovy
include ':UIAuto'
```
[app moudule 目录](https://github.com/TommyLemon/UIGO/tree/master/APIJSONApp/app)，[build.gradle](https://github.com/TommyLemon/UIGO/tree/master/APIJSONApp/app/build.gradle) 中
```groovy
dependencies {
    api project(':UIAuto')
}
```
<br />

##### 2.初始化 UIAuto
在 [Application onCreate 方法](https://github.com/TommyLemon/UIGO/blob/master/APIJSONApp/app/src/main/java/apijson/demo/application/DemoApplication.java) 中初始化
```java
    @Override
    public void onCreate() {
        super.onCreate();
        UIAutoApp.getInstance().initUIAuto(this);
    }
```
<br />

##### 3.提供 UIAuto 管理界面入口
在 [AndroidManifest.xml](https://github.com/TommyLemon/UIGO/blob/master/APIJSONApp/app/src/main/AndroidManifest.xml) 中注册 [UIAutoActivity](https://github.com/TommyLemon/UIGO/blob/master/APIJSONApp/UIAuto/src/main/java/uiauto/UIAutoActivity.java)
```xml
<manifest ... >
    <application ... >
      
        <activity
            android:name="uiauto.UIAutoActivity"
            android:label="@string/ui"
            android:windowSoftInputMode="adjustPan"
            android:configChanges="orientation|screenSize"
            android:screenOrientation="portrait"
            />
        <activity
            android:name="uiauto.UIAutoListActivity"
            android:label="@string/ui"
            android:windowSoftInputMode="adjustPan"
            android:screenOrientation="portrait"
            />

        <activity
            android:name="unitauto.apk.UnitAutoActivity"
            android:label="@string/unit"
            android:windowSoftInputMode="adjustPan"
            android:configChanges="orientation|screenSize"
            android:screenOrientation="userLandscape"
            />
      
     </application>
</manifest>
```

可在你项目的任何界面新增一个按钮或其它形式的入口，仅 DEBUG 模式下展示
```xml
    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:onClick="onClickUI"
        android:text="UIAutoActivity"
        android:textAllCaps="false"
        />
```
参考 [layout/main_tab_activity](https://github.com/TommyLemon/UIGO/blob/master/APIJSONApp/app/src/main/res/layout/main_tab_activity.xml) <br />
<br />
点击这个入口跳转到 [UIAutoActivity](https://github.com/TommyLemon/UIGO/blob/master/APIJSONApp/UIAuto/src/main/java/uiauto/UIAutoActivity.java)
```java
    public void onClickUI(View v) {
        startActivity(UIAutoActivity.createIntent(this));
    }
```
参考 [MainTabActivity](https://github.com/TommyLemon/UIGO/blob/master/APIJSONApp/app/src/main/java/apijson/demo/activity_fragment/MainTabActivity.java) <br />
<br />

##### 4.通知 HTTP API 请求与相应
在 HTTP API 发起请求处加上
```java
		UIAutoApp.getInstance().post(new Runnable() {
			@Override
			public void run() {
				UIAutoApp.getInstance().onHTTPEvent(
				  action, type, method, host, url, headerStr, reqBodyStr, null, activity, fragment
				);
			}
		});
```
参考：<br />
https://github.com/TommyLemon/UIGO/blob/master/APIJSONApp/app/src/main/java/apijson/demo/manager/HttpManager.java#L137-L147


在 HTTP API 响应结果处加上
```java
		UIAutoApp.getInstance().post(new Runnable() {
			@Override
			public void run() {
				UIAutoApp.getInstance().onHTTPEvent(
				  - action, respnseCode, method, host, url, headerStr, reqBodyStr, resBodyStr, null, activity, fragment
				);
			}
		});
```
参考：<br />
https://github.com/TommyLemon/UIGO/blob/master/APIJSONApp/app/src/main/java/apijson/demo/manager/HttpManager.java#L168-L179

<br />

##### 5.通知 Dialog, PopupWindow 显示和隐藏
**业务代码中如果使用了 android.app.AlertDialog，且录制回放用例涉及**，则可以换成 uiauto.AlertDialog， <br />
最简单的方式是顶部菜单 Edit > Find > Replace in files 全局搜索 import android.app.AlertDialog， <br />
然后点搜索弹窗右下角 Replace All 按钮批量改为 import uiauto.AlertDialog。 <br />
如果因为用了自定义或第三方通用 Base Alert Dialog 不方便替换，则可以在里面加上：

```java
	@Override
	public void show() {
		super.show();
		UIAutoApp.getInstance().onUIAutoWindowCreate(this, getWindow()); // 通知已显示
	}

	// 拦截隐藏事件监听
	private OnDismissListener listener;
	@Override
	public void setOnDismissListener(OnDismissListener listener) {
		this.listener = listener;
	}

	private Activity context;
	private void init(Context ctx) {
		this.context = (Activity) ctx;

		super.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (listener != null) {
					listener.onDismiss(dialog);
				}

				UIAutoApp.getInstance().onUIAutoWindowDestroy(AlertDialog.this, getWindow()); // 通知已隐藏
			}
		});
	}

	// 每个可重写的构造方法都保证调用到 init 方法
	public AlertDialog(Context context) {
		super(context);
		init(context);
	}

	public AlertDialog(Context context, @StyleRes int themeResId) {
		super(context, themeResId);
		init(context);
	}

	public AlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}

```

**所有 Dialog, DatePickerDialog, TimePickerDialog, ProgressDialog, CharacterPickerDialog 等也同上处理。** <br />

<br />

**业务代码中如果使用了 android.widget.PopupWindow，且录制回放用例涉及**，则可以换成 uiauto.PopupWindow， <br />
最简单的方式是顶部菜单 Edit > Find > Replace in files 全局搜索 import android.widget.PopupWindow， <br />
然后点搜索弹窗右下角 Replace All 按钮批量改为 import uiauto.PopupWindow。 <br />
如果因为用了自定义或第三方通用 Base Popup Window 不方便替换，则可以在里面加上：

```java
    private android.widget.PopupWindow popupWindow;
    private View view;

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);

        if (view == null) {
            try {
                Field field = android.widget.ListPopupWindow.class.getDeclaredField("mPopup");
                field.setAccessible(true);
                popupWindow = (android.widget.PopupWindow) field.get(this);
                //  popupWindow.setOutsideTouchable(false);

                Field dvField = android.widget.PopupWindow.class.getDeclaredField("mDecorView");
                dvField.setAccessible(true);
                view = (View) dvField.get(popupWindow);

                if (view == null) {
                    View cv = popupWindow.getContentView();
                    view = cv == null ? null : cv.getRootView();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        if (view == null) {
            View cv = getContentView();
            view = cv == null ? null : cv.getRootView();
        }

        Window w = getWindow();

        // 通知已显示
        UIAutoApp app = UIAutoApp.getInstance();
        app.onUIAutoWindowCreate(w.getCallback(), w);
        app.setCurrentPopupWindow(popupWindow, view, null, context, null);
    }


    // 拦截隐藏事件监听
    private android.widget.PopupWindow.OnDismissListener listener;
    @Override
    public void setOnDismissListener(android.widget.PopupWindow.OnDismissListener listener) {
        this.listener = listener;
    }

    public Window getWindow() {
        Activity ctx = view == null ? null : (Activity) view.getContext();
        if (ctx == null) {
            ctx = context;
        }
        return ctx.getWindow();
    }

    private Activity context;
    private void init(Context ctx) {
        this.context = (Activity) ctx;

        super.setOnDismissListener(new android.widget.PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (listener != null) {
                    listener.onDismiss();
                }

                Window w = getWindow();

                // 通知已隐藏
                UIAutoApp app = UIAutoApp.getInstance();
                app.onUIAutoWindowDestroy(w.getCallback(), w);
                app.setCurrentPopupWindow(null, null, null, context, null);
            }
        });
    }

    // 每个可重写的构造方法都保证调用到 init 方法
    public PopupWindow(Context context) {
        super(context);
        init(context);
    }

    public PopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PopupWindow(Context context, AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public PopupWindow(Context context, AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public PopupWindow(Context context, View contentView) {
        super(contentView);
        init(context);
    }

    public PopupWindow(Context context, int width, int height) {
        super(width, height);
        init(context);
    }

    public PopupWindow(Context context, View contentView, int width, int height) {
        super(contentView, width, height);
        init(context);
    }

    public PopupWindow(Context context, View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        init(context);
    }

```

**所有 AppCompatPopupWindow, ListPopupWindow 等也同上处理。** <br />
<br />

#### Java 后端 Server
可先跳过，使用 http://apijson.cn:8080 或 http://apijson.cn:9090 代替 <br />

见 APIJSON-Demo 后端上手 <br />
https://github.com/APIJSON/APIJSON-Demo?tab=readme-ov-file#1%E5%90%8E%E7%AB%AF%E4%B8%8A%E6%89%8B

<br />

### 录制、回放用例
见以上 [录制用例](https://github.com/TommyLemon/UIGO/tree/master?tab=readme-ov-file#%E5%BD%95%E5%88%B6%E7%94%A8%E4%BE%8B)、[回放用例](https://github.com/TommyLemon/UIGO/tree/master?tab=readme-ov-file#%E5%9B%9E%E6%94%BE%E7%94%A8%E4%BE%8B) 的说明。

<br /><br />

### 常见问题
#### 1.apijson.cn 访问不了
托管服务地址改为 http://47.98.196.224:8080  <br />
https://github.com/TommyLemon/APIAuto/issues/13
 

<br />
  
更多常见问题 <br />
https://github.com/TommyLemon/APIAuto/issues

<br />


### 技术交流
##### 关于作者
[https://github.com/TommyLemon](https://github.com/TommyLemon)<br />
<img width="1280" src="https://github.com/TommyLemon/UIGO/assets/5738175/ec77df98-ff9b-43aa-b2f1-2fce2549d276">

如果有什么问题或建议可以 [去 APIAuto 提 issue](https://github.com/TommyLemon/APIAuto/issues)，交流技术，分享经验。<br >
如果你解决了某些 bug，或者新增了一些功能，欢迎 [提 PR 贡献代码](https://github.com/Tencent/APIJSON/blob/master/CONTRIBUTING.md)，感激不尽。
<br />
<br />

### 其它项目

[APIJSON](https://github.com/Tencent/APIJSON) 🚀 腾讯零代码、全功能、强安全 ORM 库 🏆 后端接口和文档零代码，前端(客户端) 定制返回 JSON 的数据和结构

[APIAuto](https://github.com/TommyLemon/APIAuto) 敏捷开发最强大易用的 HTTP 接口工具，机器学习零代码测试、生成代码与静态检查、生成文档与光标悬浮注释，集 文档、测试、Mock、调试、管理 于一体的一站式体验

[UnitAuto](https://github.com/TommyLemon/UnitAuto) 机器学习零代码单元测试平台，零代码、全方位、自动化 测试 方法/函数 的正确性、可用性和性能

[SQLAuto](https://github.com/TommyLemon/SQLAuto) 智能零代码自动化测试 SQL 语句执行结果的数据库工具，任意增删改查、任意 SQL 模板变量、一键批量生成参数组合、快速构造大量测试数据

[Android-ZBLibrary](https://github.com/TommyLemon/Android-ZBLibrary) Android MVP 快速开发框架，Demo 全面，注释详细，使用简单，代码严谨


### 持续更新
[https://github.com/TommyLemon/UIGO/commits/master](https://github.com/TommyLemon/UIGO/commits/master)

### 我要赞赏
创作不易、坚持更难，右上角点 ⭐ Star 支持下本项目吧，谢谢 ^_^ <br />
[https://gitee.com/TommyLemon/UIGO](https://gitee.com/TommyLemon/UIGO)
<br />
<br />
