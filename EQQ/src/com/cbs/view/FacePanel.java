package com.cbs.view;

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.cbs.service.FindService;
import com.cbs.service.util.Config;

import net.sf.json.JSONObject;

/**
 * ÿһλ���ѵ����
 * 
 * @author CBS
 *
 */
@SuppressWarnings("serial")
public class FacePanel extends JPanel implements Comparable<FacePanel>, MouseListener, Runnable {

	private String image;// ͷ��
	private String netName;// �ǳ�
	private String info;// ����˵��
	private String uid;
	private JLabel label_image;
	private JLabel label_netName;
	private JLabel label_info;
	private boolean online = false;// �Ƿ�����
	private int xx = 0;
	private int yy = 0;
	private Vector<Msg> vector = new Vector<Msg>();// �Ĵ����е���Ϣ
	private Thread thread = null;// �̵߳Ŀ���
	private boolean run = true;// �ж��߳��Ƿ�����
	private int flag=0;//falg��ʾ���ڵĸ����壬������ڸ�����Ϊ������Ӻ���ҳ��flagΪ1������Ǻ����б����flagΪ0��Ĭ��Ϊ0��

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public FacePanel(String image, String netName, String info, String uid) {
		this.image = image;
		this.netName = netName;
		this.info = info;
		this.uid = uid;

		setOpaque(true);
		this.setLayout(null);

		label_image = new JLabel();
		label_image.setBounds(4, 4, 48, 48);
		add(label_image);
		setImage(image);

		label_netName = new JLabel();
		label_netName.setBounds(58, 4, 478, 24);
		add(label_netName);
		label_netName.setFont(new Font("΢���ź�", Font.BOLD, 14));
		label_netName.setText(netName);

		label_info = new JLabel();
		label_info.setBounds(58, 34, 478, 18);
		add(label_info);
		label_info.setText(info);
		// ����¼�����
		this.addMouseListener(this);
	}

	public void addMsg(Msg msg) {
		vector.add(msg);// �����Ϣ

		if (thread == null) {// �����߳�����ͷ�������
			thread = new Thread(this);
			thread.start();
		} else if (thread.getState() == Thread.State.TERMINATED) {
			thread = new Thread(this);
			thread.start();
		} else if (run = false) {
			thread = new Thread(this);
			thread.start();
		}
	}

	/**
	 * ����ͷ��
	 * 
	 * @param image
	 */
	public void setImage(String image) {
		if (image.equals("def")) {
			image = "0";
		}
		if (online) {
			label_image.setIcon(new ImageIcon("face0/" + image + ".png"));
		} else {
			label_image.setIcon(new ImageIcon("face1/" + image + ".png"));
		}
		this.image = image;
	}

	public void setName(String name) {
		label_netName.setText(name);
		this.netName = name;
	}

	public void setInfo(String info) {
		label_info.setText(info);
		this.info = info;
	}

	public void setOnline(boolean online) {
		this.online = online;
		if (this.online) {
			label_image.setIcon(new ImageIcon("face0/" + image + ".png"));
		} else {
			label_image.setIcon(new ImageIcon("face1/" + image + ".png"));
		}
	}

	// ???
	public int compareTo(FacePanel o) {
		if (o.online) {
			return 1;
		} else if (this.online) {
			return -1;
		} else {
			return 0;
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && flag==0) {// ˫�����������
			if (online) {// �������߲Ŵ򿪣�������չΪ���߷���
				run = false;
				Config.getChatView(uid, netName, info, image, vector);
			}
		}
		if (e.getClickCount() == 2 && flag==1) {// ˫������Ӻ�����ʾ��
			if (true) {// �������߲Ŵ򿪣�������չΪ���߷���
				int i=JOptionPane.showConfirmDialog(FacePanel.this, "��ȷ��Ҫ��Ӹ��û�Ϊ������");
				if(i==0){//yes
					//"{"myuid":"","hyuid":"","name":""}"
					JSONObject jsonstr=JSONObject.fromObject(Config.personalInfo);
					String json="{\"myuid\":\""+jsonstr.getString("uid")+"\",\"hyuid\":\""+uid+"\",\"netname\":\""+jsonstr.getString("netname")+"\"}";
					FindService.getFindServise().addFriend(json);
				}
				flag=0;
			}
		}
	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {// ����ͷ�������
		this.xx = this.getX();
		this.yy = this.getY();
		this.setLocation(xx - 3, yy - 3);
	}

	public void mouseExited(MouseEvent e) {
		this.setLocation(xx, yy);
	}

	public void run() {// ͷ�������߳�
		run = true;
		int x = this.getX();
		int y = this.getY();

		while (run) {
			this.setLocation(x - 1, y - 1);
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.setLocation(x + 2, y + 2);
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.setLocation(x, y);
	}

}
