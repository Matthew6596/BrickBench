package com.opengg.loader.components;

import com.opengg.core.Configuration;
import com.opengg.core.io.input.mouse.MouseController;
import com.opengg.core.math.*;
import com.opengg.core.world.Action;
import com.opengg.core.world.ActionType;
import com.opengg.core.world.Actionable;
import com.opengg.core.world.components.ActionTransmitterComponent;
import com.opengg.core.world.components.CameraComponent;
import com.opengg.core.world.components.ControlledComponent;
import com.opengg.core.world.components.WorldObject;

/**
 * The user view for the OpenGG instance.
 */
public class PlayerView extends ControlledComponent implements Actionable {

    private final Vector3fm control = new Vector3fm();
    private final Vector3fm rot = new Vector3fm();

    private float speed = 8;
    private boolean usingMouse = true;

    public PlayerView(){
        ActionTransmitterComponent actionTransmitter = new ActionTransmitterComponent();
        CameraComponent camera = new CameraComponent();
        WorldObject head = new WorldObject();

        attach(actionTransmitter);
        attach(head);
        head.attach(camera);
    }

    @Override
    public void update(float delta){
        if(usingMouse){
            Vector2f mouseDelta = MouseController.getDeltaPos().multiply(Configuration.getFloat("sensitivity"));
            rot.x += -mouseDelta.y;
            rot.y += -mouseDelta.x;

            if(Configuration.getBoolean("camera-lock"))
                rot.x = FastMath.clamp(rot.x, -90, 90);

            this.setRotationOffset(Quaternionf.createYXZ(new Vector3f(rot)));
        }

        Vector3f vel = this.getRotation().transform(new Vector3f(control).multiply(delta * speed));
        setPositionOffset(getPositionOffset().add(vel));
    }

    @Override
    public void onAction(Action action) {
        if(action.type == ActionType.PRESS){
            switch (action.name) {
                case "forward" -> control.z -= 1;
                case "backward" -> control.z += 1;
                case "left" -> control.x -= 1;
                case "right" -> control.x += 1;
                case "up" -> control.y += 1;
                case "down" -> control.y -= 1;
                case "fire" -> System.exit(0);
            }
        }else{
            switch (action.name) {
                case "forward" -> control.z += 1;
                case "backward" -> control.z -= 1;
                case "left" -> control.x += 1;
                case "right" -> control.x -= 1;
                case "up" -> control.y -= 1;
                case "down" -> control.y += 1;
            }
        }
    }

    public void dropInputs(){
        control.x = 0;
        control.y = 0;
        control.z = 0;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isUsingMouse() {
        return usingMouse;
    }

    public void setUsingMouse(boolean usingMouse) {
        this.usingMouse = usingMouse;
    }
}
