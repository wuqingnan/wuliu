package cn.boweikeji.wuliu.driver.fragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.activity.LoginActivity;
import cn.boweikeji.wuliu.driver.event.LoginEvent;
import cn.boweikeji.wuliu.driver.event.LogoutEvent;
import cn.boweikeji.wuliu.driver.manager.LoginManager;
import de.greenrobot.event.EventBus;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeFragment extends BaseFragment {

	private static final String TAG = HomeFragment.class.getSimpleName();
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mLogin) {
				LoginActivity.startLoginActivity(getActivity());
			}
		}
	};
	
	private View mRootView;
	
	@InjectView(R.id.titlebar_leftBtn)
	ImageView mBack;
	@InjectView(R.id.titlebar_rightTxt)
	Button mRegister;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.titlebar_rightTxt)
	Button mLogin;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_home, null);
		return mRootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getDefault().unregister(this);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	private void init() {
		ButterKnife.inject(this, mRootView);
		initTitle();
		EventBus.getDefault().register(this);
	}
	
	private void initTitle() {
		mTitle.setText(R.string.title_home);
		mBack.setVisibility(View.GONE);
		mLogin.setVisibility(View.VISIBLE);
		mLogin.setText(R.string.login);
		mLogin.setOnClickListener(mOnClickListener);
		updateLoginBtn();
	}
	
	private void updateLoginBtn() {
		if (LoginManager.getInstance().hasLogin()) {
			mLogin.setVisibility(View.GONE);
		}
	}
	
	public void onEventMainThread(LoginEvent event) {
		updateLoginBtn();
	}
	
	public void onEventMainThread(LogoutEvent event) {
		updateLoginBtn();
	}
}
