package com.wuliu.client.fragment;

import com.wuliu.client.R;
import com.wuliu.client.activity.MainActivity;
import com.wuliu.client.adapter.MenuAdapter;
import com.wuliu.client.manager.LoginManager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MenuFragment extends ListFragment {

	private static final String TAG = MenuFragment.class.getSimpleName();

	private static final int[] ICONS = new int[] { R.drawable.icon_order,
			R.drawable.icon_account, R.drawable.icon_share,
			R.drawable.icon_setting };

	private MenuAdapter mMenuAdapter;

	private TextView mMenuPhone;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_menu, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initHeader();
		initAdapter();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		switchFragment((int)id);
	}

	private void initHeader() {
		View view = getActivity().getLayoutInflater().inflate(
				R.layout.fragment_menu_header, null);
		mMenuPhone = (TextView) view.findViewById(R.id.menu_phone);
		getListView().addHeaderView(view);
	}

	private void initAdapter() {
		mMenuAdapter = new MenuAdapter(getActivity(), ICONS, getResources()
				.getStringArray(R.array.menu_names));
		setListAdapter(mMenuAdapter);
	}

	public void updateInfo() {
		mMenuPhone.setText(LoginManager.getInstance().getUserInfo().getPhone());
	}

	private void switchFragment(int position) {
		if (getActivity() == null) {
			return;
		}
		switch (position) {
		case 3:
			SetFragment fragment = new SetFragment();
			((MainActivity) getActivity()).switchContent(fragment);
			break;
		default:
			break;
		}
	}
}
