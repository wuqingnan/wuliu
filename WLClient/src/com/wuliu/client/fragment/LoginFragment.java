package com.wuliu.client.fragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.wuliu.client.R;
import com.wuliu.client.activity.MainActivity;
import com.wuliu.client.utils.Util;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends BaseFragment {

	private static final String TAG = LoginFragment.class.getSimpleName();

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				if (getActivity() instanceof MainActivity) {
					((MainActivity) getActivity())
							.onClickTitle(LoginFragment.this);
				}
			} else if (view == mLoginCheck) {
				if (Util.isPhoneNumber(mLoginNumber.getText().toString())) {
					Toast.makeText(getActivity(), R.string.phone_verify_code,
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), R.string.phone_format_error,
							Toast.LENGTH_SHORT).show();
				}
			} else if (view == mLoginSubmit) {
				String number = mLoginNumber.getText().toString();
				if (Util.isPhoneNumber(number)) {
					if (mLoginCode.getText().toString().equals("1234")) {
						if (getActivity() instanceof MainActivity) {
							((MainActivity) getActivity()).loginSuccess(number);
						}
					} else {
						Toast.makeText(getActivity(),
								R.string.verify_code_error, Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Toast.makeText(getActivity(), R.string.phone_format_error,
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	private TextWatcher mPhoneWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			mLoginCheck.setEnabled(s.length() == 11);
		}
	};

	private TextWatcher mCodeWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			mLoginSubmit.setEnabled(s.length() == 4
					&& mLoginNumber.length() == 11);
		}
	};

	private View mRootView;

	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.login_number)
	EditText mLoginNumber;
	@InjectView(R.id.login_check)
	TextView mLoginCheck;
	@InjectView(R.id.login_code)
	EditText mLoginCode;
	@InjectView(R.id.login_submit)
	TextView mLoginSubmit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_login, null);
		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	@Override
	public void onPause() {
		super.onPause();
		InputMethodManager manager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (manager.isActive()) {
			manager.hideSoftInputFromWindow(mRootView.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private void init() {
		ButterKnife.inject(this, mRootView);
		initTitle();
		initView();
	}

	private void initTitle() {
		mTitle.setText(R.string.title_login);
		mMenuBtn.setImageResource(R.drawable.btn_title_back);
		mMenuBtn.setOnClickListener(mOnClickListener);
	}

	private void initView() {
		mLoginCheck.setOnClickListener(mOnClickListener);
		mLoginSubmit.setOnClickListener(mOnClickListener);
		mLoginNumber.addTextChangedListener(mPhoneWatcher);
		mLoginCode.addTextChangedListener(mCodeWatcher);
		mLoginCheck.setEnabled(false);
		mLoginSubmit.setEnabled(false);
	}

}
