package apijson.demo;

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
    public static final String HTTP_ACTION_REQUEST_NAME = "REQUEST";
    public static final String HTTP_ACTION_RESPONSE_NAME = "RESPONSE";
    public static final String HTTP_HEADER_NAME = "HEADER";
    public static final String HTTP_CONTENT_NAME = "CONTENT";

    public static final String[] HTTP_ACTION_NAMES = new String[] {
            HTTP_ACTION_REQUEST_NAME, HTTP_ACTION_RESPONSE_NAME
    };
    public static final List<String> HTTP_ACTION_NAME_LIST = Arrays.asList(HTTP_ACTION_NAMES);

    public static int getHTTPActionCode(String action) {
        return HTTP_ACTION_NAME_LIST.indexOf(action);
    }
    public static String getHTTPActionName(int action) {
        return HTTP_ACTION_NAME_LIST.get(action);
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

    public static final String[] UI_ACTION_NAMES = new String[] {
            UI_ACTION_ATTACH_NAME, UI_ACTION_CREATE_NAME, UI_ACTION_CREATE_VIEW_NAME, UI_ACTION_ACTIVITY_CREATED_NAME
            , UI_ACTION_START_NAME, UI_ACTION_RESUME_NAME, UI_ACTION_PAUSE_NAME, UI_ACTION_STOP_NAME
            , UI_ACTION_DESTROY_VIEW_NAME, UI_ACTION_DESTROY_NAME, UI_ACTION_DETACH_NAME, UI_ACTION_RESTART_NAME
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
}
