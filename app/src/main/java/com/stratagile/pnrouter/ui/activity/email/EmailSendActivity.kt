package com.stratagile.pnrouter.ui.activity.email

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.email.component.DaggerEmailSendComponent
import com.stratagile.pnrouter.ui.activity.email.contract.EmailSendContract
import com.stratagile.pnrouter.ui.activity.email.module.EmailSendModule
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailSendPresenter
import com.stratagile.pnrouter.ui.activity.email.view.ColorPickerView
import com.stratagile.pnrouter.ui.activity.email.view.RichEditor
import kotlinx.android.synthetic.main.email_rich_edit.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: $description
 * @date 2019/07/25 11:21:29
 */

class EmailSendActivity : BaseActivity(), EmailSendContract.View,View.OnClickListener {

    /********************boolean开关 */
    //是否加粗
    internal var isClickBold = false
    //是否正在执行动画
    internal var isAnimating = false
    //是否按ol排序
    internal var isListOl = false
    //是否按ul排序
    internal var isListUL = false
    //是否下划线字体
    internal var isTextLean = false
    //是否下倾斜字体
    internal var isItalic = false
    //是否左对齐
    internal var isAlignLeft = false
    //是否右对齐
    internal var isAlignRight = false
    //是否中对齐
    internal var isAlignCenter = false
    //是否缩进
    internal var isIndent = false
    //是否较少缩进
    internal var isOutdent = false
    //是否索引
    internal var isBlockquote = false
    //字体中划线
    internal var isStrikethrough = false
    //字体上标
    internal var isSuperscript = false
    //字体下标
    internal var isSubscript = false
    /********************变量 */
    //折叠视图的宽高
    private var mFoldedViewMeasureHeight: Int = 0
    @Inject
    internal lateinit var mPresenter: EmailSendPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.email_rich_edit)
    }
    override fun initData() {
        initUI()
        initClickListener()
    }

    /**
     * 初始化View
     */
    private fun initUI() {
        initEditor()
        initMenu()
        initColorPicker()
    }
    /**
     * 初始化文本编辑器
     */
    private fun initEditor() {
        //re_main_editor.setEditorHeight(400);
        //输入框显示字体的大小
        re_main_editor.setEditorFontSize(18)
        //输入框显示字体的颜色
        re_main_editor.setEditorFontColor(Color.BLACK)
        //输入框背景设置
        re_main_editor.setEditorBackgroundColor(Color.WHITE)
        //re_main_editor.setBackgroundColor(Color.BLUE);
        //re_main_editor.setBackgroundResource(R.drawable.bg);
        //re_main_editor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        //输入框文本padding
        re_main_editor.setPadding(10, 10, 10, 10)
        //输入提示文本
        re_main_editor.setPlaceholder("请输入编辑内容")
        //是否允许输入
        //re_main_editor.setInputEnabled(false);
        //文本输入框监听事件
        re_main_editor.setOnTextChangeListener(object : RichEditor.OnTextChangeListener {
           override fun onTextChange(text: String) {
                Log.d("re_main_editor", "html文本：$text")
            }
        })
    }

    /**
     * 初始化颜色选择器
     */
    private fun initColorPicker() {
        cpv_main_color.setOnColorPickerChangeListener(object : ColorPickerView.OnColorPickerChangeListener {
            override fun onColorChanged(picker: ColorPickerView, color: Int) {
                button_text_color.setBackgroundColor(color)
                re_main_editor.setTextColor(color)
            }

           override  fun onStartTrackingTouch(picker: ColorPickerView) {

            }

            override fun onStopTrackingTouch(picker: ColorPickerView) {

            }
        })
    }

    /**
     * 初始化菜单按钮
     */
    private fun initMenu() {
        getViewMeasureHeight()
    }

    /**
     * 获取控件的高度
     */
    private fun getViewMeasureHeight() {
        //获取像素密度
        val mDensity = resources.displayMetrics.density
        //获取布局的高度
        val w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED)
        val h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED)
        ll_main_color.measure(w, h)
        val height = ll_main_color.getMeasuredHeight()
        mFoldedViewMeasureHeight = (mDensity * height + 0.5).toInt()
    }

    private fun initClickListener() {
        button_bold.setOnClickListener(this)
        button_text_color!!.setOnClickListener(this)
        tv_main_preview.setOnClickListener(this)
        button_image.setOnClickListener(this)
        button_list_ol.setOnClickListener(this)
        button_list_ul.setOnClickListener(this)
        button_underline.setOnClickListener(this)
        button_italic.setOnClickListener(this)
        button_align_left.setOnClickListener(this)
        button_align_right.setOnClickListener(this)
        button_align_center.setOnClickListener(this)
        button_indent.setOnClickListener(this)
        button_outdent.setOnClickListener(this)
        action_blockquote.setOnClickListener(this)
        action_strikethrough.setOnClickListener(this)
        action_superscript.setOnClickListener(this)
        action_subscript.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.button_bold) {//字体加粗
            if (isClickBold) {
                button_bold.setImageResource(R.mipmap.bold)
            } else {  //加粗
                button_bold.setImageResource(R.mipmap.bold_)
            }
            isClickBold = !isClickBold
            re_main_editor.setBold()
        } else if (id == R.id.button_text_color) {//设置字体颜色
            //如果动画正在执行,直接return,相当于点击无效了,不会出现当快速点击时,
            // 动画的执行和ImageButton的图标不一致的情况
            if (isAnimating) return
            //如果动画没在执行,走到这一步就将isAnimating制为true , 防止这次动画还没有执行完毕的
            //情况下,又要执行一次动画,当动画执行完毕后会将isAnimating制为false,这样下次动画又能执行
            isAnimating = true

            if (ll_main_color.getVisibility() == View.GONE) {
                //打开动画
                animateOpen(ll_main_color)
            } else {
                //关闭动画
                animateClose(ll_main_color)
            }
        } else if (id == R.id.button_image) {//插入图片
            //这里的功能需要根据需求实现，通过insertImage传入一个URL或者本地图片路径都可以，这里用户可以自己调用本地相
            //或者拍照获取图片，传图本地图片路径，也可以将本地图片路径上传到服务器（自己的服务器或者免费的七牛服务器），
            //返回在服务端的URL地址，将地址传如即可（我这里传了一张写死的图片URL，如果你插入的图片不现实，请检查你是否添加
            // 网络请求权限<uses-permission android:name="android.permission.INTERNET" />）
            re_main_editor.insertImage("http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG",
                    "dachshund")
        } else if (id == R.id.button_list_ol) {
            if (isListOl) {
                button_list_ol.setImageResource(R.mipmap.list_ol)
            } else {
                button_list_ol.setImageResource(R.mipmap.list_ol_)
            }
            isListOl = !isListOl
            re_main_editor.setNumbers()
        } else if (id == R.id.button_list_ul) {
            if (isListUL) {
                button_list_ul.setImageResource(R.mipmap.list_ul)
            } else {
                button_list_ul.setImageResource(R.mipmap.list_ul_)
            }
            isListUL = !isListUL
            re_main_editor.setBullets()
        } else if (id == R.id.button_underline) {
            if (isTextLean) {
                button_underline.setImageResource(R.mipmap.underline)
            } else {
                button_underline.setImageResource(R.mipmap.underline_)
            }
            isTextLean = !isTextLean
            re_main_editor.setUnderline()
        } else if (id == R.id.button_italic) {
            if (isItalic) {
                button_italic.setImageResource(R.mipmap.lean)
            } else {
                button_italic.setImageResource(R.mipmap.lean_)
            }
            isItalic = !isItalic
            re_main_editor.setItalic()
        } else if (id == R.id.button_align_left) {
            if (isAlignLeft) {
                button_align_left.setImageResource(R.mipmap.align_left)
            } else {
                button_align_left.setImageResource(R.mipmap.align_left_)
            }
            isAlignLeft = !isAlignLeft
            re_main_editor.setAlignLeft()
        } else if (id == R.id.button_align_right) {
            if (isAlignRight) {
                button_align_right.setImageResource(R.mipmap.align_right)
            } else {
                button_align_right.setImageResource(R.mipmap.align_right_)
            }
            isAlignRight = !isAlignRight
            re_main_editor.setAlignRight()
        } else if (id == R.id.button_align_center) {
            if (isAlignCenter) {
                button_align_center.setImageResource(R.mipmap.align_center)
            } else {
                button_align_center.setImageResource(R.mipmap.align_center_)
            }
            isAlignCenter = !isAlignCenter
            re_main_editor.setAlignCenter()
        } else if (id == R.id.button_indent) {
            if (isIndent) {
                button_indent.setImageResource(R.mipmap.indent)
            } else {
                button_indent.setImageResource(R.mipmap.indent_)
            }
            isIndent = !isIndent
            re_main_editor.setIndent()
        } else if (id == R.id.button_outdent) {
            if (isOutdent) {
                button_outdent.setImageResource(R.mipmap.outdent)
            } else {
                button_outdent.setImageResource(R.mipmap.outdent_)
            }
            isOutdent = !isOutdent
            re_main_editor.setOutdent()
        } else if (id == R.id.action_blockquote) {
            if (isBlockquote) {
                action_blockquote.setImageResource(R.mipmap.blockquote)
            } else {
                action_blockquote.setImageResource(R.mipmap.blockquote_)
            }
            isBlockquote = !isBlockquote
            re_main_editor.setBlockquote()
        } else if (id == R.id.action_strikethrough) {
            if (isStrikethrough) {
                action_strikethrough.setImageResource(R.mipmap.strikethrough)
            } else {
                action_strikethrough.setImageResource(R.mipmap.strikethrough_)
            }
            isStrikethrough = !isStrikethrough
            re_main_editor.setStrikeThrough()
        } else if (id == R.id.action_superscript) {
            if (isSuperscript) {
                action_superscript.setImageResource(R.mipmap.superscript)
            } else {
                action_superscript.setImageResource(R.mipmap.superscript_)
            }
            isSuperscript = !isSuperscript
            re_main_editor.setSuperscript()
        } else if (id == R.id.action_subscript) {
            if (isSubscript) {
                action_subscript.setImageResource(R.mipmap.subscript)
            } else {
                action_subscript.setImageResource(R.mipmap.subscript_)
            }
            isSubscript = !isSubscript
            re_main_editor.setSubscript()
        } else if (id == R.id.tv_main_preview) {//预览
           /* val intent = Intent(this@EmailSendActivity, WebDataActivity::class.java)
            intent.putExtra("diarys", re_main_editor.getHtml())
            startActivity(intent)*/
        }//H1--H6省略，需要的自己添加
    }

    /**
     * 开启动画
     *
     * @param view 开启动画的view
     */
    private fun animateOpen(view: LinearLayout) {
        view.visibility = View.VISIBLE
        val animator = createDropAnimator(view, 0, mFoldedViewMeasureHeight)
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                isAnimating = false
            }
        })
        animator.start()
    }

    /**
     * 关闭动画
     *
     * @param view 关闭动画的view
     */
    private fun animateClose(view: LinearLayout) {
        val origHeight = view.height
        val animator = createDropAnimator(view, origHeight, 0)
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.GONE
                isAnimating = false
            }
        })
        animator.start()
    }


    /**
     * 创建动画
     *
     * @param view  开启和关闭动画的view
     * @param start view的高度
     * @param end   view的高度
     * @return ValueAnimator对象
     */
    private fun createDropAnimator(view: View, start: Int, end: Int): ValueAnimator {
        val animator = ValueAnimator.ofInt(start, end)
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.height = value
            view.layoutParams = layoutParams
        }
        return animator
    }
    override fun setupActivityComponent() {
       DaggerEmailSendComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .emailSendModule(EmailSendModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: EmailSendContract.EmailSendContractPresenter) {
            mPresenter = presenter as EmailSendPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}