/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Helpers;

import java.awt.Color;
import java.awt.image.BufferedImage;


/**
 *
 * @author mdpianelli
 */
public class RoadTexture extends ProceduralTexture{


	public RoadTexture(boolean regenearteTexture){
            super(regenearteTexture);

            	per.persistence = 0.925;
		per.n = 6;

                name = "Road";
                color = new Color(50,50,50);
                gradientColor = new Color(100,100,100);
	}



        public BufferedImage createTexture(){

            return super.createTexture(1024,1024);
        }
        public Color textureColor(double x, double y) {


	    double turbPower = 2.0; //makes twists
	    int turbSize = 100; //initial size of the turbulence

            double xyValue = turbPower * per.turbulence(x, y, turbSize) / 256.0;

          double amount = 256 * Math.abs((Math.sin(xyValue * 3.14159)));


		amount = amount - Math.floor(amount);
		return  ColorHelper.sumColor(
				ColorHelper.mulColor(color, amount),
				ColorHelper.mulColor(gradientColor, 1-amount)
		);



	}




}
