package cn.boweikeji.wuliu.supplyer.activity;

import com.umeng.analytics.MobclickAgent;

import cn.boweikeji.wuliu.supplyer.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public class BaseActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		overridePendingTransition(R.anim.slide_up, R.anim.anim_no);
	}

	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.anim_no, R.anim.slide_down);
	}

}
