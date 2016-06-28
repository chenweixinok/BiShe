package android.bishe.fragment;

import android.bishe.R;
import android.bishe.Utils.NotesDB;
import android.bishe.activity.AddContent;
import android.bishe.activity.SelectAct;
import android.bishe.adapter.MAdapter;
import android.bishe.view.ArcMenu;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by Lenovo on 2015/12/24.
 */
public class Diary extends Fragment implements View.OnClickListener {


    private Button textbtn, imgbtn, videobtn;
    private RelativeLayout mRelativeLayout;
    private ListView listView;
    private Intent intent;
    private MAdapter adapter;
    private NotesDB notesDB;
    private SQLiteDatabase mDbWriter = null;
    private Cursor cursor;

    private ArcMenu mArcMenu;


    /*
    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            intent = new Intent(getActivity(), AddContent.class);

            switch (msg.what){
                case 1:
                    intent.putExtra("flag", "1");
                    startActivity(intent);
                    break;

                case 2:
                    intent.putExtra("flag", "2");
                    startActivity(intent);
                    break;

                case 3:
                    intent.putExtra("flag", "3");
                    startActivity(intent);
                    break;
            }
        };
    };
    */




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fra_diary, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();

        //initEvent();

    }


    /*
    private void initEvent() {

        mArcMenu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                Toast.makeText(getActivity(), pos + ":" + view.getTag(),
                        Toast.LENGTH_SHORT).show();


                final Message message = new Message();

                switch (pos) {
                    case 1:

                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                    message.what = 1;
                                    handler.sendMessage(message);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                        }.start();

                        break;

                    case 2:

                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                    message.what = 2;
                                    handler.sendMessage(message);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                        }.start();

                        break;

                    case 3:

                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                    message.what = 3;
                                    handler.sendMessage(message);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                        }.start();

                        break;
                }
            }
        });
    }  */

    private void initView() {

        listView = (ListView) getView().findViewById(R.id.list);
        notesDB = new NotesDB(getActivity());
        mDbWriter = notesDB.getWritableDatabase();
        mRelativeLayout = (RelativeLayout) getView().findViewById(R.id.tianjia);
        mRelativeLayout.setOnClickListener(this);


        //mArcMenu = (ArcMenu) getView().findViewById(R.id.id_right_bottom);






        /**
         * ListView单击进入显示
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                cursor.moveToPosition(position);
                Intent i = new Intent(getActivity(), SelectAct.class);
                i.putExtra(NotesDB.ID,
                        cursor.getInt(cursor.getColumnIndex(NotesDB.ID)));
                i.putExtra(NotesDB.CONTENT, cursor.getString(cursor
                        .getColumnIndex(NotesDB.CONTENT)));
                i.putExtra(NotesDB.TIME,
                        cursor.getString(cursor.getColumnIndex(NotesDB.TIME)));
                i.putExtra(NotesDB.PATH,
                        cursor.getString(cursor.getColumnIndex(NotesDB.PATH)));
                i.putExtra(NotesDB.VIDEO,
                        cursor.getString(cursor.getColumnIndex(NotesDB.VIDEO)));
                startActivity(i);
            }
        });

    }

    /**
    @Override
    public void onClick(View v) {

        intent = new Intent(getActivity(), AddContent.class);
        switch (v.getId()) {
            case R.id.text:
                intent.putExtra("flag", "1");
                startActivity(intent);
                break;

            case R.id.img:
                intent.putExtra("flag", "2");
                startActivity(intent);
                break;

            case R.id.video:
                intent.putExtra("flag", "3");
                startActivity(intent);
                break;
        }

    }
    **/






    @Override
    public void onStart() {
        super.onStart();
        show_List();
    }

    public void deleteDb(String id) {
        mDbWriter.execSQL("delete from " + NotesDB.TABLE_NAME + " where " + NotesDB.ID + "=" + id);
        mDbWriter.close();
    }

    public void show_List() {
        cursor = mDbWriter.query(NotesDB.TABLE_NAME, null, null, null, null,
                null, null);
        adapter = new MAdapter(getActivity(), cursor);
        listView.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        intent = new Intent(getActivity(),AddContent.class);
        startActivity(intent);
    }
}
