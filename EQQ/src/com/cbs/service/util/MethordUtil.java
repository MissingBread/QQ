package com.cbs.service.util;

import java.awt.Component;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.swing.JFileChooser;

import net.sf.json.JSONObject;

/**
 * һЩҵ������Ĵ���
 * @author CBS
 *
 */
public class MethordUtil {
	
//	public static void main(String[] args) {
//		MethordUtil m=new MethordUtil();
//		String s=m.getAnswer("��");
//		System.out.println(s);
//	}
	
	private static String APIKey="c29ac742b30845b38c40c2aa800dcc0d";
	/**
	 * ��ȡָ���ļ�
	 * @param parent ��������
	 * @return �ļ�����
	 */
	public File getFile(Component parent){
		final JFileChooser fc = new JFileChooser();
		int result=fc.showOpenDialog(parent);
		File f = null;
		if(result==JFileChooser.APPROVE_OPTION){
			f=fc.getSelectedFile();
		}
		return f;
	}
	/**
	 * ����ָ���ļ�
	 * @param parent
	 * @return
	 */
	public  String saveFile(Component parent){
		 JFileChooser chooser = new JFileChooser();  
		  
	        chooser.setCurrentDirectory(new File("D:\\"));//����Ĭ��Ŀ¼ ��ֱ��Ĭ��E��  
	  
	        chooser.setDialogTitle("�����ļ�λ��");     //�Զ���ѡ������  
	  
	        chooser.setDialogType(JFileChooser.SAVE_DIALOG);//����Ϊ�����桱  
	        //chooser.setDialogType(JFileChooser.OPEN_DIALOG);//����Ϊ���򿪡�  
	  
	        //chooser.setApproveButtonText("����");//���ð�ť�ϵ����֣�Ĭ���ǡ����桱���ߡ��򿪡�  
	  
	        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//����ʾĿ¼�������֣����ļ�����Ŀ¼�����߶�  
	        chooser.showSaveDialog(null);//show�����桰  
	        //chooser.showOpenDialog(null);//show���򿪡�  
	  
	        //chooser.setSelectedFile(new File("defaultFileName")); //����Ĭ���ļ���  
	  
	        String path = chooser.getSelectedFile().getPath();//��ȡ·��  
	        System.out.println(path);  
	        return  path;  
	}
	
	public static String getAnswer(String question){
		
		String answer=null;
		try {
			String info=URLEncoder.encode(question, "utf-8");
//			System.out.println(info);
			URL url=new URL("http://www.tuling123.com/openapi/api?key="+ APIKey + "&info=" + info);
//			System.out.println(url);
			HttpURLConnection conn=(HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10*1000);
			conn.setRequestMethod("GET");
			int code=conn.getResponseCode();
			if(200==code){
				InputStream  in=conn.getInputStream();
				String result=getString(in);
				JSONObject json=JSONObject.fromObject(result);
				answer = json.getString("text");
				return answer;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	private static String getString(InputStream in) {
		try {
			
			String result="";
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			byte[] buffer=new byte[1024];
			int len=0;
			while((len = in.read(buffer)) != -1){
				out.write(buffer, 0, len);
				out.flush();
			}
			result=new String(out.toByteArray(),"utf-8");
			out.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return null;
	}
	public static byte[] readFile(File file) throws IOException{
		//��ȡ�ļ�
		@SuppressWarnings("resource")
		FileInputStream fin=new FileInputStream(file);
		//��ȡ�ļ�
		Integer size=fin.available();
		byte[] bytes=new byte[size];
		fin.read(bytes);
		return bytes;
	}
	public static byte[] readFileByM(FileInputStream fin) throws IOException{
		byte[] bytes=new byte[2048];
		fin.read(bytes);
		return bytes;
	}
}
