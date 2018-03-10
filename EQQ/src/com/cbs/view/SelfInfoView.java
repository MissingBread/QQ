package com.cbs.view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import com.cbs.service.util.Config;

import net.sf.json.JSONObject;

/**
 * 个人资料
 * 
 * @author CBS
 *
 */
@SuppressWarnings("serial")
public class SelfInfoView extends JFrame {

	private JTextArea infoText;
	@SuppressWarnings("rawtypes")
	private JComboBox dcomboBox;// 日
	@SuppressWarnings("rawtypes")
	private JComboBox mcomboBox;// 月
	@SuppressWarnings("rawtypes")
	private JComboBox ycomboBox;// 年
	private JTextField realNameText;
	private JTextField introText;// 签名
	private JTextField nameText;// 昵称
	private JRadioButton manRadioButton;
	private JRadioButton femalRadioButton;
	private JLabel  headImg;

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
					SelfInfoView frame = new SelfInfoView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 构造方法，实例化个人信息窗口
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SelfInfoView() {
		setTitle("个人资料");
		setResizable(false);
		getContentPane().setLayout(null);
		setBounds(100, 100, 482, 399);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// 头像
		headImg = new JLabel();
		headImg.setBounds(10, 10, 63, 63);
		getContentPane().add(headImg);

		final JLabel nameL = new JLabel("昵称");
		nameL.setBounds(79, 10, 30, 30);
		getContentPane().add(nameL);

		// 昵称
		nameText = new JTextField();
		nameText.setBounds(119, 10, 345, 30);
		getContentPane().add(nameText);

		final JLabel introL = new JLabel("签名");
		introL.setBounds(79, 45, 45, 30);
		getContentPane().add(introL);

		introText = new JTextField();
		introText.setBounds(119, 51, 345, 30);
		getContentPane().add(introText);

		final JPanel panel = new JPanel();
		panel.setLayout(null);
		// 边框
		panel.setBorder(new TitledBorder(null, "个人资料", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		panel.setBounds(10, 94, 454, 265);
		getContentPane().add(panel);

		final JLabel label_1 = new JLabel();
		label_1.setText("真实名字:");
		label_1.setBounds(10, 37, 66, 18);
		panel.add(label_1);

		final JLabel label_2 = new JLabel();
		label_2.setText("性别:");
		label_2.setBounds(236, 37, 66, 18);
		panel.add(label_2);

		realNameText = new JTextField();
		realNameText.setBounds(75, 35, 132, 30);
		panel.add(realNameText);

		manRadioButton = new JRadioButton();
		manRadioButton.setText("男");
		manRadioButton.setBounds(279, 33, 38, 26);
		panel.add(manRadioButton);

		femalRadioButton = new JRadioButton();
		femalRadioButton.setText("女");
		femalRadioButton.setBounds(323, 33, 38, 26);
		panel.add(femalRadioButton);

		final JLabel label_3 = new JLabel();
		label_3.setText("出生年月:");
		label_3.setBounds(10, 76, 66, 18);
		panel.add(label_3);

		// 年1990-2020
		Object[] list = new Object[30];
		int j = 1990;
		for (int i = 0; i < 30; i++) {
			list[i] = j;
			j++;
		}
		ycomboBox = new JComboBox(list);
		ycomboBox.setBounds(75, 72, 66, 27);
		panel.add(ycomboBox);

		// 月
		Object[] list1 = new Object[12];
		for (int i = 0; i < 12; i++) {
			list1[i] = i + 1;
		}
		mcomboBox = new JComboBox(list1);
		mcomboBox.setBounds(179, 72, 47, 27);
		panel.add(mcomboBox);

		// 日
		Object[] list2 = new Object[30];
		for (int i = 0; i < 30; i++) {
			list2[i] = i + 1;
		}
		dcomboBox = new JComboBox(list2);
		dcomboBox.setBounds(264, 72, 47, 27);
		panel.add(dcomboBox);

		final JLabel label_4 = new JLabel();
		label_4.setText("年");
		label_4.setBounds(147, 76, 22, 18);
		panel.add(label_4);

		final JLabel label_5 = new JLabel();
		label_5.setText("月");
		label_5.setBounds(236, 76, 22, 18);
		panel.add(label_5);

		final JLabel label_6 = new JLabel();
		label_6.setText("日");
		label_6.setBounds(323, 76, 22, 18);
		panel.add(label_6);

		final JLabel label_7 = new JLabel();
		label_7.setText("备　　注:");
		label_7.setBounds(10, 116, 66, 18);
		panel.add(label_7);

		final JButton button = new JButton("提交");
		button.setBounds(8, 220, 66, 30);
		panel.add(button);
		button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});

		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(75, 116, 358, 133);
		panel.add(scrollPane);

		// 备注
		infoText = new JTextArea();
		scrollPane.setViewportView(infoText);
		
		update();
	}
	/**
	 * 更新好友资料
	 */
	private void send() {// "{"netname":"","info"："","":"","":""}"
		String netname = nameText.getText();
		String info = introText.getText();
		String name = realNameText.getText();
		String sex="男";
		String yy =  ycomboBox.getSelectedItem()+"";
		String mm =  mcomboBox.getSelectedItem()+"";
		String dd =  dcomboBox.getSelectedItem()+"";
		String back = infoText.getText();
		if (manRadioButton.isSelected() && femalRadioButton.isSelected()) {
			JOptionPane.showMessageDialog(this, "只能选择一个性别哟！");
		} else if (manRadioButton.isSelected()) {
			sex = "男";
		} else if (femalRadioButton.isSelected()) {
			sex = "女";
		}

		@SuppressWarnings("static-access")
		String str = "{\"netname\":\"" + netname + "\",\"info\":\"" + info + "\",\"name\":\"" + name + "\",\"sex\":\""
				+ sex + "\",\"yy\":\"" + yy + "\",\"mm\":\"" + mm + "\",\"dd\":\"" + dd + "\",\"back\":\"" + back
				+ "\",\"uid\":\""+new JSONObject().fromObject(Config.personalInfo).getString("uid")+"\"}";
		Socket socket=Config.tcpSocket;
		try {
			InputStream in=socket.getInputStream();
			OutputStream out=socket.getOutputStream();
			
			out.write("E0001".getBytes());
			out.flush();
			in.read();
			out.write(str.getBytes());
			out.flush();
			int code=in.read();
			if(1==code){
				JOptionPane.showMessageDialog(this, "修改成功！");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Config.friendsView.nameL.setText(netname);
		Config.friendsView.introL.setText(info);
		Config.friendsView.repaint();
	}
	/**
	 * 更新个人资料界面
	 */
	public void update(){
		
		//重新拉取个人资料
		Config.getSelfINfo();
		
		JSONObject json=JSONObject.fromObject(Config.personalInfo);
		String netname = json.getString("netname");
		String info = json.getString("info");
		String name = json.getString("name");
		String sex=json.getString("sex");
		String yy =  json.getString("yy");
		String mm =  json.getString("mm");
		String dd =  json.getString("dd");
		String back = json.getString("back");
		String image=json.getString("img");
		
		this.infoText.setText(back);
		this.introText.setText(info);
		this.nameText.setText(netname);
		this.realNameText.setText(name);
		if("男".equals(sex)){
			this.manRadioButton.setSelected(true);
		}else if("女".equals(sex)){
			this.femalRadioButton.setSelected(true);
		}
		this.dcomboBox.setSelectedItem(dd);
		this.ycomboBox.setSelectedItem(yy);
		this.mcomboBox.setSelectedItem(mm);
		this.headImg.setIcon(new ImageIcon("face0//"+image+".png"));
	
	}

}
