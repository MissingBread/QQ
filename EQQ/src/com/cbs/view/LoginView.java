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
 * 登录界面
 * @author CBS
 */
@SuppressWarnings("serial")
public class LoginView extends JDialog implements WindowListener{
	
	private JTextField numField;//登录账号
	private JTextField paswField;//登录密码
	private JTextField rgNumField;//注册
	private JTextField confimField;//验证码
	private JTextField rpaswField;//注册密码
	private JTextField rconfimpswField;//确认密码

	public static void main(String[] args) {
		
		
		
		//提供一个关于新创建的 JFrame 是否应该具有当前外观为其提供的 Window 装饰（如边框、关闭窗口的小部件、标题等等）的提示。
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		try {
			//设定样式
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
	 * 构造方法，初始化登录界面
	 */
	public LoginView(){
		this.setTitle("登录");
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.getContentPane().setLayout(null);
		this.setSize(293, 314);
		
		JLabel jNum = new JLabel("手机号:");
		jNum.setBounds(10, 102, 65, 24);
		getContentPane().add(jNum);
		
		final JLabel jEmail = new JLabel("Email:");
		jEmail.setBounds(10, 123, 65, 24);
		getContentPane().add(jEmail);
		
		numField = new JTextField();
		numField.setBounds(55, 99, 219, 48);
		getContentPane().add(numField);
		
		JLabel Jpasw = new JLabel("密　码:");
		Jpasw.setBounds(10, 186, 65, 18);
		getContentPane().add(Jpasw);
		
		paswField = new JPasswordField();
		paswField.setBounds(55, 171, 219, 48);
		getContentPane().add(paswField);
		
		JButton loginB = new JButton("登　录");
		loginB.setBounds(177, 225, 97, 51);
		getContentPane().add(loginB);
		loginB.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				//用户名和密码
				String username=numField.getText().trim();
				String password=paswField.getText().trim();
				
				if("".equals(username)||"".equals(password)){
					JOptionPane.showMessageDialog(LoginView.this, "用户名和密码不能为空！");
					return;
				}
				
				Config.username=username;
				Config.password=password;
				
				//开始登录验证
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
		
		JButton regB = new JButton("注　册");
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
		//设置此组件的边框。TitledBorder:用指定的边框、标题、标题对齐方式、标题位置、标题字体和标题颜色创建 TitledBorder 实例。
		panel.setBorder(new TitledBorder(null, "注册用户", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		panel.setBounds(10, 306, 264, 271);
		getContentPane().add(panel);
		
		JLabel label_2 = new JLabel("手  机  号:");
		label_2.setBounds(10, 33, 65, 18);
		panel.add(label_2);
		
		JLabel emailLabel_1 = new JLabel("　 Email:");
		emailLabel_1.setBounds(10, 52, 65, 18);
		panel.add(emailLabel_1);
		
		rgNumField = new JTextField();
		rgNumField.setBounds(63, 27, 180, 43);
		panel.add(rgNumField);
		
		JButton button_2 = new JButton("发送验证");
		button_2.setBounds(146, 76, 97, 30);
		panel.add(button_2);
		button_2.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if("".equals(rgNumField.getText().trim())){
					JOptionPane.showMessageDialog(LoginView.this, "用户名不能为空！");
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
						JOptionPane.showMessageDialog(LoginView.this, "验证码发送成功！");
					}else{
						JOptionPane.showMessageDialog(LoginView.this, "验证码发送失败！");
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
		label_3.setText("验  证  码:");
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

		final JLabel label_4 = new JLabel("密　　码:");
		label_4.setBounds(10, 174, 65, 18);
		panel.add(label_4);

		final JLabel label_5 = new JLabel("确认密码:");
		label_5.setBounds(10, 223, 65, 18);
		panel.add(label_5);

		final JButton regB1 = new JButton("注册用户");
		regB1.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if("".equals(rgNumField.getText().trim())){
					JOptionPane.showMessageDialog(LoginView.this, "用户名不能为空！");
					return;
				}
				if("".equals(confimField.getText().trim())){
					JOptionPane.showMessageDialog(LoginView.this, "验证码不能为空！");
					return;
				}
				if("".equals(rpaswField.getText().trim())){
					JOptionPane.showMessageDialog(LoginView.this, "密码不能为空！");
					return;
				}
				if("".equals(rconfimpswField.getText().trim())){
					JOptionPane.showMessageDialog(LoginView.this, "确认密码不能为空！");
					return;
				}
				if(!rpaswField.getText().trim().equals(rconfimpswField.getText().trim())){
					JOptionPane.showMessageDialog(LoginView.this, "两次密码不相同！");
					return;
				}
				//连接注册服务器，开始注册
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
						JOptionPane.showMessageDialog(LoginView.this, "恭喜您注册成功，可以登录了！");
						rgNumField.setText("");
						confimField.setText("");
						rpaswField.setText("");
						rconfimpswField.setText("");
					}else if(json.getInt("state")==1){
						JOptionPane.showMessageDialog(LoginView.this, "用户名已存在!");
					}else if(json.getInt("state")==2){
						JOptionPane.showMessageDialog(LoginView.this, "验证码错误，请重新获得!");
					}else if(json.getInt("state")==3){
						JOptionPane.showMessageDialog(LoginView.this, "未知错误!");
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

		final JButton quitB = new JButton("放弃");
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
