package com.cbs.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.Vector;

import com.cbs.server.UserOnlineList;

import net.sf.json.JSONObject;

/**
 * 用户的一系列操作 登录 好友列表 个人信息 注册 查找好友
 * 
 * @author CBS
 * 
 */
public class UserServices {

	/**
	 * 登录方法
	 * 
	 * @param key
	 *            手机号或者邮箱
	 * @param password
	 *            密码
	 * @param sql
	 *            SQL查询语句
	 * @return 返回uid
	 * @throws UsernameNotFoundException
	 *             用户不存在异常
	 * @throws PasswordException
	 *             密码异常
	 * @throws StateException
	 *             状态异常
	 * @throws SQLException
	 *             SQL异常
	 */
	private String login(String key, String password, String sql)
			throws UsernameNotFoundException, PasswordException, StateException, SQLException {
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			// 预编译sql语句
			PreparedStatement prs = conn.prepareStatement(sql);
			prs.setString(1, key);
			// 执行sql查询
			ResultSet set = prs.executeQuery();
			if (set.next()) {
				if (set.getInt("state") == 0) {
					if (set.getString("password").equals(password)) {
						// 保存个人资料的JSON
						// "{"uid":"","nickname":"","info":"","img":"","sex":"","name",""}"
						UserOnlineList.getUserOnlineList().JSONstr = "{\"uid\":\"" + set.getString("uid")
								+ "\",\"netname\":\"" + set.getString("netname") + "\",\"info\":\"" + set.getString("info")
								+ "\",\"img\":\"" + set.getString("image") + "\",\"sex\":\"" + set.getString("sex")
								+ "\",\"name\":\"" + set.getString("name") + "\"}";
						return set.getString(1);// 返回uid
					} else {
						throw new PasswordException();
					}
				} else {
					throw new StateException();
				}
			} else {
				throw new UsernameNotFoundException();
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			conn.close();
		}
	}

	/**
	 * 使用电话号码登录
	 * 
	 * @param phoneNumber
	 *            手机号
	 * @param password
	 *            密码
	 * @param sql
	 *            SQL查询语句
	 * @return 返回uid
	 * @throws UsernameNotFoundException
	 *             用户不存在异常
	 * @throws PasswordException
	 *             密码异常
	 * @throws StateException
	 *             状态异常
	 * @throws SQLException
	 *             SQL异常
	 */
	public String loginByPhone(String phoneNumber, String password)
			throws UsernameNotFoundException, PasswordException, StateException, SQLException {
		String sql = "select * from useres where phoneNumber=?";
		return login(phoneNumber, password, sql);
	}

	/**
	 * 使用邮箱登录
	 * 
	 * @param email
	 *            邮箱
	 * @param password
	 *            密码
	 * @param sql
	 *            SQL查询语句
	 * @return 返回uid
	 * @throws UsernameNotFoundException
	 *             用户不存在异常
	 * @throws PasswordException
	 *             密码异常
	 * @throws StateException
	 *             状态异常
	 * @throws SQLException
	 *             SQL异常
	 */
	public String loginByEmail(String email, String password)
			throws UsernameNotFoundException, PasswordException, StateException, SQLException {
		return login(email, password, "select * from useres where email=?");
	}

	/**
	 * 获取好友列表资料
	 * 
	 * @param uid
	 * @return 好友资料集合
	 * @throws SQLException
	 */
	public Vector<FriendListInfo> getFriendList(String uid) throws SQLException {
		String sql = "SELECT u.`uid`,u.`image`,u.`netname`,u.`info` FROM friends h"
				+ " INNER JOIN useres u ON u.`uid`=h.`hyuid` AND h.`uid`=?";
		Connection cnn = null;
		try {
			cnn = DBManager.getConnection();
			PreparedStatement pre = cnn.prepareStatement(sql);
			pre.setString(1, uid);
			ResultSet res = pre.executeQuery();
			Vector<FriendListInfo> vector = new Vector<FriendListInfo>();
			while (res.next()) {
				FriendListInfo f = new FriendListInfo();
				f.setUid(res.getString(1));
				f.setImg(res.getString(2));
				f.setNetname(res.getString(3));
				f.setInfo(res.getString(4));
				vector.add(f);
			}
			return vector;
		} catch (SQLException e) {
			throw e;
		} finally {
			cnn.close();
		}
	}

	/**
	 * 获取个人信息
	 * 
	 * @param uid
	 * @return 个人资料
	 * @throws SQLException
	 */
	public UserInfo getUserInfo(String uid) throws SQLException {

		String sql = "SELECT * FROM USERES WHERE UID=?";
		Connection cnn = null;
		try {
			cnn = DBManager.getConnection();
			PreparedStatement pre = cnn.prepareStatement(sql);
			pre.setString(1, uid);
			ResultSet rs = pre.executeQuery();
			UserInfo u = new UserInfo();
			if (rs.next()) {
				u.setUid(rs.getString("uid"));
				u.setPhonenumber(rs.getString("phonenumber"));
				u.setEmail(rs.getString("email"));
				u.setNetname(rs.getString("netname"));
				u.setInfo(rs.getString("info"));
				u.setName(rs.getString("name"));
				u.setImg(rs.getString("image"));
				u.setBack(rs.getString("back"));
				u.setSex(rs.getString("sex"));
				u.setYy(rs.getInt("yy"));
				u.setMm(rs.getInt("mm"));
				u.setDd(rs.getInt("dd"));
			}
			return u;
		} catch (SQLException e) {
			throw e;
		} finally {
			cnn.close();
		}
	}

