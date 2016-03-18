package android.bishe.fragment;

import android.app.AlertDialog;
import android.bishe.R;
import android.bishe.Utils.NotesDB;
import android.bishe.activity.AddContent;
import android.bishe.activity.SelectAct;
import android.bishe.adapter.MAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Lenovo on 2015/12/24.
 */
public class Diary extends Fragment implements View.OnClickListener {


    private Button textbtn, imgbtn, videobtn;
    private ListView listView;
    private Intent intent;
    private MAdapter adapter;
    private NotesDB notesDB;
    private SQLiteDatabase mDbWriter = null;
    private Cursor cursor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fra_diary, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();

    }

    private void initView() {

        listView = (ListView) getView().findViewById(R.id.list);
        textbtn = (Button) getView().findViewById(R.id.text);
        imgbtn = (Button) getView().findViewById(R.id.img);
        videobtn = (Button) getView().findViewById(R.id.video);
        textbtn.setOnClickListener(this);
        imgbtn.setOnClickListener(this);
        videobtn.setOnClickListener(this);
        notesDB = new NotesDB(getActivity());
        mDbWriter = notesDB.getWritableDatabase();



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

}
