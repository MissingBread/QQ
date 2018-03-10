package com.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 辅助类
 * 
 * @author CBS
 *
 */
public class ServerUtil {
	/**
	 * 发送文件的确认消息
	 * 
	 * @param hySocket
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public static int sendFileMsg(Socket hySocket, String name) throws IOException {
		InputStream in;
		OutputStream out;

		in = hySocket.getInputStream();
		out = hySocket.getOutputStream();

		out.write("SF0001".getBytes());
		out.flush();

		// 发送接收信息
		out.write(name.getBytes());
		out.flush();
		int i = in.read();
		return i;
	}

	public static void sendFile(Socket hySocket, byte[] bytes, int size, String fileName) throws IOException {
		InputStream in;
		OutputStream out;

		in = hySocket.getInputStream();
		out = hySocket.getOutputStream();

		// 发送文件的大小
		String str = size + "";
		out.write(str.getBytes());
		out.flush();
		System.out.println("服务器发送的原始大小：" + size);
		// 发送文件名
		out.write(fileName.getBytes());
		System.out.println(fileName);
		System.out.println("服务器已经发送文件名");
		out.flush();
		// 发送文件
		out.write(bytes);
		out.flush();
		System.out.println("服务器已经发送：");
	}
}
