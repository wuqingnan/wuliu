package com.wuliu.client.supplyer.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.wuliu.client.supplyer.Const;
import com.wuliu.client.supplyer.R;
import com.wuliu.client.supplyer.api.BaseParams;
import com.wuliu.client.supplyer.bean.Order;
import com.wuliu.client.supplyer.manager.LoginManager;
import com.wuliu.client.supplyer.utils.Util;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

public class OrderChangeActivity extends Activity {
	
	private static final String TAG = OrderChangeActivity.class.getSimpleName();
	
	private static final String KEY_ORDER = "order";
	
	private static final int REQUEST_CODE_SEARCH = 0x010;
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				finish();
			} else if (view == mSwapAddress) {
				swapAddress();
			} else if (view == mPublish) {
				publish();
			} else if (view == mAddressFrom) {
				searchAddress(true);
			} else if (view == mAddressTo) {
				searchAddress(false);
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
	@InjectView(R.id.goods_pay)
	EditText mGoodsPay;
	@InjectView(R.id.send_comment)
	EditText mSendComment;
	@InjectView(R.id.address_from)
	TextView mAddressFrom;
	@InjectView(R.id.address_to)
	TextView mAddressTo;
	@InjectView(R.id.swap_address)
	ImageView mSwapAddress;
	@InjectView(R.id.publish)
	Button mPublish;
	
	private ProgressDialog mProgressDialog;
	
	//0:省、1:市、2:区县、3:街道
	private String[] mFromInfos = new String[4];
	private String[] mToInfos = new String[4];
	private Order mOrder;
	
	private boolean mIsFrom;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_change);
		handleIntent();
		initView();
		initData();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_SEARCH) {
				if (data != null) {
					String[] infos = data.getStringArrayExtra(SearchActivity.KEY_RESULT);
					String address = "";
					if (infos != null) {
						for (int i = 0; i < infos.length; i++) {
							if (infos[i] != null) {
								address += infos[i] + " ";
							}
						}
					}
					if (mIsFrom) {
						mFromInfos = infos;
						mAddressFrom.setText(address);
					} else {
						mToInfos = infos;
						mAddressTo.setText(address);
					}
				}
			}
		}
	}
	
	private void handleIntent() {
		mOrder = (Order) getIntent().getSerializableExtra(KEY_ORDER);
	}
	
	private void initView() {
		ButterKnife.inject(this);
		mSwapAddress.setOnClickListener(mOnClickListener);
		mPublish.setOnClickListener(mOnClickListener);
		mMenuBtn.setImageResource(R.drawable.btn_title_back);
		mMenuBtn.setOnClickListener(mOnClickListener);
		mAddressFrom.setOnClickListener(mOnClickListener);
		mAddressTo.setOnClickListener(mOnClickListener);
		mTitle.setText(R.string.title_change_order);
	}
	
	private void initData() {
		if (mOrder != null) {
			mGoodsPay.setText(mOrder.getPay() + "");
			mAddressFrom.setText(mOrder.getFromAddress());
			mAddressTo.setText(mOrder.getToAddress());
			
			int free = mOrder.getFree();
			if (free == 1) {
				mMessageFree.check(R.id.message_free_yes);
			} else {
				mMessageFree.check(R.id.message_free_no);
			}
			mSendFrom.setText(mOrder.getFromName());
			mSendFromPhone.setText(mOrder.getFromPhone());
			mSendTo.setText(mOrder.getToName());
			mSendToPhone.setText(mOrder.getToPhone());
			String remarks = mOrder.getRemarks();
			if (remarks != null && !remarks.equals(BaseParams.PARAM_DEFAULT)) {
				mSendComment.setText(remarks);
			}
		}
	}
	
	private void swapAddress() {
		CharSequence temp = mAddressFrom.getText();
		mAddressFrom.setText(mAddressTo.getText());
		mAddressTo.setText(temp);
	}

	private void searchAddress(boolean isFrom) {
		mIsFrom = isFrom;
		SearchActivity.startSearchActivity(this, REQUEST_CODE_SEARCH, isFrom ? mFromInfos : mToInfos);
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
			mOrder.setPay(Integer.parseInt(mGoodsPay.getText().toString()));
			mOrder.setToAddress(mAddressTo.getText().toString());
			mOrder.setFromAddress(mAddressFrom.getText().toString());
			
			AsyncHttpClient client = new AsyncHttpClient();
			client.setURLEncodingEnabled(true);
			
			BaseParams params = mOrder.getChangeParams();
			params.add("method", "changeCos");
			params.add("supplyer_cd", LoginManager.getInstance().getUserInfo().getSupplyer_cd());
			
			Log.d(TAG, "URL: " + AsyncHttpClient.getUrlWithQueryString(false, Const.URL_CHANGE_ORDER, params));
			client.get(Const.URL_CHANGE_ORDER, params, mRequestHandler);
		}
	}
	
	private boolean checkValid() {
		String sendFrom = mSendFrom.getText().toString();
		String sendFromPhone = mSendFromPhone.getText().toString();
		String sendTo = mSendTo.getText().toString();
		String sendToPhone = mSendToPhone.getText().toString();
		String goodsPay = mGoodsPay.getText().toString();
		String addressFrom = mAddressFrom.getText().toString();
		String addressTo = mAddressTo.getText().toString();
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
		} else if (goodsPay == null || goodsPay.equals("")) {
			Util.showTips(this, getResources().getString(
					R.string.goods_pay_empty));
			return false;
		} else if (addressFrom == null || addressFrom.equals("")) {
			Util.showTips(this, getResources().getString(
					R.string.address_from_empty));
			return false;
		} else if (addressTo == null || addressTo.equals("")) {
			Util.showTips(this, getResources().getString(
					R.string.address_to_empty));
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
	
	private void showChangeSuccessDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
		.setTitle(R.string.change_success)
		.setMessage(R.string.change_order_message)
		.setCancelable(false)
		.setNegativeButton(R.string.know, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				startActivity(new Intent(OrderChangeActivity.this, MainActivity.class));
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
					showChangeSuccessDialog();
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Util.showTips(this, getString(R.string.publish_failed));
	}
	
	/**
	 * 打开修改订单页面
	 * @param context
	 * @param bespeak
	 */
	public static void startOrderChangeActivity(Context context, Order order) {
		Intent intent = new Intent(context, OrderChangeActivity.class);
		intent.putExtra(KEY_ORDER, order);
		context.startActivity(intent);
	}
}
