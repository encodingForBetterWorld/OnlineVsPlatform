package soft.chess.util;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class FrameUtil{
public static void baseSetFrame(JFrame frame,JComponent...components){
	    if(components!=null){
		    for(JComponent comp : components){
		    	 frame.add(comp);
		    }
	    }
		Image img = new ImageIcon("assets/image/bg/title.png").getImage();
		frame.setIconImage(img);
		frame.setResizable(false);
		frame.setVisible(true);
	}
/**
 * @Description: 添加圆角按钮
 * @param @param panel
 * @param @param bta
 * @param @param btns   
 * @return void  
 * @throws
 * @author wangsuqi
 * @date 2016年9月5日
 */
public static void baseSetBtn(JPanel panel,ActionListener bta,int x,int y,int alignment,int btnWidth,int btnHeight,Font btnFont,JButton...btns){
	panel.setLayout(null);
	int i=0;
	for(JButton btn:btns){
		btn.setSize(btnWidth, btnHeight);
		btn.setFont(btnFont);
		btn.setBounds((panel.getWidth()/2)-(btnWidth/2)+x, i*(btnHeight+alignment)+y, btnWidth, btnHeight);
		btn.addActionListener(bta);
		panel.add(btn);
		i++;
	}
	
  }

}
