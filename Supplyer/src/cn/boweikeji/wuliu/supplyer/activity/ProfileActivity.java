package cn.boweikeji.wuliu.supplyer.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.http.AsyncHttp;
import cn.boweikeji.wuliu.supplyer.adapter.ProfileAdapter;
import cn.boweikeji.wuliu.supplyer.api.BaseParams;
import cn.boweikeji.wuliu.supplyer.bean.UserInfo;
import cn.boweikeji.wuliu.supplyer.manager.LoginManager;
import cn.boweikeji.wuliu.supplyer.Const;
import cn.boweikeji.wuliu.supplyer.R;
import cn.boweikeji.wuliu.utils.Util;

public class ProfileActivity extends BaseActivity {

	private static final String TAG = ProfileActivity.class.getSimpleName();
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mBack) {
				finish();
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
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.profile)
	ListView mListView;

	private View mHeader;
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
		mBack.setOnClickListener(mOnClickListener);
		initHeader();
		initList();
	}

	private void initHeader() {
		mHeader = getLayoutInflater().inflate(R.layout.menu_header,
				null);
		mPhone = (TextView) mHeader.findViewById(R.id.phone);
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
		params.add("method", "getSupplyInfos");
		params.add("supplyer_cd", info.getSupplyer_cd());
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

	public static void startProfileActivity(Context context) {
		context.startActivity(new Intent(context, ProfileActivity.class));
	}
}
