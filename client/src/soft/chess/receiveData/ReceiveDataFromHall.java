package soft.chess.receiveData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import javax.swing.JTextArea;

import net.sf.json.JSONObject;
import soft.chess.client.GameHallClient;
import soft.chess.client.GameHall.GameHallMainPanel;
import soft.chess.domain.Player;
import soft.chess.domain.RoomBean;
import soft.chess.util.BroadcastMsgUtil;
import soft.chess.util.JsonUtil;
import soft.chess.util.SocketUtil;

//接收大厅组播中所有信号的总线程,接收到信号开启相应的分线程
public class ReceiveDataFromHall implements Runnable {
	private GameHallClient gameHall;
	private boolean isReceive=true;
	private MulticastSocket socket = null;
	//定义每个数据报的最大大小为4K
	private static final int DATA_LEN = 4096;
	//定义接收网络数据的字节数组
//	byte[] inBuff = new byte[DATA_LEN];
//	//以指定字节数组创建准备接受数据的DatagramPacket对象
	private DatagramPacket inPacket = 
	new DatagramPacket(new byte[0] , 0);
	public ReceiveDataFromHall(GameHallClient gameHall) throws IOException {
		// TODO Auto-generated constructor stub
		this.gameHall=gameHall;
		this.socket=SocketUtil.joinHallBroadcast();
	}
	@Override
	public void run() {
		//接收数据报
		System.out.println("开始监听来自大厅组播的信号...");
		try{
			while(true)
			{
			//读取Socket中的数据，读到的数据放在inPacket所封装的字节数组里。
				inPacket.setData(new byte[DATA_LEN]);
				socket.receive(inPacket);
				//终止线程
				if(!isReceive){
					break;
				}
				String info=new String(inPacket.getData());
				//由于socket缓冲区没有清空导致问题
				System.out.println(info);
				//如果是聊天消息，开启聊天线程
				if(BroadcastMsgUtil.isMsg(BroadcastMsgUtil.CHAT_HEAD, info)){
					new Thread(new dochat(info)).start();
					}
				//如果是用户上线，开启用户上线线程
				else if(BroadcastMsgUtil.isMsg(BroadcastMsgUtil.PLAYER_HEAD, info)){
					new Thread(new doPlayerOnline(info)).start();
				}
				//如果是更新了公告
				else if(BroadcastMsgUtil.isMsg(BroadcastMsgUtil.ANN_HEAD, info)){
					new Thread(new doAnn(info)).start();
				}
				//如果是添加了房间
				else if(BroadcastMsgUtil.isMsg(BroadcastMsgUtil.SAVE_ROOM_HEAD, info)){
					new Thread(new doSaveRoom(info)).start();
				}
				//如果是修改了房间
				else if(BroadcastMsgUtil.isMsg(BroadcastMsgUtil.UPDATE_ROOM_HEAD, info)){
					new Thread(new doUpdateRoom(info)).start();
				}
				//如果是删除了房间
				else if(BroadcastMsgUtil.isMsg(BroadcastMsgUtil.DELETE_ROOM_HEAD, info)){
					new Thread(new doDeleteRoom(info)).start();
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}	
	
	public synchronized void setReceive(boolean isReceive) {
		this.isReceive = isReceive;
	}

	//处理聊天
	private class dochat implements Runnable{
		private String info;
		public dochat(String info){
			this.info=info;
		}
		@Override
		public void run() {
			//接收数据报
			System.out.println("有聊天消息");
			System.out.println(info);
			info=BroadcastMsgUtil.getMsg(info);
			gameHall.getReceivep().hasMsg(info);		
		}	
	}
	
	//处理玩家上线
	private class doPlayerOnline implements Runnable{
		private String info;
		public doPlayerOnline(String info) {
			// TODO Auto-generated constructor stub
			this.info=info;
		}
		@Override
		public void run() {
			info=BroadcastMsgUtil.getMsg(info);
			JSONObject json=JSONObject.fromObject(info);
			Player playerBean=(Player)JSONObject.toBean(json, Player.class);
			//打印输出从socket中读取的内容
			gameHall.getRoomtablep().getOnlinePlayerListp().palyerStateChange(playerBean);	
		}	
	}
	//处理公告
	private class doAnn implements Runnable{
		private String info;
		public doAnn(String info){
			this.info=info;
		}
		@Override
		public void run() {
			//接收数据报
			System.out.println("有新的公告消息");
			System.out.println(info);
			info=BroadcastMsgUtil.getMsg(info);
			gameHall.getRoomtablep().addNumtip();
		}	
	}
	//处理添加房间
	private class doSaveRoom implements Runnable{
		private RoomBean room;
		public doSaveRoom(String info) {
			// TODO Auto-generated constructor stub
			room=(RoomBean) JsonUtil.StrParseObject(
					BroadcastMsgUtil.getMsg(info), RoomBean.class);
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			GameHallMainPanel ghmp=gameHall.getRoomtablep();
			ghmp.ShowSaveRoom(room);
		}
		
	}
	//处理添加房间
	private class doUpdateRoom implements Runnable{
		private RoomBean room;
		public doUpdateRoom(String info) {
			// TODO Auto-generated constructor stub
			room=(RoomBean) JsonUtil.StrParseObject(
					BroadcastMsgUtil.getMsg(info), RoomBean.class);
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			GameHallMainPanel ghmp=gameHall.getRoomtablep();
			ghmp.ShowUpdateRoom(room);
		}
		
	}
	//处理删除房间
	private class doDeleteRoom implements Runnable{
		private RoomBean room;
		public doDeleteRoom(String info) {
			// TODO Auto-generated constructor stub
			room=(RoomBean) JsonUtil.StrParseObject(
					BroadcastMsgUtil.getMsg(info), RoomBean.class);
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			GameHallMainPanel ghmp=gameHall.getRoomtablep();
			ghmp.HideDeleteRoom(room);
		}
		
	}
}

