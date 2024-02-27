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

import android.graphics.Rect;
import android.text.Layout;
import android.widget.TextView;

/**TextView 工具类
 * @author Lemon
 */
public class TextViewUtil {

    /**获取坐标对应的光标位置
     * @param tv
     * @param x
     * @param y
     * @return
     */
    public static int getTouchIndex(TextView tv, int x, int y) {
        String txt = tv == null ? null : StringUtil.getString(tv);
        int len = txt == null ? 0 : txt.length();

        for (int i = 0; i < len; i++) {
            Rect rect = getSelectionRect(tv, i);
            if (rect != null && x < rect.right && x > rect.left && y < rect.bottom && y > rect.top) {
                return i;
            }
        }

        return -1;
    }

    /**获取坐标对应的字符
     * @param tv
     * @param x
     * @param y
     * @return
     */
    public static String getSelectionByTouch(TextView tv, int x, int y) {
        int i = getTouchIndex(tv, x, y);
        String txt = i < 0 ? null : StringUtil.getString(tv);
        return txt == null || i >= txt.length() ? null : txt.substring(i, i + 1);
    }


    /**获取某一个字符的屏幕显示边界
     * @param tv
     * @param index
     * @return
     */
    public static Rect getSelectionRect(TextView tv, int index) {
        String txt = index < 0 || tv == null ? null : StringUtil.getString(tv);
        int len = txt == null ? 0 : txt.length();
        if (len <= 0 || index > len - 1) {
            return null;
        }

        Layout layout = tv.getLayout();
        Rect bound = new Rect();
        int line = layout.getLineForOffset(index);
        layout.getLineBounds(line, bound);

        int top = bound.top;
        int bottom = bound.bottom;
        int left = Math.round(layout.getPrimaryHorizontal(index));
        int right = Math.round(layout.getSecondaryHorizontal(index));

        if (left == right) { // 某些情况下相等时用字符宽度解决
            String s = txt.substring(index, index + 1);
            right = right + (int) tv.getPaint().measureText(s);
        }

        int sy = tv.getScrollY();
        return new Rect(left, top + sy, right, bottom + sy);
    }

}