	@SuppressWarnings("resource")
	public void regUser(String username, String password) throws UsernameException, SQLException {
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			PreparedStatement pre = conn.prepareStatement("SELECT * FROM USERES WHERE phonenumber=? or email=?");
			pre.setString(1, username);
			pre.setString(2, username);
			ResultSet res = pre.executeQuery();
			if (res.next()) {
				throw new UsernameException();
			}

			if (username.indexOf("@") >= 0) {// 邮箱注册 主键自动增长，不需要插入
				pre = conn.prepareStatement("INSERT INTO useres(email,PASSWORD,createtime,image) VALUES(?,?,SYSDATE(),?)");
			} else if (username.length() == 11) {
				pre = conn
						.prepareStatement("INSERT INTO useres(phonenumber,PASSWORD,createtime,image) VALUES(?,?,SYSDATE(),?)");
			}
			pre.setString(1, username);
			pre.setString(2, password);
			pre.setString(3, new Random().nextInt(40)+"");
			if (pre.executeUpdate() <= 0) {
				System.out.println("sql错误");
				throw new SQLException();
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			conn.close();
		}
	}

	/**
	 * 查找好友，或者查找个人资料
	 * 
	 * @param json
	 * @return 好友信息的JSON串
	 */
	public String findFriends(String jsonstr) {
		// JSON串格式 "{"type":"number||netname","netname":"", || "number":""}"
		JSONObject json = JSONObject.fromObject(jsonstr);
		String type = json.getString("type");
		ResultSet set = null;
		Connection cnn = null;
		try {
			cnn = DBManager.getConnection();
			if ("netname".equals(type)) {// 用名字查询
				String netname = json.getString("netname");
				// 模糊查询要这么写：\"%\"?\"%\"
				PreparedStatement pre = cnn.prepareStatement("select * from useres where netname like \"%\"?\"%\"");
				pre.setString(1, netname);
				set = pre.executeQuery();
			} else if ("number".equals(type)) {// 查询的是号码或者是邮箱
				String number = json.getString("number");
				PreparedStatement pre = cnn.prepareStatement("SELECT * FROM USERES WHERE phonenumber=? or email=? ");
				pre.setString(1, number);
				pre.setString(2, number);
				set = pre.executeQuery();
			}
			if (set.next()) {// 返回JSON串格式
								// "{"state":"","uid":"","netname":"","image":"","isOnline":"","info":""}"
				boolean is =UserOnlineList.getUserOnlineList().onlineUser.containsKey(set.getString("uid"));
				String str = "{\"state\":1,\"uid\":\"" + set.getString("uid") + "\",\"netname\":\""
						+ set.getString("netname") + "\",\"image\":\"" + set.getString("image") + "\",\"isOnline\":\""
						+ is + "\",\"info\":\"" + set.getString("info") + "\"}";
				return str;
			} else {// 没找到
				String str = "{\"state\":0}";
				return str;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "{\"state\":0}";
	}
	/**
	 * 添加好友
	 * @param str
	 * @return
	 */
	public int addFeiend(String str) {
		// "{"myuid":"","hyuid":""}"
		JSONObject json = JSONObject.fromObject(str);
		String uid = json.getString("myuid");
		String hyuid = json.getString("hyuid");
		// 获取数据库的连接
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			// insert into friends (uid,hyuid) values (2,5);
			PreparedStatement pre = conn.prepareStatement("insert into friends (uid,hyuid) values (?,?)");
			pre.setString(1, uid);
			pre.setString(2, hyuid);
			PreparedStatement pre1 = conn.prepareStatement("insert into friends (uid,hyuid) values (?,?)");
			pre1.setString(1, hyuid);
			pre1.setString(2, uid);
			int i = pre.executeUpdate();
			int i1 = pre1.executeUpdate();
			if (i > 0 && i1 > 0) {
				System.out.println(i+","+i1);
				return 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * 修改个人资料
	 * @param jsonstr
	 * @return 
	 */
	public static int updateSelfInfo(String jsonstr) {
		Connection conn=null;
		JSONObject json=JSONObject.fromObject(jsonstr);
		try {
			conn=DBManager.getConnection();
			PreparedStatement pre=conn.prepareStatement("update useres set netname=?,info=?,yy=?,mm=?,dd=?,name=?,sex=?,back=? where uid=?;");
			pre.setString(1, json.getString("netname"));
			pre.setString(2, json.getString("info"));
			pre.setString(3, json.getString("yy"));
			pre.setString(4, json.getString("mm"));
			pre.setString(5, json.getString("dd"));
			pre.setString(6, json.getString("name"));
			pre.setString(7, json.getString("sex"));
			pre.setString(8, json.getString("back"));
			pre.setString(9, json.getString("uid"));
			int i=pre.executeUpdate();
			return i;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
}
