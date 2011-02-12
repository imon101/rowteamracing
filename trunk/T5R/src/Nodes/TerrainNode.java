/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Nodes;

import Helpers.OrganicTexture;
import Helpers.RoadTexture;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.List;
import jme3tools.converters.ImageToAwt;

/**
 *
 * @author ArkanRow
 */
public class TerrainNode extends Node {
    public TerrainNode(AssetManager assetManager, Camera camera, boolean regenerateTextures) {
            // First, we load up our textures and the heightmap texture for the terrain
        // TERRAIN TEXTURE material
        Material matRock = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");

        // ALPHA map (for splat textures)
        matRock.setTexture("m_Alpha", assetManager.loadTexture("Textures/Terrain/splat/CraterAlphamap.png"));

        // HEIGHTMAP image (for the terrain heightmap)
        Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/Crater.png");

        // GRASS texture
        OrganicTexture grassTexture = new OrganicTexture(regenerateTextures);
        Texture grass = assetManager.loadTexture(grassTexture.texturePath());

        grass.setWrap(Texture.WrapMode.Repeat);
        matRock.setTexture("m_Tex1", grass);
        matRock.setFloat("m_Tex1Scale", 64f);

        // DIRT texture
        Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(Texture.WrapMode.Repeat);
        matRock.setTexture("m_Tex2", dirt);
        matRock.setFloat("m_Tex2Scale", 32f);

        // ROCK texture
        RoadTexture road = new RoadTexture(regenerateTextures);
        Texture rock = assetManager.loadTexture(road.texturePath());
        rock.setWrap(Texture.WrapMode.Repeat);
        matRock.setTexture("m_Tex3", rock);
        matRock.setFloat("m_Tex3Scale", 128f);

        // CREATE HEIGHTMAP
        AbstractHeightMap heightmap = null;
        try {
            //heightmap = new HillHeightMap(1025, 1000, 50, 100, (byte) 3);

            heightmap = new ImageBasedHeightMap(ImageToAwt.convert(heightMapImage.getImage(), false, true, 0), 0.25f);
            heightmap.load();

        } catch (Exception e) {
        }

        /*
         * Here we create the actual terrain. The tiles will be 65x65, and the total size of the
         * terrain will be 513x513. It uses the heightmap we created to generate the height values.
         */
        TerrainQuad terrain = new TerrainQuad("terrain", 65, 1025, heightmap.getHeightMap());
        terrain.setShadowMode(ShadowMode.Off);
        List<Camera> cameras = new ArrayList<Camera>();
        cameras.add(camera);
        TerrainLodControl control = new TerrainLodControl(terrain, cameras);
        terrain.addControl(control);
        terrain.setMaterial(matRock);
       // terrain.setLocalScale(new Vector3f(2,2,2));
        terrain.setModelBound(new BoundingBox());
        terrain.updateModelBound();

        terrain.setLocalTranslation(0, 0, 0);
        attachChild(terrain);
    }
}
