package soft.chess.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


import net.sf.json.JSONObject;
import soft.chess.client.GameRoomClient;
import soft.chess.client.GameRoomSettingClient;
import soft.chess.client.GameHall.GameHallMainPanel;
import soft.chess.client.GameHall.GameRoomInfoPanel;
import soft.chess.common.PageBean;
import soft.chess.domain.Player;
import soft.chess.domain.RoomBean;
import soft.chess.util.JsonUtil;
import soft.chess.util.RoomServerMsgUtil;
import soft.chess.util.SocketUtil;
import soft.chess.util.BroadcastInRoomMsgUtil;
import soft.chess.util.HallServerMsgUtil;
import soft.chess.util.showBusyDialog;

public class RoomAction implements ActionListener{
	public static final String SAVE_ROOM="save_room";
	public static final String JOIN_ROOM="join_room";
	public static final String QUERY_NEXT_ROOM_BY_PAGE="query_next_room_by_page"; 
	public static final String QUERY_PRE_ROOM_BY_PAGE="query_pre_room_by_page"; 
	public static final String QUERY_GOTO_ROOM_BY_PAGE="query_goto_room_by_page"; 
	public static final String SEND_CHAT="send_chat";
	public static final String SEND_READY="send_ready";
	private static boolean isReady=false;
	//定义发给Room服务器的Socket
	private DatagramSocket singlesocket;
	private DatagramPacket outPacket;
	
	//定义房间组播里的MulticastSocket实例
	private MulticastSocket RoomcastSocket = null;	
	private DatagramPacket RoomcastoutPacket = null;
	
