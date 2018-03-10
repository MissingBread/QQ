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
 * �ͻ��˵�¼�ķ��� 1.���º�������״̬ 5����һ������ 2.��¼��֤ 3.�˳��˻� ʹ�ö�������ģʽ
 * 
 * @author CBS
 * 
 */
public class NetService implements Runnable {

	private NetService() {
	}

	// ��������
	private static NetService netService = new NetService();

	public static NetService getNetService() {
		return netService;
	}

	private Socket socket = null;
	private InputStream in = null;
	private OutputStream out = null;
	@SuppressWarnings("unused")
	private boolean run = false;// �߳��Ƿ���
	private Thread thread = null;
	private FriendsView friendsView;// ������ֱ𴦵�¼��ֱ�ӹرճ���

	public void setFriendsView(FriendsView friendsView) {
		this.friendsView = friendsView;
	}

	public void run() {

		// ///////////////////�����б���Ϣ�Ļ��
		try {
			byte[] bytes = new byte[1024 * 10];
			int len = 0;
			// ���ߺ��ѵ�ʱʱ����
			while (true) {
				if(Config.update){//���µĺ�����Ҫ���º����б�
					out.write("U0001".getBytes());
					out.flush();
					bytes = new byte[2048];
					len = in.read(bytes);
					String json1 = new String(bytes, 0, len);
					Config.friends_json = json1;
					Config.analysisJson(Config.friends_json);
					System.out.println("�����б���Ϣ��" + Config.friends_json);
					Config.friendsView.updateView();
					Config.update=false;
				}
				out.write("U0002".getBytes());
				out.flush();
				int stateCode = in.read();
				if (stateCode == 4) {
					int i = JOptionPane.showConfirmDialog(friendsView, "�����˺��ڱ𴦵�¼����", "�˻��쳣",
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
					// System.out.println("���ߺ��ѣ�" + online);
					try {
						if (!online.equals(Config.friendsOnline)) {// ���ߺ��Ѳ�һ��
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
	 * �ͻ��˵�¼����
	 * 
	 * @return ��������ִ��JSON
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public JSONObject login() throws UnknownHostException, IOException {
		// �����ͻ���
		socket = new Socket(Config.IP, Config.LOGIN_PORT);

		in = socket.getInputStream();
		out = socket.getOutputStream();

		String json_str = "{\"username\":\"" + Config.username + "\",\"password\":\"" + Config.password + "\"}";

		out.write(json_str.getBytes());
		out.flush();

		// ���ܷ�������ִ��Ϣ������
		byte[] bytes = new byte[1024];
		int len = in.read(bytes);

		json_str = new String(bytes, 0, len);
		JSONObject json = JSONObject.fromObject(json_str);

		if (json.getInt("state") == 0) {// ��¼�ɹ�
			// �����������������ӷ���
			if (thread != null) {
				if (thread.getState() == Thread.State.RUNNABLE) {// ��һ�ε��߳�δ����
					run = false;// ��ֹ�߳�����
					try {
						thread.stop();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			///////////// �ǼǸ�������
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
			System.out.println("�����б���Ϣ��" + Config.friends_json);
			Config.analysisJson(Config.friends_json);
			// //////////////////////////////////////

			// /////////////////////��ø�������
			out.write("U0003".getBytes());
			out.flush();
			len = in.read(bytes);
			Config.personalInfo = new String(bytes, 0, len);
			System.out.println("�������ϣ�" + Config.personalInfo);
			// //////////////////////////////

			// //////////////////////////////����UDP����
			Config.socket = new DatagramSocket();
			new MessageRegService(Config.socket);
			new MessageService(Config.socket);
			// //////////////////////////////

			// //////////////////////////////��¼�ɹ����ȡSocket����
			Config.tcpSocket = socket;
			// //////////////////////////////

			// ���¿����߳�
			thread = new Thread(this);
			thread.start();
			run = true;
		}

		return json;
	}

}
