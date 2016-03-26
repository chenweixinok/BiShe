package android.bishe.activity;


import android.bishe.R;
import android.bishe.Utils.ImageUtil;
import android.bishe.Utils.MusicUtils;
import android.bishe.bean.Music;
import android.bishe.service.MusicService;
import android.bishe.view.LrcView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Lenovo on 2016/2/26.
 */
public class MusicActivity extends FragmentActivity implements View.OnClickListener {


    private View view;
    private PopupWindow pop;
    private ImageView btnShowAtLocation;


    private ListView listView;


    private ImageView pre, play, next, newstart;
    public static TextView singTitle, singer, beging_tv, total_tv;
    public static SeekBar skbProgress;
    public static ImageView musicImg;
    public static LrcView lrcView;



    public List<Music> musicList;//播放列表中的音乐
    private PlayListAdapter adapter;



    ActivityReceiver activityReceiver;
    public static final String CTL_ACTION = "android.music1.CTL_ACTION";
    public static final String UPDATE_ACTION = "android.music1.UPDATE_ACTION";
    Intent intentservice;
    // 定义音乐的播放状态 ，0X11 代表停止 ，0x12代表播放,0x13代表暂停
    int status = 0x11;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fra_music);

        musicList = getMusicList();// 获取音乐
        if (musicList == null || musicList.size() == 0) {// 如果音乐为空
            musicList = MusicUtils.getMusicData(getApplicationContext());//获取所有音乐
        }

        /**
         * 音乐列表
         */
        initPopupWindow();


        initUI();


        beging_tv = (TextView) findViewById(R.id.currentTime);
        total_tv = (TextView) findViewById(R.id.totalTime);
        skbProgress = (SeekBar) findViewById(R.id.playProgress);
        //进度条监听器
        skbProgress.setOnSeekBarChangeListener(new MySeekBarListener());

        listView = (ListView) view.findViewById(R.id.pop_listview);
        adapter = new PlayListAdapter();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent=new Intent(CTL_ACTION);
                intent.putExtra("control", 5);
                intent.putExtra("current", position);
                sendBroadcast(intent);

            }
        });


        activityReceiver = new ActivityReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_ACTION);
        registerReceiver(activityReceiver, filter);
        intentservice = new Intent(this, MusicService.class);
        startService(intentservice);


    }

    private void initUI() {



        pre = (ImageView) findViewById(R.id.music_pre);
        play = (ImageView) findViewById(R.id.music_play);
        next = (ImageView) findViewById(R.id.music_next);
        newstart = (ImageView) findViewById(R.id.newStart);

        singTitle = (TextView) findViewById(R.id.singerTitle);
        singer = (TextView) findViewById(R.id.singer);
        musicImg = (ImageView) findViewById(R.id.songIcon);
        musicImg.setImageBitmap(ImageUtil.toRoundBitmap(convertResToBm(R.drawable.json160)));


        lrcView = (LrcView) findViewById(R.id.music_LrcView);


        pre.setOnClickListener(this);
        play.setOnClickListener(this);
        next.setOnClickListener(this);
        newstart.setOnClickListener(this);

    }




    private Bitmap convertResToBm(int id){
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 2;  //将原图缩小四分之一读取
        option.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeResource(this.getResources(),id, option);

        return  ThumbnailUtils.extractThumbnail(bm, 480, 800); //将图片的大小限定在480*800
    }





    public static void setMusicSingTitle(String singT, String singe) {

        singTitle.setText(singT);
        singer.setText(singe);


    }



    private void initPopupWindow() {
        btnShowAtLocation = (ImageView) findViewById(R.id.liebiao);
        btnShowAtLocation.setOnClickListener(this);


        WindowManager wm = this.getWindowManager();

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        view = this.getLayoutInflater().inflate(R.layout.popup_window, null);
        pop = new PopupWindow(view, width - 6,
                600);
        pop.setAnimationStyle(R.style.popwin_anim_style);
        // pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOutsideTouchable(false);
        pop.setFocusable(true);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);

        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                if (arg1 == KeyEvent.KEYCODE_BACK) {
                    if (pop != null) {
                        pop.dismiss();
                    }
                }
                return false;
            }
        });

    }




    public List<Music> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(CTL_ACTION);

        switch (v.getId()) {
            case R.id.music_pre: // 上一首
                intent.putExtra("control", 1);
                break;
            case R.id.music_play:
                intent.putExtra("control", 2);
                break;

            case R.id.music_next: // 上一首
                intent.putExtra("control", 3);
                break;

            case R.id.newStart:
                intent.putExtra("control", 4);
                break;

            case R.id.liebiao:

                if (pop.isShowing()) {
                    pop.dismiss();
                } else {
                    pop.showAtLocation(v, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 165);
                }

                break;

        }
        sendBroadcast(intent);
    }



    private final class MySeekBarListener implements SeekBar.OnSeekBarChangeListener {
        //移动触发
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        //起始触发
        public void onStartTrackingTouch(SeekBar seekBar) {
            MusicService.isChanging = true;
        }

        //结束触发
        public void onStopTrackingTouch(SeekBar seekBar) {
            MusicService.mPlayer.seekTo(seekBar.getProgress());
            MusicService.isChanging = false;
        }
    }




    public class ActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取Intent中的update消息，update代表播放状态
            int update = intent.getIntExtra("update", -1);
            int current1 = intent.getIntExtra("current", -1);
            //int position = intent.getIntExtra("position", -1);
            //beging_tv.setText(MusicUtils.timeToString(position));
            total_tv.setText(MusicUtils.timeToString(musicList.get(current1).getTime()));
            // skbProgress.setProgress(position);
            skbProgress.setMax(musicList.get(current1).getTime());
            switch (update) {
                case 0x11: {
                    play.setImageResource(R.drawable.playing_button);
                    status = 0x11;
                    break;
                }

                // 控制系统进入播放状态
                case 0x12: {
                    // 播放状态下设置使用按钮
                    play.setImageResource(R.drawable.pause_button);
                    // 设置当前状态
                    status = 0x12;
                    break;
                }
                // 控制系统进入暂停状态
                case 0x13: {
                    play.setImageResource(R.drawable.playing_button);
                    status = 0x13;
                    break;
                }
            }
        }

    }



    class PlayListAdapter extends BaseAdapter {
        public int getCount() {
            return musicList.size();
        }

        public Object getItem(int position) {
            return musicList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.musicsshow,
                        parent, false);
            }
            ImageView icon = (ImageView) convertView.findViewById(R.id.music_img);// 显示图标的控件
            TextView title = (TextView) convertView.findViewById(R.id.name);// 显示歌曲名的控件
            TextView artist = (TextView) convertView.findViewById(R.id.artist);// 显示演唱者的控件
            TextView time = (TextView) convertView.findViewById(R.id.music_time);// 显示时间的控件
            Bitmap bitmap = MusicUtils.getAlbumPic(getApplicationContext(), musicList.get(position));//显示专辑图片的控件
            if (bitmap != null) {//如果专辑图片不为空，则显示；如果为空，则显示默认图片
                icon.setImageBitmap(bitmap);
            } else {
                icon.setImageResource(R.drawable.dianhua);//显示默认的图片
            }
            title.setText(musicList.get(position).getTitle());
            artist.setText(musicList.get(position).getSinger());
            time.setText(MusicUtils.timeToString(musicList.get(position).getTime()));

            return convertView;
        }
    }

}
