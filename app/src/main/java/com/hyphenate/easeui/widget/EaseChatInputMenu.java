package com.hyphenate.easeui.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.model.EaseDefaultEmojiconDatas;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.widget.EaseChatExtendMenu.EaseChatExtendMenuItemClickListener;
import com.hyphenate.easeui.widget.EaseChatPrimaryMenuBase.EaseChatPrimaryMenuListener;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenu;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenuBase;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenuBase.EaseEmojiconMenuListener;
import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.entity.events.ChatKeyboard;
import com.stratagile.pnrouter.utils.SpUtil;
import com.stratagile.pnrouter.utils.UIUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * input menu
 * 
 * including below component:
 *    EaseChatPrimaryMenu: main menu bar, text input, send button
 *    EaseChatExtendMenu: grid menu with image, file, location, etc
 *    EaseEmojiconMenu: emoji icons
 */
public class EaseChatInputMenu extends LinearLayout {
    FrameLayout primaryMenuContainer, emojiconMenuContainer;
    protected EaseChatPrimaryMenu chatPrimaryMenu;
    protected EaseEmojiconMenuBase emojiconMenu;
    protected EaseChatExtendMenu chatExtendMenu;
    protected FrameLayout chatExtendMenuContainer;
    protected LayoutInflater layoutInflater;
    private ChatMenuOpenListenter chatMenuOpenListenter;

    public ChatMenuOpenListenter getChatMenuOpenListenter() {
        return chatMenuOpenListenter;
    }

    public void setChatMenuOpenListenter(ChatMenuOpenListenter chatMenuOpenListenter) {
        this.chatMenuOpenListenter = chatMenuOpenListenter;
    }

    private Handler handler = new Handler();
    private ChatInputMenuListener listener;
    private Context context;
    private boolean inited;
    private View contentView;
    private InputMethodManager mInputManager;//软键盘管理类

    public EaseChatInputMenu(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public EaseChatInputMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EaseChatInputMenu(Context context) {
        super(context);
        init(context, null);
    }

    public void bindContentView(View contentView) {
        this.contentView = contentView;
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.ease_widget_chat_input_menu, this);
        mInputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //输入文字的带edittext
        primaryMenuContainer = (FrameLayout) findViewById(R.id.primary_menu_container);

        //emoji显示的
        emojiconMenuContainer = (FrameLayout) findViewById(R.id.emojicon_menu_container);

        //选择其他的，如图片菜单，文件菜单，的容器
        chatExtendMenuContainer = (FrameLayout) findViewById(R.id.extend_menu_container);

        int height = SpUtil.INSTANCE.getInt(getContext(), ConstantValue.INSTANCE.getRealKeyboardHeight(), 587);
        KLog.i("sp中记录的键盘的高度为：" + height);
        if (height != 0) {
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(UIUtils.getDisplayWidth((Activity) getContext()), height);
            chatExtendMenuContainer.setLayoutParams(llp);
        }
         // extend menu  选择其他的，如图片菜单，文件菜单
         chatExtendMenu = (EaseChatExtendMenu) findViewById(R.id.extend_menu);

    }

