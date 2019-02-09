package org.usfirst.frc.falcons6443.robot.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/*
 * The main auto class holding the logic to select the auto path and helps pass instances of
 * subsystems and autoDrive to the autoPaths class.
 *
 * Use runAutoPath() in Robot.java, function AutonomousInit()
 */
public class AutoMain {

    private AutoPaths autoPaths;
    private static SendableChooser<Position> sendable;

    public AutoMain(AutoDrive autoDrive) {
        autoPaths = new AutoPaths(autoDrive);
        printAutoSelection();
    }

    //enums for auto selection off the dashboard
    public enum Position {
        LEFT, CENTER, RIGHT, DEFAULT
    }

    //sets up the dashboard for auto path choices
    //Test and see where these should be created!!
    private void printAutoSelection() {
        SmartDashboard.putNumber("speed", 0);
    }

    //runs the auto path selected in the dashboard
    public void runAutoPath() {
        autoPaths.driveForTime(1, SmartDashboard.getNumber("speed", 0));
    }
}