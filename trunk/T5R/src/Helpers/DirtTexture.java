/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Helpers;


import Helpers.ColorHelper;
import Helpers.ProceduralTexture;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.vecmath.Vector3d;

/**
 *
 * @author mdpianelli
 */
public class DirtTexture extends ProceduralTexture{


	public DirtTexture(boolean regenerateTexture){
            super(regenerateTexture);
		per.persistence = 0.85;
		per.n = 16;
                name = "Dirt";
                color = new Color(0,200,0);
                gradientColor = new Color(0,100,0);
	}



        public BufferedImage createTexture(){

            return super.createTexture(1024,1024);
        }
        public Color textureColor(double x, double y) {
		double amount = per.perlinNoise(x,y) + per.turbulence(x, y, 2)
						+ 0.5 * per.perlinNoise(0.5*x,0.5*y) + 0.5 * per.turbulence(0.5*x, 0.5*y, 4)
						+ 0.25 * per.perlinNoise(0.25*x,0.25*y) + 0.25 * per.turbulence(0.25*x, 0.25*y, 8);
		//TODO usar   per.persistence = 1.2; per.n = 2;per.stripes(sp.x + 2*per.turbulence(sp.x,sp.y,sp.z), 2.4);
		amount = amount - Math.floor(amount);
		return  ColorHelper.sumColor(
				ColorHelper.mulColor(color, amount),
				ColorHelper.mulColor(gradientColor, 1-amount)
		);

	}




}
