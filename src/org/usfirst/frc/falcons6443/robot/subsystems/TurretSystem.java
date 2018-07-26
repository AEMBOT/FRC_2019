package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.Encoders;
import org.usfirst.frc.falcons6443.robot.hardware.LimitSwitch;
import org.usfirst.frc.falcons6443.robot.utilities.pid.PID;

public class TurretSystem extends Subsystem{

    private Spark motor;
    private Encoders encoder;
    private LimitSwitch leftSwitch;
    private LimitSwitch rightSwitch;
    private PID pid;

    private boolean isRoaming; //if true, search for target. If false, only lock on target if in view.
    private boolean movingLeft;
    private static final double totalDegrees = 180.0; //update value
    private static final double totalTicks = 425; //update value
    private static final double cameraDegrees = 15; //update value
    private static final double p = 0;
    private static final double i = 0;
    private static final double d = 0;
    private static final double epsilon = 0;

    public TurretSystem(){
        motor = new Spark(RobotMap.TurretMotor);
        encoder = new Encoders(RobotMap.TurretEncoderA, RobotMap.TurretEncoderB);
        leftSwitch = new LimitSwitch(RobotMap.TurretLeftSwitch);
        rightSwitch = new LimitSwitch(RobotMap.TurretRightSwitch);
        pid = new PID(p, i, d, epsilon);
        encoder.setReverseDirection(false);
        isRoaming = false;
        movingLeft = true;
        pid.setFinishedRange(5); //update value
        pid.setMaxOutput(1);
        pid.setMinDoneCycles(5);
        SmartDashboard.putBoolean("Centered", false);
        SmartDashboard.putBoolean("Roaming", false);
    }

    @Override
    public void initDefaultCommand() { }

    public void setMotor(double power){ motor.set(power); }

    public boolean getRoaming() { return isRoaming; }

    private void setRoaming(boolean roaming) {  isRoaming = roaming; }

    public void roamingToggle(){ isRoaming = !isRoaming; }

    public double getDegree(){
        return totalDegrees * encoder.get() / totalTicks;
    }

    //periodic function
    public void update(){
        double power;
        double targetDegree = 4; //get from vision, 0 being left side of screen
        boolean inView = true; //get from vision
        boolean lockedOn = false; //get from vision (can we get this? do we need this?)

        if(inView) {
            //moving turret to center target
            double desiredDegree;
            desiredDegree = getDegree() - (cameraDegrees / 2) + targetDegree;
            pid.setDesiredValue(desiredDegree);
            power = pid.calcPID(getDegree());
        } else if(isRoaming){
            if(movingLeft){
                power = -0.5; //negative is left, positive is right
            } else {
                power = 0.5;
            }
            SmartDashboard.putBoolean("Roaming", true);
        } else {
            power = 0;
            SmartDashboard.putBoolean("Roaming", false);
        }

        //if centered, power = 0 and inform drivers (ShuffleBoard boolean) and shooter(?)
        if(pid.isDone() && lockedOn) {
            power = 0;
            SmartDashboard.putBoolean("Centered", true);
        } else {
            SmartDashboard.putBoolean("Centered", false);
        }

        if(leftSwitch.get()) {
            power = Math.abs(power);
            encoder.reset();
        } else if(rightSwitch.get()) {
            power = -Math.abs(power);
        }

        movingLeft = !(power > 0);

        motor.set(power);
    }
}
