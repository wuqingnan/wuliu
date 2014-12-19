package cn.boweikeji.wuliu.driver.activity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.driver.R;
import cn.boweikeji.wuliu.driver.adapter.ProfileAdapter;
import cn.boweikeji.wuliu.driver.manager.LoginManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
		mTitle.setText(R.string.title_profile);
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

	public static void startProfileActivity(Context context) {
		context.startActivity(new Intent(context, ProfileActivity.class));
	}
}
