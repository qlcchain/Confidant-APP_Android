package com.stratagile.pnrouter.ui.activity.main

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Px
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.Window
import android.view.WindowManager
import com.nightonke.wowoviewpager.Animation.ViewAnimation
import com.nightonke.wowoviewpager.Animation.WoWoPositionAnimation
import com.nightonke.wowoviewpager.Animation.WoWoTranslationAnimation
import com.nightonke.wowoviewpager.Enum.Ease
import com.nightonke.wowoviewpager.WoWoViewPagerAdapter
import com.pawegio.kandroid.startActivity
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerGuestComponent
import com.stratagile.pnrouter.ui.activity.main.contract.GuestContract
import com.stratagile.pnrouter.ui.activity.main.module.GuestModule
import com.stratagile.pnrouter.ui.activity.main.presenter.GuestPresenter
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.UIUtils
import com.stratagile.pnrouter.utils.VersionUtil
import kotlinx.android.synthetic.main.activity_guest.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/18 14:25:55
 */

class GuestActivity : BaseActivity(), GuestContract.View {

    @Inject
    internal lateinit var mPresenter: GuestPresenter

    private var animationAdded = false

    private var r: Int = 0

    protected var screenW: Int = 0
    protected var screenH: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        window.requestFeature(Window.FEATURE_ACTION_BAR)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
    }

    protected fun fragmentNumber(): Int {
        return 3
    }

    protected fun fragmentColorsRes(): Array<Int> {
        return arrayOf(R.color.white, R.color.white, R.color.white)
    }

    override fun initView() {
        setContentView(R.layout.activity_guest)
        var wowoAdapter = WoWoViewPagerAdapter.builder()
                .fragmentManager(supportFragmentManager)
                .count(fragmentNumber())                       // Fragment Count
                .colorsRes(*fragmentColorsRes())                // Colors of fragments
                .build()
        wowo.setAdapter(wowoAdapter)
        screenW = UIUtils.getDisplayWidth(this)
        screenH = UIUtils.getDisplayHeigh(this)

        r = Math.sqrt((screenW * screenW + screenH * screenH).toDouble()).toInt() + 10

        wowo.addTemporarilyInvisibleViews(1, iv2, tvPage2)

        wowo.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{

            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageSelected(p0: Int) {
                if (p0 == 2) {
                    tvNext.text = getString(R.string.QR_Code)
                } else {
                    tvNext.text = resources.getString(R.string.next)
                }
            }

        })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        addAnimations()
    }

    private fun addAnimations() {
        if (animationAdded) {
            return
        }
        animationAdded = true
        addBack()
        addIv0()
        addTvPage0()
        addTvPage0TestNet()

        addIv1()
        addTvPage1()

        addIv2()
        addTvPage2()
        addGotIt()

        addDot()

        wowo.ready()
    }

    protected fun color(colorRes: Int): Int {
        return ContextCompat.getColor(this, colorRes)
    }

    private fun addTvPage0TestNet() {
//        wowo.addAnimation(tvPage0TestNet)
//                .add(WoWoTranslationAnimation.builder().page(0)
//                        .fromX(0f).toX((-screenW).toFloat())
//                        .fromY(0f).toY(-600f).build())
    }

    private fun addTvPage0() {
        wowo.addAnimation(tvPage0)
                .add(WoWoTranslationAnimation.builder().page(0)
                        .fromX(0f).toX(screenW.toFloat())
                        .fromY(0f).toY(500f).build())
    }


    private fun addIv0() {
        wowo.addAnimation(iv0)
                .add(WoWoTranslationAnimation.builder().page(0)
                        .fromX(0f).toX((-screenW).toFloat())
                        .keepY(0f).toY(-500f).build())
    }

    private fun addTvPage1() {
        wowo.addAnimation(tvPage1)
                .add(WoWoTranslationAnimation.builder().page(0)
                        .fromX((-screenW).toFloat()).toX(0f)
                        .fromY(500f).toY(0f).build())
                .add(WoWoTranslationAnimation.builder().page(1)
                        .fromX(0f).toX(screenW.toFloat())
                        .fromY(0f).toY(500f).build())
    }


    private fun addIv1() {
        wowo.addAnimation(iv1)
                .add(WoWoTranslationAnimation.builder().page(0)
                        .fromX(screenW.toFloat()).toX(0f)
                        .keepY(-500f).toY(0f).build())
                .add(WoWoTranslationAnimation.builder().page(1)
                        .fromX(0f).toX((-screenW).toFloat())
                        .keepY(0f).toY(-500f).build())
    }

    private fun addTvPage2() {
        wowo.addAnimation(tvPage2)
                .add(WoWoTranslationAnimation.builder().page(1)
                        .fromX((-screenW).toFloat()).toX(0f)
                        .fromY(500f).toY(0f).build())
                .add(WoWoTranslationAnimation.builder().page(1)
                        .fromX(0f).toX(screenW.toFloat())
                        .fromY(0f).toY(500f).build())
    }


    private fun addIv2() {
        wowo.addAnimation(iv2)
                .add(WoWoTranslationAnimation.builder().page(1)
                        .fromX(screenW.toFloat()).toX(0f)
                        .keepY(-500f).toY(0f).build())
                .add(WoWoTranslationAnimation.builder().page(1)
                        .fromX(0f).toX((-screenW).toFloat())
                        .keepY(0f).toY(-500f).build())
    }

    private fun addGotIt() {
//        wowo.addAnimation(gotIt)
//                .add(WoWoTranslationAnimation.builder().page(1)
//                        .keepX(gotIt.getTranslationX())
//                        .fromY(screenH.toFloat()).toY(0f).ease(Ease.OutBack)
//                        .build())
//                .add(WoWoTranslationAnimation.builder().page(1)
//                        .keepX(0f)
//                        .fromY(0f).toY(screenH.toFloat())
//                        .ease(Ease.InCubic).sameEaseBack(false).build())
    }

    private fun addBack() {
        wowo.addAnimation(ivBack)
    }

    private fun addDot() {
        val viewAnimation = ViewAnimation(dot)
        viewAnimation.add(WoWoPositionAnimation.builder().page(0)
                .fromX( -resources.getDimension(R.dimen.x15) + dot.width).toX(dot.x)
                .keepY(0f)
                .ease(Ease.Linear).build())
        viewAnimation.add(WoWoPositionAnimation.builder().page(1)
                .fromX(dot.x).toX(dot.x + resources.getDimension(R.dimen.x20) + dot.width)
                .keepY(0f)
                .ease(Ease.Linear).build())
        wowo.addAnimation(viewAnimation)
    }

    override fun initData() {
        SpUtil.putInt(this, ConstantValue.LOCALVERSIONCODE, VersionUtil.getAppVersionCode(this))

        tvNext.setOnClickListener {
            if (wowo.currentItem == 2) {
                startActivity(Intent(this, LoginActivityActivity::class.java))
            } else {
                wowo.next()
            }
        }
    }

    override fun setupActivityComponent() {
       DaggerGuestComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .guestModule(GuestModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: GuestContract.GuestContractPresenter) {
            mPresenter = presenter as GuestPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}