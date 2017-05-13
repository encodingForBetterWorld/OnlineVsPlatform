package soft.chess.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;

import soft.chess.action.ChessGameAction;
import soft.chess.action.RoomAction;
import soft.chess.domain.ChessBean;
import soft.chess.domain.GameRecord;
import soft.chess.domain.Player;
import soft.chess.domain.RoomBean;
import soft.chess.game.*;
import soft.chess.receiveData.ReceiveDataFromGame;
import soft.chess.util.AudioPlayWave;
import soft.chess.util.FrameUtil;

public class wuziqiFinalClient extends JFrame{
	//返回大厅
	private GameHallClient ghc;
	
	private static wuziqiFinalClient wzqfc;
	
	private Player myself;
	private Player myOpponent;
	private RoomBean room;
	//游戏记录
	private GameRecord record;
	private long beginTime;
	private long endTime;
	//棋子信息
	private ChessBean chess;
	private static boolean myTurn=false;//控制是否可以走棋
	private byte myToken;//在自己界面的小面板上下的棋子
	
	private static Cell[][] cell=new Cell[15][15];//15*15个小面板，用来画棋子
	private JLabel jlblTitle=new JLabel();//告诉用户是哪位游戏者（player1 or player2）
	private JLabel jlblStatus=new JLabel();//告诉用户当前状态（轮谁走，输赢等）

	private static wuziqiNumberClock a;
	private static wuziqiNumberClock2 a2;
	
	private static boolean continueToPlay=true;//判断是否继续游戏
	private boolean waiting=true;//判断是否等待
	
	private boolean isPlayer=true;//判断是否是玩家
	
	private boolean isLogin=true;//判断观众是否已登录
	
	private int myRole;
	
