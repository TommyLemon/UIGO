package uiauto;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;

import java.lang.reflect.Field;

public class PopupWindow extends android.widget.PopupWindow {

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
//                popupWindow.setOutsideTouchable(false);

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

        UIAutoApp app = UIAutoApp.getInstance();
        app.onUIAutoWindowCreate(w.getCallback(), w);
        app.setCurrentPopupWindow(popupWindow, view, null, context, null);
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

                UIAutoApp app = UIAutoApp.getInstance();
                app.onUIAutoWindowDestroy(w.getCallback(), w);
                app.setCurrentPopupWindow(null, null, null, context, null);
            }
        });
    }

    private android.widget.PopupWindow.OnDismissListener listener;
    @Override
    public void setOnDismissListener(android.widget.PopupWindow.OnDismissListener listener) {
        this.listener = listener;
    }





    /**
     * Create a new, empty popup window capable of displaying items from a ListAdapter.
     * Backgrounds should be set using {@link #setBackgroundDrawable(Drawable)}.
     *
     * @param context Context used for contained views.
     */
    public PopupWindow(Context context) {
        super(context);
        init(context);
    }

    /**
     * Create a new, empty popup window capable of displaying items from a ListAdapter.
     * Backgrounds should be set using {@link #setBackgroundDrawable(Drawable)}.
     *
     * @param context Context used for contained views.
     * @param attrs Attributes from inflating parent views used to style the popup.
     */
    public PopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Create a new, empty popup window capable of displaying items from a ListAdapter.
     * Backgrounds should be set using {@link #setBackgroundDrawable(Drawable)}.
     *
     * @param context Context used for contained views.
     * @param attrs Attributes from inflating parent views used to style the popup.
     * @param defStyleAttr Default style attribute to use for popup content.
     */
    public PopupWindow(Context context, AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * Create a new, empty popup window capable of displaying items from a ListAdapter.
     * Backgrounds should be set using {@link #setBackgroundDrawable(Drawable)}.
     *
     * @param context Context used for contained views.
     * @param attrs Attributes from inflating parent views used to style the popup.
     * @param defStyleAttr Style attribute to read for default styling of popup content.
     * @param defStyleRes Style resource ID to use for default styling of popup content.
     */
    public PopupWindow(Context context, AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

//    /**
//     * <p>Create a new empty, non focusable popup window of dimension (0,0).</p>
//     *
//     * <p>The popup does not provide any background. This should be handled
//     * by the content view.</p>
//     */
//    public PopupWindow() {
//        super();
//    }

    /**
     * <p>Create a new non focusable popup window which can display the
     * <tt>contentView</tt>. The dimension of the window are (0,0).</p>
     *
     * <p>The popup does not provide any background. This should be handled
     * by the content view.</p>
     *
     * @param contentView the popup's content
     */
    public PopupWindow(Context context, View contentView) {
        super(contentView);
        init(context);
    }

    /**
     * <p>Create a new empty, non focusable popup window. The dimension of the
     * window must be passed to this constructor.</p>
     *
     * <p>The popup does not provide any background. This should be handled
     * by the content view.</p>
     *
     * @param width the popup's width
     * @param height the popup's height
     */
    public PopupWindow(Context context, int width, int height) {
        super(width, height);
        init(context);
    }

    /**
     * <p>Create a new non focusable popup window which can display the
     * <tt>contentView</tt>. The dimension of the window must be passed to
     * this constructor.</p>
     *
     * <p>The popup does not provide any background. This should be handled
     * by the content view.</p>
     *
     * @param contentView the popup's content
     * @param width the popup's width
     * @param height the popup's height
     */
    public PopupWindow(Context context, View contentView, int width, int height) {
        super(contentView, width, height);
        init(context);
    }

    /**
     * <p>Create a new popup window which can display the <tt>contentView</tt>.
     * The dimension of the window must be passed to this constructor.</p>
     *
     * <p>The popup does not provide any background. This should be handled
     * by the content view.</p>
     *
     * @param contentView the popup's content
     * @param width the popup's width
     * @param height the popup's height
     * @param focusable true if the popup can be focused, false otherwise
     */
    public PopupWindow(Context context, View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        init(context);
    }


}
