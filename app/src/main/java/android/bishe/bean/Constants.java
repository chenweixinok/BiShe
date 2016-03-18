package android.bishe.bean;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;

public class Constants {
	public static List<Music> musiclist=new ArrayList<Music>();//所有音乐集合
	public static List<Music> playlist=new ArrayList<Music>();//音乐播放列表
	public static final String CONTROL_ACTION="android.riji.control";//控制音乐播放动作，播放或暂停
	public static final String SEEKBAR_ACTION="android.riji.seekbar";//音乐进度发送变化动作
	public static final String COMPLETE_ACTION="android.riji.complete";//音乐播放结束动作
	public static final String UPDATE_ACTION="android.riji.update";//更新进度条
	public static final String UPDATE_STYLE="android.riji.style";//更新播放形式
	public static final Uri ALBUM_URL=Uri.parse("content://media/external/audio/albumart");
	public static final String LIST_LOOP="列表循环";
	public static final String SINGLE_LOOP="单曲循环";
	public static final String OVER_FINISH="结束后停止";
	public static final String RANDOM_PLAY="随机播放";
	public static final int NEW=6;//开始一首新的音乐
	public static final int PLAY=1;//播放
	public static final int PAUSE=2;//暂停
	public static final int ALL_MUSIC=0x11;//播放所有的音乐
	public static final int PLAY_LIST_MUSIC=0x12;//播放播放列表中的音乐
}
