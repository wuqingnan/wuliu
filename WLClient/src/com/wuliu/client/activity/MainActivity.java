package com.wuliu.client.activity;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.wuliu.client.Const;
import com.wuliu.client.R;
import com.wuliu.client.WLApplication;
import com.wuliu.client.api.BaseParams;
import com.wuliu.client.bean.UserInfo;
import com.wuliu.client.db.DBHelper;
import com.wuliu.client.fragment.BaseFragment;
import com.wuliu.client.fragment.LoginFragment;
import com.wuliu.client.fragment.MainFragment;
import com.wuliu.client.fragment.MapFragment;
import com.wuliu.client.fragment.MenuFragment;
import com.wuliu.client.fragment.OrderFragment;
import com.wuliu.client.fragment.ProfileFragment;
import com.wuliu.client.fragment.SetFragment;
import com.wuliu.client.manager.LoginManager;
import com.wuliu.client.utils.DeviceInfo;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class MainActivity extends SlidingFragmentActivity {

	private static final String TAG = MainActivity.class.getSimpleName();
	
	private static final int EXIT_TIME = 2000;
	
	private static final int REQUEST_CODE_REGISTER = 1 << 0;

	private MapFragment mMapFragment;
	private BaseFragment mTopFragment;
	private MenuFragment mMenuFragment;
	private FragmentManager mFragmentManager;

	private SlidingMenu mSlidingMenu;
	
	private ScheduledThreadPoolExecutor mTimerTask;
	
	private long mExitTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initFragment();
		initSlidingMenu();
		initArea();
		Const.init(this);
		DeviceInfo.init(this);
		LoginManager.getInstance().autoLogin();
		mTimerTask = new ScheduledThreadPoolExecutor(1);
		mTimerTask.scheduleAtFixedRate(new PositionTask(), 30, 300, TimeUnit.SECONDS);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mSlidingMenu.isMenuShowing()) {
				mSlidingMenu.showContent();
				return true;
			}
			if (mFragmentManager.getBackStackEntryCount() == 0) {
				if (System.currentTimeMillis() - mExitTime > EXIT_TIME) {
					Toast.makeText(this, R.string.quit_next_time, Toast.LENGTH_SHORT).show();
					mExitTime = System.currentTimeMillis();
				}
				else {
					mTimerTask.shutdown();
					mTimerTask.shutdownNow();
					finish();
					System.exit(0);
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_REGISTER) {
			if (resultCode == RESULT_OK) {
				if (mTopFragment instanceof LoginFragment) {
					UserInfo info = (UserInfo) data.getSerializableExtra("userinfo");
					((LoginFragment)mTopFragment).login(info);
				}
			}
		}
	}

	private void initFragment() {
		mFragmentManager = getSupportFragmentManager();
		mMapFragment = new MapFragment();
		mTopFragment = new MainFragment();
		mMenuFragment = new MenuFragment();
		mFragmentManager.beginTransaction()
				.replace(R.id.mapLayout, mMapFragment).commit();
		mFragmentManager.beginTransaction()
				.replace(R.id.topLayout, mTopFragment).commit();
		setBehindContentView(R.layout.menu_frame);
		mFragmentManager.beginTransaction()
				.replace(R.id.menu_frame, mMenuFragment).commit();
	}

	private void initSlidingMenu() {
		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		// sm.setShadowDrawable(R.drawable.shadow);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
	}

	public void onClickTitle(BaseFragment fragment) {
		if (fragment instanceof MainFragment) {
			if (LoginManager.getInstance().hasLogin()) {
				mSlidingMenu.showMenu();
			} else {
				switchContent(new LoginFragment());
			}
		}
		else if (fragment instanceof LoginFragment) {
			mFragmentManager.popBackStack();
		}
		else if (fragment instanceof SetFragment) {
			mFragmentManager.popBackStack();
		}
		else if (fragment instanceof ProfileFragment) {
			mFragmentManager.popBackStack();
		}
		else if (fragment instanceof OrderFragment) {
			mFragmentManager.popBackStack();
		}
	}
	
	public void loginSuccess() {
		mMenuFragment.updateInfo();
		mFragmentManager.popBackStack();
	}
	
	public void register() {
		startActivityForResult(new Intent(this, RegisterActivity.class), REQUEST_CODE_REGISTER);
	}

	public void switchContent(BaseFragment fragment) {
		mTopFragment = fragment;
		FragmentTransaction trans = mFragmentManager.beginTransaction();
		trans.setCustomAnimations(R.anim.push_in, R.anim.push_out, R.anim.pop_in, R.anim.pop_out);
		trans.replace(R.id.topLayout, fragment);
		trans.addToBackStack(null);
		trans.commit();
		mSlidingMenu.showContent();
	}
	
	private void initArea() {
		DBHelper helper = ((WLApplication)getApplication()).getHelper();
		SQLiteDatabase db = helper.getReadableDatabase();
		db.close();
	}
	
	private class PositionTask implements Runnable {
		
		AsyncHttpClient mHttpClient;
		
		public PositionTask() {
			mHttpClient = new AsyncHttpClient();
			mHttpClient.setURLEncodingEnabled(true);
		}
		
		@Override
		public void run() {
			UserInfo info = LoginManager.getInstance().getUserInfo();
			if (info == null) {
				return;
			}
			
			LocationClient client = WLApplication.getLocationClient();
			if (client == null) { 
				return;
			}
			
			BDLocation location = client.getLastKnownLocation();
			
			BaseParams params = new BaseParams();
			params.add("method", "collectSupplyInfos");
			params.add("supplyer_cd", info.getSupplyer_cd());
			params.add("gps_j", "" + location.getLongitude());
			params.add("gps_w", "" + location.getLatitude());
			params.add("speed", "" + location.getSpeed());
			params.add("phone_type", "" + DeviceInfo.getModel());
			params.add("operate_sysem", "" + DeviceInfo.getOSName());
			params.add("sys_edtion", "" + DeviceInfo.getOSVersion());
			
			Log.d(TAG, "URL: " + AsyncHttpClient.getUrlWithQueryString(true, Const.URL_POSITION_UPLOAD, params));
			mHttpClient.get(Const.URL_POSITION_UPLOAD, params, null);
		}
	}
}