	private JPanel p1=new JPanel();//五子棋大面板
	private JPanel p2=new JPanel();//聊天大面板
	private static JTextArea jta=new JTextArea();//聊天文字记录窗口
	private JTextField jtf=new JTextField();//聊天文字输入口
	private JButton jbt=new JButton("发送");
	private JButton jbt1=new JButton("字体");
	private JPanel p3=new JPanel();//加jbt,jbt1,jtf的小面板
	//通讯模块
	//信号接收端
	private static ReceiveDataFromGame receiveChessDataRun=null;
	private static Thread receiveChessDataThread=null;
	//信号发出端,发送聊天信息
	private static ChessGameAction sendChatAction;
	//信号发送端,发送棋子信息
	private static ChessGameAction sendChessAction;
	//有人退出棋局，发送消息
	private static ChessGameAction sendESCAction;
	private wuziqiFinalClient()
	{	
		
		chess=new ChessBean();
		///////////////////////////聊天面板
	    jta.setEditable(false);
		p2.setLayout(new BorderLayout());
		p2.add(new JScrollPane(jta,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),BorderLayout.CENTER);
		p3.setLayout(new BorderLayout());
		p3.add(jtf,BorderLayout.CENTER);
		p3.add(jbt,BorderLayout.EAST);
		p3.add(jbt1,BorderLayout.WEST);
		p2.add(p3,BorderLayout.SOUTH);
		///////////////////////////////////下棋面板
		p1.setLayout(new BorderLayout());
		JPanel p=new JPanel();
		p.setLayout(new GridLayout(15,15,0,0));
		for(byte i=0;i<15;i++)
			for(byte j=0;j<15;j++)
			{
				p.add(cell[i][j]=new Cell(i,j));
			}
		p.setBorder(new LineBorder(Color.red,1));
		jlblTitle.setHorizontalAlignment(JLabel.CENTER);
		jlblTitle.setFont(new Font("SansSerif",Font.BOLD,16));

		p1.add(jlblTitle,BorderLayout.NORTH);
		p1.add(p,BorderLayout.CENTER);
		p1.add(jlblStatus,BorderLayout.SOUTH);
	    ////////////////////////////////////////
		setSize(913,650);
		
		setLayout(new GridBagLayout());
	
		a=new wuziqiNumberClock();
		a2=new wuziqiNumberClock2();
	    /////////////////////////////////////////新使用的布局管理器
		GridBagConstraints gb=new GridBagConstraints();
	    gb.fill=GridBagConstraints.BOTH;
	    Container con=getContentPane();
	    addComp(p1,con,gb,0,0,8,2,2,1);
	    addComp(a,con,gb,0,2,1,1,1,1);
	    addComp(a2,con,gb,1,2,1,1,1,1);
	    addComp(p2,con,gb,2,2,4,1,1,6);  
         ///////////////////////////////////////
		jtf.setBackground(Color.white);
		
		sendChatAction=new ChessGameAction(ChessGameAction.SEND_CHAT);

		jtf.addActionListener(sendChatAction);
	    jbt.addActionListener(sendChatAction);
	    
	    sendChessAction=new ChessGameAction("");
	    
		sendESCAction=new ChessGameAction("");
		
	    jbt1.addActionListener(new ActionListener(){
		   public void actionPerformed(ActionEvent e)
		   {
	            new setFontDialog(jtf, jta);
		   }
	    });
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//暂停线程
				receiveChessDataRun.pause();
				//比赛途中退出，回到大厅中
				wuziqiFinalClient.this.dispose();
//				receiveChessDataThread.resume();
				//进入大厅
				try {
					ghc = GameHallClient.getInstance();
					ghc.BackToGameHall();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(isLogin){
					ghc.getRoomtablep().hasLogin(myself);
				}
				//如果游戏还在继续，玩家身份不是观众，判负
				if(chess.getChessState()==wuziqiConstants
						.GameState.CONTINUE.getState()
						&&!(myRole==wuziqiConstants
						.PlayerRole.Audience.getRole())){
					record.setLoser_id(myself.getId());
					record.setWinner_id(myOpponent.getId());;
					endTime=System.currentTimeMillis();
					record.setEndtime(endTime);
					record.setGametime(endTime-record.getBegintime());
					
					room.setCreater(myOpponent);
					room.setJoiner(null);
					room.setNum(room.getNum()-1);
					room.setRecord(record);
					//发出房间信息
					sendESCAction.sendPlayerEsc(room);
				}
				//如果是观众退出了，更新房间状态
				if(myRole==wuziqiConstants
						.PlayerRole.Audience.getRole()){
					sendESCAction.sendAudiEsc();
				}
				super.windowClosing(e);   
			}  
		});
		receiveChessDataRun=new ReceiveDataFromGame();
		receiveChessDataThread=new Thread(receiveChessDataRun);
		receiveChessDataThread.start();
		FrameUtil.baseSetFrame(this, null);
	}
	//获取单例，负责初始化工作
	public static wuziqiFinalClient getInstance(){
		if(wzqfc==null){
			wzqfc=new wuziqiFinalClient();
		}
		sendESCAction.setParentframe(wzqfc);
		sendChatAction.setParentframe(wzqfc);
		sendChessAction.setParentframe(wzqfc);
		//初始化棋盘
		for(byte i=0;i<15;i++)
			for(byte j=0;j<15;j++)
			{
				cell[i][j].setToken((byte)-1);
			}
		//初始化聊天面板
		jta.setText("");
		//初始化钟表
		a.clearScreen();
		a2.clearScreen();
		
		continueToPlay=true;
		myTurn=false;
		
		wzqfc.setVisible(true);
		return wzqfc;
	}
	
	//用户进入游戏执行的回调方法
	public void PlayerJoinGame(RoomBean room,Player myself){
		record=new GameRecord();
		beginTime=System.currentTimeMillis();
		record.setBegintime(beginTime);
		
		this.room=room;
		this.myself=myself;
		//开始监听
		receiveChessDataRun.setWzqfc(wzqfc);
		receiveChessDataRun.beginReceive();
		
		if(myself.getId()!=room.getCreater().getId()){
			myOpponent=room.getCreater();
		}
		else{
			myOpponent=room.getJoiner();
		}
		//获取身份
		if(myself.getId()==room.getBlackPlayer().getId()){
			myRole=wuziqiConstants.PlayerRole.PLAYER1.getRole();
			myToken=1;
			jlblTitle.setText("我是玩家一（黑方）");
			myTurn=true;
			globalVariable.x=true;
			globalVariable.y=false;
		}
		else if(myself.getId()==room.getWhitePlayer().getId()){
			myRole=wuziqiConstants.PlayerRole.PLAYER2.getRole();
			myToken=0;
			jlblTitle.setText("我是玩家二（白方）");
			jlblStatus.setText("等待对手（黑方）移动");
			globalVariable.x=true;
			globalVariable.y=false;
		}
		
	}
	public void AudiJoinGame(RoomBean room,Player myself,List<ChessBean> chesses,boolean isLogin){
		this.isLogin=isLogin;
		this.room=room;
		this.myself=myself;
		isPlayer=false;
		myRole=wuziqiConstants.PlayerRole.Audience.getRole();
		jlblTitle.setText("我是观众");
		for(ChessBean chess:chesses){
			cell[chess.getX()][chess.getY()].setToken(chess.getColor());
		}
		//开始监听
		receiveChessDataRun.setWzqfc(wzqfc);
		receiveChessDataRun.beginReceive();
	}
	//////////////////////////布局管理器
	public void addComp(Component c,Container con,GridBagConstraints gb,int row,int column,int numberOfRows,int numberOfColumns,double weightx,double weighty)
	{
		gb.gridx=column;
		gb.gridy=row;
		gb.gridwidth=numberOfColumns;
		gb.gridheight=numberOfRows;
		gb.weightx=weightx;
		gb.weighty=weighty;
		con.add(c,gb);	
	}
		
	public RoomBean getRoom() {
			return room;
		}
	public void playMsg() throws MalformedURLException//聊天提示音
	{
		AudioPlayWave audioPlayWave = new AudioPlayWave("assets/audio/msg.wav");
        audioPlayWave.start(); // 启动线程
	}
	public void playChessDown()//聊天提示音
	{
		AudioPlayWave audioPlayWave = new AudioPlayWave("assets/audio/putdownchess.wav");
        audioPlayWave.start(); // 启动线程
	}
		
	private void waitForPlayerAction() throws InterruptedException
	{
		while(waiting)
		{
			Thread.sleep(100);
		}
		waiting=true;
	}
	public class Cell extends JPanel {//每个棋子所处的小面板
		private byte row;
		private byte column;
		private byte token=-1;
		public Cell(byte row,byte column)
		{
			this.row=row;
			this.column=column;
			setBackground(Color.orange);
			addMouseListener(new ClickListener());
		}
		public byte getToken()
		{
			return token;
		}
		public void setToken(byte c)
		{
			token=c;
			repaint();
		}
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			/////////////////////////在小面盘中画十字，使棋子走在十字焦点上
			g.drawLine(0,getHeight()/2,getWidth(),getHeight()/2);
			g.drawLine(getWidth()/2,0,getWidth()/2,getHeight());
			/////////////////////////////////去除大棋盘周边的多余十字部分
			if(row==0&&column==0)
			{
				g.setColor(Color.orange);
				g.drawLine(0,getHeight()/2,getWidth()/2,getHeight()/2);
				g.drawLine(getWidth()/2,0,getWidth()/2,getHeight()/2);
			}
			
			if(row==0&&column==14)
			{
				g.setColor(Color.orange);
				g.drawLine(getWidth()/2,getHeight()/2,getWidth(),getHeight()/2);
				g.drawLine(getWidth()/2,0,getWidth()/2,getHeight()/2);
			}
			if(row==14&&column==0)
			{
				g.setColor(Color.orange);
				g.drawLine(0,getHeight()/2,getWidth()/2,getHeight()/2);
				g.drawLine(getWidth()/2,getHeight()/2,getWidth()/2,getHeight());
			}
			if(row==14&&column==14)
			{
				g.setColor(Color.orange);
				g.drawLine(getWidth()/2,getHeight()/2,getWidth(),getHeight()/2);
				g.drawLine(getWidth()/2,getHeight()/2,getWidth()/2,getHeight());
			}
			if(row==0&&(column>0&&column<14))
			{
				g.setColor(Color.orange);
				g.drawLine(getWidth()/2,0,getWidth()/2,getHeight()/2);
			}
			
			if(column==0&&(row>0&&row<14))
			{
				g.setColor(Color.orange);
				g.drawLine(0,getHeight()/2,getWidth()/2,getHeight()/2);
			}
			if(row==14&&(column>0&&column<14))
			{
				g.setColor(Color.orange);
				g.drawLine(getWidth()/2,getHeight()/2,getWidth()/2,getHeight());
			}
			if(column==14&&(row>0&&row<14))
			{
				g.setColor(Color.orange);
				g.drawLine(getWidth()/2,getHeight()/2,getWidth(),getHeight()/2);
			}
			/////////////////////
			///////////////////在棋盘上绘制5个提示黑点
			if(row==3&&column==3)
			{
				g.fillOval((int)(getWidth()/2-3.12),(int)(getHeight()/2-3.12),getWidth()/5,getHeight()/5);
			}
			if(row==3&&column==11)
			{
				g.fillOval((int)(getWidth()/2-3.12),(int)(getHeight()/2-3.12),getWidth()/5,getHeight()/5);
			}
			if(row==11&&column==3)
			{
				g.fillOval((int)(getWidth()/2-3.12),(int)(getHeight()/2-3.12),getWidth()/5,getHeight()/5);
			}
			if(row==11&&column==11)
			{
				g.fillOval((int)(getWidth()/2-3.12),(int)(getHeight()/2-3.12),getWidth()/5,getHeight()/5);
			}
			if(row==7&&column==7)
			{
				g.fillOval((int)(getWidth()/2-3.12),(int)(getHeight()/2-3.12),getWidth()/5,getHeight()/5);
			}
			//////////////////////////////
			/////////////////////画棋子
			if(token==0)
			{
				g.setColor(Color.white);
				g.fillOval(4,4,getWidth()-10,getHeight()-10);
			}
			else if(token==1)
			{
				g.setColor(Color.black);
				g.fillOval(4,4,getWidth()-10,getHeight()-10);
			}
			//////////////////////
		}
		public class ClickListener extends MouseAdapter {
			public void mouseClicked(MouseEvent e)
			{
				if((token==-1)&&myTurn&&continueToPlay&&isPlayer)
				{
					////////////////////////
					myTurn=false;
					chess.setX(row);
					chess.setY(column);
					chess.setColor(myToken);
					sendChessAction.sendChessMove(chess);
					jlblStatus.setText("等待对方玩家移动...");
					waiting=false;
				}
			}
		}
	}
	public String getChatInfo(){
		String send=jtf.getText().trim();
		if(send.equals(""))
		 {
			 JOptionPane.showMessageDialog(null,"发送信息不能为空，请重新输入！");
			 return null;
		 }
		else
		{
		    jtf.setText(null);
			Calendar now = new GregorianCalendar();
			String st="";
			int nowh=now.get(Calendar.HOUR_OF_DAY);
			int nowm=now.get(Calendar.MINUTE);
			int nows=now.get(Calendar.SECOND);
			if(nowh < 10)
				  st += "0"+nowh;
			 else 
				  st +=""+nowh;
			 if(nowm<10)
				  st += ":0"+nowm;
			 else 
				  st += ":"+nowm;
			 if(nows<10)
				  st += ":0"+nows;
			 else 
				  st += ":"+nows;
			
		 send="("+st+")"+myself.getName()+":"+send;
		}
		return send;
	}
	public void showChatMsg(String msg){
		jta.append(msg.trim());
		jta.append("\n");
		try {
			playMsg();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void audienceJoin(Player audi){
		jta.append(audi.getName()+"正在观看比赛");
		jta.append("\n");
		try {
			playMsg();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void ReceiveData(ChessBean chess) throws IOException{
		if(chess.getColor()!=myToken){
			myTurn=true;
		}
		//////////////////////////控制计时装置的走停
		if(chess.getColor()==0){
			globalVariable.x=true;
			globalVariable.y=false;
		}
		else{
			globalVariable.x=false;
			globalVariable.y=true;
		}
		this.chess=chess;
		int status=chess.getChessState();
		if(status==wuziqiConstants.GameState.PLAYER1_WON.getState())
		{
			globalVariable.x=false;//控制计时装置的走停，下同
			globalVariable.y=false;
			continueToPlay=false;
			jlblStatus.setText(room.getBlackPlayer().getName()+" 持黑棋获胜！");
			receiveMove();
		}
		else if(status==wuziqiConstants.GameState.PLAYER2_WON.getState())
		{
			globalVariable.x=false;//控制计时装置的走停，下同
			globalVariable.y=false;
			continueToPlay=false;
			jlblStatus.setText(room.getWhitePlayer().getName()+" 持白棋获胜！");
			receiveMove();
		}
		else if(status==wuziqiConstants.GameState.DRAW.getState())
		{
			globalVariable.x=false;//控制计时装置的走停，下同
			globalVariable.y=false;
			continueToPlay=false;
			jlblStatus.setText("游戏结束,和局!");
			receiveMove();
		}
		else
		{
			jlblStatus.setText("游戏进行中...");
			receiveMove();
		}
	}
	private void receiveMove() throws IOException//选手收到他人走的棋子
	{
		byte row=chess.getX();
		byte column=chess.getY();
		cell[row][column].setToken(chess.getColor());
		playChessDown();
	}
	public void gameOver(RoomBean room) throws IOException{
		long winner_id=room.getRecord().getWinner_id();
		JOptionPane.showMessageDialog(null, winner_id+"已经获胜","游戏结束",JOptionPane.INFORMATION_MESSAGE);
		//暂停线程
		receiveChessDataRun.pause();
//		receiveChessDataThread.resume();
		this.dispose();
		//如果是观众，比赛结束后回到游戏大厅
		if(myRole==wuziqiConstants.PlayerRole.Audience.getRole()){
			ghc = GameHallClient.getInstance();
			ghc.BackToGameHall();
			if(isLogin){
				ghc.getRoomtablep().hasLogin(myself);
			}
		}
		//如果是玩家，比赛结束后回到房间里
		else{
			try {	
				GameRoomClient grc=GameRoomClient.getInstance(room);
				grc.setMyself(myself);
				if(room.getJoiner()!=null
						&&myself.getId()==room.getJoiner().getId()){
					grc.JoinRoomState(myself);
				}
				if(room.getCreater()!=null
						&&myself.getId()==room.getCreater().getId()){
					if(room.getJoiner()==null)GameRoomClient.setJoiner(null);
					grc.CreateRoomState(myself);
				}
				
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		
	}
	
	public ChessBean getChess() {
		return chess;
	}
	
}

