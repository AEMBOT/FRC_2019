package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.Ultrasonic;
import org.usfirst.frc.falcons6443.robot.hardware.vision.*;

/**
 * This class uses the limelight vision camera to help place hatches
 * 
 * @author Will Richards, Goirick Saha
 */
public class AssistedPlacement {

    private final int echoPin = 0;
    private final int trigPin = 1;

    private Ultrasonic ultrasonic;
    private boolean isPlacing = false;
    private boolean isInDriverMode = false;

    DriveTrainSystem drive;

    // Variables for basic PID
    private Limelight lime;
    private double Kp = -0.1;
    private double minCommand = 0.05;
    private double stearingAdjust = 0.0;
    private double right_command = 0.0;
    private double left_command = 0.0;

    public AssistedPlacement(DriveTrainSystem drive) {
        ultrasonic = new Ultrasonic(trigPin, echoPin);
        ultrasonic.setAutomaticMode(true);

        lime = new Limelight();
        this.drive = drive;
    }

    public void enablePlacing() {
        if (lime.getCamMode() == 1.0) {
            lime.swapCamera();
        }
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
        }
        isPlacing = true;
    }

    public void disablePlacing(){
        isPlacing = false;
    }

    public boolean getPlacing(){
        return isPlacing;
    }

    public void swapCamera(){
        lime.swapCamera();
        isInDriverMode = !isInDriverMode;
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
        double approxRange = 1.45; 
        double power = 0.15; //compensated for carpet

        if(lime.getValidTarget() > 0){
            isPlacing = true;
            //Order of ifs should be like this so it corrects first and then drives
            if (x < approxRange && x > -approxRange) {
                DriveForward(0.15);
            }   
            else if(x > approxRange && lime.getArea() > 1) {
                TurnLeft(power);
            } else if (x < approxRange && lime.getArea() > 1) {
                TurnRight(power);
            }
        }
        else{
            //Check if bot is within 17 inches
           if(ultrasonic.getRangeInches() < 20){
               //If so check if it is within 9 if so stop if not drive
               if(ultrasonic.getRangeInches() <= 12){
                    isPlacing = false;
                    Stop();
                    swapCamera();
               }
               else{
                DriveForward(0.15);
               }
           }

           //if it is not in view return control to the driver and swap vision mode
           else{
               isPlacing = false;
               Stop();
               swapCamera();
           }
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
        drive.leftMotors.set(-power);
        drive.rightMotors.set(-power);
    }

    //Sets motor power back to zero
    private void Stop(){
        drive.leftMotors.set(0);
        drive.rightMotors.set(0);
    }
}