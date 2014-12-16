package cn.boweikeji.wuliu.http;

import android.util.Log;



import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AsyncHttp {

	private static final String TAG = AsyncHttp.class.getSimpleName();

	private static final String BASE_URL = "http://interface.boweikeji.cn/";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		Log.d(TAG,
				"URL: "
						+ AsyncHttpClient.getUrlWithQueryString(true,
								getAbsoluteUrl(url), params));
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		Log.d(TAG,
				"URL: "
						+ AsyncHttpClient.getUrlWithQueryString(true,
								getAbsoluteUrl(url), params));
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	public static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}
}
