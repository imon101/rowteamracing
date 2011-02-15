package Controllers;


import Helpers.Score;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ArkanRow
 */
public class HighScoreTable implements ScreenController {
    final static int tableLength = 10;
    final static String scoresFile = "Assets/Text/Scores.txt";
    ArrayList<Score> scores;
    Nifty nifty;
    Screen screen;

    public HighScoreTable() {
        scores = new ArrayList<Score>();
        try {
            load(scoresFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HighScoreTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void load(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream(filename));

        try {
            while (scanner.hasNextLine()){
                String s = scanner.nextLine();
                String delims = "[ ]+";
                String[] tokens = s.split(delims);
                scores.add(new Score(tokens[0], Integer.parseInt(tokens[1])));
            }
        }
        finally{
            scanner.close();
        }

        sortScores();
    }

    private void sortScores() {
        Collections.sort(scores, new Comparator<Score>() {

        public int compare(Score t1, Score t2) {
                return t2.getScore() - t1.getScore();
            }
        });
    }

    private void save(String filename) throws FileNotFoundException, IOException {
        String toSave = "";
        System.out.println("scores.length: " + scores.size());
        for (int i = 0; i < tableLength; i++) {
            toSave += scores.get(i).getName() + " " + scores.get(i).getScore() +
                    System.getProperty("line.separator");
        }

        FileOutputStream fo = new FileOutputStream(filename);
        fo.write(toSave.getBytes());
        fo.close();
    }

    public void addScore(String name, int score) throws FileNotFoundException, IOException {
        scores.add(new Score(name, score));
        sortScores();
        save(scoresFile);
    }

    public Score getScoreForPosition(int i) {
        return scores.get(i);
    }

    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

   public void onStartScreen() {
        try {
            load(scoresFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HighScoreTable.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < tableLength; i++) {
            System.out.println("i=" + i + " " +  scores.get(i).getName() + " " + scores.get(i).getScore());
            Element e = screen.findElementByName("" + (tableLength - i - 1));
            TextRenderer tr = e.getRenderer(TextRenderer.class);
            tr.setText(scores.get(i).getName() + " " + scores.get(i).getScore());
        }
    }

    public void onEndScreen() {
    }

    public void back() {
        nifty.gotoScreen("start");
    }
}
