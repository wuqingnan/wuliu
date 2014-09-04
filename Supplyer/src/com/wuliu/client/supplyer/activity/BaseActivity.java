package com.wuliu.client.supplyer.activity;

import com.wuliu.client.supplyer.R;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {

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
