package android.bishe.fragment;

import android.bishe.R;
import android.bishe.activity.MusicActivity;
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
		TextView musicButton = (TextView) view.findViewById(R.id.textView3);
		musicButton.setOnClickListener(this);

		view.findViewById(R.id.textView4).setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {

		Intent intent = null;

		switch (v.getId()){

			case R.id.textView3:
				intent = new Intent(getActivity(), MusicActivity.class);
				break;

			case R.id.textView4:

				break;
		}

		getActivity().startActivity(intent);
	}
}
