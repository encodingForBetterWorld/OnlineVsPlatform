package soft.chess.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.ImageIcon;
import javax.swing.JButton;

//播放器上的按钮
public class OnlyImgButton extends JButton{
	public static final int PLAY_BTN=1;
	public static final int PAUSE_BTN=2;
	public static final int PRE_BTN=3;
	public static final int NEXT_BTN=4;
	
	public static final int SHOWLIST_BTN=5;
	public static final int HIDELIST_BTN=6;
	
	public static final int SEARCH_BTN=7;
	
	public static final int PRE_PAGE_BTN=8;
	public static final int NEXT_PAGE_BTN=9;
	public static final int GO_PAGE_BTN=10;
	private int type;
	private int btnWidth,btnHeight;
	private Shape shape;//用于保存按钮的形状,有助于侦听单击按钮事件
	public OnlyImgButton(int type,int btnWidth,int btnHeight){
		this.type=type;
		this.btnWidth=btnWidth;
		this.btnHeight=btnHeight;
	  //使jbutton不画背景
		setContentAreaFilled(false);
	 }
	 
	 //画图的按钮的背景和标签
	 @Override
	 protected void paintComponent(Graphics g){
	 /*
	  * 获取按钮背景图片
	  */
	 Image default_image = null;
	 Image hover_image = null;
	 Image press_image = null;
		 if(type==1){
			 default_image=new ImageIcon("assets/image/button/play.png").getImage();
			 hover_image=new ImageIcon("assets/image/button/play_hover.png").getImage();
			 press_image=new ImageIcon("assets/image/button/play_press.png").getImage();
		 }
		 else if(type==2){
			 default_image=new ImageIcon("assets/image/button/pause.png").getImage();
			 hover_image=new ImageIcon("assets/image/button/pause_hover.png").getImage();
			 press_image=new ImageIcon("assets/image/button/pause_press.png").getImage();
		 }
		 else if(type==3){
			 default_image=new ImageIcon("assets/image/button/pre.png").getImage();
			 hover_image=new ImageIcon("assets/image/button/pre_hover.png").getImage();
			 press_image=new ImageIcon("assets/image/button/pre_press.png").getImage();
		 }
		 else if(type==4){
			 default_image=new ImageIcon("assets/image/button/next.png").getImage();
			 hover_image=new ImageIcon("assets/image/button/next_hover.png").getImage();
			 press_image=new ImageIcon("assets/image/button/next_press.png").getImage();
		 }
		 else if(type==5){
			 default_image=new ImageIcon("assets/image/button/showlist.png").getImage();
			 hover_image=new ImageIcon("assets/image/button/showlist_hover.png").getImage();
			 press_image=new ImageIcon("assets/image/button/showlist_press.png").getImage();
		 }
		 else if(type==6){
			 default_image=new ImageIcon("assets/image/button/hidelist.png").getImage();
			 hover_image=new ImageIcon("assets/image/button/hidelist_hover.png").getImage();
			 press_image=new ImageIcon("assets/image/button/hidelist_press.png").getImage();
		 }
		 else if(type==7){
			 default_image=new ImageIcon("assets/image/button/search.png").getImage();
			 hover_image=new ImageIcon("assets/image/button/search_hover.png").getImage();
			 press_image=new ImageIcon("assets/image/button/search_press.png").getImage();
		 }
		 else if(type==GO_PAGE_BTN){
			 default_image=new ImageIcon("assets/image/button/goto_page.png").getImage();
			 hover_image=new ImageIcon("assets/image/button/goto_page_hover.png").getImage();
			 press_image=new ImageIcon("assets/image/button/goto_page_press.png").getImage();
		 }
		 else if(type==PRE_PAGE_BTN){
			 default_image=new ImageIcon("assets/image/button/pre_page.png").getImage();
			 hover_image=new ImageIcon("assets/image/button/pre_page_hover.png").getImage();
			 press_image=new ImageIcon("assets/image/button/pre_page_press.png").getImage();
		 }
		 else if(type==NEXT_PAGE_BTN){
			 default_image=new ImageIcon("assets/image/button/next_page.png").getImage();
			 hover_image=new ImageIcon("assets/image/button/next_page_hover.png").getImage();
			 press_image=new ImageIcon("assets/image/button/next_page_press.png").getImage();
		 }
		 else{
			 throw new IllegalArgumentException();
		 }
	  
		  //如果点击
		 if(getModel().isArmed()){			
		   	g.drawImage(press_image, 0, 0, btnWidth, btnHeight, this);
		 }
		  //如果滑过
		 else if(getModel().isRollover()){
			 g.drawImage(hover_image, 0, 0, btnWidth, btnHeight, this);
		 }
		 else{
		   //其他事件用默认的背景色显示按钮
			 g.drawImage(default_image, 0, 0, btnWidth, btnHeight, this);
		 }
		  //绘制圆角矩形中的文字,保证文字在图片中居中
	
	//		  g.fillRoundRect(0, 0, 120, 60, 60, 60);
			  
			  //调用父类的paintComponent画按钮的标签和焦点所在的小矩形
			 super.paintComponents(g);
		 }
//不画边框
	 @Override
	 protected void paintBorder(Graphics g){
//用简单的弧充当按钮的边框
//	  g.setColor(new Color(255,255,240));
//	  Graphics2D graphics2 = (Graphics2D) g;
//	  graphics2.setStroke(SoftConfig.btnstroke);
//	  RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(-2, -2, btnWidth+3, btnHeight+3, 45, 45);
//	  graphics2.draw(roundedRectangle);
	 }
	 
	 //判断鼠标是否点在按钮上
	 @Override
	 public boolean contains(int x,int y){
	  //如果按钮边框,位置发生改变,则产生一个新的形状对象
	  if((shape==null)||(!shape.getBounds().equals(getBounds()))){
	   //构造椭圆型对象
	   shape=new Ellipse2D.Float(0,0,getWidth(),getHeight());
	  }
	  //判断鼠标的x,y坐标是否落在按钮形状内
	  return shape.contains(x,y);
	 }
}
