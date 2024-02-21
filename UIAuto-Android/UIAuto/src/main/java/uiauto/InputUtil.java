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

import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.util.Arrays;
import java.util.List;

public class InputUtil {

  public static final int EVENT_TYPE_TOUCH = 0;
  public static final int EVENT_TYPE_KEY = 1;
  public static final int EVENT_TYPE_UI = 2;
  public static final int EVENT_TYPE_HTTP = 3;

  public static final int LAYOUT_TYPE_DENSITY = 0;
  public static final int LAYOUT_TYPE_RATIO = 1;
  public static final int LAYOUT_TYPE_ABSOLUTE = 2;


  public static String getTouchActionName(int action) {
        String s = StringUtil.getTrimedString(MotionEvent.actionToString(action));
        return s.startsWith("ACTION_") ? s.substring("ACTION_".length()) : s;
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                return "DOWN";
//            case MotionEvent.ACTION_MOVE:
//                return "MOVE";
//            case MotionEvent.ACTION_SCROLL:
//                return "SCROLL";
//            case MotionEvent.ACTION_UP:
//                return "UP";
//            case MotionEvent.ACTION_MASK:
//                return "MASK";
//            case MotionEvent.ACTION_OUTSIDE:
//                return "OUTSIDE";
//            default:
//                return "CANCEL";
//        }
    }

    public static String getOrientationName(int orientation) {
        return orientation == Configuration.ORIENTATION_LANDSCAPE ? "HORIZONTAL" : "VERTICAL";
    }

    public static String getKeyActionName(int keyCode) {
        return getTouchActionName(keyCode);
    }
    public static String getKeyCodeName(int keyCode) {
        String s = StringUtil.getTrimedString(KeyEvent.keyCodeToString(keyCode));
        return s.startsWith("KEYCODE_") ? s.substring("KEYCODE_".length()) : s;
    }

    public static String getScanCodeName(int scanCode) {
        return "" + scanCode;  //它是 hardware key id  KeyEvent.keyCodeToString(scanCode);
    }


    public static final int HTTP_ACTION_REQUEST = 0;
    public static final int HTTP_ACTION_RESPONSE = 1;
    public static final int HTTP_ACTION_GET = 2;
    public static final int HTTP_ACTION_POST = 3;
    public static final int HTTP_ACTION_PUT = 4;
    public static final int HTTP_ACTION_DELETE = 5;
    public static final int HTTP_ACTION_HEAD = 6;
    public static final int HTTP_ACTION_OPTION = 7;
    public static final int HTTP_ACTION_TRACE = 8;
    public static final String HTTP_ACTION_REQUEST_NAME = "REQUEST";
    public static final String HTTP_ACTION_RESPONSE_NAME = "RESPONSE";
    public static final String HTTP_ACTION_GET_NAME = "GET";
    public static final String HTTP_ACTION_POST_NAME = "POST";
    public static final String HTTP_ACTION_PUT_NAME = "PUT";
    public static final String HTTP_ACTION_DELETE_NAME = "DELETE";
    public static final String HTTP_ACTION_HEAD_NAME = "HEAD";
    public static final String HTTP_ACTION_OPTION_NAME = "OPTION";
    public static final String HTTP_ACTION_TRACE_NAME = "TRACE";

    public static final String HTTP_HEADER_NAME = "HEADER";
    public static final String HTTP_CONTENT_NAME = "CONTENT";

    public static final String[] HTTP_ACTION_NAMES = new String[] {
            HTTP_ACTION_REQUEST_NAME, HTTP_ACTION_RESPONSE_NAME, HTTP_ACTION_GET_NAME, HTTP_ACTION_POST_NAME
            , HTTP_ACTION_PUT_NAME, HTTP_ACTION_DELETE_NAME, HTTP_ACTION_HEAD_NAME, HTTP_ACTION_OPTION_NAME
            , HTTP_ACTION_TRACE_NAME
    };
    public static final List<String> HTTP_ACTION_NAME_LIST = Arrays.asList(HTTP_ACTION_NAMES);

    public static int getHTTPActionCode(String action) {
        return HTTP_ACTION_NAME_LIST.indexOf(action);
    }
    public static String getHTTPActionName(int action) {
        return HTTP_ACTION_NAME_LIST.get(action > 0 ? action : -action);
    }



