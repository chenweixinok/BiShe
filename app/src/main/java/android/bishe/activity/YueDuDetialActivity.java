package android.bishe.activity;

import android.app.Activity;
import android.bishe.R;
import android.bishe.Utils.DateTime;
import android.bishe.Utils.MySqlOpenHelper;
import android.bishe.Utils.SharedPreferencesUtil;
import android.bishe.bean.ServerURL;
import android.bishe.view.CustomProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2016/3/20.
 */
public class YueDuDetialActivity extends Activity{



    private RelativeLayout scroll;
    private TextView tv_title;
    private TextView msource;
    private TextView mptime;
    private ImageView iv;
    private WebView webView;
    private String DetialUrl;//阅读详情页URL
    private HttpUtils httpUtils;
    private String replyid;

    private CustomProgressDialog progressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yuedu_detial);
        initView();
       // ZiTiScale.zitiStyle(this, webView);
    }


    private void initView() {
        progressDialog = new CustomProgressDialog(this, "正在加载中......", R.anim.donghua_frame);

        //title_gentie = (TextView) findViewById(R.id.title_gentie);
        //rightmore_content = (ImageButton) findViewById(R.id.rightmore_content);

        scroll = (RelativeLayout) findViewById(R.id.scroll);
        tv_title = (TextView) findViewById(R.id.tv_title);
        msource = (TextView) findViewById(R.id.source);
        mptime = (TextView) findViewById(R.id.ptime);
        iv = (ImageView) findViewById(R.id.iv);
        webView = (WebView) findViewById(R.id.webView);
        //iv_writeGenTie = (ImageButton) findViewById(R.id.iv_writeGenTie);
        //tv_writeGenTie = (TextView) findViewById(R.id.tv_writeGenTie);
        //replyNum = (ImageButton) findViewById(R.id.replyNum);
        //tv_num = (TextView) findViewById(R.id.tv_num);
        //share = (ImageButton) findViewById(R.id.share);
        //settings = webView.getSettings();

        //popwindow_more = View.inflate(this, R.layout.popwindow_detial, null);
       // rightmore_content.setOnClickListener(new View.OnClickListener() {
         //   @Override
        //    public void onClick(View view) {
        //        initPopWindow(view);
        //    }
     //   });

        Intent intent = getIntent();
        replyid = intent.getStringExtra("yueduDetial");
        DetialUrl = ServerURL.yueDuDetial + replyid + ServerURL.yueDuHouzui;

        inintData();
    }

    private void inintData() {
        //共享参数缓存  首先从缓存中获取数据,
        String result = SharedPreferencesUtil.getData(this, DetialUrl, "");
        if (!TextUtils.isEmpty(result)) {
            paserData(result);
            return;
        }
        getData(DetialUrl);
    }


    private HttpHandler<String> handler;

    private void getData(final String url) {
        if (!url.equals("")) {
            httpUtils = new HttpUtils();
            progressDialog.show();
            handler = httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    if (responseInfo.result != null) {
                        SharedPreferencesUtil.saveData(YueDuDetialActivity.this, url, responseInfo.result);
                        paserData(responseInfo.result);
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(YueDuDetialActivity.this, "数据请求失败,请检查网络设置...", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(this, "链接地址有误.........", Toast.LENGTH_SHORT).show();
        }
    }

    //String replyCount;//跟帖数
    String title;
    String body;
    String src = null;
    private void paserData(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject object1 = jsonObject.getJSONObject(replyid);
            JSONArray jsonArray = object1.getJSONArray("img");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                src = jo.getString("src");
            }

            body = object1.getString("body");
            String ptime = object1.getString("ptime");
            //replyCount = object1.getString("replyCount");
            String source = object1.getString("source");
            title = object1.getString("title");

            tv_title.setText(title);//标题
            msource.setText(source);//来源
            mptime.setText(ptime);//时间
            //title_gentie.setText(replyCount + " 跟帖");//跟帖数
            //title_gentie.setTextSize(15);
            //title_gentie.setTextColor(Color.WHITE);
            //tv_num.setText(replyCount);
            webView.loadData(body, "text/html;charset=UTF-8", null);//具体内容
            BitmapUtils bitmapUtils = new BitmapUtils(this);
            bitmapUtils.display(iv, src);
            scroll.setVisibility(View.VISIBLE);//设置布局可见
            progressDialog.dismiss();
            initDate();//保存阅读记录到数据库
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (httpUtils != null) {
            handler.cancel();
        }
    }

    public void initDate(){
//        String url = xinWenXiData.getUrl();//获得详细页面的url      //分享用
//        String xinwentitle = xinWenXiData.getTitle();//获得新闻标题     //分享用
//        int replaycount = xinWenXiData.getReplaycount();//获得跟帖数目  //收藏用
//        Log.e("aa", "******xinwentitle*******" + xinwentitle);
        //拿到当前日期
        String date = DateTime.getDate();
        MySqlOpenHelper mySqlOpenHelper = new MySqlOpenHelper(this);
        SQLiteDatabase writableDatabase = mySqlOpenHelper.getWritableDatabase();
        //查询数据库  当前日期 有无存储过 本页的标题
        Cursor cursor = writableDatabase.query("read_date", null, "date =?",
                new String[]{date}, null, null, null, null);
        //有没有当天的数据
        if (cursor.getCount()>0) {
            ArrayList<String> biaoti = new ArrayList<>();//声明一个集合,用来存放遍历出来的标题
            while (cursor.moveToNext()) {   //遍历  拿到当天的 所有存储的标题
                String cursorString = cursor.getString(cursor.getColumnIndex("title"));
                biaoti.add(cursorString);
            }
            //当天数据中没有 本页的标题
            if (!biaoti.contains(title)) {
                ContentValues values = new ContentValues();
                values.put("date", date+"");
                values.put("url", replyid+"");
                values.put("title", title+"");
                values.put("num", 1+"");
//            values.put("url",url);存储详情页的地址  在 阅读记录里取出来
                writableDatabase.insert("read_date", null, values);
            }
        } else {
            ContentValues values = new ContentValues();
            values.put("date", date+"");
            values.put("url", replyid+"");
            values.put("title", title+"");
            values.put("num", 1+"");
            writableDatabase.insert("read_date", null, values);
        }
        //关闭
        cursor.close();
        writableDatabase.close();
    }


}
