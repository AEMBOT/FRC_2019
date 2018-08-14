package org.usfirst.frc.falcons6443.robot.commands.subcommands;

import edu.wpi.first.wpilibj.Preferences;
import org.usfirst.frc.falcons6443.robot.commands.SimpleCommand;
import org.usfirst.frc.falcons6443.robot.hardware.NavX;
import org.usfirst.frc.falcons6443.robot.utilities.pid.PID;

/**
 * Command to rotate the robot to an angle specified in a constructor parameter.
 *
 * @author Christopher Medlin, Ivan Kenevich
 */
public class RotateToAngleSad extends SimpleCommand {
    private PID pid;
    private NavX navX;
    private Preferences prefs;

//    private static final double P = 0.1; //.3
  //  private static final double I = 0;
 //   private static final double D = .2; //1.23
 //   private static final double Eps = 0.68; //.44 //weakest applied power //try upping more???

    private static final double buffer = 4; //degrees
    private double targetAngle;

    public RotateToAngleSad(double angle) {
        super("Rotate To Angle Beta");
        requires(driveTrain);
        navX = NavX.get();
        prefs = Preferences.getInstance();
        pid = new PID(prefs.getDouble("Turn P", 0), prefs.getDouble("Turn I", 0),
                prefs.getDouble("Turn D", 0), prefs.getDouble("Turn Eps", 0));
        pid.setMaxOutput(.7);
        pid.setMinDoneCycles(5);
        pid.setFinishedRange(buffer);
        if (angle > 180){
            angle -= 360;
        } else if (angle == 180){
            angle = 179.99;
        }
        targetAngle = angle;
    }

    private void turnToAngle(){
        double power = pid.calcPID(navX.getYaw());
        driveTrain.tankDrive(power, -power );
    }

    private void setAngle(){
        pid.setDesiredValue(targetAngle);
    }
    private boolean isAtAngle(){
        return pid.isDone();
    }

    @Override
    public void initialize() {
        navX.reset();
    }

    @Override
    public void execute() {
        setAngle();
        turnToAngle();
        if(isAtAngle()){
            driveTrain.tankDrive(0, 0);
        }
    }

    @Override
    public boolean isFinished() {
        return isAtAngle();
    }
}
