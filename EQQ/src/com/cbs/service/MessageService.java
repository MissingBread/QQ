package com.cbs.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * ���ܷ���������Ϣ��ת
 * 
 * @author CBS
 * 
 */
public class MessageService extends Thread {
	// ���ݰ�
	private DatagramSocket client = null;

	public MessageService(DatagramSocket client) {
		this.client = client;
		this.start();
	}

	public void run() {
		while (true) {
			try {
				byte[] bytes = new byte[1024 * 32];
				DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
				client.receive(packet);
				
				MessagePool.saveMessage(new String(packet.getData(), 0, packet.getData().length));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
