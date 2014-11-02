package cn.boweikeji.wuliu.driver.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import cn.boweikeji.wuliu.driver.activity.MainActivity;
import cn.boweikeji.wuliu.driver.fragment.HomeFragment;
import cn.boweikeji.wuliu.driver.fragment.MessageFragment;

public class MainAdapter extends FragmentPagerAdapter {

	public MainAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new HomeFragment();
			break;
		case 1:
			fragment = new MessageFragment();
			break;
		case 2:
			fragment = new HomeFragment();
			break;
		case 3:
			fragment = new HomeFragment();
			break;
		case 4:
			fragment = new HomeFragment();
			break;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return MainActivity.TAB_IDS.length;
	}
	
}