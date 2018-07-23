package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.Encoders;
import org.usfirst.frc.falcons6443.robot.utilities.PID;

public class ShooterSystem extends Subsystem {

    private Spark motor;
    private Encoders encoder;

    private double[] chartX = new double[5]; //distance from target
    private double[] chartY = new double[5]; //speed needed
    private double[] defaultArray = {0, 10, 20, 30, 40};

    private double distance;

    public ShooterSystem(){
        motor = new Spark(RobotMap.ShooterMotor);
        encoder = new Encoders(RobotMap.ShooterEncoderA, RobotMap.ShooterEncoderB);
        encoder.setReverseDirection(false);
        SmartDashboard.putNumber("Distance to Target", 36);
        SmartDashboard.putNumberArray("Chart X", defaultArray);
        SmartDashboard.putNumberArray("Chart Y", defaultArray);
    }

    @Override
    public void initDefaultCommand() { }

    public void setMotor(double power){ motor.set(power); }

    public void resetEncoder() {
        encoder.reset();
    }

    public double getEncoderTicks(){
        return encoder.getDistance();
    }

    //periodic function
    public void update(){
        //get distance to target (inches) from camera
        //(pulling value from ShuffleBoard until vision code done)
        distance = SmartDashboard.getNumber("Distance to Target", 36);

        //calculate speed from distance (motion profiling? With linear interpolation?)
        //linear interpolation
        chartX = SmartDashboard.getNumberArray("Chart X", defaultArray);
        chartY = SmartDashboard.getNumberArray("Chart Y", defaultArray);
        double[] xy = {-1, -1, -1, -1}; //{x1, y1, x2, y2}
        double desiredSpeed = -1;

        for(int i = 0; i < chartX.length; i++) {
            if (distance > chartX[i]) {
                if(i == chartX.length - 1) desiredSpeed = chartY[i];
                else if (distance < chartX[i + 1]) {
                    xy[0] = chartX[i];
                    xy[1] = chartY[i];
                    xy[2] = chartX[i + 1];
                    xy[3] = chartY[i + 1];
                }
            } else if (distance == chartX[i]) desiredSpeed = chartY[i];
        }

        if(xy[0] != -1 && desiredSpeed == -1){
            desiredSpeed = xy[1] + ((xy[3] - xy[1])/(xy[2] - xy[0])) * (distance - xy[0]);
        }
        //velocity PID to get wheel up to speed (encoder)

        //feed in ball when at speed (a green light [boolean] on ShuffleBoard to alert hand feeding)
    }
}
