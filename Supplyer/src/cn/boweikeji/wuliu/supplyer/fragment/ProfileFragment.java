package cn.boweikeji.wuliu.supplyer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.boweikeji.wuliu.supplyer.Const;
import cn.boweikeji.wuliu.supplyer.activity.MainActivity;
import cn.boweikeji.wuliu.supplyer.bean.UserInfo;
import cn.boweikeji.wuliu.supplyer.manager.LoginManager;

import cn.boweikeji.wuliu.supplyer.R;

public class ProfileFragment extends BaseFragment {

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				if (getActivity() instanceof MainActivity) {
					((MainActivity) getActivity()).back();
				}
			}
		}
	};

	private View mRootView;

	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.profile_portrait)
	ImageView mPortrait;
	@InjectView(R.id.profile_name)
	TextView mName;
	@InjectView(R.id.profile_phone)
	TextView mPhone;
	@InjectView(R.id.profile_credit_level)
	RatingBar mCreditLevel;
	@InjectView(R.id.profile_id_number)
	TextView mIdNumber;
	@InjectView(R.id.profile_register_time)
	TextView mRegisterTime;

	private UserInfo mUserInfo;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_profile, null);
		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	private void init() {
		ButterKnife.inject(this, mRootView);
		initTitle();
		initView();
		initData();
	}

	private void initTitle() {
		mTitle.setText(R.string.title_profile);
		mMenuBtn.setImageResource(R.drawable.btn_title_back);
		mMenuBtn.setOnClickListener(mOnClickListener);
	}

	private void initView() {
		
	}
	
	private void initData() {
		mUserInfo = LoginManager.getInstance().getUserInfo();
		if (mUserInfo != null) {
			String supplyer_name = mUserInfo.getSupplyer_name();
			if (supplyer_name != null && !supplyer_name.equals(Const.NULL)) {
				mName.setText(supplyer_name);
			}
			mPhone.setText(mUserInfo.getPhone());
			mCreditLevel.setRating(Integer.parseInt(mUserInfo.getCredit_level() + 1));
			String card_id = mUserInfo.getCard_id();
			if (card_id != null && !card_id.equals(Const.NULL)) {
				mIdNumber.setText(card_id);
			}
			mRegisterTime.setText("2020-02-20 20:20:20");
		}
	}
}
