package soft.chess.server.gameHall;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

import soft.chess.dao.Dao;
import soft.chess.domain.Announcement;
import soft.chess.util.JsonUtil;

public class QueryAllAnnsServer implements Runnable{
	private DatagramPacket inpacket;
	private DatagramPacket outpacket;
	private DatagramSocket socket;
	private List<Announcement> anns;
	public QueryAllAnnsServer(DatagramPacket inpack,DatagramSocket socket) {
		// TODO Auto-generated constructor stub
		this.inpacket=inpack;
		this.socket=socket;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.outpacket=new DatagramPacket(new byte[0], 0,
				inpacket.getAddress(), inpacket.getPort());
		Dao dao=Dao.getInstance();
		anns=dao.queryAnns();
		String send_info=JsonUtil.<Announcement>ArrayToStr(anns);
		outpacket.setData(send_info.getBytes());
		try {
			socket.send(outpacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
