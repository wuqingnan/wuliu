package cn.boweikeji.wuliu.supplyer.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.supplyer.R;
import cn.boweikeji.wuliu.supplyer.api.BaseParams;
import cn.boweikeji.wuliu.supplyer.bean.Driver;
import cn.boweikeji.wuliu.supplyer.bean.Order;
import cn.boweikeji.wuliu.utils.Util;

public class OrderDetailAdapter extends BaseAdapter {

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ v.getTag()));
			mContext.startActivity(intent);
		}
	};

	private final int TYPE_DIVIDER = 0;
	private final int TYPE_SINGLE = 1;
	private final int TYPE_CALL = 2;

	private static final int[] NAMES = { R.string.label_order_code,
			R.string.label_order_state, R.string.label_pick_time,
			R.string.label_create_time, R.string.label_divider_line,
			R.string.label_goods_name, R.string.label_goods_type,
			R.string.label_goods_weight, R.string.label_goods_value,
			R.string.label_goods_pay, R.string.label_need_truck,
			R.string.label_divider_line, R.string.label_message_free,
			R.string.label_creater_phone, R.string.label_creater_type,
			R.string.label_send_comment, R.string.label_from_address,
			R.string.label_to_address, R.string.label_divider_line,
			R.string.label_from_name, R.string.label_from_phone,
			R.string.label_divider_line, R.string.label_to_name,
			R.string.label_to_phone, R.string.label_divider_line,
			R.string.label_driver_name, R.string.label_driver_phone,
			R.string.label_driver_level, R.string.label_divider_line,
			R.string.label_truck_type, R.string.label_truck_no,
			R.string.label_truck_load };

	private Context mContext;
	private LayoutInflater mInflater;
	private Order mOrder;
	private Driver mDriver;

	private String[] mGoodsTypes;
	private String[] mStateName;
	private int[] mStateValue;
	private String[] mUserTypes;
	private String[] mTruckTypes;

	public OrderDetailAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mGoodsTypes = context.getResources().getStringArray(
				R.array.goods_type_list);
		mStateName = context.getResources().getStringArray(
				R.array.order_state_name);
		mStateValue = context.getResources().getIntArray(
				R.array.order_state_value);
		mUserTypes = context.getResources().getStringArray(R.array.user_types);
		mTruckTypes = context.getResources().getStringArray(
				R.array.truck_type_list);
	}

	@Override
	public int getCount() {
		if (mOrder == null) {
			return 0;
		}
		if (mDriver != null) {
			return 32;
		}
		return 25;
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
		notifyDataSetChanged();
	}

	public Order getOrder() {
		return mOrder;
	}

	public Driver getDriver() {
		return mDriver;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	private boolean callable(int resid) {
		if (resid == R.string.label_creater_phone
				|| resid == R.string.label_from_phone
				|| resid == R.string.label_to_phone
				|| resid == R.string.label_driver_phone) {
			return true;
		}
		return false;
	}

	@Override
	public int getItemViewType(int position) {
		if (NAMES[position] == R.string.label_divider_line) {
			return TYPE_DIVIDER;
		} else if (callable(NAMES[position])) {
			return TYPE_CALL;
		}
		return TYPE_SINGLE;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		switch (getItemViewType(position)) {
		case TYPE_DIVIDER:
			convertView = getDividerView(position, convertView);
			break;
		case TYPE_SINGLE:
			convertView = getSingleView(position, convertView);
			break;
		case TYPE_CALL:
			convertView = getCallView(position, convertView);
			break;
		}
		return convertView;
	}

	private View getDividerView(int position, View convertView) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.order_detail_item_divider,
					null);
		}
		return convertView;
	}

	private View getSingleView(int position, View convertView) {
		SingleHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.order_detail_item, null);
			holder = new SingleHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (SingleHolder) convertView.getTag();
		}
		int name = NAMES[position];
		String value = null;
		switch (name) {
		case R.string.label_order_code:
			value = mOrder.getGoods_cd();
			break;
		case R.string.label_order_state: {
			value = mStateName[getStateIndexByValue(mOrder.getState())];
		}
			break;
		case R.string.label_pick_time:
			value = mOrder.getPick_time();
			if (TextUtils.isEmpty(value)
					|| value.equals(BaseParams.PARAM_DEFAULT)) {
				value = mContext.getString(R.string.unknown);
			}
			break;
		case R.string.label_create_time:
			value = mOrder.getCreate_date();
			break;
		case R.string.label_goods_name:
			value = mOrder.getGoods_name();
			break;
		case R.string.label_goods_type: {
			int type = mOrder.getGoods_type_code();
			if (type >= 0 && type < mGoodsTypes.length) {
				value = mGoodsTypes[type];
			}
		}
			break;
		case R.string.label_goods_weight: {
			int weight = mOrder.getWeight();
			if (weight <= 0) {
				value = mContext.getString(R.string.unknown);
			} else {
				value = String.format(mContext.getString(R.string.format_ton),
						weight);
			}
		}
			break;
		case R.string.label_goods_value: {
			int goodsValue = mOrder.getGoods_value();
			if (goodsValue <= 0) {
				value = mContext.getString(R.string.unknown);
			} else {
				value = String.format(mContext.getString(R.string.format_yuan),
						goodsValue);
			}
		}
			break;
		case R.string.label_goods_pay: {
			int cost = mOrder.getGoods_cost();
			if (cost <= 0) {
				value = mContext.getString(R.string.negotiable);
			} else {
				value = String.format(mContext.getString(R.string.format_yuan),
						cost);
			}
		}
			break;
		case R.string.label_need_truck: {
			int type = mOrder.getTruck_type_code();
			if (type >= 0 && type < mTruckTypes.length) {
				value = mTruckTypes[type];
			}
		}
			break;
		case R.string.label_message_free: {
			int free = mOrder.getMess_fee();
			if (free == 0) {
				value = mContext.getString(R.string.free_no);
			} else {
				value = mContext.getString(R.string.free_yes);
			}
		}
			break;
		case R.string.label_creater_type: {
			int type = mOrder.getSupplyer_type();
			if (type >= 0 && type < mUserTypes.length) {
				value = mUserTypes[type];
			}
		}
			break;
		case R.string.label_send_comment:
			value = mOrder.getRemark();
			if (TextUtils.isEmpty(value)
					|| value.equals(BaseParams.PARAM_DEFAULT)) {
				value = null;
			}
			break;
		case R.string.label_from_address:
			value = mOrder.getStart_addr();
			if (TextUtils.isEmpty(value)
					|| value.equals(BaseParams.PARAM_DEFAULT)) {
				value = mContext.getString(R.string.unknown);
			}
			break;
		case R.string.label_to_address:
			value = mOrder.getEnd_addr();
			if (TextUtils.isEmpty(value)
					|| value.equals(BaseParams.PARAM_DEFAULT)) {
				value = mContext.getString(R.string.unknown);
			}
			break;
		case R.string.label_from_name:
			value = mOrder.getSupplyer_name();
			if (TextUtils.isEmpty(value)
					|| value.equals(BaseParams.PARAM_DEFAULT)) {
				value = mContext.getString(R.string.unknown);
			}
			break;
		case R.string.label_to_name:
			value = mOrder.getReciver();
			if (TextUtils.isEmpty(value)
					|| value.equals(BaseParams.PARAM_DEFAULT)) {
				value = mContext.getString(R.string.unknown);
			}
			break;
		case R.string.label_driver_name:
			value = mDriver.getDriver_name();
			break;
		case R.string.label_truck_type: {
			int type = mDriver.getTruck_type_code();
			if (type >= 0 && type < mTruckTypes.length) {
				value = mTruckTypes[type];
			}
		}
			break;
		case R.string.label_truck_no:
			value = mDriver.getTruck_no();
			break;
		case R.string.label_truck_load:
			value = String.format(mContext.getString(R.string.format_ton),
					mDriver.getLoad_weight());
			break;
		}
		if (name == R.string.label_driver_level) {
			int level = mDriver.getCredit_level();
			holder.refresh(name, level);
		} else {
			holder.refresh(name, value);
		}
		return convertView;
	}

	private View getCallView(int position, View convertView) {
		CallHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.order_detail_item_call,
					null);
			holder = new CallHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (CallHolder) convertView.getTag();
		}
		int name = NAMES[position];
		String value = null;
		switch (name) {
		case R.string.label_creater_phone:
			value = mOrder.getSender_phone();
			break;
		case R.string.label_from_phone:
			value = mOrder.getSupplyer_phone();
			if (TextUtils.isEmpty(value)
					|| value.equals(BaseParams.PARAM_DEFAULT)) {
				value = mContext.getString(R.string.unknown);
			}
			break;
		case R.string.label_to_phone:
			value = mOrder.getReciver_phone();
			if (TextUtils.isEmpty(value)
					|| value.equals(BaseParams.PARAM_DEFAULT)) {
				value = mContext.getString(R.string.unknown);
			}
			break;
		case R.string.label_driver_phone:
			value = mDriver.getPhone();
			break;
		}
		holder.refresh(name, value, mOnClickListener);
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

	public static class SingleHolder {

		@InjectView(R.id.item_name)
		TextView mName;
		@InjectView(R.id.item_value)
		TextView mValue;
		@InjectView(R.id.item_level)
		RatingBar mLevel;

		public SingleHolder(View parent) {
			ButterKnife.inject(this, parent);
		}

		public void refresh(int name, String value) {
			mName.setText(name);
			mValue.setText(value);
			mValue.setVisibility(View.VISIBLE);
			mLevel.setVisibility(View.GONE);
		}

		public void refresh(int name, int level) {
			mName.setText(name);
			if (level < 0) {
				level = 0;
			} else if (level > 4) {
				level = 4;
			}
			mLevel.setRating(level + 1);
			mValue.setVisibility(View.GONE);
			mLevel.setVisibility(View.VISIBLE);
		}
	}

	public static class CallHolder {

		@InjectView(R.id.item_name)
		TextView mName;
		@InjectView(R.id.item_value)
		TextView mValue;
		@InjectView(R.id.item_call)
		ImageView mCall;

		public CallHolder(View parent) {
			ButterKnife.inject(this, parent);
		}

		public void refresh(int name, String value, OnClickListener listener) {
			mName.setText(name);
			mValue.setText(value);
			mCall.setTag(value);
			mCall.setOnClickListener(listener);
			if (Util.isPhoneNumber(value)) {
				mCall.setVisibility(View.VISIBLE);
			} else {
				mCall.setVisibility(View.GONE);
			}
		}
	}
}
