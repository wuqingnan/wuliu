package cn.boweikeji.wuliu.driver.activity;

import cn.boweikeji.wuliu.driver.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BaseActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_up, R.anim.anim_no);
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.anim_no, R.anim.slide_down);
	}
	
}
