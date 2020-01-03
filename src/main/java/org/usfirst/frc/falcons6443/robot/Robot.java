/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.falcons6443.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.cscore.VideoMode;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.FileNotFoundException;

import org.usfirst.frc.falcons6443.robot.hardware.NavX;
import org.usfirst.frc.falcons6443.robot.hardware.joysticks.Xbox;
import org.usfirst.frc.falcons6443.robot.subsystems.*;
import org.usfirst.frc.falcons6443.robot.subsystems.ArmadilloClimber.ClimbEnum;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the Main.java file in the
 * project.
 */
// If you rename or move this class, update the Main.java file in the org folder
public class Robot extends TimedRobot {
    private Xbox primary;
    private Xbox secondary;

    private DriveTrainSystem driveTrain;

    private ArmadilloClimber climber;

    // Used to change speed for demo mode
    private double speedMultiplier = 1;

    /**
     * This function is run when the robot is first started up and should be used
     * for any initialization code.
     */
    @Override
    public void robotInit() {
        primary = new Xbox(new XboxController(0)); // change controller type here
        secondary = new Xbox(new XboxController(1));

        driveTrain = new DriveTrainSystem();

        climber = new ArmadilloClimber();

    }

    /*
     * Called when the robot first enters autonomous mode.
     */
    @Override
    public void autonomousInit() {
        climber.setClimb(ClimbEnum.Steady);

    }

    /**
     * This function is called periodically during autonomous.
     * For 2019 season put all sandstorm code in this block
     */
    @Override
    public void autonomousPeriodic() {
        // Just so it can stay above the ground
       climber.climb();
    }

    /*
     * Called when the robot first enter teleop mode.
     * This currently just swaps the camera to driver mode as opposed to vision processing
     */
    @Override
    public void teleopInit() {
       

    }

    /**
     * This function is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic() {
       
    }

    /*
     * Called when the robot first enters disabled mode.
     */
    @Override
    public void disabledInit(){
    
    }
    /*
     * Called periodically when the robot is in disabled mode.
     */
    @Override
    public void disabledPeriodic(){  }
}