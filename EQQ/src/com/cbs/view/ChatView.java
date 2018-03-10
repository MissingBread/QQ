package com.cbs.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Date;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import com.cbs.service.FindService;
import com.cbs.service.util.Config;
import com.cbs.service.util.MethordUtil;

import net.sf.json.JSONObject;

/**
 * ���촰��
 * 
 * @author CBS
 * 
 */
@SuppressWarnings("serial")
public class ChatView extends JFrame implements WindowListener{

	private JTextArea textArea;// ����������Ϣ
	private JLabel imgLabel = new JLabel();// ͷ���ǩ
	private JLabel nameLabel = new JLabel();// �ǳ�
	private JLabel infoLabel = new JLabel();// ����˵��
	private JTextArea msgText;//��ʾ�������ݵ��ı���

	private String uid = "";
//	private String img = "";
	private String name = "";
//	private String info = "";

	// ���췽������ʼ������
	public ChatView(final String uid, String img, String name, String info, Vector<Msg> vector) {//
		this.uid = uid;
//		this.img = img;
		this.name = name;
//		this.info = info;

		this.setTitle(name);
		ImageIcon icon = new ImageIcon("face0//" + img + ".png");
		this.setIconImage(icon.getImage());

		this.imgLabel.setIcon(icon);
		this.infoLabel.setText(info);
		this.nameLabel.setText(name);

		setSize(500, 655);

		// �������,����ͷ��ȴ��
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		getContentPane().add(panel, BorderLayout.NORTH);

		// ͷ��
		imgLabel.setPreferredSize(new Dimension(63, 63));
		panel.add(imgLabel, BorderLayout.WEST);

		// ���ǩ�����ǳ�
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(new BorderLayout());
		panel.add(panel_1, BorderLayout.CENTER);

		panel_1.add(nameLabel, BorderLayout.CENTER);

		panel_1.add(infoLabel, BorderLayout.SOUTH);

		// �ָ��������Ե�����С
		JSplitPane splitPane = new JSplitPane();
		// ���÷ָ�����λ�á�
		splitPane.setDividerLocation(400);
		// ���÷��򣬻��߷ָ�����ķ�ʽ��JSplitPane.VERTICAL_SPLIT ��ʾ y ��ָ
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		getContentPane().add(splitPane, BorderLayout.CENTER);

		// �������ݵ����
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(new BorderLayout());
		splitPane.setRightComponent(panel_2);

		// ��������������ť
		JPanel panel_3 = new JPanel();
		FlowLayout flowLayout_1 = new FlowLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panel_3.setLayout(flowLayout_1);
		panel_2.add(panel_3, BorderLayout.NORTH);

		JButton fontB = new JButton("����");
		panel_3.add(fontB);

		final JButton fileB = new JButton("�ļ�");
		panel_3.add(fileB);
		fileB.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				File file=new MethordUtil().getFile(fileB);
				String fileName=file.getName();
				String myuid=JSONObject.fromObject(Config.personalInfo).getString("uid");
				String netname=JSONObject.fromObject(Config.personalInfo).getString("netname");
				String json="{\"myuid\":\""+myuid+"\",\"hyuid\":\""+uid+"\",\"netname\":\""+netname+"\",\"filename\":\""+fileName+"\"}";
				System.out.println(json);
				FindService.getFindServise().sendFile(file, json);
			}
		});
		

		// ����رշ��Ͱ�ť
		JPanel panel_4 = new JPanel();
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel_4.setLayout(flowLayout);
		panel_2.add(panel_4, BorderLayout.SOUTH);

		JButton closeB = new JButton("�ر�");
		panel_4.add(closeB);

		JButton sendB = new JButton("�������͡�");
		sendB.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// {��type��:��msg��,��toUID��:����,��MyUID��:����,��msg��:����,��code��:����}
				try {
					Msg msg = new Msg();
					msg.setCode(System.currentTimeMillis() + "");
					msg.setMyUID(JSONObject.fromObject(Config.personalInfo)
							.getString("uid"));
					msg.setToUID(uid);
					msg.setMsg(textArea.getText());
					msg.setType("msg");
					if(msg.getMsg().indexOf("@cbs")>=0){
						textArea.setText("");
						//����Լ�����Ϣ
						addMyMsg(msg);
						String question=msg.getMsg().substring(4);
						String answer=MethordUtil.getAnswer(question);
						@SuppressWarnings("deprecation")
						String str="\ncbs\t"+new Date().toLocaleString()+"\n"+answer+"\n";
						msgText.setText(msgText.getText()+str);
					}else{
						JSONObject json = JSONObject.fromObject(msg);
						System.out.println(json);
						String jsonstr = json.toString();
						byte[] bytes = jsonstr.getBytes();
						// ��װΪ���ݰ�
						DatagramPacket packet = new DatagramPacket(bytes,
								bytes.length, InetAddress.getByName(Config.IP),
								4003);
						Config.socket.send(packet);
						textArea.setText("");
						//����Լ�����Ϣ
						addMyMsg(msg);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
		});
		panel_4.add(sendB);

		JScrollPane scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);
	

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		JScrollPane scrollPane_1 = new JScrollPane();
		// ��������õ��ָ�������ߣ������棩��
		splitPane.setLeftComponent(scrollPane_1);
		
		msgText=new JTextArea();
		scrollPane_1.setViewportView(msgText);
		
		this.addWindowListener(this);
		this.setVisible(true);
		//ѭ��������е���Ϣ
		for (Msg msg : vector) {
			addMessage(msg);
		}
	}
	/**
	 * ��ʾ���˵��������Ϣ
	 * @param messg
	 */
	public void addMessage(Msg messg) {
		@SuppressWarnings("deprecation")
		String str="\n"+name+"\t"+new Date().toLocaleString()+"\n"+messg.getMsg()+"\n";
		msgText.setText(msgText.getText()+str);
		
//		msgText.setSelectionStart(msgText.getText().length());
//		msgText.setSelectionEnd(msgText.getText().length());
//		msgText.requestFocus();
	}
	/**
	 * ����Լ�����Ϣ
	 * @param msg
	 */
	private void addMyMsg(Msg msg){
		@SuppressWarnings("deprecation")
		String str="\n"+JSONObject.fromObject(Config.personalInfo).getString("netname")+"\t"+new Date().toLocaleString()+"\n"+msg.getMsg()+"\n";
		msgText.setText(msgText.getText()+str);
		
//		msgText.setSelectionStart(msgText.getText().length());
//		msgText.setSelectionEnd(msgText.getText().length());
//		msgText.requestFocus();
	}
	public void windowOpened(WindowEvent e) {
		
	}
	public void windowClosing(WindowEvent e) {
		Config.closeChatView(uid);
		this.dispose();
	}
	public void windowClosed(WindowEvent e) {
		
	}
	public void windowIconified(WindowEvent e) {
		
	}
	public void windowDeiconified(WindowEvent e) {
		
	}
	public void windowActivated(WindowEvent e) {
		
	}
	public void windowDeactivated(WindowEvent e) {
		
	}

}
