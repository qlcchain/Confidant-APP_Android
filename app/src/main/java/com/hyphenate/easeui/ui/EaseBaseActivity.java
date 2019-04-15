/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hyphenate.easeui.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.hyphenate.easeui.EaseUI;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.base.ActivityDelegate;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.utils.SpUtil;

@SuppressLint({"NewApi", "Registered"})
public class EaseBaseActivity extends FragmentActivity implements ActivityDelegate {

    protected InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        AppConfig.instance.mAppActivityManager.addActivity(this);
        //http://stackoverflow.com/questions/4341600/how-to-prevent-multiple-instances-of-an-activity-when-it-is-launched-with-differ/
        // should be in launcher activity, but all app use this can avoid the problem
        if(!isTaskRoot()){
            Intent intent = getIntent();
            String action = intent.getAction();
            if(intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)){
                finish();
                return;
            }
        }
        String screenshotsSettingFlag = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getScreenshotsSetting(), "1");
        if(screenshotsSettingFlag.equals("1"))
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }
    

    @Override
    protected void onResume() {
        super.onResume();
        // cancel the notification
        EaseUI.getInstance().getNotifier().reset();
    }
    
    protected void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * back
     * 
     * @param view
     */
    public void back(View view) {
        finish();
    }

    @Override
    public void destoryContainer() {
        finish();
    }

    @Override
    public Activity getContainerActivity() {
        return this;
    }

    @Override
    public boolean isContainerDead() {
        return false;
    }
}
