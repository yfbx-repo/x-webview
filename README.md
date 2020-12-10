# x-webview



- 垂直居中    
  经测试，在html中各种方法都无法很好的实现垂直居中效果。最终想出一个在原生端实现垂直居中的方案：    
  `将WebView的高度设为wrap_content,然后让WebView在其父布局中居中`    
  目前这是最简单的方案    
  
  
- 事件响应
  Webview 只有在调用其onResume()方法后才会响应事件(点击、缩放等)
