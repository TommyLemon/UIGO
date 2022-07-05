package apijson.demo.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class FloatBallView extends android.support.v7.widget.AppCompatImageView {

  private OnTouchListener extraOnTouchListener;
  public FloatBallView setExtraOnTouchListener(OnTouchListener extraOnTouchListener) {
    this.extraOnTouchListener = extraOnTouchListener;
    return this;
  }

  public FloatBallView(Context context) {
    super(context);
  }

  public FloatBallView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public FloatBallView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  // 无效
  // @Override
  // public boolean onTouchEvent(MotionEvent event) {
  //   if (extraOnTouchListener != null) {
  //     extraOnTouchListener.onTouch(this, event);
  //   }
  //   return super.onTouchEvent(event);
  // }

  @Override
  public void setOnTouchListener(OnTouchListener l) {
    super.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (extraOnTouchListener != null) {
          extraOnTouchListener.onTouch(v, event);
        }
        return l.onTouch(v, event);
      }
    });
  }
}
