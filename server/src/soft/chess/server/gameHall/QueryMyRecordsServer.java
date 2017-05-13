package soft.chess.server.gameHall;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import soft.chess.dao.Dao;
import soft.chess.domain.Player;
import soft.chess.util.HallServerMsgUtil;
import soft.chess.util.JsonUtil;

public class QueryMyRecordsServer implements Runnable{
	private DatagramPacket inpack;
	private DatagramPacket outpack;
	private DatagramSocket socket;
	private long player_id;
	private Player player;
	public QueryMyRecordsServer(DatagramPacket inpack,DatagramSocket socket) {
		// TODO Auto-generated constructor stub
		this.inpack=inpack;
		this.socket=socket;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.outpack=new DatagramPacket(new byte[0], 0,
				inpack.getAddress(), inpack.getPort());
		String msg=new String(inpack.getData());
		player_id=Long.parseLong(HallServerMsgUtil.getMsg(msg).trim());
		
		Dao dao=Dao.getInstance();
		player=dao.queryPlayerGameRecords(player_id);
		String send_msg=JsonUtil.ObjectToStr(player);
		send_msg=HallServerMsgUtil.setAction(HallServerMsgUtil.QUERY_MY_RECORDS_ACTION,send_msg);
		outpack.setData(send_msg.getBytes());
		try {
			socket.send(outpack);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
