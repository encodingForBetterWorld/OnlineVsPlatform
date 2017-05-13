package soft.chess.client.GameHall;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import soft.chess.util.FrameUtil;
import soft.chess.util.ImageIconUtil;
import soft.chess.util.RoundRectButton;
import soft.chess.util.SoftConstant;



public class PlayerOperationPanel extends JPanel{
	//公告更新提示
	private int count=0;
	private JLabel updatetip,numtip;
	private JButton updateInfoButton = new RoundRectButton("编辑资料",120,50),
	showRecordButton = new RoundRectButton("查看战绩",120,50),
	creatRoomButton = new RoundRectButton("创建房间", 120,50),
	showAnnButton = new RoundRectButton("显示公告", 120,50);
	public PlayerOperationPanel(){
		setSize(150, 340);
		//公告提示标签
		updatetip=new JLabel();
		updatetip.setBounds(90, 230, 64, 64);
		updatetip.setLayout(null);

		//显示更新数字		
		numtip=new JLabel("");
		numtip.setFont(SoftConstant.mfont);
		numtip.setForeground(Color.white);
		
		updatetip.setVisible(false);
		updatetip.add(numtip);

		add(updatetip);
		showAnnButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				count=0;
				if(!numtip.getFont().equals(SoftConstant.mfont)){
					System.out.println("重设");
					numtip.setFont(SoftConstant.mfont);
				}
				updatetip.setVisible(false);
			}
		});
		FrameUtil.baseSetBtn(this, null, 0, 0, 28, 120, 50, SoftConstant.smfont, updateInfoButton,showRecordButton,creatRoomButton,showAnnButton);
		setBorder(SoftConstant.shadowborder); //设置边框    
	}
	//回调方法
	public void addNumtip() {
		if(!updatetip.isVisible()){
			System.out.println("显示");
			updatetip.setIcon(ImageIconUtil.setImageIcon("assets/image/button/dot_red.png", updatetip));
			updatetip.setVisible(true);			
		}
		count++;
				
		numtip.setText(""+count);
		int strSize=numtip.getText().length();
		//根据位数改变字体大小
		if(strSize==3&&!numtip.getFont().equals(SoftConstant.mmfont)){
			numtip.setFont(SoftConstant.mmfont);
		}
		if(strSize==4&&!numtip.getFont().equals(SoftConstant.lsmfont)){
			numtip.setFont(SoftConstant.lsmfont);
		}		
		numtip.setBounds(28-strSize*4, 17, 30, 30);
	}
}
