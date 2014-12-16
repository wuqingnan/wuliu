package cn.boweikeji.wuliu.driver.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;

import butterknife.ButterKnife;
import butterknife.InjectView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.boweikeji.wuliu.driver.Const;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.bean.UpdateInfo;
import cn.boweikeji.wuliu.driver.event.ExitEvent;
import cn.boweikeji.wuliu.driver.manager.LoginManager;
import cn.boweikeji.wuliu.driver.manager.UpdateManager;
import cn.boweikeji.wuliu.utils.DeviceInfo;
import cn.boweikeji.wuliu.utils.Util;
import de.greenrobot.event.EventBus;

public class SetActivity extends BaseActivity {

	private static final String TAG = SetActivity.class.getSimpleName();
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mBack) {
				finish();
			} else if (view == mLogout) {
				logout();
			} else if (view == mUpdate) {
				update();
			} else if (view == mAbout) {
				about();
			}
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
	ImageView mBack;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.push_switch)
	Button mUserName;
	@InjectView(R.id.update)
	LinearLayout mUpdate;
	@InjectView(R.id.version)
	TextView mVersion;
	@InjectView(R.id.about)
	TextView mAbout;
	@InjectView(R.id.logout)
	Button mLogout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);
		init();
	}

	private void init() {
		ButterKnife.inject(this);
		initTitle();
		initView();
	}

	private void initTitle() {
		mTitle.setText(R.string.setting);
		mBack.setImageResource(R.drawable.ic_navi_back);
		mBack.setOnClickListener(mOnClickListener);
	}

	private void initView() {
		mVersion.setText(DeviceInfo.getVersionName());
		mUpdate.setOnClickListener(mOnClickListener);
		mAbout.setOnClickListener(mOnClickListener);
		mLogout.setOnClickListener(mOnClickListener);
	}

	private void about() {
		WebViewActivity.startWebViewActivity(this,
				getResources().getString(R.string.about), Const.URL_ABOUT);
	}
	
	private void update() {
		UpdateManager.checkUpdate(mResponseHandler);
	}

	private void logout() {
		LoginManager.getInstance().logout();
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(MainActivity.KEY_LOGOUT, true);
		startActivity(intent);
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
