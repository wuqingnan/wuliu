package cn.boweikeji.wuliu.supplyer.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.supplyer.Const;
import cn.boweikeji.wuliu.supplyer.api.BaseParams;
import cn.boweikeji.wuliu.supplyer.manager.LoginManager;
import cn.boweikeji.wuliu.supplyer.utils.Util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import cn.boweikeji.wuliu.supplyer.R;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SuggestActivity extends BaseActivity {
	
	private static final String TAG = SuggestActivity.class.getSimpleName();
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				finish();
			} else if (view == mSubmit) {
				submit();
			}
		}
	};
	
	private JsonHttpResponseHandler mRequestHandler = new JsonHttpResponseHandler() {
		
		public void onFinish() {
			hideProgressDialog();
		};
		
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			requestResult(response);
		};
		
		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
			requestResult(null);
		};
	};
	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.suggestion)
	EditText mSuggestion;
	@InjectView(R.id.submit)
	Button mSubmit;
	
	private ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggest);
		initView();
	}
	
	private void initView() {
		ButterKnife.inject(this);
		mTitle.setText(R.string.title_suggest);
		mMenuBtn.setOnClickListener(mOnClickListener);
		mSubmit.setOnClickListener(mOnClickListener);
	}
	
	/**
	 * �ύ
	 */
	private void submit() {
		if (validCheck()) {
			showProgressDialog();
			
			String content = mSuggestion.getText().toString();
			
			AsyncHttpClient client = new AsyncHttpClient();
			client.setURLEncodingEnabled(true);
			
			BaseParams params = new BaseParams();
			params.add("method", "supplyerSuggest");
			params.add("supplyer_cd", LoginManager.getInstance().getUserInfo().getSupplyer_cd());
			params.add("passwd", LoginManager.getInstance().getUserInfo().getPasswd());
			params.add("content", content);
			
			Log.d(TAG, "URL: " + AsyncHttpClient.getUrlWithQueryString(true, Const.URL_SUGGEST, params));
			client.get(Const.URL_SUGGEST, params, mRequestHandler);
		}
	}
	
	/**
	 * �����Ϣ�Ϸ���
	 * 
	 * @return
	 */
	private boolean validCheck() {
		boolean bRes = true;
		String content = mSuggestion.getText().toString();
		if (content == null || content.trim().length() == 0) {
			Util.showTips(this, getString(
					R.string.suggestion_empty));
			bRes = false;
		}
		return bRes;
	}
	
	private void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage(getString(R.string.requesting));
			mProgressDialog.setCancelable(false);
		}
		mProgressDialog.show();
	}
	
	private void hideProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
		mProgressDialog = null;
	}
	
	private void requestResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response: " + response.toString());
			try {
				int res = response.getInt("res");
				String msg = response.getString("msg");
				Util.showTips(this, msg);
				if (res == 2) {//�ɹ�
					mSuggestion.setText(null);
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Util.showTips(this, getString(R.string.request_failed));
	}
}
