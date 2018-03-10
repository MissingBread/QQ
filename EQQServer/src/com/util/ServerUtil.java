package com.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * ������
 * 
 * @author CBS
 *
 */
public class ServerUtil {
	/**
	 * �����ļ���ȷ����Ϣ
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

		// ���ͽ�����Ϣ
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

		// �����ļ��Ĵ�С
		String str = size + "";
		out.write(str.getBytes());
		out.flush();
		System.out.println("���������͵�ԭʼ��С��" + size);
		// �����ļ���
		out.write(fileName.getBytes());
		System.out.println(fileName);
		System.out.println("�������Ѿ������ļ���");
		out.flush();
		// �����ļ�
		out.write(bytes);
		out.flush();
		System.out.println("�������Ѿ����ͣ�");
	}
}
