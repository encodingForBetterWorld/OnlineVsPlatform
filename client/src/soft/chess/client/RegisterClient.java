package soft.chess.client;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.sf.json.JSONObject;
import soft.chess.action.PlayerAction;
import soft.chess.domain.Player;
import soft.chess.util.FrameUtil;
import soft.chess.util.ImageIconUtil;
import soft.chess.util.HallServerMsgUtil;
import soft.chess.util.SocketUtil;
import soft.chess.util.SoftConstant;
import soft.chess.util.showBusyDialog;

//窗体全部用单例模式
public class RegisterClient extends JFrame{
	private Player player;
	
	private static RegisterClient regisc;
	
	private JButton okButton = new JButton("注册"),clearButton = new JButton("重置");
	private JTextField TextField;
	private JPasswordField pwdField;
	private JLabel bglabel;
	private JLabel tip1,tip2;
	
	private static GameHallClient parentFrame;
	
	private RegisterClient(JFrame parentFrame) throws IOException{
		setLayout(null);
		setSize(300, 470);
		setTitle("请注册");
		/*
		 * 设置窗口在屏幕中的出现位置
		 */
		setLocation(SoftConstant.screenWidth/3,SoftConstant.screenHeight/5);
		setLocationByPlatform(false);
		/*
		 * 添加图片
		 */
		bglabel=new JLabel();
		bglabel.setBounds(0, 0, 300, 300);
		bglabel.setIcon(ImageIconUtil.setImageIcon("assets/image/bg/LoginBg.png", bglabel));
		/*
		 * 添加文本框
		 */
		tip1=new JLabel("昵称:");
		tip2=new JLabel("密码:");
		TextField=new JTextField();
		pwdField=new JPasswordField();
		tip1.setBounds(10, 310, 50, 30);
		tip1.setFont(SoftConstant.smfont);
		TextField.setBounds(60, 310, 200, 30);
		TextField.setFont(SoftConstant.smfont);
		tip2.setBounds(10, 360, 50, 30);
		tip2.setFont(SoftConstant.smfont);
		pwdField.setBounds(60, 360, 200, 30);
		pwdField.setFont(SoftConstant.smfont);

		/*
		 * 添加按钮
		 */
		okButton.setFont(SoftConstant.smfont);
		clearButton.setFont(SoftConstant.smfont);
		okButton.setBounds(40, 400, 100, 30);
		clearButton.setBounds(150, 400, 100, 30);
		
		okButton.addActionListener(new PlayerAction
				.Builder(PlayerAction.REGIS_ACTION)
				.setOutterFrame(this).build());
		
		clearButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				TextField.setText("");
				pwdField.setText("");
			}
		});
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		/*
		 * 添加
		 */		
		FrameUtil.baseSetFrame(this,
				bglabel,tip1,tip2,TextField,pwdField,okButton,clearButton);
	}
	public static RegisterClient getInstance(JFrame parentFrame) throws IOException{
		RegisterClient.parentFrame=(GameHallClient) parentFrame;
		if(regisc==null){
			regisc=new RegisterClient(parentFrame);
		}
		regisc.setVisible(true);
		return regisc;
	}
	
	public void hasLogin(Player player){
		parentFrame.getRoomtablep().hasLogin(player);
	}

	public Player getPlayer() {
		if(TextField.getText().trim().equals("")
				||new String(pwdField.getPassword()).trim().equals("")){
			JOptionPane.showMessageDialog(null, "用户名或密码不能为空","错误提示",JOptionPane.ERROR_MESSAGE);
		}
		else{
			player=new Player();
			player.setName(TextField.getText().trim());
			player.setPassword(new String(pwdField.getPassword()));
		}
		return player;
	}
	
	
}
