package soft.chess.util;

import javax.swing.JComponent;

public class BoundUtil {
	private BoundUtil(){}
	public static final int horizontal=1;
	public static final int vertical=2;
	public static void setCompsBound(int direct,int x,int y,int w,int h,int aw,int ah,JComponent...components){
		for(JComponent comp:components){
			comp.setBounds(x, y, w, h);
		}
		for(int i=0;i<components.length;i++){
			JComponent comp=components[i];
			if(direct==vertical){
				comp.setBounds(x, y+i*(h+ah), w, h);
			}
			if(direct==horizontal){
				comp.setBounds(x+i*(w+aw), y, w, h);
			}
		}
	}
}
