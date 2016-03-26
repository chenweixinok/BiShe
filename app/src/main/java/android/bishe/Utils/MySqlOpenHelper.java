package android.bishe.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Lenovo on 2016/3/20.
 */
public class MySqlOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASENAME = "Login.db";
    private static int version=1;
    public MySqlOpenHelper(Context context) {
        super(context, DATABASENAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table login_date( _id integer primary key autoincrement, name varchar(36), password varchar(36),url varchar(36))");
        db.execSQL("create table read_date( _id integer primary key autoincrement, date varchar(36), " +
                "num varchar(36), title varchar(36), url varchar(36),replaycount varchar(36),lanMuType varchar(36))");
//        db.execSQL("create table login_date( _id integer primary key autoincrement,name varchar(36),password varchar(36))");
//        "_id integer primary key autoincrement,name varchar(36),age interger"
        Log.e("db", "数据库创建了");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
