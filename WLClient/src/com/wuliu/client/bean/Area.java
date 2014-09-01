package com.wuliu.client.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.wuliu.client.bean.dao.AreaDao;

@DatabaseTable(tableName = "area", daoClass = AreaDao.class)
public class Area {

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField(uniqueCombo = true)
	private int parentId;
	@DatabaseField(uniqueCombo = true, canBeNull = false)
	private String name;
	
	public Area() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return getName();
	}
}
