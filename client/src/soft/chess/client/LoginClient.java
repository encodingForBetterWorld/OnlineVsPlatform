package soft.chess.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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

//全部用单例模式
public class LoginClient extends JFrame{
	private Player player;
	
	private static LoginClient logc;
	
	private JButton okButton = new JButton("登录"),clearButton = new JButton("重置");
	private JTextField TextField;
	private JPasswordField pwdField;
	private JLabel bglabel;
	private JLabel tip1,tip2;
	
	
	private static GameHallClient parentFrame;
	
	private LoginClient(JFrame parentFrame) throws IOException{
		//构造窗口时在服务器上申请连接
		
		setLayout(null);
		setSize(300, 470);
		setTitle("请登录");
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

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		/*
		 * 添加按钮
		 */
		okButton.setFont(SoftConstant.smfont);
		okButton.setBorder(SoftConstant.shadowborder);
		clearButton.setFont(SoftConstant.smfont);
		okButton.setBounds(40, 400, 100, 40);
		
		clearButton.setBounds(150, 400, 100, 40);
		clearButton.setBorder(SoftConstant.shadowborder);
		
		PlayerAction loginAction=new PlayerAction
				.Builder(PlayerAction.LOGIN_ACTION)
				.setOutterFrame(LoginClient.this)
				.build();
		okButton.addActionListener(loginAction);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				parentFrame.setEnabled(true);
				//构造窗口时在服务器上释放连接
				super.windowClosing(e);
			}
		});

		clearButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				TextField.setText("");
				pwdField.setText("");
			}
		});
		
		/*
		 * 添加
		 */		
		FrameUtil.baseSetFrame(this,
				bglabel,tip1,tip2,TextField,pwdField,okButton,clearButton);
	}
	public static LoginClient getInstance(JFrame parentFrame) throws IOException{
		LoginClient.parentFrame=(GameHallClient) parentFrame;
		if(logc==null){
			logc=new LoginClient(parentFrame);
		}
		logc.setVisible(true);
		return logc;
	}
	
	//回调方法
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
