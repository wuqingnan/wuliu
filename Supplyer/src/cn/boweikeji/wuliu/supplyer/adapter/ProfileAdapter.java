package cn.boweikeji.wuliu.supplyer.adapter;

import cn.boweikeji.wuliu.supplyer.R;
import cn.boweikeji.wuliu.supplyer.api.BaseParams;
import cn.boweikeji.wuliu.supplyer.manager.LoginManager;
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

public class ProfileAdapter extends BaseAdapter {

	private static final int[] NAMES = { R.string.label_user_name,
			R.string.label_user_state, R.string.label_user_level,
			R.string.label_phone_no, R.string.label_user_type };

	private Context mContext;
	private LayoutInflater mInflater;

	private String[] mUserTypes;
	private String[] mUserStates;

	public ProfileAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mUserTypes = context.getResources().getStringArray(
				R.array.user_types);
		mUserStates = context.getResources().getStringArray(
				R.array.user_states);
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
			value = LoginManager.getInstance().getUserInfo().getSupplyer_name();
			if (TextUtils.isEmpty(value) || value.equals(BaseParams.PARAM_DEFAULT)) {
				value = null;
			}
			break;
		case R.string.label_user_state: {
			int state = LoginManager.getInstance().getUserInfo().getState();
			if (state >= 0 && state < mUserStates.length) {
				value = mUserStates[state];
			}
		}
			break;
		case R.string.label_phone_no:
			value = LoginManager.getInstance().getUserInfo().getPhone();
			break;
		case R.string.label_user_type: {
			int type = LoginManager.getInstance().getUserInfo()
					.getSupplyer_type();
			if (type >= 0 && type < mUserTypes.length) {
				value = mUserTypes[type];
			}
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
