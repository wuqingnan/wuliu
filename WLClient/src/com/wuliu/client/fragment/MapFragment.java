package com.wuliu.client.fragment;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMyLocationClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.model.LatLng;
import com.wuliu.client.R;
import com.wuliu.client.WLApplication;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MapFragment extends BaseFragment {

	private static final String TAG = MapFragment.class.getSimpleName();

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

	private BDLocationListener mLocListener = new BDLocationListener() {

		@Override
		public void onReceivePoi(BDLocation poiLocation) {

		}

		@Override
		public void onReceiveLocation(BDLocation location) {
			Log.d(TAG, "shizy---onReceiveLocation");
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
			}
		}
	};

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private UiSettings mUiSettings;

	private View mMyLocLayout;
	private TextView mMyLocTitle;
	private TextView mMyLocInfo;
	private View mDriverLayout;
	private ImageView mDriverPortrait;
	private TextView mDriverName;
	private TextView mDriverInfo;

	private LocationClient mLocClient;

	private boolean mIsFirstLoc;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMapView = (MapView) inflater.inflate(R.layout.fragment_map, null);
		return mMapView;
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		mLocClient.stop();
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
	}

	private void init() {
		mIsFirstLoc = true;
		initView();
		setMap();
		setMyLocation();
		addMarker();
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

	private void setMap() {
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		mBaiduMap.setOnMyLocationClickListener(mLocClickListener);
		mBaiduMap.setOnMarkerClickListener(mOnMarkerClickListener);
		mUiSettings = mBaiduMap.getUiSettings();
		mUiSettings.setCompassEnabled(false);
		mUiSettings.setZoomGesturesEnabled(true);
		mUiSettings.setScrollGesturesEnabled(true);
		mUiSettings.setRotateGesturesEnabled(false);
		mUiSettings.setOverlookingGesturesEnabled(false);
	}

	private void setMyLocation() {
		mBaiduMap.setMyLocationEnabled(true);
		// mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(
		// LocationMode.NORMAL, true, null));
		mLocClient = new LocationClient(getActivity());
		WLApplication.setLocationClient(mLocClient);
		mLocClient.registerLocationListener(mLocListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(1000);
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	private void addMarker() {
		LatLng ll1 = new LatLng(39.972821, 116.400244);
		LatLng ll2 = new LatLng(39.972821, 116.429199);
		LatLng ll3 = new LatLng(39.959723, 116.415541);
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.marker1);

		Bundle bundle1 = new Bundle();
		Bundle bundle2 = new Bundle();
		Bundle bundle3 = new Bundle();

		bundle1.putString("name", "史师傅");
		bundle2.putString("name", "高师傅");
		bundle3.putString("name", "杨师傅");

		bundle1.putString("info", "为人正直、乐善好施");
		bundle2.putString("info", "阴险狡诈、卑鄙无耻");
		bundle3.putString("info", "奸淫掳掠、无恶不作");

		OverlayOptions option1 = new MarkerOptions().position(ll1).icon(bitmap)
				.extraInfo(bundle1);
		OverlayOptions option2 = new MarkerOptions().position(ll2).icon(bitmap)
				.extraInfo(bundle2);
		OverlayOptions option3 = new MarkerOptions().position(ll3).icon(bitmap)
				.extraInfo(bundle3);
		mBaiduMap.addOverlay(option1);
		mBaiduMap.addOverlay(option2);
		mBaiduMap.addOverlay(option3);
	}

	private void showMyLocInfo() {
		BDLocation loc = mLocClient.getLastKnownLocation();
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
	
}
