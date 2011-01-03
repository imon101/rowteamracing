/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Helpers;

import java.awt.Color;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
/**
 *
 * @author mdpianelli
 */
public abstract class ProceduralTexture {

public  Perlin per;
//BufferedImage textureImage;

public Color color;
public Color gradientColor;
public String name;


static String outputType = "png";

public ProceduralTexture(){

     per = new Perlin();
    
}


public String texturePath(){

    this.createTexture();
    return "Textures/Terrain/splat/"+name+"."+outputType;
}


public BufferedImage createTexture(){

    return createTexture(1024,1024);
}


public BufferedImage createTexture( int width, int height){

   File  f = new File("assets/Textures/Terrain/splat/"+name+"."+outputType);


   BufferedImage textureImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB) ;

		for(int i = 0; i < textureImage.getWidth(); i++)
			for(int j = 0; j < textureImage.getHeight(); j++){
				Color c = textureColor(i/(double)textureImage.getWidth(),j/(double)textureImage.getHeight());
                                            Color old = new Color(textureImage.getRGB(i, j));
				textureImage.setRGB(i,j,ColorToInt(ColorHelper.sumColor(c,old)));
			}


     try{
        	ImageIO.write( textureImage, outputType, f);
	} catch(IOException e) {
            System.out.println("File '" +  textureImage + "' couldn't be written.");
	}

    return textureImage;

}


int ColorToInt(Color c) {
	return (c.getRed() << 16) + (c.getGreen() << 8) + c.getBlue();
}

// Function to calculate new color
public abstract Color textureColor(double x, double y);



}
