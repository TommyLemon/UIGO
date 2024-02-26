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

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StyleRes;

/**通用对话框类
 * @author Lemon
 * @use 把业务代码中 android.app.Dialog 换成 uiauto.Dialog
 */
public class Dialog extends android.app.Dialog {
	//	private static final String TAG = "Dialog";

	@Override
	public void show() {
		super.show();
		UIAutoApp.getInstance().onUIAutoDialogShow(this);
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

				UIAutoApp.getInstance().onUIAutoDialogDismiss(Dialog.this);
			}
		});
	}

	private OnDismissListener listener;
	@Override
	public void setOnDismissListener(OnDismissListener listener) {
		this.listener = listener;
	}


	/**
	 * Creates a dialog window that uses the default dialog theme.
	 * <p>
	 * The supplied {@code context} is used to obtain the window manager and
	 * base theme used to present the dialog.
	 *
	 * @param context the context in which the dialog should run
	 */
	public Dialog(Context context) {
		super(context);
		init(context);
	}

	/**
	 * Creates a dialog window that uses a custom dialog style.
	 * <p>
	 * The supplied {@code context} is used to obtain the window manager and
	 * base theme used to present the dialog.
	 * <p>
	 * The supplied {@code theme} is applied on top of the context's theme. See
	 * <a href="{@docRoot}guide/topics/resources/available-resources.html#stylesandthemes">
	 * Style and Theme Resources</a> for more information about defining and
	 * using styles.
	 *
	 * @param context the context in which the dialog should run
	 * @param themeResId a style resource describing the theme to use for the
	 *              window, or {@code 0} to use the default dialog theme
	 */
	public Dialog(Context context, @StyleRes int themeResId) {
		super(context, themeResId);
		init(context);
	}

//	/**
//	 * @deprecated
//	 * @hide
//	 */
//	@Deprecated
//	protected Dialog(Context context, boolean cancelable, Message cancelCallback) {
//		super(context, cancelable, cancelCallback);
//		init(context);
//	}

	protected Dialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}

}

