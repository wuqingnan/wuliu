package com.wuliu.client.supplyer.activity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.wuliu.client.supplyer.Const;
import com.wuliu.client.supplyer.R;
import com.wuliu.client.supplyer.WLApplication;
import com.wuliu.client.supplyer.WeakHandler;
import com.wuliu.client.supplyer.db.DBHelper;
import com.wuliu.client.supplyer.utils.DeviceInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class WelcomeActivity extends Activity {

	private static final String TAG = WelcomeActivity.class.getSimpleName();
	
	private static final String PREFERENCE_NAME = "preference";
	private static final String KEY_VERSION = "version";
	
	private static final int SLEEP_TIME = 1000;
	
	private static final int MSG_LOGO_FINISH = 1 << 0;
	
	@InjectView(R.id.new_feature)
	ViewPager mNewFeature;
	@InjectView(R.id.logo_layout)
	RelativeLayout mLogoLayout;
	
	private WelcomeHandler mHandler = null;
	
	private FeatureAdapter mAdapter;
	
	private boolean mNeedInit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		Const.init(this);
		DeviceInfo.init(this);
		initView();
		initData();
		mHandler = new WelcomeHandler(this);
		new Thread(new InitTask()).start();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void initView() {
		ButterKnife.inject(this);
	}
	
	private void initData() {
		SharedPreferences preference = getSharedPreferences(PREFERENCE_NAME, MODE_MULTI_PROCESS);
		String lastVersion = preference.getString(KEY_VERSION, null);
		String curVersion = DeviceInfo.getAppVersion();
		if (lastVersion == null || !lastVersion.equals(curVersion)) {
			mNeedInit = true;
		} else {
			mNeedInit = false;
		}
	}
	
	private void saveVersion() {
		SharedPreferences preference = getSharedPreferences(PREFERENCE_NAME, MODE_MULTI_PROCESS);
		Editor editor = preference.edit();
		editor.putString(KEY_VERSION, DeviceInfo.getAppVersion());
		editor.commit();
	}
	
	private void initDB() {
		DBHelper helper = ((WLApplication)getApplication()).getHelper();
		SQLiteDatabase db = helper.getReadableDatabase();
		db.close();
	}
	
	private void logoFinish() {
		if (mNeedInit) {
			showNewFeature();
		} else {
			enterApp();
		}
	}
	
	private void showNewFeature() {
		mAdapter = new FeatureAdapter(this);
		mNewFeature.setAdapter(mAdapter);
		mNewFeature.setVisibility(View.VISIBLE);
		mLogoLayout.setVisibility(View.GONE);
	}
	
	private void enterApp() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void onClick(View view) {
		enterApp();
	}
	
	private class InitTask implements Runnable {
		@Override
		public void run() {
			try {
				if (mNeedInit) {
					long start = System.currentTimeMillis();
					initDB();
					saveVersion();
					long time = System.currentTimeMillis() - start;
					if (time < SLEEP_TIME) {
						Thread.sleep(SLEEP_TIME - time);
					}
				} else {
					Thread.sleep(SLEEP_TIME);
				}
				mHandler.sendEmptyMessage(MSG_LOGO_FINISH);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	} 
	
	private static class WelcomeHandler extends WeakHandler<WelcomeActivity> {

		public WelcomeHandler(WelcomeActivity reference) {
			super(reference);
		}

		@Override
		public void handleMessage(WelcomeActivity t, Message msg) {
			switch (msg.what) {
			case MSG_LOGO_FINISH:
				t.logoFinish();
				break;
			}
		}
		
	}
	
	private class FeatureAdapter extends PagerAdapter {
		
		private final int[] VIEWS = {
			R.layout.new_feature0,
			R.layout.new_feature1,
			R.layout.new_feature2
		};
		
		private List<View> mViews = new ArrayList<View>();
		
		public FeatureAdapter(Context context) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = null;
			for (int i = 0; i < VIEWS.length; i++) {
				view = inflater.inflate(VIEWS[i], null);
				mViews.add(view);
			}
		}
		
		@Override
		public int getCount() {
			return mViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
		@Override
        public void destroyItem(ViewGroup container, int position, Object obj) {
            container.removeView((View) obj);
        }

        @Override
        public Object instantiateItem(View container, int position) {
            View view = mViews.get(position);
            ((ViewPager) container).addView(view, 0);
            return view;
        }
	}
}