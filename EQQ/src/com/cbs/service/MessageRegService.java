package com.cbs.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.cbs.service.util.Config;

import net.sf.json.JSONObject;

/**
 * ��UDP������ע��˿ں�IP��ֹ�˿ڻ���
 * @author CBS
 *
 */
public class MessageRegService extends Thread{
	
	//UDP Socket
	private DatagramSocket socket=null;
	
	public MessageRegService(DatagramSocket socket){
		this.socket=socket;
		this.start();
	}
	
	//ÿʮ���������������һ��
	public void run(){
		//����Լ���uid
		String uid=JSONObject.fromObject(Config.personalInfo).getString("uid");
		String jsonStr="{\"type\":\"reg\",\"MyUID\":\""+uid+"\"}";
		byte[] bytes=jsonStr.getBytes();
		
		while(true){
			try {
				//��װ����
				DatagramPacket packet=new DatagramPacket(bytes,bytes.length,InetAddress.getByName(Config.IP),4003);
				//��������
				socket.send(packet);
				Thread.sleep(9999);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
}
