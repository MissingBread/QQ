package com.cbs.view;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.cbs.service.util.Config;
/**
 * �����б����
 * @author CBS
 *
 */
@SuppressWarnings("serial")
public class FriendsList extends JPanel{
	
	public FriendsList(){
		setLayout(null);
		updata();
		//��netServiceʹ�ú���ʱʱ����
		Config.friendsListP=this;
	}
//	//�������Ĵ��
//	private Hashtable<String, FacePanel> list=new Hashtable();
	
	/**
	 * ���غ����б�
	 */
	public void updata() {
		// �����б�
		String friendList = Config.friends_json;
		JSONArray jsonArray = JSONArray.fromObject(friendList);

		if (Config.list.size() == 0) {// ��һ�μ����б�
			// {"img":"def","info":"��һ������ɫ","netname":"fengjie","uid":"448552211"}
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject json = (JSONObject) jsonArray.get(i);

				Config.list.put(
						json.getString("uid"),
						new FacePanel(json.getString("img"), json
								.getString("netname"), json
								.getString("info"), json.getString("uid")));
			}
		}else{//�Ѿ����б�,�ж��Ƿ����µ��˼ӽ���
			for(int i=0;i<jsonArray.size();i++){
				JSONObject json=JSONObject.fromObject(jsonArray.get(i));
				String uid=json.getString("uid");
				
				FacePanel facePanel=Config.list.get(uid);
				if(facePanel!=null){//������Ѿ����ں����б�������
					facePanel.setName(json.getString("netname"));
					facePanel.setImage(json.getString("img"));
					facePanel.setInfo(json.getString("info"));
				}else{//������
					Config.list.put(
							json.getString("uid"),
							new FacePanel(json.getString("img"), json
									.getString("netname"), json
									.getString("info"), json.getString("uid")));
				}
				
			}
		}
		updateOnline();
	}
	/**
	 * ���ߺ��Ѹ���
	 */
	@SuppressWarnings("unchecked")
	public void updateOnline(){
		// �����б�
		String onlineList = Config.friendsOnline;
		
		String[] uids=onlineList.split(",");
		Set<String> set=Config.list.keySet();
		
		for (String string : set) {//��ʼ����ȫ����Ϊ������
			Config.list.get(string).setOnline(false);
		}
		
		if(!onlineList.trim().equals("notFound") && !onlineList.trim().equals("")){//�����ߺ���
			for (String uid : uids) {
				if(!"".equals(uid.trim())){
					FacePanel facePanel=Config.list.get(uid);
					if(facePanel!=null)
						facePanel.setOnline(true);
				}
			}
		}
		
		//����,���ߵ���ǰ��
		Collection<FacePanel> faceJPanels = Config.list.values();
		@SuppressWarnings("rawtypes")
		List<FacePanel> list = new ArrayList(faceJPanels);
		Collections.sort(list);
		
		this.removeAll();
		int i=0;
		for (FacePanel facePanel : list) {
			facePanel.setBounds(0, i++ * 50, 546, 59);
			this.add(facePanel);
		}
		
		//�����޷�����
		this.setPreferredSize(new Dimension(0, 40 * list.size()));
		this.updateUI();
	}
}
