package org.usfirst.frc.falcons6443.robot.commands;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.falcons6443.robot.Robot;
import org.usfirst.frc.falcons6443.robot.hardware.Joysticks.Xbox;
import org.usfirst.frc.falcons6443.robot.utilities.enums.*;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * Teleoperated mode for the robot.
 * The execute method of this class handles all possible inputs from the driver during the game.
 */
public class TeleopMode extends SimpleCommand {

    private int unpressedID = 0;
    private int numOfSubsystems = 2;

    private Xbox primary;           //Drive and flywheel/output
    private Xbox secondary;         //Secondary functions
    private boolean[] unpressed = new boolean[numOfSubsystems];
    private boolean[] isManualLessThanBuffer = new boolean[numOfSubsystems];
    private List<Callable<Boolean>> isManualGetter = new ArrayList<>(); //add control manual getters
    private List<Consumer<Boolean>> isManualSetter = new ArrayList<>(); //add control manual setters
    //private WCDProfile driveProfile;//Profile used to calculate robot drive power

    public TeleopMode() {
        super("Teleop Command");
        requires(driveTrain);
        requires(shooter);
        requires(turret);
    }

    @Override
    public void initialize() {
        primary = Robot.oi.getXbox(true);
        secondary = Robot.oi.getXbox(false);
        //driveProfile = new FalconDrive(primary);

        //add manual getters and setters using isManualGetter and isManualSetter
        //.add(Subsystems.subsystemEnum.ordinal(),() -> function() or (Boolean set) -> function(set))
        while(isManualGetter.size() < numOfSubsystems) isManualGetter.add(null);
        while(isManualSetter.size() < numOfSubsystems) isManualSetter.add(null);

        SmartDashboard.putNumber("Number", 1);
    }

    @Override
    public void execute() {

        //drive
        driveTrain.falconDrive(primary.leftStickX(), primary.leftTrigger(), primary.rightTrigger());
        // driveTrain.tankDrive(driveProfile.calculate()); TODO: TEST this cause profiles are cool

        //shooter
        press(primary.leftBumper(), () -> shooter.charge());
        unpressed(primary.rightBumper(), () -> shooter.shoot(), true); //resets the dashboard Load boolean

        //off
        off(() -> shooter.off(), primary.leftBumper());

        //turret
        unpressed(primary.eight(), () -> turret.disable(), false);
        unpressed(primary.Y(), () -> turret.roamingToggle(), false);

        //general periodic functions
        turret.update();
        periodicEnd();

        //other junk
        if(shooter.isCharged()) primary.setRumble(XboxRumble.RumbleBoth, 0.4);
    }

    //Pairs an action with a button
    private void press(boolean button, Runnable action){
        if(button) action.run();
    }

    //Pairs an action with a button, compatible with manual()
    // ie: this function can be used with manual() to control the same component
    // eg: button control and (backup) manual control of the same component
    private void press(Consumer<Boolean> setManual, boolean button, Runnable action){
        if(button) {
            setManual.accept(false);
            action.run();
        }
    }

    //Pairs an action with a manual input (joystick, trigger, etc)
    private void manual(Subsystems manualNumber, double input, Runnable action){
        if(Math.abs(input) > 0.2){
            isManualSetter.get(manualNumber.ordinal()).accept(true);
            isManualLessThanBuffer[manualNumber.ordinal()] = false;
            action.run();
        } else {
            isManualLessThanBuffer[manualNumber.ordinal()] = true;
        }
    }

    //Runs an action when a set of buttons is not pressed
    private void off(Runnable off, boolean ... button){
        if(areAllFalse(button)) off.run();
    }

    //Runs an action when manual is less than buffer
    private void off(Runnable off, Subsystems manualNumber) {
        if(isManualLessThanBuffer[manualNumber.ordinal()]) off.run();
    }

    //Runs an action when a set of buttons is not pressed and manual is less than buffer
    private void off(Runnable off, Subsystems manualNumber, boolean ... button){
        try {
            if(areAllFalse(button) && !isManualGetter.get(manualNumber.ordinal()).call()) off.run();
            else if((areAllFalse(button) && isManualGetter.get(manualNumber.ordinal()).call()
                    && isManualLessThanBuffer[manualNumber.ordinal()])) off.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Pairs an action with a button, activated only once unpressed (true) or once pressed (false)
    private void unpressed(boolean button, Runnable function, boolean unpressedMode){
        if(button){
            if(!unpressedMode && !unpressed[unpressedID]){
                function.run();
            }
            unpressed[unpressedID] = true;
        } else {
            if(unpressedMode && unpressed[unpressedID]){
                function.run();
            }
            unpressed[unpressedID] = false;
        }
        unpressedID++;
    }

    //clears the unpressedID
    private void periodicEnd(){
        unpressedID = 0;
    }

    private boolean areAllFalse(boolean[] array) {
        for(boolean b : array) if(b) return false;
        return true;
    }

    private static boolean areAllZero(double buffer, double[] array) {
        for(double d : array) if(d > buffer) return false;
        return true;
    }

    public void reset(){
        //driveProfile = null;
    }

    public boolean isFinished() {
        return false;
    }
}