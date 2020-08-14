@file:Suppress("unused")

package com.yfbx.xwebview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

/**
 * Author: Edward
 * Date: 2019-11-14
 * Description:
 */
@SuppressLint("SetJavaScriptEnabled")
class XWebView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : WebView(context.createConfigurationContext(Configuration()), attrs, defStyleAttr), LifecycleObserver {

    private val client = WebClient()

    init {
        webViewClient = client

        settings.setSupportZoom(true)
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true

        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        settings.domStorageEnabled = true

        //允许混合模式，已解决http图片加载不出来问题
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        settings.blockNetworkImage = false

        //让页面可以获得焦点，以弹出键盘
        isFocusable = true
        isFocusableInTouchMode = true

        //开启硬件加速
        setLayerType(View.LAYER_TYPE_HARDWARE, null)

        //监听生命周期
        if (context is LifecycleOwner) {
            context.lifecycle.addObserver(this)
        }

    }

    /**
     * Accept Cookie
     */
    fun setAcceptCookie(acceptCookie: Boolean, acceptThirdPartyCookies: Boolean) {
        CookieManager.getInstance().setAcceptCookie(acceptCookie)
        CookieManager.getInstance().setAcceptThirdPartyCookies(this, acceptThirdPartyCookies)
    }

    /**
     *是否允许访问本地文件
     */
    fun setAccessFile(allow: Boolean) {
        settings.allowFileAccess = allow
        settings.allowFileAccessFromFileURLs = allow
        settings.allowUniversalAccessFromFileURLs = allow
    }

    /**
     *是否允许弹窗
     */
    fun setAutoOpenWindow(allow: Boolean) {
        settings.javaScriptCanOpenWindowsAutomatically = allow
    }


    /**
     * 加载内容
     * @param content url 或者  html文本
     * @param interceptor url拦截
     */
    fun load(content: String, interceptor: ((view: WebView?, url: String) -> Unit)? = null) {
        interceptor?.let { client.setIntercept(it) }
        //加载链接
        if (content.startsWith("http")) {
            loadUrl(content)
            return
        }
        //加载html文本
        if (content.contains("<!DOCTYPE html>")) {
            loadDataWithBaseURL(null, content, "text/html", "UTF-8", null)
            return
        }
    }

    /**
     * 脚本
     */
    fun setScripts(js: List<String>) {
        client.setScripts(js)
    }

    /**
     * 脚本
     */
    fun addScript(js: String) {
        client.addScript(js)
    }

    /**
     * 设置拦截监听
     */
    fun setIntercept(interceptor: (view: WebView?, url: String) -> Unit) {
        client.setIntercept(interceptor)
    }

    /**
     * 错误页面
     */
    fun setErrorPage(onError: (code: Int, msg: String, url: String) -> Unit) {
        client.setErrorPage(onError)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onActivityResume() {
        settings.javaScriptEnabled = true
    }

    /**
     * 在后台时，关闭js交互，以释放js占用资源
     * 优化cpu占用以及耗电问题
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onActivityStop() {
        settings.javaScriptEnabled = false
    }

    /**
     * 销毁时，从父容器移除WebView
     * 解决音视频播放退出后仍有声音问题
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onActivityDestroy() {
        val group = parent as? ViewGroup
        group?.removeView(this)
        removeAllViews()
        destroy()
    }


}
