package cn.boweikeji.wuliu.supplyer.activity;

import butterknife.ButterKnife;
import butterknife.InjectView;

import cn.boweikeji.wuliu.supplyer.R;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class RuleActivity extends BaseActivity {

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view == mMenuBtn) {
				finish();
			}
		}
	};
	
	@InjectView(R.id.titlebar_leftBtn)
	ImageView mMenuBtn;
	@InjectView(R.id.titlebar_title)
	TextView mTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rule);
		initView();
	}
	
	private void initView() {
		ButterKnife.inject(this);
		mTitle.setText(R.string.title_rule);
		mMenuBtn.setImageResource(R.drawable.btn_title_back);
		mMenuBtn.setOnClickListener(mOnClickListener);
	}
}
