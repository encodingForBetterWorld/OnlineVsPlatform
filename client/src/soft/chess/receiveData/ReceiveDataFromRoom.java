package soft.chess.receiveData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import soft.chess.client.GameHallClient;
import soft.chess.client.GameRoomClient;
import soft.chess.client.wuziqiFinalClient;
import soft.chess.domain.ChessBean;
import soft.chess.domain.Player;
import soft.chess.domain.RoomBean;
import soft.chess.util.BroadcastInRoomMsgUtil;
import soft.chess.util.BroadcastMsgUtil;
import soft.chess.util.JsonUtil;
import soft.chess.util.SocketUtil;
//用户处理从房间组播中传过来的信号
public class ReceiveDataFromRoom implements Runnable{
	private GameRoomClient roomc;
	
	private long room_id;
	private boolean isPause=true;
	//定义本程序的MulticastSocket实例
	private MulticastSocket roomSocket = null;
	//定义每个数据报的最大大小为4K
	private static final int DATA_LEN = 4096;
	//定义接收网络数据的字节数组
//	byte[] inBuff = new byte[DATA_LEN];
//	//以指定字节数组创建准备接受数据的DatagramPacket对象
	private DatagramPacket inPacket = 
	new DatagramPacket(new byte[0] , 0);
	public ReceiveDataFromRoom() throws IOException {
		// TODO Auto-generated constructor stub
		roomSocket=SocketUtil.joinRoomBroadcast();
	}
	
	public void setRoomc(GameRoomClient roomc) {
		this.roomc = roomc;
		this.room_id=roomc.getRoomId();
	}

	@Override
	public void run() {
		//接收数据报
		System.out.println("开始监听来自大厅组播的信号...");
		try{
			while(true)
			{
				while(isPause){}
				//读取Socket中的数据，读到的数据放在inPacket所封装的字节数组里。
				inPacket.setData(new byte[DATA_LEN]);
				roomSocket.receive(inPacket);
				//暂停线程
				if(isPause)continue;
				//开始根据消息头，过滤信号
				String info=new String(inPacket.getData());
				//由于socket缓冲区没有清空导致问题
				System.out.println("房间组播收到:"+info);
				//如果是由用户加入，开启处理用户加入的线程
				if(BroadcastInRoomMsgUtil
						.isMyMsg(room_id,
								BroadcastInRoomMsgUtil.JOIN_HEAD, info)){
					new Thread(new doJoin(info)).start();
				}
				else if(BroadcastInRoomMsgUtil
						.isMyMsg(room_id,
								BroadcastInRoomMsgUtil.LEAVE_HEAD, info)){
					new Thread(new doLeave(info)).start();
				}
				else if(BroadcastInRoomMsgUtil
						.isMyMsg(room_id,
								BroadcastInRoomMsgUtil.READY_GAME, info)){
					new Thread(new doReady()).start();
				}
				else if(BroadcastInRoomMsgUtil
						.isMyMsg(room_id,
								BroadcastInRoomMsgUtil.DISREADY_GAME, info)){
					new Thread(new doDisReady()).start();
				}
				else if(BroadcastInRoomMsgUtil
						.isMyMsg(room_id,
								BroadcastInRoomMsgUtil.CHAT_HEAD, info)){
					new Thread(new showChat(info)).start();
				}
				else if(BroadcastInRoomMsgUtil
						.isMyMsg(room_id,
								BroadcastInRoomMsgUtil.BEGIN_GAME, info)){
					new Thread(new gameBegin(info)).start();
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}	
	public void pause() {
		this.isPause = true;
	}
	public void beginReceive() {
		this.isPause = false;
	}
	private class doJoin implements Runnable{
		private String _info;
		public doJoin(String info) {
			// TODO Auto-generated constructor stub
			_info=BroadcastInRoomMsgUtil.getMsg(info);
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Player player=(Player) JsonUtil.StrParseObject(_info, Player.class);
			roomc.receivedPlayerJoin(player);
		}
		
	}
	private class doLeave implements Runnable{
		private String _info;
		public doLeave(String info) {
			// TODO Auto-generated constructor stub
			_info=BroadcastInRoomMsgUtil.getMsg(info);
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Player player=(Player) JsonUtil.StrParseObject(_info, Player.class);
			roomc.receivedPlayerLeave(player);
		}
		
	}
	private class doReady implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			roomc.ready();
		}
		
	}
	private class doDisReady implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			roomc.disready();
		}
		
	}
	private class showChat implements Runnable{
		private String _info;
		public showChat(String info) {
			// TODO Auto-generated constructor stub
			_info=BroadcastInRoomMsgUtil.getMsg(info);
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			roomc.showChatInfo(_info);
		}
		
	}
	private class gameBegin implements Runnable{
		private RoomBean room;
		public gameBegin(String info) {
			// TODO Auto-generated constructor stub
			String _info=BroadcastInRoomMsgUtil.getMsg(info);
			room=(RoomBean) JsonUtil.StrParseObject(_info, RoomBean.class);
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			roomc.beginChessGame(room);
		}
	}
}
