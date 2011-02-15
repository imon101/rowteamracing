/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Helpers;

import com.jme3.math.Vector3f;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author ArkanRow
 */
public class Vector3ArrayLoader {
    public static Vector3f[] load(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream(filename));
        ArrayList<Vector3f> vec = new ArrayList<Vector3f>();

        try {
            while (scanner.hasNextLine()){
                String s = scanner.nextLine();
                String delims = "[ ]+";
                String[] tokens = s.split(delims);

                vec.add(new Vector3f(
                         Float.parseFloat(tokens[0]),
                         Float.parseFloat(tokens[2]),
                        -Float.parseFloat(tokens[1])));
                //System.out.println("Elem: " + vec.get(vec.size() - 1));
            }
        }
        finally{
            scanner.close();
        }

        return vec.toArray(new Vector3f[0]);
    }
}