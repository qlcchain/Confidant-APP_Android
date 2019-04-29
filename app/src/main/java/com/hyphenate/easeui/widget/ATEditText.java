package com.hyphenate.easeui.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.NoCopySpan;
import android.text.Selection;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

/**
 * 带@功能的文本
 */
public class ATEditText extends AppCompatEditText {

    private TInputConnection inputConnection;

    public ATEditText(Context context) {
        super(context);
        init();
    }

    public ATEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ATEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        inputConnection = new TInputConnection(null,true);
        setEditableFactory(new NoCopySpanEditableFactory(new DirtySpanWatcher()));
    }
    /**
     * 当输入法和EditText建立连接的时候会通过这个方法返回一个InputConnection。
     * 我们需要代理这个方法的父类方法生成的InputConnection并返回我们自己的代理类。
     * */
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        inputConnection.setTarget(super.onCreateInputConnection(outAttrs));
        return inputConnection;
    }

    public void setBackSpaceLisetener(TInputConnection.BackspaceListener backSpaceLisetener){
        inputConnection.setBackspaceListener(backSpaceLisetener);
    }
    /**
     * 添加 @内容
     *
     * @param text 包含 @ 符号的字符
     */
    public void addSpan(String text,String data) {
        if(!isHasSpan(data))
        {
            getText().insert(getSelectionEnd(), text);
            DataSpan myTextSpan = new DataSpan();
            myTextSpan.setUserId(data);
            getText().setSpan(myTextSpan, getSelectionEnd() - text.length(), getSelectionEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setSelection(getText().length());
        }
    }

    public boolean isHasSpan(String userId)
    {
        int selectionEnd = getText().length();
        int selectionStart = 0;
        DataSpan[] spans =  getText().getSpans(selectionStart, selectionEnd, DataSpan.class);
        for (DataSpan span : spans) {
            if (span != null && span.getUserId() != null && span.getUserId().equals(userId)) {
               return true;
            }
        }
        return false;
    }
    /**
     * 找到最后 Span 块
     *
     * @param text
     * @return
     */
    public static boolean KeyDownHelper(Editable text) {
        int selectionEnd = Selection.getSelectionEnd(text);
        int selectionStart = Selection.getSelectionStart(text);
        DataSpan[] spans = text.getSpans(selectionStart, selectionEnd, DataSpan.class);
        for (DataSpan span : spans) {
            if (span != null) {
                int spanStart = text.getSpanStart(span);
                int spanEnd = text.getSpanEnd(span);
                if (selectionEnd == spanEnd) {
                    Selection.setSelection(text, spanStart, spanEnd);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 数据载体
     */
    static  class DataSpan {

        private String userId;
        public DataSpan() {

        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }


    class NoCopySpanEditableFactory extends Editable.Factory {

        private NoCopySpan spans;

        public NoCopySpanEditableFactory(NoCopySpan spans) {
            this.spans = spans;
        }

        @Override
        public Editable newEditable(CharSequence source) {
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(source);
            stringBuilder.setSpan(spans, 0, source.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            return stringBuilder;
        }
    }

    class DirtySpanWatcher implements SpanWatcher {

        @Override
        public void onSpanAdded(Spannable text, Object what, int start, int end) {

        }

        @Override
        public void onSpanRemoved(Spannable text, Object what, int start, int end) {

        }

        @Override
        public void onSpanChanged(Spannable text, Object what, int ostart, int oend, int nstart, int nend) {
            int spanEnd = text.getSpanEnd(what);
            int spanStart = text.getSpanStart(what);
            if (spanStart >= 0 && spanEnd >= 0 && what instanceof DataSpan) {
                CharSequence charSequence = text.subSequence(spanStart, spanEnd);
                if (!charSequence.toString().contains("@")) {
                    DataSpan[] spans = text.getSpans(spanStart, spanEnd, DataSpan.class);
                    for (DataSpan span : spans) {
                        if (span != null) {
                            text.removeSpan(span);
                            break;
                        }
                    }
                }
            }
        }
    }
}
