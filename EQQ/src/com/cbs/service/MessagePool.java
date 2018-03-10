package com.cbs.service;

import java.util.HashMap;
import java.util.LinkedList;

import net.sf.json.JSONObject;

import com.cbs.service.util.Config;
import com.cbs.view.ChatView;
import com.cbs.view.FacePanel;
import com.cbs.view.Msg;

/**
 * ��Ϣ�� �����н��յ�����Ϣ��������������У���Ҫ��ʱ��ȡ��
 * ģʽ����������
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
	//��ϣ�������洢��Ϣ��valueֵ��һ��LinkedList������ͣ��׷��
	public static HashMap<String,LinkedList<Msg>> hashMap=new HashMap<String,LinkedList<Msg>>();
	
	/**
	 * ������ʲô��Ϣ���ȴ洢����
	 *{��type��:��msg��,��toUID��:����,��myUID��:����,��msg��:����,��code��:����}
	 * @param jsonstr
	 */
	public static void saveMessage(String jsonstr){
		JSONObject json=JSONObject.fromObject(jsonstr);
		
		String myUID=json.getString("myUID");
		String messg=json.getString("msg");
		//�ѽ��յ�
		Msg msg=new Msg();
		msg.setMyUID(myUID);
		msg.setToUID(json.getString("toUID"));
		msg.setMsg(messg);
		msg.setCode(json.getString("code"));
		msg.setType(json.getString("type"));
		
		try {
			ChatView chatView=Config.table.get(myUID);
			if(chatView.isVisible()){//�������Ĵ����Ǵ򿪵ľͿ���ֱ����ʾ��Ϣ
				chatView.addMessage(msg);
			}else{
				throw new Exception();
			}
		} catch (Exception e) {
			
			FacePanel face=Config.list.get(myUID);
			face.addMsg(msg);
			
//			//����������洢Msg�����Ա����ȡ��Ϣ
//			LinkedList<Msg> list=hashMap.get(myUID);
//			if(list==null){
//				list=new LinkedList<Msg>();
//			}
//			//�����Ϣ
//			list.add(msg);
//			//���ϣ�����������
//			hashMap.put(myUID, list);
		}
		
		
	}
}
