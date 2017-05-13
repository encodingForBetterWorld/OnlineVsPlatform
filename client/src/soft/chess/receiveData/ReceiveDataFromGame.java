package soft.chess.receiveData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import soft.chess.client.GameRoomClient;
import soft.chess.client.wuziqiFinalClient;
import soft.chess.domain.ChessBean;
import soft.chess.domain.Player;
import soft.chess.domain.RoomBean;
import soft.chess.util.BroadcastInRoomMsgUtil;
import soft.chess.util.JsonUtil;
import soft.chess.util.SocketUtil;
/*
 * 监听来自游戏组播的信号
 */
public class ReceiveDataFromGame implements Runnable{
	private wuziqiFinalClient wzqfc;
	private long room_id;
	
	private boolean isPause=false;
	//定义本程序的MulticastSocket实例
	private MulticastSocket gameSocket = null;
	//定义每个数据报的最大大小为4K
	private static final int DATA_LEN = 4096*5;
	//定义接收网络数据的字节数组
//	byte[] inBuff = new byte[DATA_LEN];
//	//以指定字节数组创建准备接受数据的DatagramPacket对象
	private DatagramPacket inPacket = 
	new DatagramPacket(new byte[0] , 0);
	public ReceiveDataFromGame() {
		// TODO Auto-generated constructor stub
		try {
			gameSocket=SocketUtil.joinGameBroadcast();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setWzqfc(wuziqiFinalClient wzqfc) {
		this.wzqfc = wzqfc;
		this.room_id=wzqfc.getRoom().getRoomid();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		//接收数据报
			System.out.println("开始监听来自大厅组播的棋局信号...");
			try{
				while(true)
				{
					while(isPause){}
					//读取Socket中的数据，读到的数据放在inPacket所封装的字节数组里。
					inPacket.setData(new byte[DATA_LEN]);
					gameSocket.receive(inPacket);
					//暂停线程
					if(isPause)continue;
					//开始根据消息头，过滤信号
					String info=new String(inPacket.getData());
					//由于socket缓冲区没有清空导致问题
					System.out.println("房间组播收到游戏信号:"+info);
					if(BroadcastInRoomMsgUtil
							.isMyMsg(room_id,
									BroadcastInRoomMsgUtil.CHESS_MOVE, info)){
						new Thread(new chessMove(info)).start();
					}
					if(BroadcastInRoomMsgUtil
							.isMyMsg(room_id, 
									BroadcastInRoomMsgUtil.GAME_CHAT_HEAD, info)){
						new Thread(new showChat(info)).start();
					}
					if(BroadcastInRoomMsgUtil
							.isMyMsg(room_id, 
									BroadcastInRoomMsgUtil.GAME_OVER, info)){
						new Thread(new gameOver(info)).start();
					}
					if(BroadcastInRoomMsgUtil
							.isMyMsg(room_id, 
									BroadcastInRoomMsgUtil.JOIN_AUDI_HEAD, info)){
						new Thread(new AudienceJoin(info)).start();
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			}
	}
	private class chessMove implements Runnable{
		private String _info;
		private ChessBean chess;
		public chessMove(String info) {
			// TODO Auto-generated constructor stub
			_info=BroadcastInRoomMsgUtil.getMsg(info);
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			chess=(ChessBean) JsonUtil.StrParseObject(_info, ChessBean.class);
			try {
				wzqfc.ReceiveData(chess);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	private class showChat implements Runnable{
		private String msg;
		public showChat(String info) {
			// TODO Auto-generated constructor stub
			msg=BroadcastInRoomMsgUtil.getMsg(info);
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			wzqfc.showChatMsg(msg);
		}
	}
	private class gameOver implements Runnable{
		private RoomBean room;
		public gameOver(String info) {
			// TODO Auto-generated constructor stub
			String _info=BroadcastInRoomMsgUtil.getMsg(info);
			room=(RoomBean) JsonUtil.StrParseObject(_info, RoomBean.class);
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				wzqfc.gameOver(room);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private class AudienceJoin implements Runnable{
		private Player audience;
		public AudienceJoin(String info) {
			// TODO Auto-generated constructor stub
			String _info=BroadcastInRoomMsgUtil.getMsg(info);
			audience=(Player) JsonUtil.StrParseObject(_info, Player.class);
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			wzqfc.audienceJoin(audience);
		}
	}
	public void pause() {
		this.isPause = true;
	}
	public void beginReceive() {
		this.isPause = false;
	}
	public long getRoom_id() {
		return room_id;
	}
	
	
}
