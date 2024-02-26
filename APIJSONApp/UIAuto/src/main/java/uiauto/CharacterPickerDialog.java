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
import android.text.Editable;
import android.view.View;

/**通用对话框类
 * @author Lemon
 * @use 把业务代码中 android.text.method.CharacterPickerDialog 换成 uiauto.CharacterPickerDialog
 */
public class CharacterPickerDialog extends android.text.method.CharacterPickerDialog {
	//	private static final String TAG = "CharacterPickerDialog";

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

				UIAutoApp.getInstance().onUIAutoDialogDismiss(CharacterPickerDialog.this);
			}
		});
	}

	private OnDismissListener listener;
	@Override
	public void setOnDismissListener(OnDismissListener listener) {
		this.listener = listener;
	}


	/**
	 * Creates a new CharacterPickerDialog that presents the specified
	 * <code>options</code> for insertion or replacement (depending on
	 * the sense of <code>insert</code>) into <code>text</code>.
	 */
	public CharacterPickerDialog(Context context, View view,
								 Editable text, String options,
								 boolean insert) {
		super(context, view, text, options, insert);
		init(context);
	}

}

