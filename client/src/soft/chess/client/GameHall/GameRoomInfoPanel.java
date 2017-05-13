package soft.chess.client.GameHall;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jhlabs.math.BlackFunction;

import soft.chess.action.ChessGameAction;
import soft.chess.action.PlayerAction;
import soft.chess.action.RoomAction;
import soft.chess.client.GameRoomClient;
import soft.chess.client.wuziqiFinalClient;
import soft.chess.domain.ChessBean;
import soft.chess.domain.Player;
import soft.chess.domain.RoomBean;
import soft.chess.util.BackgroundImagePanel;
import soft.chess.util.GameChessServerMsgUtil;
import soft.chess.util.ImageIconUtil;
import soft.chess.util.JsonUtil;
import soft.chess.util.RoundRectButton;
import soft.chess.util.SoftConstant;

public class GameRoomInfoPanel extends JPanel{
	//
	private wuziqiFinalClient wuziqic;
	
	private RoomBean room;
	
	private Player myself;
	//显示房主名称和房间内人数
	private JLabel createrl,numl;
	private titelPanel titlep;
	//显示房间状态
	private JLabel roomState1,roomStateIcon1,roomState2,roomStateIcon2;	
	//加入游戏和参观游戏的按钮
	private JButton joinBtn,viewBtn;
	//主界面
	private GameHallMainPanel ghmp=null;
	//关闭房间的图标
	private JLabel roomClosedl;
	//判断是否是登录的用户
	private boolean isLogin=false;
	//发送用户状态
	private PlayerAction playerAction;
	public GameRoomInfoPanel() {
		// TODO Auto-generated constructor stub
		playerAction=new PlayerAction.Builder("").build();
		
		setLayout(null);
		room=new RoomBean();
		titlep=new titelPanel();
		createrl=new JLabel();
		numl=new JLabel();
		roomClosedl=new JLabel();
		joinBtn=new RoundRectButton("怼他", 65,36);
		viewBtn=new RoundRectButton("围观", 65,36);
		roomState1=new JLabel("等待中");
		roomStateIcon1=new JLabel();
		roomState2=new JLabel("游戏中");
		roomStateIcon2=new JLabel();
		roomState1.setVisible(false);
		roomState2.setVisible(false);
		roomStateIcon1.setVisible(false);
		roomStateIcon2.setVisible(false);
		joinBtn.setVisible(false);
		viewBtn.setVisible(false);
		//布局
		titlep.setBounds(0, 0, 170, 60);
		
		roomState1.setBounds(10, 62, 50, 16);
		roomState2.setBounds(10, 62, 50, 16);
		
		roomStateIcon1.setBounds(60, 62, 16, 16);
		roomStateIcon1.setIcon(ImageIconUtil.setImageIcon("assets/image/label/room_wait.png", roomStateIcon1));
		roomStateIcon2.setBounds(60, 62, 16, 16);
		roomStateIcon2.setIcon(ImageIconUtil.setImageIcon("assets/image/label/room_gaming.png", roomStateIcon2));
		
		roomClosedl.setBounds(22, 0, 128, 128);
		roomClosedl.setIcon(ImageIconUtil.setImageIcon("assets/image/label/room_closed.png", roomClosedl));
		
		numl.setBounds(10, 90, 90, 15);
		createrl.setBounds(10, 110, 150, 15);
		joinBtn.setBounds(100, 80, 65,36);
		viewBtn.setBounds(100, 80, 65,36);
		//绑定事件
		RoomAction joinAction=new RoomAction
				.Builder(RoomAction.JOIN_ROOM)
				.setOutterPanel(this).build();
		joinBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//ghmp是一个可变的对象
				myself=ghmp.getPlayer();
				if(myself==null){
					JOptionPane.showMessageDialog(null,"您当前身份是游客，需要登录才能加入战斗","请登录",JOptionPane.ERROR_MESSAGE);
					return;
				}
				//
				joinAction.actionPerformed(e);
				//
				myself.setPlayerState((byte)2);
				playerAction.sendPlayerStateToHallBroadcast(myself);
				playerAction.sendPlayerStateToHallServer(myself);
			}
		});
		
		ChessGameAction viewGameAction=new ChessGameAction(ChessGameAction.JOIN_AUDIENCE);
		viewGameAction.setParentPanel(this);
		viewBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				myself=ghmp.getPlayer();
				
				ghmp.setCurrenRoomp(GameRoomInfoPanel.this);
				//进入游戏
				viewGameAction.actionPerformed(e);
				//发送登录用户状态
				if(myself==null)return;
				myself.setPlayerState((byte)2);
				playerAction.sendPlayerStateToHallBroadcast(myself);
				playerAction.sendPlayerStateToHallServer(myself);
			}
		});
		
		add(titlep);
		add(createrl);
		add(numl);
		add(joinBtn);
		add(viewBtn);
		add(roomState1);
		add(roomStateIcon1);
		add(roomState2);
		add(roomStateIcon2);
		add(roomClosedl);
		setBorder(SoftConstant.shadowborder);
	}
	public void changeContent(RoomBean _room){	
		this.room=_room;
		createrl.setText("房主:"+room.getCreater().getName());		
		numl.setText("人数："+room.getNum());
		titlep.change(room);
		if(room.getRoomState()==0){
			roomState2.setVisible(false);
			roomState1.setVisible(true);
			roomStateIcon2.setVisible(false);
			roomStateIcon1.setVisible(true);
			viewBtn.setVisible(false);
			joinBtn.setVisible(true);
		}
		else if(room.getRoomState()==1){
			roomState1.setVisible(false);
			roomState2.setVisible(true);
			roomStateIcon1.setVisible(false);
			roomStateIcon2.setVisible(true);
			joinBtn.setVisible(false);
			viewBtn.setVisible(true);
		}
		roomClosedl.setVisible(false);
		numl.setVisible(true);
		createrl.setVisible(true);
		titlep.setVisible(true);
		this.setVisible(true);
	}
	public void hasExpired(){
		roomState1.setVisible(false);
		roomState2.setVisible(false);
		roomStateIcon1.setVisible(false);
		roomStateIcon2.setVisible(false);
		numl.setVisible(false);
		createrl.setVisible(false);
		joinBtn.setVisible(false);
		viewBtn.setVisible(false);
		titlep.setVisible(false);
		roomClosedl.setVisible(true);
	}
	
	public void setGhmp(GameHallMainPanel ghmp) {
		this.ghmp = ghmp;
	}
	
	public GameHallMainPanel getGhmp() {
		return ghmp;
	}
	public RoomBean getRoom() {
		return room;
	}	
	public String getSendInfo(){
		//ghmp是一个可变对象
		this.myself=ghmp.getPlayer();
		//如果是游客
		if(myself==null){
			isLogin=false;
			//这样赋值是否合理
			myself=new Player();
			myself.setName("游客");
			myself.setId(-1);
		}
		else{
			isLogin=true;
		}
		String send_info=JsonUtil.ObjectToStr(myself);
		send_info=GameChessServerMsgUtil
				.useGameServerMsg(room.getRoomid(),
						GameChessServerMsgUtil.AUDI_JOIN, send_info);
		return send_info;
	}
	public void ViewGame(List<ChessBean> chesses) throws IOException{
		wuziqic=wuziqiFinalClient.getInstance();
		wuziqic.AudiJoinGame(room, myself, chesses, isLogin);
		//生成消息
		ghmp.getGameHall().LeaveGameHall();
	}

	private class titelPanel extends BackgroundImagePanel{
		private JLabel idl;
		private JLabel titlel;
		private JLabel iconl;
		public titelPanel() {
			super(SoftConstant.room_title_panel_bgimg);
			// TODO Auto-generated constructor stub
			setLayout(null);
			setBackground(Color.gray);
			idl=new JLabel();
			titlel=new JLabel();
			iconl=new JLabel();
			iconl.setBounds(130, 10, 32, 32);
			iconl.setIcon(ImageIconUtil.setImageIcon("assets/image/label/chess_icon.png", iconl));
			idl.setBounds(15, 10, 110, 32);
			idl.setFont(SoftConstant.userNamefont);
			idl.setForeground(Color.white);
			titlel.setFont(SoftConstant.roomtitlefont);
			titlel.setForeground(Color.BLACK);
			titlel.setBounds(0, 30, 150, 28);
			add(iconl);
			add(idl);
			add(titlel);
		}
		public void change(RoomBean room){
			idl.setText("房间号:"+room.getRoomid()+"");
			titlel.setText(room.getTitle());
		}
	}
}
