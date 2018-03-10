package com.cbs.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 用于传递消息的服务器
 * @author CBS
 *
 */
public class MsgServer implements Runnable{
	private Socket socket=null;
	
	private MsgServer(Socket socket){
		this.socket=socket;
	}
	
	public static void openServer()throws Exception{
		@SuppressWarnings("resource")
		ServerSocket server=new ServerSocket(9092);
		ExecutorService executors=Executors.newFixedThreadPool(2000);
		
		while(true){
			Socket socket=server.accept();
			
			executors.execute(new MsgServer(socket));
		}
	}

	public void run() {
		try {
			InputStream in=socket.getInputStream();
			byte[] bytes=new byte[1024];
			int len=in.read(bytes);
			String str=new String(bytes,0,len);
			System.out.println("MsgServer>>"+str+UserOnlineList.getUserOnlineList().onlineUser.containsKey(str));
			UserOnlineList.getUserOnlineList().onlineUser.get(str).msgSocket=socket;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
