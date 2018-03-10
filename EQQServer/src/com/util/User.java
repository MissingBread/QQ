package com.util;

import java.net.Socket;

/**
 * �û��ౣ��uid�͸���socket
 * @author CBS
 *
 */
public class User {
	public String uid;
	public Socket loginSocket=null;
	public Socket msgSocket=null;
//	public Socket loginSocket;
	
	public User(){
		
	}
	public User(String uid,Socket loginSocket){
		this.uid=uid;
		this.loginSocket=loginSocket;
	}
	public User(String uid,Socket msgSocket,Socket loginSocket){
		this.uid=uid;
		this.loginSocket=loginSocket;
		this.msgSocket=msgSocket;
	}
}
