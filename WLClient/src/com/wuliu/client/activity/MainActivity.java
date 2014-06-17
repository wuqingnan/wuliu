package com.wuliu.client.activity;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.wuliu.client.R;
import com.wuliu.client.fragment.MainFragment;
import com.wuliu.client.fragment.MapFragment;
import com.wuliu.client.fragment.MenuFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

public class MainActivity extends SlidingFragmentActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private Fragment mContent;
	private FragmentManager mFragmentManager;
	
	private SlidingMenu mSlidingMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		mFragmentManager = getSupportFragmentManager();

		mContent = new MapFragment();

		mFragmentManager.beginTransaction().replace(R.id.mapLayout, mContent)
				.commit();

		mFragmentManager.beginTransaction()
				.replace(R.id.topLayout, new MainFragment()).commit();

		setBehindContentView(R.layout.menu_frame);
		mFragmentManager.beginTransaction()
				.replace(R.id.menu_frame, new MenuFragment()).commit();

		initSlidingMenu();
	}

	private void initSlidingMenu() {
		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		// sm.setShadowDrawable(R.drawable.shadow);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
	}

	public void onTitleClick(View view) {
		mSlidingMenu.showMenu();
	}
	
	public void switchContent(Fragment fragment) {
		mContent = fragment;
		// getSupportFragmentManager().beginTransaction()
		// .replace(R.id.content_frame, fragment).commit();
		// getSlidingMenu().showContent();
	}

}
