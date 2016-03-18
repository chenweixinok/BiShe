package android.bishe.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lenovo on 2016/3/6.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int  VERSON = 1;//Ĭ�ϵ����ݿ�汾

    //�̳�SQLiteOpenHelper�����������Լ��Ĺ��캯��
    //�ù��캯��4��������ֱ�ӵ��ø���Ĺ��캯�������е�һ������Ϊ���౾���ڶ�������Ϊ���ݿ�����֣�
    //��3�����������������α����ģ�����һ������Ϊnull�������������ݿ�İ汾�š�
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int verson){
        super(context, name, factory, verson);
    }

    //�ù��캯����3����������Ϊ�������溯���ĵ�3�������̶�Ϊnull��
    public DatabaseHelper(Context context, String name, int verson){
        this(context, name, null, verson);
    }

    //�ù��캯��ֻ��2�������������溯�� �Ļ���ɽ���汾�Ź̶���
    public DatabaseHelper(Context context, String name){
        this(context, name, VERSON);
    }

    //�ú��������ݿ��һ�α�����ʱ����
    @Override
    public void onCreate(SQLiteDatabase arg0) {
        // TODO Auto-generated method stub
        System.out.println("create a sqlite database");
        //execSQL()Ϊִ�в��������SQL��䣬��˲����е������Ҫ����SQL�﷨,�����Ǵ���һ����
        arg0.execSQL("CREATE TABLE t_user(id INTEGER PRIMARY KEY, username VARCHAR(20), password VARCHAR(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        System.out.println("update a sqlite database");
    }



}
