package soft.chess.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import soft.chess.client.GameRoomClient;
import soft.chess.client.wuziqiFinalClient;
import soft.chess.client.GameHall.GameRoomInfoPanel;
import soft.chess.domain.ChessBean;
import soft.chess.domain.GameRecord;
import soft.chess.domain.Player;
import soft.chess.domain.RoomBean;
import soft.chess.util.BroadcastInRoomMsgUtil;
import soft.chess.util.GameChessServerMsgUtil;
import soft.chess.util.JsonUtil;
import soft.chess.util.SocketUtil;

public class ChessGameAction implements ActionListener{
	public static final String GAME_BEGIN="GAME_BEGIN";
	public static final String SEND_CHAT="SEND_CHAT";
	public static final String JOIN_AUDIENCE="JOIN_AUDIENCE";
	//定义发给Game服务器的Socket
	private DatagramSocket singlesocket;
	private DatagramPacket outPacket;
	
	//定义房间组播里的MulticastSocket实例
//	private MulticastSocket RoomcastSocket = null;	
//	private DatagramPacket RoomcastoutPacket = null;
	//定义游戏组播里的
	private MulticastSocket gameCastSocket = null;	
	private DatagramPacket gameCastoutPacket = null;
	private String actionName;
	
	private JFrame parentframe;
	
	private JPanel parentPanel;
	public ChessGameAction(String type) {
		this.actionName=type;
		try{
		//使用随机端口
		this.singlesocket=SocketUtil.getSingleSocket();
		//发送到游戏服务器的数据报
		this.outPacket=SocketUtil.getChessGameServerPacket();
		//游戏组播
		this.gameCastSocket=SocketUtil.joinGameBroadcast();
		this.gameCastoutPacket=SocketUtil.getGameBroadcastPacket();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void setParentframe(JFrame parentframe) {
		this.parentframe = parentframe;
	}
	
	public void setParentPanel(JPanel parentPanel) {
		this.parentPanel = parentPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// 如果是点击了开始游戏的按钮
		if(GAME_BEGIN.equals(actionName)){
			new Thread(new doGameBegin()).start();
		}
		if(SEND_CHAT.equals(actionName)){
			new Thread(new sendChatInfo()).start();
		}
		if(JOIN_AUDIENCE.equals(actionName)){
			new Thread(new sendAudienceJoin()).start();
		}
	}
	public void sendChessMove(ChessBean chess){
		new Thread(new sendChessInfo()).start();
	}
	public void sendPlayerEsc(RoomBean room){
		new Thread(new sendPlayerEscInfo(room)).start();
	}
	public void sendAudiEsc(){
		new Thread(new sendAudienceEscInfo()).start();
	}
	private class doGameBegin implements Runnable{
		private GameRoomClient roomFrame;
		@Override
		public void run() {
			roomFrame=(GameRoomClient) parentframe;
			// TODO Auto-generated method stub
			String send_info=JsonUtil.ObjectToStr(roomFrame.getRoom());
			send_info=GameChessServerMsgUtil
					.useGameServerMsg(roomFrame.getRoomId(),
							GameChessServerMsgUtil.GAME_BEGIN_ACTION, send_info);
			outPacket.setData(send_info.getBytes());
			try {
				singlesocket.send(outPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	private class sendChatInfo implements Runnable{
		private wuziqiFinalClient chessGameFrame;
		private RoomBean room;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			chessGameFrame=(wuziqiFinalClient) parentframe;
			room=chessGameFrame.getRoom();
			String send_info=chessGameFrame.getChatInfo();
			if(send_info==null)return;
			send_info=BroadcastInRoomMsgUtil
					.useMyRoomMsg(room.getRoomid(),
							BroadcastInRoomMsgUtil.GAME_CHAT_HEAD, send_info);
			gameCastoutPacket.setData(send_info.getBytes());
			try {
				gameCastSocket.send(gameCastoutPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	private class sendChessInfo implements Runnable{
		private wuziqiFinalClient chessGameFrame;
		private ChessBean chess;
		private long room_id;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			chessGameFrame=(wuziqiFinalClient) parentframe;
			chess=chessGameFrame.getChess();
			room_id=chessGameFrame.getRoom().getRoomid();
			String send_info=JsonUtil.ObjectToStr(chess);
			send_info=GameChessServerMsgUtil
					.useGameServerMsg(room_id,
							GameChessServerMsgUtil.MOVE_CHESS, send_info);
			outPacket.setData(send_info.getBytes());
			try {
				singlesocket.send(outPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	private class sendPlayerEscInfo implements Runnable{
		private wuziqiFinalClient chessGameFrame;
		private RoomBean _room;
		private long room_id;
		public sendPlayerEscInfo(RoomBean room) {
			// TODO Auto-generated constructor stub
			_room=room;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			chessGameFrame=(wuziqiFinalClient) parentframe;
			room_id=chessGameFrame.getRoom().getRoomid();
			String send_info=JsonUtil.ObjectToStr(_room);
			send_info=GameChessServerMsgUtil
					.useGameServerMsg(room_id,
							GameChessServerMsgUtil.PLAYER_ESC, send_info);
			outPacket.setData(send_info.getBytes());
			try {
				//让服务器记录比赛结果
				singlesocket.send(outPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	private class sendAudienceJoin implements Runnable{
		private GameRoomInfoPanel grip;
		private String send_info;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			grip=(GameRoomInfoPanel) parentPanel;	
			send_info=grip.getSendInfo();
			outPacket.setData(send_info.getBytes());
			try {
				//让服务器获取观众信息
				singlesocket.send(outPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	private class sendAudienceEscInfo implements Runnable{
		private wuziqiFinalClient chessGameFrame;
		private long room_id;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			chessGameFrame=(wuziqiFinalClient) parentframe;
			room_id=chessGameFrame.getRoom().getRoomid();
			String send_info="";
			send_info=GameChessServerMsgUtil
					.useGameServerMsg(room_id,
							GameChessServerMsgUtil.AUDI_ESC, send_info);
			outPacket.setData(send_info.getBytes());
			try {
				//让服务器记录比赛结果
				singlesocket.send(outPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
}
