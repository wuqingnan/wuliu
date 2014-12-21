package cn.boweikeji.wuliu.driver.adapter;

import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.api.BaseParams;
import cn.boweikeji.wuliu.driver.bean.Order;
import cn.boweikeji.wuliu.view.OrderDetailItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class OrderDetailAdapter extends BaseAdapter {

	private final int TYPE_DIVIDER = 0;
	private final int TYPE_SINGLE = 1;

	private static final int[] NAMES = { R.string.label_order_code,
			R.string.label_order_state, R.string.label_order_type,
			R.string.label_pick_time, R.string.label_create_time,
			R.string.label_divider_line, R.string.label_goods_name,
			R.string.label_goods_type, R.string.label_goods_weight,
			R.string.label_goods_value, R.string.label_goods_pay,
			R.string.label_need_truck, R.string.label_divider_line,
			R.string.label_message_free, R.string.label_creater_phone,
			R.string.label_creater_type, R.string.label_send_comment,
			R.string.label_from_address, R.string.label_to_address,
			R.string.label_divider_line, R.string.label_from_name,
			R.string.label_from_phone, R.string.label_divider_line,
			R.string.label_to_name, R.string.label_to_phone };

	private Context mContext;
	private LayoutInflater mInflater;
	private Order mOrder;

	private String[] mGoodsTypes;
	private String[] mUserTypes;
	private String[] mStateName;
	private int[] mStateValue;
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
		return NAMES.length;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public void setData(Order order) {
		mOrder = order;
		notifyDataSetChanged();
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if (NAMES[position] == R.string.label_divider_line) {
			return TYPE_DIVIDER;
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

//	private View getTwoColsView(int position, View convertView) {
//		TwoColsHolder holder = null;
//		if (convertView == null) {
//			convertView = mInflater.inflate(R.layout.order_detail_item_two,
//					null);
//			holder = new TwoColsHolder(convertView);
//			convertView.setTag(holder);
//		} else {
//			holder = (TwoColsHolder) convertView.getTag();
//		}
//		holder.refresh(mOrder,
//				mStateName[getStateIndexByValue(mOrder.getState())]);
//		return convertView;
//	}

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
		case R.string.label_order_state:{
			value = mStateName[getStateIndexByValue(mOrder.getState())];
		}
			break;
		case R.string.label_order_type:
			value = mContext
					.getString(mOrder.isOrder() ? R.string.order_bespeak
							: R.string.order_realtime);
			break;
		case R.string.label_pick_time:
			value = mOrder.getPick_time();
			if (value != null && value.equals(BaseParams.PARAM_DEFAULT)) {
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
						mOrder.getGoods_cost());
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
		case R.string.label_creater_phone:
			value = mOrder.getPhone();
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
			if (value != null && value.equals(BaseParams.PARAM_DEFAULT)) {
				value = null;
			}
			break;
		case R.string.label_from_address:
			value = mOrder.getStart_addr();
			if (value != null && value.equals(BaseParams.PARAM_DEFAULT)) {
				value = mContext.getString(R.string.unknown);
			}
			break;
		case R.string.label_to_address:
			value = mOrder.getEnd_addr();
			if (value != null && value.equals(BaseParams.PARAM_DEFAULT)) {
				value = mContext.getString(R.string.unknown);
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

//	public static class TwoColsHolder {
//
//		@InjectView(R.id.item_left)
//		OrderDetailItem mLeftItem;
//		@InjectView(R.id.item_right)
//		OrderDetailItem mRightItem;
//
//		public TwoColsHolder(View parent) {
//			ButterKnife.inject(this, parent);
//		}
//
//		public void refresh(Order order, String state) {
//			mLeftItem.setName(R.string.label_order_code);
//			mLeftItem.setValue(order.getGoods_cd());
//			mRightItem.setName(R.string.label_order_state);
//			mRightItem.setValue(state);
//		}
//
//	}

	public static class SingleHolder {

		@InjectView(R.id.item_name)
		TextView mName;
		@InjectView(R.id.item_value)
		TextView mValue;

		public SingleHolder(View parent) {
			ButterKnife.inject(this, parent);
		}

		public void refresh(int name, String value) {
			mName.setText(name);
			mValue.setText(value);
		}

	}
}
