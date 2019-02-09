package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.Ultrasonic;
import org.usfirst.frc.falcons6443.robot.hardware.vision.*;

/**
 * This class uses the limelight vision camera to help place hatches
 * 
 * @author Will Richards, Goirick Saha
 */
public class AssistedPlacement{

    private final int echoPin = 0;
    private final int trigPin = 1;

    private Ultrasonic ultrasonic; 

    DriveTrainSystem drive;

    //Variables for basic PID
    private Limelight lime;
    private double Kp = -0.1;
    private double minCommand = 0.05;
    private double stearingAdjust = 0.0; 
    private double right_command = 0.0;
    private double left_command = 0.0;


    public AssistedPlacement(DriveTrainSystem drive){
        ultrasonic = new Ultrasonic(trigPin, echoPin);
        ultrasonic.setAutomaticMode(true);
        lime = new Limelight();
        this.drive = drive;
    }

    //This method mainly serves the purpose of testing however it will drive straight upon a button press until a specific distance is reached
    public void runToDistance(){

        System.out.println(ultrasonic.getRangeInches());

        if(ultrasonic.getRangeInches() > 35.6){
            DriveForward(-0.5);
        }

        else if(ultrasonic.getRangeInches() < 35.6 && ultrasonic.getRangeInches() > 9.6){
            DriveForward(-0.1);
        }
        else{
            Stop();
        }
    }

   

    public void trackTargetPixy() {
        double x = lime.getX();
        double approxRange = 1.2; 
        double power = 0.25; //compensated for carpet

        //Prints out distance to target we will check and see when we need to switch to using the ultrasonic 
        System.out.println(calcDistance());

        //Order of ifs should be like this so it corrects first and then drives
        if(x > approxRange) {
            TurnLeft(power);
        } else if (x < approxRange) {
            TurnRight(power);
        }
        else if (x < approxRange && x > -approxRange) {
            DriveForward(power);
        }        
    }

    private double baseAngleRad, angle, baseAngleTan, distance;
    private double camHeight = 6.8; //temporary values
    private double targetHeight = 40; //temporary values
    private double camAngle = 30; //approximate camAngle

    public double calcDistance() {
        angle = lime.getY(); 
        baseAngleRad = Math.toRadians(camAngle + angle); // Convert total camera angle to radians
        baseAngleTan = Math.tan(baseAngleRad); // Take the tangent of total angle
        distance = (targetHeight - camHeight)/baseAngleTan; 
        return distance; 
    }

    private void TurnRight(double power){
        drive.rightMotors.set(-power); 
        drive.leftMotors.set(power);
    }

    private void TurnLeft(double power){
        drive.leftMotors.set(-power);
        drive.rightMotors.set(power);
    }

     //Template for driving forward
    private void DriveForward(double power){
        drive.leftMotors.set(power);
        drive.rightMotors.set(power);
    }

    //Sets motor power back to zero
    private void Stop(){
        drive.leftMotors.set(0);
        drive.rightMotors.set(0);
    }
}