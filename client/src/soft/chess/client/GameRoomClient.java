package soft.chess.client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import soft.chess.action.ChessGameAction;
import soft.chess.action.RoomAction;
import soft.chess.client.GameHall.GameChatPanel;
import soft.chess.client.GameHall.PlayerInfoPanel;
import soft.chess.domain.Player;
import soft.chess.domain.RoomBean;
import soft.chess.receiveData.ReceiveDataFromRoom;
import soft.chess.util.BroadcastInRoomMsgUtil;
import soft.chess.util.FrameUtil;
import soft.chess.util.ImageIconUtil;
import soft.chess.util.RoundRectButton;
import soft.chess.util.SoftConstant;
/**
 * room是一个可变对象，在本对象中，4个收发信息的方法(类的行为):
 * 收到进入，收到离开...发出进入，发出离开，都会引起它的状态改变
 * 当发送状态的对象RoomAction在调用这个对象时，它的状态一定是我期望的吗？
 * 如何使对象的可变性最小,能否优化？
 */
public class GameRoomClient extends JFrame{
	//是否是房主
	private static boolean ishost=false;
	//是否准备就绪
	private static boolean isReady=false;
	//单例模式
	private static GameRoomClient grc;
	//五子棋游戏界面
	private static wuziqiFinalClient gamec=null;
	//大厅界面
	private static GameHallClient ghc;
	//打开这个页面的用户
	private static Player myself;
	//房间对象
	private static RoomBean room;
	//创建房间者
	private static Player creater;
	//加入房间者
	private static Player joiner;
	//房间名称
	private static String title;
	//房间号
	private static long roomId;
	//显示创建者的信息面板
	private static PlayerInfoPanel createrP;
	//显示加入者的信息面板
	private static PlayerInfoPanel joinerP;
	//对手的信息面板
	private static PlayerInfoPanel opponentP;
	//显示房间聊天消息的面板
	private static GameChatPanel chatPanel;
	//显示房间中央的VS图片
	private JLabel vsLabel;
	//开始游戏按钮
	private static JButton beginBtn;
	//准备游戏按钮
	private static JButton prepareBtn;
	//发送房间消息按钮
	private JButton sendMsgBtn;
	//发送消息的输入框
	private JTextField sendMsgf;
	//接收并处理大厅组播传来信号的线程
	private static ReceiveDataFromRoom roomInforun=null;
	private Thread roomInfoThread=null;
	private GameRoomClient() throws IOException {
		//设置布局
		setLayout(null);		
		setSize(500,300);
		setLocation(SoftConstant.screenWidth/3,SoftConstant.screenHeight/3);
		setLocationByPlatform(false);

		createrP=new PlayerInfoPanel();
		joinerP=new PlayerInfoPanel();
		chatPanel=new GameChatPanel();
		vsLabel=new JLabel();
		beginBtn=new RoundRectButton("开始游戏", 120, 50);
		//需要等待
		beginBtn.setEnabled(false);
		
		prepareBtn=new RoundRectButton("点击准备", 120, 50);
		sendMsgBtn=new RoundRectButton("发送", 70, 30);
		sendMsgf=new JTextField();
		//字体
		sendMsgBtn.setFont(SoftConstant.userNamefont);
		sendMsgf.setFont(SoftConstant.userNamefont);
		
		createrP.setBounds(15, 5, 150, 185);
		joinerP.setBounds(335, 5, 150, 185);
		chatPanel.setBounds(260, 190, 230, 70);
		chatPanel.getScrollPane().setBounds(5, 5, 210, 50);
		vsLabel.setBounds(190, 10, 125, 125);
		beginBtn.setBounds(190, 130, 120, 50);
		prepareBtn.setBounds(190, 130, 120, 50);
		sendMsgf.setBounds(15, 220, 150, 30);
		sendMsgBtn.setBounds(165, 215, 70, 30);
		//特殊设置
		prepareBtn.setVisible(false);
		vsLabel.setIcon(ImageIconUtil.setImageIcon("assets/image/label/vs.png", vsLabel));
		//加边框
		chatPanel.setBorder(SoftConstant.shadowborder);
		sendMsgf.setBorder(SoftConstant.shadowborder);

		//添加
		FrameUtil.baseSetFrame(this,
				createrP,joinerP,chatPanel,vsLabel,beginBtn,prepareBtn,sendMsgf,sendMsgBtn);
		
		ChessGameAction chessAction=new ChessGameAction(ChessGameAction.GAME_BEGIN);
		chessAction.setParentframe(this);
		beginBtn.addActionListener(chessAction);
		
		RoomAction readyAction=new RoomAction
				.Builder(RoomAction.SEND_READY).setOutterFrame(this).build();
		prepareBtn.addActionListener(readyAction);
		
		RoomAction sendChatAction=new RoomAction
				.Builder(RoomAction.SEND_CHAT).setOutterFrame(this).build();
		sendMsgBtn.addActionListener(sendChatAction);
		//离开房间执行的事件
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				RoomAction leaveAction=new RoomAction.Builder("")
						.setOutterFrame(GameRoomClient.this).build();
				leaveAction.leaveRoom();
				super.windowClosing(e);
			}
		});
		//开启接收大厅组播信号的线程
		roomInforun=new ReceiveDataFromRoom();
		roomInfoThread=new Thread(roomInforun);
		roomInfoThread.start();
	}
	//单例模式
	public static GameRoomClient getInstance(RoomBean room) throws IOException{
		GameRoomClient.room=room;
		//需要获取服务器分配的ID
		roomId=room.getRoomid();
		title=room.getTitle();
		creater=room.getCreater();
		joiner=room.getJoiner();
		if(grc==null){
			grc=new GameRoomClient();
		}
		grc.setTitle(title+"(房间号:"+roomId+")"+"-----by"+creater.getName());
//		if(creater!=null){
//			createrP.hasLogin(creater);
//		}

		//清除先前的消息
		chatPanel.clearJtaLog();
		//修改状态
		roomInforun.setRoomc(grc);
		
		isReady=false;
		
		beginBtn.setEnabled(isReady);
		
		grc.setVisible(true);
		roomInforun.beginReceive();
		return grc;
	}
	//当用户以创建者身份执行的回调方法
	public void CreateRoomState(Player creater){
		ishost=true;
		
		beginBtn.setVisible(ishost);
		prepareBtn.setVisible(false);
		
		opponentP=joinerP;
		
		createrP.hasLogin(creater);
		if(joiner==null){
			joinerP.setVisible(false);
		}
		else{
			joinerP.hasJoin(joiner);
			room.setJoiner(joiner);
		}
		room.setCreater(creater);
	}
	//当用户以玩家身份加入房间后执行的回调方法
	public void JoinRoomState(Player joiner){
		ishost=false;
		
		GameRoomClient.joiner=joiner;
		//更新状态
		if(room.getJoiner()==null){
			room.setNum(room.getNum()+1);
		}	
		room.setCreater(creater);
		room.setJoiner(GameRoomClient.joiner);
		prepareBtn.setVisible(true);
		beginBtn.setVisible(ishost);
		joinerP.hasLogin(GameRoomClient.joiner);
		
		opponentP=createrP;
		createrP.hasJoin(creater);
	}
	//离开房间,和服务器交互
	public void leaveRoomState() throws IOException{
		room.setNum(room.getNum()-1);
		//判断用户的身份,如果是房主离开了
		//挑战者在，挑战者成为房主
		//没有挑战者，直接关闭房间
		if(joiner==null){
			room.setJoiner(null);
			return;
		}
		else if(myself.getId()==creater.getId()){
			creater=joiner;
			room.setCreater(joiner);
			
			joiner=null;
			room.setJoiner(null);
		}
		//判断用户的身份,如果是挑战者离开了
		else if(myself.getId()==joiner.getId()){
			joiner=null;
			room.setJoiner(null);
		}
		//如果是观众离开了
		else{
			
		}
	}
	/**
	 * 接收端
	 */
	//当收到有用户加入了游戏执行的回调方法
	public void receivedPlayerJoin(Player joiner){
		//如何使可变性最小化?
		room.setNum(2);
		//如果是房主
		if(GameRoomClient.creater.getId()==myself.getId()){
			joinerP.hasJoin(joiner);
		}
		
		GameRoomClient.joiner=joiner;
		room.setJoiner(joiner);	
		
		chatPanel.hasMsgFromRoom(joiner.getName()+"已加入房间..."
				+"房间内人数:"+room.getNum());
	}
	//当有玩家离开房间时,收到有用户离开房间时执行的回调方法
	public void receivedPlayerLeave(Player player){
		//如何使可变性最小化?
		room.setNum(1);
		
		if(joiner==null)return;
		//判断用户的身份,如果是房主离开了,挑战者成为房主
		else if(player.getId()==creater.getId()){
			ishost=true;
			
			room.setCreater(joiner);
			room.setJoiner(null);
			
			creater=joiner;
			joiner=null;
			
			joinerP.setVisible(false);
			prepareBtn.setVisible(false);
			
			beginBtn.setVisible(ishost);
			
			isReady=false;
			beginBtn.setEnabled(isReady);
			
			createrP.hasLogin(creater);
			JOptionPane.showMessageDialog(null, "你已成为房主","提示",JOptionPane.INFORMATION_MESSAGE);
		}
		//判断用户的身份,如果是挑战者离开了
		else if(player.getId()
				==joiner.getId()){
			room.setJoiner(null);
			
			joiner=null;
			
			joinerP.setVisible(false);
		}
		//如果是观众离开了
		else{
			
		}
		chatPanel.hasMsgFromRoom(player.getName()+"已离开房间..."
		+"房间内人数:"+room.getNum());
	}
	//返回大厅
	public void backToHall() throws IOException{
//		roomInforun.setReceive(false);
//		roomInfoThread.resume();
		grc.dispose();
		ghc=GameHallClient.getInstance();
		ghc.BackToGameHall();
		ghc.getRoomtablep().hasLogin(myself);
	}
	//收到准备信号执行的回调方法
	public synchronized void ready(){
		isReady=true;
		beginBtn.setEnabled(isReady);
		chatPanel.hasMsgFromRoom("挑战者已准备就绪，游戏即将开始...");
	}
	//收到取消准备信号执行的回调方法
	public synchronized void disready(){
		isReady=false;
		beginBtn.setEnabled(isReady);
		chatPanel.hasMsgFromRoom("挑战者已取消准备...");
	}
	
	public void showChatInfo(String info){
		chatPanel.hasMsgFromRoom(info);
	}
	public String getChatInfo(){
		String chat_info=sendMsgf.getText().trim();
		//清空输入栏
		sendMsgf.setText("");
		if(chat_info==null||"".equals(chat_info)){
			return null;
		}
		chat_info=myself.getName()+"说:"+chat_info;
		chat_info=BroadcastInRoomMsgUtil
				.useMyRoomMsg(roomId,
						BroadcastInRoomMsgUtil.CHAT_HEAD, chat_info);
		return chat_info;
	}
	//游戏开始触发的回调事件
	public void beginChessGame(RoomBean _room){
		GameRoomClient.room=_room;
		gamec=wuziqiFinalClient.getInstance();
		gamec.PlayerJoinGame(_room, myself);
		grc.setVisible(false);
		//进入游戏线程后暂停房间线程
		roomInforun.pause();
	}
	public synchronized RoomBean getRoom() {
		return room;
	}
	public synchronized long getRoomId() {
		return roomId;
	}
	public synchronized Player getMyself() {
		return myself;
	}
	public void setMyself(Player myself) {
		GameRoomClient.myself = myself;
	}
	public static PlayerInfoPanel getOpponentP() {
		return opponentP;
	}
	public static void setJoiner(Player joiner) {
		GameRoomClient.joiner = joiner;
	}
	
	
}
