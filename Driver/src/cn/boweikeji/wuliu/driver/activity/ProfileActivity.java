package cn.boweikeji.wuliu.driver.activity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.manager.LoginManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class ProfileActivity extends BaseActivity {

	private static final String TAG = ProfileActivity.class.getSimpleName();

	private static final String KEY_GOODS_CD = "goods_cd";

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mBack) {
				finish();
			}
		}
	};

	@InjectView(R.id.titlebar_leftBtn)
	ImageView mBack;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.profile)
	ListView mListView;

	private View mHeader;
	private TextView mName;
	private TextView mPhone;

	private ProfileAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		initView();
	}

	private void initView() {
		ButterKnife.inject(this);
		mTitle.setText(R.string.title_order_detail);
		mBack.setOnClickListener(mOnClickListener);
		initHeader();
		initList();
	}

	private void initHeader() {
		mHeader = getLayoutInflater().inflate(R.layout.fragment_more_header,
				null);
		mName = (TextView) mHeader.findViewById(R.id.name);
		mPhone = (TextView) mHeader.findViewById(R.id.phone);
		mName.setVisibility(View.GONE);
		mPhone.setVisibility(View.GONE);
		mHeader.setBackgroundColor(getResources().getColor(R.color.bg_back));
	}

	private void initList() {
		mListView.addHeaderView(mHeader);
		mAdapter = new ProfileAdapter(this);
		mListView.setAdapter(mAdapter);
	}

	public static class ProfileAdapter extends BaseAdapter {

		private static final int[] NAMES = {
			R.string.label_user_name,
			R.string.label_user_level,
			R.string.label_phone_no,
			R.string.label_id_no,
			R.string.label_user_type,
			R.string.label_area_code,
			R.string.label_truck_type,
			R.string.label_truck_no,
			R.string.label_truck_load,
			R.string.label_user_company
		};
		
		private Context mContext;
		private LayoutInflater mInflater;
		
		private String[] mDriverTypes;
		private String[] mTruckTypes;

		public ProfileAdapter(Context context) {
			mContext = context;
			mInflater = LayoutInflater.from(context);
			mDriverTypes = context.getResources().getStringArray(R.array.driver_types);
			mTruckTypes = context.getResources().getStringArray(R.array.truck_type_list);
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
			case R.string.label_phone_no:
				value = LoginManager.getInstance().getUserInfo().getPhone();
				break;
			case R.string.label_id_no:
				value = LoginManager.getInstance().getUserInfo().getCard_id();
				break;
			case R.string.label_user_type:
			{
				int type = LoginManager.getInstance().getUserInfo().getDriver_type();
				if (type >= 0 && type < mDriverTypes.length) {
					value = mDriverTypes[type];
				}
			}
				break;
			case R.string.label_area_code:
				break;
			case R.string.label_truck_type:
			{
				int type = LoginManager.getInstance().getUserInfo().getTruck_type_code();
				if (type >= 0 && type < mTruckTypes.length) {
					value = mTruckTypes[type];
				}
			}
				break;
			case R.string.label_truck_no:
				break;
			case R.string.label_truck_load:
				break;
			}
			if (name == R.string.label_user_level) {
				int level = LoginManager.getInstance().getUserInfo().getCredit_level();
				holder.refresh(name, level);
			} else {
				holder.refresh(name, value);
			}
			return convertView;
		}

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
		
		public void refresh(int name, String value) {
			mName.setText(name);
			mValue.setText(value);
			mValue.setVisibility(View.VISIBLE);
			mLevel.setVisibility(View.GONE);
		}
		
		public void refresh(int name, int level) {
			mName.setText(name);
			mLevel.setRating(level >= 0 ? level : 0);
			mValue.setVisibility(View.GONE);
			mLevel.setVisibility(View.VISIBLE);
		}
		
	}

	public static void startProfileActivity(Context context) {
		context.startActivity(new Intent(context, ProfileActivity.class));
	}
}
