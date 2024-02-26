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

/**通用对话框类
 * @author Lemon
 * @use 把业务代码中 android.app.ProgressDialog 换成 uiauto.ProgressDialog
 */
@Deprecated
public class ProgressDialog extends android.app.ProgressDialog {
	//	private static final String TAG = "ProgressDialog";

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

				UIAutoApp.getInstance().onUIAutoDialogDismiss(ProgressDialog.this);
			}
		});
	}

	private OnDismissListener listener;
	@Override
	public void setOnDismissListener(OnDismissListener listener) {
		this.listener = listener;
	}


	/**
	 * Creates a Progress dialog.
	 *
	 * @param context the parent context
	 */
	public ProgressDialog(Context context) {
		super(context);
		init(context);
	}

	/**
	 * Creates a Progress dialog.
	 *
	 * @param context the parent context
	 * @param theme the resource ID of the theme against which to inflate
	 *              this dialog, or {@code 0} to use the parent
	 *              {@code context}'s default alert dialog theme
	 */
	public ProgressDialog(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	/**
	 * Creates and shows a ProgressDialog.
	 *
	 * @param context the parent context
	 * @param title the title text for the dialog's window
	 * @param message the text to be displayed in the dialog
	 * @return the ProgressDialog
	 */
	public static ProgressDialog show(Context context, CharSequence title,
												  CharSequence message) {
		return show(context, title, message, false);
	}

	/**
	 * Creates and shows a ProgressDialog.
	 *
	 * @param context the parent context
	 * @param title the title text for the dialog's window
	 * @param message the text to be displayed in the dialog
	 * @param indeterminate true if the dialog should be {@link #setIndeterminate(boolean)
	 *        indeterminate}, false otherwise
	 * @return the ProgressDialog
	 */
	public static ProgressDialog show(Context context, CharSequence title,
												  CharSequence message, boolean indeterminate) {
		return show(context, title, message, indeterminate, false, null);
	}

	/**
	 * Creates and shows a ProgressDialog.
	 *
	 * @param context the parent context
	 * @param title the title text for the dialog's window
	 * @param message the text to be displayed in the dialog
	 * @param indeterminate true if the dialog should be {@link #setIndeterminate(boolean)
	 *        indeterminate}, false otherwise
	 * @param cancelable true if the dialog is {@link #setCancelable(boolean) cancelable},
	 *        false otherwise
	 * @return the ProgressDialog
	 */
	public static ProgressDialog show(Context context, CharSequence title,
												  CharSequence message, boolean indeterminate, boolean cancelable) {
		return show(context, title, message, indeterminate, cancelable, null);
	}

	/**
	 * Creates and shows a ProgressDialog.
	 *
	 * @param context the parent context
	 * @param title the title text for the dialog's window
	 * @param message the text to be displayed in the dialog
	 * @param indeterminate true if the dialog should be {@link #setIndeterminate(boolean)
	 *        indeterminate}, false otherwise
	 * @param cancelable true if the dialog is {@link #setCancelable(boolean) cancelable},
	 *        false otherwise
	 * @param cancelListener the {@link #setOnCancelListener(OnCancelListener) listener}
	 *        to be invoked when the dialog is canceled
	 * @return the ProgressDialog
	 */
	public static ProgressDialog show(Context context, CharSequence title,
												  CharSequence message, boolean indeterminate,
												  boolean cancelable, OnCancelListener cancelListener) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setIndeterminate(indeterminate);
		dialog.setCancelable(cancelable);
		dialog.setOnCancelListener(cancelListener);
		dialog.show();
		return dialog;
	}

}

