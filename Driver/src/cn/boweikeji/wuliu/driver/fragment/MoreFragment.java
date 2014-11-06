package cn.boweikeji.wuliu.driver.fragment;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.R;

public class MoreFragment extends BaseFragment {

	public static final String TAG = MoreFragment.class.getSimpleName();
	
	private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
		}
	};
	
	private View mRootView;

	@InjectView(R.id.titlebar_leftBtn)
	ImageView mBack;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.fragment_more_list)
	ListView mListView;
	
	private MoreAdapter mAdapter;

	private String[] mListNames;
	private TypedArray mListIcons;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_more, null);
		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mListIcons != null) {
			mListIcons.recycle();
		}
	}
	
	private void init() {
		ButterKnife.inject(this, mRootView);
		initTitle();
		initView();
		initData();
	}

	private void initTitle() {
		mTitle.setText(R.string.home_tab_more);
		mBack.setVisibility(View.GONE);
	}

	private void initView() {
		mListView.setOnItemClickListener(mOnItemClickListener);
	}
	
	private void initData() {
		mListNames = getResources().getStringArray(R.array.more_list_name);
		mListIcons = getResources().obtainTypedArray(R.array.more_list_icon);
		mAdapter = new MoreAdapter();
		mListView.setAdapter(mAdapter);
	}
	
	private class MoreAdapter extends BaseAdapter {
		
		private LayoutInflater mInflater;
		
		public MoreAdapter() {
			mInflater = LayoutInflater.from(getActivity());
		}
		
		@Override
		public int getCount() {
			return mListNames == null ? 0 : mListNames.length;
		}

		@Override
		public Object getItem(int position) {
			return mListNames[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView != null) {
				holder = (ViewHolder) convertView.getTag();
			} else {
				convertView = mInflater.inflate(R.layout.fragment_more_item, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}
			holder.mIconView.setImageDrawable(mListIcons.getDrawable(position));
			holder.mTitleView.setText(mListNames[position]);
			return convertView;
		}
	}
	
	class ViewHolder {
		
		@InjectView(R.id.item_icon)
		ImageView mIconView;
		@InjectView(R.id.item_title)
		TextView mTitleView;
		
		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
}
