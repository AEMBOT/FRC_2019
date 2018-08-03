package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.Encoders;
import org.usfirst.frc.falcons6443.robot.hardware.LimitSwitch;
import org.usfirst.frc.falcons6443.robot.hardware.Pixy;
import org.usfirst.frc.falcons6443.robot.utilities.pid.PID;

public class TurretSystem extends Subsystem {

    private Spark motor;
    private Pixy pixy;
    private Encoders encoder;
    private LimitSwitch leftLimitSwitch;
    private LimitSwitch rightLimitSwitch;
    private PID pid;

    private boolean movingLeft;
    private boolean isDisabled;
    private boolean isRoaming;
    private double roamingPower = 0.5;
    private static final int totalTicks = 425; //update value
    private static final double totalDegrees = 180.0; //update value
    private static final double p = 0;
    private static final double i = 0;
    private static final double d = 0;
    private static final double epsilon = 0;

    public TurretSystem() {
        motor = new Spark(RobotMap.TurretMotor);
        pixy = Pixy.get();
        encoder = new Encoders(RobotMap.TurretEncoderA, RobotMap.TurretEncoderB);
        leftLimitSwitch = new LimitSwitch(RobotMap.TurretLeftSwitch);
        rightLimitSwitch = new LimitSwitch(RobotMap.TurretRightSwitch);
        pid = new PID(p, i, d, epsilon);
        encoder.setReverseDirection(false);
        movingLeft = true;
        isDisabled = false;
        isRoaming = true;
        pid.setFinishedRange(pixy.getBuffer());
        pid.setMaxOutput(1);
        pid.setMinDoneCycles(5);
        SmartDashboard.putBoolean("Centered", false);
        SmartDashboard.putBoolean("Roaming", false);
    }

    @Override
    public void initDefaultCommand() {
    }

    private double getDegree() { return encoder.get() * totalDegrees / totalTicks; }

    public void disable() { isDisabled = true; }

    public void roamingToggle(){ isRoaming = !isRoaming; }

    public void update(){
        double power;
        double targetDegree = pixy.getAngleToObject(); //get from vision, 0 being center, left negative, right positive

        if(pixy.isTargetInView()) { //moving turret to center of target
            double desiredDegree = getDegree() + targetDegree;
            pid.setDesiredValue(desiredDegree);
            power = pid.calcPID(getDegree());
        } else if(isRoaming){
            if(movingLeft){
                power = -roamingPower; //negative is left, positive is right
            } else {
                power = roamingPower;
            }
            SmartDashboard.putBoolean("Roaming", true);
        } else {
            power = 0;
            SmartDashboard.putBoolean("Roaming", false);
        }

        //if centered, power = 0 and inform drivers (ShuffleBoard boolean) and shooter(?)
        if(pid.isDone() && pixy.isObjLocked()) {
            power = 0;
            SmartDashboard.putBoolean("Centered", true);
        } else {
            SmartDashboard.putBoolean("Centered", false);
        }

        if(leftLimitSwitch.get()) {
            power = Math.abs(power);
            encoder.reset();
        } else if(rightLimitSwitch.get()) {
            power = -Math.abs(power);
            encoder.set(totalTicks);
        }

        if(power != 0) movingLeft = power < 0;

        if(!isDisabled) motor.set(power);
    }
}

    /*public void update() {
        if(!pixy.isTargetInView()){
            roaming();
        } else if (!pixy.isObjLocked()){
            SmartDashboard.putBoolean("Centered", false);
            pixy.lockOnObject((Double set) -> move(set));
        } else {
            SmartDashboard.putBoolean("Centered", true);
        }
    }

    public void move(double targetDegree) { //negative is left, positive is right
        double power;
        double desiredDegree = getDegree() + targetDegree;
        pid.setDesiredValue(desiredDegree);
        power = pid.calcPID(getDegree());

        if (power != 0) movingLeft = !(power > 0);

        if(!isDisabled) motor.set(power);
    }*/