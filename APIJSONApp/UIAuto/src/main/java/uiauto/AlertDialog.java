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
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.annotation.ArrayRes;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**通用对话框类
 * @author Lemon
 * @use 把业务代码中 android.app.AlertDialog 换成 uiauto.AlertDialog
 */
public class AlertDialog extends android.app.AlertDialog {
	//	private static final String TAG = "AlertDialog";

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

				UIAutoApp.getInstance().onUIAutoDialogDismiss(AlertDialog.this);
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
	public AlertDialog(Context context) {
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
	public AlertDialog(Context context, @StyleRes int themeResId) {
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

	public AlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}

	public static class Builder extends android.app.AlertDialog.Builder {



		/**
		 * Creates a builder for an alert dialog that uses the default alert
		 * dialog theme.
		 * <p>
		 * The default alert dialog theme is defined by
		 * {@link android.R.attr#alertDialogTheme} within the parent
		 * {@code context}'s theme.
		 *
		 * @param context the parent context
		 */
		public Builder(Context context) {
			super(context);
		}

		/**
		 * Creates a builder for an alert dialog that uses an explicit theme
		 * resource.
		 * <p>
		 * The specified theme resource ({@code themeResId}) is applied on top
		 * of the parent {@code context}'s theme. It may be specified as a
		 * style resource containing a fully-populated theme, such as
		 * {@link android.R.style#Theme_Material_Dialog}, to replace all
		 * attributes in the parent {@code context}'s theme including primary
		 * and accent colors.
		 * <p>
		 * To preserve attributes such as primary and accent colors, the
		 * {@code themeResId} may instead be specified as an overlay theme such
		 * as {@link android.R.style#ThemeOverlay_Material_Dialog}. This will
		 * override only the window attributes necessary to style the alert
		 * window as a dialog.
		 * <p>
		 * Alternatively, the {@code themeResId} may be specified as {@code 0}
		 * to use the parent {@code context}'s resolved value for
		 * {@link android.R.attr#alertDialogTheme}.
		 *
		 * @param context the parent context
		 * @param themeResId the resource ID of the theme against which to inflate
		 *                   this dialog, or {@code 0} to use the parent
		 *                   {@code context}'s default alert dialog theme
		 */
		public Builder(Context context, int themeResId) {
			super(context, themeResId);
		}

		private CharSequence mTitle;
		/**
		 * Set the title using the given resource id.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public Builder setTitle(@StringRes int titleId) {
			mTitle = getContext().getText(titleId);
			return this;
		}

		/**
		 * Set the title displayed in the {@link Dialog}.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setTitle(CharSequence title) {
			mTitle = title;
			return this;
		}

		private View mCustomTitleView;
		/**
		 * Set the title using the custom view {@code customTitleView}.
		 * <p>
		 * The methods {@link #setTitle(int)} and {@link #setIcon(int)} should
		 * be sufficient for most titles, but this is provided if the title
		 * needs more customization. Using this will replace the title and icon
		 * set via the other methods.
		 * <p>
		 * <strong>Note:</strong> To ensure consistent styling, the custom view
		 * should be inflated or constructed using the alert dialog's themed
		 * context obtained via {@link #getContext()}.
		 *
		 * @param customTitleView the custom view to use as the title
		 * @return this Builder object to allow for chaining of calls to set
		 *         methods
		 */
		public android.app.AlertDialog.Builder setCustomTitle(View customTitleView) {
			mCustomTitleView = customTitleView;
			return this;
		}

		private CharSequence mMessage;
		/**
		 * Set the message to display using the given resource id.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setMessage(@StringRes int messageId) {
			mMessage = getContext().getText(messageId);
			return this;
		}

		/**
		 * Set the message to display.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setMessage(CharSequence message) {
			mMessage = message;
			return this;
		}

		private int mIconId;
		/**
		 * Set the resource id of the {@link Drawable} to be used in the title.
		 * <p>
		 * Takes precedence over values set using {@link #setIcon(Drawable)}.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setIcon(@DrawableRes int iconId) {
			mIconId = iconId;
			return this;
		}

		private Drawable mIcon;
		/**
		 * Set the {@link Drawable} to be used in the title.
		 * <p>
		 * <strong>Note:</strong> To ensure consistent styling, the drawable
		 * should be inflated or constructed using the alert dialog's themed
		 * context obtained via {@link #getContext()}.
		 *
		 * @return this Builder object to allow for chaining of calls to set
		 *         methods
		 */
		public android.app.AlertDialog.Builder setIcon(Drawable icon) {
			mIcon = icon;
			return this;
		}

		/**
		 * Set an icon as supplied by a theme attribute. e.g.
		 * {@link android.R.attr#alertDialogIcon}.
		 * <p>
		 * Takes precedence over values set using {@link #setIcon(int)} or
		 * {@link #setIcon(Drawable)}.
		 *
		 * @param attrId ID of a theme attribute that points to a drawable resource.
		 */
		public android.app.AlertDialog.Builder setIconAttribute(@AttrRes int attrId) {
			TypedValue out = new TypedValue();
			getContext().getTheme().resolveAttribute(attrId, out, true);
			mIconId = out.resourceId;
			return this;
		}

		private CharSequence mPositiveButtonText;
		private OnClickListener mPositiveButtonListener;
		/**
		 * Set a listener to be invoked when the positive button of the dialog is pressed.
		 * @param textId The resource id of the text to display in the positive button
		 * @param listener The {@link DialogInterface.OnClickListener} to use.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setPositiveButton(@StringRes int textId, final OnClickListener listener) {
			mPositiveButtonText = getContext().getText(textId);
			mPositiveButtonListener = listener;
			return this;
		}

		/**
		 * Set a listener to be invoked when the positive button of the dialog is pressed.
		 * @param text The text to display in the positive button
		 * @param listener The {@link DialogInterface.OnClickListener} to use.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setPositiveButton(CharSequence text, final OnClickListener listener) {
			mPositiveButtonText = text;
			mPositiveButtonListener = listener;
			return this;
		}

		private CharSequence mNegativeButtonText;
		private OnClickListener mNegativeButtonListener;
		/**
		 * Set a listener to be invoked when the negative button of the dialog is pressed.
		 * @param textId The resource id of the text to display in the negative button
		 * @param listener The {@link DialogInterface.OnClickListener} to use.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setNegativeButton(@StringRes int textId, final OnClickListener listener) {
			mNegativeButtonText = getContext().getText(textId);
			mNegativeButtonListener = listener;
			return this;
		}

		/**
		 * Set a listener to be invoked when the negative button of the dialog is pressed.
		 * @param text The text to display in the negative button
		 * @param listener The {@link DialogInterface.OnClickListener} to use.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setNegativeButton(CharSequence text, final OnClickListener listener) {
			mNegativeButtonText = text;
			mNegativeButtonListener = listener;
			return this;
		}

		private CharSequence mNeutralButtonText;
		private OnClickListener mNeutralButtonListener;
		/**
		 * Set a listener to be invoked when the neutral button of the dialog is pressed.
		 * @param textId The resource id of the text to display in the neutral button
		 * @param listener The {@link DialogInterface.OnClickListener} to use.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setNeutralButton(@StringRes int textId, final OnClickListener listener) {
			mNeutralButtonText = getContext().getText(textId);
			mNeutralButtonListener = listener;
			return this;
		}

		/**
		 * Set a listener to be invoked when the neutral button of the dialog is pressed.
		 * @param text The text to display in the neutral button
		 * @param listener The {@link DialogInterface.OnClickListener} to use.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setNeutralButton(CharSequence text, final OnClickListener listener) {
			mNeutralButtonText = text;
			mNeutralButtonListener = listener;
			return this;
		}

		private boolean mCancelable;
		/**
		 * Sets whether the dialog is cancelable or not.  Default is true.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setCancelable(boolean cancelable) {
			mCancelable = cancelable;
			return this;
		}

		private OnCancelListener mOnCancelListener;
		/**
		 * Sets the callback that will be called if the dialog is canceled.
		 *
		 * <p>Even in a cancelable dialog, the dialog may be dismissed for reasons other than
		 * being canceled or one of the supplied choices being selected.
		 * If you are interested in listening for all cases where the dialog is dismissed
		 * and not just when it is canceled, see
		 * {@link #setOnDismissListener(android.content.DialogInterface.OnDismissListener) setOnDismissListener}.</p>
		 * @see #setCancelable(boolean)
		 * @see #setOnDismissListener(android.content.DialogInterface.OnDismissListener)
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setOnCancelListener(OnCancelListener onCancelListener) {
			mOnCancelListener = onCancelListener;
			return this;
		}

		private OnDismissListener mOnDismissListener;
		/**
		 * Sets the callback that will be called when the dialog is dismissed for any reason.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setOnDismissListener(OnDismissListener onDismissListener) {
			mOnDismissListener = onDismissListener;
			return this;
		}

		private OnKeyListener mOnKeyListener;
		/**
		 * Sets the callback that will be called if a key is dispatched to the dialog.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setOnKeyListener(OnKeyListener onKeyListener) {
			mOnKeyListener = onKeyListener;
			return this;
		}

		private CharSequence[] mItems;
		private OnClickListener mOnClickListener;
		/**
		 * Set a list of items to be displayed in the dialog as the content, you will be notified of the
		 * selected item via the supplied listener. This should be an array type i.e. R.array.foo
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setItems(@ArrayRes int itemsId, final OnClickListener listener) {
			mItems = getContext().getResources().getTextArray(itemsId);
			mOnClickListener = listener;
			return this;
		}

		/**
		 * Set a list of items to be displayed in the dialog as the content, you will be notified of the
		 * selected item via the supplied listener.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setItems(CharSequence[] items, final OnClickListener listener) {
			mItems = items;
			mOnClickListener = listener;
			return this;
		}

		private ListAdapter mAdapter;
		/**
		 * Set a list of items, which are supplied by the given {@link ListAdapter}, to be
		 * displayed in the dialog as the content, you will be notified of the
		 * selected item via the supplied listener.
		 *
		 * @param adapter The {@link ListAdapter} to supply the list of items
		 * @param listener The listener that will be called when an item is clicked.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setAdapter(final ListAdapter adapter, final OnClickListener listener) {
			mAdapter = adapter;
			mOnClickListener = listener;
			return this;
		}

		private Cursor mCursor;
		private String mLabelColumn;
		/**
		 * Set a list of items, which are supplied by the given {@link Cursor}, to be
		 * displayed in the dialog as the content, you will be notified of the
		 * selected item via the supplied listener.
		 *
		 * @param cursor The {@link Cursor} to supply the list of items
		 * @param listener The listener that will be called when an item is clicked.
		 * @param labelColumn The column name on the cursor containing the string to display
		 *          in the label.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setCursor(final Cursor cursor, final OnClickListener listener,
														 String labelColumn) {
			mCursor = cursor;
			mLabelColumn = labelColumn;
			mOnClickListener = listener;
			return this;
		}

		private boolean mIsMultiChoice;
		private boolean[] mCheckedItems;
		private OnMultiChoiceClickListener mOnCheckboxClickListener;
		/**
		 * Set a list of items to be displayed in the dialog as the content,
		 * you will be notified of the selected item via the supplied listener.
		 * This should be an array type, e.g. R.array.foo. The list will have
		 * a check mark displayed to the right of the text for each checked
		 * item. Clicking on an item in the list will not dismiss the dialog.
		 * Clicking on a button will dismiss the dialog.
		 *
		 * @param itemsId the resource id of an array i.e. R.array.foo
		 * @param checkedItems specifies which items are checked. It should be null in which case no
		 *        items are checked. If non null it must be exactly the same length as the array of
		 *        items.
		 * @param listener notified when an item on the list is clicked. The dialog will not be
		 *        dismissed when an item is clicked. It will only be dismissed if clicked on a
		 *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setMultiChoiceItems(@ArrayRes int itemsId, boolean[] checkedItems,
																   final OnMultiChoiceClickListener listener) {
			mItems = getContext().getResources().getTextArray(itemsId);
			mOnCheckboxClickListener = listener;
			mCheckedItems = checkedItems;
			mIsMultiChoice = true;
			return this;
		}

		/**
		 * Set a list of items to be displayed in the dialog as the content,
		 * you will be notified of the selected item via the supplied listener.
		 * The list will have a check mark displayed to the right of the text
		 * for each checked item. Clicking on an item in the list will not
		 * dismiss the dialog. Clicking on a button will dismiss the dialog.
		 *
		 * @param items the text of the items to be displayed in the list.
		 * @param checkedItems specifies which items are checked. It should be null in which case no
		 *        items are checked. If non null it must be exactly the same length as the array of
		 *        items.
		 * @param listener notified when an item on the list is clicked. The dialog will not be
		 *        dismissed when an item is clicked. It will only be dismissed if clicked on a
		 *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems,
																   final OnMultiChoiceClickListener listener) {
			mItems = items;
			mOnCheckboxClickListener = listener;
			mCheckedItems = checkedItems;
			mIsMultiChoice = true;
			return this;
		}

		private String mIsCheckedColumn;
		/**
		 * Set a list of items to be displayed in the dialog as the content,
		 * you will be notified of the selected item via the supplied listener.
		 * The list will have a check mark displayed to the right of the text
		 * for each checked item. Clicking on an item in the list will not
		 * dismiss the dialog. Clicking on a button will dismiss the dialog.
		 *
		 * @param cursor the cursor used to provide the items.
		 * @param isCheckedColumn specifies the column name on the cursor to use to determine
		 *        whether a checkbox is checked or not. It must return an integer value where 1
		 *        means checked and 0 means unchecked.
		 * @param labelColumn The column name on the cursor containing the string to display in the
		 *        label.
		 * @param listener notified when an item on the list is clicked. The dialog will not be
		 *        dismissed when an item is clicked. It will only be dismissed if clicked on a
		 *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setMultiChoiceItems(Cursor cursor, String isCheckedColumn, String labelColumn,
																   final OnMultiChoiceClickListener listener) {
			mCursor = cursor;
			mOnCheckboxClickListener = listener;
			mIsCheckedColumn = isCheckedColumn;
			mLabelColumn = labelColumn;
			mIsMultiChoice = true;
			return this;
		}

		private int mCheckedItem;
		private boolean mIsSingleChoice;
		/**
		 * Set a list of items to be displayed in the dialog as the content, you will be notified of
		 * the selected item via the supplied listener. This should be an array type i.e.
		 * R.array.foo The list will have a check mark displayed to the right of the text for the
		 * checked item. Clicking on an item in the list will not dismiss the dialog. Clicking on a
		 * button will dismiss the dialog.
		 *
		 * @param itemsId the resource id of an array i.e. R.array.foo
		 * @param checkedItem specifies which item is checked. If -1 no items are checked.
		 * @param listener notified when an item on the list is clicked. The dialog will not be
		 *        dismissed when an item is clicked. It will only be dismissed if clicked on a
		 *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setSingleChoiceItems(@ArrayRes int itemsId, int checkedItem,
																	final OnClickListener listener) {
			mItems = getContext().getResources().getTextArray(itemsId);
			mOnClickListener = listener;
			mCheckedItem = checkedItem;
			mIsSingleChoice = true;
			return this;
		}

		/**
		 * Set a list of items to be displayed in the dialog as the content, you will be notified of
		 * the selected item via the supplied listener. The list will have a check mark displayed to
		 * the right of the text for the checked item. Clicking on an item in the list will not
		 * dismiss the dialog. Clicking on a button will dismiss the dialog.
		 *
		 * @param cursor the cursor to retrieve the items from.
		 * @param checkedItem specifies which item is checked. If -1 no items are checked.
		 * @param labelColumn The column name on the cursor containing the string to display in the
		 *        label.
		 * @param listener notified when an item on the list is clicked. The dialog will not be
		 *        dismissed when an item is clicked. It will only be dismissed if clicked on a
		 *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setSingleChoiceItems(Cursor cursor, int checkedItem, String labelColumn,
																	final OnClickListener listener) {
			mCursor = cursor;
			mOnClickListener = listener;
			mCheckedItem = checkedItem;
			mLabelColumn = labelColumn;
			mIsSingleChoice = true;
			return this;
		}

		/**
		 * Set a list of items to be displayed in the dialog as the content, you will be notified of
		 * the selected item via the supplied listener. The list will have a check mark displayed to
		 * the right of the text for the checked item. Clicking on an item in the list will not
		 * dismiss the dialog. Clicking on a button will dismiss the dialog.
		 *
		 * @param items the items to be displayed.
		 * @param checkedItem specifies which item is checked. If -1 no items are checked.
		 * @param listener notified when an item on the list is clicked. The dialog will not be
		 *        dismissed when an item is clicked. It will only be dismissed if clicked on a
		 *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setSingleChoiceItems(CharSequence[] items, int checkedItem, final OnClickListener listener) {
			mItems = items;
			mOnClickListener = listener;
			mCheckedItem = checkedItem;
			mIsSingleChoice = true;
			return this;
		}

		/**
		 * Set a list of items to be displayed in the dialog as the content, you will be notified of
		 * the selected item via the supplied listener. The list will have a check mark displayed to
		 * the right of the text for the checked item. Clicking on an item in the list will not
		 * dismiss the dialog. Clicking on a button will dismiss the dialog.
		 *
		 * @param adapter The {@link ListAdapter} to supply the list of items
		 * @param checkedItem specifies which item is checked. If -1 no items are checked.
		 * @param listener notified when an item on the list is clicked. The dialog will not be
		 *        dismissed when an item is clicked. It will only be dismissed if clicked on a
		 *        button, if no buttons are supplied it's up to the user to dismiss the dialog.
		 *
		 * @return This Builder object to allow for chaining of calls to set methods
		 */
		public android.app.AlertDialog.Builder setSingleChoiceItems(ListAdapter adapter, int checkedItem, final OnClickListener listener) {
			mAdapter = adapter;
			mOnClickListener = listener;
			mCheckedItem = checkedItem;
			mIsSingleChoice = true;
			return this;
		}

		private AdapterView.OnItemSelectedListener mOnItemSelectedListener;
		/**
		 * Sets a listener to be invoked when an item in the list is selected.
		 *
		 * @param listener the listener to be invoked
		 * @return this Builder object to allow for chaining of calls to set methods
		 * @see AdapterView#setOnItemSelectedListener(android.widget.AdapterView.OnItemSelectedListener)
		 */
		public android.app.AlertDialog.Builder setOnItemSelectedListener(final AdapterView.OnItemSelectedListener listener) {
			mOnItemSelectedListener = listener;
			return this;
		}

		private View mView;
		private int mViewLayoutResId;
		private boolean mViewSpacingSpecified;
		/**
		 * Set a custom view resource to be the contents of the Dialog. The
		 * resource will be inflated, adding all top-level views to the screen.
		 *
		 * @param layoutResId Resource ID to be inflated.
		 * @return this Builder object to allow for chaining of calls to set
		 *         methods
		 */
		public android.app.AlertDialog.Builder setView(int layoutResId) {
			mView = null;
			mViewLayoutResId = layoutResId;
			mViewSpacingSpecified = false;
			return this;
		}

		/**
		 * Sets a custom view to be the contents of the alert dialog.
		 * <p>
		 * When using a pre-Holo theme, if the supplied view is an instance of
		 * a {@link ListView} then the light background will be used.
		 * <p>
		 * <strong>Note:</strong> To ensure consistent styling, the custom view
		 * should be inflated or constructed using the alert dialog's themed
		 * context obtained via {@link #getContext()}.
		 *
		 * @param view the view to use as the contents of the alert dialog
		 * @return this Builder object to allow for chaining of calls to set
		 *         methods
		 */
		public android.app.AlertDialog.Builder setView(View view) {
			mView = view;
			mViewLayoutResId = 0;
			mViewSpacingSpecified = false;
			return this;
		}

		private int mViewSpacingLeft;
		private int mViewSpacingTop;
		private int mViewSpacingRight;
		private int mViewSpacingBottom;
		/**
		 * Sets a custom view to be the contents of the alert dialog and
		 * specifies additional padding around that view.
		 * <p>
		 * When using a pre-Holo theme, if the supplied view is an instance of
		 * a {@link ListView} then the light background will be used.
		 * <p>
		 * <strong>Note:</strong> To ensure consistent styling, the custom view
		 * should be inflated or constructed using the alert dialog's themed
		 * context obtained via {@link #getContext()}.
		 *
		 * @param view the view to use as the contents of the alert dialog
		 * @param viewSpacingLeft spacing between the left edge of the view and
		 *                        the dialog frame
		 * @param viewSpacingTop spacing between the top edge of the view and
		 *                       the dialog frame
		 * @param viewSpacingRight spacing between the right edge of the view
		 *                         and the dialog frame
		 * @param viewSpacingBottom spacing between the bottom edge of the view
		 *                          and the dialog frame
		 * @return this Builder object to allow for chaining of calls to set
		 *         methods
		 *
		 * @hide Remove once the framework usages have been replaced.
		 * @deprecated Set the padding on the view itself.
		 */
		@Deprecated
		public android.app.AlertDialog.Builder setView(View view, int viewSpacingLeft, int viewSpacingTop,
													   int viewSpacingRight, int viewSpacingBottom) {
			mView = view;
			mViewLayoutResId = 0;
			mViewSpacingSpecified = true;
			mViewSpacingLeft = viewSpacingLeft;
			mViewSpacingTop = viewSpacingTop;
			mViewSpacingRight = viewSpacingRight;
			mViewSpacingBottom = viewSpacingBottom;
			return this;
		}

		private boolean mForceInverseBackground;
		/**
		 * Sets the alert dialog to use the inverse background, regardless of
		 * what the contents is.
		 *
		 * @param useInverseBackground whether to use the inverse background
		 * @return this Builder object to allow for chaining of calls to set methods
		 * @deprecated This flag is only used for pre-Material themes. Instead,
		 *             specify the window background using on the alert dialog
		 *             theme.
		 */
		@Deprecated
		public android.app.AlertDialog.Builder setInverseBackgroundForced(boolean useInverseBackground) {
			mForceInverseBackground = useInverseBackground;
			return this;
		}

		private boolean mRecycleOnMeasure;
		/**
		 * @hide
		 */
		public android.app.AlertDialog.Builder setRecycleOnMeasureEnabled(boolean enabled) {
			mRecycleOnMeasure = enabled;
			return this;
		}


		/**
		 * Creates an {@link android.app.AlertDialog} with the arguments supplied to this
		 * builder.
		 * <p>
		 * Calling this method does not display the dialog. If no additional
		 * processing is needed, {@link #show()} may be called instead to both
		 * create and display the dialog.
		 */
		public AlertDialog create() {
			// Context has already been wrapped with the appropriate theme.
			AlertDialog dialog = new AlertDialog(getContext());
			dialog.setTitle(mTitle);
			dialog.setMessage(mMessage);

			if (mViewLayoutResId > 0) {
				dialog.setContentView(mViewLayoutResId);
			}
			if (mView != null) {
				dialog.setContentView(mView);
			}

			dialog.setButton(BUTTON_POSITIVE, mPositiveButtonText, mPositiveButtonListener);
			dialog.setButton(BUTTON_NEGATIVE, mNegativeButtonText, mNegativeButtonListener);
			dialog.setButton(BUTTON_NEUTRAL, mNeutralButtonText, mNeutralButtonListener);

			dialog.setCancelable(mCancelable);
			if (mCancelable) {
				dialog.setCanceledOnTouchOutside(true);
			}
			dialog.setOnCancelListener(mOnCancelListener);
			dialog.setOnDismissListener(mOnDismissListener);
			if (mOnKeyListener != null) {
				dialog.setOnKeyListener(mOnKeyListener);
			}
			return dialog;
		}

		/**
		 * Creates an {@link AlertDialog} with the arguments supplied to this
		 * builder and immediately displays the dialog.
		 * <p>
		 * Calling this method is functionally identical to:
		 * <pre>
		 *     AlertDialog dialog = builder.create();
		 *     dialog.show();
		 * </pre>
		 */
		public AlertDialog show() {
			AlertDialog dialog = create();
			dialog.show();
			return dialog;
		}
	}

}