    public void lockContentHeight(ChatKeyboard chatKeyboard){
        if (chatKeyboard.isLock()) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView.getLayoutParams();
            params.height = contentView.getHeight();
            params.weight = 0.0F;
        } else {
            ((LinearLayout.LayoutParams) contentView.getLayoutParams()).weight = 1.0F;
        }
    }

    /**
     * init view 
     * 
     * This method should be called after registerExtendMenuItem(), setCustomEmojiconMenu() and setCustomPrimaryMenu().
     * @param emojiconGroupList --will use default if null
     */
    @SuppressLint("InflateParams")
    public void init(List<EaseEmojiconGroupEntity> emojiconGroupList) {
        if(inited){
            return;
        }
        // primary menu, use default if no customized one
        //文字输入的菜单，有edittext，控制发送，显示emoji，菜单
        if(chatPrimaryMenu == null){
            chatPrimaryMenu = (EaseChatPrimaryMenu) layoutInflater.inflate(R.layout.ease_layout_chat_primary_menu, null);
        }
        ((EaseChatPrimaryMenu)chatPrimaryMenu).bindContentView(contentView);
        primaryMenuContainer.addView(chatPrimaryMenu);

        // emojicon menu, use default if no customized one
        if(emojiconMenu == null){
            emojiconMenu = (EaseEmojiconMenu) layoutInflater.inflate(R.layout.ease_layout_emojicon_menu, null);
            if(emojiconGroupList == null){
                emojiconGroupList = new ArrayList<EaseEmojiconGroupEntity>();
                emojiconGroupList.add(new EaseEmojiconGroupEntity(R.drawable.ee_1,  Arrays.asList(EaseDefaultEmojiconDatas.getData())));
            }
            ((EaseEmojiconMenu)emojiconMenu).init(emojiconGroupList);
        }
        emojiconMenuContainer.addView(emojiconMenu);

        processChatMenu();
        chatExtendMenu.init();
        chatPrimaryMenu.editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && chatExtendMenuContainer.isShown()) {
                    lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                    hideEmotionLayout(true);//隐藏表情布局，显示软件盘

                    //软件盘显示后，释放内容高度
                    chatPrimaryMenu.editText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            unlockContentHeightDelayed();
                        }
                    }, 200L);
                }
                return false;
            }
        });
        inited = true;
    }
    
    public void init(){
        init(null);
    }
    
    /**
     * set custom emojicon menu
     * @param customEmojiconMenu
     */
    public void setCustomEmojiconMenu(EaseEmojiconMenuBase customEmojiconMenu){
        this.emojiconMenu = customEmojiconMenu;
    }
    
    /**
     * set custom primary menu
     * @param customPrimaryMenu
     */
    public void setCustomPrimaryMenu(EaseChatPrimaryMenu customPrimaryMenu){
        this.chatPrimaryMenu = customPrimaryMenu;
    }
    
    public EaseChatPrimaryMenuBase getPrimaryMenu(){
        return chatPrimaryMenu;
    }
    
    public EaseChatExtendMenu getExtendMenu(){
        return chatExtendMenu;
    }
    
    public EaseEmojiconMenuBase getEmojiconMenu(){
        return emojiconMenu;
    }
    

    /**
     * register menu item
     * 
     * @param name
     *            item name
     * @param drawableRes
     *            background of item
     * @param itemId
     *             id
     * @param listener
     *            on click event of item
     */
    public void registerExtendMenuItem(String name, int drawableRes, int itemId,
            EaseChatExtendMenuItemClickListener listener) {
        chatExtendMenu.registerMenuItem(name, drawableRes, itemId, listener);
    }

    /**
     * register menu item
     * 
     * @param name
     *            resource id of item name
     * @param drawableRes
     *            background of item
     * @param itemId
     *             id
     * @param listener
     *            on click event of item
     */
    public void registerExtendMenuItem(int nameRes, int drawableRes, int itemId,
            EaseChatExtendMenuItemClickListener listener) {
        chatExtendMenu.registerMenuItem(nameRes, drawableRes, itemId, listener);
    }


    protected void processChatMenu() {
        // send message button
        chatPrimaryMenu.setChatPrimaryMenuListener(new EaseChatPrimaryMenuListener() {

            @Override
            public void onTyping(CharSequence s, int start, int before, int count) {
                if (listener != null) {
                    listener.onTyping(s, start, before, count);
                }
            }

            @Override
            public void onSendBtnClicked(String content) {
                if (listener != null)
                    listener.onSendMessage(content);
            }

            @Override
            public void onToggleVoiceBtnClicked() {
                hideExtendMenuContainer();
            }

            @Override
            public void onToggleExtendClicked() {
                toggleMore();
                if (chatMenuOpenListenter != null) {
                    chatMenuOpenListenter.onOpen();
                }
            }

            @Override
            public void onToggleEmojiconClicked() {
                toggleEmojicon();
                if (chatMenuOpenListenter != null) {
                    chatMenuOpenListenter.onOpen();
                }
            }

            @Override
            public void onEditTextClicked() {
                hideExtendMenuContainer();
                if (chatMenuOpenListenter != null) {
                    chatMenuOpenListenter.onOpen();
                }
            }


            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                if(listener != null){
                    return listener.onPressToSpeakBtnTouch(v, event);
                }
                return false;
            }
        });

        // emojicon menu
        emojiconMenu.setEmojiconMenuListener(new EaseEmojiconMenuListener() {

            @Override
            public void onExpressionClicked(EaseEmojicon emojicon) {
                if(emojicon.getType() != EaseEmojicon.Type.BIG_EXPRESSION){
                    if(emojicon.getEmojiText() != null){
                        chatPrimaryMenu.onEmojiconInputEvent(EaseSmileUtils.getSmiledText(context,emojicon.getEmojiText()));
                    }
                }else{
                    if(listener != null){
                        listener.onBigExpressionClicked(emojicon);
                    }
                }
            }

            @Override
            public void onDeleteImageClicked() {
                chatPrimaryMenu.onEmojiconDeleteEvent();
            }
        });

    }
    
   
    /**
     * insert text
     * @param text
     */
    public void insertText(String text){
        getPrimaryMenu().onTextInsert(text);
    }

    /**
     * show or hide extend menu
     * 
     */
    protected void toggleMore() {
        //更多点击。
//        KLog.i("更多按钮点击");
        chatPrimaryMenu.setModeKeyboard();
        if (chatExtendMenuContainer.getVisibility() == View.GONE) {
            //如果包括表情，更多是不可见的，关闭键盘，然后显示菜单
            KLog.i("更多按钮点击, 显示菜单");
            if (isSoftInputShown()) {
                lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                hideKeyboard();
                chatExtendMenuContainer.setVisibility(View.VISIBLE);
                KLog.i("extendMenu的高度为：" + chatExtendMenuContainer.getHeight());
                chatExtendMenu.setVisibility(View.VISIBLE);
                emojiconMenu.setVisibility(View.GONE);
                unlockContentHeightDelayed();//软件盘显示后，释放内容高度
            } else {
                handler.postDelayed(new Runnable() {
                    public void run() {
                        chatExtendMenuContainer.setVisibility(View.VISIBLE);
                        KLog.i("extendMenu的高度为：" + chatExtendMenuContainer.getHeight());
                        chatExtendMenu.setVisibility(View.VISIBLE);
                        emojiconMenu.setVisibility(View.GONE);
                        chatExtendMenuContainer.setAnimation(AnimationUtils.loadAnimation(context, R.anim.push_bottom_in));
                    }
                }, 50);
            }
//            handler.postDelayed(new Runnable() {
//                public void run() {
//
//                }
//            }, 50);
        } else {
            //如果包括表情，更多是可见的，打开键盘
            if (emojiconMenu.getVisibility() == View.VISIBLE) {
                emojiconMenu.setVisibility(View.GONE);
                chatExtendMenu.setVisibility(View.VISIBLE);
            } else {
                if (isSoftInputShown()) {//同上
                    KLog.i("更多按钮点击, 隐藏键盘");
                    //如果键盘是打开的，那么就是现实发表情
                    lockContentHeight();
                    hideKeyboard();
                    chatExtendMenuContainer.setVisibility(VISIBLE);
                    chatExtendMenu.setVisibility(View.VISIBLE);
                    emojiconMenu.setVisibility(View.GONE);
                    unlockContentHeightDelayed();
                } else {
                    KLog.i("更多按钮点击, 显示键盘");
                    //键盘是关闭的，就是就要关闭菜单页面，比如文件，，，
                    lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                    chatExtendMenuContainer.setVisibility(GONE);
                    chatExtendMenu.setVisibility(View.GONE);
                    emojiconMenu.setVisibility(View.GONE);
                    showSoftInput();
                    unlockContentHeightDelayed();//软件盘显示后，释放内容高度
                }
//                chatExtendMenuContainer.setVisibility(View.GONE);
            }
        }
    }

    /**
     * show or hide emojicon
     */
    protected void toggleEmojicon() {
        //表情菜单关闭状态
        chatPrimaryMenu.setModeKeyboard();
        if (chatExtendMenuContainer.getVisibility() == View.GONE) {
            KLog.i("意料之外的跳闪");
            if (isSoftInputShown()) {
                lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                hideKeyboard();
                chatExtendMenuContainer.setVisibility(VISIBLE);
                emojiconMenu.setVisibility(View.VISIBLE);
                chatExtendMenu.setVisibility(View.GONE);
                unlockContentHeightDelayed();//软件盘显示后，释放内容高度
            } else {
                handler.postDelayed(new Runnable() {
                    public void run() {
                        chatExtendMenuContainer.setVisibility(View.VISIBLE);
                        KLog.i("extendMenu的高度为：" + chatExtendMenuContainer.getHeight());
                        chatExtendMenu.setVisibility(View.GONE);
                        emojiconMenu.setVisibility(View.VISIBLE);
                        chatExtendMenuContainer.setAnimation(AnimationUtils.loadAnimation(context, R.anim.push_bottom_in));
                    }
                }, 50);
            }
//            EventBus.getDefault().post(new ChatKeyboard(true));
//            lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
//            hideKeyboard();
//            unlockContentHeightDelayed();//软件盘显示后，释放内容高度
        } else {
            //表情菜单打开状态,打开软键盘
//            EventBus.getDefault().post(new ChatKeyboard(false));
            if (emojiconMenu.getVisibility() == View.VISIBLE) {
                KLog.i("表情菜单打开状态,打开软键盘");
                //表情是开着的，打开键盘
                lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                hideEmotionLayout(true);//隐藏表情布局，显示软件盘
                chatExtendMenuContainer.setVisibility(GONE);
                KLog.i("extendMenu的高度为：" + chatExtendMenuContainer.getHeight());
                emojiconMenu.setVisibility(View.GONE);
                chatExtendMenu.setVisibility(View.GONE);
                unlockContentHeightDelayed();//软件盘显示后，释放内容高度
//                chatExtendMenuContainer.setVisibility(View.GONE);
//                emojiconMenu.setVisibility(View.GONE);
            } else {
                //表情是关着的，关闭键盘
                KLog.i("表情是关着的，关闭键盘");
                if (isSoftInputShown()) {//同上
                    //如果键盘是打开的，那么就是现实发表情
                    lockContentHeight();
                    hideKeyboard();
                    chatExtendMenuContainer.setVisibility(VISIBLE);
                    KLog.i("extendMenu的高度为：" + chatExtendMenuContainer.getHeight());
                    emojiconMenu.setVisibility(View.VISIBLE);
                    chatExtendMenu.setVisibility(View.GONE);
                    unlockContentHeightDelayed();
                } else {
                    //键盘是关闭的，就是就要关闭菜单页面，比如文件，，，
//                    showEmotionLayout();//两者都没显示，直接显示表情布局
                    chatExtendMenuContainer.setVisibility(VISIBLE);
                    KLog.i("extendMenu的高度为：" + chatExtendMenuContainer.getHeight());
                    chatExtendMenu.setVisibility(View.GONE);
                    emojiconMenu.setVisibility(View.VISIBLE);
                }
//                chatExtendMenu.setVisibility(View.GONE);
//                emojiconMenu.setVisibility(View.VISIBLE);
                
            }

        }
    }

    /**
     * 是否显示软件盘
     * @return
     */
    private boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    /**
     * 获取软件盘的高度
     * @return
     */
    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        /**
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        ((Activity)context).getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = ((Activity)context).getWindow().getDecorView().getRootView().getHeight();
        //计算软件盘的高度
        int softInputHeight = screenHeight - r.bottom;

        /**
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }

        if (softInputHeight < 0) {
            softInputHeight = 0;
//            LogUtils.w("EmotionKeyboard--Warning: value of softInputHeight is below zero!");
        }
        //存一份到本地
//        if (softInputHeight > 0) {
//            sp.edit().putInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, softInputHeight).apply();
//        }
        return softInputHeight;
    }

    /**
     * 底部虚拟按键栏的高度
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        ((Activity)context).getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    /**
     * 隐藏表情布局
     * @param showSoftInput 是否显示软件盘
     */
    private void hideEmotionLayout(boolean showSoftInput) {
        if (emojiconMenu.getVisibility() == View.VISIBLE) {
            emojiconMenu.setVisibility(View.GONE);
            if (showSoftInput) {
                showSoftInput();
            }
        }
    }

    /**
     * 编辑框获取焦点，并显示软件盘
     */
    private void showSoftInput() {
        chatPrimaryMenu.editText.requestFocus();
        chatPrimaryMenu.editText.post(new Runnable() {
            @Override
            public void run() {
                mInputManager.showSoftInput(chatPrimaryMenu.editText, 0);
            }
        });
    }

    public void hideSoftInput() {
        chatPrimaryMenu.editText.post(new Runnable() {
            @Override
            public void run() {
                mInputManager.hideSoftInputFromWindow(chatPrimaryMenu.editText.getWindowToken(), 0);
            }
        });
    }

    /**
     * 锁定内容高度，防止跳闪
     */
    private void lockContentHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView.getLayoutParams();
        params.height = contentView.getHeight();
        params.weight = 0.0F;
    }

    /**
     * 释放被锁定的内容高度
     */
    private void unlockContentHeightDelayed() {
        chatExtendMenuContainer.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) contentView.getLayoutParams()).weight = 1.0F;
                KLog.i("unlockContentHeightDelayed");
            }
        }, 300L);
    }

    /**
     * hide keyboard
     */
    private void hideKeyboard() {
        chatPrimaryMenu.hideKeyboard();
    }

    /**
     * hide extend menu
     */
    public void hideExtendMenuContainer() {
        chatExtendMenu.setVisibility(View.GONE);
        emojiconMenu.setVisibility(View.GONE);
        chatExtendMenuContainer.setVisibility(View.GONE);
        chatPrimaryMenu.onExtendMenuContainerHide();
    }

    /**
     * when back key pressed
     * 
     * @return false--extend menu is on, will hide it first
     *         true --extend menu is off 
     */
    public boolean onBackPressed() {
        if (chatExtendMenuContainer.getVisibility() == View.VISIBLE) {
            hideExtendMenuContainer();
            return false;
        } else {
            return true;
        }

    }
    

    public void setChatInputMenuListener(ChatInputMenuListener listener) {
        this.listener = listener;
    }

    public interface ChatInputMenuListener {

        /**
         * when typing on the edit-text layout.
         */
        void onTyping(CharSequence s, int start, int before, int count);

        /**
         * when send message button pressed
         * 
         * @param content
         *            message content
         */
        void onSendMessage(String content);
        
        /**
         * when big icon pressed
         * @param emojicon
         */
        void onBigExpressionClicked(EaseEmojicon emojicon);

        /**
         * when speak button is touched
         * @param v
         * @param event
         * @return
         */
        boolean onPressToSpeakBtnTouch(View v, MotionEvent event);
    }

    public interface ChatMenuOpenListenter {
        void onOpen();
    }
    
}
