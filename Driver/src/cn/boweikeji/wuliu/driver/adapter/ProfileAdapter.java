package cn.boweikeji.wuliu.driver.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.Const;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.api.BaseParams;
import cn.boweikeji.wuliu.driver.manager.LoginManager;

public class ProfileAdapter extends BaseAdapter {

	private static final int[] NAMES = { R.string.label_user_name,
			R.string.label_user_state, R.string.label_user_level,
			R.string.label_phone_no, R.string.label_user_type,
			R.string.label_area_code, R.string.label_truck_type,
			R.string.label_truck_no, R.string.label_truck_load,
			R.string.label_user_company };

	private Context mContext;
	private LayoutInflater mInflater;

	private String[] mDriverTypes;
	private String[] mTruckTypes;
	private String[] mDriverStates;

	public ProfileAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mDriverTypes = context.getResources().getStringArray(
				R.array.driver_types);
		mTruckTypes = context.getResources().getStringArray(
				R.array.truck_type_list);
		mDriverStates = context.getResources().getStringArray(
				R.array.driver_status);
	}

	@Override
	public int getCount() {
		return NAMES.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.profile_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		int name = NAMES[position];
		String value = null;
		switch (name) {
		case R.string.label_user_name:
			value = LoginManager.getInstance().getUserInfo().getDriver_name();
			break;
		case R.string.label_user_state: {
			int state = LoginManager.getInstance().getUserInfo().getState();
			if (state >= 0 && state < mDriverStates.length) {
				value = mDriverStates[state];
			}
		}
			break;
		case R.string.label_phone_no:
			value = LoginManager.getInstance().getUserInfo().getPhone();
			break;
		case R.string.label_id_no:
			value = LoginManager.getInstance().getUserInfo().getCard_id();
			break;
		case R.string.label_user_type: {
			int type = LoginManager.getInstance().getUserInfo()
					.getDriver_type();
			if (type >= 0 && type < mDriverTypes.length) {
				value = mDriverTypes[type];
			}
		}
			break;
		case R.string.label_area_code:
			value = LoginManager.getInstance().getUserInfo().getCity_name();
			break;
		case R.string.label_truck_type: {
			int type = LoginManager.getInstance().getUserInfo()
					.getTruck_type_code();
			if (type >= 0 && type < mTruckTypes.length) {
				value = mTruckTypes[type];
			}
		}
			break;
		case R.string.label_truck_no:
			value = LoginManager.getInstance().getUserInfo().getTruck_no();
			break;
		case R.string.label_truck_load: {
			int load = LoginManager.getInstance().getUserInfo()
					.getLoad_weight();
			value = String
					.format(mContext.getString(R.string.format_ton), load);
		}
			break;
		case R.string.label_user_company:
			value = LoginManager.getInstance().getUserInfo().getComp_name();
			if (TextUtils.isEmpty(value)
					|| value.equals(BaseParams.PARAM_DEFAULT)) {
				value = null;
			}
			break;
		}
		if (name == R.string.label_user_level) {
			int level = LoginManager.getInstance().getUserInfo()
					.getCredit_level();
			holder.refresh(name, level);
		} else {
			int valueColor = name == R.string.label_user_state ? Color.RED
					: mContext.getResources().getColor(R.color.color_333333);
			holder.refresh(name, value, valueColor);
		}
		return convertView;
	}

	public static class ViewHolder {

		@InjectView(R.id.item_name)
		TextView mName;
		@InjectView(R.id.item_value)
		TextView mValue;
		@InjectView(R.id.item_level)
		RatingBar mLevel;

		public ViewHolder(View parent) {
			ButterKnife.inject(this, parent);
		}

		public void refresh(int name, String value, int valueColor) {
			mName.setText(name);
			mValue.setText(value);
			mValue.setTextColor(valueColor);
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
}
