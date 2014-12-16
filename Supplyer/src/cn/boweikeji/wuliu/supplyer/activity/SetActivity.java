package cn.boweikeji.wuliu.supplyer.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.supplyer.activity.ChangePasswordActivity;
import cn.boweikeji.wuliu.supplyer.activity.SuggestActivity;
import cn.boweikeji.wuliu.supplyer.activity.WebViewActivity;
import cn.boweikeji.wuliu.supplyer.bean.UpdateInfo;
import cn.boweikeji.wuliu.supplyer.event.ExitEvent;
import cn.boweikeji.wuliu.supplyer.manager.LoginManager;
import cn.boweikeji.wuliu.supplyer.manager.UpdateManager;
import cn.boweikeji.wuliu.supplyer.Const;
import cn.boweikeji.wuliu.supplyer.R;
import cn.boweikeji.wuliu.utils.DeviceInfo;
import cn.boweikeji.wuliu.utils.Util;
import de.greenrobot.event.EventBus;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {

		}
	};

	private JsonHttpResponseHandler mResponseHandler = new JsonHttpResponseHandler() {

		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			requestResult(response);
		};

		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			requestResult(null);
		};
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
		mVersion.setText(DeviceInfo.getVersionName());
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
		WebViewActivity.startWebViewActivity(this,
				getResources().getString(R.string.title_send_guide),
				Const.URL_GUIDE);
	}

	private void suggest() {
		startActivity(new Intent(this, SuggestActivity.class));
	}

	private void about() {
		WebViewActivity
				.startWebViewActivity(this,
						getResources().getString(R.string.title_about),
						Const.URL_ABOUT);
	}

	private void update() {
		UpdateManager.checkUpdate(mResponseHandler);
	}

	private void changePasswd() {
		startActivity(new Intent(this, ChangePasswordActivity.class));
	}

	private void logout() {
		LoginManager.getInstance().logout();
		finish();
	}

	private void requestResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response: " + response.toString());
			try {
				int res = response.getInt("res");
				if (res == 2) {// 成功
					JSONObject infos = response.optJSONObject("infos");
					UpdateManager.saveUpdateInfo(infos);
					final UpdateInfo updateInfo = new UpdateInfo(infos);
					if (updateInfo.isNeedUpdate()) {
						showUpdateDialog(updateInfo);
					} else {
						Util.showTips(
								this,
								getResources().getString(
										R.string.lastest_version));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void showUpdateDialog(final UpdateInfo info) {
		UpdateManager.showUpdateDialog(this, info,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(info.getUrl()));
						startActivity(intent);
					}
				}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (info.isForce()) {
							finish();
							EventBus.getDefault().post(new ExitEvent());
						}
					}
				});
	}

	public static void startSetActivity(Context context) {
		context.startActivity(new Intent(context, SetActivity.class));
	}
}
