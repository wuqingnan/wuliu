package cn.boweikeji.wuliu.supplyer.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cn.boweikeji.wuliu.supplyer.bean.Area;
import cn.boweikeji.wuliu.supplyer.dao.AreaDao;
import cn.boweikeji.wuliu.supplyer.manager.AreaManager;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DBHelper extends OrmLiteSqliteOpenHelper {

	private static final String TAG = DBHelper.class.getSimpleName();
	
	private static final String DB_NAME = "wuliu.db";
	private static final int DB_VERSION = 1;
	
	private Context mContext;
	
	private AreaDao mAreaDao = null;

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		Log.d(TAG, "shizy---DBHelper.onCreate");
		try {
			TableUtils.createTable(connectionSource, Area.class);
			AreaManager.cityToDatabase(getAreaDao(), mContext);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion,
			int newVersion) {

	}
	
	@Override
	public void close() {
		super.close();
		mAreaDao = null;
	}

	public AreaDao getAreaDao() throws SQLException {
		if (mAreaDao == null) {
			mAreaDao = getDao(Area.class);
		}
		return mAreaDao;
	}
}
