package com.cbs.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.json.JSONObject;

/**
 * UDP����Ϣ��ת������
 * 
 * @author CBS
 * 
 */
public class UDPMessageServer implements Runnable {

	// ���ݰ�
	private DatagramPacket packet = null;
	// UDP Scoket����
	private static DatagramSocket datagramSocket = null;

	public UDPMessageServer(DatagramPacket packet) {
		this.packet = packet;
	}

	public void run() {
		try {
			String str = new String(packet.getData(), 0, packet.getLength());
			JSONObject json = JSONObject.fromObject(str);

			// ��������������ֹ�˿ںű�����
			if ("reg".equals(json.getString("type"))) {

				String MyUID = json.getString("MyUID");
				// �������µ�ip�Ͷ˿ں�
				if(!UserOnlineList.getUserOnlineList().isUserOnline(MyUID)){
					Thread.sleep(3000);
//					System.out.println(UserOnlineList.getUserOnlineList().isUserOnline(MyUID));
				}
				UserOnlineList.getUserOnlineList().updataUserUDP(MyUID,
						packet.getAddress().getHostAddress(), packet.getPort());
//				System.out.println("��ע����Ϣ������" + str);

			} else if ("msg".equals(json.getString("type"))
					|| "qr".equals(json.getString("type"))) {// ������Ϣ��ת����ȷ��
				//�����JSON����JSONObjective�Զ����ɣ��������������MyUID���Զ��ĳ���myUID
				String MyUID = json.getString("myUID");
				String toUID = json.getString("toUID");
				// �������µ�ip�Ͷ˿ں�
				UserOnlineList.getUserOnlineList().updataUserUDP(MyUID,
						packet.getAddress().getHostAddress(), packet.getPort());
				// ���Ҫ��������Ϣ����
				UserInfo userInfo = UserOnlineList.getUserOnlineList()
						.getOnlineUserInfo(toUID);
				// ���°�װҪ���͵����ݰ�
				DatagramPacket datagramPacket = new DatagramPacket(
						packet.getData(), packet.getLength(),
						InetAddress.getByName(userInfo.getUdpip()), userInfo.getUdpPort());
				//�������ݰ�
				datagramSocket.send(datagramPacket);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����UDP������
	 * 
	 * @throws Exception
	 */
	public static void openServer() throws Exception {
		datagramSocket = new DatagramSocket(4003);
		// �����̳߳�
		ExecutorService execute = Executors.newFixedThreadPool(1000);

		// �ȴ��ͻ��˵�����
		while (true) {
			try {
				// UDP�������֧��64k
				byte[] bytes = new byte[1024 * 32];
				// �������
				DatagramPacket datagramPacket = new DatagramPacket(bytes,
						bytes.length);
				// ��������
				datagramSocket.receive(datagramPacket);

				// �õ�һ����������ץȡһ���߳�ȥ����
				execute.execute(new UDPMessageServer(datagramPacket));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
