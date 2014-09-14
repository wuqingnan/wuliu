package com.wuliu.client.supplyer.fragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.wuliu.client.supplyer.R;
import com.wuliu.client.supplyer.activity.ChangePasswordActivity;
import com.wuliu.client.supplyer.activity.MainActivity;
import com.wuliu.client.supplyer.activity.WebViewActivity;
import com.wuliu.client.supplyer.manager.LoginManager;
import com.wuliu.client.supplyer.utils.DeviceInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SetFragment extends BaseFragment {

	private static final String TAG = SetFragment.class.getSimpleName();

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				if (getActivity() instanceof MainActivity) {
					((MainActivity) getActivity()).back();
				}
			} else if (view == mGuide) {
				guide();
			} else if (view == mSuggest) {
				suggest();
			} else if (view == mAbout) {
				about();
			} else if (view == mUpdate) {
				update();
			} else if (view == mChangePasswd) {
				changePasswd();
			} else if (view == mLogout) {
				logout();
			}
		}
	};

	private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			
		}
	};
	
	private View mRootView;

	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.set_voice)
	ToggleButton mVoice;
	@InjectView(R.id.set_guide)
	TextView mGuide;
	@InjectView(R.id.set_suggest)
	TextView mSuggest;
	@InjectView(R.id.set_about)
	TextView mAbout;
	@InjectView(R.id.set_update)
	RelativeLayout mUpdate;
	@InjectView(R.id.set_version)
	TextView mVersion;
	@InjectView(R.id.set_change_passwd)
	TextView mChangePasswd;
	@InjectView(R.id.set_logout)
	Button mLogout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_set, null);
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
		initData();
		initView();
	}

	private void initTitle() {
		mTitle.setText(R.string.title_set);
		mMenuBtn.setImageResource(R.drawable.btn_title_back);
		mMenuBtn.setOnClickListener(mOnClickListener);
	}

	private void initData() {
		mVersion.setText(DeviceInfo.getAppVersion());
	}
	
	private void initView() {
		mVoice.setOnCheckedChangeListener(mOnCheckedChangeListener);
		mGuide.setOnClickListener(mOnClickListener);
		mSuggest.setOnClickListener(mOnClickListener);
		mAbout.setOnClickListener(mOnClickListener);
		mUpdate.setOnClickListener(mOnClickListener);
		mChangePasswd.setOnClickListener(mOnClickListener);
		mLogout.setOnClickListener(mOnClickListener);
	}
	
	private void guide() {
		WebViewActivity.startWebViewActivity(getActivity(), getResources().getString(R.string.title_send_guide), WebViewActivity.URL_GUIDE);
	}
	
	private void suggest() {
		
	}
	
	private void about() {
		WebViewActivity.startWebViewActivity(getActivity(), getResources().getString(R.string.title_about_app), WebViewActivity.URL_ABOUT);
	}
	
	private void update() {
		
	}
	
	private void changePasswd() {
		getActivity().startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
	}
	
	private void logout() {
		LoginManager.getInstance().logout();
		((MainActivity) getActivity()).back();
	}
}
