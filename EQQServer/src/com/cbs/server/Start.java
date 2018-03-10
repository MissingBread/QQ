package com.cbs.server;

import java.io.IOException;

/**
 * 统一启动服务器
 * @author CBS
 *
 */
public class Start {

	public static void main(String[] args) {
		new Thread(){
			public void run(){
				try {
					LoginServer.OpenServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		new Thread(){
			public void run(){
				try {
					RegServer.openServer();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
		new Thread(){
			public void run(){
				try {
						UDPMessageServer.openServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		new Thread(){
			public void run(){
				try {
						AddAndFileServer.openServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		new Thread(){
			public void run(){
				try {
						MsgServer.openServer();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	

}
