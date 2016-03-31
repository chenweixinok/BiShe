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
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;


public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener {


    private RadioGroup radioGroup;
    private TextView title;

    private Schedule schedule;


    private DrawerLayout mDrawerLayout;

    private MyReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvents();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }


}
