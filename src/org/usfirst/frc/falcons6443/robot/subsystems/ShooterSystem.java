package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.Encoders;

public class ShooterSystem extends Subsystem {

    private Spark motor;
    private Encoders encoder;

    private double distance;

    public ShooterSystem(){
        motor = new Spark(RobotMap.ShooterMotor);
        encoder = new Encoders(RobotMap.ShooterEncoderA, RobotMap.ShooterEncoderB);
        encoder.setReverseDirection(false);
        SmartDashboard.putNumber("Distance to Target", 36);
    }

    @Override
    public void initDefaultCommand() { }

    public void setMotor(double power){ motor.set(power); }

    public void resetEncoder() {
        encoder.reset();
    }

    public double getEncoder(){
        return encoder.getDistance();
    }

    //periodic function
    public void update(){
        //get distance to target (inches) from camera
        //(pulling value from ShuffleBoard until vision code done)
        distance = SmartDashboard.getNumber("Distance to Target", 36);
        //calculate speed from distance (motion profiling? With linear interpolation?)
        //velocity PID to get wheel up to speed (encoder)
        //feed in ball when at speed (a green light on ShuffleBoard to alert hand feeding)
    }
}
