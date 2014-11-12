package cn.boweikeji.wuliu.supplyer.activity;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.loopj.android.http.AsyncHttpClient;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import cn.boweikeji.wuliu.supplyer.api.BaseParams;
import cn.boweikeji.wuliu.supplyer.bean.UserInfo;
import cn.boweikeji.wuliu.supplyer.event.UpdateEvent;
import cn.boweikeji.wuliu.supplyer.fragment.BaseFragment;
import cn.boweikeji.wuliu.supplyer.fragment.LoginFragment;
import cn.boweikeji.wuliu.supplyer.fragment.MainFragment;
import cn.boweikeji.wuliu.supplyer.fragment.MapFragment;
import cn.boweikeji.wuliu.supplyer.fragment.OrderFragment;
import cn.boweikeji.wuliu.supplyer.fragment.ProfileFragment;
import cn.boweikeji.wuliu.supplyer.fragment.SetFragment;
import cn.boweikeji.wuliu.supplyer.listener.ILocationListener;
import cn.boweikeji.wuliu.supplyer.manager.LoginManager;
import cn.boweikeji.wuliu.supplyer.utils.DeviceInfo;
import cn.boweikeji.wuliu.supplyer.utils.UpdateUtil;
import cn.boweikeji.wuliu.supplyer.utils.Util;
import cn.boweikeji.wuliu.supplyer.view.MenuView;
import cn.boweikeji.wuliu.supplyer.Const;
import cn.boweikeji.wuliu.supplyer.R;
import cn.boweikeji.wuliu.supplyer.WLApplication;
import de.greenrobot.event.EventBus;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

	private static final String TAG = MainActivity.class.getSimpleName();
	
	private static final int EXIT_TIME = 2000;
	
	private static final int REQUEST_CODE_REGISTER = 1 << 0;
	
	public static final String KEY_REDIRECT = "redirect";
	public static final String KEY_REDIRECT_TO = "redirect_to";
	public static final int REDIRECT_TO_ORDERDETAIL = 1 << 0;

	private MenuView.OnMenuClickListener mOnMenuClickListener = new MenuView.OnMenuClickListener() {
		@Override
		public void onMenuClick(int menu) {
			switch (menu) {
			case MenuView.MENU_PROFILE:
				changeFragment(new ProfileFragment());
				break; 
			case MenuView.MENU_ORDER:
				changeFragment(new OrderFragment());
				break;
			case MenuView.MENU_MESSAGE:
				mMenuDrawer.closeMenu();
				WebViewActivity.startWebViewActivity(MainActivity.this, getString(R.string.title_system_msg), Const.URL_SYSTEM_MSG);
				break;
			case MenuView.MENU_INVITE:
				mMenuDrawer.closeMenu();
				Util.sendMessage(MainActivity.this, null, getResources().getString(R.string.invite_msg));
				break;
			case MenuView.MENU_SHARE:
				mMenuDrawer.closeMenu();
				Util.showShare(MainActivity.this);
				break;
			case MenuView.MENU_SETTING:
				changeFragment(new SetFragment());
				break;
			}
		}
	};
	
	private BDLocationListener mBDLocListener = new BDLocationListener() {

		@Override
		public void onReceivePoi(BDLocation poiLocation) {

		}

		@Override
		public void onReceiveLocation(BDLocation location) {
//			Log.d(TAG, "shizy---onReceiveLocation");
			if (mLocationListener != null) {
				mLocationListener.onLocation(location);
			}
		}
	};
	
	private MenuView mMenuView;
	private MenuDrawer mMenuDrawer;
	
	private MapFragment mMapFragment;
	private FragmentManager mFragmentManager;

	private LocationClient mLocClient;
	private ILocationListener mLocationListener;

	private ScheduledThreadPoolExecutor mTimerTask;
	
	private long mExitTime;

	private boolean mFirstCheckUpdate = true;
	private boolean mShowUpdateDialog = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocClient.unRegisterLocationListener(mBDLocListener);
		mLocClient.stop();
		mTimerTask.shutdown();
		mTimerTask.shutdownNow();
		EventBus.getDefault().unregister(this);
		EventBus.getDefault().unregister(mMenuView);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			final int drawerState = mMenuDrawer.getDrawerState();
	        if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
	            mMenuDrawer.closeMenu();
	            return true;
	        }
			if (mFragmentManager.getBackStackEntryCount() == 0) {
				if (System.currentTimeMillis() - mExitTime > EXIT_TIME) {
					Toast.makeText(this, R.string.quit_next_time, Toast.LENGTH_SHORT).show();
					mExitTime = System.currentTimeMillis();
				}
				else {
					exit();
				}
				return true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (mFragmentManager.getBackStackEntryCount() == 0 && LoginManager.getInstance().hasLogin()) {
				mMenuDrawer.toggleMenu();
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
				Fragment fragment = mFragmentManager.findFragmentByTag(LoginFragment.class.getName());
				if (fragment != null) {
					UserInfo info = (UserInfo) data.getSerializableExtra("userinfo");
					((LoginFragment)fragment).login(info);
				}
			}
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getBooleanExtra(KEY_REDIRECT, false)) {
			int to = intent.getIntExtra(KEY_REDIRECT_TO, 0);
			switch (to) {
			case REDIRECT_TO_ORDERDETAIL:
				OrderDetailActivity.startOrderDetailActivity(this, intent.getStringExtra("goods_cd"));
				break;
			}
		}
		else {
			if (mFragmentManager.getBackStackEntryCount() > 0) {
				back();
			}
		}
	}
	
	private void init() {
		initMenu();
        initFragment();
        initLocation();
		LoginManager.getInstance().autoLogin();
		EventBus.getDefault().register(this);
		UpdateUtil.checkUpdate();
		mTimerTask = new ScheduledThreadPoolExecutor(1);
		mTimerTask.scheduleAtFixedRate(new PositionTask(), 30, 300, TimeUnit.SECONDS);
	}

	private void initMenu() {
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, Position.LEFT, MenuDrawer.MENU_DRAG_WINDOW);
        mMenuDrawer.setContentView(R.layout.activity_main);
        mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);
        
        mMenuView = new MenuView(this);
        mMenuView.setOnMenuClickListener(mOnMenuClickListener);
        EventBus.getDefault().register(mMenuView);
        
        mMenuDrawer.setMenuView(mMenuView);
	}

	private void initFragment() {
		mFragmentManager = getSupportFragmentManager();
		
		mMapFragment = new MapFragment();
		mFragmentManager.beginTransaction()
				.replace(R.id.mapLayout, mMapFragment).commit();
		mFragmentManager.beginTransaction()
				.replace(R.id.topLayout, new MainFragment()).commit();
	}
	
	private void initLocation() {
		mLocClient = new LocationClient(getApplicationContext());
		WLApplication.setLocationClient(mLocClient);
		mLocClient.registerLocationListener(mBDLocListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(60 * 1000);
		option.setIsNeedAddress(true);
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		mLocClient.setLocOption(option);
		mLocClient.start();
		mLocClient.requestLocation();
	}

	public void showMenu() {
		if (LoginManager.getInstance().hasLogin()) {
			mMenuDrawer.openMenu();
		} else {
			changeFragment(new LoginFragment());
		}
	}
	
	public void back() {
		mFragmentManager.popBackStack();
	}
	
	public void register() {
		startActivityForResult(new Intent(this, RegisterActivity.class), REQUEST_CODE_REGISTER);
	}

	public void changeFragment(BaseFragment fragment) {
		FragmentTransaction trans = mFragmentManager.beginTransaction();
		trans.setCustomAnimations(R.anim.push_in, R.anim.push_out, R.anim.pop_in, R.anim.pop_out);
		trans.replace(R.id.topLayout, fragment, fragment.getClass().getName());
		trans.addToBackStack(fragment.getClass().getSimpleName());
		trans.commit();
		mMenuDrawer.closeMenu();
	}
	
	private void exit() {
		finish();
		System.exit(0);
	}
	
	public void setLocationListener(ILocationListener listener) {
		mLocationListener = listener;
	}
	
	public void onEventMainThread(UpdateEvent event) {
		if (mFirstCheckUpdate) {
			mFirstCheckUpdate = false;			
			showUpdateDialog(event);
		}
	}
	
	public synchronized void showUpdateDialog(final UpdateEvent event) {
		if (event != null) {
			if (event.isNeedUpdate()) {
				if (mShowUpdateDialog) {
					return;
				}
				mShowUpdateDialog = true;
				
				AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(event.getVersion())
				.setMessage(event.getContent())
				.setCancelable(false)
				.setPositiveButton(R.string.upgrade, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						mShowUpdateDialog = false;
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(event.getUrl()));
						startActivity(intent);
					}
				})
				.setNegativeButton(event.isForce() ? R.string.exit : R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						mShowUpdateDialog = false;
						if (event.isForce()) {
							exit();
						}
					}
				})
				.create();
				dialog.show();
			}
		}
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
			params.add("operate_system", "" + DeviceInfo.getOSName());
			params.add("sys_edtion", "" + DeviceInfo.getOSVersion());
			
			Log.d(TAG, "URL: " + AsyncHttpClient.getUrlWithQueryString(true, Const.URL_POSITION_UPLOAD, params));
			mHttpClient.get(Const.URL_POSITION_UPLOAD, params, null);
		}
	}
}
