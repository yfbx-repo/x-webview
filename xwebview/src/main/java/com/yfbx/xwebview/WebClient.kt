package com.yfbx.xwebview

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.webkit.*
import androidx.annotation.RequiresApi

/**
 * Author: Edward
 * Date: 2020-08-14
 * Description:
 */
class WebClient : WebViewClient() {


    private var onError: ((code: Int, msg: String, url: String) -> Unit)? = null
    private var interceptor: ((view: WebView?, url: String) -> Unit)? = null
    private val jsScripts = mutableListOf<String>()

    /**
     * 设置拦截监听
     */
    fun setIntercept(interceptor: (view: WebView?, url: String) -> Unit) {
        this.interceptor = interceptor
    }

    /**
     * 错误页面
     */
    fun setErrorPage(onError: (code: Int, msg: String, url: String) -> Unit) {
        this.onError = onError
    }

    fun setScripts(js: List<String>) {
        jsScripts.clear()
        jsScripts.addAll(js)
    }

    fun addScript(js: String) {
        jsScripts.add(js)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        //开始加载时，不自动加载图片，以优化加载速度
        view?.settings?.loadsImagesAutomatically = false
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (view == null) return true
        val url = request?.url.toString()

        //有interceptor 交给 interceptor 处理
        if (interceptor != null) {
            interceptor?.invoke(view, url)
            return true
        }

        //没有interceptor 自动处理
        if (url.startsWith("http")) {
            view.loadUrl(url)
        } else {
            intentTo(view.context, url)
        }
        return true
    }

    //忽略ssl证书错误
    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        handler?.proceed()
    }

    //忽略太多重定向导致取消加载
    override fun onTooManyRedirects(view: WebView?, cancelMsg: Message?, continueMsg: Message?) {
        //super.onTooManyRedirects(view, cancelMsg, continueMsg)
        //ignore
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        view?.let { loadScripts(it) }
        super.onPageFinished(view, url)
        //结束加载时，再开启图片加载
        view?.settings?.loadsImagesAutomatically = true
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        if (error != null && request != null) {
            val code = error.errorCode
            val msg = error.description.toString()
            val url = request.url.toString()
            onError?.invoke(code, msg, url)
        } else {
            onError?.invoke(0, "未知错误", "")
        }
    }

    /**
     * 加载脚本
     */
    private fun loadScripts(web: WebView) {
        for (js in jsScripts) {
            web.loadUrl("javascript:$js")
        }
    }


    /**
     * 打开 Scheme
     */
    private fun intentTo(context: Context, uri: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse(uri)
            context.startActivity(intent)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
}