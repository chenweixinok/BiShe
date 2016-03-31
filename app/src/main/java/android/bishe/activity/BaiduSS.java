package android.bishe.activity;

import android.app.Activity;
import android.bishe.R;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Lenovo on 2016/3/26.
 */
public class BaiduSS extends Activity{

    WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baiduss);
        webView = (WebView) findViewById(R.id.webView1);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return true;
            }
        });

        webView.setVerticalScrollbarOverlay(true);

        //网页中包含JavaScript内容需调用以下方法，参数为true
        webView.getSettings().setJavaScriptEnabled(true);

        //出现net::ERR_CACHE_MISS错误提示
        //使用缓存的方式是基于导航类型。正常页面加载的情况下将缓存内容。当导航返回,
        //内容不会恢复（重新加载生成）,而只是从缓存中取回内容
        if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        }
         webHtml();
    }

    private void webHtml() {

        try {
            webView.loadUrl("https://www.baidu.com");

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }



    //重写onKeyDown(keyCode, event)方法 改写物理按键 返回的逻辑
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            if(webView.canGoBack())
            {
                webView.goBack();//返回上一页面
                return true;
            }
            else
            {
                finish();//退出程序
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
