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

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class FloatBallView extends android.support.v7.widget.AppCompatImageView {

  private View.OnTouchListener extraOnTouchListener;
  public FloatBallView setExtraOnTouchListener(View.OnTouchListener extraOnTouchListener) {
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
  public void setOnTouchListener(View.OnTouchListener l) {
    super.setOnTouchListener(new View.OnTouchListener() {
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
