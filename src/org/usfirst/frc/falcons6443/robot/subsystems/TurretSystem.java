package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.Encoders;
import org.usfirst.frc.falcons6443.robot.hardware.LimitSwitch;
import org.usfirst.frc.falcons6443.robot.utilities.pid.PID;

public class TurretSystem extends Subsystem {

    private Spark motor;
    private Encoders encoder;
    private LimitSwitch leftLimitSwitch;
    private LimitSwitch rightLimitSwitch;
    private PID pid;

    private boolean movingLeft;
    private boolean isDisabled;
    private static final int totalTicks = 425; //update value
    private static final double totalDegrees = 180.0; //update value
    private static final double p = 0;
    private static final double i = 0;
    private static final double d = 0;
    private static final double epsilon = 0;
    private static final double buffer = 0.5; //update value; use from pixy

    public TurretSystem() {
        motor = new Spark(RobotMap.TurretMotor);
        encoder = new Encoders(RobotMap.TurretEncoderA, RobotMap.TurretEncoderB);
        leftLimitSwitch = new LimitSwitch(RobotMap.TurretLeftSwitch);
        rightLimitSwitch = new LimitSwitch(RobotMap.TurretRightSwitch);
        pid = new PID(p, i, d, epsilon);
        encoder.setReverseDirection(false);
        movingLeft = true;
        isDisabled = false;
        pid.setFinishedRange(buffer);
        pid.setMaxOutput(1);
        pid.setMinDoneCycles(5);
        SmartDashboard.putBoolean("Centered", false);
        SmartDashboard.putBoolean("Roaming", false);
    }

    @Override
    public void initDefaultCommand() {
    }

    public double getDegree() {
        return totalDegrees * encoder.get() / totalTicks;
    }

    public void disable() { isDisabled = true; }

    public void roaming() {
        double power;
        if (movingLeft) {
            power = -0.5; //negative is left, positive is right
        } else {
            power = 0.5;
        }

        if (leftLimitSwitch.get()) {
            power = Math.abs(power);
            encoder.reset();
        } else if (rightLimitSwitch.get()) {
            power = -Math.abs(power);
            encoder.set(totalTicks);
        }
    }

    public void move(double targetDegree) { //negative is left, positive is right
        double power;
        double desiredDegree = getDegree() + targetDegree;
        pid.setDesiredValue(desiredDegree);
        power = pid.calcPID(getDegree());

        if (power != 0) movingLeft = !(power > 0);
    }
}



/*    public void update(){
        double power;
        double targetDegree = 4; //get from vision, 0 being center, left side of screen negative, right positive
        boolean inView = true; //get from vision

        if(inView && !isDisabled) {
            //moving turret to center of target
            double desiredDegree = getDegree() + targetDegree;
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
//        if(pid.isDone() && Math.abs(targetDegree) < buffer) {
//            power = 0;
//            SmartDashboard.putBoolean("Centered", true);
//        } else {
//            SmartDashboard.putBoolean("Centered", false);
//        }

        if(leftLimitSwitch.get()) {
            power = Math.abs(power);
            encoder.reset();
        } else if(rightLimitSwitch.get()) {
            power = -Math.abs(power);
            encoder.set(totalTicks);
        }

        if(power != 0) movingLeft = !(power > 0);

        motor.set(power);
    }*/
