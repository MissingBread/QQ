package com.cbs.service.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Vector;

import com.cbs.view.ChatView;
import com.cbs.view.FacePanel;
import com.cbs.view.FriendsList;
import com.cbs.view.FriendsView;
import com.cbs.view.Msg;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 登录服务的辅助类 记录一些常用的常量或者变量
 * @author CBS
 *
 */
public class Config {
	//服务器地址，目前是本地主机地址
	public static final String IP="127.0.0.1";
	//登录的端口号  据说以后会有聊天端口号等
	public static final int LOGIN_PORT=9090;
	//添加好友服务器端口
	public static final int ADD_PORT=9091;
	//注册端口
	public static final int REG_PORT=4002;
	//信息传递端口
	public static final int MSG_PORT=9092;
	
	//寄存用户名和密码
	public static String username;
	public static String password;
	
	//好友列表JSON
	public static String friends_json="";
	//好友列表
	public static String friendsList="";
	//个人资料// "{"uid":"","nickname":"","info":"","img":"","sex":"","name",""}"
	public static String personalInfo="";
	//在线的好友
	public static String friendsOnline="";
	//在netService处使用使得好友时时更新
	public static FriendsList friendsListP;
	//UDP服务
	public static DatagramSocket socket=null;
	//聊天窗口的登记
	public static Hashtable<String,ChatView> table=new Hashtable<String, ChatView>(); 
	//登记头像闪动的面板
	public static Hashtable<String,FacePanel> list=new Hashtable<String, FacePanel>();
	//登录后的Socket
	public static Socket tcpSocket=null;
	//是否应该更新好友列表
	public static boolean update=false;
	//好友列表面板对象，用于更新
	public static FriendsView friendsView=null;
	
	/**
	 * 解析好友列表JSON
	 * @param friends_json
	 */
	public static void analysisJson(String friends_json){
		JSONArray json=JSONArray.fromObject(friends_json);
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<json.size();i++){
			//能否用json.getJSONObject()?
			JSONObject jsonObj=(JSONObject) json.get(i);
			sb.append(jsonObj.getString("uid"));
			sb.append(",");
		}
		friendsList=sb.toString();
	}
	/**
	 * 打开一个聊天窗口
	 * @param uid
	 * @param name
	 * @param info
	 * @param img
	 * @param vector 聊天信息的集合
	 */
	public static void getChatView(String uid,String name,String info,String img, Vector<Msg> vector){
		if(table.get(uid)==null){//
			ChatView chatView=new ChatView(uid, img, name, info,vector);
			table.put(uid, chatView);
		}else{
			table.get(uid).setAlwaysOnTop(true);
			table.get(uid).setVisible(true);
		}
	}
	public static void closeChatView(String uid){
		table.remove(uid);
	}
	/**
	 * 重新拉取个人资料
	 */
	public static void getSelfINfo(){
		
		InputStream in;
		try {
			in = tcpSocket.getInputStream();
			OutputStream out=tcpSocket.getOutputStream();
			byte[] bytes=new byte[512];
			
			out.write("U0003".getBytes());
			out.flush();
			int len = in.read(bytes);
			Config.personalInfo = new String(bytes, 0, len);
			System.out.println("个人资料：" + Config.personalInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
