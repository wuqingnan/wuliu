package cn.boweikeji.wuliu.supplyer.utils;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cn.boweikeji.wuliu.supplyer.Const;
import cn.boweikeji.wuliu.supplyer.api.BaseParams;
import cn.boweikeji.wuliu.supplyer.event.UpdateEvent;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import cn.boweikeji.wuliu.supplyer.R;

import de.greenrobot.event.EventBus;
import android.content.Context;
import android.util.Log;

public class UpdateUtil {

	private static final String TAG = UpdateUtil.class.getSimpleName();
	
	private static boolean sChecking = false;
	
	public static void checkUpdate() {
		if (sChecking) {
			return;
		}
		sChecking = true;
		String appVersion = DeviceInfo.getAppVersion();
		
		AsyncHttpClient client = new AsyncHttpClient();
		client.setURLEncodingEnabled(true);
		
		BaseParams params = new BaseParams();
		params.add("method", "updateAPP");
		params.add("app_name", "ANDSUP");
		params.add("now_version", appVersion);
		
		Log.d(TAG, "URL: " + AsyncHttpClient.getUrlWithQueryString(true, Const.URL_UPDATE, params));
		client.get(Const.URL_UPDATE, params, new JsonHttpResponseHandler() {
			
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				requestResult(response);
			};
			
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				requestResult(null);
			};
		});
	}
	
	private static void requestResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response: " + response.toString());
			try {
				int res = response.getInt("res");
				if (res == 2) {//成功
					int state = response.getInt("state");
					UpdateEvent event = new UpdateEvent();
					switch (state) {
					case 1://不需要升级
						event.setNeedUpdate(false);
						break;
					case 2://需要升级（非强制）
					case 3://需要升级（强制）
						event.setNeedUpdate(true);
						event.setForce(state == 3);
						JSONObject info = response.optJSONObject("info");
						if (info != null) {
							event.setVersion(info.optString("version_cd"));
							event.setUrl(info.optString("url"));
							event.setContent(info.optString("content"));
						}
						break;
					}
					EventBus.getDefault().post(event);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		sChecking = false;
	}
}
