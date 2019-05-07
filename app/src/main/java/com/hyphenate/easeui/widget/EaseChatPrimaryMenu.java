package com.hyphenate.easeui.widget;

import android.Manifest;
import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.hyphenate.util.EMLog;
import com.stratagile.pnrouter.application.AppConfig;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

/**
 * primary menu
 *
 */
public class EaseChatPrimaryMenu extends EaseChatPrimaryMenuBase implements OnClickListener {
    public ATEditText editText;
    private View buttonSetModeKeyboard;
    private RelativeLayout edittext_layout;
    private View buttonSetModeVoice;
    private View buttonSend;
    private View buttonPressToSpeak;
    private ImageView faceNormal;
    private ImageView faceChecked;
    private Button buttonMore;
    private boolean ctrlPress = false;
    private View contentView;
    private TextView holdTextView;
    private boolean onKeyDel = false;

    //是否正在录音，正在录音，其他点击不能生效
    private boolean isRecording = false;

    public EaseChatPrimaryMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public EaseChatPrimaryMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EaseChatPrimaryMenu(Context context) {
        super(context);
        init(context, null);
    }

    public String getEdittext() {
        return editText.getText().toString();
    }

    public void setEdittext(String edittext,boolean showSoft) {
        editText.setText(EaseSmileUtils.getSmiledTextInput(editText.getContext(),edittext));
        editText.setSelection(editText.getText().length());
        if (!"".equals(edittext) && showSoft) {
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                }
            }, 100);
        }
    }

    public void showKeyBorad() {
        KLog.i("打开键盘。。。。");
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        }, 100);
    }


    private void init(final Context context, AttributeSet attrs) {
        Context context1 = context;
        LayoutInflater.from(context).inflate(R.layout.ease_widget_chat_primary_menu, this);
        editText = (ATEditText) findViewById(R.id.et_sendmessage);
        buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
        buttonSend = findViewById(R.id.btn_send);
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
        faceNormal = (ImageView) findViewById(R.id.iv_face_normal);
        faceChecked = (ImageView) findViewById(R.id.iv_face_checked);
        RelativeLayout faceLayout = (RelativeLayout) findViewById(R.id.rl_face);
        buttonMore = (Button) findViewById(R.id.btn_more);
        buttonMore.setVisibility(View.VISIBLE);
//        edittext_layout.setBackgroundResource(R.drawable.ease_input_bar_bg_normal);
        holdTextView = findViewById(R.id.holdTextView);
        buttonSend.setOnClickListener(this);
        buttonSetModeKeyboard.setOnClickListener(this);
        buttonSetModeVoice.setOnClickListener(this);
        buttonMore.setOnClickListener(this);
        faceLayout.setOnClickListener(this);
        editText.setOnClickListener(this);
        editText.requestFocus();
        
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                KLog.i("焦点：" + hasFocus);
                if (hasFocus) {
                    if(listener != null)
                        listener.onEditTextClicked();
                }
//                if (hasFocus) {
//                    edittext_layout.setBackgroundResource(R.drawable.ease_input_bar_bg_active);
//                } else {
//                    edittext_layout.setBackgroundResource(R.drawable.ease_input_bar_bg_normal);
//                }

            }
        });
        // listen the text change
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s) && !"".equals(s.toString().trim())) {
                    if (buttonSend.getVisibility() != View.VISIBLE) {
                        buttonMore.setVisibility(View.INVISIBLE);
                        buttonSend.setVisibility(View.VISIBLE);
                        buttonSend.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.send_button_in));
                    }
                } else {
                    buttonMore.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.INVISIBLE);
                    buttonMore.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.send_button_in));
                }

                if (listener != null) {
                    listener.onTyping(s, start, before, count);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                KLog.i("key"+"keyCode:" + keyCode + " action:" + event.getAction());

                // test on Mac virtual machine: ctrl map to KEYCODE_UNKNOWN
                if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        ctrlPress = true;
                    } else if (event.getAction() == KeyEvent.ACTION_UP) {
                        ctrlPress = false;
                    }
                }
                onKeyDel = true;
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    return ATEditText.KeyDownHelper(editText.getText());
                }
                return false;
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                EMLog.d("key", "keyCode:" + event.getKeyCode() + " action" + event.getAction() + " ctrl:" + ctrlPress);
                if (actionId == EditorInfo.IME_ACTION_SEND ||
                        (event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                         event.getAction() == KeyEvent.ACTION_DOWN &&
                         ctrlPress == true)) {
                    String s = editText.getText().toString().trim();
                    editText.setText("");
                    listener.onSendBtnClicked(s,"");
                    return true;
                }
                else{
                    return false;
                }
            }
        });
        editText.setBackSpaceLisetener(backspaceListener);
        buttonPressToSpeak.setOnTouchListener(new OnTouchListener() {
            
            @Override 
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isRecording = true;
                    holdTextView.setText(context.getString(R.string.release_to_send));
                    buttonPressToSpeak.setBackground(context.getResources().getDrawable(R.drawable.input_background_press));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    isRecording = false;
                    holdTextView.setText(context.getString(R.string.button_pushtotalk));
                    buttonPressToSpeak.setBackground(context.getResources().getDrawable(R.drawable.input_background));
                }
                if(listener != null){
                    return listener.onPressToSpeakBtnTouch(v, event);
                }
                return false;
            }
        });
    }
    TInputConnection.BackspaceListener backspaceListener = new TInputConnection.BackspaceListener() {
        @Override
        public boolean onBackspace() {
            Editable editable = editText.getText();

            if(editable.length() == 0){
                return false;
            }
            if(!onKeyDel)
            {
                ATEditText.KeyDownHelper(editText.getText());
            }
            onKeyDel = false;
            return false;
        }
    };
    /**
     * set recorder view when speak icon is touched
     * @param voiceRecorderView
     */
    public void setPressToSpeakRecorderView(EaseVoiceRecorderView voiceRecorderView){
        EaseVoiceRecorderView voiceRecorderView1 = voiceRecorderView;
    }

    public void bindContentView(View contentView) {
        this.contentView = contentView;
    }

    /**
     * append emoji icon to editText
     * @param emojiContent
     */
    @Override
    public void onEmojiconInputEvent(CharSequence emojiContent){
        editText.append(emojiContent);
    }
    
    /**
     * delete emojicon
     */
    @Override
    public void onEmojiconDeleteEvent(){
        if (!TextUtils.isEmpty(editText.getText())) {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            editText.dispatchKeyEvent(event);
        }
    }
    
    /**
     * on clicked event
     * @param view
     */
    @Override
    public void onClick(View view){
        int id = view.getId();
        if (id == R.id.btn_send) {
            if (isRecording) {
                KLog.i("拦截点击");
                return;
            }
            if(listener != null){
                int selectionEnd = editText.length();
                int selectionStart = 0;
                ATEditText.DataSpan[] spans = editText.getText().getSpans(selectionStart, selectionEnd, ATEditText.DataSpan.class);
                String point  = "";
                int index = 0;
                for (ATEditText.DataSpan span : spans) {
                    if (span != null && span.getUserId() != null && !span.getUserId().equals("")) {
                        if(index > 0)
                        {
                            point += ","+span.getUserId();
                        }else{
                            point += span.getUserId();
                        }
                        index ++;
                    }
                }
                String s = editText.getText().toString().trim();
                editText.setText("");
                listener.onSendBtnClicked(s,point);
            }
        } else if (id == R.id.btn_set_mode_voice) {
            if (isRecording) {
                return;
            }
            AndPermission.with(AppConfig.instance)
                    .requestCode(101)
                    .permission(
                            Manifest.permission.RECORD_AUDIO
                    )
                    .callback(permission)
                    .start();

        } else if (id == R.id.btn_set_mode_keyboard) {
            if (isRecording) {
                KLog.i("拦截点击");
                return;
            }
            setModeKeyboard();
            showNormalFaceImage();
            if(listener != null)
                listener.onToggleVoiceBtnClicked(true);
        } else if (id == R.id.btn_more) {
            if (isRecording) {
                KLog.i("拦截点击");
                return;
            }
            KLog.i("更多按钮点击");
            buttonSetModeVoice.setVisibility(View.VISIBLE);
            buttonSetModeKeyboard.setVisibility(View.GONE);
            edittext_layout.setVisibility(View.VISIBLE);
            buttonPressToSpeak.setVisibility(View.GONE);
            showNormalFaceImage();
            if(listener != null)
                listener.onToggleExtendClicked();
        } else if (id == R.id.et_sendmessage) {
            KLog.i("输入框点击");
            if (isRecording) {
                KLog.i("拦截点击");
                return;
            }
//            edittext_layout.setBackgroundResource(R.drawable.ease_input_bar_bg_active);
            faceNormal.setVisibility(View.VISIBLE);
            faceChecked.setVisibility(View.INVISIBLE);
            if(listener != null)
                listener.onEditTextClicked();
        } else if (id == R.id.rl_face) {
            if (isRecording) {
                KLog.i("拦截点击");
                return;
            }
            toggleFaceImage();
            if(listener != null){
                listener.onToggleEmojiconClicked();
            }
        } else {
        }
    }
    
    
    /**
     * show voice icon when speak bar is touched
     * 
     */
    protected void setModeVoice() {
        hideKeyboard();
        edittext_layout.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.GONE);
        buttonSetModeKeyboard.setVisibility(View.VISIBLE);
        buttonSend.setVisibility(View.GONE);
        buttonMore.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.VISIBLE);
        faceNormal.setVisibility(View.VISIBLE);
        faceChecked.setVisibility(View.INVISIBLE);

    }

    /**
     * show keyboard
     */
    protected void setModeKeyboard() {
        edittext_layout.setVisibility(View.VISIBLE);
        buttonSetModeKeyboard.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        // mEditTextContent.setVisibility(View.VISIBLE);
        editText.requestFocus();
        // buttonSend.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);
        if (TextUtils.isEmpty(editText.getText().toString().trim())) {
            buttonMore.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.GONE);
        } else {
            buttonMore.setVisibility(View.GONE);
            buttonSend.setVisibility(View.VISIBLE);
        }

    }
    
    
    protected void toggleFaceImage(){
        if(faceNormal.getVisibility() == View.VISIBLE){
            showSelectedFaceImage();
        }else{
            showNormalFaceImage();
        }
    }
    
    private void showNormalFaceImage(){
        faceNormal.setVisibility(View.VISIBLE);
        faceChecked.setVisibility(View.INVISIBLE);
    }
    
    private void showSelectedFaceImage(){
        faceNormal.setVisibility(View.INVISIBLE);
        faceChecked.setVisibility(View.VISIBLE);
    }
    

    @Override
    public void onExtendMenuContainerHide() {
        showNormalFaceImage();
    }

    @Override
    public void onTextInsert(CharSequence text) {
       int start = editText.getSelectionStart();
       Editable editable = editText.getEditableText();
       editable.insert(start, text);
       setModeKeyboard();
    }
    @Override
    public int onAddAtText(String text,String data) {
        /*int start = editText.getSelectionStart();
        Editable editable = editText.getEditableText();
        editable.insert(start, text);
        setModeKeyboard();*/
        String editContent = getEdittext();
        if(editContent.contains("@") && editContent.lastIndexOf("@") == editContent.length() -1)
        {
            editText.getText().delete(editContent.length() -1,editContent.length());
        }
        int result = editText.addSpan(text,data);
        return result;
    }
    @Override
    public EditText getEditText() {
        return editText;
    }
    private PermissionListener permission = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {

            // 权限申请成功回调。
            if (requestCode == 101) {
                setModeVoice();
                showNormalFaceImage();
                if(listener != null)
                    listener.onToggleVoiceBtnClicked(false);
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。
            if (requestCode == 101) {
                KLog.i("权限申请失败");

            }
        }
    };
}
