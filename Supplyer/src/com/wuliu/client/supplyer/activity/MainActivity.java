package com.wuliu.client.supplyer.activity;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import com.wuliu.client.supplyer.R;
import com.wuliu.client.supplyer.bean.UserInfo;
import com.wuliu.client.supplyer.fragment.BaseFragment;
import com.wuliu.client.supplyer.fragment.LoginFragment;
import com.wuliu.client.supplyer.fragment.MainFragment;
import com.wuliu.client.supplyer.fragment.MapFragment;
import com.wuliu.client.supplyer.fragment.OrderFragment;
import com.wuliu.client.supplyer.fragment.ProfileFragment;
import com.wuliu.client.supplyer.fragment.SetFragment;
import com.wuliu.client.supplyer.manager.LoginManager;
import com.wuliu.client.supplyer.utils.Util;
import com.wuliu.client.supplyer.view.MenuView;

import de.greenrobot.event.EventBus;
import android.content.Intent;
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

	private MenuView.OnMenuClickListener mOnMenuClickListener = new MenuView.OnMenuClickListener() {
		@Override
		public void onMenuClick(int menu) {
			switch (menu) {
			case MenuView.MENU_ORDER:
				changeFragment(new OrderFragment());
				break;
			case MenuView.MENU_PROFILE:
				changeFragment(new ProfileFragment());
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
	
	private MenuView mMenuView;
	private MenuDrawer mMenuDrawer;
	
	private MapFragment mMapFragment;
	private FragmentManager mFragmentManager;

	private long mExitTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        initMenu();
        initFragment();
		LoginManager.getInstance().autoLogin();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
					finish();
					System.exit(0);
				}
				return true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (mFragmentManager.getBackStackEntryCount() == 0) {
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
		trans.addToBackStack(null);
		trans.commit();
		mMenuDrawer.closeMenu();
	}
	
}
