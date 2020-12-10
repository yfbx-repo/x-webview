package com.yfbx.xwebview

import android.webkit.MimeTypeMap
import java.util.*

/**
 * Author: Edward
 * Date: 2020-12-10
 * Description:扩展
 */

/**
 * 加载媒体资源
 */
fun XWebView.loadMedia(path: String) {
    val url = assembleUrl(path)
    val ext = path.substringAfterLast(".").toLowerCase(Locale.getDefault())
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
    when {
        mimeType == null -> load(url)
        mimeType.endsWith("pdf") -> loadPDF(url)//MimeType: application/pdf
        mimeType.startsWith("image") -> loadImage(url)
        mimeType.startsWith("video") -> loadVideo(url)
        else -> load(url)
    }
}


/**
 * 加载视频
 */
fun XWebView.loadVideo(url: String) {
    val content = """
           <!DOCTYPE html>
           <html>
             <body style="margin:0;">
               <video src="$url" autoplay controls style="width:100%;display:block"/>
             </body>
           </html>
        """
    loadDataWithBaseURL(null, content, "text/html", "UTF-8", null)
}

/**
 * 加载图片
 */
fun XWebView.loadImage(url: String) {
    val content = """
           <!DOCTYPE html>
           <html>
             <body style="margin:0;">
               <img src="$url" style="width:100%;display:block"/>
             </body>
           </html>
        """
    loadDataWithBaseURL(null, content, "text/html", "UTF-8", null)
}

/**
 * 加载PDF
 */
fun XWebView.loadPDF(pdfUrl: String) {
    loadUrl("https://static.yuxiaor.com/web/viewer.html?file=$pdfUrl")
}

/**
 * 路径处理
 */
fun assembleUrl(path: String): String {
    return when {
        path.startsWith("http") -> path
        path.startsWith("/storage") -> "file://$path"
        else -> path//可能需要拼接域名
    }
}