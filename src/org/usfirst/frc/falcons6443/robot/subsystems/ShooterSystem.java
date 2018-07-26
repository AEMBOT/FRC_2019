package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.Encoders;
import org.usfirst.frc.falcons6443.robot.utilities.pid.PIDF;

public class ShooterSystem extends Subsystem {

    private Spark motor;
    private Encoders encoder;
    private PIDF pidf;

    private double[] chartX = new double[5]; //distance from target
    private double[] chartY = new double[5]; //speed needed
    private double[] defaultArray = {0, 10, 20, 30, 40};

    private double distance;
    private static final double p = 0;
    private static final double i = 0;
    private static final double d = 0;
    private static final double f = 0;
    private static final double epsilon = 0;

    public ShooterSystem(){
        motor = new Spark(RobotMap.ShooterMotor);
        encoder = new Encoders(RobotMap.ShooterEncoderA, RobotMap.ShooterEncoderB);
        pidf = new PIDF(p, i, d, f, epsilon);
        encoder.setReverseDirection(false);
        pidf.setFinishedRange(5); //update value
        pidf.setMaxOutput(1);
        pidf.setMinDoneCycles(5);
        SmartDashboard.putNumber("Distance to Target", 36);
        SmartDashboard.putNumberArray("Distance From Target", defaultArray);
        SmartDashboard.putNumberArray("Speed at Distance", defaultArray);
        SmartDashboard.putBoolean("Load", false);
    }

    @Override
    public void initDefaultCommand() { }

    public void charge(){
        //get distance to target (inches) from camera
        //(pulling value from ShuffleBoard until vision code done)
        distance = SmartDashboard.getNumber("Distance to Target", 36);
        //data tables
        chartX = SmartDashboard.getNumberArray("Distance From Target", defaultArray);
        chartY = SmartDashboard.getNumberArray("Speed at Distance", defaultArray);

        //calculate speed from distance (motion profiling? With linear interpolation?)
        //linear interpolation
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
            desiredSpeed = xy[1] + ((xy[3] - xy[1])/(xy[2] - xy[0])) * (distance - xy[0]); //linear interpolation equation
        }

        pidf.setDesiredValue(desiredSpeed);
        //velocity PID to get wheel up to speed (encoder)
        double power = pidf.calcPID(encoder.getRate());
        motor.set(power);

        //feed in ball when at speed (a green light [boolean] on ShuffleBoard to alert hand feeding)
        if (pidf.isDone()) SmartDashboard.putBoolean("Load", true);
        else SmartDashboard.putBoolean("Load", false);
    }

    //turns "Load" light off (mimics motor feeding on a button, logic wise)
    public void shoot(){
        SmartDashboard.putBoolean("Load", false);
    }

    public void off(){ motor.set(0); }
}