package com.cbs.server;

import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

import com.util.User;

/**
 * 在线用户列表
 * @author CBS
 *
 */
public class UserOnlineList {
	//1、单例类只能有一个实例。
	//2、单例类必须自己创建自己的唯一实例。
	//3、单例类必须给所有其他对象提供这一实例。
	
	private UserOnlineList(){
		
	}
	
	private static UserOnlineList userOnlineList=new UserOnlineList();

	public static UserOnlineList getUserOnlineList() {
		return userOnlineList;
	}
	//存放好友的列表
	private HashMap<String,UserInfo> hashMap=new HashMap<String,UserInfo>();
	//存放所有的在线用户
	public HashMap<String, User> onlineUser=new HashMap<String, User>();
	//个人资料的JSON串
	public String JSONstr;
	/**
	 * 注册好友，添加好友
	 * @param uid	
	 * @param socket	
	 * @param email
	 * @param phoneNumber
	 */
	public void RegOnline(String uid, Socket socket, String email, String phoneNumber){
		
		UserInfo userInfo=hashMap.get(uid);
		//如果用户存在，就把用户移出去
		if(userInfo!=null){
			try {
				userInfo.getSocket().getOutputStream().write(4);
				Thread.sleep(3000);
				userInfo.getSocket().close();
			} catch (Exception e) {
				
			}
		}
		
		userInfo=new UserInfo(); 
		userInfo.setEmail(email);
		userInfo.setPhone(phoneNumber);
		userInfo.setUid(uid);
		userInfo.setSocket(socket);
		hashMap.put(uid, userInfo);
	}
	/**
	 * 更新客户端的UDP信息
	 * @param uid
	 * @param ip
	 * @param port
	 */
	public  void updataUserUDP(String uid,String ip,int port){
		
		UserInfo u=hashMap.get(uid);
		if(u!=null){
			u.setUdpip(ip);
			u.setUdpPort(port);
		}
		
	}
	/**
	 * 判断是否在线
	 * @param uid
	 * @return 在线返回true 否则返回FALSE
	 */
	public boolean isUserOnline(String uid){
		return hashMap.containsKey(uid);
	}
	/**
	 * 用户下线
	 * @param uid
	 */
	public void loginOut(String uid){
		hashMap.remove(uid);
	}
	/**
	 * 获取信息
	 * @param uid
	 * @return UserInfo
	 */
	public UserInfo getOnlineUserInfo (String uid){
		return hashMap.get(uid);
	}
	
	public Set<String> getOnlineUsers(){
		return hashMap.keySet();
	}
	
}
