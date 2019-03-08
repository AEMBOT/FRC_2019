package org.usfirst.frc.falcons6443.robot.subsystems;

import org.usfirst.frc.falcons6443.robot.RobotMap;

import edu.wpi.first.wpilibj.Spark;

/**
 * Allows for changing of LED strip colors
 * @author Will Richards
 */
public class LEDSystem{

    private Spark ledController;

    public LEDSystem(){
        ledController = new Spark(RobotMap.LedController);
    }

    /**
     * Enables the default LED colors
     */
    public void enableDefault(){
        ledController.set(-0.95);
    }

    /**
     * Enables rainbow colors
     */
    public void enableRainbow(){
        ledController.set(-0.99);
    }

}