package android.bishe.service;



import android.app.Service;
import android.bishe.R;
import android.bishe.Utils.ImageUtil;
import android.bishe.Utils.MusicUtils;
import android.bishe.activity.MusicActivity;
import android.bishe.bean.LrcContent;
import android.bishe.bean.LrcProcess;
import android.bishe.bean.Music;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Lenovo on 2016/2/26.
 */
public class MusicService extends Service{

    MyReceiver serviceReceiver;

    private Animation operatingAnim;


    private LrcProcess mLrcProcess; //歌词处理
    private List<LrcContent> lrcList = new ArrayList<LrcContent>(); //存放歌词列表对象


    private int index = 0;
    private int currentTime;
    private int duration;

    private Handler handler = new Handler();
    public static boolean isChanging=false;//???????????????????SeekBar??????????
    public static MediaPlayer mPlayer;


    List<Music> musicList;

    int status = 0x11;
    //?????��?
    int current = 0;
    int flog = 0;
    int position;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        flog=0;
        mPlayer.stop();
        mPlayer.release();
        super.onDestroy();
    }

    @Override
    public void onCreate() {

        musicList = getMusicList();// ???????
        if (musicList == null || musicList.size() == 0) {// ??????????
            musicList = MusicUtils.getMusicData(getApplicationContext());//???????????
        }


        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.tip);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        operatingAnim.setFillAfter(true);

        serviceReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicActivity.CTL_ACTION);
        registerReceiver(serviceReceiver, filter);

        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next();
            }
        });

        super.onCreate();
    }

    private void next() {

        current = (current + 1) % musicList.size();
        playMusic();

    }



    public void next(View view) {
        next();
    }


    public void previous(View view) {
        previous();
    }

    private void previous() {

        current = current - 1 < 0 ? musicList.size() - 1 : current - 1;
        playMusic();
    }


    private void playMusic()
    {
        try
        {
	            /* ????MediaPlayer */
            mPlayer.reset();
	            /* ???????????????��?? */
            mPlayer.setDataSource(musicList.get(current).getUrl());
	            /* ??????? */
            mPlayer.prepare();
	            /* ??????? */
            mPlayer.start();

            initLrc();

            initTuBiao();

            MusicActivity.musicImg.startAnimation(operatingAnim);



        } catch (IOException e) {

        }

        //???????Runnable, handler???????????run()????
        handler.post(new Runnable() {
            public void run() {
                // ???????????
                if (!isChanging)
                    MusicActivity.skbProgress.setProgress(mPlayer.getCurrentPosition());
                MusicActivity.beging_tv.setText(MusicUtils.timeToString(mPlayer.getCurrentPosition()));
                // 1???????��???
                handler.postDelayed(this, 1000);

            }
        });
    }

    private void initTuBiao() {

        Bitmap bitmap = MusicUtils.getAlbumPic(getApplicationContext(), musicList.get(current));
        String singTitle = musicList.get(current).getTitle();
        String singer = musicList.get(current).getSinger();

        MusicActivity.setMusicSingTitle(singTitle,singer);
        setMusicImg(bitmap);
    }

    public void setMusicImg(Bitmap bm) {

        if (bm != null) {

            MusicActivity.musicImg.setImageBitmap(ImageUtil.toRoundBitmap(bm));
            //MusicImg.setImageBitmap(ImageUtil.createRoundedCornerBitmap(bm,100));
        } else {
            //MusicActivity.musicImg.setImageResource(R.drawable.background);
            MusicActivity.musicImg.setImageBitmap(ImageUtil
                    .toRoundBitmap(convertResToBm(R.drawable.background)));




        }

    }



    private Bitmap convertResToBm(int id){
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 2;  //将原图缩小四分之一读取
        option.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeResource(this.getResources(),id, option);

        return  ThumbnailUtils.extractThumbnail(bm, 480, 800); //将图片的大小限定在480*800
    }



    @Override
    public void onStart(Intent intent, int startId) {
        if(flog==2){
            Intent sendIntent = new Intent(MusicActivity.UPDATE_ACTION);
            sendIntent.putExtra("update", status);
            sendIntent.putExtra("current", current);
            sendIntent.putExtra("position",position);
            sendBroadcast(sendIntent);
        }
        flog=2;
    }



    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int control = intent.getIntExtra("control", -1);
            switch (control) {
                case 2: {
                    // ????????????????
                    if (status == 0x11) {
                        playMusic();
                        status = 0x12;
                    }
                    // ????????????
                    else if (status == 0x12) {
                        mPlayer.pause();
                        status = 0x13;
                    }
                    // ????????????
                    else if (status == 0x13) {
                        mPlayer.start();
                        status = 0x12;
                    }
                    break;
                }
                case 4: {

                    playMusic();

                    break;
                }
                /*
                ?????
                 */
                case 1: {
                    previous();
                    status = 0x12;
                    break;
                }
                /*
                ?????
                 */
                case 3: {
                    next();
                    status = 0x12;
                    break;
                }
                case 5: {
                    current = intent.getIntExtra("current", -1);
                    playMusic();
                    status = 0x12;
                    break;
                }
            }

            Intent sendIntent = new Intent(MusicActivity.UPDATE_ACTION);
            sendIntent.putExtra("update", status);
            sendIntent.putExtra("current", current);
            sendIntent.putExtra("position",position);
            sendBroadcast(sendIntent);
        }

    }


    public List<Music> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }



    public void initLrc(){
        mLrcProcess = new LrcProcess();
        //读取歌词文件
        mLrcProcess.readLRC(musicList.get(current).getUrl());
        //传回处理后的歌词文件
        lrcList = mLrcProcess.getLrcList();
        MusicActivity.lrcView.setmLrcList(lrcList);
        //切换带动画显示歌词
        // PlayerActivity.lrcView.setAnimation(AnimationUtils.loadAnimation(PlayerService.this,R.anim.alpha_z));
        handler.post(mRunnable);
    }
    Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            MusicActivity.lrcView.setIndex(lrcIndex());
            MusicActivity.lrcView.invalidate();
            handler.postDelayed(mRunnable, 100);
        }
    };


    /**
     * 根据时间获取歌词显示的索引值
     * @return
     */
    public int lrcIndex() {
        if(mPlayer.isPlaying()) {
            currentTime = mPlayer.getCurrentPosition();
            duration = mPlayer.getDuration();
        }
        if(currentTime < duration) {
            for (int i = 0; i < lrcList.size(); i++) {
                if (i < lrcList.size() - 1) {
                    if (currentTime < lrcList.get(i).getLrcTime() && i == 0) {
                        index = i;
                    }
                    if (currentTime > lrcList.get(i).getLrcTime()
                            && currentTime < lrcList.get(i + 1).getLrcTime()) {
                        index = i;
                    }
                }
                if (i == lrcList.size() - 1
                        && currentTime > lrcList.get(i).getLrcTime()) {
                    index = i;
                }
            }
        }
        return index;
    }


}