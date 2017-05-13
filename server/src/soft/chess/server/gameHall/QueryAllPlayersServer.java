package soft.chess.server.gameHall;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

import soft.chess.dao.Dao;
import soft.chess.domain.Player;
import soft.chess.util.HallServerMsgUtil;
import soft.chess.util.JsonUtil;

public class QueryAllPlayersServer implements Runnable {
	private DatagramPacket outpack;
	private DatagramSocket socket;
	private List<Player> players;
	public QueryAllPlayersServer(DatagramPacket inpack,DatagramSocket socket) {
		// TODO Auto-generated constructor stub
		this.outpack=new DatagramPacket(new byte[0], 0,
				inpack.getAddress(), inpack.getPort());
		this.socket=socket;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Dao dao=Dao.getInstance();
		players=dao.queryPlayers();
		String send_info=JsonUtil.ArrayToStr(players);
		send_info=HallServerMsgUtil
				.setAction(HallServerMsgUtil.QUERY_ALL_PLAYERS_ACTION, send_info);
		outpack.setData(send_info.getBytes());
		try {
			socket.send(outpack);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
