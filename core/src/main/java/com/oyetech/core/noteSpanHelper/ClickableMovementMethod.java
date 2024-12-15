package com.oyetech.core.noteSpanHelper;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.BaseMovementMethod;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * A movement method that traverses links in the text buffer and fires clicks. Unlike
 * {@link LinkMovementMethod}, this will not consume touch events outside {@link ClickableSpan}s.
 */
public class ClickableMovementMethod extends BaseMovementMethod {

    private static ClickableMovementMethod sInstance;

    public boolean enableClick = true;

    public ClickableMovementMethod() {

    }

    public ClickableMovementMethod(Boolean enableClick) {
        this.enableClick = enableClick;
    }

    public static ClickableMovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new ClickableMovementMethod();
        }
        return sInstance;
    }

    @Override
    public boolean canSelectArbitrarily() {
        return true;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {

            int x = (int) event.getX();
            int y = (int) event.getY();
            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();
            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
            if (link.length > 0) {
                if (action == MotionEvent.ACTION_UP) {
                    if (enableClick) {
                        link[0].onClick(widget);
                    }
                } else {
                    //Selection.setSelection(buffer, buffer.getSpanStart(link[0]),
                    //      buffer.getSpanEnd(link[0]));
                }
                return true;
            } else {
                //Selection.removeSelection(buffer);
            }
        }

        return false;
    }

    @Override
    public void initialize(TextView widget, Spannable text) {
        Selection.removeSelection(text);
    }
}
