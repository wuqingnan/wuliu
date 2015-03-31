package cn.boweikeji.wuliu.driver.activity;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfigeration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMyLocationClickListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MyLocationConfigeration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.Const;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.WLApplication;
import cn.boweikeji.wuliu.driver.WeakHandler;
import cn.boweikeji.wuliu.driver.aidl.ILocationListener;
import cn.boweikeji.wuliu.driver.aidl.IReportService;
import cn.boweikeji.wuliu.driver.api.BaseParams;
import cn.boweikeji.wuliu.driver.bean.Order;
import cn.boweikeji.wuliu.driver.bean.UpdateInfo;
import cn.boweikeji.wuliu.driver.bean.UserInfo;
import cn.boweikeji.wuliu.driver.event.ExitEvent;
import cn.boweikeji.wuliu.driver.fragment.BaseFragment;
import cn.boweikeji.wuliu.driver.fragment.FindFragment;
import cn.boweikeji.wuliu.driver.fragment.HomeFragment;
import cn.boweikeji.wuliu.driver.fragment.ActivityFragment;
import cn.boweikeji.wuliu.driver.fragment.MoreFragment;
import cn.boweikeji.wuliu.driver.fragment.OrderFragment;
import cn.boweikeji.wuliu.driver.manager.LoginManager;
import cn.boweikeji.wuliu.driver.manager.UpdateManager;
import cn.boweikeji.wuliu.http.AsyncHttp;
import de.greenrobot.event.EventBus;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	public static final String KEY_LOGOUT = "logout";

	private static final int MSG_PUSH = 1 << 0;

	private static final int EXIT_TIME = 2000;

	public static final int[] TAB_IDS = { R.id.home_tab_home,
			R.id.home_tab_activity, R.id.home_tab_find, R.id.home_tab_order,
			R.id.home_tab_more };

	private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			for (int i = 0; i < TAB_IDS.length; i++) {
				if (TAB_IDS[i] == checkedId) {
					if (!changeFragment(i)) {
						mTabGroup.check(TAB_IDS[mTabIndex]);
					}
					break;
				}
			}
		}
	};

	private ILocationListener mLocationListener = new ILocationListener.Stub() {
		@Override
		public void onReceiveLocation() throws RemoteException {
			updateMap();
		}
	};
	
	private Runnable mUpdateMap = new Runnable() {
		@Override
		public void run() {
			updateMap();
		}
	};
	
	private JsonHttpResponseHandler mRequestHandler = new JsonHttpResponseHandler() {
		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			requestResult(response);
		};

		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			requestResult(null);
		};
	};

	private OnMarkerClickListener mOnMarkerClickListener = new OnMarkerClickListener() {
		@Override
		public boolean onMarkerClick(Marker marker) {
			Log.d(TAG, "shizy---onMarkerClick: " + marker.getTitle());
			showMarkerInfo(marker);
			return true;
		}
	};

	private OnMapClickListener mOnMapClickListener = new OnMapClickListener() {
		@Override
		public boolean onMapPoiClick(MapPoi poi) {
			return false;
		}

		@Override
		public void onMapClick(LatLng point) {
			mBaiduMap.hideInfoWindow();
		}
	};

	@InjectView(R.id.bottom_layout)
	LinearLayout mBottomLayout;
	@InjectView(R.id.home_tab_group)
	RadioGroup mTabGroup;
	@InjectView(R.id.bmapView)
	MapView mMapView;

	private BaiduMap mBaiduMap;
	private UiSettings mUiSettings;

	private View mDriverLayout;
	private TextView mDriverName;
	private TextView mDriverPhone;
	private TextView mDriverTruckNo;
	private TextView mDriverTruckType;

	private boolean mMapShowing;
	private boolean mIsFirstLoc;
	private boolean mHasCheckUpdate;

	private FragmentManager mFragmentManager;
	private BaseFragment mCurFragment;

	private long mExitTime;

	private int mTabIndex;

	private MainHandler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MobclickAgent.openActivityDurationTrack(false);
		init();
		pageJump(getIntent());
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
		mMapShowing = true;
		checkUpdate();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
		mMapShowing = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		((WLApplication)getApplication()).removeLocationListener(mLocationListener);
		((WLApplication)getApplication()).unbindReportService();
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mCurFragment != null) {
				if (mCurFragment.onKeyDown(keyCode, event)) {
					return true;
				}
			}
			if (mTabIndex != 0) {
				goHome();
				return true;
			}
			if (mFragmentManager.getBackStackEntryCount() == 0) {
				if (System.currentTimeMillis() - mExitTime > EXIT_TIME) {
					Toast.makeText(this, R.string.quit_next_time,
							Toast.LENGTH_SHORT).show();
					mExitTime = System.currentTimeMillis();
				} else {
					exit();
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == LoginActivity.REQUEST_CODE_REDIRECT) {
				if (data != null) {
					pageJump(data);
				}
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getBooleanExtra(KEY_LOGOUT, false)) {
			goHome();
		} else {
			pageJump(intent);
		}
	}

	private void init() {
		ButterKnife.inject(this);
		mFragmentManager = getSupportFragmentManager();
		mHandler = new MainHandler(this);
		mIsFirstLoc = true;
		mHasCheckUpdate = false;
		initView();
		initMap();
		changeFragment(0);
		EventBus.getDefault().register(this);
		((WLApplication)getApplication()).bindReportService();
		((WLApplication)getApplication()).addLocationListener(mLocationListener);
	}

	private void pageJump(Intent intent) {
		Log.d(TAG, "shizy---pageJump");
		boolean push = intent.getBooleanExtra("push", false);
		int type = intent.getIntExtra("type", -1);
		if (push && type == Const.PUSH_TYPE_ROB) {
			// 目前只有抢单须路转页面
			mHandler.removeMessages(MSG_PUSH);
			Message msg = Message.obtain();
			msg.what = MSG_PUSH;
			msg.obj = intent;
			mHandler.sendMessage(msg);
		}
	}

	private void pushIntent(Intent intent) {
		int type = intent.getIntExtra("type", -1);
		if (type > 0) {
			switch (type) {
			case Const.PUSH_TYPE_DETAIL:
				showDetail(intent);
				break;
			case Const.PUSH_TYPE_MSG:
				showMessage(intent);
				break;
			case Const.PUSH_TYPE_PERSONAL:
				showPersonal(intent);
				break;
			case Const.PUSH_TYPE_ROB:
				showRob(intent);
				break;
			}
		}
	}

	private void initView() {
		mTabGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
		mDriverLayout = getLayoutInflater()
				.inflate(R.layout.popup_driver, null);
		mDriverName = (TextView) mDriverLayout.findViewById(R.id.driver_name);
		mDriverPhone = (TextView) mDriverLayout.findViewById(R.id.driver_phone);
		mDriverTruckNo = (TextView) mDriverLayout
				.findViewById(R.id.driver_truck_no);
		mDriverTruckType = (TextView) mDriverLayout
				.findViewById(R.id.driver_truck_type);
	}

	private void initMap() {
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		mBaiduMap.setOnMapClickListener(mOnMapClickListener);
		mBaiduMap.setOnMarkerClickListener(mOnMarkerClickListener);

		mUiSettings = mBaiduMap.getUiSettings();
		mUiSettings.setCompassEnabled(false);
		mUiSettings.setZoomGesturesEnabled(true);
		mUiSettings.setScrollGesturesEnabled(true);
		mUiSettings.setRotateGesturesEnabled(false);
		mUiSettings.setOverlookingGesturesEnabled(false);

		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(
				LocationMode.NORMAL, true, null));
	}

	public void updateMap() {
		BDLocation location = WLApplication.getLastKnownLocation();
		if (location == null || mMapView == null) {
			if (mIsFirstLoc) {
				//未定到位置，每移主动刷新
				mHandler.postDelayed(mUpdateMap, 1000);
			}
			return;
		}
		if (!mMapShowing) {
			if (mIsFirstLoc) {
				//未定到位置，每移主动刷新
				mHandler.postDelayed(mUpdateMap, 1000);
			}
			return;
		}
		MyLocationData locData = new MyLocationData.Builder()
				.accuracy(location.getRadius())
				.direction(location.getDirection())
				.latitude(location.getLatitude())
				.longitude(location.getLongitude()).build();
		mBaiduMap.setMyLocationData(locData);
		if (mIsFirstLoc) {
			mIsFirstLoc = false;
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}
		mBaiduMap.clear();
		requestDriver(location);
	}

	private boolean changeFragment(int index) {
		if (index != 0 && !LoginManager.getInstance().hasLogin()) {
			LoginActivity.startLoginActivity(this);
			return false;
		}
		BaseFragment fragment = null;
		switch (index) {
		case 0:
			fragment = new HomeFragment();
			break;
		case 1:
			fragment = new ActivityFragment();
			break;
		case 2:
			fragment = new FindFragment();
			break;
		case 3:
			fragment = new OrderFragment();
			break;
		case 4:
			fragment = new MoreFragment();
			break;
		}
		mFragmentManager
				.beginTransaction()
				.replace(R.id.container, fragment,
						fragment.getClass().getName()).commit();
		mTabIndex = index;
		mCurFragment = fragment;
		return true;
	}

	private void checkUpdate() {
		if (mHasCheckUpdate) {
			return;
		}
		mHasCheckUpdate = true;
		UpdateInfo updateInfo = UpdateManager.readUpdateInfo();
		if (updateInfo != null && updateInfo.isNeedUpdate()) {
			showUpdateDialog(updateInfo);
		}
	}

	private void addMarker(JSONArray markers) {
		if (markers == null || markers.length() <= 0) {
			return;
		}

		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.ic_marker);

		JSONObject info = null;
		String[] truckTypes = getResources().getStringArray(
				R.array.truck_type_list);
		for (int i = 0; i < markers.length(); i++) {
			try {
				info = markers.getJSONObject(i);
				int truck = info.optInt("trunk_type_code");
				LatLng ll = new LatLng(info.optDouble("gps_w"),
						info.optDouble("gps_j"));
				Bundle bundle = new Bundle();
				bundle.putString("name", info.optString("driver_name"));
				bundle.putString("phone", info.optString("phone"));
				bundle.putString("truck_no", info.optString("trunk_no"));
				if (truck >= 0 && truck < truckTypes.length) {
					bundle.putString("truck_type", truckTypes[truck]);
				}
				OverlayOptions option = new MarkerOptions().position(ll)
						.icon(bitmap).extraInfo(bundle);
				mBaiduMap.addOverlay(option);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void showMarkerInfo(final Marker marker) {
		mBaiduMap.hideInfoWindow();
		Bundle bundle = marker.getExtraInfo();
		if (bundle != null) {
			mDriverName.setText(bundle.getString("name"));
			mDriverPhone
					.setText(String.format(getString(R.string.format_phone),
							bundle.getString("phone")));
			mDriverTruckNo.setText(String.format(
					getString(R.string.format_truck_no),
					bundle.getString("truck_no")));
			mDriverTruckType.setText(String.format(
					getString(R.string.format_truck_type),
					bundle.getString("truck_type")));
		}
		LatLng ll = marker.getPosition();
		InfoWindow infoWindow = new InfoWindow(mDriverLayout, ll,
				new OnInfoWindowClickListener() {
					public void onInfoWindowClick() {
						mBaiduMap.hideInfoWindow();
					}
				});
		mBaiduMap.showInfoWindow(infoWindow);
	}

	private void requestDriver(BDLocation location) {
		BaseParams params = new BaseParams();
		params.add("method", "getNearDriversByDrv");
		params.add("radius", "5000");
		params.add("gps_j", "" + location.getLongitude());
		params.add("gps_w", "" + location.getLatitude());
		AsyncHttp.get(Const.URL_NEAR_DRIVER, params, mRequestHandler);
	}

	private void requestResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response: " + response.toString());
			try {
				int res = response.getInt("res");
				String msg = response.getString("msg");
				if (res == 2) {// 成功
					if (mMapShowing) {
						addMarker(response.optJSONArray("info"));
					}
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void showUpdateDialog(final UpdateInfo info) {
		UpdateManager.showUpdateDialog(this, info,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(info.getUrl()));
						startActivity(intent);
					}
				}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (info.isForce()) {
							exit();
						}
					}
				});
	}

	private void exit() {
		MobclickAgent.onKillProcess(this);
		finish();
		System.exit(0);
	}

	public void goHome() {
		((RadioButton) mTabGroup.findViewById(R.id.home_tab_home))
				.setChecked(true);
	}

	private void showDetail(Intent intent) {

	}

	private void showMessage(Intent intent) {

	}

	private void showPersonal(Intent intent) {

	}

	private void showRob(Intent intent) {
		try {
			if (LoginManager.getInstance().hasLogin()) {
				String infos = intent.getStringExtra("infos");
				if (infos != null) {
					JSONObject obj = new JSONObject(infos);
					Order order = new Order();
					order.setGoods_cd(obj.optString("good_cd"));
					order.setGoods_name(obj.optString("goods_name"));
					order.setIs_order(obj.optInt("is_order"));
					order.setStart_addr(obj.optString("start_addr"));
					order.setEnd_addr(obj.optString("end_addr"));
					order.setDistance(obj.optDouble("distance"));
					RobOrderActivity.startRobOrderActivity(this, order);
				}
			} else {
				LoginActivity.startLoginActivity(this, intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onEventMainThread(ExitEvent event) {
		exit();
	}
	
	public static class MainHandler extends WeakHandler<MainActivity> {

		public MainHandler(MainActivity reference) {
			super(reference);
		}

		@Override
		public void handleMessage(MainActivity reference, Message msg) {
			switch (msg.what) {
			case MSG_PUSH:
				Intent intent = (Intent) msg.obj;
				reference.pushIntent(intent);
				break;
			}
		}

	}
	
	
}
