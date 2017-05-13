package soft.chess.client.GameHall;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import soft.chess.client.GameHallClient;
import soft.chess.client.LoginClient;
import soft.chess.client.RegisterClient;
import soft.chess.util.ImageIconUtil;
import soft.chess.util.SoftConstant;

public class VisitorPanel extends JPanel{
	private JButton loginbtn,regisbtn;
	private JLabel tiplabel,iconlabel;
	private LoginClient loginc;
	private RegisterClient regc;
	public VisitorPanel(JFrame parentFrame){
		setLayout(null);
		loginbtn=new JButton("登录");
		regisbtn=new JButton("注册");
		setBorder(SoftConstant.shadowborder);
		iconlabel = new JLabel();
		tiplabel = new JLabel();
		
		iconlabel.setBounds(5, 5, 133,135);
		iconlabel.setIcon(ImageIconUtil.setImageIcon("assets/image/playerIcon/Portrait.png", iconlabel));
		tiplabel.setText("游客");
		tiplabel.setFont(SoftConstant.userNamefont);
		
		tiplabel.setBounds(50, 140, 100, 30);
		loginbtn.setBounds(10, 180, 60, 30);
		regisbtn.setBounds(75, 180, 60, 30);
		
		loginbtn.setBorder(SoftConstant.shadowborder);
		regisbtn.setBorder(SoftConstant.shadowborder);
		
		loginbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				parentFrame.setEnabled(false);
				try {
					loginc=LoginClient.getInstance(parentFrame);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		regisbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					regc=RegisterClient.getInstance(parentFrame);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		add(iconlabel);
		add(tiplabel);
		add(loginbtn);
		add(regisbtn);
		setBorder(SoftConstant.shadowborder); //设置边框    
	}
	public synchronized LoginClient getLoginc() {
		return loginc;
	}
	public synchronized RegisterClient getRegc() {
		return regc;
	}
	
}
