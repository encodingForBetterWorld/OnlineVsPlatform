package soft.chess.game;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class setFontDialog extends JDialog{
	String name="宋体";
	public setFontDialog(JTextField jtf,JTextArea jta){
        setLayout(new FlowLayout());
        ButtonGroup  bg=new ButtonGroup();
        JRadioButton jrb1=new JRadioButton("幼圆");
        JRadioButton jrb2=new JRadioButton("新宋体");
        JRadioButton jrb3=new JRadioButton("黑体");
        JRadioButton jrb5=new JRadioButton("华文行楷") ;
        JRadioButton jrb4=new JRadioButton("Gulim");
        JRadioButton jrb6=new JRadioButton("华文细黑");
        bg.add(jrb1);
        bg.add(jrb2);
        bg.add(jrb3);
        bg.add(jrb4);
        bg.add(jrb5);
        bg.add(jrb6);
        add(jrb1);
        add(jrb2);
        add(jrb3);
        add(jrb4);
        add(jrb5);
        add(jrb6);
        jrb1.addItemListener(new ItemListener()
        {
             public void itemStateChanged(ItemEvent e)
             {
                  name="幼圆";
                  dispose();
                     Font f=new Font(name,Font.BOLD,16);
                     jtf.setFont(f);
                     jta.setFont(f);
            
             }
        });
        jrb2.addItemListener(new ItemListener()
        {
             public void itemStateChanged(ItemEvent e)
             {
                  name="新宋体";
                  dispose();
                     Font f=new Font(name,Font.BOLD,16);
                     jtf.setFont(f);
                     jta.setFont(f);
            
             }
        });
        
        jrb3.addItemListener(new ItemListener()
        {
             public void itemStateChanged(ItemEvent e)
             {
                  name="黑体";
                  dispose();
                     Font f=new Font(name,Font.BOLD,16);
                     jtf.setFont(f);
                     jta.setFont(f);
            
             }
        });
        jrb5.addItemListener(new ItemListener()
        {
             public void itemStateChanged(ItemEvent e)
             {
                  name="华文行楷";
                  dispose();
                     Font f=new Font(name,Font.BOLD,16);
                     jtf.setFont(f);
                     jta.setFont(f);
            
             }
        });
        jrb4.addItemListener(new ItemListener()
        {
             public void itemStateChanged(ItemEvent e)
             {
                  name="Gulim";
                  dispose();
                   Font f=new Font(name,Font.BOLD,16);
                   jtf.setFont(f);
                   jta.setFont(f);
            
             }
        });
        jrb6.addItemListener(new ItemListener()
        {
             public void itemStateChanged(ItemEvent e)
             {
                  name="华文细黑";
                  dispose();
                     Font f=new Font(name,Font.BOLD,16);
                     jtf.setFont(f);
                     jta.setFont(f);
            
             }
        });
        setTitle("字体");
        setSize(150, 200);
        setVisible(true);
        validate();

	}
}
