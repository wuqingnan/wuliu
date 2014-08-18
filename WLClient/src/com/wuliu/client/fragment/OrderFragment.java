package com.wuliu.client.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.wuliu.client.R;
import com.wuliu.client.activity.MainActivity;

public class OrderFragment extends BaseFragment {

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				if (getActivity() instanceof MainActivity) {
					((MainActivity) getActivity())
							.onClickTitle(OrderFragment.this);
				}
			}
		}
	};

	private View mRootView;

	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;

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
		mTitle.setText(R.string.title_order);
		mMenuBtn.setImageResource(R.drawable.btn_title_back);
		mMenuBtn.setOnClickListener(mOnClickListener);
	}

	private void initView() {

	}
}
