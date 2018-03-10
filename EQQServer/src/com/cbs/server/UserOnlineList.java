package com.cbs.server;

import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

import com.util.User;

/**
 * �����û��б�
 * @author CBS
 *
 */
public class UserOnlineList {
	//1��������ֻ����һ��ʵ����
	//2������������Լ������Լ���Ψһʵ����
	//3�������������������������ṩ��һʵ����
	
	private UserOnlineList(){
		
	}
	
	private static UserOnlineList userOnlineList=new UserOnlineList();

	public static UserOnlineList getUserOnlineList() {
		return userOnlineList;
	}
	//��ź��ѵ��б�
	private HashMap<String,UserInfo> hashMap=new HashMap<String,UserInfo>();
	//������е������û�
	public HashMap<String, User> onlineUser=new HashMap<String, User>();
	//�������ϵ�JSON��
	public String JSONstr;
	/**
	 * ע����ѣ���Ӻ���
	 * @param uid	
	 * @param socket	
	 * @param email
	 * @param phoneNumber
	 */
	public void RegOnline(String uid, Socket socket, String email, String phoneNumber){
		
		UserInfo userInfo=hashMap.get(uid);
		//����û����ڣ��Ͱ��û��Ƴ�ȥ
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
	 * ���¿ͻ��˵�UDP��Ϣ
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
	 * �ж��Ƿ�����
	 * @param uid
	 * @return ���߷���true ���򷵻�FALSE
	 */
	public boolean isUserOnline(String uid){
		return hashMap.containsKey(uid);
	}
	/**
	 * �û�����
	 * @param uid
	 */
	public void loginOut(String uid){
		hashMap.remove(uid);
	}
	/**
	 * ��ȡ��Ϣ
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
