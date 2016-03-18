package android.bishe.fragment;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


import org.json.JSONException;
import org.json.JSONObject;


import android.bishe.R;
import android.bishe.Utils.HttpData;
import android.bishe.Utils.HttpGetDataListener;
import android.bishe.adapter.TextAdapter;
import android.bishe.bean.ListData;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;


/**
 * Created by Lenovo on 2016/2/26.
 */
public class TLXiaoJingLing extends Fragment implements android.view.View.OnClickListener,HttpGetDataListener {



    private HttpData httpData;
    private List<ListData> lists;

    private Context context;


    private ListView lv;
    private EditText sendtext;

    private Button send_btn;


    private String content_str;
    private TextAdapter adapter;

    private String [] welcome_array;

    private double currentTime,oldTime = 0;



    private SpeechSynthesizer mTts;
    private String TAG = "shitou";
    private String voicer="vinn";
    // 缓冲进度
    private int mPercentForBuffering = 0;
    // 播放进度
    private int mPercentForPlaying = 0;





    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.fragment_tuling, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {

        lv=(ListView) getView().findViewById(R.id.lv);
        sendtext=(EditText) getView().findViewById(R.id.sendText);
        send_btn=(Button) getView().findViewById(R.id.send_btn);

        lists=new ArrayList<ListData>();
        send_btn.setOnClickListener(this);

        adapter=new TextAdapter(lists, getActivity());
        lv.setAdapter(adapter);

        String randomWelcomeTips = getRandomWelcomeTips();
        ListData listData;
        listData=new ListData(randomWelcomeTips, ListData.RECEIVER,getTime());
        lists.add(listData);

        SpeechUtility.createUtility(getActivity(), "appid=56ea2ad9");


        mTts = SpeechSynthesizer.createSynthesizer(getActivity(), mTtsInitListener);
        setTtsParam();
        mTts.startSpeaking(randomWelcomeTips, mTtsListener);
    }



    private String getRandomWelcomeTips() {
        String welcome_tip = null;
        welcome_array = this.getResources().getStringArray(R.array.welcome_tips);
        int index = (int) (Math.random()*(welcome_array.length-1));
        welcome_tip = welcome_array[index];
        return welcome_tip;
    }

    @Override
    public void getDataUrl(String data) {
        //System.out.println(data);
        parseText(data);
    }

    public void parseText(String str){

        try {
            JSONObject jb=new JSONObject(str);
            //	System.out.println(jb.getString("code"));
            //	System.out.println(jb.getString("text"));

            String text = jb.getString("text");
            ListData listData;
            listData = new ListData(text,ListData.RECEIVER,getTime());
            lists.add(listData);
            adapter.notifyDataSetChanged();
            mTts.startSpeaking(text, mTtsListener);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }




    @Override
    public void onClick(View arg0) {
        content_str = "";
        if (arg0.getId() == R.id.send_btn) {
            content_str = sendtext.getText().toString();
            sendtext.setText("");
            sendContent(content_str);
        }

    }



    private void sendContent(String content_str) {
        if (!content_str.equals("")) {
            String dropk = content_str.replace(" ", "");
            String droph = dropk.replace("\n", "");
            ListData listData;
            listData = new ListData(content_str,ListData.SEND,getTime());
            lists.add(listData);
            if (lists.size()>30) {
                for (int i = 0; i < 10; i++) {
                    lists.remove(0);
                }
            }
            adapter.notifyDataSetChanged();
            httpData = (HttpData) new HttpData(
                    "http://www.tuling123.com/openapi/api?key=8f57ba49bb7d13c44599c8b0cd430773&info="+droph,
                    this).execute();
        }
    }




    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };


    /**
     * 参数设置
     *
     * @param param
     * @return
     */

    private void setTtsParam(){
        // 设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME,voicer);
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED,"80");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME,"100");
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE,"3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
    }






    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
        }

        @Override
        public void onSpeakPaused() {
        }

        @Override
        public void onSpeakResumed() {
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            mPercentForBuffering = percent;
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            mPercentForPlaying = percent;
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
            } else if (error != null) {
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };





    private String getTime(){
        currentTime=System.currentTimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date curDate = new Date((long) currentTime);
        String str=format.format(curDate);
        if (currentTime-oldTime >= 2*60*1000) {
            oldTime=currentTime;
            return str;
        }else {
            return  "";
        }
    }
}
