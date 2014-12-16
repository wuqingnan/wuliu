package cn.boweikeji.wuliu.supplyer.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.http.AsyncHttp;
import cn.boweikeji.wuliu.supplyer.Const;
import cn.boweikeji.wuliu.supplyer.api.BaseParams;
import cn.boweikeji.wuliu.supplyer.bean.Order;
import cn.boweikeji.wuliu.supplyer.manager.LoginManager;






import com.loopj.android.http.JsonHttpResponseHandler;

import cn.boweikeji.wuliu.supplyer.R;
import cn.boweikeji.wuliu.utils.Util;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

public class OrderChangeActivity extends BaseActivity {
	
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
			} else if (view == mFromAddress) {
				searchAddress(true);
			} else if (view == mToAddress) {
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
	@InjectView(R.id.from_name)
	EditText mFromName;
	@InjectView(R.id.from_phone)
	EditText mFromPhone;
	@InjectView(R.id.to_name)
	EditText mToName;
	@InjectView(R.id.to_phone)
	EditText mToPhone;
	@InjectView(R.id.message_free)
	RadioGroup mMessageFree;
	@InjectView(R.id.goods_pay)
	EditText mGoodsPay;
	@InjectView(R.id.send_comment)
	EditText mSendComment;
	@InjectView(R.id.from_address)
	TextView mFromAddress;
	@InjectView(R.id.to_address)
	TextView mToAddress;
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
						mFromAddress.setText(address);
					} else {
						mToInfos = infos;
						mToAddress.setText(address);
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
		mMenuBtn.setImageResource(R.drawable.ic_navi_back);
		mMenuBtn.setOnClickListener(mOnClickListener);
		mFromAddress.setOnClickListener(mOnClickListener);
		mToAddress.setOnClickListener(mOnClickListener);
		mTitle.setText(R.string.title_change_order);
	}
	
	private void initData() {
		if (mOrder != null) {
			mGoodsPay.setText(mOrder.getGoods_cost() > 0 ? mOrder.getGoods_cost() + "" : "");
			mFromAddress.setText(mOrder.getStart_addr());
			mToAddress.setText(mOrder.getEnd_addr());
			
			int free = mOrder.getMess_fee();
			if (free == 1) {
				mMessageFree.check(R.id.message_free_yes);
			} else {
				mMessageFree.check(R.id.message_free_no);
			}
			mFromName.setText(mOrder.getSupplyer_name().equals(Const.NULL) ? "" : mOrder.getSupplyer_name());
			mFromPhone.setText(mOrder.getSupplyer_phone().equals(Const.NULL) ? "" : mOrder.getSupplyer_phone());
			mToName.setText(mOrder.getReciver().equals(Const.NULL) ? "" : mOrder.getReciver());
			mToPhone.setText(mOrder.getReciver_phone().equals(Const.NULL) ? "" : mOrder.getReciver_phone());
			String remarks = mOrder.getRemark();
			if (remarks != null && !remarks.equals(BaseParams.PARAM_DEFAULT)) {
				mSendComment.setText(remarks);
			}
		}
	}
	
	private void swapAddress() {
		CharSequence temp = mFromAddress.getText();
		mFromAddress.setText(mToAddress.getText());
		mToAddress.setText(temp);
	}

	private void searchAddress(boolean isFrom) {
		mIsFrom = isFrom;
		SearchActivity.startSearchActivity(this, REQUEST_CODE_SEARCH, isFrom ? mFromInfos : mToInfos);
	}

	private void publish() {
		if (checkValid()) {
			showProgressDialog();
			String pay = mGoodsPay.getText().toString();
			mOrder.setSupplyer_name(mFromName.getText().toString());
			mOrder.setSupplyer_phone(mFromPhone.getText().toString());
			mOrder.setReciver(mToName.getText().toString());
			mOrder.setReciver_phone(mToPhone.getText().toString());
			mOrder.setRemark(mSendComment.getText().toString());
			mOrder.setMess_fee(mMessageFree.getCheckedRadioButtonId() == R.id.message_free_yes ? 1 : 2);
			mOrder.setGoods_cost(TextUtils.isEmpty(pay) ? -9 : Integer.parseInt(pay));
			mOrder.setEnd_addr(mToAddress.getText().toString());
			mOrder.setStart_addr(mFromAddress.getText().toString());
			BaseParams params = mOrder.getChangeParams();
			params.add("method", "changeCos");
			params.add("supplyer_cd", LoginManager.getInstance().getUserInfo().getSupplyer_cd());
			AsyncHttp.get(Const.URL_CHANGE_ORDER, params, mRequestHandler);
		}
	}
	
	private boolean checkValid() {
		String fromPhone = mFromPhone.getText().toString();
		String toPhone = mToPhone.getText().toString();
		String fromAddress = mFromAddress.getText().toString();
		if (TextUtils.isEmpty(fromPhone)) {
			Util.showTips(this, getResources().getString(
					R.string.send_phone_empty));
			return false;
		} else if (!Util.isPhoneNumber(fromPhone)) {
			Util.showTips(this, getResources().getString(
					R.string.send_phone_error));
			return false;
		} else if (!TextUtils.isEmpty(toPhone) && !Util.isPhoneNumber(toPhone)) {
			Util.showTips(this, getResources().getString(
					R.string.to_phone_error));
			return false;
		} else if (TextUtils.isEmpty(fromAddress)) {
			Util.showTips(this, getResources().getString(
					R.string.address_from_empty));
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
