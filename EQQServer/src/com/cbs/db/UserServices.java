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
 * �û���һϵ�в��� ��¼ �����б� ������Ϣ ע�� ���Һ���
 * 
 * @author CBS
 * 
 */
public class UserServices {

	/**
	 * ��¼����
	 * 
	 * @param key
	 *            �ֻ��Ż�������
	 * @param password
	 *            ����
	 * @param sql
	 *            SQL��ѯ���
	 * @return ����uid
	 * @throws UsernameNotFoundException
	 *             �û��������쳣
	 * @throws PasswordException
	 *             �����쳣
	 * @throws StateException
	 *             ״̬�쳣
	 * @throws SQLException
	 *             SQL�쳣
	 */
	private String login(String key, String password, String sql)
			throws UsernameNotFoundException, PasswordException, StateException, SQLException {
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			// Ԥ����sql���
			PreparedStatement prs = conn.prepareStatement(sql);
			prs.setString(1, key);
			// ִ��sql��ѯ
			ResultSet set = prs.executeQuery();
			if (set.next()) {
				if (set.getInt("state") == 0) {
					if (set.getString("password").equals(password)) {
						// ����������ϵ�JSON
						// "{"uid":"","nickname":"","info":"","img":"","sex":"","name",""}"
						UserOnlineList.getUserOnlineList().JSONstr = "{\"uid\":\"" + set.getString("uid")
								+ "\",\"netname\":\"" + set.getString("netname") + "\",\"info\":\"" + set.getString("info")
								+ "\",\"img\":\"" + set.getString("image") + "\",\"sex\":\"" + set.getString("sex")
								+ "\",\"name\":\"" + set.getString("name") + "\"}";
						return set.getString(1);// ����uid
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
	 * ʹ�õ绰�����¼
	 * 
	 * @param phoneNumber
	 *            �ֻ���
	 * @param password
	 *            ����
	 * @param sql
	 *            SQL��ѯ���
	 * @return ����uid
	 * @throws UsernameNotFoundException
	 *             �û��������쳣
	 * @throws PasswordException
	 *             �����쳣
	 * @throws StateException
	 *             ״̬�쳣
	 * @throws SQLException
	 *             SQL�쳣
	 */
	public String loginByPhone(String phoneNumber, String password)
			throws UsernameNotFoundException, PasswordException, StateException, SQLException {
		String sql = "select * from useres where phoneNumber=?";
		return login(phoneNumber, password, sql);
	}

	/**
	 * ʹ�������¼
	 * 
	 * @param email
	 *            ����
	 * @param password
	 *            ����
	 * @param sql
	 *            SQL��ѯ���
	 * @return ����uid
	 * @throws UsernameNotFoundException
	 *             �û��������쳣
	 * @throws PasswordException
	 *             �����쳣
	 * @throws StateException
	 *             ״̬�쳣
	 * @throws SQLException
	 *             SQL�쳣
	 */
	public String loginByEmail(String email, String password)
			throws UsernameNotFoundException, PasswordException, StateException, SQLException {
		return login(email, password, "select * from useres where email=?");
	}

	/**
	 * ��ȡ�����б�����
	 * 
	 * @param uid
	 * @return �������ϼ���
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
	 * ��ȡ������Ϣ
	 * 
	 * @param uid
	 * @return ��������
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

			if (username.indexOf("@") >= 0) {// ����ע�� �����Զ�����������Ҫ����
				pre = conn.prepareStatement("INSERT INTO useres(email,PASSWORD,createtime,image) VALUES(?,?,SYSDATE(),?)");
			} else if (username.length() == 11) {
				pre = conn
						.prepareStatement("INSERT INTO useres(phonenumber,PASSWORD,createtime,image) VALUES(?,?,SYSDATE(),?)");
			}
			pre.setString(1, username);
			pre.setString(2, password);
			pre.setString(3, new Random().nextInt(40)+"");
			if (pre.executeUpdate() <= 0) {
				System.out.println("sql����");
				throw new SQLException();
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			conn.close();
		}
	}

	/**
	 * ���Һ��ѣ����߲��Ҹ�������
	 * 
	 * @param json
	 * @return ������Ϣ��JSON��
	 */
	public String findFriends(String jsonstr) {
		// JSON����ʽ "{"type":"number||netname","netname":"", || "number":""}"
		JSONObject json = JSONObject.fromObject(jsonstr);
		String type = json.getString("type");
		ResultSet set = null;
		Connection cnn = null;
		try {
			cnn = DBManager.getConnection();
			if ("netname".equals(type)) {// �����ֲ�ѯ
				String netname = json.getString("netname");
				// ģ����ѯҪ��ôд��\"%\"?\"%\"
				PreparedStatement pre = cnn.prepareStatement("select * from useres where netname like \"%\"?\"%\"");
				pre.setString(1, netname);
				set = pre.executeQuery();
			} else if ("number".equals(type)) {// ��ѯ���Ǻ������������
				String number = json.getString("number");
				PreparedStatement pre = cnn.prepareStatement("SELECT * FROM USERES WHERE phonenumber=? or email=? ");
				pre.setString(1, number);
				pre.setString(2, number);
				set = pre.executeQuery();
			}
			if (set.next()) {// ����JSON����ʽ
								// "{"state":"","uid":"","netname":"","image":"","isOnline":"","info":""}"
				boolean is =UserOnlineList.getUserOnlineList().onlineUser.containsKey(set.getString("uid"));
				String str = "{\"state\":1,\"uid\":\"" + set.getString("uid") + "\",\"netname\":\""
						+ set.getString("netname") + "\",\"image\":\"" + set.getString("image") + "\",\"isOnline\":\""
						+ is + "\",\"info\":\"" + set.getString("info") + "\"}";
				return str;
			} else {// û�ҵ�
				String str = "{\"state\":0}";
				return str;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "{\"state\":0}";
	}
	/**
	 * ��Ӻ���
	 * @param str
	 * @return
	 */
	public int addFeiend(String str) {
		// "{"myuid":"","hyuid":""}"
		JSONObject json = JSONObject.fromObject(str);
		String uid = json.getString("myuid");
		String hyuid = json.getString("hyuid");
		// ��ȡ���ݿ������
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
	 * �޸ĸ�������
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
