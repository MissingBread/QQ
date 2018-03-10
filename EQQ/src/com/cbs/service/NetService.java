package com.cbs.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import net.sf.json.JSONObject;

import com.cbs.service.util.Config;
import com.cbs.view.FriendsView;

/**
 * 客户端登录的服务 1.更新好友在线状态 5秒钟一次请求 2.登录验证 3.退出账户 使用饿汉单例模式
 * 
 * @author CBS
 * 
 */
public class NetService implements Runnable {

	private NetService() {
	}

	// 单例对象
	private static NetService netService = new NetService();

	public static NetService getNetService() {
		return netService;
	}

	private Socket socket = null;
	private InputStream in = null;
	private OutputStream out = null;
	@SuppressWarnings("unused")
	private boolean run = false;// 线程是否存活
	private Thread thread = null;
	private FriendsView friendsView;// 如果出现别处登录，直接关闭程序

	public void setFriendsView(FriendsView friendsView) {
		this.friendsView = friendsView;
	}

	public void run() {

		// ///////////////////好友列表信息的获得
		try {
			byte[] bytes = new byte[1024 * 10];
			int len = 0;
			// 在线好友的时时更新
			while (true) {
				if(Config.update){//有新的好友需要更新好友列表
					out.write("U0001".getBytes());
					out.flush();
					bytes = new byte[2048];
					len = in.read(bytes);
					String json1 = new String(bytes, 0, len);
					Config.friends_json = json1;
					Config.analysisJson(Config.friends_json);
					System.out.println("好友列表信息：" + Config.friends_json);
					Config.friendsView.updateView();
					Config.update=false;
				}
				out.write("U0002".getBytes());
				out.flush();
				int stateCode = in.read();
				if (stateCode == 4) {
					int i = JOptionPane.showConfirmDialog(friendsView, "您的账号在别处登录！！", "账户异常",
							JOptionPane.YES_NO_OPTION);
					if (i == JOptionPane.YES_NO_OPTION) {
						System.exit(0);
					}
				}
				if ("".equals(Config.friendsList)) {
					out.write("null".getBytes());
					out.flush();
				} else {
					out.write(Config.friendsList.getBytes());
					out.flush();
					bytes = new byte[1024];
					len = in.read(bytes);
					String online = new String(bytes, 0, len);
					// System.out.println("在线好友：" + online);
					try {
						if (!online.equals(Config.friendsOnline)) {// 在线好友不一样
							Config.friendsOnline = online;
							if(Config.friendsListP!=null){
								Config.friendsListP.updateOnline();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					Config.friendsOnline = online;
				}
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			e.printStackTrace();
			run = false;
		}
	}

	/**
	 * 客户端登录服务
	 * 
	 * @return 服务器回执的JSON
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public JSONObject login() throws UnknownHostException, IOException {
		// 创建客户端
		socket = new Socket(Config.IP, Config.LOGIN_PORT);

		in = socket.getInputStream();
		out = socket.getOutputStream();

		String json_str = "{\"username\":\"" + Config.username + "\",\"password\":\"" + Config.password + "\"}";

		out.write(json_str.getBytes());
		out.flush();

		// 接受服务器回执信息并解析
		byte[] bytes = new byte[1024];
		int len = in.read(bytes);

		json_str = new String(bytes, 0, len);
		JSONObject json = JSONObject.fromObject(json_str);

		if (json.getInt("state") == 0) {// 登录成功
			// 开启持续的网络连接服务
			if (thread != null) {
				if (thread.getState() == Thread.State.RUNNABLE) {// 上一次的线程未消亡
					run = false;// 终止线程运行
					try {
						thread.stop();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			///////////// 登记个人资料
			len = in.read(bytes);
			Config.personalInfo = new String(bytes, 0, len);
			System.out.println(Config.personalInfo);
			//////////////////////

			out.write("U0001".getBytes());
			out.flush();
			bytes = new byte[2048];
			len = in.read(bytes);
			String json1 = new String(bytes, 0, len);
			Config.friends_json = json1;
			System.out.println("好友列表信息：" + Config.friends_json);
			Config.analysisJson(Config.friends_json);
			// //////////////////////////////////////

			// /////////////////////获得个人资料
			out.write("U0003".getBytes());
			out.flush();
			len = in.read(bytes);
			Config.personalInfo = new String(bytes, 0, len);
			System.out.println("个人资料：" + Config.personalInfo);
			// //////////////////////////////

			// //////////////////////////////启动UDP服务
			Config.socket = new DatagramSocket();
			new MessageRegService(Config.socket);
			new MessageService(Config.socket);
			// //////////////////////////////

			// //////////////////////////////登录成功后获取Socket对象
			Config.tcpSocket = socket;
			// //////////////////////////////

			// 重新开启线程
			thread = new Thread(this);
			thread.start();
			run = true;
		}

		return json;
	}

}
