package soft.chess.server.gameHall;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import soft.chess.dao.Dao;
import soft.chess.domain.Player;
import soft.chess.util.JsonUtil;
import soft.chess.util.HallServerMsgUtil;
import soft.chess.util.SocketUtil;

public class LoginServer implements Runnable{
	
	private String info;
	private DatagramPacket outpacket;
	private DatagramSocket socket;
	private String send_info;
	
	public LoginServer(String _info,DatagramPacket _inpacket,DatagramSocket _socket) {
		// TODO Auto-generated constructor stub
		info=HallServerMsgUtil.getMsg(_info);
		socket=_socket;
		outpacket=new DatagramPacket(new byte[0], 0, _inpacket.getAddress(), _inpacket.getPort());
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Player player=(Player) JsonUtil.StrParseObject(info, Player.class);
		//查询数据库
		Dao dao=Dao.getInstance();
		player=(Player) dao.queryLogin(player.getName(), player.getPassword());
		if(player==null){
			send_info="reject";
		}
		else{			
			//把查询到的对象转json字符串发送，防止死循环递归调用
			send_info=JsonUtil.ObjectToStr(player);
		}
		send_info=HallServerMsgUtil.setAction(HallServerMsgUtil.LOGIN_ACTION, send_info);
		outpacket.setData(send_info.getBytes());
		try {
			socket.send(outpacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
