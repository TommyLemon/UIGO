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
 * @use 把业务代码中 android.app.TimePickerDialog 换成 uiauto.TimePickerDialog
 */
public class TimePickerDialog extends android.app.TimePickerDialog {
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

				UIAutoApp.getInstance().onUIAutoDialogDismiss(TimePickerDialog.this);
			}
		});
	}

	private OnDismissListener listener;
	@Override
	public void setOnDismissListener(OnDismissListener listener) {
		this.listener = listener;
	}


	/**
	 * Creates a new time picker dialog.
	 *
	 * @param context the parent context
	 * @param listener the listener to call when the time is set
	 * @param hourOfDay the initial hour
	 * @param minute the initial minute
	 * @param is24HourView whether this is a 24 hour view or AM/PM
	 */
	public TimePickerDialog(Context context, OnTimeSetListener listener, int hourOfDay, int minute,
							boolean is24HourView) {
		super(context, listener, hourOfDay, minute, is24HourView);
		init(context);
	}

	/**
	 * Creates a new time picker dialog with the specified theme.
	 * <p>
	 * The theme is overlaid on top of the theme of the parent {@code context}.
	 * If {@code themeResId} is 0, the dialog will be inflated using the theme
	 * specified by the
	 * {@link android.R.attr#timePickerDialogTheme android:timePickerDialogTheme}
	 * attribute on the parent {@code context}'s theme.
	 *
	 * @param context the parent context
	 * @param themeResId the resource ID of the theme to apply to this dialog
	 * @param listener the listener to call when the time is set
	 * @param hourOfDay the initial hour
	 * @param minute the initial minute
	 * @param is24HourView Whether this is a 24 hour view, or AM/PM.
	 */
	public TimePickerDialog(Context context, int themeResId, OnTimeSetListener listener,
							int hourOfDay, int minute, boolean is24HourView) {
		super(context, themeResId, listener, hourOfDay, minute, is24HourView);
		init(context);
	}

}

