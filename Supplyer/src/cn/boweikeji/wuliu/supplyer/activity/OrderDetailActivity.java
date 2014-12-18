package cn.boweikeji.wuliu.supplyer.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.http.AsyncHttp;
import cn.boweikeji.wuliu.supplyer.Const;
import cn.boweikeji.wuliu.supplyer.adapter.OrderDetailAdapter;
import cn.boweikeji.wuliu.supplyer.api.BaseParams;
import cn.boweikeji.wuliu.supplyer.bean.Driver;
import cn.boweikeji.wuliu.supplyer.bean.Order;
import cn.boweikeji.wuliu.supplyer.bean.UserInfo;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

public class OrderDetailActivity extends BaseActivity {

	private static final String TAG = OrderDetailActivity.class.getSimpleName();

	private static final String KEY_GOODS_CD = "goods_cd";

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				finish();
			} else if (view == mCancelBtn) {
				showCancelDialog();
			} else if (view == mChangeBtn) {
				changeOrder();
			} else if (view == mConsultBtn) {
				showConsultDialog();
			} else if (view == mConfirmBtn) {
				showConfirmDialog();
			} else if (view == mCommentBtn) {
				showCommentDialog();
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

	private JsonHttpResponseHandler mOperateHandler = new JsonHttpResponseHandler() {

		public void onFinish() {
			hideProgressDialog();
		};

		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			operateResult(response);
		};

		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			operateResult(null);
		};
	};

	private RadioGroup.OnCheckedChangeListener mStarListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.comment_driver_star_good:
				mStar = 0;
				break;
			case R.id.comment_driver_star_normal:
				mStar = 1;
				break;
			case R.id.comment_driver_star_bad:
				mStar = 2;
				break;
			}
		}
	};

	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.order_detail)
	ListView mListView;
	@InjectView(R.id.order_detail_footer)
	View mFooter;
	@InjectView(R.id.order_detail_cancel)
	Button mCancelBtn;
	@InjectView(R.id.order_detail_change)
	Button mChangeBtn;
	@InjectView(R.id.order_detail_consult)
	Button mConsultBtn;
	@InjectView(R.id.order_detail_confirm)
	Button mConfirmBtn;
	@InjectView(R.id.order_detail_comment)
	Button mCommentBtn;

	private ProgressDialog mProgressDialog;

	private String mGoodsCD;

	private OrderDetailAdapter mAdapter;

	private AlertDialog mCommentDialog;
	private RadioGroup mCommentStar;
	private EditText mCommentContent;
	private int mStar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);
		initView();
		initData();
	}

	private void initView() {
		ButterKnife.inject(this);
		mTitle.setText(R.string.title_order_detail);
		mMenuBtn.setOnClickListener(mOnClickListener);
		initFooter();
		mAdapter = new OrderDetailAdapter(this);
		mListView.setAdapter(mAdapter);
	}

	private void initFooter() {
		mFooter.setVisibility(View.GONE);
		mCancelBtn.setOnClickListener(mOnClickListener);
		mChangeBtn.setOnClickListener(mOnClickListener);
		mConsultBtn.setOnClickListener(mOnClickListener);
		mConfirmBtn.setOnClickListener(mOnClickListener);
		mCommentBtn.setOnClickListener(mOnClickListener);
	}

	private void updateFooter(Order order) {
		int state = order.getState();
		mCancelBtn.setVisibility(View.GONE);
		mChangeBtn.setVisibility(View.GONE);
		mConsultBtn.setVisibility(View.GONE);
		mConfirmBtn.setVisibility(View.GONE);
		mCommentBtn.setVisibility(View.GONE);
		if (state == 0 || state == 7) {
			mCancelBtn.setVisibility(View.VISIBLE);
			mChangeBtn.setVisibility(View.VISIBLE);
			mFooter.setVisibility(View.VISIBLE);
		} else if (state == 1) {
			mConsultBtn.setVisibility(View.VISIBLE);
			mConfirmBtn.setVisibility(View.VISIBLE);
			mFooter.setVisibility(View.VISIBLE);
		} else if (state == 9 && order.getCredit() == 0) {
			mCommentBtn.setVisibility(View.VISIBLE);
			mFooter.setVisibility(View.VISIBLE);
		} else {
			mFooter.setVisibility(View.GONE);
		}
	}

	private void initData() {
		Intent intent = getIntent();
		mGoodsCD = intent.getStringExtra(KEY_GOODS_CD);
		loadDetail();
	}

	private void loadDetail() {
		showProgressDialog();
		UserInfo info = LoginManager.getInstance().getUserInfo();
		BaseParams params = new BaseParams();
		params.add("method", "getMySupplyCos");
		params.add("supplyer_cd", info.getSupplyer_cd());
		params.add("passwd", info.getPasswd());
		params.add("goods_cd", "" + mGoodsCD);
		AsyncHttp.get(Const.URL_ORDER_DETAIL, params, mRequestHandler);
	}

	private void refresh() {
		loadDetail();
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
				if (res == 2) {// 成功
					parseJson(response.optJSONObject("infos"));
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

	private void operateResult(JSONObject response) {
		if (response != null && response.length() > 0) {
			Log.d(TAG, "shizy---response: " + response.toString());
			try {
				int res = response.getInt("res");
				String msg = response.getString("msg");
				if (res == 2) {// 成功
					refresh();
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

	private void parseJson(JSONObject infos) {
		Order order = new Order();
		Driver driver = null;
		order.setGoods_cd(infos.optString("goods_cd"));
		order.setGoods_name(infos.optString("goods_name"));
		order.setGoods_type_code(infos.optInt("goods_type_code"));

		order.setSupplyer_name(infos.optString("supplyer_name"));
		order.setSupplyer_phone(infos.optString("supplyer_phone"));
		order.setStart_addr(infos.optString("start_addr"));
		order.setReciver(infos.optString("reciver"));
		order.setReciver_phone(infos.optString("reciver_phone"));
		order.setEnd_addr(infos.optString("end_addr"));

		order.setCredit(infos.optInt("credit"));
		order.setStars(infos.optInt("stars"));
		order.setMess_fee(infos.optInt("mess_fee"));
		order.setGoods_cost(infos.optInt("goods_cost"));
		order.setState(infos.optInt("state"));
		order.setPick_time(infos.optString("pick_time"));
		order.setCreate_date(infos.optString("create_date"));
		order.setRemark(infos.optString("remark"));

		String driverCD = infos.optString("driver_cd");
		if (driverCD != null && driverCD.length() > 0
				&& !driverCD.equals(BaseParams.PARAM_DEFAULT)) {
			driver = new Driver();
			driver.setDriver_cd(driverCD);
			driver.setDriver_name(infos.optString("driver_name"));
			driver.setPhone(infos.optString("phone"));
			driver.setCredit_level(infos.optString("credit_level"));
			driver.setTruck_type_code(infos.optInt("trunk_type_code"));
		}
		updateFooter(order);
		mAdapter.setData(order, driver);
	}

	private void cancelOrder() {
		showProgressDialog();
		UserInfo info = LoginManager.getInstance().getUserInfo();
		BaseParams params = new BaseParams();
		params.add("method", "cancelMessage");
		params.add("supplyer_cd", info.getSupplyer_cd());
		params.add("passwd", info.getPasswd());
		params.add("goods_cd", "" + mGoodsCD);
		AsyncHttp.get(Const.URL_CANCEL_ORDER, params, mOperateHandler);
	}

	private void changeOrder() {
		OrderChangeActivity.startOrderChangeActivity(this, mAdapter.getOrder());
	}

	private void consultOrder() {
		showProgressDialog();
		UserInfo info = LoginManager.getInstance().getUserInfo();
		BaseParams params = new BaseParams();
		params.add("method", "supplyerDropMessage");
		params.add("supplyer_cd", info.getSupplyer_cd());
		params.add("passwd", info.getPasswd());
		params.add("goods_cd", "" + mGoodsCD);
		AsyncHttp.get(Const.URL_CONSULT_ORDER, params, mOperateHandler);
	}

	private void confirmOrder() {
		showProgressDialog();
		UserInfo info = LoginManager.getInstance().getUserInfo();
		BaseParams params = new BaseParams();
		params.add("method", "supplyerCompleteMessage");
		params.add("supplyer_cd", info.getSupplyer_cd());
		params.add("passwd", info.getPasswd());
		params.add("goods_cd", "" + mGoodsCD);
		AsyncHttp.get(Const.URL_CONFIRM_ORDER, params, mOperateHandler);
	}

	private void commentOrder() {
		showProgressDialog();
		String comment = mCommentContent.getText().toString();
		UserInfo info = LoginManager.getInstance().getUserInfo();
		BaseParams params = new BaseParams();
		params.add("method", "ticketDriver");
		params.add("supplyer_cd", info.getSupplyer_cd());
		params.add("passwd", info.getPasswd());
		params.add("goods_cd", "" + mGoodsCD);
		params.add("stars", "" + mStar);
		params.add("remark", comment == null ? BaseParams.PARAM_DEFAULT
				: comment);
		AsyncHttp.get(Const.URL_COMMENT_ORDER, params, mOperateHandler);
	}

	private void showCancelDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(R.string.order_cancel)
				.setMessage(R.string.order_cancel_message)
				.setCancelable(true)
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								cancelOrder();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
		dialog.show();
	}

	private void showConsultDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(R.string.order_consult)
				.setMessage(R.string.order_consult_message)
				.setCancelable(true)
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								consultOrder();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
		dialog.show();
	}

	private void showConfirmDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(R.string.order_confirm)
				.setMessage(R.string.order_confirm_message)
				.setCancelable(true)
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								confirmOrder();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
		dialog.show();
	}

	private void showCommentDialog() {
		if (mCommentDialog == null) {
			View contentView = getLayoutInflater().inflate(
					R.layout.dialog_comment_driver, null);
			mCommentStar = (RadioGroup) contentView
					.findViewById(R.id.comment_driver_star);
			mCommentContent = (EditText) contentView
					.findViewById(R.id.comment_driver_content);
			mCommentStar.setOnCheckedChangeListener(mStarListener);
			mCommentDialog = new AlertDialog.Builder(this)
					.setTitle(R.string.order_comment)
					.setCancelable(true)
					.setView(contentView)
					.setPositiveButton(R.string.confirm,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									commentOrder();
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).create();
		}
		mCommentStar.check(R.id.comment_driver_star_good);
		mCommentContent.setText(null);
		mCommentDialog.show();
	}

	public static void startOrderDetailActivity(Context context, String goods_cd) {
		Intent intent = new Intent(context, OrderDetailActivity.class);
		intent.putExtra(KEY_GOODS_CD, goods_cd);
		context.startActivity(intent);
	}
}