    public static final int UI_ACTION_ATTACH = 0;
    public static final int UI_ACTION_CREATE = 1;
    public static final int UI_ACTION_CREATE_VIEW = 2;
    public static final int UI_ACTION_ACTIVITY_CREATED = 3;
    public static final int UI_ACTION_START = 4;
    public static final int UI_ACTION_RESUME = 5;
    public static final int UI_ACTION_PAUSE = 6;
    public static final int UI_ACTION_STOP = 7;
    public static final int UI_ACTION_DESTROY_VIEW = 8;
    public static final int UI_ACTION_DESTROY = 9;
    public static final int UI_ACTION_DETACH = 10;
    public static final int UI_ACTION_RESTART = 11;
    public static final int UI_ACTION_PREATTACH = 12;
    public static final int UI_ACTION_PRECREATE = 13;

    public static final String UI_ACTION_ATTACH_NAME = "ATTACH";
    public static final String UI_ACTION_CREATE_NAME = "CREATE";
    public static final String UI_ACTION_CREATE_VIEW_NAME = "CREATE_VIEW";
    public static final String UI_ACTION_ACTIVITY_CREATED_NAME = "ACTIVITY_CREATED";
    public static final String UI_ACTION_START_NAME = "START";
    public static final String UI_ACTION_RESUME_NAME = "RESUME";
    public static final String UI_ACTION_PAUSE_NAME = "PAUSE";
    public static final String UI_ACTION_STOP_NAME = "STOP";
    public static final String UI_ACTION_DESTROY_VIEW_NAME = "DESTROY_VIEW";
    public static final String UI_ACTION_DESTROY_NAME = "DESTROY";
    public static final String UI_ACTION_DETACH_NAME = "DETACH";
    public static final String UI_ACTION_RESTART_NAME = "RESTART";
    public static final String UI_ACTION_PREATTACH_NAME = "PREATTACH";
    public static final String UI_ACTION_PRECREATE_NAME = "PRECREATE";

    public static final String[] UI_ACTION_NAMES = new String[] {
            UI_ACTION_ATTACH_NAME, UI_ACTION_CREATE_NAME, UI_ACTION_CREATE_VIEW_NAME, UI_ACTION_ACTIVITY_CREATED_NAME
            , UI_ACTION_START_NAME, UI_ACTION_RESUME_NAME, UI_ACTION_PAUSE_NAME, UI_ACTION_STOP_NAME
            , UI_ACTION_DESTROY_VIEW_NAME, UI_ACTION_DESTROY_NAME, UI_ACTION_DETACH_NAME, UI_ACTION_RESTART_NAME
            , UI_ACTION_PREATTACH_NAME, UI_ACTION_PRECREATE_NAME
    };
    public static final List<String> UI_ACTION_NAME_LIST = Arrays.asList(UI_ACTION_NAMES);

    public static int getUIActionCode(String action) {
        return UI_ACTION_NAME_LIST.indexOf(action);
    }
    public static String getUIActionName(int action) {
        return UI_ACTION_NAME_LIST.get(action);
    }


    public static String getActionName(int type, int action) {
        switch (type) {
            case EVENT_TYPE_KEY:
                return getKeyActionName(action);
            case EVENT_TYPE_UI:
                return getUIActionName(action);
            case EVENT_TYPE_HTTP:
                return getHTTPActionName(action);
            default:
                return getTouchActionName(action);
        }
    }


    public static final int GRAVITY_DEFAULT = 0; // left|top
    public static final int GRAVITY_RATIO = 0; // ratio

    public static final int GRAVITY_CENTER = 3; // center
    public static final int GRAVITY_LEFT = 1; // left
    public static final int GRAVITY_RIGHT = 2; // right
    public static final int GRAVITY_TOP = 1; // top
    public static final int GRAVITY_BOTTOM = 2; // bottom

    public static final int GRAVITY_TOP_LEFT = 1; // top|left
    public static final int GRAVITY_TOP_RIGHT = 2; // top|right
    public static final int GRAVITY_BOTTOM_LEFT = 3; //  bottom|left
    public static final int GRAVITY_BOTTOM_RIGHT = 4; //  bottom|right
    public static final int GRAVITY_RATIO_LEFT = 5; // ratio|left
    public static final int GRAVITY_RATIO_RIGHT = 6; // ratio|right
    public static final int GRAVITY_RATIO_TOP = 7; //  ratio|top
    public static final int GRAVITY_RATIO_BOTTOM = 8; //  ratio|bottom

