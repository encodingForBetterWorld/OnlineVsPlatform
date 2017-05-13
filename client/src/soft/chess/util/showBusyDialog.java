package soft.chess.util;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXBusyLabel;

public class showBusyDialog extends JFrame {
    private JLabel text;
    Toolkit tk = Toolkit.getDefaultToolkit();
    Dimension screensize = tk.getScreenSize();
    int height = screensize.height;
    int width = screensize.width;
    private String str = null;
    private JXBusyLabel busyLabel;
    public showBusyDialog(String str) {
        this.str = str;
        setUndecorated(true);
        setLocationRelativeTo(null);
        setVisible(true);
        setLayout(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        text = new JLabel( str );         
        text.setBounds(10, 0, 100, 50);
        text.setFont(SoftConstant.mfont);
        add(text);
        
        busyLabel=new JXBusyLabel();
        busyLabel.setBusy(true);
        busyLabel.setBounds(120, 0, 50, 50);
        add(busyLabel);
        
        pack();
        setBounds(width / 2 - 150, height - 300, 160, 60);
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				 try {
			            Thread.sleep(15000);
			        } catch (InterruptedException e1) {
			            e1.printStackTrace();
			        }
			        text.setText("连接超时");
			        try {
			            Thread.sleep(2000);
			        } catch (InterruptedException e1) {
			            e1.printStackTrace();
			        }
			        dispose();
			}
		}).start();       
        
 
    }
    public static void main(String[] args){
    	new showBusyDialog("请稍后...");
    }
}
