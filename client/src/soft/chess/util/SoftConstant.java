package soft.chess.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.border.Border;



public class SoftConstant {
	private SoftConstant(){}
	public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int screenWidth = screenSize.width;
	public static final int screenHeight = screenSize.height;
	public static final Font roomtitlefont=new Font("宋体", Font.PLAIN,11);
	public static final Font toosmfont = new Font("", Font.BOLD,12);
	public static final Font lsmfont = new Font("", Font.BOLD,13);
	public static final Font smfont = new Font("", Font.BOLD,16);
	public static final Font userNamefont = new Font("黑体", Font.BOLD,16);
	public static final Font mmfont = new Font("", Font.BOLD,18);
	public static final Font mfont = new Font("", Font.BOLD,20);
	public static final Font OnlienPlayerTitlefont = new Font("宋体", Font.BOLD,20);
	public static final Font bgfont = new Font("", Font.BOLD,25);
	public static final BasicStroke stroke=new BasicStroke(3.0f);
	public static final BasicStroke btnstroke=new BasicStroke(0.1f);
	public static final Border shadowborder = BorderFactory.createCompoundBorder(ShadowBorder.newInstance(), BorderFactory.createLineBorder(Color.white));
	
	public static final Image chat_panel_bgimg = Toolkit.getDefaultToolkit().getImage("assets/image/bg/chattop.jpg");
	public static final Image hall_panel_bgimg = Toolkit.getDefaultToolkit().getImage("assets/image/bg/GameHallBg.jpg");
	public static final Image online_player_panel_bgimg = Toolkit.getDefaultToolkit().getImage("assets/image/bg/Online_Player_Bg.png");	
	public static final Image room_title_panel_bgimg = Toolkit.getDefaultToolkit().getImage("assets/image/bg/roomId_Bg.png");	
	
	public static final String MUSIC_PATH="assets/audio/";
	public static final String MUSIC1=MUSIC_PATH+"The Tumbled Sea -  - 钢琴版纯音乐.mp3";
	public static final String MUSIC2=MUSIC_PATH+"The Tumbled Sea - Emily’s Song.mp3";
	public static final String MUSIC3=MUSIC_PATH+"The Tumbled Sea - summer iii.mp3";
	public static final String MUSIC4=MUSIC_PATH+"蔡健雅 - 红色高跟鞋.mp3";
	public static final String MUSIC5=MUSIC_PATH+"The Crave.mp3";
}
