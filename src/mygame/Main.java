package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import mygame.states.TestAppState;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    AppState testAppSTate = new TestAppState();
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Box b = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);

        //rootNode.attachChild(geom);
        //Spatial model = getAssetManager().loadModel("Models/Building/Building.j3o");
        //model.setMaterial(mat);
        //rootNode.attachChild(model);
        
        PointLight light = new PointLight();
        light.setColor(ColorRGBA.White);
        light.setPosition(new Vector3f(10.0f, 5.0f, 30.0f));
        rootNode.addLight(light);

        
        this.getStateManager().attach(testAppSTate);
        testAppSTate.setEnabled(true);
        
        initKeys();
    }

    @Override
    public void simpleUpdate(float tpf) {
        //Spatial s = rootNode.getChild("Box");
        //s.rotate(0.1f*tpf, 0.1f*tpf, 0.1f*tpf);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    private void initKeys(){
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "Pause");
    }
    
    private ActionListener actionListener = new ActionListener(){

        public void onAction(String name, boolean isPressed, float tpf) {
            if(name.equals("Pause") && isPressed){
                testAppSTate.setEnabled(!testAppSTate.isEnabled());
            }
        }
        
    };
}
