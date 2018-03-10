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
 * �����ѯ��Ϣ�ĸ����࣬��������ADDServer
 * �ļ������ͨ��Ҳ�����︺��
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
		//�����ͻ���
		socket=new Socket(Config.IP, Config.ADD_PORT);
		
		in=socket.getInputStream();
		out=socket.getOutputStream();
	}
	
	/**
	 * ���Һ���
	 * @param json	�����json��
	 * @return	����json ��
	 */
	public String find(String json){
		try {
			out.write("U0004".getBytes());
			//�����������������Ϣ
			out.write(json.getBytes());
			
			System.out.println("Findservice:"+json);
			
			byte[] bytes =new byte[1024];
			//�����ڶ�ȡ��Ϣ���߳�����Ҳ�ڶ�ȡ��������
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
	 * ��Ӻ���
	 * @param json
	 */
	public void addFriend(String json){//"{"myuid":"","hyuid":"","netname":""}"
		try {
			//U0005��Ӻ���
			out.write("U0005".getBytes());
			//�����������������Ϣ
			out.write(json.getBytes());
			
			int i=in.read();
			if(i!=-1){//���º����б�
				Config.update=true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * �����ļ�
	 * @param path �ļ�·��
	 * @param json ���͵�json��Ϣ
	 */
	public void sendFile(File file,String json){//{"myuid":"","hyuid":"","netname":"","filename":""}
		try {
			out.write("F0001".getBytes());
			in.read();
			out.write(json.getBytes());
			int flag=in.read();
			if(flag==1){
				byte[] bytes=MethordUtil.readFile(file);
				//�����ļ�
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
