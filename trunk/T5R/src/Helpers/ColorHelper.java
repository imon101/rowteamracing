package Helpers;

import java.awt.Color;

public class ColorHelper{

	
	public final static Color sumColor(Color c1,Color c2){
	/*	System.out.println("Entra");
		System.out.println(c1);
		System.out.println(c2);
	*/	Color c = new Color(clamp(c1.getRed()+c2.getRed(),0,255),clamp(c1.getGreen()+c2.getGreen(),0,255),clamp(c1.getBlue()+c2.getBlue(),0,255));
	/*	System.out.println(c);
		System.out.println("Sale");
	*/	return c;
	}
	
	public final static Color subColor(Color c1, Color c2){
		return new Color(clamp(c1.getRed()-c2.getRed(),0,255),clamp(c1.getGreen()-c2.getGreen(),0,255),clamp(c1.getBlue()-c2.getBlue(),0,255));
	}
	
	public final static Color mulColor(Color c1, Color c2){
		return new Color(clamp(c1.getRed()*c2.getRed(),0,255),clamp(c1.getGreen()*c2.getGreen(),0,255),clamp(c1.getBlue()*c2.getBlue(),0,255));
	}
	public final static Color mulColor(Color c1,double number){
		
/*		System.out.println("Entra");
		System.out.println(c1);
		System.out.println(number);
		Color c = new Color(clamp((int)(c1.getRed()*number),0,255),clamp((int)(c1.getGreen()*number),0,255),clamp((int)(c1.getBlue()*number),0,255));
	*/  Color c = new Color( (float)(clamp(c1.getRed()*number))/255,(float)(clamp(c1.getGreen()*number)/255),(float)(clamp(c1.getBlue()*number)/255));
		//Color c = new Color( (float)(c1.getRed()*number)/255,(float)(c1.getGreen()*number/255),(float)(c1.getBlue()*number/255));
		/*System.out.println(c);
		System.out.println("Sale");
*/
		return c;
	}
	
	static double clamp(double val){
		return val < 0? 0 : (val > 255? 255 : val);
	}
	
	static int clamp(int val, int min, int max) {
		return val < min? min : (val > max ? max : val); 
	}
	
	
}
