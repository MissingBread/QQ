package com.cbs.view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import net.sf.json.JSONObject;

import com.cbs.service.FindService;
import com.cbs.service.MsgService;
import com.cbs.service.NetService;
import com.cbs.service.util.Config;



/**
 * ��¼����
 * @author CBS
 */
@SuppressWarnings("serial")
public class LoginView extends JDialog implements WindowListener{
	
	private JTextField numField;//��¼�˺�
	private JTextField paswField;//��¼����
	private JTextField rgNumField;//ע��
	private JTextField confimField;//��֤��
	private JTextField rpaswField;//ע������
	private JTextField rconfimpswField;//ȷ������

	public static void main(String[] args) {
		
		
		
		//�ṩһ�������´����� JFrame �Ƿ�Ӧ�þ��е�ǰ���Ϊ���ṩ�� Window װ�Σ���߿򡢹رմ��ڵ�С����������ȵȣ�����ʾ��
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		try {
			//�趨��ʽ
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//			UIManager.setLookAndFeel( "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginView frame = new LoginView();
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * ���췽������ʼ����¼����
	 */
	public LoginView(){
		this.setTitle("��¼");
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.getContentPane().setLayout(null);
		this.setSize(293, 314);
		
		JLabel jNum = new JLabel("�ֻ���:");
		jNum.setBounds(10, 102, 65, 24);
		getContentPane().add(jNum);
		
		final JLabel jEmail = new JLabel("Email:");
		jEmail.setBounds(10, 123, 65, 24);
		getContentPane().add(jEmail);
		
		numField = new JTextField();
		numField.setBounds(55, 99, 219, 48);
		getContentPane().add(numField);
		
		JLabel Jpasw = new JLabel("�ܡ���:");
		Jpasw.setBounds(10, 186, 65, 18);
		getContentPane().add(Jpasw);
		
		paswField = new JPasswordField();
		paswField.setBounds(55, 171, 219, 48);
		getContentPane().add(paswField);
		
		JButton loginB = new JButton("�ǡ�¼");
		loginB.setBounds(177, 225, 97, 51);
		getContentPane().add(loginB);
		loginB.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				//�û���������
				String username=numField.getText().trim();
				String password=paswField.getText().trim();
				
				if("".equals(username)||"".equals(password)){
					JOptionPane.showMessageDialog(LoginView.this, "�û��������벻��Ϊ�գ�");
					return;
				}
				
				Config.username=username;
				Config.password=password;
				
				//��ʼ��¼��֤
				try {
					JSONObject json=NetService.getNetService().login();
					if(json.getInt("state")==0){
						FindService.getFindServise().LinkAddServer();
						MsgService.getMsgService().LinkServer(json.getString("uid"));
						FriendsView f=new FriendsView();
						Config.friendsView=f;
						f.setVisible(true);
						LoginView.this.dispose();
					}else{
						JOptionPane.showMessageDialog(LoginView.this, json.getString("msg"));
					}
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JButton regB = new JButton("ע����");
		regB.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				
				if(LoginView.this.getHeight()==646){//646  314
					LoginView.this.setSize(293, 314);
				}else{
					LoginView.this.setSize(293, 646);
				}
				
			}
		});
		regB.setBounds(10, 225, 97, 51);
		getContentPane().add(regB);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		//���ô�����ı߿�TitledBorder:��ָ���ı߿򡢱��⡢������뷽ʽ������λ�á���������ͱ�����ɫ���� TitledBorder ʵ����
		panel.setBorder(new TitledBorder(null, "ע���û�", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		panel.setBounds(10, 306, 264, 271);
		getContentPane().add(panel);
		
		JLabel label_2 = new JLabel("��  ��  ��:");
		label_2.setBounds(10, 33, 65, 18);
		panel.add(label_2);
		
		JLabel emailLabel_1 = new JLabel("�� Email:");
		emailLabel_1.setBounds(10, 52, 65, 18);
		panel.add(emailLabel_1);
		
		rgNumField = new JTextField();
		rgNumField.setBounds(63, 27, 180, 43);
		panel.add(rgNumField);
		
		JButton button_2 = new JButton("������֤");
		button_2.setBounds(146, 76, 97, 30);
		panel.add(button_2);
		button_2.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if("".equals(rgNumField.getText().trim())){
					JOptionPane.showMessageDialog(LoginView.this, "�û�������Ϊ�գ�");
					return;
				}
				try {
					Socket socket=new Socket(Config.IP, Config.REG_PORT);
					InputStream in=socket.getInputStream();
					OutputStream out=socket.getOutputStream();
					
					out.write(("{\"type\":\"code\",\"username\":\"" + rgNumField.getText() + "\"}").getBytes());
					out.flush();
					
					byte[] bytes=new byte[1024];
					int len=in.read(bytes);
					String str=new String(bytes, 0, len);
					
					JSONObject json=JSONObject.fromObject(str);
					if(json.getInt("state")==0){
						JOptionPane.showMessageDialog(LoginView.this, "��֤�뷢�ͳɹ���");
					}else{
						JOptionPane.showMessageDialog(LoginView.this, "��֤�뷢��ʧ�ܣ�");
					}
					
					in.close();
					out.close();
					socket.close();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JLabel label_3 = new JLabel();
		label_3.setText("��  ֤  ��:");
		label_3.setBounds(10, 125, 65, 18);
		panel.add(label_3);
		
		confimField = new JTextField();
		confimField.setBounds(63, 113, 85, 43);
		panel.add(confimField);
		
		rpaswField = new JPasswordField();
		rpaswField.setBounds(63, 162, 180, 43);
		panel.add(rpaswField);

		rconfimpswField = new JPasswordField();
		rconfimpswField.setBounds(63, 211, 180, 43);
		panel.add(rconfimpswField);

		final JLabel label_4 = new JLabel("�ܡ�����:");
		label_4.setBounds(10, 174, 65, 18);
		panel.add(label_4);

		final JLabel label_5 = new JLabel("ȷ������:");
		label_5.setBounds(10, 223, 65, 18);
		panel.add(label_5);

		final JButton regB1 = new JButton("ע���û�");
		regB1.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if("".equals(rgNumField.getText().trim())){
					JOptionPane.showMessageDialog(LoginView.this, "�û�������Ϊ�գ�");
					return;
				}
				if("".equals(confimField.getText().trim())){
					JOptionPane.showMessageDialog(LoginView.this, "��֤�벻��Ϊ�գ�");
					return;
				}
				if("".equals(rpaswField.getText().trim())){
					JOptionPane.showMessageDialog(LoginView.this, "���벻��Ϊ�գ�");
					return;
				}
				if("".equals(rconfimpswField.getText().trim())){
					JOptionPane.showMessageDialog(LoginView.this, "ȷ�����벻��Ϊ�գ�");
					return;
				}
				if(!rpaswField.getText().trim().equals(rconfimpswField.getText().trim())){
					JOptionPane.showMessageDialog(LoginView.this, "�������벻��ͬ��");
					return;
				}
				//����ע�����������ʼע��
				Socket socket;
				try {
					socket = new Socket(Config.IP, Config.REG_PORT);
					InputStream in=socket.getInputStream();
					OutputStream out=socket.getOutputStream();
					
					out.write(("{\"type\":\"reg\",\"username\":\"" + rgNumField.getText() + "\",\"password\":\""
							+ rpaswField.getText() + "\",\"code\":\"" + confimField.getText() + "\"}").getBytes());
					out.flush();
					
					byte[] bytes=new byte[1024];
					int len=in.read(bytes);
					String str=new String(bytes,0,len);
					JSONObject json=JSONObject.fromObject(str);
					
					if(json.getInt("state")==0){
						JOptionPane.showMessageDialog(LoginView.this, "��ϲ��ע��ɹ������Ե�¼�ˣ�");
						rgNumField.setText("");
						confimField.setText("");
						rpaswField.setText("");
						rconfimpswField.setText("");
					}else if(json.getInt("state")==1){
						JOptionPane.showMessageDialog(LoginView.this, "�û����Ѵ���!");
					}else if(json.getInt("state")==2){
						JOptionPane.showMessageDialog(LoginView.this, "��֤����������»��!");
					}else if(json.getInt("state")==3){
						JOptionPane.showMessageDialog(LoginView.this, "δ֪����!");
					}
					in.close();
					out.close();
					socket.close();
				}catch (Exception e1) {
					e1.printStackTrace();
				}
				
			}
		});
		regB1.setBounds(177, 583, 97, 30);
		getContentPane().add(regB1);

		final JButton quitB = new JButton("����");
		quitB.setBounds(10, 583, 97, 30);
		getContentPane().add(quitB);
		
		this.addWindowListener(this);
		
	}
	public void windowActivated(WindowEvent e) {
		
	}
	public void windowClosed(WindowEvent e) {
		
	}
	public void windowClosing(WindowEvent e) {
		System.exit(0);
		
	}
	public void windowDeactivated(WindowEvent e) {
		
	}
	public void windowDeiconified(WindowEvent e) {
		
	}
	public void windowIconified(WindowEvent e) {
		
	}
	public void windowOpened(WindowEvent e) {
		
	}

}
