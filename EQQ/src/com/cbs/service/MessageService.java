package com.cbs.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * 接受服务器的消息中转
 * 
 * @author CBS
 * 
 */
public class MessageService extends Thread {
	// 数据包
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
