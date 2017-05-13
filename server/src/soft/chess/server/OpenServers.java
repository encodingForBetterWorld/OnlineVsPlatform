package soft.chess.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class OpenServers {
	private static String filename;
	private static ServerSocket server;
	public synchronized static void setFilename(String filename){
		OpenServers.filename=filename;
	}
	public static void main(String[] args){
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			public void run() {
				new GameRoomServer();
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				new GameHallServer();
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				try {
					new GameChessServer();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		//接收文件的服务器
		new Thread(new Runnable() {
			public void run() {
			try {
				server = new ServerSocket(8888);   
		        for(;;){  
		            Socket socket;
					socket = server.accept(); 
		            new FTPServer().run(filename, socket);  
		        }  
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			}
		}).start();
	}
}

