# x-webview



- 垂直居中    
  经测试，在html中各种方法都无法很好的实现垂直居中效果。最终想出一个在原生端实现垂直居中的方案：    
  `将WebView的高度设为wrap_content,然后让WebView在其父布局中居中`    
  目前这是最简单的方案    
  
  
- 事件响应    
  Webview 只有在调用其`onResume()`方法后才会响应事件(点击、缩放等)


# Exceptions

- Using WebView from more than one process at once with the same data directory is not supported
  Android P (9.0)不支持多个进程中共用同一 WebView 数据目录。
  解决方法：在进程启动时(Application)，将本进程的WebView数据目录后缀设置为进程名称，以区分数据目录
  ```
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
     WebView.setDataDirectorySuffix(Application.getProcessName())
  }
  ```
