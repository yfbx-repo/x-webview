package com.yfbx.demo

import android.app.Application
import android.os.Build
import android.webkit.WebView

/**
 * Author: Edward
 * Date: 2020-08-15
 * Description:
 */
class App : Application() {


    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            //将本进程的WebView数据目录后缀设置为进程名称
            WebView.setDataDirectorySuffix(getProcessName())
        }
    }
}