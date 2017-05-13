package soft.chess.receiveData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.sf.json.JSONObject;
import soft.chess.client.GameRoomClient;
import soft.chess.client.GameRoomSettingClient;
import soft.chess.client.LoginClient;
import soft.chess.client.RegisterClient;
import soft.chess.client.ShowRecordsClient;
import soft.chess.client.GameHall.GameHallMainPanel;
import soft.chess.client.GameHall.OnlinePlayerPanel;
import soft.chess.common.PageBean;
import soft.chess.domain.ChessBean;
import soft.chess.domain.Player;
import soft.chess.domain.RoomBean;
import soft.chess.util.BroadcastMsgUtil;
import soft.chess.util.GameChessServerMsgUtil;
import soft.chess.util.HallServerMsgUtil;
import soft.chess.util.JsonUtil;
import soft.chess.util.RoomServerMsgUtil;
import soft.chess.util.SocketUtil;
import soft.chess.util.showBusyDialog;

//直接接收来自服务器的信号
public class ReceiveDataFromServer implements Runnable {
	private GameHallMainPanel ghmp;
	private boolean isReceive=true;
	private DatagramSocket singleSocket;
	//定义每个数据报的最大大小为100K
	private static final int DATA_LEN = 1024*100;
	
	private DatagramPacket inPacket = 
			new DatagramPacket(new byte[0] , 0);
	//显示等待图标
	private JFrame showwait;
	public ReceiveDataFromServer(GameHallMainPanel ghmp) {
		// TODO Auto-generated constructor stub
		this.ghmp=ghmp;
		try {
			this.singleSocket=SocketUtil.getSingleSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//接收数据报
				System.out.println("开始监听来自服务器的信号...");
				try{
					while(true)
					{	
					    //读取Socket中的数据，读到的数据放在inPacket所封装的字节数组里。
						inPacket.setData(new byte[DATA_LEN]);
//						showwait=new showBusyDialog("正在连接");
						singleSocket.receive(inPacket);
//						showwait.dispose();
						//终止线程
						if(!isReceive){
							break;
						}
						String info=new String(inPacket.getData());
						//由于socket缓冲区没有清空导致问题
						System.out.println("来自服务器的信号:"+info);
						//如果是房间列表消息
						if(RoomServerMsgUtil.isAction(RoomServerMsgUtil.FIND_ROOMS_BYPAGE_ACTION, info)){
							new Thread(new ShowRoomsByPage(info)).start();
						}
						//如果是新建房间消息
						if(RoomServerMsgUtil.isAction(RoomServerMsgUtil.SAVE_ROOM_ACTION, info)){
							new Thread(new IntoRoom(info)).start();
						}
						//如果是验证登录消息
						if(HallServerMsgUtil.isAction(HallServerMsgUtil.LOGIN_ACTION, info)){
							new Thread(new CheckLogin(info)).start();
						}
						//如果是验证注册消息
						if(HallServerMsgUtil.isAction(HallServerMsgUtil.REGIS_ACTION, info)){
							new Thread(new CheckRegister(info)).start();
						}
						//如果是玩家列表消息
						if(HallServerMsgUtil.isAction(HallServerMsgUtil.QUERY_ALL_PLAYERS_ACTION, info)){
							new Thread(new ShowAllPlayers(info)).start();
						}
						//如果是查询战绩的消息
						if(HallServerMsgUtil.isAction(HallServerMsgUtil.QUERY_MY_RECORDS_ACTION, info)){
							new Thread(new ShowMyRecords(info)).start();
						}
						//如果是查询棋局信息
						if(HallServerMsgUtil.isAction(HallServerMsgUtil.QUERY_CHESSES, info)){
							new Thread(new ShowChesses(info)).start();
						}
						//如果是发送图片消息头
						if(HallServerMsgUtil.isAction(HallServerMsgUtil.GET_IMG, info)){
							new Thread(new getImg(info)).start();
						}
						//如果是发送房间中图片的消息头
						if(HallServerMsgUtil.isAction(HallServerMsgUtil.GET_ROOM_IMG, info)){
							new Thread(new getRoomImg(info)).start();
						}
					}
				}catch(IOException e){
					e.printStackTrace();
				}
			}
	private class getRoomImg implements Runnable{
		private String info;
		public getRoomImg(String info) {
			// TODO Auto-generated constructor stub
			this.info=info;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String filename=HallServerMsgUtil.getMsg(info);
			String iconpath="";
			//如果服务器上文件存在,开始接收
			if(!filename.equals("nodata")){
				iconpath="roomImg/"+filename;
				try {
					ReceiveFileDataFromServer recefile=new ReceiveFileDataFromServer();
					recefile.Get(iconpath);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				iconpath="assets/image/playerIcon/Portrait.png";
			}
			GameRoomClient.getOpponentP().loadImg(iconpath);
		}
		
	}
	private class getImg implements Runnable{
		private String info;
		public getImg(String info) {
			// TODO Auto-generated constructor stub
			this.info=info;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String filename=HallServerMsgUtil.getMsg(info);
			String iconpath="";
			//如果服务器上文件存在,开始接收
			if(!filename.equals("nodata")){
				iconpath="playerIcon/"+filename;
				try {
					ReceiveFileDataFromServer recefile=new ReceiveFileDataFromServer();
					recefile.Get(iconpath);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				iconpath="assets/image/playerIcon/Portrait.png";
			}
			ghmp.getPlayerp().loadImg(iconpath);
		}
		
	}
	private class ShowAllPlayers implements Runnable{
		private String info;
		public ShowAllPlayers(String info) {
			this.info=info;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			info=HallServerMsgUtil.getMsg(info);
			//字符串太长会导致接收不了完整的数据
			List<Player> players=JsonUtil.StrParseArray(info, Player.class);
			OnlinePlayerPanel opp=ghmp.getOnlinePlayerListp();
			opp.playerInfoInit(players);
		}
		
	}
	private class ShowRoomsByPage implements Runnable{
		private String info;
		public ShowRoomsByPage(String info) {
			this.info=info;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			info=RoomServerMsgUtil.getMsg(info);
			System.out.println("收到房间的分页查询信息:"+info);
			if(info.equals("nodata")){
				ghmp.ShowRoomsByPage(null);
			}
			else{
				PageBean page=(PageBean) JsonUtil.StrParseObject(info, PageBean.class);			
				//在大厅中显示所有房间信息
				ghmp.ShowRoomsByPage(page);
			}
		}
		
	}
	private class IntoRoom implements Runnable{
		private String info;
		private RoomBean room;
		private GameRoomSettingClient grsc;
		public IntoRoom(String info) {
			this.info=info;
		}
		@Override
		public void run() {	
			info=RoomServerMsgUtil.getMsg(info);
			room=(RoomBean) JsonUtil.StrParseObject(info, RoomBean.class);
			grsc=ghmp.getGrsc();
			grsc.dispose();
			grsc.CreatedRoom(room);
		}
	}
	private class CheckLogin implements Runnable{
		private String info;
		private Player player;
		private LoginClient logc;
		public CheckLogin(String info) {
			this.info=info;
		}
		@Override
		public void run() {
			info=HallServerMsgUtil.getMsg(info);
			logc=ghmp.getVisitorp().getLoginc();
			//验证成功		
			if(!info.equals("reject")){
				player=(Player) JsonUtil.StrParseObject(info, Player.class);
				//变更游戏大厅主窗口的状态
				logc.hasLogin(player);
				logc.dispose();
			}
			//验证失败
			else{
				JOptionPane.showMessageDialog(null, "用户信息输入有误","错误提示",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	private class CheckRegister implements Runnable{
		private String info;
		private RegisterClient regisc;
		private Player player;
		public CheckRegister(String info) {
			this.info=info;
		}
		@Override
		public void run() {
			info=HallServerMsgUtil.getMsg(info);
			regisc=ghmp.getVisitorp().getRegc();
			//验证成功		
			if(!info.equals("reject")){
				player=(Player) JsonUtil.StrParseObject(info, Player.class);
				//注册成功登录跳转
				regisc.hasLogin(player);
				regisc.dispose();
			}
			//验证失败
			else{
				JOptionPane.showMessageDialog(null, "注册被拒绝","错误提示",JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	private class ShowMyRecords implements Runnable{
		private String info;
		private ShowRecordsClient src;
		private Player player;
		public ShowMyRecords(String info) {
			this.info=info;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			info=HallServerMsgUtil.getMsg(info);
			player=(Player) JsonUtil.StrParseObject(info, Player.class);
			src=ghmp.getSrc();
			src.hasRecords(player);
		}
		
	}
	private class ShowChesses implements Runnable{
		private String info;
		public ShowChesses(String info) {
			// TODO Auto-generated constructor stub
			this.info=info;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			info=HallServerMsgUtil.getMsg(info);
			List<ChessBean> chesses=JsonUtil.StrParseArray(info, ChessBean.class);
			//切换界面
			try {
				ghmp.getCurrenRoomp().ViewGame(chesses);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
}
