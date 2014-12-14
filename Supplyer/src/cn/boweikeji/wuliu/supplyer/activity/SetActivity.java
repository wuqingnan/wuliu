package cn.boweikeji.wuliu.supplyer.activity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.supplyer.activity.ChangePasswordActivity;
import cn.boweikeji.wuliu.supplyer.activity.SuggestActivity;
import cn.boweikeji.wuliu.supplyer.activity.WebViewActivity;
import cn.boweikeji.wuliu.supplyer.event.UpdateEvent;
import cn.boweikeji.wuliu.supplyer.manager.LoginManager;
import cn.boweikeji.wuliu.supplyer.utils.DeviceInfo;
import cn.boweikeji.wuliu.supplyer.utils.UpdateUtil;
import cn.boweikeji.wuliu.supplyer.Const;
import cn.boweikeji.wuliu.supplyer.R;
import de.greenrobot.event.EventBus;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SetActivity extends BaseActivity {

	private static final String TAG = SetActivity.class.getSimpleName();

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				finish();
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);
		init();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this);
		}
	}
	
	private void init() {
		ButterKnife.inject(this);
		initTitle();
		initData();
		initView();
	}

	private void initTitle() {
		mTitle.setText(R.string.title_set);
		mMenuBtn.setImageResource(R.drawable.ic_navi_back);
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
		WebViewActivity.startWebViewActivity(this, getResources().getString(R.string.title_send_guide), Const.URL_GUIDE);
	}
	
	private void suggest() {
		startActivity(new Intent(this, SuggestActivity.class));
	}
	
	private void about() {
		WebViewActivity.startWebViewActivity(this, getResources().getString(R.string.title_about), Const.URL_ABOUT);
	}
	
	private void update() {
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}
		UpdateUtil.checkUpdate();
	}
	
	private void changePasswd() {
		startActivity(new Intent(this, ChangePasswordActivity.class));
	}
	
	private void logout() {
		LoginManager.getInstance().logout();
		finish();
	}
	
	public void onEventMainThread(UpdateEvent event) {
//		((MainActivity)getActivity()).showUpdateDialog(event);
//		if (event == null || !event.isNeedUpdate()) {
//			Util.showTips(getActivity(), getString(R.string.latest_version));
//		}
	}
	
	public static void startSetActivity(Context context) {
		context.startActivity(new Intent(context, SetActivity.class));
	}
}
