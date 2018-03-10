package com.cbs.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cbs.db.UserServices;
import com.util.ServerUtil;

import net.sf.json.JSONObject;

/**
 * ������Ӻ��ѺͲ��Һ��ѵķ�����
 * ͬʱ�������ļ����������
 * @author CBS
 *
 */
public class AddAndFileServer implements Runnable{
	
	private Socket socket;
	
	private AddAndFileServer(Socket socket){
		this.socket=socket;
	}
	
	
	public static void openServer() throws Exception{
		//����һ������
		@SuppressWarnings("resource")
		ServerSocket server=new ServerSocket(9091);
		//�̳߳�
		ExecutorService executors=Executors.newFixedThreadPool(2000);
		
		System.out.println("��ӷ���������...");
		
		while(true){
			Socket socket=server.accept();
//			server.setSoTimeout(30000);
			executors.execute(new AddAndFileServer(socket));
		}
	}
	
	public void run() {
		InputStream in=null;
		OutputStream out=null;
		
		try {
			in=socket.getInputStream();
			out=socket.getOutputStream();
			
			while(true){
				byte[] bytes=new byte[1024];
				int len=in.read(bytes);
				String command=new String(bytes,0,len);
				
				if("U0004".equals(command)){//��ѯ�û���Ϣ
					len=in.read(bytes);
					String jsonstr=new String(bytes, 0, len);
					jsonstr=new UserServices().findFriends(jsonstr);
					out.write(jsonstr.getBytes());
				}else if("U0005".equals(command)){//��Ӻ���
					len=in.read(bytes);
					String jsonstr=new String(bytes, 0, len);
					String addUid=JSONObject.fromObject(jsonstr).getString("hyuid");
					String name=JSONObject.fromObject(jsonstr).getString("netname");
					Socket hySocket=UserOnlineList.getUserOnlineList().onlineUser.get(addUid).msgSocket;
					if(send(hySocket,name)==1){
						int i=new UserServices().addFeiend(jsonstr);
						out.write(i);
					}else{
						out.write(-1);
					}
				}else if("F0001".equals(command)){//�ļ���������
					out.write(1);
					len=in.read(bytes);
					String jsonstr=new String(bytes, 0, len);
					JSONObject json=JSONObject.fromObject(jsonstr);
					String addUid=json.getString("hyuid");
					String name=json.getString("netname");
					Socket hySocket=UserOnlineList.getUserOnlineList().onlineUser.get(addUid).msgSocket;
					
					//���ͽ����ļ���ȷ����Ϣ
					int flag=ServerUtil.sendFileMsg(hySocket, name);
					
					if(flag==1){//ͬ�ⷢ��
						out.write(1);//��ʾͬ�ⷢ��
						String fileName=json.getString("filename");
						len=in.read(bytes);
						String s=new String(bytes,0,len);
						int size=Integer.parseInt(s);
						byte[] fbytes=new byte[size];
						in.read(fbytes);
						out.write(1);
						ServerUtil.sendFile(hySocket,fbytes,size,fileName);
					}else{//��ͬ�ⷢ�ͣ�������Ϣ
						
					}
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����ȷ����Ϣ
	 * @param hySocket Socket
	 * @param name �����ߵ�����
	 * @return
	 */
	private int send(Socket hySocket,String name) {
		
		InputStream in;
		OutputStream out;
		try {
			in = hySocket.getInputStream();
			out=hySocket.getOutputStream();
			
			out.write("M0001".getBytes());
			System.out.println(UserOnlineList.getUserOnlineList().JSONstr);
			out.write(name.getBytes());
			int i=in.read();
			return i;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	
	
}
