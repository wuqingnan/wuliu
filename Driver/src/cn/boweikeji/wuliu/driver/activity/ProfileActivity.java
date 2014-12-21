package cn.boweikeji.wuliu.driver.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.Const;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.adapter.ProfileAdapter;
import cn.boweikeji.wuliu.driver.api.BaseParams;
import cn.boweikeji.wuliu.driver.bean.UserInfo;
import cn.boweikeji.wuliu.driver.manager.LoginManager;
import cn.boweikeji.wuliu.http.AsyncHttp;
import cn.boweikeji.wuliu.utils.Util;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class ProfileActivity extends BaseActivity {

	private static final String TAG = ProfileActivity.class.getSimpleName();

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mBack) {
				finish();
			} else if (view == mChange) {
				changeProfile();
			}
		}
	};
	
	private JsonHttpResponseHandler mRequestHandler = new JsonHttpResponseHandler() {
		
		public void onFinish() {
			
		};
		
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			requestResult(response);
		};
		
		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
			requestResult(null);
		};
	};

	@InjectView(R.id.titlebar_leftBtn)
	ImageView mBack;
	@InjectView(R.id.titlebar_rightTxt)
	TextView mChange;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.profile)
	ListView mListView;

	private View mHeader;
	private TextView mName;
	private TextView mPhone;

	private ProfileAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		initView();
		initData();
	}

	private void initView() {
		ButterKnife.inject(this);
		mTitle.setText(R.string.title_profile);
		mChange.setText(R.string.change);
		mChange.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(mOnClickListener);
		mChange.setOnClickListener(mOnClickListener);
		initHeader();
		initList();
	}

	private void initHeader() {
		mHeader = getLayoutInflater().inflate(R.layout.fragment_more_header,
				null);
		mName = (TextView) mHeader.findViewById(R.id.name);
		mPhone = (TextView) mHeader.findViewById(R.id.phone);
		mName.setVisibility(View.GONE);
		mPhone.setVisibility(View.GONE);
		mHeader.setBackgroundColor(getResources().getColor(R.color.bg_back));
	}

	private void initList() {
		mListView.addHeaderView(mHeader);
		mAdapter = new ProfileAdapter(this);
		mListView.setAdapter(mAdapter);
	}
	
	private void initData() {
		UserInfo info = LoginManager.getInstance().getUserInfo();
		BaseParams params = new BaseParams();
		params.add("method", "getDriverInfos");
		params.add("driver_cd", info.getDriver_cd());
		params.add("passwd", info.getPasswd());
		AsyncHttp.get(Const.URL_PROFILE, params, mRequestHandler);
	}
	
	private void requestResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response: " + response.toString());
			try {
				int res = response.getInt("res");
				String msg = response.getString("msg");
				if (res == 2) {//成功
					UserInfo userInfo = LoginManager.getInstance().getUserInfo();
					userInfo.update(response.optJSONObject("infos"));
					LoginManager.getInstance().setUserInfo(userInfo);
					mAdapter.notifyDataSetChanged();
				} else {
					Util.showTips(this, msg);
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Util.showTips(this, getString(R.string.request_failed));
	}
	
	private void changeProfile() {
		ChangeProfile1Activity.startChangeProfile1Activity(this);
	}
	
	public static void startProfileActivity(Context context) {
		context.startActivity(new Intent(context, ProfileActivity.class));
	}
}
