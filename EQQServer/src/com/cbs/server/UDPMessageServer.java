package com.cbs.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.json.JSONObject;

/**
 * UDP的信息中转服务器
 * 
 * @author CBS
 * 
 */
public class UDPMessageServer implements Runnable {

	// 数据包
	private DatagramPacket packet = null;
	// UDP Scoket服务
	private static DatagramSocket datagramSocket = null;

	public UDPMessageServer(DatagramPacket packet) {
		this.packet = packet;
	}

	public void run() {
		try {
			String str = new String(packet.getData(), 0, packet.getLength());
			JSONObject json = JSONObject.fromObject(str);

			// 处理心跳包，防止端口号被回收
			if ("reg".equals(json.getString("type"))) {

				String MyUID = json.getString("MyUID");
				// 更新最新的ip和端口号
				if(!UserOnlineList.getUserOnlineList().isUserOnline(MyUID)){
					Thread.sleep(3000);
//					System.out.println(UserOnlineList.getUserOnlineList().isUserOnline(MyUID));
				}
				UserOnlineList.getUserOnlineList().updataUserUDP(MyUID,
						packet.getAddress().getHostAddress(), packet.getPort());
//				System.out.println("有注册信息发来：" + str);

			} else if ("msg".equals(json.getString("type"))
					|| "qr".equals(json.getString("type"))) {// 处理消息的转发和确认
				//这里的JSON是由JSONObjective自动生成，所以这里的属性MyUID被自动改成了myUID
				String MyUID = json.getString("myUID");
				String toUID = json.getString("toUID");
				// 更新最新的ip和端口号
				UserOnlineList.getUserOnlineList().updataUserUDP(MyUID,
						packet.getAddress().getHostAddress(), packet.getPort());
				// 获得要接受你消息的人
				UserInfo userInfo = UserOnlineList.getUserOnlineList()
						.getOnlineUserInfo(toUID);
				// 重新包装要发送的数据包
				DatagramPacket datagramPacket = new DatagramPacket(
						packet.getData(), packet.getLength(),
						InetAddress.getByName(userInfo.getUdpip()), userInfo.getUdpPort());
				//发送数据包
				datagramSocket.send(datagramPacket);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 启动UDP服务器
	 * 
	 * @throws Exception
	 */
	public static void openServer() throws Exception {
		datagramSocket = new DatagramSocket(4003);
		// 制作线程池
		ExecutorService execute = Executors.newFixedThreadPool(1000);

		// 等待客户端的数据
		while (true) {
			try {
				// UDP报文最大支持64k
				byte[] bytes = new byte[1024 * 32];
				// 打包数据
				DatagramPacket datagramPacket = new DatagramPacket(bytes,
						bytes.length);
				// 监听数据
				datagramSocket.receive(datagramPacket);

				// 得到一个数据立马抓取一个线程去处理
				execute.execute(new UDPMessageServer(datagramPacket));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
