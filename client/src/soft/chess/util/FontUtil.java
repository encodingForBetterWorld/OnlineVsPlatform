package soft.chess.util;

import java.awt.Font;

import javax.swing.JComponent;

public class FontUtil {
	private FontUtil() {}
	public static void setComsFont(Font font,JComponent...components){
		for(JComponent comp:components){
			comp.setFont(font);
		}
	}
}
