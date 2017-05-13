package soft.chess.client.GameHall;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import soft.chess.action.PlayerAction;
import soft.chess.client.GameHallClient;
import soft.chess.domain.Player;
import soft.chess.util.BackgroundImagePanel;
import soft.chess.util.ImageIconUtil;
import soft.chess.util.BroadcastMsgUtil;
import soft.chess.util.SocketUtil;
import soft.chess.util.SoftConstant;

public class GameHallSendMsgPanel extends BackgroundImagePanel{
	private JLabel tipLabel;
	private JTextField text;
	private JButton sendChat,sendAnn;
	private GameHallClient gameHall;
	//停止播放动画的线程
	private volatile boolean isAnimate=true;
	
	//播放动画的线程
	private Thread animate;
	private Icon icon0;
	
	private Player player;
	//登录了才显示发公告按钮
	private volatile static boolean isLogin=false;
	public GameHallSendMsgPanel(JFrame parentframe) throws IOException {				
		super(SoftConstant.chat_panel_bgimg);
		setLayout(null);
		this.gameHall=(GameHallClient) parentframe;
		// TODO Auto-generated constructor stub
		tipLabel=new JLabel();
		text=new JTextField();
		sendChat=new JButton("发聊天");
		sendAnn=new JButton("发公告");
		
		//登录了才显示发公告按钮
		sendAnn.setVisible(isLogin);
		
		sendChat.setBounds(87, 6, 80, 28);
		add(sendChat);
		
		tipLabel.setBounds(183, 0, 48, 48);
		//设置默认图标
		icon0=ImageIconUtil.setImageIcon("assets/image/label/twitter0.png", tipLabel);

		tipLabel.setIcon(icon0);
		
		List<Icon> myTwitters=new ArrayList<>();
		myTwitters.add(ImageIconUtil.setImageIcon("assets/image/label/twitter1.png", tipLabel));
		myTwitters.add(ImageIconUtil.setImageIcon("assets/image/label/twitter2.png", tipLabel));
		myTwitters.add(ImageIconUtil.setImageIcon("assets/image/label/twitter3.png", tipLabel));
		myTwitters.add(ImageIconUtil.setImageIcon("assets/image/label/twitter4.png", tipLabel));
		
		//播放动画的线程
		animate=new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(isAnimate){
					for(int i=0;i<7;i++){
						try {
							Thread.sleep(300);
							if(i>3){
								int j=7-i;
								tipLabel.setIcon(myTwitters.get(j));
							}else{
								tipLabel.setIcon(myTwitters.get(i));
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
		PlayerAction sendChatAction=new PlayerAction
				.Builder(PlayerAction.SEND_CHAT)
				.setOutterPanel(this).build();
		sendChat.addActionListener(sendChatAction);
		
		sendAnn.setBounds(5, 6, 80, 28);
		PlayerAction sendAnnAction=new PlayerAction
				.Builder(PlayerAction.SEND_ANN)
				.setOutterPanel(this).build();
		sendAnn.addActionListener(sendAnnAction);
		add(sendAnn);
				
				
		add(tipLabel);
		
		text.setBounds(5, 45, 225, 28);
		text.setFont(SoftConstant.smfont);
		
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e){
				if(!animate.isAlive()){
					animate.start();
					//启动一个阻塞动画的线程,一秒后后如果用户无输入则停止动画
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
							while(isAnimate){
								String beforeStr=text.getText();
								Thread.sleep(1000);
								String afterStr=text.getText();
								if(beforeStr.equals(afterStr)){
									stopAnimate();
									}
								}
							} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
							}
						}
					}).start();
				}
				else{
					animate.resume();
				}				
			}			
		});
		
		add(text);
	}
	//回调方法
	public void hasLogin(){
		isLogin=true;
		this.player=gameHall.getRoomtablep().getPlayer();
		sendAnn.setVisible(isLogin);
	}
	public void hasLogout(){
		isLogin=false;
		this.player=null;
		sendAnn.setVisible(false);
	}
	private synchronized void stopAnimate(){
		animate.suspend();
		tipLabel.setIcon(icon0);
	}
	public synchronized void exitHall(){
		isAnimate=false;
	}
	public synchronized void backHall(){
		isAnimate=true;
		animate.start();
	}
	public synchronized String getChatMsg(){
		stopAnimate();
		if(text.getText().trim().equals("")){
			return null;
		}
		String sendMsg="";

		if(player==null){
			sendMsg+="游客说:"+text.getText().trim();
		}
		else{
			sendMsg+=player.getName()+"说:"+text.getText().trim();
		}
		text.setText("");
		return sendMsg;
	}
	public synchronized String getAnnMsg(){
		stopAnimate();
		// TODO Auto-generated method stub
		if(text.getText().trim().equals("")){
			return null;
		}
		String sendMsg="";
//		Player player=gameHall.getPlayer();
		
		sendMsg+=player.getName()+"发布大厅公告:"+text.getText().trim();

		text.setText("");
		return sendMsg;
	}
	
}
