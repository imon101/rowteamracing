/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Helpers;

import com.jme3.math.Vector3f;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author ArkanRow
 */
public class FloatArrayLoader {
    public static Float[] load(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream(filename));
        ArrayList<Float> vec = new ArrayList<Float>();

        try {
            while (scanner.hasNextLine()){
                String s = scanner.nextLine();
                vec.add(Float.parseFloat(s));
                //System.out.println("Elem: " + vec.get(vec.size() - 1));
            }
        }
        finally{
            scanner.close();
        }

        return vec.toArray(new Float[0]);
    }
}