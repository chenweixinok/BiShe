package android.bishe.fragment;

import android.bishe.R;
import android.bishe.Utils.CommonUtil;
import android.bishe.Utils.SharedPreferencesUtil;
import android.bishe.activity.YueDuDetialActivity;
import android.bishe.adapter.YueDuAdapter;
import android.bishe.bean.ServerURL;
import android.bishe.bean.YueDuBean;
import android.bishe.view.PullToRefreshBase;
import android.bishe.view.PullToRefreshListView;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MenuLeftFragment extends Fragment{


	private PullToRefreshListView rListView;
	private YueDuAdapter adapter;
	private HttpUtils httpUtils;
	private HttpHandler<String> httpHandler;
	private List<YueDuBean.推荐Entity> list = new ArrayList<>();
	View view;

	private ImageView mTodayWeatherImage;
	private TextView mTodayTemperatureTV;
	private TextView mTodayWindTV;
	private TextView mTodayWeatherTV;
	private TextView mCityTV;

	private TextView L_Htemp;



	public LocationClient mLocationClient = null;
	public MyLocationListener myListener = new MyLocationListener();

	public String normalDistrict;
	public String normalCity;




	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mLocationClient = new LocationClient(getActivity());
		mLocationClient.registerLocationListener(myListener);
		setLocationOption();
		mLocationClient.start();

	}
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		try {
			if (view == null) {
				view = initview(inflater);
			}
			return view;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//初始化控件
	private View initview(LayoutInflater inflater) {
		View inflate = inflater.inflate(R.layout.yuedu_content, null, false);


		mTodayWeatherImage = (ImageView) inflate.findViewById(R.id.weatherImage);
		mTodayTemperatureTV = (TextView) inflate.findViewById(R.id.weatherTemp);
		mTodayWindTV = (TextView) inflate.findViewById(R.id.wind);
		mTodayWeatherTV = (TextView) inflate.findViewById(R.id.weather);
		mCityTV = (TextView) inflate.findViewById(R.id.city);

		L_Htemp = (TextView) inflate.findViewById(R.id.l_hTemp);


		httpUtils = new HttpUtils();
		initPullTorefresh(inflate);
		return inflate;
	}













	private void initPullTorefresh(View inflate) {
		rListView = (PullToRefreshListView) inflate.findViewById(R.id.refresh);

		initdata();
		rListView.setPullLoadEnabled(false);  //上拉加载，屏蔽
//        rListView.setPullLoadEnabled(true);
		rListView.setScrollLoadEnabled(true); //设置滚动加载可用
		rListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {//下拉刷新
				if (CommonUtil.isNetWork(getActivity())){
					getData(ServerURL.yueDuURL,true);
					String stringDate = CommonUtil.getStringDate();
					rListView.setLastUpdatedLabel(stringDate);
				}else {
					Toast.makeText(getActivity(),"网络不给力",Toast.LENGTH_SHORT).show();
					rListView.onPullDownRefreshComplete();
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {//上拉加载
				//if (CommonUtil.isNetWork(getActivity())){
					//getData(ServerURL.yueDuURLJiaZai,false);
			//	}else {
					//Toast.makeText(getActivity(),"网络不给力",Toast.LENGTH_SHORT).show();
					//rListView.onPullUpRefreshComplete();
				//}
			}
		});

		rListView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String replyid = adapter.getList().get(position).replyid;
				Intent intent = new Intent(getActivity(), YueDuDetialActivity.class);
				intent.putExtra("yueduDetial", replyid);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.zcdh_set_in, R.anim.zcdh_alpha_out);
			}
		});
	}

	//初始化数据
	private void initdata() {
		if (!CommonUtil.isNetWork(getActivity())) {
			String result = SharedPreferencesUtil.getData(getActivity(), ServerURL.yueDuURL, "");
			if (!TextUtils.isEmpty(result)) {//如果缓存有数据,直接Gson解析
				paserData(result,false);
			}
		} else {
			getData(ServerURL.yueDuURL,false);
		}
	}

	//从网络获取数据
	private void getData(final String url, final boolean isRefresh) {
		//LogUtils.e("----------", url);
		if (!url.equals("")) {
			httpHandler = httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					//LogUtils.e("--------", responseInfo.result);
					SharedPreferencesUtil.saveData(getActivity(), url, responseInfo.result);
					paserData(responseInfo.result,isRefresh);//Gson解析数据
				}

				@Override
				public void onFailure(HttpException e, String s) {
					Toast.makeText(getActivity(), "数据请求失败", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	//解析数据并添加到集合
	private void paserData(String result,boolean isRefresh) {
		YueDuBean yueDuBean = new Gson().fromJson(result, YueDuBean.class);
		if (isRefresh == true){
			adapter = new YueDuAdapter(getActivity(), yueDuBean.推荐);
			rListView.getRefreshableView().setAdapter(adapter);
		}else {
			if (adapter == null) {
				adapter = new YueDuAdapter(getActivity(), yueDuBean.推荐);
				rListView.getRefreshableView().setAdapter(adapter);
			} else {
				list.addAll(adapter.getList());
				adapter.setList(list);
			}
		}
		rListView.onPullDownRefreshComplete();
		//rListView.onPullUpRefreshComplete();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if (httpUtils != null) {
			httpHandler.cancel();
		}
	}




	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");//返回的定位结果包含地址信息
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(500000);//设置发起定位请求的间隔时间为5000ms
		option.disableCache(true);//禁止启用缓存定位
		option.setIsNeedAddress(true);

		mLocationClient.setLocOption(option);
	}






	private class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null) {
				normalDistrict = location.getDistrict();
				normalCity = location.getCity();
				String str = normalCity.substring(0, normalCity.length() - 1);
				String urlEncode = Uri.encode(str);


				String httpUrl = "http://apis.baidu.com/apistore/weatherservice/cityname";
				String httpArg = "cityname=" + urlEncode;

				request(httpUrl, httpArg);



				Toast.makeText(getActivity(), "定位成功" + normalCity, Toast.LENGTH_LONG).show();
				Log.i("TAG", normalCity + "---->>normalCity");
				if(normalCity == null){
					Toast.makeText(getActivity(), "定位失败，请检查网络", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}






	private void request(String httpUrl,String httpArg) {
		new BaiduTask().execute(new String[]{httpUrl,httpArg});

	}

	private class BaiduTask extends AsyncTask<String,Void,String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			String httpUrl = params[0];
			String httpArg = params[1];
			BufferedReader reader = null;
			String result = null;
			StringBuffer sbf = new StringBuffer();
			httpUrl = httpUrl + "?" + httpArg;

			try {
				URL url = new URL(httpUrl);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setRequestMethod("GET");
				// 填入apikey到HTTP header
				connection.setRequestProperty("apikey", "967e7dbb249d934f442d2a094df87cf0");
				connection.connect();
				InputStream is = connection.getInputStream();
				reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String strRead = null;
				while ((strRead = reader.readLine()) != null) {
					sbf.append(strRead);
					sbf.append("\r\n");
				}
				reader.close();
				result = sbf.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {



			String s = "";
			try {


				JSONObject resultJsonObject = new JSONObject(result.toString()).getJSONObject("retData");


				String altitude = resultJsonObject.getString("altitude");
				String weather = resultJsonObject.getString("weather");
				String wind = resultJsonObject.getString("WD");
				String l_tmp = resultJsonObject.getString("l_tmp");
				String h_tmp = resultJsonObject.getString("h_tmp");
				String city = resultJsonObject.getString("city");
				String temp = resultJsonObject.getString("temp");
				String sunrise = resultJsonObject.getString("sunrise");
				String sunset = resultJsonObject.getString("sunset");

				mTodayWeatherTV.setText(weather);
				L_Htemp.setText("海拨:"+altitude+"米");
				mTodayWindTV.setText(wind);
				mTodayWeatherImage.setImageResource(getWeatherImage(weather));
				mCityTV.setText(city);

				//ichu.setText("日出：" + sunrise);
				//ruluo.setText("日落：" + sunset);
				mTodayTemperatureTV.setText(temp + "℃");




			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}






	public static int getWeatherImage(String weather) {
		if (weather.equals("多云") || weather.equals("多云转阴") || weather.equals("多云转晴")) {
			return R.drawable.biz_plugin_weather_duoyun;
		} else if (weather.equals("中雨") || weather.equals("中到大雨")) {
			return R.drawable.biz_plugin_weather_zhongyu;
		} else if (weather.equals("雷阵雨")) {
			return R.drawable.biz_plugin_weather_leizhenyu;
		} else if (weather.equals("阵雨") || weather.equals("阵雨转多云")) {
			return R.drawable.biz_plugin_weather_zhenyu;
		} else if (weather.equals("暴雪")) {
			return R.drawable.biz_plugin_weather_baoxue;
		} else if (weather.equals("暴雨")) {
			return R.drawable.biz_plugin_weather_baoyu;
		} else if (weather.equals("大暴雨")) {
			return R.drawable.biz_plugin_weather_dabaoyu;
		} else if (weather.equals("大雪")) {
			return R.drawable.biz_plugin_weather_daxue;
		} else if (weather.equals("大雨") || weather.equals("大雨转中雨")) {
			return R.drawable.biz_plugin_weather_dayu;
		} else if (weather.equals("雷阵雨冰雹")) {
			return R.drawable.biz_plugin_weather_leizhenyubingbao;
		} else if (weather.equals("晴")) {
			return R.drawable.biz_plugin_weather_qing;
		} else if (weather.equals("沙尘暴")) {
			return R.drawable.biz_plugin_weather_shachenbao;
		} else if (weather.equals("特大暴雨")) {
			return R.drawable.biz_plugin_weather_tedabaoyu;
		} else if (weather.equals("雾") || weather.equals("雾霾")) {
			return R.drawable.biz_plugin_weather_wu;
		} else if (weather.equals("小雪")) {
			return R.drawable.biz_plugin_weather_xiaoxue;
		} else if (weather.equals("小雨")) {
			return R.drawable.biz_plugin_weather_xiaoyu;
		} else if (weather.equals("阴")) {
			return R.drawable.biz_plugin_weather_yin;
		} else if (weather.equals("雨夹雪")) {
			return R.drawable.biz_plugin_weather_yujiaxue;
		} else if (weather.equals("阵雪")) {
			return R.drawable.biz_plugin_weather_zhenxue;
		} else if (weather.equals("中雪")) {
			return R.drawable.biz_plugin_weather_zhongxue;
		} else {
			return R.drawable.biz_plugin_weather_duoyun;
		}
	}



	@Override
	public void onDestroy() {
		super.onDestroy();

	}
}
