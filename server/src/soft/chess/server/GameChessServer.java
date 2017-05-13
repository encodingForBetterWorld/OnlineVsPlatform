package soft.chess.server;
import java.io.*;
import java.net.*;
import javax.swing.*;

import soft.chess.dao.Dao;
import soft.chess.domain.ChessBean;
import soft.chess.domain.GameRecord;
import soft.chess.domain.Player;
import soft.chess.domain.RoomBean;

import soft.chess.util.BroadcastInRoomMsgUtil;
import soft.chess.util.BroadcastMsgUtil;
import soft.chess.util.GameChessServerMsgUtil;
import soft.chess.util.HallServerMsgUtil;
import soft.chess.util.JsonUtil;
import soft.chess.util.RoomServerMsgUtil;
import soft.chess.util.SocketUtil;
import soft.chess.util.wuziqiConstants;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class GameChessServer extends JFrame{
	private static final String SERVER_ADDRESS="127.0.0.1";
	public static final int CHESS_GAME_PORT = 8008;
    // 定义每个数据报的最大大小为4KB  
    private static final int DATA_LEN = 4096; 
	//记录棋局信息
    //房间ID,对应的房间中棋局信息,观众进入游戏时
    //把对应房间ID的棋局信息发送给客户端
	private static ConcurrentHashMap<Long, byte[][]> RoomChesses;
	//存储对应的房间信息
	private static ConcurrentHashMap<Long, RoomBean> Rooms;
	
	private static DatagramSocket serverSocket = null;
	private static InetAddress serverAddress = null;
	//发送到游戏组播
	private static MulticastSocket gameCastSocket=null;
	private static DatagramPacket gameCastPacket=null;
	//发送到房间组播
	private static MulticastSocket roomCastSocket=null;
	private static DatagramPacket roomCastPacket=null;
	//发送到大厅组播
	private static MulticastSocket hallCastSocket=null;
	private static DatagramPacket hallCastPacket=null;
	//发送到room服务器
	private static DatagramPacket roomServerPacket=null;
	public static void main(String[] args)
	{
		try {
			new GameChessServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public GameChessServer() throws IOException
	{
		Rooms=new ConcurrentHashMap<>();
		
		RoomChesses=new ConcurrentHashMap<>();
		
		serverAddress=InetAddress.getByName(SERVER_ADDRESS);
    	serverSocket=new DatagramSocket(CHESS_GAME_PORT, serverAddress);
    	
    	gameCastSocket=SocketUtil.joinGameBroadcast();
    	roomCastSocket=SocketUtil.joinRoomBroadcast();
    	hallCastSocket=SocketUtil.joinHallBroadcast();
    	
    	gameCastPacket=SocketUtil.getGameBroadcastPacket();
    	roomCastPacket=SocketUtil.getRoomBroadcastPacket();
    	hallCastPacket=SocketUtil.getHallBroadcastPacket();
    	
    	
    	roomServerPacket=SocketUtil.getRoomServerPacket();
    	while(true){
    		//客户端点击开始游戏后，会发给服务器本房间的信息
    		DatagramPacket inpacket=new DatagramPacket(new byte[DATA_LEN], DATA_LEN);
    		serverSocket.receive(inpacket);
    		String info=new String(inpacket.getData());
    		System.out.println("游戏服务器收到:"+info);
    		//如果开启了新的游戏
    		if(GameChessServerMsgUtil
    				.isAction(GameChessServerMsgUtil.GAME_BEGIN_ACTION, info)){
    			new Thread(new HandleGameBegin(info)).start();
    		}
    		//如果是玩家移动了棋子，判断胜负
    		if(GameChessServerMsgUtil
    				.isAction(GameChessServerMsgUtil.MOVE_CHESS, info)){
    			new Thread(new HandleChessMove(info)).start();
    		}
    		//如果是玩家逃跑了，记录战绩
    		if(GameChessServerMsgUtil
    				.isAction(GameChessServerMsgUtil.PLAYER_ESC, info)){
    			new Thread(new HandlePlayerEsc(info)).start();
    		}
    		//如果是有观众游戏途中加入了房间
    		if(GameChessServerMsgUtil
    				.isAction(GameChessServerMsgUtil.AUDI_JOIN, info)){
    			new Thread(new HandleAudiJoin(info, inpacket)).start();
    		}
    		//如果是有观众游戏途中离开了房间
    		if(GameChessServerMsgUtil
    				.isAction(GameChessServerMsgUtil.AUDI_ESC, info)){
    			new Thread(new HandleAudiEsc(info)).start();
    		}
    	}
	}
	//发出游戏结束的信息到房间组播
	private synchronized void sendGameOverInfoToRoom(long room_id,String room_info) throws IOException{
			//通知正在游戏中的用户结束游戏
			String send_to_room=BroadcastInRoomMsgUtil
					.useMyRoomMsg(room_id,
							BroadcastInRoomMsgUtil.GAME_OVER,room_info);
			gameCastPacket.setData(send_to_room.getBytes());
			gameCastSocket.send(gameCastPacket);
	}
	//发出游戏结束的信息到房间组播
	private synchronized void sendAudienceInfoToRoom(long room_id,String audi_info) throws IOException{
				//通知正在游戏中的用户结束游戏
				String send_to_room=BroadcastInRoomMsgUtil
						.useMyRoomMsg(room_id,
								BroadcastInRoomMsgUtil.JOIN_AUDI_HEAD,audi_info);
				gameCastPacket.setData(send_to_room.getBytes());
				gameCastSocket.send(gameCastPacket);
	}
	//发出房间状态更新的消息到大厅组播
	private synchronized void sendRoomChangeInfoToHall(long room_id,String room_info) throws IOException{
		//更改大厅内的房间状态
		String send_to_hall=BroadcastMsgUtil
				.parseMsg(BroadcastMsgUtil.UPDATE_ROOM_HEAD, room_info);
		hallCastPacket.setData(send_to_hall.getBytes());
		hallCastSocket.send(hallCastPacket);
		//更改服务器上的房间状态
		String send_to_room_server=RoomServerMsgUtil
				.setAction(RoomServerMsgUtil.UPDATE_ROOM_ACTION, room_info);
		roomServerPacket.setData(send_to_room_server.getBytes());
		serverSocket.send(roomServerPacket);
	}
	//比赛记录存入数据库
	private synchronized void saveGameRecord(GameRecord record){		
		Dao dao=Dao.getInstance();
		dao.saveObject(record);
	}
	private class HandleAudiEsc implements Runnable{
		private String _info;
		private long room_id;
		private RoomBean room;
		public HandleAudiEsc(String info) {
			// TODO Auto-generated constructor stub
			_info=info;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			room_id=GameChessServerMsgUtil.getRoomId(_info);
			room=Rooms.get(room_id);
			room.setNum(room.getNum()-1);
			String room_info=JsonUtil.ObjectToStr(room);
			try {
				sendRoomChangeInfoToHall(room_id, room_info);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	private class HandleAudiJoin implements Runnable{
		private DatagramPacket singleOutPacket=null;
		private String _info;
		private long room_id;
		private Player audience;
		private RoomBean room;
		private byte[][] chesses; 
		private List<ChessBean> sendChesses;
		public HandleAudiJoin(String info,DatagramPacket inpacket) {
			// TODO Auto-generated constructor stub
			singleOutPacket=new DatagramPacket(new byte[0], 0, 
					inpacket.getAddress(),inpacket.getPort());
			_info=info;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			room_id=GameChessServerMsgUtil.getRoomId(_info);
			_info=GameChessServerMsgUtil.getMsg(_info);
			audience=(Player) JsonUtil.StrParseObject(_info, Player.class);
			
			room=Rooms.get(room_id);
			room.setNum(room.getNum()+1);
			
			String room_info=JsonUtil.ObjectToStr(room);
			String audi_info=JsonUtil.ObjectToStr(audience);
			try {
				//发送房间更新的信息到大厅组播
				sendRoomChangeInfoToHall(room_id, room_info);
				//发送观众信息到房间组播
				sendAudienceInfoToRoom(room_id, audi_info);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//发送房间的棋局信息给观众
			sendChesses=new ArrayList<>();
			chesses=RoomChesses.get(room_id);
			for(byte i=0;i<15;i++){
				for(byte j=0;j<15;j++){
					ChessBean chess=new ChessBean();
					chess.setX(i);
					chess.setY(j);
					chess.setColor(chesses[i][j]);
					sendChesses.add(chess);
				}
			}
			String chessesInfo=JsonUtil.<ChessBean>ArrayToStr(sendChesses);
			chessesInfo=HallServerMsgUtil.setAction(HallServerMsgUtil.QUERY_CHESSES, chessesInfo);
			singleOutPacket.setData(chessesInfo.getBytes());
			try {
				serverSocket.send(singleOutPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	private class HandlePlayerEsc implements Runnable{
		private long room_id;
		private RoomBean room;
		private GameRecord record;
		public HandlePlayerEsc(String info) {
			// TODO Auto-generated constructor stub
			room_id=GameChessServerMsgUtil.getRoomId(info);
			String roomStr=GameChessServerMsgUtil.getMsg(info);
			room=(RoomBean) JsonUtil.StrParseObject(roomStr, RoomBean.class);
			record=room.getRecord();
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//比赛记录存入数据库
			saveGameRecord(record);
			//移除棋局信息
			RoomChesses.remove(room_id);
			//移除房间信息
			Rooms.remove(room_id);
			//更改房间状态
			room.setRoomState((byte) 0);
			String room_info=JsonUtil.ObjectToStr(room);
			try {
				sendGameOverInfoToRoom(room_id, room_info);
				sendRoomChangeInfoToHall(room_id, room_info);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private class HandleGameBegin implements Runnable{
		private long room_id;
		private RoomBean room;
		//记录棋局信息
		private byte[][] chesses;
		
		private GameRecord record;
		
		public HandleGameBegin(String info) {
			room_id=GameChessServerMsgUtil.getRoomId(info);
			String roomStr=GameChessServerMsgUtil.getMsg(info);
			room=(RoomBean) JsonUtil.StrParseObject(roomStr, RoomBean.class);
		}
		@Override
		public void run() {
			// 申请空间
			chesses=new byte[15][15];
			for(int i=0;i<15;i++)
				for(int j=0;j<15;j++)
					chesses[i][j]=-1;
			//随机分配权限给房间中的玩家，不一定房主先走
			Random random=new Random();
			int i=random.nextInt(2);
			if(i==0){
				room.setBlackPlayer(room.getCreater());
				room.setWhitePlayer(room.getJoiner());
			}
			else{
				room.setBlackPlayer(room.getJoiner());
				room.setWhitePlayer(room.getCreater());
			}
			room.setRoomState((byte)1);
			
			//设置游戏开始时间
			record=new GameRecord();
			record.setBegintime(System.currentTimeMillis());
			room.setRecord(record);
			
			Rooms.put(room_id, room);
			RoomChesses.put(room_id, chesses);
			String room_info=JsonUtil.ObjectToStr(room);
			//发给房间组播，通知房间里的玩家进入游戏
			try {
				String send_to_room=BroadcastInRoomMsgUtil
					.useMyRoomMsg(room_id,
							BroadcastInRoomMsgUtil.BEGIN_GAME, room_info);
				roomCastPacket.setData(send_to_room.getBytes());
				roomCastSocket.send(roomCastPacket);
				//发给大厅组播，通知改变游戏大厅面板上的房间状态
				String send_to_hall=BroadcastMsgUtil
						.parseMsg(BroadcastMsgUtil.UPDATE_ROOM_HEAD, room_info);
				hallCastPacket.setData(send_to_hall.getBytes());
				hallCastSocket.send(hallCastPacket);
				//还要更改服务器上的房间状态
				String send_to_room_server=RoomServerMsgUtil
						.setAction(RoomServerMsgUtil.UPDATE_ROOM_ACTION, room_info);
				roomServerPacket.setData(send_to_room_server.getBytes());
				serverSocket.send(roomServerPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	//接收一个chess对象，判断棋局胜负后，发出chess对象
	private class HandleChessMove implements Runnable{
		private ChessBean chess;
		//存取棋局的信息
		private byte[][] chesses;
		private long room_id;
		private String _info;
		//判断棋局是否结束
		private boolean isOver=false;
		//判断是否是玩家一（黑方）赢了
		private boolean p1win=false;
		//用于棋局结束时
		private RoomBean room;
		private GameRecord record;
		
		public HandleChessMove(String info) {
			// TODO Auto-generated constructor stub	
			room_id=GameChessServerMsgUtil.getRoomId(info);
			_info=GameChessServerMsgUtil.getMsg(info);
		}
		@Override
		public void run() {	
			chess=(ChessBean) JsonUtil.StrParseObject(_info, ChessBean.class);
			chesses=RoomChesses.get(room_id);
			chesses[chess.getX()][chess.getY()]=chess.getColor();
			if(isFull()){
				isOver=true;
				chess.setChessState(wuziqiConstants.GameState.DRAW.getState());
			}
			byte token=chess.getColor();
			if(isWon(token)){
				isOver=true;
				if(token==1){
					chess.setChessState(wuziqiConstants.GameState.PLAYER1_WON.getState());
					p1win=true;
				}
				else if(token==0){
					chess.setChessState(wuziqiConstants.GameState.PLAYER2_WON.getState());
				}
			}
			else{
				chess.setChessState(wuziqiConstants.GameState.CONTINUE.getState());
			}
			String send_info=JsonUtil.ObjectToStr(chess);
			send_info=BroadcastInRoomMsgUtil
					.useMyRoomMsg(room_id,
							BroadcastInRoomMsgUtil.CHESS_MOVE, send_info);
			gameCastPacket.setData(send_info.getBytes());
			try {
				gameCastSocket.send(gameCastPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//如果棋局结束了
			if(isOver){
				room=Rooms.get(room_id);
				//移除存取的信息
				Rooms.remove(room_id);
				RoomChesses.remove(room_id);
				
				room.setNum(2);
				
				room.setRoomState((byte) 0);
				record=room.getRecord();
				record.setEndtime(System.currentTimeMillis());
				record.setGametime(record.getEndtime()-record.getBegintime());

				if(p1win){
					record.setWinner_id(room.getBlackPlayer().getId());
					record.setLoser_id(room.getWhitePlayer().getId());
				}else{
					record.setWinner_id(room.getWhitePlayer().getId());
					record.setLoser_id(room.getBlackPlayer().getId());
				}
				//保存比赛记录
				saveGameRecord(record);
				room.setRecord(record);
				
				String room_info=JsonUtil.ObjectToStr(room);
				//等待1秒
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//发送棋局结束的消息
				try {
					sendGameOverInfoToRoom(room_id, room_info);
					sendRoomChangeInfoToHall(room_id, room_info);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		private boolean isFull()//棋盘是否满
		{
			for(int i=0;i<15;i++)
				for(int j=0;j<15;j++)
					if(chesses[i][j]==-1)
						return false;
			return true;
		}
		private boolean isWon(byte token)//胜负判断，采用全局扫描的方法
		{
			for(int i=0;i<15;i++)
				for(int j=0;j<15-4;j++)
				if((chesses[i][j+0]==token)&&(chesses[i][j+1]==token)&&(chesses[i][j+2]==token)&&(chesses[i][j+3]==token)&&(chesses[i][j+4]==token))
					return true;
			for(int i=0;i<15;i++)
				for(int j=0;j<15-4;j++)
				if((chesses[j+0][i]==token)&&(chesses[j+1][i]==token)&&(chesses[j+2][i]==token)&&(chesses[j+3][i]==token)&&(chesses[j+4][i]==token))
					return true;
			for(int i=0;i<15-4;i++)
				for(int j=0;j<15-4;j++)
				if((chesses[i][j+0]==token)&&(chesses[i+1][j+1]==token)&&(chesses[i+2][j+2]==token)&&(chesses[i+3][j+3]==token)&&(chesses[i+4][j+4]==token))
					return true;
			for(int i=0;i<=10;i++)
				for(int j=14;j>=4;j--)
				if((chesses[i][j-0]==token)&&(chesses[i+1][j-1]==token)&&(chesses[i+2][j-2]==token)&&(chesses[i+3][j-3]==token)&&(chesses[i+4][j-4]==token))
					return true;
			return false;	
		}
		
	}
	
}