package Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File file=new File("G:\\1.txt");
		FileInputStream fin=new FileInputStream(file);
		String path="G:\\2.txt";
		File file1=new File(path);
		file1.createNewFile();
		FileOutputStream out=new FileOutputStream(file1);
		//¶ÁÈ¡ÎÄ¼þ
		Integer size=fin.available();
		byte[] bytes=new byte[size];
		fin.read(bytes);
		out.write(bytes);
	}

}