	//指定名称（用键值对可以？）
	private String actionName;
	//外部Frame
	private JFrame frame;
	//外部JPanel
	private JPanel panel;
	//私有构造方法传入
	private RoomAction(Builder build) {
		this.actionName=build.actionName;
		this.frame=build.frame;
		this.panel=build.panel;
		try{
		//使用随机端口
		this.singlesocket=SocketUtil.getSingleSocket();
		//发送到服务器的数据包
		this.outPacket=SocketUtil.getRoomServerPacket();
		//与房间组播建立通信
		RoomcastSocket=SocketUtil.joinRoomBroadcast();
		RoomcastoutPacket=SocketUtil.getRoomBroadcastPacket();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	//公有内部建造器
	public static class Builder{
		private String actionName;
		private JFrame frame;
		private JPanel panel;
		public Builder(String actionName){
			this.actionName=actionName;
		}
		public Builder setOutterFrame(JFrame frame){
			this.frame=frame;
			return this;
		}
		public Builder setOutterPanel(JPanel panel){
			this.panel=panel;
			return this;
		}
		//建造方法
		public RoomAction build(){
			return new RoomAction(this);
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub		
		if(SAVE_ROOM.equals(actionName)){
			new Thread(new saveRoomInfo()).start();
		}
		if(JOIN_ROOM.equals(actionName)){
			new Thread(new joinRoomInfo()).start();
		}
		if(QUERY_NEXT_ROOM_BY_PAGE.equals(actionName)){
			GameHallMainPanel ghmp=(GameHallMainPanel) panel;
			if(!ghmp.NextPage())return;
			new Thread(new queryRoomsByPage(ghmp)).start();
		}
		if(QUERY_PRE_ROOM_BY_PAGE.equals(actionName)){
			GameHallMainPanel ghmp=(GameHallMainPanel) panel;
			if(!ghmp.PrePage())return;
			new Thread(new queryRoomsByPage(ghmp)).start();
		}
		if(QUERY_GOTO_ROOM_BY_PAGE.equals(actionName)){
			GameHallMainPanel ghmp=(GameHallMainPanel) panel;
			ghmp.GotoPage();
			new Thread(new queryRoomsByPage(ghmp)).start();
		}
		if(SEND_CHAT.equals(actionName)){
			new Thread(new sendChatInfo()).start();
		}
		if(SEND_READY.equals(actionName)){
			new Thread(new sendReadyInfo()).start();
		}
		
	}
	//构造游戏大厅时会触发这个方法,获取当前数据库中房间记录的第一页
	public void getInitRooms(){
		new Thread(new queryRoomsByPage((GameHallMainPanel) panel)).start();
	}
	//关闭房间窗口时会触发这个方法
	public void leaveRoom(){
		new Thread(new leaveRoomInfo()).start();
	}
	public void sendInfoToRoomBroadcast(String info) throws IOException{
		RoomcastoutPacket.setData(info.getBytes());
		RoomcastSocket.send(RoomcastoutPacket);
	}
	public void sendInfoToRoomServer(String info) throws IOException{
		outPacket.setData(info.getBytes());
		singlesocket.send(outPacket);
	}
	//获取后的房间信息
	private String getUpdateOrDelRoomInfo(RoomBean room) throws IOException{
		String roomInfo=JsonUtil.ObjectToStr(room);
		if(room.getNum()==0){
			//如果房间里完全没人了，删除本房间
			roomInfo=RoomServerMsgUtil
					.setAction(RoomServerMsgUtil.DELETE_ROOM_ACTION,
							roomInfo);
		}
		else{
			roomInfo=RoomServerMsgUtil
					.setAction(RoomServerMsgUtil.UPDATE_ROOM_ACTION,
							roomInfo);
		}
		return roomInfo;
	}
	//获取用户信息
	private String getPlayerInfo(Player player,String type,long roomId) throws IOException{
		String playerInfo=JsonUtil.ObjectToStr(player);
		//发消息前设置消息头
		playerInfo=BroadcastInRoomMsgUtil.useMyRoomMsg(roomId, type, playerInfo);
		return playerInfo;
	}
	//进行分页查询，发送一个pageBean，返回一个pageBean
	private class queryRoomsByPage implements Runnable{
		private GameHallMainPanel _ghmp;
		public queryRoomsByPage(GameHallMainPanel ghmp) {
			// TODO Auto-generated constructor stub
			this._ghmp=ghmp;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				PageBean roomPage=_ghmp.getRoomPage();
				String req=JsonUtil.ObjectToStr(roomPage);
				req=RoomServerMsgUtil.setAction(RoomServerMsgUtil.FIND_ROOMS_BYPAGE_ACTION, req);	
				outPacket.setData(req.getBytes());
				singlesocket.send(outPacket);
				}catch(IOException e){
					e.printStackTrace();
				}
		}	
	}
	//点击了大厅房间小面板上的挑战按钮,发给房间组播加入者的信号
	//更新房间信息(房间人数+1),把房间信息发给房间服务器
	private class joinRoomInfo implements Runnable{
		private RoomBean room;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			GameRoomInfoPanel grip=(GameRoomInfoPanel) panel;
			Player myself=grip.getGhmp().getPlayer();
			this.room=grip.getRoom();
			//如果游客身份，不能参加游戏
			if(room.getNum()==2){
				JOptionPane.showMessageDialog(null,"该游戏最多只能两个人玩","人数已满",JOptionPane.ERROR_MESSAGE);
			}
			else{
				try {
				//注入保存在小面板对象中的房间对象
				GameRoomClient grc=GameRoomClient.getInstance(room);
				//更新房间的状态
				grc.setMyself(myself);
				grc.JoinRoomState(myself);	
			
				//房间服务器端发出更新后的房间状态
				String send_sever_info=getUpdateOrDelRoomInfo(room);
				sendInfoToRoomServer(send_sever_info);
				//向房间内的组播发出有玩家加入的UDP数据报
				String send_broadcast_info=getPlayerInfo(myself,BroadcastInRoomMsgUtil.JOIN_HEAD,room.getRoomid());
				sendInfoToRoomBroadcast(send_broadcast_info);
				
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//关闭先前的组件
				grip.getGhmp().CreatedRoom();
			}
		}
		
	}
	//用户关闭窗口时，发出离开用户的信息给大厅组播
	//更新房间状态，再把房间状态发出给房间服务器
	//关闭掉房间窗口，重新进入游戏大厅中
	private class leaveRoomInfo implements Runnable{
		private Player myself;
		private RoomBean room;
		private GameRoomClient grc;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			grc=(GameRoomClient)frame;
			myself=grc.getMyself();
			//注入保存在小面板对象中的房间对象
			//更新房间的状态
			try {
				grc.leaveRoomState();
				room=grc.getRoom();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				//房间服务器端发出更新后的房间状态
				String send_sever_info=getUpdateOrDelRoomInfo(room);
				sendInfoToRoomServer(send_sever_info);
				//向房间内的组播发出有玩家加入的UDP数据报
				String send_broadcast_info=getPlayerInfo(myself,BroadcastInRoomMsgUtil.LEAVE_HEAD,grc.getRoomId());
				sendInfoToRoomBroadcast(send_broadcast_info);
				//返回大厅
				grc.backToHall();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}	
	}
	//接收一个PlayerRoom对象，发送给房间服务器
	//接收到服务器赋予ID后的PlayerRoom对象
	private class saveRoomInfo implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				GameRoomSettingClient grsc=(GameRoomSettingClient) frame;						
				RoomBean room=grsc.getRoom();
				if(room==null)return;
				//输入有效才通信
				JSONObject json=JSONObject.fromObject(room);
				//客服端发错信号将会导致服务器崩溃，如何改进?
				String roomInfo=RoomServerMsgUtil.setAction(RoomServerMsgUtil.SAVE_ROOM_ACTION, json.toString());
				
				outPacket.setData(roomInfo.getBytes());
				singlesocket.send(outPacket);
				}catch(IOException e){
					e.printStackTrace();
				}
		}	
	}
	private class sendReadyInfo implements Runnable{

		@Override
		public void run() {
			// 如果已准备，发送取消准备的消息
			GameRoomClient grc=(GameRoomClient) frame;
			long roomid=grc.getRoomId();
			String send_info="";		
			if(isReady){
				send_info=BroadcastInRoomMsgUtil.useMyRoomMsg(roomid, BroadcastInRoomMsgUtil.DISREADY_GAME, send_info);
				isReady=false;
			}
			//如果未准备，发送准备的消息
			else {
				send_info=BroadcastInRoomMsgUtil.useMyRoomMsg(roomid, BroadcastInRoomMsgUtil.READY_GAME, send_info);
				isReady=true;
			}
			RoomcastoutPacket.setData(send_info.getBytes());
			try {
				RoomcastSocket.send(RoomcastoutPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	private class sendChatInfo implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			GameRoomClient grc=(GameRoomClient) frame;
			String send_info=grc.getChatInfo();
			RoomcastoutPacket.setData(send_info.getBytes());
			try {
				RoomcastSocket.send(RoomcastoutPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
