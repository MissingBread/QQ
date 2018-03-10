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
 * 好友列表面板
 * @author CBS
 *
 */
@SuppressWarnings("serial")
public class FriendsList extends JPanel{
	
	public FriendsList(){
		setLayout(null);
		updata();
		//在netService使得好友时时更新
		Config.friendsListP=this;
	}
//	//好友面板的存放
//	private Hashtable<String, FacePanel> list=new Hashtable();
	
	/**
	 * 加载好友列表
	 */
	public void updata() {
		// 好友列表
		String friendList = Config.friends_json;
		JSONArray jsonArray = JSONArray.fromObject(friendList);

		if (Config.list.size() == 0) {// 第一次加载列表
			// {"img":"def","info":"不一样的姿色","netname":"fengjie","uid":"448552211"}
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject json = (JSONObject) jsonArray.get(i);

				Config.list.put(
						json.getString("uid"),
						new FacePanel(json.getString("img"), json
								.getString("netname"), json
								.getString("info"), json.getString("uid")));
			}
		}else{//已经有列表,判断是否有新的人加进来
			for(int i=0;i<jsonArray.size();i++){
				JSONObject json=JSONObject.fromObject(jsonArray.get(i));
				String uid=json.getString("uid");
				
				FacePanel facePanel=Config.list.get(uid);
				if(facePanel!=null){//这个人已经存在好友列表里面了
					facePanel.setName(json.getString("netname"));
					facePanel.setImage(json.getString("img"));
					facePanel.setInfo(json.getString("info"));
				}else{//不存在
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
	 * 在线好友更新
	 */
	@SuppressWarnings("unchecked")
	public void updateOnline(){
		// 在线列表
		String onlineList = Config.friendsOnline;
		
		String[] uids=onlineList.split(",");
		Set<String> set=Config.list.keySet();
		
		for (String string : set) {//初始化，全部设为不在线
			Config.list.get(string).setOnline(false);
		}
		
		if(!onlineList.trim().equals("notFound") && !onlineList.trim().equals("")){//有在线好友
			for (String uid : uids) {
				if(!"".equals(uid.trim())){
					FacePanel facePanel=Config.list.get(uid);
					if(facePanel!=null)
						facePanel.setOnline(true);
				}
			}
		}
		
		//排序,在线的在前面
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
		
		//否则无法滚动
		this.setPreferredSize(new Dimension(0, 40 * list.size()));
		this.updateUI();
	}
}
