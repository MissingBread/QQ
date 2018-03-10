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
 * ����ͨ�ŵ�TCP����
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
	private FriendsView friendsView;//������Ϣ

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
				
				if("M0001".equals(command)){//��Ӻ�����Ϣ
					len =in.read(bytes);
					String name=new String(bytes, 0, len);
					//�����û����ͺ�string
					int i=JOptionPane.showConfirmDialog(friendsView, "�û���"+name+"�����������Ϊ���ѣ��Ƿ�ͬ�⣿");
					if(i==0){
						//1��ʾͬ�����
						out.write(1);
						//���º����б�
						Config.update=true;
					}else{
						//0��ʾ��ͬ��
						out.write(0);
					}
				}else if("SF0001".equals(command)){//�ļ�����
					len =in.read(bytes);
					String name=new String(bytes, 0, len);
					int i=JOptionPane.showConfirmDialog(friendsView, "�û���"+name+"����������һ���ļ����Ƿ���գ�");
					if(i==0){
						//1��ʾͬ�����
						out.write(1);
						
						//�ļ���С
						byte[] sizeBytes=new byte[512];
						int s=in.read(sizeBytes);
						String sSize=new String(sizeBytes,0,s);
						int size=Integer.parseInt(sSize);
						System.out.println("�ͻ��˽��յ��Ĵ�С��"+size);
						
						
						
						
						len =in.read(bytes);
						String fileName=new String(bytes, 0, len);
						
						
						String file=new MethordUtil().saveFile(friendsView);
						
						String path=file+"\\"+fileName;
						System.out.println("�Ƿ��Ѿ������ļ�����"+path);
						File f=new File(path);
						f.createNewFile();
						FileOutputStream fos=new FileOutputStream(f);
						
						byte[] fbytes=new byte[size];
						in.read(fbytes);
						System.out.println("�Ƿ��Ѿ�����"+fbytes.length);
						
						fos.write(fbytes);
						fos.flush();
						fos.close();
					}else{
						//0��ʾ��ͬ��
						out.write(0);
					}
					
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
