package org.usfirst.frc.falcons6443.robot;

import edu.wpi.cscore.VideoMode;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.falcons6443.robot.commands.*;
import org.usfirst.frc.falcons6443.robot.commands.autocommands.AutoChooser;
import org.usfirst.frc.falcons6443.robot.communication.NetTables;
import org.usfirst.frc.falcons6443.robot.subsystems.*;
import org.usfirst.frc.falcons6443.robot.utilities.*;

/**
 * ROBOTS DON'T QUIT!
 * The Robot class is FRC team 6443's implementation of WPIlib's IterativeRobot class.
 *
 * @author Christopher Medlin
 */
public class Robot extends IterativeRobot {

    // All the subsystems that the robot possesses
    // If a new subsystem is added, it must also be added to SimpleCommand.
    // From there the subsystem can be referred to from any command that inherits SimpleCommand.
    public static final DriveTrainSystem DriveTrain = new DriveTrainSystem();
    public static final ShooterSystem Shooter = new ShooterSystem();
    public static final TurretSystem Turret = new TurretSystem();

    public static OI oi;

    private AutoChooser chooser;
    private Command autonomy;
    private Command teleop;

    public Stopwatch autoWatch;
    private Preferences prefs;

    //public Reader autoReader;
    /*
     * Called when the robot first starts.
     */
    @Override
    public void robotInit() {

        oi = new OI();
        autonomy = null;
        teleop = new TeleopMode();
        chooser = new AutoChooser();
        prefs = Preferences.getInstance();

        //CameraServer.getInstance().putVideo();
        NetTables.setBoolean("left", false);
        NetTables.setBoolean("center", false);
        NetTables.setBoolean("right", false);
        NetTables.flush();
        //format 1 is kMJPEG
        VideoMode vm = new VideoMode(1, 640, 480, 60);
        CameraServer.getInstance().startAutomaticCapture().setVideoMode(vm);

        SmartDashboard.putNumber("Drive P", prefs.getDouble("Drive P", 0));
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
    }

    /*
     * Called when the robot first enters disabled mode.
     */
    @Override
    public void disabledInit() {
        Logger.disabled();
        Scheduler.getInstance().removeAll();
    }

    /*
     * Called periodically when the robot is in disabled mode.
     */
    @Override
    public void disabledPeriodic() {
        Scheduler.getInstance().removeAll();
    }

    /*
     * Called when the robot first enters autonomous mode.
     */
    @Override
    public void autonomousInit() {
        Logger.autoInit();
        autoWatch = new Stopwatch(true);//begins timing
        autonomy = chooser.getFinalAuto();
        if (autonomy != null) autonomy.start();
    }

    /*
     * Called periodically when the robot is in autonomous mode.
     */
    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
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
        }
    }

    /*
     * Called when the robot first enter teleop mode.
     */
    @Override
    public void teleopInit() {
        Logger.teleopInit();
        if (autonomy != null) autonomy.cancel();
        if (teleop != null) teleop.start();
    }

    /*
     * Called periodically when the robot is in teleop mode.
     */
    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

    /*
     * Called periodically when the robot is in testing mode.
     */
    @Override
    public void testPeriodic() {
        LiveWindow.run();
    }
}