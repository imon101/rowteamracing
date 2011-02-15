package Controllers;

import Nodes.CarNode;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import de.lessvoid.nifty.elements.render.TextRenderer;

/**
 *
 * @author ArkanRow
 */

public class PlayerNameController implements ActionListener {
    final int maxChars = 3;
    final String[] chars = {
        "0123456789",
        "QWERTYUIOP",
        "ASDFGHJKL",
        "ZXCVBNM"
    };

    int index;
    String name = "ROW";
    String nameWithUnderScore = "_OW";
    TextRenderer playerName;
    InputManager inputManager;
    final float underscoreTime = 0.4f;
    float time;

    public PlayerNameController(TextRenderer playerName, InputManager inputManager) {
        this.playerName = playerName;
        this.inputManager = inputManager;
        playerName.setText(name);
    }

    public void listenKeys() {
        for (int i = KeyInput.KEY_0; i <= KeyInput.KEY_M; i++) {
            String mappingName = keyInputToCharacter(i);
            if (mappingName != null) {
                inputManager.addMapping(mappingName, new KeyTrigger(i));
                inputManager.addListener(this, mappingName);
            }
        }
    }

    public void unlistenKeys() {
        inputManager.removeListener(this);
    }

    private String keyInputToCharacter(int key) {
        int sIndex, eIndex;

        if (KeyInput.KEY_0 <= key && key <= KeyInput.KEY_9) {
            sIndex = key - KeyInput.KEY_0;
            eIndex = sIndex + 1;
            return chars[0].substring(sIndex, eIndex);
        } else if (KeyInput.KEY_Q <= key && key <= KeyInput.KEY_P) {
            sIndex = key - KeyInput.KEY_Q;
            eIndex = sIndex + 1;
            return chars[1].substring(sIndex, eIndex);
        } else if (KeyInput.KEY_A <= key && key <= KeyInput.KEY_L) {
            sIndex = key - KeyInput.KEY_A;
            eIndex = sIndex + 1;
            return chars[2].substring(sIndex, eIndex);
        } else if (KeyInput.KEY_Z <= key && key <= KeyInput.KEY_M) {
            sIndex = key - KeyInput.KEY_Z;
            eIndex = sIndex + 1;
            return chars[3].substring(sIndex, eIndex);
        } else {
            return null;
        }
    }

    public void update(float tick) {
        time += tick;

        if (time < underscoreTime) {
            playerName.setText(name);
        } else if (time < underscoreTime * 2) {
            playerName.setText(nameWithUnderScore);
        } else {
            time = 0;
        }
    }
    
    public String getName() {
        return name;
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (!value)
            return;

        name = 
            (index == 0? binding : name.substring(0,1)) +
            (index == 1? binding : name.substring(1,2)) +
            (index == 2? binding : name.substring(2,3));

        playerName.setText(name);
        index = (index + 1) % maxChars;

        nameWithUnderScore =
            (index == 0? "_" : name.substring(0,1)) +
            (index == 1? "_" : name.substring(1,2)) +
            (index == 2? "_" : name.substring(2,3));
    }
}
