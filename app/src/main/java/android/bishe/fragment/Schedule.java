package android.bishe.fragment;

import android.bishe.R;
import android.bishe.activity.AddClassActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by Lenovo on 2015/12/24.
 */
public class Schedule extends Fragment{


    private SQLiteDatabase database;
    private String databaseFilename = Environment
            .getExternalStorageDirectory() + "/timetable/timetable.db";

    private int colors[] = { Color.rgb(0xff, 0xb6, 0xc1),
            Color.rgb(0xf0, 0x96, 0x09), Color.rgb(0x8c, 0xbf, 0x26),
            Color.rgb(0x00, 0xab, 0xa9), Color.rgb(0x99, 0x6c, 0x33),
            Color.rgb(0x3b, 0x92, 0xbc), Color.rgb(0xd5, 0x4d, 0x34),
            Color.rgb(0xcc, 0xcc, 0xcc) };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fra_schedule, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // 切换语言
        Locale.setDefault(Locale.CHINESE);
        Configuration config = getActivity().getResources()
                .getConfiguration();
        config.locale = Locale.CHINESE;
        getActivity().getResources().updateConfiguration(config,
                getActivity().getResources().getDisplayMetrics());



        database = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
        TextView t_addClass = (TextView) getView().findViewById(R.id.t_addClass);

        // 分别表示周一到周日
        LinearLayout monday = (LinearLayout) getView().findViewById(R.id.monday);
        LinearLayout tuesday = (LinearLayout) getView().findViewById(R.id.tuesday);
        LinearLayout wednesday = (LinearLayout) getView().findViewById(R.id.wednesday);
        LinearLayout thursday = (LinearLayout) getView().findViewById(R.id.thursday);
        LinearLayout friday = (LinearLayout) getView().findViewById(R.id.friday);
        LinearLayout saturday = (LinearLayout) getView().findViewById(R.id.saturday);
        LinearLayout sunday = (LinearLayout) getView().findViewById(R.id.sunday);

        LinearLayout layout = null;
        Cursor cursor = database.rawQuery("SELECT * FROM t_course", null);
        while (cursor.moveToNext()) {
            String week = cursor.getString(cursor.getColumnIndex("week"));
            String course = cursor.getString(cursor.getColumnIndex("course"));
            String teacher = cursor.getString(cursor.getColumnIndex("teacher"));
            String place = cursor.getString(cursor.getColumnIndex("place"));
            int num = cursor.getInt(cursor.getColumnIndex("num"));
            int color = cursor.getInt(cursor.getColumnIndex("color"));
            switch (week) {
                case "monday":
                    layout = monday;
                    break;
                case "tuesday":
                    layout = tuesday;
                    break;
                case "wednesday":
                    layout = wednesday;
                    break;
                case "thursday":
                    layout = thursday;
                    break;
                case "friday":
                    layout = friday;
                    break;
                case "saturday":
                    layout = saturday;
                    break;
                case "sunday":
                    layout = sunday;
                    break;
                default:
                    break;
            }
            setClass(layout, course, teacher, place, num, color);
        }
        cursor.close();

        t_addClass.setClickable(true);
        t_addClass.setFocusable(true);
        t_addClass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), AddClassActivity.class);
                getActivity().startActivity(intent);
                //overridePendingTransition(R.anim.fade, R.anim.hold);
               // getActivity().finish();
            }
        });
    }


    /**
     * 设置课程的方法
     *
     * @param layout
     * @param title
     *            课程名称
     * @param place
     *            地点
     * @param classes
     *            节数
     * @param color
     *            背景色
     */
    void setClass(LinearLayout layout, String course, String teacher,
                  String place, int num, int color) {
        int classes = 0;
        if (num == 1 || num == 2) {
            classes = 2;
        } else {
            classes = 3;
        }
        if(course!=null){

        }
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_timetable_item, null);
        view.setMinimumHeight(dip2px(getActivity(), classes * 48));
        view.setBackgroundColor(colors[color]);
        ((TextView) view.findViewById(R.id.course)).setText(course);
        ((TextView) view.findViewById(R.id.teacher)).setText(teacher);
        ((TextView) view.findViewById(R.id.place)).setText(place);
        // 为课程View设置点击的监听器
        layout.addView(view);
        TextView blank = new TextView(getActivity());
        blank.setHeight(dip2px(getActivity(), classes));
        layout.addView(blank);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }



    /** * 根据手机的分辨率从 dp 的单位 转成为 px(像素) */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /** * 根据手机的分辨率从 px(像素) 的单位 转成为 dp */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
