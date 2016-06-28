package android.bishe.bean;

/**
 * Created by Lenovo on 2016/2/29.
 *
 * 封装
 */

public class ListData {

    public static final int SEND=1;
    public static final int RECEIVER=2;

    private String content;
    private int flag;
    private String Time;


    public ListData(String content,int flag,String Time){
        setContent(content);
        setFlag(flag);
        setTime(Time);
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public int getFlag() {
        return flag;
    }
    public void setFlag(int flag) {
        this.flag = flag;
    }
    public String getTime() {
        return Time;
    }
    public void setTime(String time) {
        Time = time;
    }
}
