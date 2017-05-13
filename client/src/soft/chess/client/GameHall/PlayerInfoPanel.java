package soft.chess.client.GameHall;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import soft.chess.action.PlayerAction;
import soft.chess.client.LoginClient;
import soft.chess.domain.Player;
import soft.chess.util.FileUtil;
import soft.chess.util.HallServerMsgUtil;
import soft.chess.util.ImageIconUtil;
import soft.chess.util.SoftConstant;

public class PlayerInfoPanel extends JPanel {
	private JLabel tiplabel1,badgelabel1,iconlabel;
	private String name;
	private String playerIncon;	
	private String filename;
	
	private PlayerAction loadImgAction;
	public PlayerInfoPanel(){
		setLayout(null);
		setBorder(SoftConstant.shadowborder);
		iconlabel = new JLabel();
		tiplabel1 = new JLabel();
		badgelabel1=new JLabel();
		
		tiplabel1.setHorizontalAlignment(JLabel.CENTER);
		iconlabel.setBounds(5, 5, 133,135);

		tiplabel1.setBounds(10, 140, 90, 30);
		tiplabel1.setFont(SoftConstant.userNamefont);
				
		badgelabel1.setBounds(100, 140, 46, 46);
		
		filename="assets/image/playerIcon/Portrait.png";
		
		add(iconlabel);
		add(tiplabel1);
		add(badgelabel1);
		setBorder(SoftConstant.shadowborder); //设置边框  
	}
	
	//回调方法,用于主面板上注册后登陆
	public void hasLogin(Player player){
		playerIncon=player.getIcon();
		name=player.getName();
		badgelabel1.setIcon(ImageIconUtil.setImageIcon("assets/image/badge/level"+player.getLevel()+".png",badgelabel1));
		tiplabel1.setText(name);
		PlayerInfoPanel.this.setVisible(true);
		if(playerIncon!=null){
			//先查找本地缓存是否有用户头像
			filename="playerIcon/"+playerIncon;
			File iconfile=new File(filename);
			//如果在缓存区有图片了，直接引用
			//如果缓存区没有向服务器发出加载图片的请求
			if(!iconfile.exists()){
				loadImgAction=new PlayerAction.Builder("")
						.setOutterPanel(this).build();
				loadImgAction.getImg(player,HallServerMsgUtil.GET_IMG);
			}
		}
		iconlabel.setIcon(ImageIconUtil.setImageIcon(filename, iconlabel));
	}
	//回调方法，用于房间中显示头像
	public void hasJoin(Player player){
		playerIncon=player.getIcon();
		name=player.getName();
		badgelabel1.setIcon(ImageIconUtil.setImageIcon("assets/image/badge/level"+player.getLevel()+".png",badgelabel1));
		tiplabel1.setText(name);
		PlayerInfoPanel.this.setVisible(true);
		if(playerIncon!=null){
			//先查找本地缓存是否有用户头像
			filename="roomImg/"+playerIncon;
			File iconfile=new File(filename);
			//如果在缓存区有图片了，直接引用
			//如果缓存区没有向服务器发出加载图片的请求
			if(!iconfile.exists()){
				loadImgAction=new PlayerAction.Builder("")
						.setOutterPanel(this).build();
				loadImgAction.getImg(player,HallServerMsgUtil.GET_ROOM_IMG);
			}
		}
		iconlabel.setIcon(ImageIconUtil.setImageIcon(filename, iconlabel));
	}
	//加载图片的回调方法
	public void loadImg(String path){
		iconlabel.setIcon(ImageIconUtil.setImageIcon(path, iconlabel));
	}
}
