package android.bishe.fragment;

import android.bishe.R;
import android.bishe.activity.BaiDuActivity;
import android.bishe.activity.BaiduSS;
import android.bishe.activity.MusicActivity;
import android.bishe.activity.SearchActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MenuRightFragment extends Fragment implements View.OnClickListener {

	final Context context = getActivity();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.menu_layout_right,container,false);

		TextView musicButton = (TextView) view.findViewById(R.id.textMusic);
		musicButton.setOnClickListener(this);

		TextView MapBtn = (TextView) view.findViewById(R.id.textMap);
		MapBtn.setOnClickListener(this);

		TextView BaiduSS = (TextView) view.findViewById(R.id.text_Baidu_SS);
		BaiduSS.setOnClickListener(this);


		return view;
	}

	@Override
	public void onClick(View v) {


		switch (v.getId()){

			case R.id.textMusic:
				Intent intentMusic = new Intent(getActivity(), MusicActivity.class);
				startActivity(intentMusic);
				break;

			case R.id.textMap:
				Intent intentMap = new Intent(getActivity(), BaiDuActivity.class);
				startActivity(intentMap);
				break;

			case R.id.text_Baidu_SS:
				Intent intentSS = new Intent(getActivity(),SearchActivity.class);
				startActivity(intentSS);
				break;
		}


	}
}
