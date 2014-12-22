package cn.boweikeji.wuliu.driver.fragment;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.Const;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.activity.MainActivity;
import cn.boweikeji.wuliu.driver.activity.ProfileActivity;
import cn.boweikeji.wuliu.driver.activity.SetActivity;
import cn.boweikeji.wuliu.driver.activity.SuggestActivity;
import cn.boweikeji.wuliu.driver.activity.WebViewActivity;
import cn.boweikeji.wuliu.driver.bean.UserInfo;
import cn.boweikeji.wuliu.driver.manager.LoginManager;
import cn.boweikeji.wuliu.utils.Util;

public class MoreFragment extends BaseFragment {

	public static final String TAG = MoreFragment.class.getSimpleName();

	private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch ((int) id) {
			case -1:// 我的资料
				profile();
				break;
			case 0:// 承运指南
				guide();
				break;
			case 1:// 邀请好友
				Util.sendMessage(getActivity(), null,
						getResources().getString(R.string.invite_msg));
				break;
			case 2:// 分享给好友
				Util.showShare(getActivity());
				break;
			case 3:// 反馈与建议
				suggest();
				break;
			case 4:// 设置
				set();
				break;
			}
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
		initHeader();
		mListView.setOnItemClickListener(mOnItemClickListener);
	}

	private void initHeader() {
		View header = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_more_header, null);
		mListView.addHeaderView(header);
		TextView name = (TextView) header.findViewById(R.id.name);
		TextView phone = (TextView) header.findViewById(R.id.phone);
		UserInfo userInfo = LoginManager.getInstance().getUserInfo();
		name.setText(userInfo.getDriver_name());
		phone.setText(userInfo.getPhone());
	}

	private void initData() {
		mListNames = new String[] { getString(R.string.driver_guide),
				getString(R.string.invite_firend),
				getString(R.string.share_firend),
				getString(R.string.suggestion), getString(R.string.setting) };
		mListIcons = getResources().obtainTypedArray(R.array.more_list_icon);
		mAdapter = new MoreAdapter();
		mListView.setAdapter(mAdapter);
	}

	private void profile() {
		ProfileActivity.startProfileActivity(getActivity());
	}

	private void guide() {
		WebViewActivity.startWebViewActivity(getActivity(), getResources()
				.getString(R.string.driver_guide), Const.URL_GUIDE);
	}

	private void suggest() {
		getActivity().startActivity(
				new Intent(getActivity(), SuggestActivity.class));
	}

	private void set() {
		SetActivity.startSetActivity(getActivity());
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
				convertView = mInflater.inflate(R.layout.fragment_more_item,
						null);
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
