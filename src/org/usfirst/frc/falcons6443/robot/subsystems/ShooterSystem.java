package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.Encoders;
import org.usfirst.frc.falcons6443.robot.hardware.Pixy;
import org.usfirst.frc.falcons6443.robot.utilities.pid.PIDF;

public class ShooterSystem {

    private VictorSP motor;
    //    private Pixy pixy;
    public Encoders encoder;
    private PIDF pidf;
    private Preferences prefs;

    private boolean isManual = true;
    public boolean justShot;
    private boolean isCharging;
    private double[] chartX = {0, 10, 20, 30, 40}; //distance from target
    private double[] chartY = {0, 10, 20, 30, 40}; //speed needed

    private double ballCounter; //just for knowledge sake
    private double prevRev;
    private double prevTime;

    public ShooterSystem(){
        motor = new VictorSP(RobotMap.ShooterMotor);
//        pixy = Pixy.get();
        encoder = new Encoders(RobotMap.ShooterEncoderA, RobotMap.ShooterEncoderB);
        encoder.setDiameter(4);
        encoder.setTicksPerRev(1024);
        prefs = Preferences.getInstance();
        pidf = new PIDF(prefs.getDouble("Shooter P", 0), prefs.getDouble("Shooter I", 0),
                prefs.getDouble("Shooter D", 0), prefs.getDouble("Shooter F", 0),
                prefs.getDouble("Shooter Eps", 0));
//        encoder.setReverseDirection(false);
//        pidf.setFinishedRange(5); //update value
        pidf.setMaxOutput(0.75);
        pidf.setMinDoneCycles(5);
        ballCounter = 0;
        SmartDashboard.putNumberArray("Distance From Target", chartX);
        SmartDashboard.putNumberArray("Speed at Distance", chartY);
        SmartDashboard.putNumber("Balls Shot", ballCounter);
        SmartDashboard.putBoolean("Load", false); //true when the ball can be loaded
    }

    public boolean isCharged(){
        return pidf.isDone() && isCharging;
    }

    //used for auto
    public void readyToChargeAnotherBall(){
        justShot = false;
    }

    public void autoChargePeriodic(){
        if (isCharged()){
            shoot();
//        } else if(pixy.isObjLocked() && !justShot && SmartDashboard.getBoolean("Centered", false)){
            charge();
        } else {
            off();
        }
    }

    //returns rotations per minute
    //MAX: ~4100 revs/min
    public double getRate(){
        double time = (double) RobotController.getFPGATime() / 1000000.0;
        double rev = encoder.getRevs();
        double rate = (rev - prevRev) / (time - prevTime);
        prevTime = time;
        prevRev = rev;
        return rate * 60; //rotations per minute
    }

    public void tuningCharge(double desiredSpeed){
        //     pidf.setFinishedRange(); //update value

        pidf.setDesiredValue(desiredSpeed);

        double power = pidf.calcPID(getRate());
        motor.set(power);

        if (pidf.isDone()) System.out.println("PID DONE!!!!!");
    }

    public void charge() {
        //get distance to target (inches) from camera
        double distance = 40;//pixy.getDistanceToObject();
        //data tables
        chartX = SmartDashboard.getNumberArray("Distance From Target", chartX);
        chartY = SmartDashboard.getNumberArray("Speed at Distance", chartY);

        //calculate speed from distance
        //linear interpolation
        double[] xy = {-1, -1, -1, -1}; //{x1, y1, x2, y2}
        double desiredSpeed = -1;

        for (int i = 0; i < chartX.length; i++) {
            if (distance > chartX[i]) {
                if (i == chartX.length - 1) desiredSpeed = chartY[i];
                else if (distance < chartX[i + 1]) {
                    xy[0] = chartX[i];
                    xy[1] = chartY[i];
                    xy[2] = chartX[i + 1];
                    xy[3] = chartY[i + 1];
                }
            } else if (distance == chartX[i]) desiredSpeed = chartY[i];
        }

        if (xy[0] != -1 && desiredSpeed == -1) {
            desiredSpeed = xy[1] + ((xy[3] - xy[1]) / (xy[2] - xy[0])) * (distance - xy[0]); //linear interpolation equation
        }

        pidf.setDesiredValue(desiredSpeed);
        isCharging = true;
        //velocity PID to get wheel up to speed (encoder)
        double power = pidf.calcPID(getRate());
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
            justShot = false;
        }
    }

    public void off(){ motor.set(0); }

    public void manual(double power){
        if (Math.abs(power) > .1 ) {
            motor.set(power);
        }
        else motor.set(0);
    }

    public boolean getManual(){
        return isManual;
    }

    public void setManual(boolean set){
        isManual = set;
    }
}