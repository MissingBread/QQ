package com.cbs.view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.cbs.service.FindService;

import net.sf.json.JSONObject;

/**
 * 查询好友界面
 * @author CBS
 *
 */
@SuppressWarnings("serial")
public class FindView extends JFrame {
	
	private JTextField nameText;//要查找的好友的昵称
	final JScrollPane scrollPane;

	public static void main(String[] args) {
		
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FindView frame = new FindView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * 构造方法，初始化界面
	 */
	public FindView() {
		setTitle("查找好友");
		getContentPane().setLayout(null);
		setResizable(false);
		setBounds(100, 100, 469, 422);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		final JLabel label = new JLabel();
		label.setText("昵称:");
		label.setBounds(10, 12, 66, 18);
		getContentPane().add(label);
		final JLabel label1 = new JLabel();
		label1.setText("账户:");
		label1.setBounds(10, 32, 66, 18);
		getContentPane().add(label1);

		nameText = new JTextField();
		nameText.setBounds(50, 14, 326, 35);
		getContentPane().add(nameText);

		final JButton button = new JButton();
		button.setText("查找");
		button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String num=nameText.getText();
				String json="";
				if("".equals(num)){
					JOptionPane.showMessageDialog(FindView.this, "查询的账号或者昵称不能为空！");
					return;
				}else{
					//发送JSON串格式 "{"type":"number||netname","netname":"", || "number":""}"
					if(num.contains("@")||num.trim().length()==11){//邮箱查找或者是电话号码
						String jsonstr="{\"type\":\"number\",\"number\":\""+num+"\"}";
						json=FindService.getFindServise().find(jsonstr);
					}else {//昵称查找
						String jsonstr="{\"type\":\"netname\",\"netname\":\""+num+"\"}";
						json=FindService.getFindServise().find(jsonstr);
					}
				}
				
				showFindMsg(json);
				
			}
		});
		button.setBounds(382, 17, 66, 28);
		getContentPane().add(button);
		
		JButton jb=new JButton("用户");
		jb.setBounds(10, 60, 438, 30);
		getContentPane().add(jb);

		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 95, 438, 280);
		getContentPane().add(scrollPane);

	}
	
	private void showFindMsg(String jsonstr){
		// 返回JSON串格式 "{"state":"","uid":"","netname":"","image":"","isOnline":"","info":""}"
		JSONObject json=JSONObject.fromObject(jsonstr);
		int state=json.getInt("state");
		if(state==0){
			JOptionPane.showMessageDialog(FindView.this, "未找到该用户！！");
			return;
		}
		String uid=json.getString("uid");
		String netname=json.getString("netname");
		String img=json.getString("image");
		String isOnline=json.getString("isOnline");
		boolean online=false;
		if("true".equals(isOnline)){
			online=true;
		}
		System.out.println(jsonstr);
		String info=json.getString("info");
		FacePanel fp=new FacePanel(img, netname, info, uid);
		fp.setOnline(online);
		fp.setFlag(1);//表示是由添加好友窗口点击的
		scrollPane.setViewportView(fp);
	}
}
