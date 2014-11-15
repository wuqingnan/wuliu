package cn.boweikeji.wuliu.driver.fragment;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.Const;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.WLApplication;
import cn.boweikeji.wuliu.driver.activity.LoginActivity;
import cn.boweikeji.wuliu.driver.activity.MainActivity;
import cn.boweikeji.wuliu.driver.api.BaseParams;
import cn.boweikeji.wuliu.driver.event.LoginEvent;
import cn.boweikeji.wuliu.driver.event.LogoutEvent;
import cn.boweikeji.wuliu.driver.listener.ILocationListener;
import cn.boweikeji.wuliu.driver.manager.LoginManager;

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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import de.greenrobot.event.EventBus;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeFragment extends BaseFragment {

	private static final String TAG = HomeFragment.class.getSimpleName();
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mLogin) {
				LoginActivity.startLoginActivity(getActivity());
			}
		}
	};
	
	private JsonHttpResponseHandler mRequestHandler = new JsonHttpResponseHandler() {
		
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			requestResult(response);
		};
		
		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
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

	private OnMyLocationClickListener mLocClickListener = new OnMyLocationClickListener() {
		@Override
		public boolean onMyLocationClick() {
			Log.d(TAG, "shizy---onMyLocationClick");
			showMyLocInfo();
			return false;
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
	
	private ILocationListener mLocationListener = new ILocationListener() {
		@Override
		public void onLocation(BDLocation location) {
			if (location == null || mMapView == null) {
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
				showMyLocInfo();
				requestDriver(location);
			}
		}
	};

	private View mRootView;
	
	@InjectView(R.id.titlebar_leftBtn)
	ImageView mBack;
	@InjectView(R.id.titlebar_rightTxt)
	Button mRegister;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.titlebar_rightTxt)
	Button mLogin;
	@InjectView(R.id.bmapView)
	MapView mMapView;
	private BaiduMap mBaiduMap;
	private UiSettings mUiSettings;

	private View mMyLocLayout;
	private TextView mMyLocTitle;
	private TextView mMyLocInfo;
	private View mDriverLayout;
	private ImageView mDriverPortrait;
	private TextView mDriverName;
	private TextView mDriverInfo;

	private boolean mIsFirstLoc;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_home, null);
		return mRootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		((MainActivity)getActivity()).setLocationListener(null);
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		EventBus.getDefault().unregister(this);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	private void init() {
		ButterKnife.inject(this, mRootView);
		mIsFirstLoc = true;
		initTitle();
		initView();
		initMap();
		EventBus.getDefault().register(this);
		((MainActivity)getActivity()).setLocationListener(mLocationListener);
	}

	private void initTitle() {
		mTitle.setText(R.string.title_home);
		mBack.setVisibility(View.GONE);
		mLogin.setVisibility(View.VISIBLE);
		mLogin.setText(R.string.login);
		mLogin.setOnClickListener(mOnClickListener);
		updateLoginBtn();
	}
	
	private void initView() {
		mMyLocLayout = LayoutInflater.from(getActivity()).inflate(
				R.layout.popup_myloc, null);
		mMyLocTitle = (TextView) mMyLocLayout.findViewById(R.id.myloc_title);
		mMyLocInfo = (TextView) mMyLocLayout.findViewById(R.id.myloc_info);

		mDriverLayout = LayoutInflater.from(getActivity()).inflate(
				R.layout.popup_driver, null);
		mDriverPortrait = (ImageView) mDriverLayout
				.findViewById(R.id.driver_portrait);
		mDriverName = (TextView) mDriverLayout.findViewById(R.id.driver_name);
		mDriverInfo = (TextView) mDriverLayout.findViewById(R.id.driver_info);
	}

	private void initMap() {
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		mBaiduMap.setOnMapClickListener(mOnMapClickListener);
		mBaiduMap.setOnMyLocationClickListener(mLocClickListener);
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

	private void addMarker(JSONArray markers) {
		if (markers == null || markers.length() <= 0) {
			return;
		}
		
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.marker1);

		JSONObject info = null;
		String[] truckTypes = getResources().getStringArray(R.array.truck_type_list);
		for (int i = 0; i < markers.length(); i++) {
			try {
				info = markers.getJSONObject(i);
				int truck = info.optInt("trunk_type_code");
				LatLng ll = new LatLng(info.optDouble("gps_w"), info.optDouble("gps_j"));
				Bundle bundle = new Bundle();
				bundle.putString("name", info.optString("driver_name"));
				if (truck >= 0 && truck < truckTypes.length) {
					bundle.putString("info", truckTypes[truck]);
				}
				OverlayOptions option = new MarkerOptions().position(ll).icon(bitmap)
						.extraInfo(bundle);
				mBaiduMap.addOverlay(option);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void showMyLocInfo() {
		BDLocation loc = WLApplication.getLocationClient().getLastKnownLocation();
		if (loc == null || loc.getAddrStr() == null) {
			return;
		}
		mMyLocTitle.setText(String.format(
				getResources().getString(R.string.myloc), loc.getStreet()));
		mMyLocInfo.setText(loc.getCity() + " " + loc.getDistrict());
		MyLocationData data = mBaiduMap.getLocationData();
		LatLng ll = new LatLng(data.latitude, data.longitude);
		InfoWindow infoWindow = new InfoWindow(mMyLocLayout, ll,
				new OnInfoWindowClickListener() {
					public void onInfoWindowClick() {
						mBaiduMap.hideInfoWindow();
					}
				});
		mBaiduMap.showInfoWindow(infoWindow);
	}

	private void showMarkerInfo(final Marker marker) {
		Bundle bundle = marker.getExtraInfo();
		if (bundle != null) {
			mDriverName.setText(bundle.getString("name"));
			mDriverInfo.setText(bundle.getString("info"));
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
		AsyncHttpClient client = new AsyncHttpClient();
		client.setURLEncodingEnabled(true);
		
		BaseParams params = new BaseParams();
		params.add("method", "getNearDriversByDrv");
		params.add("radius", "5000");
		params.add("gps_j", "" + location.getLongitude());
		params.add("gps_w", "" + location.getLatitude());
		
		Log.d(TAG, "URL: " + AsyncHttpClient.getUrlWithQueryString(true, Const.URL_NEAR_DRIVER, params));
		client.get(Const.URL_NEAR_DRIVER, params, mRequestHandler);
	}
	
	private void requestResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response123: " + response.toString());
			try {
				int res = response.getInt("res");
				String msg = response.getString("msg");
				if (res == 2) {//成功
					addMarker(response.optJSONArray("info"));
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateLoginBtn() {
		if (LoginManager.getInstance().hasLogin()) {
			mLogin.setVisibility(View.GONE);
		}
	}
	
	public void onEventMainThread(LoginEvent event) {
		updateLoginBtn();
	}
	
	public void onEventMainThread(LogoutEvent event) {
		updateLoginBtn();
	}
}
