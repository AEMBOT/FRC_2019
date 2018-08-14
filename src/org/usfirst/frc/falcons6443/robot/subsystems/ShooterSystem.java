package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.Encoders;
import org.usfirst.frc.falcons6443.robot.hardware.Pixy;
import org.usfirst.frc.falcons6443.robot.utilities.pid.PIDF;

public class ShooterSystem extends Subsystem {

    private Spark motor;
    private Pixy pixy;
    private Encoders encoder;
    private PIDF pidf;
    private Preferences prefs;

    private double[] chartX = {0, 10, 20, 30, 40}; //distance from target
    private double[] chartY = {0, 10, 20, 30, 40}; //speed needed
    private double[] defaultArray = {0, 10, 20, 30, 40};

    private double ballCounter;

    public ShooterSystem(){
        motor = new Spark(RobotMap.ShooterMotor);
        pixy = Pixy.get();
        encoder = new Encoders(RobotMap.ShooterEncoderA, RobotMap.ShooterEncoderB);
        prefs = Preferences.getInstance();
        pidf = new PIDF(prefs.getDouble("Shooter P", 0), prefs.getDouble("Shooter I", 0),
                prefs.getDouble("Shooter D", 0), prefs.getDouble("Shooter F", 0),
                prefs.getDouble("Shooter Eps", 0));
        encoder.setReverseDirection(false);
        pidf.setFinishedRange(5); //update value
        pidf.setMaxOutput(1);
        pidf.setMinDoneCycles(5);
        ballCounter = 0;
        SmartDashboard.putNumberArray("Distance From Target", chartX);
        SmartDashboard.putNumberArray("Speed at Distance", chartY);
        SmartDashboard.putNumber("Balls Shot", ballCounter);
        SmartDashboard.putBoolean("Load", false);
    }

    @Override
    public void initDefaultCommand() { }

    public boolean isCharged(){
        return pidf.isDone();
    }

    public void autoUpdate(){
        if (SmartDashboard.getBoolean("Load", false)){
            shoot();
        } else if(pixy.isObjLocked()){
            charge();
        } else {
            off();
        }
    }

    public void charge(){
        //get distance to target (inches) from camera
        double distance = pixy.getDistanceToObject();
        //data tables
        chartX = SmartDashboard.getNumberArray("Distance From Target", chartX);
        chartY = SmartDashboard.getNumberArray("Speed at Distance", chartY);

        //calculate speed from distance
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
        if(SmartDashboard.getBoolean("Load", false)){
            ballCounter ++;
            SmartDashboard.putBoolean("Load", false);
            SmartDashboard.putNumber("Balls Shot", ballCounter);
        }
    }

    public void off(){ motor.set(0); }
}