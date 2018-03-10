package com.cbs.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.cbs.service.util.Config;
import com.cbs.service.util.MethordUtil;

/**
 * 负责查询信息的辅助类，用于连接ADDServer
 * 文件传输的通信也在这里负责
 * @author CBS
 *
 */
public class FindService {//implements Runnable
	
	private FindService(){
		
	}
	
	private static FindService findServise=new FindService();
	
	public static FindService  getFindServise(){
		return findServise;
	}
	
	private Socket socket;
	private InputStream in=null;
	private OutputStream out=null;
	
	public void LinkAddServer() throws UnknownHostException, IOException{
		//创建客户端
		socket=new Socket(Config.IP, Config.ADD_PORT);
		
		in=socket.getInputStream();
		out=socket.getOutputStream();
	}
	
	/**
	 * 查找好友
	 * @param json	请求的json串
	 * @return	返回json 串
	 */
	public String find(String json){
		try {
			out.write("U0004".getBytes());
			//向服务器发送请求信息
			out.write(json.getBytes());
			
			System.out.println("Findservice:"+json);
			
			byte[] bytes =new byte[1024];
			//这里在读取信息，线程里面也在读取。。。。
			int len=in.read(bytes);
			if(len==-1){
				return null;
			}else{
				String str=new String(bytes,0,len);
				return str;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 添加好友
	 * @param json
	 */
	public void addFriend(String json){//"{"myuid":"","hyuid":"","netname":""}"
		try {
			//U0005添加好友
			out.write("U0005".getBytes());
			//向服务器发送请求信息
			out.write(json.getBytes());
			
			int i=in.read();
			if(i!=-1){//更新好友列表
				Config.update=true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 发送文件
	 * @param path 文件路径
	 * @param json 发送的json信息
	 */
	public void sendFile(File file,String json){//{"myuid":"","hyuid":"","netname":"","filename":""}
		try {
			out.write("F0001".getBytes());
			in.read();
			out.write(json.getBytes());
			int flag=in.read();
			if(flag==1){
				byte[] bytes=MethordUtil.readFile(file);
				//发送文件
				Integer size=bytes.length;
				out.write(size.toString().getBytes());
				out.flush();
				out.write(bytes);
				out.flush();
				in.read();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
