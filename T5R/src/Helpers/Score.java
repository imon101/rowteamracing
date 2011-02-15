/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Helpers;

/**
 *
 * @author ArkanRow
 */
public class Score {
    int score;
    String name;

    public Score(String name, int score) {
        this.score = score;
        this.name = name;
    }

    public int getScore() { return score; }
    public String getName() { return name; }
}
