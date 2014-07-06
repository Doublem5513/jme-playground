/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Matija
 */
public class TestAppState extends AbstractAppState {
    
    private SimpleApplication app;
    
    private Spatial model;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication)app;
        
        loadScene();
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
    }
    
    private void loadScene(){
        final Node rootNode = app.getRootNode();
        model  = app.getAssetManager().loadModel("Models/Building/Building.j3o");
        
        rootNode.attachChild(model);
    }
}
