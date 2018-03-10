package com.cbs.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import com.cbs.service.util.Config;
import com.cbs.service.util.MethordUtil;
import com.cbs.view.FriendsView;

/**
 * 建立通信的TCP连接
 * @author CBS
 *
 */
public class MsgService implements Runnable{
	private MsgService(){
		
	}
	
	private static MsgService msgService=new MsgService();
	
	public static MsgService getMsgService(){
		return msgService;
	}
	
	private Socket socket=null;
	private InputStream in=null;
	private OutputStream out=null;
	private FriendsView friendsView;//弹出消息

	public void setFriendsView(FriendsView friendsView) {
		this.friendsView = friendsView;
	}
	
	public void LinkServer(String uid) throws UnknownHostException, IOException{
		
		socket=new Socket(Config.IP, Config.MSG_PORT);
		
		in=socket.getInputStream();
		out=socket.getOutputStream();
		
		out.write(uid.getBytes());
		
		Thread t=new Thread(msgService);
		t.start();
		
	}
	
	public void run() {
		byte[] bytes;
		int len;
		try {
			while(true){
				bytes=new byte[1024];
				len = in.read(bytes);
				String command=new String(bytes,0,len);
				
				if("M0001".equals(command)){//添加好友消息
					len =in.read(bytes);
					String name=new String(bytes, 0, len);
					//传递用户名就好string
					int i=JOptionPane.showConfirmDialog(friendsView, "用户："+name+"，请求添加你为好友，是否同意？");
					if(i==0){
						//1表示同意添加
						out.write(1);
						//更新好友列表
						Config.update=true;
					}else{
						//0表示不同意
						out.write(0);
					}
				}else if("SF0001".equals(command)){//文件接收
					len =in.read(bytes);
					String name=new String(bytes, 0, len);
					int i=JOptionPane.showConfirmDialog(friendsView, "用户："+name+"，给您发来一个文件，是否接收？");
					if(i==0){
						//1表示同意添加
						out.write(1);
						
						//文件大小
						byte[] sizeBytes=new byte[512];
						int s=in.read(sizeBytes);
						String sSize=new String(sizeBytes,0,s);
						int size=Integer.parseInt(sSize);
						System.out.println("客户端接收到的大小："+size);
						
						
						
						
						len =in.read(bytes);
						String fileName=new String(bytes, 0, len);
						
						
						String file=new MethordUtil().saveFile(friendsView);
						
						String path=file+"\\"+fileName;
						System.out.println("是否已经接收文件名："+path);
						File f=new File(path);
						f.createNewFile();
						FileOutputStream fos=new FileOutputStream(f);
						
						byte[] fbytes=new byte[size];
						in.read(fbytes);
						System.out.println("是否已经接收"+fbytes.length);
						
						fos.write(fbytes);
						fos.flush();
						fos.close();
					}else{
						//0表示不同意
						out.write(0);
					}
					
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
