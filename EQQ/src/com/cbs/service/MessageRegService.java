package com.cbs.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.cbs.service.util.Config;

import net.sf.json.JSONObject;

/**
 * 向UDP服务器注册端口和IP防止端口回收
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
	
	//每十秒钟向服务器心跳一下
	public void run(){
		//获得自己的uid
		String uid=JSONObject.fromObject(Config.personalInfo).getString("uid");
		String jsonStr="{\"type\":\"reg\",\"MyUID\":\""+uid+"\"}";
		byte[] bytes=jsonStr.getBytes();
		
		while(true){
			try {
				//包装数据
				DatagramPacket packet=new DatagramPacket(bytes,bytes.length,InetAddress.getByName(Config.IP),4003);
				//发送数据
				socket.send(packet);
				Thread.sleep(9999);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
}
