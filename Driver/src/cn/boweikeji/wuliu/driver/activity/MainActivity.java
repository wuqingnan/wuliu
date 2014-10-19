package cn.boweikeji.wuliu.driver.activity;

import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.R.layout;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();
	
	private static final int MAIN_PAGE_CNT = 5;
	
	private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private class MainAdapter extends FragmentPagerAdapter {

		public MainAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return null;
		}

		@Override
		public int getCount() {
			return MAIN_PAGE_CNT;
		}
    	
    }
}
