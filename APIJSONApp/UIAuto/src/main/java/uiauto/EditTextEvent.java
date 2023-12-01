package uiauto;

import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class EditTextEvent extends KeyEvent {
    public static final int WHEN_BEFORE = -1;
    public static final int WHEN_ON = 0;
    public static final int WHEN_AFTER = 1;

    EditText target;

    public static String getWhenName(int when) {
        switch (when) {
            case WHEN_BEFORE:
                return "BEFORE";
            case WHEN_AFTER:
                return "AFTER";
            default:
                return "ON";
        }
    }

    String text;
    public String getText() {
        if (text == null) {
            text = StringUtil.getString(getS()); // target 文本在变，不稳定 StringUtil.getString(target == null ? getS() : target.getText());
        }
        return text;
    }

    int selectStart;
    public int getSelectStart() {
        return selectStart;
    }

    int selectEnd;
    public int getSelectEnd() {
        return selectEnd;
    }

    public EditText getTarget() {
        UIAutoApp app = UIAutoApp.getInstance();
        boolean isWeb = StringUtil.isNotEmpty(targetWebId, true);
        if (target == null && isWeb) {
            target = app.findView(targetWebId);
        }
        if (target == null || (isWeb == false && target.isAttachedToWindow() == false)) {
            target = app.findView(targetId);
        }
        if (target == null) {
            target = app.findViewByFocus(app.getCurrentDecorView(), EditText.class);
        }
        return target;
    }
    int targetId;
    public int getTargetId() {
        if (targetId <= 0) {
            target = getTarget();
            targetId = target == null ? targetId : target.getId();
        }
        return targetId;
    }

    String targetWebId;
    public String getTargetWebId() {
        return targetWebId;
    }
    public EditTextEvent setTargetWebId(String targetWebId) {
        this.targetWebId = targetWebId;
        return this;
    }

    Integer x;
    Integer y;
    public Integer getX() {
        return x;
    }
    public EditTextEvent setX(Integer x) {
        this.x = x;
        return this;
    }
    public Integer getY() {
        return y;
    }
    public EditTextEvent setY(Integer y) {
        this.y = y;
        return this;
    }

    int when;
    public int getWhen() {
        return when;
    }

    CharSequence s;
    public CharSequence getS() {
        return s;
    }


    int start;
    public int getStart() {
        return start;
    }

    int count;
    public int getCount() {
        return count;
    }

    int after;
    public int getAfter() {
        return after;
    }

    public EditTextEvent(int action, int code) {
        super(action, code);
    }

    public EditTextEvent(long downTime, long eventTime, int action,
                         int code, int repeat) {
        super(downTime, eventTime, action, code, repeat);
    }

    public EditTextEvent(long downTime, long eventTime, int action,
                         int code, int repeat, int metaState) {
        super(downTime, eventTime, action, code, repeat, metaState);
    }

    public EditTextEvent(long downTime, long eventTime, int action,
                         int code, int repeat, int metaState,
                         int deviceId, int scancode) {
        super(downTime, eventTime, action, code, repeat, metaState, deviceId, scancode);
    }

    public EditTextEvent(long downTime, long eventTime, int action,
                         int code, int repeat, int metaState,
                         int deviceId, int scancode, int flags) {
        super(downTime, eventTime, action, code, repeat, metaState, deviceId, scancode, flags);
    }

    public EditTextEvent(long downTime, long eventTime, int action,
                         int code, int repeat, int metaState,
                         int deviceId, int scancode, int flags, int source) {
        super(downTime, eventTime, action, code, repeat, metaState, deviceId, scancode, flags, source);
    }

    public EditTextEvent(long time, String characters, int deviceId, int flags) {
        super(time, characters, deviceId, flags);
    }

    public EditTextEvent(KeyEvent origEvent) {
        super(origEvent);
    }

    public EditTextEvent(int action, int code, EditText target, int when, String text, int selectStart, int selectEnd, CharSequence s) {
        super(System.currentTimeMillis(), System.currentTimeMillis(), action, code, 0);
        init(target, when, text, selectStart, selectEnd, s);
    }
    public EditTextEvent(int action, int code, EditText target, int when, String text, int selectStart, int selectEnd, CharSequence s, int start, int count) {
        super(System.currentTimeMillis(), System.currentTimeMillis(), action, code, 0);
        init(target, when, text, selectStart, selectEnd, s, start, count);
    }
    public EditTextEvent(int action, int code, EditText target, int when, String text, int selectStart, int selectEnd, CharSequence s, int start, int count, int after) {
        super(System.currentTimeMillis(), System.currentTimeMillis(), action, code, 0);
        init(target, when, text, selectStart, selectEnd, s, start, count, after);
    }
    public EditTextEvent(long downTime, long eventTime, int action, int code, int repeat
            , EditText target, int when, String text, int selectStart, int selectEnd, CharSequence s, int start, int count, int after) {
        super(downTime, eventTime, action, code, repeat);
        init(target, when, text, selectStart, selectEnd, s, start, count, after);
    }
    public EditTextEvent(
            long downTime, long eventTime, int action
            , int code, int repeat, int metaState
            , int deviceId, int scancode, int flags, int source
            , EditText target, int when, String text, int selectStart, int selectEnd, CharSequence s, int start, int count, int after
    ) {
        super(downTime, eventTime, action, code, repeat, metaState, deviceId, scancode, flags, source);
        init(target, when, text, selectStart, selectEnd, s, start, count, after);
    }

    public void init(EditText target, int when, String text, int selectStart, int selectEnd, CharSequence s) {
        init(target, when, text, selectStart, selectEnd, s, 0, 0, 0);
    }
    public void init(EditText target, int when, String text, int selectStart, int selectEnd, CharSequence s, int start, int count) {
        init(target, when, text, selectStart, selectEnd, s, start, count, 0);
    }
    public void init(EditText target, int when, String text, int selectStart, int selectEnd, CharSequence s, int start, int count, int after) {
        this.target = target;
        this.targetId = target == null ? View.NO_ID : target.getId();
        this.when = when;
        this.text = text;
        this.selectStart = selectStart;
        this.selectEnd = selectEnd;
        this.s = s;
        this.start = start;
        this.count = count;
        this.after = after;
    }


}