    public static final int[] X_GRAVITIES = new int[] {
            GRAVITY_RATIO, GRAVITY_LEFT, GRAVITY_RIGHT, GRAVITY_CENTER
    };
    public static final int[] Y_GRAVITIES = new int[] {
            GRAVITY_RATIO, GRAVITY_TOP, GRAVITY_BOTTOM, GRAVITY_CENTER
    };
    public static final int[] BALL_GRAVITIES = new int[] {
            GRAVITY_RATIO, GRAVITY_TOP_LEFT, GRAVITY_TOP_RIGHT, GRAVITY_BOTTOM_LEFT, GRAVITY_BOTTOM_RIGHT
            , GRAVITY_RATIO_LEFT, GRAVITY_RATIO_RIGHT, GRAVITY_RATIO_TOP, GRAVITY_RATIO_BOTTOM
    };

    public static int getXGravityImageResource(int gravity) {
        switch (gravity) {
            case GRAVITY_CENTER:
                return R.drawable.center_light;
            case GRAVITY_RATIO:
                return R.drawable.percent_light;
            case GRAVITY_LEFT:
                return R.drawable.back2_light;
            case GRAVITY_RIGHT:
                return R.drawable.forward2_light;
            default:
                return 0;
        }
    }

    public static int getYGravityImageResource(int gravity) {
        switch (gravity) {
            case GRAVITY_CENTER:
                return R.drawable.center_light;
            case GRAVITY_RATIO:
                return R.drawable.percent_light;
            case GRAVITY_TOP:
                return R.drawable.up2_light;
            case GRAVITY_BOTTOM:
                return R.drawable.down2_light;
            default:
                return 0;
        }
    }

    public static int getBallGravityImageResource(int gravity) {
        switch (gravity) {
            case GRAVITY_RATIO:
                return R.drawable.ratio; // R.drawable.percent_light;
            case GRAVITY_TOP_LEFT:
                return R.drawable.top_left;
            case GRAVITY_TOP_RIGHT: // top|right
                return R.drawable.top_right;
            case GRAVITY_BOTTOM_LEFT:
                return R.drawable.bottom_left;
            case GRAVITY_BOTTOM_RIGHT: // top|right
                return R.drawable.bottom_right;
            case GRAVITY_RATIO_LEFT: // ratio|left
                return R.drawable.ratio_left;
            case GRAVITY_RATIO_RIGHT: // ratio|right
                return R.drawable.ratio_right;
            case GRAVITY_RATIO_TOP: // ratio|left
                return R.drawable.ratio_top;
            case GRAVITY_RATIO_BOTTOM: // ratio|right
                return R.drawable.ratio_bottom;
            default:
                return 0;
        }
    }


    public static int getBallGravityNameResId(int gravity) {
        switch (gravity) {
            case GRAVITY_RATIO:
                return R.string.ratio; // R.drawable.percent_light;
            case GRAVITY_TOP_LEFT:
                return R.string.top_left;
            case GRAVITY_TOP_RIGHT: // top|right
                return R.string.top_right;
            case GRAVITY_BOTTOM_LEFT:
                return R.string.bottom_left;
            case GRAVITY_BOTTOM_RIGHT: // top|right
                return R.string.bottom_right;
            case GRAVITY_RATIO_LEFT: // ratio|left
                return R.string.ratio_left;
            case GRAVITY_RATIO_RIGHT: // ratio|right
                return R.string.ratio_right;
            case GRAVITY_RATIO_TOP: // ratio|left
                return R.string.ratio_top;
            case GRAVITY_RATIO_BOTTOM: // ratio|right
                return R.string.ratio_bottom;
            default:
                return 0;
        }
    }
    public static boolean isRatio(int ballGravity) {
        return ballGravity == GRAVITY_RATIO || ballGravity == GRAVITY_RATIO_LEFT || ballGravity == GRAVITY_RATIO_RIGHT;
    }

    public static boolean isBottom(int ballGravity) {
        return ballGravity == GRAVITY_RATIO_BOTTOM || ballGravity == GRAVITY_BOTTOM_LEFT || ballGravity == GRAVITY_BOTTOM_RIGHT;
    }

    public static boolean isTop(int ballGravity) {
        return ballGravity == GRAVITY_RATIO_TOP || ballGravity == GRAVITY_TOP_LEFT || ballGravity == GRAVITY_TOP_RIGHT;
    }

    public static boolean isRight(int ballGravity) {
        return ballGravity == GRAVITY_RATIO_RIGHT || ballGravity == GRAVITY_TOP_RIGHT || ballGravity == GRAVITY_BOTTOM_RIGHT;
    }

    public static boolean isLeft(int ballGravity) {
        return ballGravity == GRAVITY_RATIO_LEFT || ballGravity == GRAVITY_TOP_LEFT || ballGravity == GRAVITY_BOTTOM_LEFT;
    }

}
