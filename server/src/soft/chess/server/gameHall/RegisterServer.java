package soft.chess.server.gameHall;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import net.sf.json.JSONObject;
import soft.chess.dao.Dao;
import soft.chess.domain.Player;
import soft.chess.util.HallServerMsgUtil;
import soft.chess.util.JsonUtil;

public class RegisterServer implements Runnable {
		
	private String info;
	private DatagramPacket outpacket;
	private DatagramSocket socket;
	private String send_info;
	
	public RegisterServer(String _info,DatagramPacket _inpacket,DatagramSocket _socket) {
		// TODO Auto-generated constructor stub
		info=HallServerMsgUtil.getMsg(_info);
		socket=_socket;
		outpacket=new DatagramPacket(new byte[0], 0, _inpacket.getAddress(), _inpacket.getPort());
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		JSONObject getjson=JSONObject.fromObject(info);
		Player player=new Player();
		player=(Player)JSONObject.toBean(getjson, Player.class);
		//插入数据
		Dao dao=Dao.getInstance();
		long pid=dao.saveAndGetId(player);
		player.setId(pid);
		if(pid==-1){
			send_info="reject";
		}
		else{
			send_info=JsonUtil.ObjectToStr(player);
		}
		send_info=HallServerMsgUtil.setAction(HallServerMsgUtil.REGIS_ACTION, send_info);
		outpacket.setData(send_info.getBytes());
		try {
			socket.send(outpacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
