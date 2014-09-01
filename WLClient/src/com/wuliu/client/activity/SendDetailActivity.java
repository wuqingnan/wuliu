package com.wuliu.client.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.wuliu.client.Const;
import com.wuliu.client.R;
import com.wuliu.client.api.BaseParams;
import com.wuliu.client.bean.Order;
import com.wuliu.client.manager.LoginManager;
import com.wuliu.client.utils.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SendDetailActivity extends Activity {

	private static final String TAG = SendDetailActivity.class.getSimpleName();
	
	private static final String KEY_ORDER = "order";
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				finish();
			} else if (view == mSendRule) {
				showRule();
			} else if (view == mPublish) {
				publish();
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
	@InjectView(R.id.send_from)
	EditText mSendFrom;
	@InjectView(R.id.send_from_phone)
	EditText mSendFromPhone;
	@InjectView(R.id.send_to)
	EditText mSendTo;
	@InjectView(R.id.send_to_phone)
	EditText mSendToPhone;
	@InjectView(R.id.message_free)
	RadioGroup mMessageFree;
	@InjectView(R.id.send_comment)
	EditText mSendComment;
	@InjectView(R.id.publish)
	Button mPublish;
	@InjectView(R.id.send_accept)
	CheckBox mSendAccept;
	@InjectView(R.id.send_rule)
	TextView mSendRule;

	private ProgressDialog mProgressDialog;
	
	private Order mOrder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_detail);
		handleIntent();
		initView();
		initData();
	}
	
	private void handleIntent() {
		mOrder = (Order) getIntent().getSerializableExtra(KEY_ORDER);
	}

	private void initView() {
		ButterKnife.inject(this);
		mPublish.setOnClickListener(mOnClickListener);
		mSendRule.setOnClickListener(mOnClickListener);
		mTitle.setText(R.string.title_send);
		mMenuBtn.setImageResource(R.drawable.btn_title_back);
		mMenuBtn.setOnClickListener(mOnClickListener);
	}
	
	private void initData() {
		mMessageFree.check(R.id.message_free_yes);
	}

	private void showRule() {
		Intent intent = new Intent(this, RuleActivity.class);
		startActivity(intent);
	}
	
	private void publish() {
		if (checkValid()) {
			showProgressDialog();
			mOrder.setFromName(mSendFrom.getText().toString());
			mOrder.setFromPhone(mSendFromPhone.getText().toString());
			mOrder.setToName(mSendTo.getText().toString());
			mOrder.setToPhone(mSendToPhone.getText().toString());
			mOrder.setRemarks(mSendComment.getText().toString());
			mOrder.setFree(mMessageFree.getCheckedRadioButtonId() == R.id.message_free_yes ? 1 : 0);
			
			AsyncHttpClient client = new AsyncHttpClient();
			client.setURLEncodingEnabled(true);
			
			BaseParams params = mOrder.getPublishParams();
			params.add("method", "sendGoodInfos");
			params.add("supplyer_cd", LoginManager.getInstance().getUserInfo().getSupplyer_cd());
			
			Log.d(TAG, "URL: " + AsyncHttpClient.getUrlWithQueryString(false, Const.URL_SEND_GOODS, params));
			client.get(Const.URL_SEND_GOODS, params, mRequestHandler);
		}
	}

	private boolean checkValid() {
		String sendFrom = mSendFrom.getText().toString();
		String sendFromPhone = mSendFromPhone.getText().toString();
		String sendTo = mSendTo.getText().toString();
		String sendToPhone = mSendToPhone.getText().toString();
		
		if (sendFrom == null || sendFrom.equals("")) {
			Util.showTips(this, getResources().getString(
					R.string.send_from_empty));
			return false;
		} else if (sendFromPhone == null || sendFromPhone.equals("")) {
			Util.showTips(this, getResources().getString(
					R.string.send_phone_empty));
			return false;
		} else if (sendTo == null || sendTo.equals("")) {
			Util.showTips(this, getResources().getString(
					R.string.send_to_empty));
			return false;
		} else if (sendToPhone == null || sendToPhone.equals("")) {
			Util.showTips(this, getResources().getString(
					R.string.send_phone_empty));
			return false;
		} else if (!Util.isPhoneNumber(sendFromPhone)) {
			Util.showTips(this, getResources().getString(
					R.string.send_phone_error));
			return false;
		} else if (!Util.isPhoneNumber(sendToPhone)) {
			Util.showTips(this, getResources().getString(
					R.string.send_phone_error));
			return false;
		}
		return true;
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
	
	private void showPublishSuccessDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(R.string.publish_success)
				.setMessage(R.string.publish_order_message)
				.setCancelable(false)
				.setPositiveButton(R.string.continue_send, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						startActivity(new Intent(SendDetailActivity.this, MainActivity.class));
					}
				})
				.setNegativeButton(R.string.know, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						startActivity(new Intent(SendDetailActivity.this, MainActivity.class));
					}
				})
				.create();
		dialog.show();
	}
	
	private void requestResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			try {
				int res = response.getInt("res");
				String msg = response.getString("msg");
				Util.showTips(this, msg);
				if (res == 2) {//成功
					showPublishSuccessDialog();
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Util.showTips(this, getString(R.string.publish_failed));
	}
	
	public static void startSendDetailActivity(Context context, Order order) {
		Intent intent = new Intent(context, SendDetailActivity.class);
		intent.putExtra(KEY_ORDER, order);
		context.startActivity(intent);
	}
}
