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
 * 一些业务处理方面的代码
 * @author CBS
 *
 */
public class MethordUtil {
	
//	public static void main(String[] args) {
//		MethordUtil m=new MethordUtil();
//		String s=m.getAnswer("额");
//		System.out.println(s);
//	}
	
	private static String APIKey="c29ac742b30845b38c40c2aa800dcc0d";
	/**
	 * 获取指定文件
	 * @param parent 父类容器
	 * @return 文件对象
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
	 * 保存指定文件
	 * @param parent
	 * @return
	 */
	public  String saveFile(Component parent){
		 JFileChooser chooser = new JFileChooser();  
		  
	        chooser.setCurrentDirectory(new File("D:\\"));//设置默认目录 打开直接默认E盘  
	  
	        chooser.setDialogTitle("保存文件位置");     //自定义选择框标题  
	  
	        chooser.setDialogType(JFileChooser.SAVE_DIALOG);//设置为“保存”  
	        //chooser.setDialogType(JFileChooser.OPEN_DIALOG);//设置为“打开”  
	  
	        //chooser.setApproveButtonText("保存");//设置按钮上的文字，默认是“保存”或者“打开”  
	  
	        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//仅显示目录；有三种，仅文件，仅目录，两者都  
	        chooser.showSaveDialog(null);//show”保存“  
	        //chooser.showOpenDialog(null);//show“打开”  
	  
	        //chooser.setSelectedFile(new File("defaultFileName")); //设置默认文件名  
	  
	        String path = chooser.getSelectedFile().getPath();//获取路径  
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
		//读取文件
		@SuppressWarnings("resource")
		FileInputStream fin=new FileInputStream(file);
		//读取文件
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
