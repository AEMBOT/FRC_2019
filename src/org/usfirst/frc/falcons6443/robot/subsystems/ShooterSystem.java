package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.Encoders;

public class ShooterSystem extends Subsystem {

    private Spark motor;
    private Encoders encoder;

    public ShooterSystem(){
        motor = new Spark(RobotMap.ShooterMotor);
        encoder = new Encoders(RobotMap.ShooterEncoderA, RobotMap.ShooterEncoderB);
        encoder.setReverseDirection(false);
    }

    @Override
    public void initDefaultCommand() { }

    public void setMotor(double power){
        motor.set(power);
    }

    public void resetEncoder() {
        encoder.reset();
    }

    public double getEncoder(){
        return encoder.getDistance();
    }

    public void periodic(){
        //stuffs
    }
}
