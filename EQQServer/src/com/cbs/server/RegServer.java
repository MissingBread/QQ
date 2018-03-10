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
 * 注册服务器 1.注册申请 2.验证码申请
 * @author CBS
 *
 */
public class RegServer implements Runnable{
	//Socket对象
	private Socket socket;
	
	public RegServer(Socket socket){
		this.socket=socket;
	}
	//保存验证码
	private static HashMap<String,String> map=new HashMap<String,String>();
	private InputStream in=null;
	private OutputStream out=null;
	public void run() {
		try {
			in=socket.getInputStream();
			out=socket.getOutputStream();
			
			//等待客户端发送消息 发送格式： {“type”:”reg”,“username”:””,”password”:””,”code”:””}
			byte[] bytes=new byte[1024];
			int len=in.read(bytes);
			String str=new String(bytes,0,len);
			JSONObject json=JSONObject.fromObject(str);
			String type=json.getString("type");
			
			if("code".equals(type)){//验证码请求
				String username=json.getString("username");
				
				Random random=new Random();
				StringBuffer code=new StringBuffer();
				//生成验证码
				for(int i=0;i<6;i++){
					code.append(random.nextInt(10));
				}
				
				if(username.trim().length()==11){//手机登录
					try{
						Long.parseLong(username);
						map.put(username, code.toString());
						SendCode.sendByPhone(username, code.toString());
						out.write("{\"state\":0,\"msg\":\"验证码发送成功！\"}".getBytes());
						out.flush();
					}catch(Exception e){
						out.write("{\"state\":1,\"msg\":\"验证码发送失败！\"}".getBytes());
						out.flush();
					}
				}else{//邮箱登录
					if(username.indexOf("@")>0){
						map.put(username, code.toString());
						SendCode.sendByEmail(username, code.toString());
						out.write("{\"state\":0,\"msg\":\"验证码发送成功！\"}".getBytes());
						out.flush();
					}else{
						out.write("{\"state\":1,\"msg\":\"验证码发送失败！\"}".getBytes());
						out.flush();
					}
				}
			}else if("reg".equals(type)){//注册请求
				String username=json.getString("username");
				String password=json.getString("password");
				String code=json.getString("code");
				String code1=map.get(username);
				if(code1!=null){//验证码只能用一次
					map.remove(username);
				}
				
				if(code.equals(code1)){
					
					try {
						new UserServices().regUser(username, password);
					
					} catch (UsernameException e) {
						out.write("{\"state\":1,\"msg\":\"用户名已存在!\"}".getBytes());
						out.flush();
						return;
					} catch (SQLException e) {
						out.write("{\"state\":3,\"msg\":\"未知错误\"}".getBytes());
						out.flush();
						return;
					}
					out.write("{\"state\":0,\"msg\":\"注册成功！可以登录了!\"}".getBytes());
					out.flush();
				}else{
					out.write("{\"state\":2,\"msg\":\"验证码错误，请重新获得!\"}".getBytes());
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
	 * 开启注册服务器
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
