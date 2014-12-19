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
import cn.boweikeji.wuliu.view.SendGroupItem;
import cn.boweikeji.wuliu.view.SendInputItem;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SendDetailActivity extends BaseActivity {

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
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.from_name)
	SendInputItem mFromName;
	@InjectView(R.id.from_phone)
	SendInputItem mFromPhone;
	@InjectView(R.id.to_name)
	SendInputItem mToName;
	@InjectView(R.id.to_phone)
	SendInputItem mToPhone;
	@InjectView(R.id.message_free)
	SendGroupItem mMessageFree;
	@InjectView(R.id.send_comment)
	SendInputItem mSendComment;
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
		setContentView(R.layout.activity_send_detail_new);
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
		mMenuBtn.setImageResource(R.drawable.ic_navi_back);
		mMenuBtn.setOnClickListener(mOnClickListener);
		initLabel();
		initInput();
	}

	private void initLabel() {
		mFromName.setName(R.string.label_from_name);
		mToName.setName(R.string.label_to_name);
		mFromPhone.setName(R.string.label_from_phone);
		mToPhone.setName(R.string.label_to_phone);
		mSendComment.setName(R.string.label_send_comment);
		mMessageFree.setName(R.string.label_message_free);
	}
	
	private void initInput() {
		mFromName.setMaxLength(6);
		mToName.setMaxLength(6);
		mFromPhone.setMaxLength(11);
		mToPhone.setMaxLength(11);
		mSendComment.setMaxLength(30);
		mFromPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
		mToPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
	}
	
	private void initData() {
		mMessageFree.check(R.id.message_free_yes);
	}

	private void showRule() {
		WebViewActivity.startWebViewActivity(this,
				getString(R.string.title_rule), Const.URL_RULES);
	}

	private void publish() {
		if (checkValid()) {
			showProgressDialog();
			mOrder.setSupplyer_name(mFromName.getValue());
			mOrder.setSupplyer_phone(mFromPhone.getValue());
			mOrder.setReciver(mToName.getValue());
			mOrder.setReciver_phone(mToPhone.getValue());
			mOrder.setRemark(mSendComment.getValue());
			mOrder.setMess_fee(mMessageFree.getCheckedRadioButtonId() == R.id.message_free_yes ? 1
					: 2);
			BaseParams params = mOrder.getPublishParams();
			params.add("method", "sendGoodInfos");
			params.add("supplyer_cd", LoginManager.getInstance().getUserInfo()
					.getSupplyer_cd());
			AsyncHttp.get(Const.URL_SEND_GOODS, params, mRequestHandler);
		}
	}

	private boolean checkValid() {
		String fromPhone = mFromPhone.getValue();
		String toPhone = mToPhone.getValue();
		if (TextUtils.isEmpty(fromPhone)) {
			Util.showTips(this,
					getResources().getString(R.string.send_phone_empty));
			return false;
		} else if (!Util.isPhoneNumber(fromPhone)) {
			Util.showTips(this,
					getResources().getString(R.string.send_phone_error));
			return false;
		} else if (!TextUtils.isEmpty(toPhone) && !Util.isPhoneNumber(toPhone)) {
			Util.showTips(this,
					getResources().getString(R.string.to_phone_error));
			return false;
		} else if (!mSendAccept.isChecked()) {
			Util.showTips(this, getResources().getString(R.string.accept_rules));
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
				.setPositiveButton(R.string.know,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								startActivity(new Intent(
										SendDetailActivity.this,
										MainActivity.class));
							}
						})
				.setNegativeButton(R.string.title_order_detail,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								Intent intent = new Intent(
										SendDetailActivity.this,
										MainActivity.class);
								intent.putExtra(MainActivity.KEY_REDIRECT, true);
								intent.putExtra(MainActivity.KEY_REDIRECT_TO,
										MainActivity.REDIRECT_TO_ORDERDETAIL);
								intent.putExtra("goods_cd",
										mOrder.getGoods_cd());
								startActivity(intent);
							}
						}).create();
		dialog.show();
	}

	private void requestResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			try {
				int res = response.getInt("res");
				String msg = response.getString("msg");
				Util.showTips(this, msg);
				if (res == 2) {// 成功
					JSONObject info = response.optJSONObject("infos");
					if (info != null) {
						String goods_cd = info.optString("goods_cd");
						mOrder.setGoods_cd(goods_cd);
					}
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
