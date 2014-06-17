package com.wuliu.client.fragment;

import com.wuliu.client.R;
import com.wuliu.client.activity.MainActivity;
import com.wuliu.client.adapter.MenuAdapter;

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
		super.onListItemClick(l, v, position, id);
	}
	
	private void initHeader() {
		View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_menu_header, null);
		mMenuPhone = (TextView) view.findViewById(R.id.menu_phone);
		getListView().addHeaderView(view);
		mMenuPhone.setText("15810759237");
	}
	
	private void initAdapter() {
		mMenuAdapter = new MenuAdapter(getActivity(), ICONS, getResources()
				.getStringArray(R.array.menu_names));
		setListAdapter(mMenuAdapter);
	}

	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		// if (getActivity() instanceof SlidingActivity) {
		// SlidingActivity fca = (SlidingActivity) getActivity();
		// fca.switchContent(fragment);
		// }
	}

}
