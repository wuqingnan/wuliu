package cn.boweikeji.wuliu.driver.activity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.adapter.MainAdapter;
import cn.boweikeji.wuliu.driver.fragment.HomeFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends BaseActivity {

	private static final String TAG = MainActivity.class.getSimpleName();
	
	public static final int[] TAB_IDS = {
		R.id.home_tab_home,
		R.id.home_tab_msg,
		R.id.home_tab_order,
		R.id.home_tab_find,
		R.id.home_tab_mine
	};
	
	private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int state) {
			
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			
		}

		@Override
		public void onPageSelected(int position) {
    		((RadioButton)mTabGroup.findViewById(TAB_IDS[position])).setChecked(true);
		}
	};
	
	private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			for (int i = 0; i < TAB_IDS.length; i++) {
				if (TAB_IDS[i] == checkedId) {
		    		mViewPager.setCurrentItem(i, false);
					break;
				}
			}
		}
	};
	
	@InjectView(R.id.bottom_layout)
	LinearLayout mBottomLayout;
	@InjectView(R.id.home_tab_group)
	RadioGroup mTabGroup;
	@InjectView(R.id.viewpager)
	ViewPager mViewPager;
	
	private MainAdapter mAdapter;
	
	private FragmentManager mFragmentManager;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    
    private void init() {
    	mFragmentManager = getSupportFragmentManager();
    	mAdapter = new MainAdapter(mFragmentManager);
    	initView();
    }
    
    private void initView() {
    	ButterKnife.inject(this);
    	mTabGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
    	mViewPager.setOnPageChangeListener(mOnPageChangeListener);
    	mViewPager.setAdapter(mAdapter);
    }
    
}
