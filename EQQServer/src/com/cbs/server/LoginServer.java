package com.cbs.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cbs.db.FriendListInfo;
import com.cbs.db.PasswordException;
import com.cbs.db.StateException;
import com.cbs.db.UserServices;
import com.cbs.db.UserInfo;
import com.cbs.db.UsernameNotFoundException;
import com.util.User;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * ������
 * @author CBS
 *
 */
public class LoginServer implements Runnable{
	
	private Socket socket=null;

	
	public LoginServer(Socket socket){
		this.socket=socket;
	}
	
	@SuppressWarnings("resource")
	public static void OpenServer() throws Exception{
		//����һ��������
		ServerSocket server=new ServerSocket(9090);
		//�̳߳�
		ExecutorService executor=Executors.newFixedThreadPool(2000);
		
		System.out.println("�����������ɹ����ȴ�����...");
		
		while (true){
			//�����ͻ���
			Socket socket =server.accept();
			socket.setSoTimeout(20000);
			executor.execute(new LoginServer(socket));
		}
		
	}

	public void run() {
		InputStream in=null;
		OutputStream out=null;
		String uid=null;
		try {
			in=socket.getInputStream();
			out=socket.getOutputStream();
			
			//����֤�����
			
			//��������ʼ���ܵ�¼��Ϣ
			byte[] bytes=new byte[1024];
			int len=in.read(bytes);
			String json_str=new String(bytes, 0, len);
			
			//��ʼ����JSON
			JSONObject json=JSONObject.fromObject(json_str);
			String username=json.getString("username");
			String password=json.getString("password");
			
			boolean type=false;
			
			
			
			//�ж��ǲ����ֻ�����
			if(username.trim().length()==11){
				try{
					
					Long.parseLong(username);//�Ƿ��Ǵ�����
					type=true;
				}catch (NumberFormatException e){
					out.write("{\"satate\":4,\"msg\":\"δ֪����!\"}".getBytes());
					out.flush();
					return;
				}
			}else{//���ֻ���
				type=false;
			}
			
			
			try {
				if(type){//�ֻ������¼
					//�������ݿ���֤
					uid=new UserServices().loginByPhone(username, password);
					//�Ǽ�����
					UserOnlineList.getUserOnlineList().RegOnline(uid, socket, null, username);
				}else{//���ֻ��ŵ�¼
					uid=new UserServices().loginByEmail(username, password);
					UserOnlineList.getUserOnlineList().RegOnline(uid, socket, username, null);
				}
				
				//�����¼�ɹ�
				String jsonstr="{\"state\":0,\"msg\":\"��¼�ɹ�!\",\"uid\":\""+uid+"\"}";
//				out.write("{\"state\":0,\"msg\":\"��¼�ɹ�!\"}".getBytes());
				out.write(jsonstr.getBytes());
				out.flush();
				
				//��ͻ��˷��͸���������Ϣ
				out.write(UserOnlineList.getUserOnlineList().JSONstr.getBytes());
				out.flush();
				
				//�Ǽ��û�����
				User u=new User(uid, socket);
				UserOnlineList.getUserOnlineList().onlineUser.put(uid, u);
				//:��֤����
				
				//ʱʱ������Ϣ
				while(true){
					//���ܿͻ��˵�����Ϣ
					bytes=new byte[1024];
					len=in.read(bytes);
					String command=new String(bytes,0,len);
					if("U0001".equals(command)){//���º����б�
						Vector<FriendListInfo> vector=new UserServices().getFriendList(uid);
						out.write(JSONArray.fromObject(vector).toString().getBytes());
						out.flush();
					}else if("U0002".equals(command)){// ���º�������
						//��Ӧ��Ϣ
						out.write(1);
						out.flush();
						//��ȡ�����б��ţ�������123456789,13456789����ʽ
						len=in.read(bytes);
						String str=new String(bytes, 0, len);
						if("null".equals(str)){
							
						}else{
							
							//��","�ָ��ַ���
							String[] ids=str.split(",");
							StringBuffer sb=new StringBuffer();
							
							for (String string : ids) {
								if(UserOnlineList.getUserOnlineList().isUserOnline(string)){
									sb.append(string);
									sb.append(",");
								}
							}
							
							if(sb.length()==0){//û�ҵ����ߺ���
								out.write("notFound".getBytes());
								out.flush();
							}else{//��ִ�����б�
								out.write(sb.toString().getBytes());
								out.flush();
							}
						}
					}else if("U0003".equals(command)){//���¸�������
						UserInfo uin=new UserServices().getUserInfo(uid);
						out.write(JSONObject.fromObject(uin).toString().getBytes());
						out.flush();
					}else if("E0001".equals(command)){//�޸ĸ�������
						//��Ӧ
						out.write(1);
						bytes=new byte[512];
						len=in.read(bytes);
						String str=new String(bytes,0,len);
						int code=UserServices.updateSelfInfo(str);
						if(code>0){//���ݿ�д��ɹ�
							out.write(1);
						}
					}else if("EXIT".equals(command)){//�û��˳�
						//���º����б�
						UserOnlineList.getUserOnlineList().loginOut(uid);
						//�������е������û����б�
						UserOnlineList.getUserOnlineList().onlineUser.remove(uid);
						return;
					}else if("G0001".equals(command)){//���»�ȡ��������
						out.write(UserOnlineList.getUserOnlineList().JSONstr.getBytes());
						out.flush();
					}
					
				}
				
			} catch (UsernameNotFoundException e) {
				out.write("{\"state\":1,\"msg\":\"�û�������!\"}".getBytes());
				out.flush();
				return;
			} catch (PasswordException e) {
				out.write("{\"state\":2,\"msg\":\"�������!\"}".getBytes());
				out.flush();
				return;
			} catch (StateException e) {
				out.write("{\"state\":3,\"msg\":\"�˻�����!\"}".getBytes());
				out.flush();
				return;
			} catch (SQLException e) {
				out.write("{\"state\":4,\"msg\":\"δ֪����!\"}".getBytes());
				out.flush();
				return;
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}finally{
			try {
				//����ԭ���˳���¼ҲҪ�ǳ�
				UserOnlineList.getUserOnlineList().loginOut(uid);
				UserOnlineList.getUserOnlineList().onlineUser.remove(uid);
				out.close();
				in.close();
				socket.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	

}
