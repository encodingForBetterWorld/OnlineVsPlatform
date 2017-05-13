package soft.chess.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXBusyLabel;

import net.sf.json.JSONObject;
import soft.chess.client.GameRoomSettingClient;
import soft.chess.client.LoginClient;
import soft.chess.client.RegisterClient;
import soft.chess.client.ShowRecordsClient;
import soft.chess.client.GameHall.GameHallMainPanel;
import soft.chess.client.GameHall.GameHallSendMsgPanel;
import soft.chess.client.GameHall.OnlinePlayerPanel;
import soft.chess.domain.Announcement;
import soft.chess.domain.Player;
import soft.chess.domain.RoomBean;
import soft.chess.util.JsonUtil;
import soft.chess.util.BroadcastMsgUtil;
import soft.chess.util.HallServerMsgUtil;
import soft.chess.util.SocketUtil;
import soft.chess.util.showBusyDialog;
//负责收发报文，与服务器端通信
public class PlayerAction implements ActionListener{
	//定义action的类型
	public static final String LOGIN_ACTION="login";
	public static final String REGIS_ACTION="register";
	public static final String SEND_CHAT="send_chat";
	public static final String SEND_ANN="send_ann";
	public static final String QUERY_MY_RECORDS="QUERY_MY_RECORDS";
	public static final String QUERY_ANN="QUERY_ANN";
	//随机套接字
	private DatagramSocket singlesocket;
	//发送到大厅服务器的数据报
	private DatagramPacket hallServerOutPacket;
	//加入大厅组播的套接字
	private MulticastSocket hallBroadcastSocket;
	//发送到大厅组播的数据报
	private DatagramPacket hallBroadcastOutPacket;
	//指定名称（用键值对可以？）
	private String actionName;
	//外部Frame
	private JFrame frame;
	//外部Panel
	private JPanel panel;
	//私有构造方法传入
	private PlayerAction(Builder build) {
		this.actionName=build.actionName;
		this.frame=build.frame;
		this.panel=build.panel;
		try{
		//使用随机端口
		this.singlesocket=SocketUtil.getSingleSocket();
		//发送到服务器的数据包
		this.hallServerOutPacket=SocketUtil.getHallServerPacket();
		
		this.hallBroadcastSocket=SocketUtil.joinHallBroadcast();
		
		this.hallBroadcastOutPacket=SocketUtil.getHallBroadcastPacket();
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
		public PlayerAction build(){
			return new PlayerAction(this);
		}
	}
	//向服务器请求加载图片资源
	public void getImg(Player player,String type){
		new Thread(new getMyIcon(player,type)).start();
	}
	//把用户的状态发送给大厅组播
	public void sendPlayerStateToHallBroadcast(Player playerBean){
		String send_info=JsonUtil.ObjectToStr(playerBean);
		send_info=BroadcastMsgUtil.parseMsg(BroadcastMsgUtil.PLAYER_HEAD, send_info);
		hallBroadcastOutPacket.setData(send_info.getBytes());
		try {
			hallBroadcastSocket.send(hallBroadcastOutPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//把用户的状态发送给服务器
	public void sendPlayerStateToHallServer(Player playerBean){
		String send_info=JsonUtil.ObjectToStr(playerBean);
		send_info=HallServerMsgUtil.setAction(HallServerMsgUtil.UPDATE_PLAYER_ACTION, send_info);
		hallServerOutPacket.setData(send_info.getBytes());
		try {
			singlesocket.send(hallServerOutPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//构造游戏大厅时会触发这个方法,获取当前数据库中所有用户
	public void getInitPlayers(){
		new Thread(new queryAllplayers()).start();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if(LOGIN_ACTION.equals(actionName)){
			new Thread(new sendLoginInfo()).start();
		}
		else if(REGIS_ACTION.equals(actionName)){
			new Thread(new sendRegisterInfo()).start();
		}
		else if(SEND_CHAT.equals(actionName)){
			new Thread(new sendChatInfo()).start();
		}
		else if(SEND_ANN.equals(actionName)){
			new Thread(new sendAnnInfo()).start();
		}
		else if(QUERY_MY_RECORDS.equals(actionName)){
			new Thread(new queryMyRecords()).start();
		}
	}
	//获取服务器上的图片
	private class getMyIcon implements Runnable{
		private String send_info;
		private Player player;
		private String type;
		public getMyIcon(Player player,String type) {
			// TODO Auto-generated constructor stub
			this.player=player;
			this.type=type;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			send_info=player.getIcon();
			send_info=HallServerMsgUtil
					.setAction(type, send_info);
			hallServerOutPacket.setData(send_info.getBytes());
			try {
				singlesocket.send(hallServerOutPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	//查询数据库中的所有玩家信息
	private class queryAllplayers implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String send_info="";
			send_info=HallServerMsgUtil.setAction(HallServerMsgUtil.QUERY_ALL_PLAYERS_ACTION, send_info);
			hallServerOutPacket.setData(send_info.getBytes());
			try {
				singlesocket.send(hallServerOutPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	////接收一个Player对象，查询其是否以存入数据库，是则返回查询结果，否则弹出错误窗口
	private class sendLoginInfo implements Runnable{

		@Override
		public void run() {
			try{
			// TODO Auto-generated method stub
			LoginClient logc=(LoginClient) frame;			
			Player player=logc.getPlayer();
			if(player==null)return;
			//输入有效才进行通讯
			
			JSONObject json=JSONObject.fromObject(player);
			
			String loginInfo=HallServerMsgUtil.setAction(HallServerMsgUtil.LOGIN_ACTION, json.toString());
			
			hallServerOutPacket.setData(loginInfo.getBytes());
			singlesocket.send(hallServerOutPacket);
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
	}
	//接收一个Player对象，存入数据库
	private class sendRegisterInfo implements Runnable{

		@Override
		public void run() {
			try{
			RegisterClient regisc=(RegisterClient) frame;						
			Player playerBean=regisc.getPlayer();
			if(playerBean==null)return;
			JSONObject json=JSONObject.fromObject(playerBean);
			
			String regInfo=HallServerMsgUtil.setAction(HallServerMsgUtil.REGIS_ACTION, json.toString());
			
			hallServerOutPacket.setData(regInfo.getBytes());
			singlesocket.send(hallServerOutPacket);
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
	}	
	//用于大厅中发送聊天
	private class sendChatInfo implements Runnable{

		@Override
		public void run() {
			GameHallSendMsgPanel ghsmp=(GameHallSendMsgPanel) panel;
			String sendMsg=ghsmp.getChatMsg();
			if(sendMsg==null)return;
			sendMsg=BroadcastMsgUtil.parseMsg(BroadcastMsgUtil.CHAT_HEAD, sendMsg);
			hallBroadcastOutPacket.setData(sendMsg.getBytes());
			try {
				hallBroadcastSocket.send(hallBroadcastOutPacket);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	//用于大厅中发出公告
	private class sendAnnInfo implements Runnable{

		@Override
		public void run() {
			GameHallSendMsgPanel ghsmp=(GameHallSendMsgPanel) panel;
			String sendMsg=ghsmp.getAnnMsg();
			if(sendMsg==null)return;
			sendMsg=BroadcastMsgUtil.parseMsg(BroadcastMsgUtil.ANN_HEAD, sendMsg);
			hallBroadcastOutPacket.setData(sendMsg.getBytes());
			try {
				hallBroadcastSocket.send(hallBroadcastOutPacket);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	//用于查询用户的个人战绩
	private class queryMyRecords implements Runnable{
		private GameHallMainPanel ghmp;
		private Player myself;
		private String send_info;
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ghmp=(GameHallMainPanel) panel;
			myself=ghmp.getPlayer();
			
			send_info=""+myself.getId();
			
			send_info=HallServerMsgUtil
					.setAction(HallServerMsgUtil.QUERY_MY_RECORDS_ACTION, send_info);
			hallServerOutPacket.setData(send_info.getBytes());
			try {
				singlesocket.send(hallServerOutPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}

