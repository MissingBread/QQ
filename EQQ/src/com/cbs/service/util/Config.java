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
 * ��¼����ĸ����� ��¼һЩ���õĳ������߱���
 * @author CBS
 *
 */
public class Config {
	//��������ַ��Ŀǰ�Ǳ���������ַ
	public static final String IP="127.0.0.1";
	//��¼�Ķ˿ں�  ��˵�Ժ��������˿ںŵ�
	public static final int LOGIN_PORT=9090;
	//��Ӻ��ѷ������˿�
	public static final int ADD_PORT=9091;
	//ע��˿�
	public static final int REG_PORT=4002;
	//��Ϣ���ݶ˿�
	public static final int MSG_PORT=9092;
	
	//�Ĵ��û���������
	public static String username;
	public static String password;
	
	//�����б�JSON
	public static String friends_json="";
	//�����б�
	public static String friendsList="";
	//��������// "{"uid":"","nickname":"","info":"","img":"","sex":"","name",""}"
	public static String personalInfo="";
	//���ߵĺ���
	public static String friendsOnline="";
	//��netService��ʹ��ʹ�ú���ʱʱ����
	public static FriendsList friendsListP;
	//UDP����
	public static DatagramSocket socket=null;
	//���촰�ڵĵǼ�
	public static Hashtable<String,ChatView> table=new Hashtable<String, ChatView>(); 
	//�Ǽ�ͷ�����������
	public static Hashtable<String,FacePanel> list=new Hashtable<String, FacePanel>();
	//��¼���Socket
	public static Socket tcpSocket=null;
	//�Ƿ�Ӧ�ø��º����б�
	public static boolean update=false;
	//�����б����������ڸ���
	public static FriendsView friendsView=null;
	
	/**
	 * ���������б�JSON
	 * @param friends_json
	 */
	public static void analysisJson(String friends_json){
		JSONArray json=JSONArray.fromObject(friends_json);
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<json.size();i++){
			//�ܷ���json.getJSONObject()?
			JSONObject jsonObj=(JSONObject) json.get(i);
			sb.append(jsonObj.getString("uid"));
			sb.append(",");
		}
		friendsList=sb.toString();
	}
	/**
	 * ��һ�����촰��
	 * @param uid
	 * @param name
	 * @param info
	 * @param img
	 * @param vector ������Ϣ�ļ���
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
	 * ������ȡ��������
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
			System.out.println("�������ϣ�" + Config.personalInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
