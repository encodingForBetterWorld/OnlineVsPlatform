package soft.chess.server.gameHall;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import soft.chess.server.OpenServers;
import soft.chess.util.HallServerMsgUtil;

public class GetImgServer implements Runnable {
	private DatagramPacket outpacket;
	private DatagramSocket socket;
	private String type;
	private String info;
	private boolean hasData=false;
	public GetImgServer(String type,String info,DatagramPacket inpacket,DatagramSocket socket) {
		// TODO Auto-generated constructor stub
		outpacket=new DatagramPacket(new byte[0], 0,
				inpacket.getAddress(), inpacket.getPort());
		this.info=info;
		this.type=type;
		this.socket=socket;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		String filename=HallServerMsgUtil.getMsg(info);
		String iconpath="PlayerIcon/"+filename;
		File file=new File(iconpath);
		String send_info="";
		if(file.exists()){
			send_info=filename;
			//开启接收数据服务器
			hasData=true;
		}
		else{
			send_info="nodata";
		}
		send_info=HallServerMsgUtil.setAction(type, send_info);
		outpacket.setData(send_info.getBytes());
		try {
			socket.send(outpacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(hasData){
			OpenServers.setFilename(iconpath);
		}
	}

}
