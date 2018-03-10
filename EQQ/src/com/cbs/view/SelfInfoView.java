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
 * ��������
 * 
 * @author CBS
 *
 */
@SuppressWarnings("serial")
public class SelfInfoView extends JFrame {

	private JTextArea infoText;
	@SuppressWarnings("rawtypes")
	private JComboBox dcomboBox;// ��
	@SuppressWarnings("rawtypes")
	private JComboBox mcomboBox;// ��
	@SuppressWarnings("rawtypes")
	private JComboBox ycomboBox;// ��
	private JTextField realNameText;
	private JTextField introText;// ǩ��
	private JTextField nameText;// �ǳ�
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
	 * ���췽����ʵ����������Ϣ����
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SelfInfoView() {
		setTitle("��������");
		setResizable(false);
		getContentPane().setLayout(null);
		setBounds(100, 100, 482, 399);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// ͷ��
		headImg = new JLabel();
		headImg.setBounds(10, 10, 63, 63);
		getContentPane().add(headImg);

		final JLabel nameL = new JLabel("�ǳ�");
		nameL.setBounds(79, 10, 30, 30);
		getContentPane().add(nameL);

		// �ǳ�
		nameText = new JTextField();
		nameText.setBounds(119, 10, 345, 30);
		getContentPane().add(nameText);

		final JLabel introL = new JLabel("ǩ��");
		introL.setBounds(79, 45, 45, 30);
		getContentPane().add(introL);

		introText = new JTextField();
		introText.setBounds(119, 51, 345, 30);
		getContentPane().add(introText);

		final JPanel panel = new JPanel();
		panel.setLayout(null);
		// �߿�
		panel.setBorder(new TitledBorder(null, "��������", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		panel.setBounds(10, 94, 454, 265);
		getContentPane().add(panel);

		final JLabel label_1 = new JLabel();
		label_1.setText("��ʵ����:");
		label_1.setBounds(10, 37, 66, 18);
		panel.add(label_1);

		final JLabel label_2 = new JLabel();
		label_2.setText("�Ա�:");
		label_2.setBounds(236, 37, 66, 18);
		panel.add(label_2);

		realNameText = new JTextField();
		realNameText.setBounds(75, 35, 132, 30);
		panel.add(realNameText);

		manRadioButton = new JRadioButton();
		manRadioButton.setText("��");
		manRadioButton.setBounds(279, 33, 38, 26);
		panel.add(manRadioButton);

		femalRadioButton = new JRadioButton();
		femalRadioButton.setText("Ů");
		femalRadioButton.setBounds(323, 33, 38, 26);
		panel.add(femalRadioButton);

		final JLabel label_3 = new JLabel();
		label_3.setText("��������:");
		label_3.setBounds(10, 76, 66, 18);
		panel.add(label_3);

		// ��1990-2020
		Object[] list = new Object[30];
		int j = 1990;
		for (int i = 0; i < 30; i++) {
			list[i] = j;
			j++;
		}
		ycomboBox = new JComboBox(list);
		ycomboBox.setBounds(75, 72, 66, 27);
		panel.add(ycomboBox);

		// ��
		Object[] list1 = new Object[12];
		for (int i = 0; i < 12; i++) {
			list1[i] = i + 1;
		}
		mcomboBox = new JComboBox(list1);
		mcomboBox.setBounds(179, 72, 47, 27);
		panel.add(mcomboBox);

		// ��
		Object[] list2 = new Object[30];
		for (int i = 0; i < 30; i++) {
			list2[i] = i + 1;
		}
		dcomboBox = new JComboBox(list2);
		dcomboBox.setBounds(264, 72, 47, 27);
		panel.add(dcomboBox);

		final JLabel label_4 = new JLabel();
		label_4.setText("��");
		label_4.setBounds(147, 76, 22, 18);
		panel.add(label_4);

		final JLabel label_5 = new JLabel();
		label_5.setText("��");
		label_5.setBounds(236, 76, 22, 18);
		panel.add(label_5);

		final JLabel label_6 = new JLabel();
		label_6.setText("��");
		label_6.setBounds(323, 76, 22, 18);
		panel.add(label_6);

		final JLabel label_7 = new JLabel();
		label_7.setText("������ע:");
		label_7.setBounds(10, 116, 66, 18);
		panel.add(label_7);

		final JButton button = new JButton("�ύ");
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

		// ��ע
		infoText = new JTextArea();
		scrollPane.setViewportView(infoText);
		
		update();
	}
	/**
	 * ���º�������
	 */
	private void send() {// "{"netname":"","info"��"","":"","":""}"
		String netname = nameText.getText();
		String info = introText.getText();
		String name = realNameText.getText();
		String sex="��";
		String yy =  ycomboBox.getSelectedItem()+"";
		String mm =  mcomboBox.getSelectedItem()+"";
		String dd =  dcomboBox.getSelectedItem()+"";
		String back = infoText.getText();
		if (manRadioButton.isSelected() && femalRadioButton.isSelected()) {
			JOptionPane.showMessageDialog(this, "ֻ��ѡ��һ���Ա�Ӵ��");
		} else if (manRadioButton.isSelected()) {
			sex = "��";
		} else if (femalRadioButton.isSelected()) {
			sex = "Ů";
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
				JOptionPane.showMessageDialog(this, "�޸ĳɹ���");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Config.friendsView.nameL.setText(netname);
		Config.friendsView.introL.setText(info);
		Config.friendsView.repaint();
	}
	/**
	 * ���¸������Ͻ���
	 */
	public void update(){
		
		//������ȡ��������
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
		if("��".equals(sex)){
			this.manRadioButton.setSelected(true);
		}else if("Ů".equals(sex)){
			this.femalRadioButton.setSelected(true);
		}
		this.dcomboBox.setSelectedItem(dd);
		this.ycomboBox.setSelectedItem(yy);
		this.mcomboBox.setSelectedItem(mm);
		this.headImg.setIcon(new ImageIcon("face0//"+image+".png"));
	
	}

}
