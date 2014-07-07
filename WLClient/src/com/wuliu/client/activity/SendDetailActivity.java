package com.wuliu.client.activity;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.wuliu.client.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SendDetailActivity extends Activity {

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				finish();
			} else if (view == mSendPay) {
				showPayTypeChooseDialog();
			} else if (view == mSendRule) {
				showRule();
			}
		}
	};

	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	@InjectView(R.id.send_from)
	EditText mSendFrom;
	@InjectView(R.id.send_phone)
	EditText mSendPhone;
	@InjectView(R.id.send_to)
	EditText mSendTo;
	@InjectView(R.id.to_phone)
	EditText mToPhone;
	@InjectView(R.id.send_pay)
	TextView mSendPay;
	@InjectView(R.id.id_number)
	EditText mIdNumber;
	@InjectView(R.id.send_comment)
	EditText mSendComment;
	@InjectView(R.id.publish)
	Button mPublish;
	@InjectView(R.id.send_accept)
	CheckBox mSendAccept;
	@InjectView(R.id.send_rule)
	TextView mSendRule;

	private String[] mPayTypeList;
	private int mPayTypeIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_detail);
		initView();
		initData();
	}

	private void initView() {
		ButterKnife.inject(this);
		mSendPay.setOnClickListener(mOnClickListener);
		mPublish.setOnClickListener(mOnClickListener);
		mSendRule.setOnClickListener(mOnClickListener);
		mTitle.setText(R.string.title_send);
		mMenuBtn.setImageResource(R.drawable.btn_title_back);
		mMenuBtn.setOnClickListener(mOnClickListener);
	}

	private void initData() {
		mPayTypeIndex = 0;
		mPayTypeList = getResources().getStringArray(R.array.pay_type_list);
		updatePayType();
	}

	private void updatePayType() {
		mSendPay.setText(mPayTypeList[mPayTypeIndex]);
	}
	
	private void showRule() {
		Intent intent = new Intent(this, RuleActivity.class);
		startActivity(intent);
	}

	private void showPayTypeChooseDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setSingleChoiceItems(mPayTypeList, mPayTypeIndex,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mPayTypeIndex = which;
								dialog.dismiss();
								updatePayType();
							}
						}).setTitle("¸¶¿î·½Ê½").create();
		dialog.show();
	}

}
