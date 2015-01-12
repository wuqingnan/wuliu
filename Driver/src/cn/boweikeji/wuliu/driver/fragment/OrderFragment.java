package cn.boweikeji.wuliu.driver.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.R;

public class OrderFragment extends BaseFragment {

	private static final String TAG = OrderFragment.class.getSimpleName();

	private static final String[] TAB_NAMES = { "待选", "已抢到", "已完成", "取消" };

	private View mRootView;

	@InjectView(R.id.titlebar_leftBtn)
	ImageView mBack;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(android.R.id.tabhost)
	TabHost mTabHost;
	@InjectView(R.id.pager)
	ViewPager mViewPager;

	private TabsAdapter mTabsAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_order, null);
		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	private void init() {
		ButterKnife.inject(this, mRootView);
		initTitle();
		initView();
	}

	private void initTitle() {
		mTitle.setText(R.string.home_tab_order);
		mBack.setVisibility(View.GONE);
	}

	private void initView() {
		Log.d(TAG, "shizy---OrderFragment.initView");
		mTabHost.setup();
		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View[] indicators = new View[4];
		for (int i = 0; i < indicators.length; i++) {
			indicators[i] = inflater.inflate(R.layout.tab_indicator, null);
			((TextView) (indicators[i].findViewById(R.id.title)))
					.setText(TAB_NAMES[i]);
		}
		mTabsAdapter.addTab(
				mTabHost.newTabSpec("select").setIndicator(indicators[0]),
				OrderListFragment.class, OrderListFragment.TYPE_SELECT);
		mTabsAdapter.addTab(
				mTabHost.newTabSpec("selected").setIndicator(indicators[1]),
				OrderListFragment.class, OrderListFragment.TYPE_SELECTED);
		mTabsAdapter.addTab(
				mTabHost.newTabSpec("completed").setIndicator(indicators[2]),
				OrderListFragment.class, OrderListFragment.TYPE_COMPLETED);
		mTabsAdapter.addTab(
				mTabHost.newTabSpec("cancel").setIndicator(indicators[3]),
				OrderListFragment.class, OrderListFragment.TYPE_CANCEL);
		indicators = null;
		mTabHost.getTabWidget().setDividerDrawable(R.color.white);
		mTabHost.setCurrentTab(0);
	}

	public static class TabsAdapter extends FragmentPagerAdapter implements
			TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final TabHost mTabHost;
		private final ViewPager mViewPager;
		private final FragmentManager mFM;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		private boolean mNeedInit = true;
		
		public TabsAdapter(Fragment fragment, TabHost tabHost, ViewPager pager) {
			super(fragment.getChildFragmentManager());
			mFM = fragment.getChildFragmentManager();
			mContext = fragment.getActivity();
			mTabHost = tabHost;
			mViewPager = pager;
			mTabHost.setOnTabChangedListener(this);
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, int type) {
			tabSpec.setContent(new DummyTabFactory(mContext));
			String tag = tabSpec.getTag();

			Bundle args = new Bundle();
			args.putInt(OrderListFragment.KEY_TYPE, type);
			TabInfo info = new TabInfo(tag, clss, args);
			mTabs.add(info);
			mTabHost.addTab(tabSpec);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Object object = super.instantiateItem(container, position);
			((OrderListFragment)object).setNeedInit(mNeedInit);
			if (mNeedInit) {
				mNeedInit = false;
			}
			return object;
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(),
					info.args);
		}

		@Override
		public void onTabChanged(String tabId) {
			int position = mTabHost.getCurrentTab();
			mViewPager.setCurrentItem(position);
			OrderListFragment fragment = getFragmentByPosition(position);
			if (fragment != null) {
				fragment.refresh();
			}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			// Unfortunately when TabHost changes the current tab, it kindly
			// also takes care of putting focus on it when not in touch mode.
			// The jerk.
			// This hack tries to prevent this from pulling focus out of our
			// ViewPager.
			TabWidget widget = mTabHost.getTabWidget();
			int oldFocusability = widget.getDescendantFocusability();
			widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
			mTabHost.setCurrentTab(position);
			widget.setDescendantFocusability(oldFocusability);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		private OrderListFragment getFragmentByPosition(int position) {
			List<Fragment> fragments = mFM.getFragments();
			if (fragments == null || fragments.size() == 0) {
				return null;
			}
			for (Fragment fragment : fragments) {
				if (fragment instanceof OrderListFragment) {
					if (((OrderListFragment)fragment).getType() == position) {
						return (OrderListFragment)fragment;
					}
				}
			}
			return null;
		}
		
		static final class TabInfo {
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}
	}
}
