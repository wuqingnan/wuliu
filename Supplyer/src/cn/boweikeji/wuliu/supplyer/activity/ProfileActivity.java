package cn.boweikeji.wuliu.supplyer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.supplyer.adapter.ProfileAdapter;
import cn.boweikeji.wuliu.supplyer.R;

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
		mHeader = getLayoutInflater().inflate(R.layout.menu_header,
				null);
		mPhone = (TextView) mHeader.findViewById(R.id.phone);
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
