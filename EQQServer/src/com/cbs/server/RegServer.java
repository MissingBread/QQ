package com.cbs.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cbs.db.UserServices;
import com.cbs.db.UsernameException;

import net.sf.json.JSONObject;

/**
 * ע������� 1.ע������ 2.��֤������
 * @author CBS
 *
 */
public class RegServer implements Runnable{
	//Socket����
	private Socket socket;
	
	public RegServer(Socket socket){
		this.socket=socket;
	}
	//������֤��
	private static HashMap<String,String> map=new HashMap<String,String>();
	private InputStream in=null;
	private OutputStream out=null;
	public void run() {
		try {
			in=socket.getInputStream();
			out=socket.getOutputStream();
			
			//�ȴ��ͻ��˷�����Ϣ ���͸�ʽ�� {��type��:��reg��,��username��:����,��password��:����,��code��:����}
			byte[] bytes=new byte[1024];
			int len=in.read(bytes);
			String str=new String(bytes,0,len);
			JSONObject json=JSONObject.fromObject(str);
			String type=json.getString("type");
			
			if("code".equals(type)){//��֤������
				String username=json.getString("username");
				
				Random random=new Random();
				StringBuffer code=new StringBuffer();
				//������֤��
				for(int i=0;i<6;i++){
					code.append(random.nextInt(10));
				}
				
				if(username.trim().length()==11){//�ֻ���¼
					try{
						Long.parseLong(username);
						map.put(username, code.toString());
						SendCode.sendByPhone(username, code.toString());
						out.write("{\"state\":0,\"msg\":\"��֤�뷢�ͳɹ���\"}".getBytes());
						out.flush();
					}catch(Exception e){
						out.write("{\"state\":1,\"msg\":\"��֤�뷢��ʧ�ܣ�\"}".getBytes());
						out.flush();
					}
				}else{//�����¼
					if(username.indexOf("@")>0){
						map.put(username, code.toString());
						SendCode.sendByEmail(username, code.toString());
						out.write("{\"state\":0,\"msg\":\"��֤�뷢�ͳɹ���\"}".getBytes());
						out.flush();
					}else{
						out.write("{\"state\":1,\"msg\":\"��֤�뷢��ʧ�ܣ�\"}".getBytes());
						out.flush();
					}
				}
			}else if("reg".equals(type)){//ע������
				String username=json.getString("username");
				String password=json.getString("password");
				String code=json.getString("code");
				String code1=map.get(username);
				if(code1!=null){//��֤��ֻ����һ��
					map.remove(username);
				}
				
				if(code.equals(code1)){
					
					try {
						new UserServices().regUser(username, password);
					
					} catch (UsernameException e) {
						out.write("{\"state\":1,\"msg\":\"�û����Ѵ���!\"}".getBytes());
						out.flush();
						return;
					} catch (SQLException e) {
						out.write("{\"state\":3,\"msg\":\"δ֪����\"}".getBytes());
						out.flush();
						return;
					}
					out.write("{\"state\":0,\"msg\":\"ע��ɹ������Ե�¼��!\"}".getBytes());
					out.flush();
				}else{
					out.write("{\"state\":2,\"msg\":\"��֤����������»��!\"}".getBytes());
					out.flush();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				out.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * ����ע�������
	 */
	@SuppressWarnings("resource")
	public static void openServer() throws IOException{
		ExecutorService service=Executors.newFixedThreadPool(1000);
		ServerSocket server=new ServerSocket(4002);
		while(true){
			Socket socket=server.accept();
			service.execute(new RegServer(socket));
		}
	}
	
}
