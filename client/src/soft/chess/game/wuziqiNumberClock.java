package soft.chess.game;
import java.awt.*;
import javax.swing.*;
public class wuziqiNumberClock extends JPanel implements Runnable{ //the black clock
	Thread timer;
	int h1=0;
	int m1=0;
	int s1=0;
	public boolean se=globalVariable.x;//to control the clock
	public wuziqiNumberClock()
	{
		setBackground(Color.black);
		Font f=new Font("Times New Roman",Font.BOLD,30);
		setFont(f);
		setVisible(true); 
		Thread timer=new Thread(this);
		timer.start();
	}
	public void run()
	{
		while(true)
		{
			try{
				Thread.sleep(1);
				}catch(Exception e){
					System.err.println(e); //print error message
					}
				se=globalVariable.x;
				if(se){	
					s1++;
					repaint();
					int delay=1000;
					try{
						Thread.sleep(delay);
						se=globalVariable.x; //to check the clock go on or stop
						}catch(Exception  e){
							System.err.println(e);  
							}
						}
				}
		}
	public void clearScreen(){
		h1=0;
		m1=0;
		s1=0;
	}
	public void paint(Graphics g)
	{
		super.paint(g);
        int m2=s1/60;
	    s1=s1%60;
	    int h2=(m1+m2)/60;
	    m1=m1+m2;
		m1=m1%60;
		h1=(h1+h2)%24;
		String st="";
		if(h1 < 10)
			st += "0"+h1;
		else 
			st +=""+h1;
		if(m1<10)
			st += ":0"+m1;
		else 
			st += ":"+m1;
		if(s1<10)
			st += ":0"+s1;
		else 
			st += ":"+s1;  
		String s="Black: ";
		g.setColor(Color.yellow);
		g.drawString(s+st,20,50); 
		}
}

