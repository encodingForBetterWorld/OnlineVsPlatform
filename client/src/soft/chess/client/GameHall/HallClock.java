package soft.chess.client.GameHall;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.*;
public class HallClock extends JPanel implements Runnable{ //the black clock
	Thread timer;
	Calendar cal=Calendar.getInstance();
	int h1=0;
	int m1=0;
	int s1=0;
	public HallClock()
	{
		Date nowdate=cal.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		String timestr=dateFormat.format(nowdate);
		String[] timeInfo=timestr.split(":");
		h1=Integer.parseInt(timeInfo[0]);
		m1=Integer.parseInt(timeInfo[1]);
		s1=Integer.parseInt(timeInfo[2]);
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
				s1++;
				repaint();
				int delay=1000;
				try{
					Thread.sleep(delay);
					}catch(Exception  e){
						System.err.println(e);  
						}
					}
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
		g.setColor(Color.white);
		g.drawString(st,10,30); 
	}
}

