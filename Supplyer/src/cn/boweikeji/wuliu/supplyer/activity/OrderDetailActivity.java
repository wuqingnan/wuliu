package cn.boweikeji.wuliu.supplyer.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.http.AsyncHttp;
import cn.boweikeji.wuliu.supplyer.Const;
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
		
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			requestResult(response);
		};
		
		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
			requestResult(null);
		};
	};
	
	private JsonHttpResponseHandler mOperateHandler = new JsonHttpResponseHandler() {
		
		public void onFinish() {
			hideProgressDialog();
		};
		
		public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
			operateResult(response);
		};
		
		public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
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
	
	private View mFooter;
	private Button mCancelBtn;
	private Button mChangeBtn;
	private Button mConsultBtn;
	private Button mConfirmBtn;
	private Button mCommentBtn;
	
	private ProgressDialog mProgressDialog;
	
	private String mGoodsCD;
	
	private DetailAdapter mAdapter;
	
	private AlertDialog mCommentDialog;
	private RadioGroup mCommentStar;
	private EditText mCommentContent;
	
	private String[] mDriverScore;
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
		mAdapter = new DetailAdapter(this);
		mListView.setAdapter(mAdapter);
	}
	
	private void initFooter() {
		View layout = getLayoutInflater().inflate(R.layout.order_detail_footer, null);
		mFooter = layout.findViewById(R.id.order_detail_footer);
		mCancelBtn = (Button) mFooter.findViewById(R.id.order_detail_cancel);
		mChangeBtn = (Button) mFooter.findViewById(R.id.order_detail_change);
		mConsultBtn = (Button) mFooter.findViewById(R.id.order_detail_consult);
		mConfirmBtn = (Button) mFooter.findViewById(R.id.order_detail_confirm);
		mCommentBtn = (Button) mFooter.findViewById(R.id.order_detail_comment);
		mFooter.setVisibility(View.GONE);
		mListView.addFooterView(layout);
		mListView.setFooterDividersEnabled(false);
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
				if (res == 2) {//成功
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
				if (res == 2) {//成功
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
		if (driverCD != null && driverCD.length() > 0 && !driverCD.equals(BaseParams.PARAM_DEFAULT)) {
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
		params.add("remark", comment == null ? BaseParams.PARAM_DEFAULT : comment);
		AsyncHttp.get(Const.URL_COMMENT_ORDER, params, mOperateHandler);
	}
	
	private void showCancelDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
		.setTitle(R.string.order_cancel)
		.setMessage(R.string.order_cancel_message)
		.setCancelable(true)
		.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				cancelOrder();
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.create();
		dialog.show();
	}

	private void showConsultDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
		.setTitle(R.string.order_consult)
		.setMessage(R.string.order_consult_message)
		.setCancelable(true)
		.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				consultOrder();
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.create();
		dialog.show();
	}
	
	private void showConfirmDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
		.setTitle(R.string.order_confirm)
		.setMessage(R.string.order_confirm_message)
		.setCancelable(true)
		.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				confirmOrder();
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.create();
		dialog.show();
	}
	
	private void showCommentDialog() {
		if (mCommentDialog == null) {
			View contentView = getLayoutInflater().inflate(R.layout.dialog_comment_driver, null);
			mCommentStar = (RadioGroup) contentView.findViewById(R.id.comment_driver_star);
			mCommentContent = (EditText) contentView.findViewById(R.id.comment_driver_content);
			mCommentStar.setOnCheckedChangeListener(mStarListener);
			mCommentDialog = new AlertDialog.Builder(this)
			.setTitle(R.string.order_comment)
			.setCancelable(true)
			.setView(contentView)
			.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					commentOrder();
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create();
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
	
	public static class DetailAdapter extends BaseAdapter {

		private static final int[] NAMES = {
			R.string.label_order_code,
			R.string.label_order_state,
			R.string.label_goods_name,
			R.string.label_goods_type,
			R.string.label_goods_pay,
			R.string.label_message_free,
			R.string.label_pick_time,
			R.string.label_create_time,
			R.string.label_send_comment,
			R.string.label_from_name,
			R.string.label_from_phone,
			R.string.label_to_name,
			R.string.label_to_phone,
			R.string.label_from_address,
			R.string.label_to_address,
			R.string.label_driver_name,
			R.string.label_driver_phone,
			R.string.label_driver_truck,
			R.string.label_driver_level,
			R.string.label_driver_score
		};
		
		private Context mContext;
		private LayoutInflater mInflater;
		private Order mOrder;
		private Driver mDriver;
		
		private String[] mTypes;
		private String[] mStars;
		private String[] mStateName;
		private int[] mStateValue;
		
		private String[] mTruckTypes;
		
		private int mOrderCnt;
		private int mDriverCnt;
		
		public DetailAdapter(Context context) {
			mContext = context;
			mInflater = LayoutInflater.from(context);
			mTypes = context.getResources().getStringArray(R.array.goods_type_list);
			mStars = context.getResources().getStringArray(R.array.driver_stars);
			mStateName = context.getResources().getStringArray(R.array.order_state_name);
			mStateValue = context.getResources().getIntArray(R.array.order_state_value);
			mTruckTypes = context.getResources().getStringArray(R.array.truck_type_list);
		}
		
		@Override
		public int getCount() {
			int size = 0;
			if (mOrder != null) {
				size += mOrderCnt;
			}
			if (mDriver != null) {
				size += mDriverCnt;
			}
			return size;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		public void setData(Order order, Driver driver) {
			mOrder = order;
			mDriver = driver;
			mOrderCnt = 15;
			if (mDriver != null) {
				if (mOrder.getCredit() == 1) {
					mDriverCnt = 5;
				} else {
					mDriverCnt = 4;
				}
			}
			notifyDataSetChanged();
		}
		
		public Order getOrder() {
			return mOrder;
		}
		
		public Driver getDriver() {
			return mDriver;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.order_detail_item, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			int name = NAMES[position];
			String value = null;
			switch (name) {
			case R.string.label_order_code:
				value = mOrder.getGoods_cd();
				break;
			case R.string.label_order_state:
				value = mStateName[getStateIndexByValue(mOrder.getState())];
				break;
			case R.string.label_goods_pay:
				int cost = mOrder.getGoods_cost();
				if (cost <= 0) {
					value = mContext.getString(R.string.negotiable);
				} else {
					value = String.format(mContext.getString(R.string.value_yuan), mOrder.getGoods_cost());
				}
				break;
			case R.string.label_message_free:
				int free = mOrder.getMess_fee();
				if (free == 0) {
					value = mContext.getString(R.string.free_no);
				} else {
					value = mContext.getString(R.string.free_yes);
				}
				break;
			case R.string.label_pick_time:
				value = mOrder.getPick_time();
				break;
			case R.string.label_create_time:
				value = mOrder.getCreate_date();
				break;
			case R.string.label_send_comment:
				value = mOrder.getRemark();
				if (value != null && value.equals(BaseParams.PARAM_DEFAULT)) {
					value = null;
				}
				break;
			case R.string.label_goods_name:
				value = mOrder.getGoods_name();
				break;
			case R.string.label_goods_type:
				int type = mOrder.getGoods_type_code();
				if (type >= 0 && type < mTypes.length) {
					value = mTypes[type];
				}
				break;
			case R.string.label_from_name:
				value = mOrder.getSupplyer_name();
				if (value != null && value.equals(BaseParams.PARAM_DEFAULT)) {
					value = mContext.getString(R.string.unknown);
				}
				break;
			case R.string.label_from_phone:
				value = mOrder.getSupplyer_phone();
				if (value != null && value.equals(BaseParams.PARAM_DEFAULT)) {
					value = mContext.getString(R.string.unknown);
				}
				break;
			case R.string.label_to_phone:
				value = mOrder.getReciver_phone();
				if (value != null && value.equals(BaseParams.PARAM_DEFAULT)) {
					value = mContext.getString(R.string.unknown);
				}
				break;
			case R.string.label_to_name:
				value = mOrder.getReciver();
				if (value != null && value.equals(BaseParams.PARAM_DEFAULT)) {
					value = mContext.getString(R.string.unknown);
				}
				break;
			case R.string.label_from_address:
				value = mOrder.getStart_addr();
				break;
			case R.string.label_to_address:
				value = mOrder.getEnd_addr();
				break;
			case R.string.label_driver_name:
				value = mDriver.getDriver_name();
				break;
			case R.string.label_driver_phone:
				value = mDriver.getPhone();
				break;
			case R.string.label_driver_truck:
				int truck = mDriver.getTruck_type_code();
				if (truck >= 0 && truck < mTruckTypes.length) {
					value = mTruckTypes[truck];
				}
				break;
			case R.string.label_driver_level:
				value = mDriver.getCredit_level();
				break;
			case R.string.label_driver_score:
				int stars = mDriver.getStars();
				if (stars >= 0 && stars < mStars.length) {
					value = mStars[stars];
				}
				break;
			}
			holder.refresh(name, value);
			return convertView;
		}
		
		private int getStateIndexByValue(int value) {
			int index = 0;
			for (int i = 0; i < mStateValue.length; i++) {
				if (mStateValue[i] == value) {
					index = i;
					break;
				}
			}
			return index;
		}
	}
	
	public static class ViewHolder {
		
		@InjectView(R.id.item_name)
		TextView mName;
		@InjectView(R.id.item_value)
		TextView mValue;
		
		public ViewHolder(View parent) {
			ButterKnife.inject(this, parent);
		}
		
		public void refresh(int name, String value) {
			mName.setText(name);
			mValue.setText(value);
		}
		
	}
}
