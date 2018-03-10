package com.cbs.service;

import java.util.HashMap;
import java.util.LinkedList;

import net.sf.json.JSONObject;

import com.cbs.service.util.Config;
import com.cbs.view.ChatView;
import com.cbs.view.FacePanel;
import com.cbs.view.Msg;

/**
 * 消息池 把所有接收到的消息都储存在这个池中，需要的时候取出
 * 模式：饿汉单例
 * @author CBS
 *
 */
public class MessagePool {
	private MessagePool(){
		
	}
	
	private static MessagePool pool=new MessagePool();
	
	@SuppressWarnings("static-access")
	public MessagePool getMessagePool(){
		return this.pool;
	}
	//哈希表用来存储消息，value值是一个LinkedList用来不停的追加
	public static HashMap<String,LinkedList<Msg>> hashMap=new HashMap<String,LinkedList<Msg>>();
	
	/**
	 * 不管是什么消息都先存储起来
	 *{“type”:”msg”,”toUID”:””,”myUID”:””,”msg”:””,”code”:””}
	 * @param jsonstr
	 */
	public static void saveMessage(String jsonstr){
		JSONObject json=JSONObject.fromObject(jsonstr);
		
		String myUID=json.getString("myUID");
		String messg=json.getString("msg");
		//把接收到
		Msg msg=new Msg();
		msg.setMyUID(myUID);
		msg.setToUID(json.getString("toUID"));
		msg.setMsg(messg);
		msg.setCode(json.getString("code"));
		msg.setType(json.getString("type"));
		
		try {
			ChatView chatView=Config.table.get(myUID);
			if(chatView.isVisible()){//如果聊天的窗口是打开的就可以直接显示信息
				chatView.addMessage(msg);
			}else{
				throw new Exception();
			}
		} catch (Exception e) {
			
			FacePanel face=Config.list.get(myUID);
			face.addMsg(msg);
			
//			//链表集合里面存储Msg对象，以便今后读取信息
//			LinkedList<Msg> list=hashMap.get(myUID);
//			if(list==null){
//				list=new LinkedList<Msg>();
//			}
//			//添加消息
//			list.add(msg);
//			//向哈希表中添加数据
//			hashMap.put(myUID, list);
		}
		
		
	}
}
