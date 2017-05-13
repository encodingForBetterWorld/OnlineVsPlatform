package soft.chess.client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.sf.json.JSONObject;
import soft.chess.action.RoomAction;
import soft.chess.client.GameHall.GameChatPanel;
import soft.chess.client.GameHall.GameHallMusicPlayerPanel;
import soft.chess.client.GameHall.GameHallSendMsgPanel;
import soft.chess.client.GameHall.GameRoomInfoPanel;
import soft.chess.client.GameHall.GameHallMainPanel;
import soft.chess.domain.Player;
import soft.chess.domain.RoomBean;
import soft.chess.receiveData.ReceiveDataFromHall;
import soft.chess.util.FrameUtil;
import soft.chess.util.RoomServerMsgUtil;
import soft.chess.util.SocketUtil;
import soft.chess.util.SoftConstant;
/**
 * 
 * ClassName: GameHallClient 
 * @Description: 游戏大厅的主界面
 * @author wangsuqi
 * @date 2016年10月5日
 * @see
 */
public class GameHallClient extends JFrame{
	//单例模式
	private static GameHallClient gameHall;
//	//定义大厅组播里的MulticastSocket实例
//	private static MulticastSocket socket = null;
//	
//	private DatagramPacket outPacket = null;
	
	//发消息
	private JPanel sendp;
	//接收消息
	private JPanel receivep;
	//游戏房间桌面
	private GameHallMainPanel roomtablep;
	//音乐播放器
	private GameHallMusicPlayerPanel musicPlayerp;
	//接收大厅信号的线程
	private ReceiveDataFromHall receiveInfoRun;
	private Thread receiveInfoThread;
	private GameHallClient() throws IOException{
		
		setLayout(null);
		setSize(1100, 700);
		setTitle("欢迎进入游戏大厅");
		
		sendp=new GameHallSendMsgPanel(this);
		sendp.setBounds(5, 590,245, 85);
		sendp.setBorder(SoftConstant.shadowborder);
		
		receivep=new GameChatPanel();
		receivep.setBounds(255, 590, 635, 85);
		receivep.setBorder(SoftConstant.shadowborder);
		
		musicPlayerp=new GameHallMusicPlayerPanel();
		musicPlayerp.setBounds(890, 590, 207, 85);
		musicPlayerp.setBorder(SoftConstant.shadowborder);
		musicPlayerp.beginMusic();
		
		roomtablep=new GameHallMainPanel(this);
		roomtablep.setBounds(5, 5, 1090, 585);
		roomtablep.setBorder(SoftConstant.shadowborder);
		
		FrameUtil.baseSetFrame(this,sendp,receivep,roomtablep,musicPlayerp);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				roomtablep.hasLogout();
				super.windowClosing(e);
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//启动接收信号线程
		receiveInfoRun=new ReceiveDataFromHall(this);
		receiveInfoThread=new Thread(receiveInfoRun);
		receiveInfoThread.start();
	}
	public void BackToGameHall() throws MalformedURLException{
		musicPlayerp.beginMusic();
	}
	//跳转进入游戏房间的回调方法
	public void LeaveGameHall() throws IOException{
		//关闭歌曲
		musicPlayerp.stopMusic();
//		//关闭输入框动画
//		((GameHallSendMsgPanel) sendp).exitHall();
		//关闭接收大厅信号的线程
//		receiveInfoRun.setReceive(false);
//		receiveInfoThread.resume();
		//关闭主界面
		GameHallClient.this.setVisible(false);
		//退出组播
//		SocketUtil.leaveBroadcast(socket);
	}
	public static GameHallClient getInstance() throws IOException{
		if(gameHall==null){
			gameHall=new GameHallClient();
		}
		gameHall.setVisible(true);
		return gameHall;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			getInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized GameChatPanel getReceivep() {
		return (GameChatPanel) receivep;
	}
	
	public synchronized GameHallMainPanel getRoomtablep() {
		return  roomtablep;
	}
	
	public synchronized GameHallSendMsgPanel getSendp() {
		return (GameHallSendMsgPanel) sendp;
	}
}
