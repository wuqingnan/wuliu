package com.wuliu.client.supplyer.fragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.wuliu.client.supplyer.R;
import com.wuliu.client.supplyer.activity.MainActivity;
import com.wuliu.client.supplyer.activity.SendActivity;
import com.wuliu.client.supplyer.manager.LoginManager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainFragment extends BaseFragment {

	private static final String TAG = MainFragment.class.getSimpleName();
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				if (getActivity() instanceof MainActivity) {
					((MainActivity)getActivity()).showMenu();
				}
			}
			else if (view == mMainSend) {
				if (LoginManager.getInstance().hasLogin()) {
					SendActivity.startSendActivity(getActivity(), false);
				} else {
					((MainActivity)getActivity()).changeFragment(new LoginFragment());
				}
			}
			else if (view == mMainBook) {
				if (LoginManager.getInstance().hasLogin()) {
					SendActivity.startSendActivity(getActivity(), true);
				} else {
					((MainActivity)getActivity()).changeFragment(new LoginFragment());
				}
			}
		}
	};
	
	private View mRootView;
	
	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.main_send)
	Button mMainSend;
	@InjectView(R.id.main_book)
	Button mMainBook;
	
	private boolean mSpeakMode;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "MainFragment.onCreateView");
		mRootView = inflater.inflate(R.layout.fragment_main, null);
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
		Log.d(TAG, "MainFragment.onDestroyView");
	}
	
	private void init() {
		ButterKnife.inject(this, mRootView);
		mSpeakMode = true;
		initTitle();
		initBottom();
	}
	
	private void initTitle() {
		mTitle.setText(R.string.title_main);
		mMenuBtn.setImageResource(R.drawable.btn_title_menu);
		mMenuBtn.setOnClickListener(mOnClickListener);
	}
	
	private void initBottom() {
		mMainSend.setOnClickListener(mOnClickListener);
		mMainBook.setOnClickListener(mOnClickListener);
	}
	
}
