package com.wuliu.client.supplyer.manager;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.wuliu.client.supplyer.Const;
import com.wuliu.client.supplyer.R;
import com.wuliu.client.supplyer.api.BaseParams;
import com.wuliu.client.supplyer.utils.DeviceInfo;

import android.content.Context;
import android.util.Log;

public class UpdateManager {

	private static final String TAG = UpdateManager.class.getSimpleName();
	
	private JsonHttpResponseHandler mRequestHandler = new JsonHttpResponseHandler() {
		
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			requestResult(response);
		};
		
		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
			requestResult(null);
		};
	};
	
	private static UpdateManager mInstance;

	private boolean mNeedUpdate;
	private boolean mForce;
	private String mVersion;
	private String mUrl;
	private String mContent;
	
	private UpdateManager() {
		mNeedUpdate = false;
	}
	
	public static UpdateManager getInstance() {
		if (mInstance == null) {
			mInstance = new UpdateManager();
		}
		return mInstance;
	}
	
	public void checkUpdate(Context context) {
		String app_name = context.getResources().getString(R.string.app_name);
		String appVersion = DeviceInfo.getAppVersion();
		
		AsyncHttpClient client = new AsyncHttpClient();
		client.setURLEncodingEnabled(true);
		
		BaseParams params = new BaseParams();
		params.add("method", "updateAPP");
		params.add("app_name", app_name);
		params.add("now_version", appVersion);
		
		Log.d(TAG, "URL: " + AsyncHttpClient.getUrlWithQueryString(true, Const.URL_UPDATE, params));
		client.get(Const.URL_UPDATE, params, mRequestHandler);
	}
	
	private void requestResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response: " + response.toString());
			try {
				int res = response.getInt("res");
				if (res == 2) {//成功
					int state = response.getInt("state");
					switch (state) {
					case 1://没有新版本
						mNeedUpdate = false;
						break;
					case 2://有新版本，非强制
					case 3://有新版本，强制
						mNeedUpdate = true;
						mForce = state == 3;
						JSONObject info = response.optJSONObject("info");
						if (info != null) {
							mVersion = info.optString("version_cd");
							mUrl = info.optString("url");
							mContent = info.optString("content");
						}
						break;
					}
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
