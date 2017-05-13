package soft.chess.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import soft.chess.action.PlayerAction;
import soft.chess.action.RoomAction;
import soft.chess.client.GameHall.GameHallMainPanel;
import soft.chess.domain.Player;
import soft.chess.domain.RoomBean;
import soft.chess.util.FrameUtil;
import soft.chess.util.SoftConstant;
/**
 * 
 * ClassName: GameRoomClient 
 * @Description: 游戏房间的界面
 * @author wangsuqi
 * @date 2016年10月5日
 * @see
 */
public class GameRoomSettingClient extends JFrame{
	//单例模式
	private static GameRoomSettingClient grsc;
	//引入可变对象
	private static GameHallMainPanel parentp;
	
	private static Player myself;
	
	private GameRoomClient grc;
	private RoomBean room;
	private JLabel tip1;
	private JTextField titleTxt;
	private JButton okbtn;
	private static Player creater;
	private GameRoomSettingClient(){
		tip1=new JLabel("房间名:");
		titleTxt=new JTextField();
		okbtn=new JButton("确定");
		//不仅需要发送房间状态，还需要发送用户的状态是否使用中介者模式？
		okbtn.addActionListener(new addRoomAction());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				parentp.setGameHallEnabled(true);
				super.windowClosing(e);
			};
		});	
		setLocation(SoftConstant.screenWidth/3,SoftConstant.screenHeight/3);
		setLocationByPlatform(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("房间设置");
		setLayout(null);
		setSize(250, 200);
		
		tip1.setFont(SoftConstant.userNamefont);
		tip1.setBounds(5, 10, 70, 30);
		
		titleTxt.setText("来吧！少年...战个痛快！");
		titleTxt.setFont(SoftConstant.userNamefont);
		titleTxt.setBounds(85, 10, 130, 30);
		
		okbtn.setFont(SoftConstant.userNamefont);
		okbtn.setBounds(85, 120, 80, 30);
	
		FrameUtil.baseSetFrame(this, tip1,titleTxt,okbtn);
	}
	//单例模式
	public static GameRoomSettingClient getInstance(GameHallMainPanel ghmp){
		GameRoomSettingClient.parentp=ghmp;
		GameRoomSettingClient.creater=ghmp.getPlayer();
		if(grsc==null){
			grsc=new GameRoomSettingClient();
		}
		grsc.setVisible(true);
		return grsc;
	}
	//提供给Action，Action负责发送UDP报文
	public RoomBean getRoom() {
		room=new RoomBean();
		room.setTitle(titleTxt.getText().trim());
		room.setCreater(creater);
		room.setCreatetime(System.currentTimeMillis());
		return room;
	}	
	//回调方法，房间创建成功后进入房间，关闭游戏大厅主窗口，退出大厅组播
	public void CreatedRoom(RoomBean room){
		
		myself=parentp.getPlayer();
				
		try {
			grc=GameRoomClient.getInstance(room);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		grc.CreateRoomState(myself);
		grc.setMyself(myself);
		//执行回调方法关闭大厅
		parentp.CreatedRoom();
		//类层次混乱，过于复杂
		parentp.setGrc(grc);
		
		GameRoomSettingClient.this.dispose();
	}
	//定义一个中介者，持有RoomAction和PlayerAction
	private class addRoomAction implements ActionListener{
		private RoomAction roomAction;
		private PlayerAction playerAction;
		public addRoomAction() {
			roomAction=new RoomAction.Builder(RoomAction.SAVE_ROOM)
					.setOutterFrame(GameRoomSettingClient.this).build();
			playerAction=new PlayerAction.Builder("").build();
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			//触发保存房间的操作
			roomAction.actionPerformed(e);
			//触发用户状态更新的操作
			creater.setPlayerState((byte)2);
			playerAction.sendPlayerStateToHallBroadcast(creater);
			playerAction.sendPlayerStateToHallServer(creater);	
		}	
	}
}
