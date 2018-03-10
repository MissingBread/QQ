package com.cbs.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 数据库管理对象
 * @author CBS
 *
 */
public class DBManager {
	
	public static final String DRIVER_NAME = "com.mysql.jdbc.Driver";
	public static final String USERNAME = "root";
	public static final String PASSWORD = "root";
	public static final String URL = "jdbc:mysql://127.0.0.1:3306/eq";
	public static DataSource dataSource = null;
	
	//静态代码块，准备加载数据源 C3P0
	static {
		try {
			ComboPooledDataSource pool=new ComboPooledDataSource();
			pool.setDataSourceName(DRIVER_NAME);
			pool.setUser(USERNAME);
			pool.setPassword(PASSWORD);
			pool.setJdbcUrl(URL);
			pool.setMaxPoolSize(30);
			pool.setMinPoolSize(5);
			dataSource=pool;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("数据库连接失败！");
		}
	}
	/**
	 * 公共方法获取数据库连接
	 * @return 数据库连接对象
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException{
		return dataSource.getConnection();
	}
}
