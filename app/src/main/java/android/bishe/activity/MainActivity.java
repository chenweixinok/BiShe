package android.bishe.activity;

import android.app.AlertDialog;
import android.bishe.R;
import android.bishe.Utils.CommonUtil;
import android.bishe.fragment.Diary;
import android.bishe.fragment.Schedule;
import android.bishe.fragment.TLXiaoJingLing;
import android.bishe.receiver.MyReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener {


    private RadioGroup radioGroup;
    private TextView title;

    private Schedule schedule;


    private DrawerLayout mDrawerLayout;

    private MyReceiver myReceiver;


    private SQLiteDatabase database;
    private final String DATABASE_PATH = Environment
            .getExternalStorageDirectory() + "/timetable";
    private final String DATABASE_FILENAME = "timetable.db";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvents();

        database = openDatabase();

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, intentFilter);

    }

    private void initEvents() {
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = mDrawerLayout.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;

                if (drawerView.getTag().equals("LEFT")) {

                    //float leftScale = 1 - 0.3f * scale;

                    //ViewHelper.setScaleX(mMenu, leftScale);
                    //ViewHelper.setScaleY(mMenu, leftScale);
                    //ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
                    ViewHelper.setTranslationX(mContent,
                            mMenu.getMeasuredWidth() * (1 - scale));
                    ViewHelper.setPivotX(mContent, 0);
                    ViewHelper.setPivotY(mContent,
                            mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                    ViewHelper.setScaleX(mContent, rightScale);
                    ViewHelper.setScaleY(mContent, rightScale);
                } else {
                    ViewHelper.setTranslationX(mContent,
                            -mMenu.getMeasuredWidth() * slideOffset);
                    ViewHelper.setPivotX(mContent, mContent.getMeasuredWidth());
                    ViewHelper.setPivotY(mContent,
                            mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                    ViewHelper.setScaleX(mContent, rightScale);
                    ViewHelper.setScaleY(mContent, rightScale);
                }

            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //mDrawerLayout.setDrawerLockMode(
                //DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
            }
        });
    }


    public void OpenRightMenu(View view)
    {
        mDrawerLayout.openDrawer(Gravity.RIGHT);
        //mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
        //		Gravity.RIGHT);
    }

    public void OpenLeftMenu(View view){
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    private void initView() {

        Diary diary = new Diary();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, diary)
                .commit();

        /**
         * MainActivity底部的几个RadioButton
         */
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(this);
        title = (TextView) findViewById(R.id.title);



        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                Gravity.RIGHT);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                Gravity.LEFT);

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.rb_recommend:

                Diary diary = new Diary();
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, diary)
                        .commit();

                title.setText("日志");
                break;

            case R.id.rb_sort:

                TLXiaoJingLing tlXiaoJingLing = new TLXiaoJingLing();
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, tlXiaoJingLing)
                        .commit();

                title.setText("聊天");
                break;

            case R.id.rb_topic:

                schedule = new Schedule();
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, schedule)
                        .commit();

                title.setText("课表");
                break;
        }

    }



    //判断是否存在sql文件
    private SQLiteDatabase openDatabase() {
        try {
            // 获得dictionary.db文件的绝对路径
            String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
            File dir = new File(DATABASE_PATH);
            // 如果DATABASE_PATH目录不存在，创建这个目录
            if (!dir.exists())
                dir.mkdir();
            // 如果在DATABASE_PATH目录中不存在
            // DATABASE_FILENAME文件，则从res\raw目录中复制这个文件到
            // SD卡的目录（DATABASE_PATH）
            if (!(new File(databaseFilename)).exists()) {
                System.out.println("正在复制数据库文件！");
                // 获得封装文件的InputStream对象
                InputStream is = getResources()
                        .openRawResource(R.raw.timetable);
                System.out.println("获得数据库文件数据流！");
                // true就是追加文字，false就是替换文字。而不写就默认替换。
                FileOutputStream fos = new FileOutputStream(databaseFilename,
                        false);
                System.out.println("获得数据库文件输出流！");
                byte[] buffer = new byte[8192];
                int count = 0;
                // 开始复制文件
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
                System.out.println("数据库文件复制完成！");
            }
            // 打开/sdcard/dictionary目录中的dictionary.db文件
            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
                    databaseFilename, null);
            return database;
        } catch (Exception e) {
            System.out.println("数据库加载出错！" + e.getMessage());
        }
        return null;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);

        if (database != null) {
            database.close();
        }
    }


}
