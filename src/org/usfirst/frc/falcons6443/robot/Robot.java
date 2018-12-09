/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.falcons6443.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.cscore.VideoMode;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.falcons6443.robot.autonomous.AutoDrive;
import org.usfirst.frc.falcons6443.robot.autonomous.AutoMain;
import org.usfirst.frc.falcons6443.robot.hardware.joysticks.Xbox;
import org.usfirst.frc.falcons6443.robot.subsystems.*;
import org.usfirst.frc.falcons6443.robot.utilities.TeleopStructure;
import org.usfirst.frc.falcons6443.robot.utilities.Logger;
import org.usfirst.frc.falcons6443.robot.utilities.enums.XboxRumble;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
// If you rename or move this class, update the build.properties file in the project root
public class Robot extends IterativeRobot {
    private Xbox primary;
    private Xbox secondary;
    private TeleopStructure teleop;
    private DriveTrainSystem driveTrain;
    private TurretSystem turret;
    private ShooterSystem shooter;
    private IntakeSystem intake;
    private AutoDrive autoDrive;
    private AutoMain autoMain;
    private Preferences prefs;


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
        turret = new TurretSystem();
        shooter = new ShooterSystem();
        intake = new IntakeSystem();
        autoDrive = new AutoDrive();
        autoMain = new AutoMain(autoDrive, turret, shooter);
        //CameraServer.getInstance().putVideo();
        //format 1 is kMJPEG
        VideoMode vm = new VideoMode(1, 640, 480, 60);
        CameraServer.getInstance().startAutomaticCapture().setVideoMode(vm);

        SmartDashboard.putBoolean("Baby Mode", babyMode);

