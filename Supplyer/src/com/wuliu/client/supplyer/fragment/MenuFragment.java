package com.wuliu.client.supplyer.fragment;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.wuliu.client.supplyer.R;
import com.wuliu.client.supplyer.activity.MainActivity;
import com.wuliu.client.supplyer.adapter.MenuAdapter;
import com.wuliu.client.supplyer.manager.LoginManager;
import com.wuliu.client.supplyer.utils.Util;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class MenuFragment extends ListFragment {

	private static final String TAG = MenuFragment.class.getSimpleName();

	private static final int[] ICONS = new int[] { R.drawable.icon_order,
			R.drawable.icon_account, R.drawable.icon_share, R.drawable.icon_share,
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

	private void showShare() {
        ShareSDK.initSDK(getActivity());
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        
        // 分享时Notification的图标和文字
        oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(getActivity());
   }
	
	private void switchFragment(int position) {
		if (getActivity() == null) {
			return;
		}
		BaseFragment fragment = null;
		switch (position) {
		case 0:
			fragment = new OrderFragment();
			break;
		case 1:
			fragment = new ProfileFragment();
			break;
		case 2:
			Util.sendMessage(getActivity(), null, getResources().getString(R.string.invite_msg));
			break;
		case 3:
			showShare();
			break;
		case 4:
			fragment = new SetFragment();
			break;
		default:
			break;
		}
		if (fragment != null) {
			((MainActivity) getActivity()).switchContent(fragment);
		}
	}
}
