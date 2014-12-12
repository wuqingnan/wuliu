package cn.boweikeji.wuliu.supplyer.fragment;

import com.umeng.analytics.MobclickAgent;

import android.support.v4.app.Fragment;
import android.view.KeyEvent;

public class BaseFragment extends Fragment {

	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass().getSimpleName());
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getSimpleName());
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

}
