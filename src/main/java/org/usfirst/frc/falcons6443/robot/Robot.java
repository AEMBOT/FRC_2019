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
import org.usfirst.frc.falcons6443.robot.autonomous.AutoDrive;
import org.usfirst.frc.falcons6443.robot.autonomous.AutoMain;
import org.usfirst.frc.falcons6443.robot.hardware.joysticks.Xbox;
import org.usfirst.frc.falcons6443.robot.subsystems.*;
import org.usfirst.frc.falcons6443.robot.utilities.TeleopStructure;
import org.usfirst.frc.falcons6443.robot.utilities.Logger;
import org.usfirst.frc.falcons6443.robot.utilities.enums.DriveStyles;
import org.usfirst.frc.falcons6443.robot.utilities.enums.XboxRumble;


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
    //private ArmadilloClimberTest climber;
    private VacuumSystem vacuum;
    private DriveStyles controlMethod;
    private ArmadilloClimber climber;

    public static Preferences prefs;
    private SendableChooser<DriveStyles> driveStyle;
    public static boolean isKillSwitchEnabled = false;


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
        climber = new ArmadilloClimber();
       vacuum = new VacuumSystem();

//        autoDrive = new AutoDrive();
  //      autoMain = new AutoMain(autoDrive);
        //CameraServer.getInstance().putVideo();
        //format 1 is kMJPEG
        //VideoMode vm = new VideoMode(1, 640, 480, 60);
        //CameraServer.getInstance().startAutomaticCapture().setVideoMode(vm);
        driveTrain = new DriveTrainSystem();
        
        driveStyle = new SendableChooser<DriveStyles>();
        driveStyle.addOption("Tank", DriveStyles.Tank);
        driveStyle.addOption("Arcade", DriveStyles.Arcade);
        driveStyle.addOption("RC", DriveStyles.RC);
        driveStyle.addOption("Curve", DriveStyles.Curve);
        driveStyle.setDefaultOption("Arcade", DriveStyles.Arcade);

        SmartDashboard.putData("driveStyle", driveStyle);


        //autoDrive = new AutoDrive();
        //autoMain = new AutoMain(autoDrive);
        //CameraServer.getInstance().putVideo();
        //format 1 is kMJPEG
        VideoMode vm = new VideoMode(1, 640, 480, 60);
       // CameraServer.getInstance().startAutomaticCapture().setVideoMode(vm);

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
        controlMethod = (DriveStyles) driveStyle.getSelected();

        if(!isKillSwitchEnabled){
        //Drive controlled by Left and Right joysticks
        driveTrain.generalDrive(primary, controlMethod);

        //Drive Shifting
        teleop.runOncePerPress(primary.leftBumper(), () -> driveTrain.changeSpeed(false), false);
        teleop.runOncePerPress(primary.rightBumper(), () -> driveTrain.changeSpeed(true), false);

        //climb control
        teleop.runOncePerPress(primary.B() && secondary.dPadRight(), () -> climber.climb(), false);
        teleop.runOncePerPress(primary.Y(), () -> climber.enableKillSwitch(), false);

        // Arm control
        teleop.runOncePerPress(secondary.A(), () -> vacuum.moveArmDown(), false);
        teleop.runOncePerPress(secondary.B(), () -> vacuum.moveArmUp(), false);
        teleop.runOncePerPress(secondary.Y(), () -> vacuum.moveArmBack(), false);

        // Vacumm control
        teleop.press(secondary.rightBumper(), () -> vacuum.activateHatchSuction());
        teleop.press(secondary.leftBumper(), () -> vacuum.activateBallSuction());

        //Alignment Controls (primary - A) (secondary - triggers)
        //__teleop.runOncePerPress(primary.A(), () -> TBDFUNCTION, false);
        //__teleop.runOncePerPress(secondary.rightTrigger(), () -> TBDFUNCTION, false);
    
        } else{

        //manual climber control
        climber.manualControl(primary.rightStickY());

        //control reset
        teleop.runOncePerPress(primary.seven(), () -> isKillSwitchEnabled = false, false);    

        }

       
        teleop.periodicEnd();
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