        /*      SmartDashboard.putNumber("Drive P", prefs.getDouble("Drive P", 0));
        SmartDashboard.putNumber("Drive I", prefs.getDouble("Drive I", 0));
        SmartDashboard.putNumber("Drive D", prefs.getDouble("Drive D", 0));
        SmartDashboard.putNumber("Drive Eps", prefs.getDouble("Drive Eps", 0));
        SmartDashboard.putNumber("Turn P", prefs.getDouble("Turn P", 0));
        SmartDashboard.putNumber("Turn I", prefs.getDouble("Turn I", 0));
        SmartDashboard.putNumber("Turn D", prefs.getDouble("Turn D", 0));
        SmartDashboard.putNumber("Turn Eps", prefs.getDouble("Turn Eps", 0));
        SmartDashboard.putNumber("Turret P", prefs.getDouble("Turret P", 0));
        SmartDashboard.putNumber("Turret I", prefs.getDouble("Turret I", 0));
        SmartDashboard.putNumber("Turret D", prefs.getDouble("Turret D", 0));
        SmartDashboard.putNumber("Turret Eps", prefs.getDouble("Turret Eps", 0));
        SmartDashboard.putNumber("Shooter P", prefs.getDouble("Shooter P", 0));
        SmartDashboard.putNumber("Shooter I", prefs.getDouble("Shooter I", 0));
        SmartDashboard.putNumber("Shooter D", prefs.getDouble("Shooter D", 0));
        SmartDashboard.putNumber("Shooter F", prefs.getDouble("Shooter F", 0));
        SmartDashboard.putNumber("Shooter Eps", prefs.getDouble("Shooter Eps", 0));
        SmartDashboard.putBoolean("Save Prefs", false);
*/
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
        if(SmartDashboard.getBoolean("Save Prefs", false)){
            prefs.putDouble("Drive P", SmartDashboard.getNumber("Drive P", 0));
            prefs.putDouble("Drive I", SmartDashboard.getNumber("Drive I", 0));
            prefs.putDouble("Drive D", SmartDashboard.getNumber("Drive D", 0));
            prefs.putDouble("Drive Esp", SmartDashboard.getNumber("Drive Esp", 0));
            prefs.putDouble("Turn P", SmartDashboard.getNumber("Turn P", 0));
            prefs.putDouble("Turn I", SmartDashboard.getNumber("Turn I", 0));
            prefs.putDouble("Turn D", SmartDashboard.getNumber("Turn D", 0));
            prefs.putDouble("Turn Eps", SmartDashboard.getNumber("Turn Eps", 0));
            prefs.putDouble("Turret P", SmartDashboard.getNumber("Turret P", 0));
            prefs.putDouble("Turret I", SmartDashboard.getNumber("Turret I", 0));
            prefs.putDouble("Turret D", SmartDashboard.getNumber("Turret D", 0));
            prefs.putDouble("Turret Eps", SmartDashboard.getNumber("Turret Eps", 0));
            prefs.putDouble("Shooter P", SmartDashboard.getNumber("Shooter P", 0));
            prefs.putDouble("Shooter I", SmartDashboard.getNumber("Shooter I", 0));
            prefs.putDouble("Shooter D", SmartDashboard.getNumber("Shooter D", 0));
            prefs.putDouble("Shooter F", SmartDashboard.getNumber("Shooter F", 0));
            prefs.putDouble("Shooter Eps", SmartDashboard.getNumber("Shooter Eps", 0));
        } }

    /*
     * Called when the robot first enter teleop mode.
     */
    @Override
    public void teleopInit(){
        Logger.teleopInit();
        teleop.addIsManualGetterSetter(TeleopStructure.ManualControls.Shooter, () -> shooter.getManual(), (Boolean set) ->  shooter.setManual(set));
        teleop.addIsManualGetterSetter(TeleopStructure.ManualControls.Turret, () -> turret.getManual(), (Boolean set) ->  turret.setManual(set));
        teleop.addIsManualGetterSetter(TeleopStructure.ManualControls.Intake, () -> intake.getManual(), (Boolean set) -> intake.setManual(set));
        teleop.addIsManualGetterSetter(TeleopStructure.ManualControls.Turret, () -> turret.getManual(), (Boolean set) ->  turret.setManual(set));
    }
    /**
     * This function is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic()
    {
        //drive
      //  driveTrain.falconDrive(primary.leftStickX(), primary.leftTrigger(), primary.rightTrigger());
        // driveTrain.tankDrive(driveProfile.calculate()); TODO: TEST this cause profiles are cool

        //shooter
   //     teleop.press(primary.leftBumper(), () -> shooter.charge());
   //     teleop.runOncePerPress(primary.rightBumper(), () -> shooter.shoot(), true); //resets the dashboard Load boolean
        teleop.manual(TeleopStructure.ManualControls.Shooter, primary.leftStickY(), (Double power) -> shooter.manual(power));

        //turret
   //     teleop.runOncePerPress(primary.eight(), () -> turret.off(), false);
   //     teleop.runOncePerPress(primary.X(), () -> turret.roamingToggle(), false);
        teleop.manual(TeleopStructure.ManualControls.Turret, primary.rightStickY(), (Double power) -> turret.manual(power));

        //intake
        teleop.press(primary.A(), () -> intake.movePistonIn());
        teleop.press(primary.B(), () -> intake.movePistonOut());
        teleop.press(primary.Y(), () -> intake.startCompressor());
        teleop.manual(TeleopStructure.ManualControls.Intake, primary.leftTrigger(), (Double power) -> intake.manual(power));
        teleop.manual(TeleopStructure.ManualControls.Intake, primary.rightTrigger(), (Double power) -> intake.manual(power));

        //off
        teleop.off(() -> intake.stopCompressor(), primary.Y());
        teleop.off(() -> shooter.off(), TeleopStructure.ManualControls.Shooter, primary.leftBumper());
        teleop.off(() -> turret.off(), TeleopStructure.ManualControls.Turret, primary.eight(), primary.X());
        teleop.off(() -> intake.off(), TeleopStructure.ManualControls.Intake);

        //general periodic functions
    //    turret.update(!primary.seven());
        teleop.periodicEnd();
        shooter.printRate();

        //other junk
        if(shooter.isCharged()) primary.setRumble(XboxRumble.RumbleBoth, 0.4);
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