package com.cbs.view;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.cbs.service.NetService;
import com.cbs.service.util.Config;

import net.sf.json.JSONObject;

/**
 * �����б�
 * @author CBS
 *
 */
@SuppressWarnings("serial")
public class FriendsView extends JDialog {
	/**
	 * ���췽������ʼ������
	 */
	
	JLabel nameL=new JLabel();
	JLabel introL =new JLabel();
	JLabel HImgL =new JLabel(new ImageIcon("face//15.png"));
	JScrollPane scrollPane ;
	FriendsList friendsList;
	
	public FriendsView() {
		
		setBounds(100, 100, 246, 743);
		//ͷ���ǩ�����ֵ����
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(5,5));
		getContentPane().add(panel, BorderLayout.NORTH);
		//ͷ��
//		HImgL = new JLabel();
		HImgL.setPreferredSize(new Dimension(63, 63)); 

		panel.add(HImgL, BorderLayout.WEST);
		//�ǳƺ�ǩ�����ֵ����
		JPanel panel_1 = new JPanel();
		BorderLayout borderLayout = new BorderLayout(5,5);
		panel_1.setLayout(borderLayout);
		panel.add(panel_1, BorderLayout.CENTER);
		//�ǳ�
		nameL = new JLabel("��ʧ��С�˰�");
		nameL.setFont(new Font("", Font.BOLD, 16));
		panel_1.add(nameL, BorderLayout.CENTER);
		//ǩ��
		introL = new JLabel();
		introL.setFont(new Font("����", Font.PLAIN, 12));
		introL.setText("֪ʶ�ļ�ֵ������ռ�У�������ʹ�á�");
		panel_1.add(introL, BorderLayout.SOUTH);
		//����İ�ť���б�����
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(new BorderLayout());
		getContentPane().add(panel_2, BorderLayout.SOUTH);
		//�ײ���ť���
		JPanel panel_3 = new JPanel();
		FlowLayout flowLayout_1 = new FlowLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panel_3.setLayout(flowLayout_1);
		panel_2.add(panel_3);

		JButton setB = new JButton("����");
		panel_3.add(setB);
		setB.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				new SelfInfoView().setVisible(true);
			}
		});

		JButton findB = new JButton("����");
		findB.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				new FindView().setVisible(true);
			}
		});
		panel_3.add(findB);

		JPanel panel_4 = new JPanel();
		FlowLayout flowLayout = new FlowLayout();
		panel_4.setLayout(flowLayout);
		panel_2.add(panel_4, BorderLayout.EAST);
		
		JButton button_1 = new JButton();
		button_1.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		button_1.setText("�˳�");
		panel_4.add(button_1);
		//ѡ�
		JTabbedPane tabbedPane = new JTabbedPane();
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		//�ҵĺ������
		JPanel panel_5 = new JPanel();
		panel_5.setLayout(new BorderLayout());
		tabbedPane.addTab("�ҵĺ���", null, panel_5, null);
		
		//Ⱥ���
		JPanel panel_6 = new JPanel();
		panel_6.setLayout(new BorderLayout());
		tabbedPane.addTab("�ҵ�Ⱥ��", null, panel_6, null);

		scrollPane = new JScrollPane();
		panel_5.add(scrollPane, BorderLayout.CENTER);
		//��Ӻ����б�
		friendsList=new FriendsList();
		scrollPane.getViewport().add(friendsList);
		
		JScrollPane scrollPane1 = new JScrollPane();
		panel_6.add(scrollPane1, BorderLayout.CENTER);
		
		updata();
		NetService.getNetService().setFriendsView(this);
	}
	
	/**
	 * ���¸�����Ϣ
	 */
	private void updata(){
		// {"back":"","dd":0,"email":"1234561@qq.com","img":"2","info":"��һ�����̻�","mm":0,"name":"","netname":"kaige","phonenumber":"17070125314","sex":"","uid":"123456789","yy":0}
		JSONObject json=JSONObject.fromObject(Config.personalInfo);
		this.setTitle("ͨ��ͨѶ");
		
		this.nameL.setText(json.getString("netname"));
		this.introL.setText(json.getString("info"));
		this.HImgL.setIcon(new ImageIcon("face0//"+json.getString("img")+".png"));
	}
	/**
	 * ���º����б�
	 */
	public void updateView(){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		friendsList.updata();
		this.repaint();
	}
}
