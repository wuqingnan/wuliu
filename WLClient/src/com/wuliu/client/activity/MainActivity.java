package com.wuliu.client.activity;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.wuliu.client.R;
import com.wuliu.client.fragment.ColorFragment;
import com.wuliu.client.fragment.ColorMenuFragment;
import com.wuliu.client.fragment.MapFragment;
import com.wuliu.client.fragment.MenuFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class MainActivity extends SlidingFragmentActivity {

	private static final String TAG = MainActivity.class.getSimpleName();
	
	private Fragment mContent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		mContent = new MapFragment();
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.mapLayout, mContent).commit();
		
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new MenuFragment()).commit();

		initSlidingMenu();
	}
	
	private void initSlidingMenu() {
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		// sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}

	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		getSlidingMenu().showContent();
	}

}
