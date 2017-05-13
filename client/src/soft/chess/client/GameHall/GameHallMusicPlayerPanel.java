package soft.chess.client.GameHall;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.jhlabs.image.ImageUtils;

import soft.chess.util.AudioPlayMP3;
import soft.chess.util.BackgroundImagePanel;
import soft.chess.util.ImageIconUtil;
import soft.chess.util.OnlyImgButton;
import soft.chess.util.SoftConstant;
/*
 * 大厅背景音乐播放器面板
 */
public class GameHallMusicPlayerPanel extends BackgroundImagePanel{
	//播放前一首
	private JButton preBtn;
	//播放后一首
	private JButton nextBtn;
	//暂停播放
	private JButton pauseBtn;
	//继续播放
	private JButton playeBtn;
	//显示当前音乐名称
	private JTextField currentMusicInfo;
	//播放音乐线程
	private AudioPlayMP3 playMp3;
	//播放动画线程
	private Thread animate;
	//显示动画的标签
	private volatile JLabel animateLabel;
	private volatile static boolean isAnimate=true;

	public GameHallMusicPlayerPanel() {
		super(SoftConstant.chat_panel_bgimg);
			
		setLayout(null);
		// TODO Auto-generated constructor stub
		currentMusicInfo=new JTextField();
		animateLabel=new JLabel();
		playeBtn=new OnlyImgButton(OnlyImgButton.PLAY_BTN,32,32);
		pauseBtn=new OnlyImgButton(OnlyImgButton.PAUSE_BTN,32,32);
		nextBtn=new OnlyImgButton(OnlyImgButton.NEXT_BTN,32,32);
		preBtn=new OnlyImgButton(OnlyImgButton.PRE_BTN,32,32);
		playMp3=new AudioPlayMP3(new String[]{SoftConstant.MUSIC1,SoftConstant.MUSIC2,SoftConstant.MUSIC3},this);
		
		preBtn.setBounds(5, 40, 32, 32);
		playeBtn.setBounds(50, 40, 32, 32);
		pauseBtn.setBounds(50, 40, 32, 32);
		nextBtn.setBounds(95, 40, 32, 32);
		animateLabel.setBounds(142, 30, 48, 48);
		
		Icon vol0=ImageIconUtil.setImageIcon("assets/image/label/vol0.png", animateLabel);
		Icon vol1=ImageIconUtil.setImageIcon("assets/image/label/vol1.png", animateLabel);
		Icon vol2=ImageIconUtil.setImageIcon("assets/image/label/vol2.png", animateLabel);
		Icon vol3=ImageIconUtil.setImageIcon("assets/image/label/vol3.png", animateLabel);
		
		List<Icon> myVols=new ArrayList<>();
		myVols.add(vol0);
		myVols.add(vol1);
		myVols.add(vol2);
		myVols.add(vol3);
		add(animateLabel);
		animateLabel.setIcon(vol0);
		//启动播放动画的线程
		animate=new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
			try {
				while(isAnimate){					
					for(int i=0;i<6;i++){
						Thread.sleep(500);
						if(i>3){
							int j=6-i;
							animateLabel.setIcon(myVols.get(j));
						}
						else{
							animateLabel.setIcon(myVols.get(i));
						}
					}
				}
			} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		
		playeBtn.setVisible(false);
		
		currentMusicInfo.setBounds(5, 5, 190, 28);
		currentMusicInfo.setEditable(false);
		currentMusicInfo.setHorizontalAlignment(JTextField.CENTER);
		currentMusicInfo.setFont(SoftConstant.toosmfont);
		
		preBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				playMp3.cutPre();
				
			}
		});
		playeBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				animate.resume();
				playeBtn.setVisible(false);
				pauseBtn.setVisible(true);
				playMp3.goon();
			}
		});
		pauseBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				animate.suspend();
				animateLabel.setIcon(vol0);				
				pauseBtn.setVisible(false);
				playeBtn.setVisible(true);
				playMp3.pause();			
			}
		});
		nextBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				playMp3.cutNext();
			}
		});
		
		add(currentMusicInfo);
		add(preBtn);
		add(playeBtn);
		add(pauseBtn);
		add(nextBtn);

	}
	//播放大厅背景音乐
	public void playBgMusic() throws MalformedURLException
	{
		new Thread(playMp3).start();
	}
	//回调方法,显示当前播放的歌曲名称
	public void changeMusic(String str){
		this.currentMusicInfo.setText(str);
	}
	//关闭歌曲
	public void stopMusic(){
		playMp3.stopPlay();
	}
	public void beginMusic(){
		playMp3.beginPlay();
		beginAnimate();
		if(!animate.isAlive()){
			animate.start();
		}
		//开启播放音乐线程
		try {
			playBgMusic();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized void stopAnimate(){
		isAnimate=false;
	}
	public synchronized void beginAnimate(){
		isAnimate=true;
	}
}
