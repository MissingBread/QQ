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
 * 服务器
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
		//开启一个服务器
		ServerSocket server=new ServerSocket(9090);
		//线程池
		ExecutorService executor=Executors.newFixedThreadPool(2000);
		
		System.out.println("服务器启动成功，等待连接...");
		
		while (true){
			//监听客户端
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
			
			//：验证代码块
			
			//服务器开始接受登录信息
			byte[] bytes=new byte[1024];
			int len=in.read(bytes);
			String json_str=new String(bytes, 0, len);
			
			//开始解析JSON
			JSONObject json=JSONObject.fromObject(json_str);
			String username=json.getString("username");
			String password=json.getString("password");
			
			boolean type=false;
			
			
			
			//判断是不是手机号码
			if(username.trim().length()==11){
				try{
					
					Long.parseLong(username);//是否是存数字
					type=true;
				}catch (NumberFormatException e){
					out.write("{\"satate\":4,\"msg\":\"未知错误!\"}".getBytes());
					out.flush();
					return;
				}
			}else{//非手机号
				type=false;
			}
			
			
			try {
				if(type){//手机号码登录
					//连接数据库验证
					uid=new UserServices().loginByPhone(username, password);
					//登记在线
					UserOnlineList.getUserOnlineList().RegOnline(uid, socket, null, username);
				}else{//非手机号登录
					uid=new UserServices().loginByEmail(username, password);
					UserOnlineList.getUserOnlineList().RegOnline(uid, socket, username, null);
				}
				
				//输出登录成功
				String jsonstr="{\"state\":0,\"msg\":\"登录成功!\",\"uid\":\""+uid+"\"}";
//				out.write("{\"state\":0,\"msg\":\"登录成功!\"}".getBytes());
				out.write(jsonstr.getBytes());
				out.flush();
				
				//向客户端发送个人资料信息
				out.write(UserOnlineList.getUserOnlineList().JSONstr.getBytes());
				out.flush();
				
				//登记用户在线
				User u=new User(uid, socket);
				UserOnlineList.getUserOnlineList().onlineUser.put(uid, u);
				//:验证结束
				
				//时时更新信息
				while(true){
					//接受客户端的信信息
					bytes=new byte[1024];
					len=in.read(bytes);
					String command=new String(bytes,0,len);
					if("U0001".equals(command)){//更新好友列表
						Vector<FriendListInfo> vector=new UserServices().getFriendList(uid);
						out.write(JSONArray.fromObject(vector).toString().getBytes());
						out.flush();
					}else if("U0002".equals(command)){// 更新好友在线
						//响应信息
						out.write(1);
						out.flush();
						//获取好友列表编号，发送是123456789,13456789的形式
						len=in.read(bytes);
						String str=new String(bytes, 0, len);
						if("null".equals(str)){
							
						}else{
							
							//用","分割字符串
							String[] ids=str.split(",");
							StringBuffer sb=new StringBuffer();
							
							for (String string : ids) {
								if(UserOnlineList.getUserOnlineList().isUserOnline(string)){
									sb.append(string);
									sb.append(",");
								}
							}
							
							if(sb.length()==0){//没找到在线好友
								out.write("notFound".getBytes());
								out.flush();
							}else{//回执好友列表
								out.write(sb.toString().getBytes());
								out.flush();
							}
						}
					}else if("U0003".equals(command)){//更新个人资料
						UserInfo uin=new UserServices().getUserInfo(uid);
						out.write(JSONObject.fromObject(uin).toString().getBytes());
						out.flush();
					}else if("E0001".equals(command)){//修改个人资料
						//响应
						out.write(1);
						bytes=new byte[512];
						len=in.read(bytes);
						String str=new String(bytes,0,len);
						int code=UserServices.updateSelfInfo(str);
						if(code>0){//数据库写入成功
							out.write(1);
						}
					}else if("EXIT".equals(command)){//用户退出
						//更新好友列表
						UserOnlineList.getUserOnlineList().loginOut(uid);
						//更新所有的在线用户的列表
						UserOnlineList.getUserOnlineList().onlineUser.remove(uid);
						return;
					}else if("G0001".equals(command)){//重新获取个人资料
						out.write(UserOnlineList.getUserOnlineList().JSONstr.getBytes());
						out.flush();
					}
					
				}
				
			} catch (UsernameNotFoundException e) {
				out.write("{\"state\":1,\"msg\":\"用户不存在!\"}".getBytes());
				out.flush();
				return;
			} catch (PasswordException e) {
				out.write("{\"state\":2,\"msg\":\"密码错误!\"}".getBytes());
				out.flush();
				return;
			} catch (StateException e) {
				out.write("{\"state\":3,\"msg\":\"账户锁定!\"}".getBytes());
				out.flush();
				return;
			} catch (SQLException e) {
				out.write("{\"state\":4,\"msg\":\"未知错误!\"}".getBytes());
				out.flush();
				return;
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}finally{
			try {
				//不明原因退出登录也要登出
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
