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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.falcons6443.robot.autonomous.AutoDrive;
import org.usfirst.frc.falcons6443.robot.autonomous.AutoMain;
import org.usfirst.frc.falcons6443.robot.hardware.joysticks.Xbox;
import org.usfirst.frc.falcons6443.robot.subsystems.*;
import org.usfirst.frc.falcons6443.robot.utilities.TeleopStructure;
import org.usfirst.frc.falcons6443.robot.utilities.Logger;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
// If you rename or move this class, update the build.properties file in the project root
public class Robot extends TimedRobot {
    private Xbox primary;
    private Xbox secondary;
    private TeleopStructure teleop;
    private DriveTrainSystem driveTrain;
    private AutoDrive autoDrive;
    private AutoMain autoMain;
    private ArmadilloClimber climber;
    private VacuumSystem vacuum;

    boolean toggleOn = false;
    boolean togglePressed = false;

    public static Preferences prefs;


    private boolean babyMode = false;
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    @Override
    public void robotInit()
    {
        primary = new Xbox(new XboxController(0)); //change controller type here
        secondary = new Xbox(new XboxController(1));
        teleop = new TeleopStructure();
      //  driveTrain = new DriveTrainSystem();

        autoDrive = new AutoDrive();
        autoMain = new AutoMain(autoDrive);
        //CameraServer.getInstance().putVideo();
        //format 1 is kMJPEG
        //VideoMode vm = new VideoMode(1, 640, 480, 60);
        //CameraServer.getInstance().startAutomaticCapture().setVideoMode(vm);

        SmartDashboard.putBoolean("Baby Mode", babyMode);

    }

    /*
     * Called when the robot first enters autonomous mode.
            */
    @Override
    public void autonomousInit()
    {
        Logger.autoInit();
        autoMain.runAutoPath();
    }

    /**
     * This function is called periodically during autonomous.
     */
    @Override
    public void autonomousPeriodic() {  //what...? where should this go??

         }

    /*
     * Called when the robot first enter teleop mode.
     */
    @Override
    public void teleopInit(){
        Logger.teleopInit();
    }
    /**
     * This function is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic()
    {
        updateToggle(); //checks the toggled button

        //Drive train usage
        //driveTrain.falconDrive(primary.leftStickX(), primary.leftTrigger(), primary.rightTrigger());
        driveTrain.tankDrive(primary.leftStickY(), primary.rightStickY()); //TODO: TEST this cause profiles are cool

        //Run the method that allows the robot to begin its climb
        teleop.press(primary.A(), () -> climber.enableClimb());

        //Will stop the climb no matter where it is if pressed (E-Stop)
        teleop.press(primary.B(), () -> climber.enableKillSwitch());

        //Will only fire the method when the button is in the full up position, thus toggling the arm up
        //teleop.runOncePerPress(primary.X(), () -> toggleX(), true);

        //if the toggle button is on, the vacuum will move the arm up and check if its vertical.
        if(toggleOn){
           vacuum.moveArmUp();
        }

        //Will only run if A has been pressed
        climber.climb();

        //general periodic functions
        teleop.periodicEnd();

    }

    /**
     *  This function checks if a button is toggled. One button is allowed to be toggled at a time.
     */
    public void updateToggle() {
        if (primary.X()) {
            if (!togglePressed) {
                toggleOn = !toggleOn;
                togglePressed = true;
            }
        }
        else {
            togglePressed = false;
        }
    }

    /**
     *  This is a simplified version of Goirick's toggle function, Left yours as is for now
     */
    public void toggleX(){
        toggleOn = !toggleOn;
    }

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic() {  LiveWindow.run();  }

    /*
     * Called when the robot first enters disabled mode.
     */
    @Override
    public void disabledInit(){
        try{
            Logger.printSpace();
        } catch (Exception e){
            System.out.println("Failed to print storage");
        }
        Logger.disabled();
    }
    /*
     * Called periodically when the robot is in disabled mode.
     */
    @Override
    public void disabledPeriodic(){    }
}