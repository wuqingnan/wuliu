package cn.boweikeji.wuliu.driver.fragment;

import com.baidu.location.BDLocation;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.WLApplication;
import cn.boweikeji.wuliu.driver.activity.LoginActivity;
import cn.boweikeji.wuliu.driver.activity.MainActivity;
import cn.boweikeji.wuliu.driver.event.LoginEvent;
import cn.boweikeji.wuliu.driver.event.LogoutEvent;
import cn.boweikeji.wuliu.driver.manager.LoginManager;
import de.greenrobot.event.EventBus;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeFragment extends BaseFragment {

	private static final String TAG = HomeFragment.class.getSimpleName();
	
	private final int DELAY_MILLIS = 1000 * 10;
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mLogin) {
				LoginActivity.startLoginActivity(getActivity());
			}
		}
	};
	
	private Runnable mMyPosRunnable = new Runnable() {
		@Override
		public void run() {
			updateMyInfo();
		}
	};
	
	private View mRootView;
	
	@InjectView(R.id.titlebar_leftBtn)
	ImageView mBack;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.titlebar_rightTxt)
	TextView mLogin;
	@InjectView(R.id.my_pos_info)
	TextView mMyPosInfo;
	
	private Handler mHandler;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_home, null);
		return mRootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mHandler.removeCallbacks(mMyPosRunnable);
		mHandler = null;
		EventBus.getDefault().unregister(this);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "shizy---onResume");
		((MainActivity)getActivity()).updateMap();
	}

	private void init() {
		ButterKnife.inject(this, mRootView);
		mHandler = new Handler();
		initTitle();
		mHandler.postDelayed(mMyPosRunnable, 1000);
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
	
	public void updateMyInfo() {
		BDLocation loc = WLApplication.getLocationClient()
				.getLastKnownLocation();
		if (loc != null && loc.getAddrStr() != null) {
			mMyPosInfo.setText(loc.getAddrStr());
			mMyPosInfo.setVisibility(View.VISIBLE);
		}
		mHandler.postDelayed(mMyPosRunnable, DELAY_MILLIS);
	}
	
	public void onEventMainThread(LoginEvent event) {
		updateLoginBtn();
	}
	
	public void onEventMainThread(LogoutEvent event) {
		updateLoginBtn();
	}
}
