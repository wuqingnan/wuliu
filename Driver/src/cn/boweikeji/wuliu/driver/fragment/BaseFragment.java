﻿package cn.boweikeji.wuliu.driver.fragment;

import android.support.v4.app.Fragment;
import android.view.KeyEvent;

public class BaseFragment extends Fragment {
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

}