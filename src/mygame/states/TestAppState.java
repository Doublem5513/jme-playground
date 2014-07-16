/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import net.java.games.input.Component;

/**
 *
 * @author Matija
 */
public class TestAppState extends AbstractAppState implements ActionListener {
    
    private SimpleApplication app;
    
    private Spatial model;
    private Spatial town;
    private Spatial shuttle;
    private Spatial sceneModel;
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;

    private boolean thruster = false;
    private boolean floating = false;

      //Temporary vectors used on each frame.
  //They here to avoid instanciating new vectors on each frame
  private Vector3f camDir = new Vector3f();
  private Vector3f camLeft = new Vector3f();

  
  private final Vector3f gravity = new Vector3f(0.0f, -9.81f, 0.0f);
  private int count = 0;
  
  PointLight light = new PointLight();

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication)app;
        bulletAppState = new BulletAppState();
        app.getStateManager().attach(bulletAppState);
        
        loadScene();
        initShuttle();
        setUpKeys();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        app.getRootNode().detachChild(model);
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
        model.rotate(0.0f, 0.1f*tpf, 0.0f);
        
        RigidBodyControl control = null;
        if(shuttle != null){
            control = shuttle.getControl(RigidBodyControl.class);
        }
        if(!(control == null)){
            if(thruster){
                //control.setGravity(gravity.divide(-2.0f));
                control.applyCentralForce(gravity.mult(-1.5f));
            }else{
                //control.setGravity(gravity);
            }
            
            if(floating){
                //if(shuttle.getLocalTranslation().y < 2.0f){
                    control.applyCentralForce(
                            gravity.mult(
                            -1.0f * 
                            ((3.0f - shuttle.getLocalTranslation().y) 
                            * control.getLinearVelocity().y) - 1.0f)
                            
                            );
                
                    //}
            }
            
            light.setPosition(shuttle.getLocalTranslation());
        }
    }
    
    private void loadScene(){
        final Node rootNode = app.getRootNode();
        model = app.getAssetManager().loadModel("Models/Building/Building.j3o");
        
        app.getAssetManager().registerLocator("external-assets/town.zip", ZipLocator.class);
        town = app.getAssetManager().loadModel("main.scene");
        town.setLocalScale(0.5f);
        
        //rootNode.attachChild(model);
        rootNode.attachChild(town);
        
        RigidBodyControl floorPhy = new RigidBodyControl(0.0f);
        town.addControl(floorPhy);
        bulletAppState.getPhysicsSpace().add(floorPhy);

    }
    
    private void setUpLight() {
    // We add light so we see the scene
    AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.White.mult(1.3f));
    app.getRootNode().addLight(al);
 
    DirectionalLight dl = new DirectionalLight();
    dl.setColor(ColorRGBA.White);
    dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
    app.getRootNode().addLight(dl);
  }
 
  /** We over-write some navigational key mappings here, so we can
   * add physics-controlled walking and jumping: */
  private void setUpKeys() {
    app.getInputManager().addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
    app.getInputManager().addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
    app.getInputManager().addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
    app.getInputManager().addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
    app.getInputManager().addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
    app.getInputManager().addMapping("Fire", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    app.getInputManager().addMapping("Thrust", new KeyTrigger(KeyInput.KEY_RETURN));
    app.getInputManager().addMapping("Float", new KeyTrigger(KeyInput.KEY_F));

    app.getInputManager().addListener(this, "Left");
    app.getInputManager().addListener(this, "Right");
    app.getInputManager().addListener(this, "Up");
    app.getInputManager().addListener(this, "Down");
    app.getInputManager().addListener(this, "Jump");

    app.getInputManager().addListener(this, "Fire", "Thrust", "Float");
  }

    public void onAction(String name, boolean isPressed, float tpf) {
        if(name.equals("Fire") && isPressed){
            System.out.println("Fired");
            fire();
        }
        if(name.equals("Thrust") && isPressed){
            thruster = true;
            System.out.println("Thruster on");
        }else if(name.equals("Thrust") && !isPressed){
            thruster = false;
            System.out.println("Thruster off");
        }
        
        if(name.equals("Float") && isPressed){
            floating = !floating;
            System.out.println("Float on");
        }
    }
    
    private void fire(){
        Sphere sphere = new Sphere(12, 12, 0.2f);
        Geometry geom = new Geometry("Sphere"+(count), sphere);
        count++;
        
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        geom.setMaterial(mat);
        app.getRootNode().attachChild(geom);
        
        geom.setLocalTranslation(app.getCamera().getLocation());
        
        RigidBodyControl ballPhy = new RigidBodyControl(0.28f);
        geom.addControl(ballPhy);
        bulletAppState.getPhysicsSpace().add(geom);
        
        ballPhy.setLinearVelocity(app.getCamera().getDirection().mult(15.0f));
       
        
    }
    
    private void initShuttle(){
        Box box = new Box(0.5f, 0.5f, 0.5f);
        Geometry geom = new Geometry("Shuttle", box);
        shuttle = geom;
        
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
        app.getRootNode().attachChild(geom);
        
        geom.setLocalTranslation(-5.0f, 6.0f, 3.0f);
        
        RigidBodyControl ballPhy = new RigidBodyControl(1.0f);
        geom.addControl(ballPhy);
        bulletAppState.getPhysicsSpace().add(geom);
        
        ballPhy.setLinearVelocity(Vector3f.UNIT_Y.mult(5.0f));
        
        light.setColor(ColorRGBA.White);
        light.setPosition(new Vector3f(10.0f, 5.0f, 15.0f));
        geom.addLight(light);
        app.getRootNode().addLight(light);
  }

}
