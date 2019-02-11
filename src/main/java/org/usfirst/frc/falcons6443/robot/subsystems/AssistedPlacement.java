package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.Ultrasonic;
import org.usfirst.frc.falcons6443.robot.hardware.vision.*;

/**
 * This class uses the limelight vision camera to help place hatches
 * 
 * @author Will Richards, Goirick Saha
 */
public class AssistedPlacement {

    //Assigns DIO pins for ultrasonic sensor
    private final int echoPin = 0;
    private final int trigPin = 1;

    //Creates a new WPILIB Ultrasonic reference
    private Ultrasonic ultrasonic;

    //Creates a ref to DriveTrainSystem
    DriveTrainSystem drive;

    // Creates limelight variables, create a limelight object, specify if is currently placing
    private boolean isPlacing = false;
    private Limelight lime;

    /**
     * Contructs the AssistedPlacement
     * @param drive Refernce to drive train in the parent class, to avoid a "Resource already allocated" error
     */
    public AssistedPlacement(DriveTrainSystem drive) {

        // Gives the previously created ultrasonic object values, and tells it to automatically collect data
        ultrasonic = new Ultrasonic(trigPin, echoPin);
        ultrasonic.setAutomaticMode(true);

        //Initilizes a reference to the limelight class and a refernce to the global DriveTrain
        lime = new Limelight();
        this.drive = drive;

        //Sets the limelight to driver mode at the start
        //lime.setCamMode(1.0);
    }

    /**
     *  This method is called when the 'A' button (will be changed) is pressed and does as follows, it will swap the camera mode from vision into tracking mode
     *  Then it will wait 250ms to allow enough time for the vision camera to begin tracking the new object
     */
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

    /**
     * Kill switch method, this method is called when the 'B' button (change) is pressed and will imediatly stop the track and return control to the driver
     */
    public void disablePlacing(){
        if(isPlacing == true){
            isPlacing = false;
            lime.swapCamera();
            Stop();
        }
    }

    /**
     * Will return true or false based on if the robot is attempting to place a hatch or not
     * @return value of isPlacing
     */
    public boolean getPlacing(){
        return isPlacing;
    }

    /**
     * Creates a runable refernce to the swap camera method in Limelight.java
     */
    public void swapCamera(){
        lime.swapCamera();
    }

    /**
     * This method mainly serves the purpose of testing however it will drive straight upon a button press until a specific distance is reached
     */
    public void runToDistance(){

        //Prints out distance that the ultrasonic sensor recieves in inches
        System.out.println(ultrasonic.getRangeInches());

        //Checks if the distance is greater than 32 inches (3 inch buffer, inertia)
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