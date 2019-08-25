package org.usfirst.frc.falcons6443.robot.hardware;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.SpeedController;

/**
 * SpeedControllerGroup serves as a wrapper to an array SpeedController objects, enabling
 * easy passing of methods to a group of SpeedControllers.
 *
 * @author Patrick Higgins
 */

public class SpeedControllerGroup implements SpeedController {
    private SpeedController[] controllers;

    /**
     * Constructor for SpeedControllerGroup.
     *
     * @param controllers any number of speed controllers.
     */
    public SpeedControllerGroup(SpeedController ... controllers) {
         this.controllers = controllers;
    }

    /**
     * Writes a power to the motors using PID
     */
    @Override
    public void pidWrite(double arg0) {
        for (SpeedController controller : controllers) {
            controller.pidWrite(arg0);
        }
    }

    /**
     * Disables all motors in group
     */
    @Override
    public void disable() {
        for (SpeedController controller : controllers) {
            controller.disable();
        }
    }

    /**
     * Returns the current power level of the motors
     */
    @Override
    public double get() {
        return controllers[0].get();
    }

    /**
     * Gets the direction status
     */
    @Override
    public boolean getInverted() {
        return controllers[0].getInverted();
    }

    /**
     * Sets motor speeds
     * @param arg0 The speed -1 to +1
     */
    @Override
    public void set(double arg0) {
        for (SpeedController controller : controllers) {
            controller.set(arg0);
        }
    }

    /**
     * Sets motor's directions
     * @param arg0 if the motors direction is inverted or not
     */

    @Override
    public void setInverted(boolean arg0) {
        for (SpeedController controller : controllers) {
            controller.setInverted(arg0);
        }
    }

    /**
     * Stops all motors
     */
    @Override
    public void stopMotor() {
        for (SpeedController controller : controllers) {
            controller.stopMotor();
        }
    }

    /**
     * Toggles whether the speed controllers in this group are inverted.
     */
    public void toggleInverted() {
        for (int i = 0; i < controllers.length; i++) {
            SpeedController controller = controllers[i];
            controller.setInverted(!controller.getInverted());
        }
    }

}
