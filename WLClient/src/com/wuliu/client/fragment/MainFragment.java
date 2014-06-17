package com.wuliu.client.fragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.wuliu.client.R;
import com.wuliu.client.activity.MainActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MainFragment extends Fragment {

	private static final String TAG = MainFragment.class.getSimpleName();
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (getActivity() instanceof MainActivity) {
				((MainActivity)getActivity()).onTitleClick(view);
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
		mRootView = inflater.inflate(R.layout.fragment_main, null);
		return mRootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}
	
	private void init() {
		ButterKnife.inject(this, mRootView);
		mTitle.setText(R.string.app_name);
		mMenuBtn.setImageResource(R.drawable.btn_title_menu);
		mMenuBtn.setOnClickListener(mOnClickListener);
	}
	
}
