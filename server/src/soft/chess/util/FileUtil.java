package soft.chess.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.DatagramPacket;

/**
 * ClassName: FileUtil 
 * @Description: 截图和拷贝
 * @author wangsuqi
 * @date 2016年6月11日
 * @see 
 */
public class FileUtil {
	//图片最大为1M
	public static final int DATA_LEN=1024*1024;
	public static void readFile(String srcfile,DatagramPacket outpacket){
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(srcfile);
			int l = -1;
			byte[] b = new byte[DATA_LEN];
			while ((l = fis.read(b)) != -1) {
			}
			outpacket.setData(b);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				;
			}
		}
	}
	public static void wirteFile(String targetFile, byte[] b) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(targetFile);
			fos.write(b);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (fos != null) {
					fos.flush();
					fos.close();
				}
			} catch (IOException e) {
				;
			}
		}
	}
	//取文件名称
	public static String getFileName(String str) {
		System.out.println("reg"+str);
		String[] strs=str.split("/");
		str = strs[strs.length-1];
		System.out.println(str);
		return str;
    }
	//取文件后缀名
	public static String getFileExt(String str){
		return str.substring(str.lastIndexOf(".")+1);
	}
	public static void FileCopy(String oldFile, String newfile) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(oldFile);
			fos = new FileOutputStream(newfile);
			int l = -1;
			byte[] b = new byte[1024];
			while ((l = fis.read(b)) != -1) {
				fos.write(b, 0, l);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (fos != null) {
					fos.flush();
					fos.close();
				}
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				;
			}
		}
	}
	public static void main(String[] args){
//		FileCopy("assets/image/bg/title.png", "assets/image/bg/123.png");
		DatagramPacket packet=new DatagramPacket(new byte[1024*1024], 1024*1024);
		readFile("assets/image/bg/title.png", packet);
		wirteFile("assets/image/bg/123.png", packet.getData());
	}
}
