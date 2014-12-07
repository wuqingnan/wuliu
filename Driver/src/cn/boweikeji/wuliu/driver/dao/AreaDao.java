package cn.boweikeji.wuliu.driver.dao;

import java.sql.SQLException;
import java.util.List;
import cn.boweikeji.wuliu.driver.bean.Area;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

public class AreaDao extends BaseDaoImpl<Area, Integer> {

	public AreaDao(Class<Area> dataClass) throws SQLException {
		super(dataClass);
	}

	public AreaDao(ConnectionSource connectionSource, Class<Area> dataClass)
			throws SQLException {
		super(connectionSource, dataClass);
	}

	public AreaDao(ConnectionSource connectionSource,
			DatabaseTableConfig<Area> tableConfig) throws SQLException {
		super(connectionSource, tableConfig);
	}

	public List<Area> listByParentId(int parentId) {
		List<Area> list = null;
		try {
			list = queryForEq("parentId", parentId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
}
