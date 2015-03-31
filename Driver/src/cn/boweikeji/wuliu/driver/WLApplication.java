package cn.boweikeji.wuliu.driver;

import java.util.ArrayList;
import java.util.List;

import cn.boweikeji.wuliu.driver.aidl.ILocationListener;
import cn.boweikeji.wuliu.driver.aidl.IReportService;
import cn.boweikeji.wuliu.driver.aidl.WLLocation;
import cn.boweikeji.wuliu.driver.db.DBHelper;
import cn.boweikeji.wuliu.driver.service.ReportService;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class WLApplication extends Application {

	private static final String LOG_TAG = WLApplication.class.getSimpleName();
	
	private ILocationListener mLocationListener = new ILocationListener.Stub() {
		@Override
		public void onReceiveLocation()	throws RemoteException {
			Log.d(LOG_TAG, "shizy---onReceiveLocation");
			if (mListeners != null && mListeners.size() > 0) {
				for (ILocationListener listener : mListeners) {
					if (listener != null) {
						listener.onReceiveLocation();
					}
				}
			}
		}
	};
	
	private DBHelper mDBHelper;
	
	private ReportServiceConnection mConn;
	private static IReportService mReportService;
	private List<ILocationListener> mListeners;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG, "WLApplication.onCreate: " + this);
		startReportService();
	}
	
	public DBHelper getHelper() {
		if (mDBHelper == null) {
			mDBHelper = OpenHelperManager.getHelper(this, DBHelper.class);
		}
		return mDBHelper;
	}
	
	public void startReportService() {
        Log.d(LOG_TAG, "shizy---startRemoteService");
        Intent intent = new Intent(this, ReportService.class);
        startService(intent);
    }
	
	public void bindReportService() {
        Log.d(LOG_TAG, "shizy---bindReportService");
        if (mReportService != null) {
        	return;
        }
        mConn = new ReportServiceConnection();
        Intent intent = new Intent(this, ReportService.class);
        bindService(intent, mConn, Context.BIND_AUTO_CREATE);
    }

    public void unbindReportService() {
        Log.d(LOG_TAG, "shizy---unbindReportService");
        if (mConn != null) {
            unbindService(mConn);
            mReportService = null;
            mConn = null;
        }
    }

    public void addLocationListener(ILocationListener listener) {
    	if (listener == null) {
    		return;
    	}
    	if (mListeners == null) {
    		mListeners = new ArrayList<ILocationListener>();
    	}
    	mListeners.add(listener);
    }
    
    public void removeLocationListener(ILocationListener listener) {
    	if (mListeners != null && listener != null) {
    		mListeners.remove(listener);
    	}
    }
    
    public static void setUserCd(String userCd) {
    	if (mReportService != null) {
    		try {
				mReportService.setUserCd(userCd);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public static void setClientId(String clientId) {
    	if (mReportService != null) {
    		try {
				mReportService.setClientId(clientId);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public static void reportLocation() {
    	if (mReportService != null) {
    		try {
				mReportService.reportLocation();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public static BDLocation getLastKnownLocation() {
    	if (mReportService != null) {
    		try {
    			WLLocation location = mReportService.getLastKnownLocation();
    			if (location != null) {
    				return location.getBDLocation();
    			}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    	}
    	return null;
    }
	
	private class ReportServiceConnection implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mReportService = IReportService.Stub.asInterface(service);
			if (mReportService != null) {
				try {
					mReportService.setLocationListener(mLocationListener);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			if (mReportService != null) {
				try {
					mReportService.setLocationListener(null);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				mReportService = null;
			}
		}
		
	}
}
