package cn.boweikeji.wuliu.driver.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.Const;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.api.BaseParams;
import cn.boweikeji.wuliu.driver.bean.Order;
import cn.boweikeji.wuliu.driver.bean.UserInfo;
import cn.boweikeji.wuliu.driver.event.OrderEvent;
import cn.boweikeji.wuliu.driver.http.AsyncHttp;
import cn.boweikeji.wuliu.driver.manager.LoginManager;
import cn.boweikeji.wuliu.driver.utils.Util;


import com.loopj.android.http.JsonHttpResponseHandler;

import de.greenrobot.event.EventBus;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class OrderDetailActivity extends BaseActivity {

	private static final String TAG = OrderDetailActivity.class.getSimpleName();
	
	private static final String KEY_GOODS_CD = "goods_cd";
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				finish();
			} else if (view == mRobBtn) {
				robOrder();
			} else if (view == mDropBtn) {
				showDropDialog();
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
	
	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.order_detail)
	ListView mListView;
	
	private View mFooter;
	private Button mRobBtn;
	private Button mDropBtn;
	
	private ProgressDialog mProgressDialog;
	
	private String mGoodsCD;
	
	private DetailAdapter mAdapter;
	
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
		initList();
	}
	
	private void initFooter() {
		mFooter = getLayoutInflater().inflate(R.layout.order_detail_footer, null);
		mRobBtn = (Button) mFooter.findViewById(R.id.rob_order);
		mDropBtn = (Button) mFooter.findViewById(R.id.drop_order);
		mRobBtn.setOnClickListener(mOnClickListener);
		mDropBtn.setOnClickListener(mOnClickListener);
	}
	
	private void initList() {
		mListView.addFooterView(mFooter);
		mAdapter = new DetailAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.removeFooterView(mFooter);
		mListView.setFooterDividersEnabled(false);
	}
	
	private void updateFooter(Order order) {
		int state = order.getState();
		if (state == 0 || state == 7 || state == 21 || state == 22) {
			mRobBtn.setVisibility(View.VISIBLE);
			mDropBtn.setVisibility(View.GONE);
			if (mListView.getFooterViewsCount() <= 0) {
				mListView.addFooterView(mFooter);
			}
		} else if (state == 1) {
			mRobBtn.setVisibility(View.GONE);
			mDropBtn.setVisibility(View.VISIBLE);
			if (mListView.getFooterViewsCount() <= 0) {
				mListView.addFooterView(mFooter);
			}
		} else {
			mListView.removeFooterView(mFooter);
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
		params.add("method", "getCoDetails");
		params.add("driver_cd", info.getDriver_cd());
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
				Util.showTips(this, msg);
				if (res == 2) {//成功
					parseJson(response.optJSONObject("info"));
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
				Util.showTips(this, msg);
				if (res == 2) {//成功
					refresh();
					EventBus.getDefault().post(new OrderEvent(mGoodsCD));
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
		order.setGoods_cd(infos.optString("goods_cd"));
		order.setState(infos.optInt("state"));
		order.setCreate_date(infos.optString("create_date"));
		order.setPick_time(infos.optString("pick_time"));
		order.setGoods_name(infos.optString("goods_name"));
		order.setGoods_type_code(infos.optInt("goods_type_code"));
		order.setTrunk_type_code(infos.optInt("trunk_type_code"));
		order.setWeight(infos.optInt("weight"));
		order.setGoods_value(infos.optInt("goods_value"));
		order.setMess_fee(infos.optInt("mess_fee"));
		order.setGoods_cost(infos.optInt("goods_cost"));
		order.setRemark(infos.optString("remark"));
		
		order.setPhone(infos.optString("phone"));
		order.setSupplyer_type(infos.optInt("supplyer_type"));
		order.setSupplyer_name(infos.optString("supplyer_name"));
		order.setSupplyer_phone(infos.optString("supplyer_phone"));
		order.setStart_addr(infos.optString("start_addr"));
		order.setReciver(infos.optString("reciver"));
		order.setReciver_phone(infos.optString("reciver_phone"));
		order.setEnd_addr(infos.optString("end_addr"));
		
		updateFooter(order);
		mAdapter.setData(order);
	}
	
	private void robOrder() {
		showProgressDialog();
		UserInfo info = LoginManager.getInstance().getUserInfo();
		BaseParams params = new BaseParams();
		params.add("method", "robMessage");
		params.add("driver_cd", info.getDriver_cd());
		params.add("passwd", info.getPasswd());
		params.add("goods_cd", "" + mGoodsCD);
		AsyncHttp.get(Const.URL_ROB_ORDER, params, mOperateHandler);
	}
	
	private void dropOrder() {
		showProgressDialog();
		UserInfo info = LoginManager.getInstance().getUserInfo();
		BaseParams params = new BaseParams();
		params.add("method", "driverDropMessage");
		params.add("driver_cd", info.getDriver_cd());
		params.add("passwd", info.getPasswd());
		params.add("goods_cd", "" + mGoodsCD);
		AsyncHttp.get(Const.URL_DROP_ORDER, params, mOperateHandler);
	}
	
	private void showDropDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
		.setTitle(R.string.drop_order)
		.setMessage(R.string.order_drop_message)
		.setCancelable(true)
		.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				dropOrder();
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
			R.string.label_goods_weight,
			R.string.label_goods_value,
			R.string.label_goods_pay,
			R.string.label_need_truck,
			R.string.label_message_free,
			R.string.label_pick_time,
			R.string.label_create_time,
			R.string.label_send_comment,
			R.string.label_message_phone,
			R.string.label_message_type,
			R.string.label_from_name,
			R.string.label_from_phone,
			R.string.label_from_address,
			R.string.label_to_name,
			R.string.label_to_phone,
			R.string.label_to_address,
		};
		
		private Context mContext;
		private LayoutInflater mInflater;
		private Order mOrder;
		
		private String[] mGoodsTypes;
		private String[] mStateName;
		private String[] mUserTypes;
		private String[] mTruckTypes;
		private int[] mStateValue;
		
		public DetailAdapter(Context context) {
			mContext = context;
			mInflater = LayoutInflater.from(context);
			mGoodsTypes = context.getResources().getStringArray(R.array.goods_type_list);
			mStateName = context.getResources().getStringArray(R.array.order_state_name);
			mStateValue = context.getResources().getIntArray(R.array.order_state_value);
			mUserTypes = context.getResources().getStringArray(R.array.user_types);
			mTruckTypes = context.getResources().getStringArray(R.array.truck_type_list);
		}
		
		@Override
		public int getCount() {
			return mOrder == null ? 0 : NAMES.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void setData(Order order) {
			mOrder = order;
			notifyDataSetChanged();
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
			case R.string.label_need_truck:
			{
				int type = mOrder.getTrunk_type_code();
				if (type >= 0 && type < mTruckTypes.length) {
					value = mTruckTypes[type];
				}
			}
				break;
			case R.string.label_message_free:
				if (mOrder.isFree()) {
					value = mContext.getString(R.string.free_yes);
				} else {
					value = mContext.getString(R.string.free_no);
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
			{
				int type = mOrder.getGoods_type_code();
				if (type >= 0 && type < mGoodsTypes.length) {
					value = mGoodsTypes[type];
				}
			}
				break;
			case R.string.label_goods_weight:
				value = String.format(mContext.getString(R.string.value_ton), mOrder.getWeight());
				break;
			case R.string.label_goods_value:
				int goodsValue = mOrder.getGoods_value();
				if (goodsValue <= 0) {
					value = mContext.getString(R.string.unknown);
				} else {
					value = String.format(mContext.getString(R.string.value_yuan), mOrder.getGoods_value());
				}
				break;
			case R.string.label_message_phone:
				value = mOrder.getPhone();
				break;
			case R.string.label_message_type:
				int userType = mOrder.getSupplyer_type();
				if (userType >= 0 && userType < mUserTypes.length) {
					value = mUserTypes[userType];
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
			case R.string.label_to_name:
				value = mOrder.getReciver();
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
			case R.string.label_from_address:
				value = mOrder.getStart_addr();
				break;
			case R.string.label_to_address:
				value = mOrder.getEnd_addr();
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
