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

        //��ҳ�а���JavaScript������������·���������Ϊtrue
        webView.getSettings().setJavaScriptEnabled(true);

        //����net::ERR_CACHE_MISS������ʾ
        //ʹ�û���ķ�ʽ�ǻ��ڵ������͡�����ҳ����ص�����½��������ݡ�����������,
        //���ݲ���ָ������¼������ɣ�,��ֻ�Ǵӻ�����ȡ������
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



    //��дonKeyDown(keyCode, event)���� ��д������ ���ص��߼�
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            if(webView.canGoBack())
            {
                webView.goBack();//������һҳ��
                return true;
            }
            else
            {
                finish();//�˳�����
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
