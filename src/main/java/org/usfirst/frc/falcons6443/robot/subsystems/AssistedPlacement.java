package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.Ultrasonic;

import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.vision.*;
import org.usfirst.frc.falcons6443.robot.utilities.pid.PID;

/**
 * This class uses the limelight vision camera to help place hatches
 * 
 * @author Will Richards, Goirick Saha
 */
public class AssistedPlacement {

    private PID pid;

    //Creates a new WPILIB Ultrasonic reference
    private Ultrasonic ultrasonic;

    //Creates a ref to DriveTrainSystem
    DriveTrainSystem drive;

    // Creates limelight variables, create a limelight object, specify if is currently placing
    public boolean isPlacing = false;
    private Limelight lime;

    /**
     * Contructs the AssistedPlacement
     * @param drive Refernce to drive train in the parent class, to avoid a "Resource already allocated" error
     */
    public AssistedPlacement(DriveTrainSystem drive) {

        // Gives the previously created ultrasonic object values, and tells it to automatically collect data
        ultrasonic = new Ultrasonic(RobotMap.UltrasonicEchoPin, RobotMap.UltrasonicTrigPin);
        ultrasonic.setAutomaticMode(true);

        //Initilizes a reference to the limelight class and a refernce to the global DriveTrain
        lime = new Limelight();
        this.drive = drive;

        pid = new PID(.03,0,0,0);
        pid.setMaxOutput(1);
        pid.setMinDoneCycles(5);
        pid.setFinishedRange(1);
        pid.setDesiredValue(0);

        //Sets the limelight to driver mode at the start
        //lime.setCamMode(1.0);
    }

    /**
     *  This method is called when the 'A' button (will be changed) is pressed and does as follows, it will swap the camera mode from vision into tracking mode
     *  Then it will wait 300ms to allow enough time for the vision camera to begin tracking the new object
     */
    public void enablePlacing() {
        if(isPlacing == false){
            if (lime.getCamMode() == 1.0) {
            lime.setCamMode(0);
            lime.turnOnLED();
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
            }
            isPlacing = true;
        }
    }

    /**
     * Kill switch method, this method is called when the 'B' button (change) is pressed and will imediatly stop the track and return control to the driver
     */
    public void disablePlacing(){
        if(isPlacing == true){
            isPlacing = false;
            lime.setCamMode(1);
            lime.turnOffLED();
            Stop();
        }
    }

    public void toggleTracking(){
        isPlacing = !isPlacing;
        if(isPlacing == false)
            enablePlacing();
        else
            disablePlacing();

    }

    /**
     * Will return true or false based on if the robot is attempting to place a hatch or not
     * @return value of isPlacing
     */
    public boolean getPlacing(){
        return isPlacing;
    }

    /**
     * This method mainly serves the purpose of testing however it will drive straight upon a button press until a specific distance is reached
     */
    public void runToDistance(){

        //Prints out distance that the ultrasonic sensor recieves in inches
        System.out.println(ultrasonic.getRangeInches());

        //Checks if the distance is greater than 32 inches (3 inch buffer, inertia)
        if(ultrasonic.getRangeInches() > 35.6){
            DriveForward(-0.2);
        }

        //If the ultrasonic sensor returns a value in between 32 inches and 9 inches slow down for final alignment 
        else if(ultrasonic.getRangeInches() < 35.6){
            DriveForward(-0.07);
        }
        
        //If not in this range stop
        else{
            Stop();
            enableDriverMode();
        }
    }

    /**
     * Tracks the vision tape centers and drives forward to the center
     */
    public void trackTarget() {
        double x = lime.getX(); // Grabs the current degrees to the x value from the limelight class
        double approxRange = 1.9; // Acceptable range around the target, prevents oscilation
        double power = 0; // Power of the motors

        

        power = pid.calcPID(x);
        if(x<-1.3)
            drive.arcadeDrive(power+0.16, 0);
        else if(x>1.3)
            drive.arcadeDrive(power-0.16, 0);
        else if(lime.getValidTarget() > 0){
            DriveForward(0.15);
        }
        else{
            Stop();
            isPlacing = false;
            enableDriverMode();
        }


        //Checks if the limelight can see the target
        // if(lime.getValidTarget() > 0){
        //     isPlacing = true; // Sets the is placing variable to true thus locking out normal drivercontrol, kill switch still works
            
        //     //Order of ifs should be like this so it drives first and then corrects 
        //     if (x < approxRange && x > -approxRange) {
        //         DriveForward(0.15);
        //     }   
            
        //     //Makes sure area is greater than one to limit random small objects being tracked
        //     else if(x > approxRange) {
        //         TurnLeft(power);
        //     } else if (x < approxRange) {
        //         TurnRight(power);
        //     }
        // }
        // else{

        //     //Check if bot is within 19 inches
        //    if(ultrasonic.getRangeInches() < 40){

        //        //If so check if it is within 9 if so stop if not drive
        //        if(ultrasonic.getRangeInches() <= 14){
        //             isPlacing = false;
        //             Stop();
        //             enableDriverMode();
        //        }
        //        else{

        //             //If it less than 30 then continue to drive
        //            if(ultrasonic.getRangeInches() < 30){
        //                 DriveForward(0.15);
        //            }
        //        }
        //    }

        //    //if it is not in view return control to the driver and swap vision mode
        //    else{
        //        isPlacing = false;
        //        Stop();
        //        enableDriverMode();
        //    }
        // }
          
    }

    
    private double baseAngleRad, angle, baseAngleTan, distance;
    private double camHeight = 6.8; //temporary values
    private double targetHeight = 40; //temporary values
    private double camAngle = 30; //approximate camAngle

    /**
     * Not fully functional method that is meant to calculate distance to an objec, doesnt work quite right
     * @return distance to tracked object
     */
    public double calcDistance() {
        angle = lime.getY(); 
        baseAngleRad = Math.toRadians(camAngle + angle); // Convert total camera angle to radians
        baseAngleTan = Math.tan(baseAngleRad); // Take the tangent of total angle
        distance = (targetHeight - camHeight)/baseAngleTan; 
        return distance; 
    }

    /**
     * Method in place to allow for easy turning to the right 
     * @param power motor power level
     */
    private void TurnRight(double power){
        drive.rightMotors.set(-power); 
        drive.leftMotors.set(power);
    }

    /**
     * Method in place to allow for easy turning to the left 
     * @param power motor power level
     */
    private void TurnLeft(double power){
        drive.leftMotors.set(-power);
        drive.rightMotors.set(power);
    }

    public void enableDriverMode(){
        lime.turnOffLED();
        lime.setCamMode(1);
    }

    public void disableDriverMode(){
        lime.turnOnLED();
        lime.setCamMode(0);
    }


    /**
     * Method in place to allow for easy driving straight
     * @param power motor power level
     */
    private void DriveForward(double power){
        drive.leftMotors.set(-power);
        drive.rightMotors.set(-power);
    }

    /**
     * Method in place to allow for easy stopping of the motors 
     * @param power motor power level
     */
    private void Stop(){
        drive.leftMotors.set(0);
        drive.rightMotors.set(0);
    }
}