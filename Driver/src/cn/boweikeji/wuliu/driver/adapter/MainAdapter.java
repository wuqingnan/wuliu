package cn.boweikeji.wuliu.driver.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import cn.boweikeji.wuliu.driver.activity.MainActivity;
import cn.boweikeji.wuliu.driver.fragment.HomeFragment;

public class MainAdapter extends FragmentPagerAdapter {

	public MainAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		HomeFragment fragment = new HomeFragment();
		return fragment;
	}

	@Override
	public int getCount() {
		return MainActivity.TAB_IDS.length;
	}
	
}