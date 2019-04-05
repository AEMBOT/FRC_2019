package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.Servo;
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
    public Servo servo; 
    public boolean isServoDown = false;
    public boolean isServoUp = false;

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
        //Initilizes a reference to the limelight class and a refernce to the global DriveTrain
        lime = new Limelight();
        servo = new Servo(RobotMap.LimelightServo);
        this.drive = drive;
        pid = new PID(.028,0,0,0);
        pid.setMaxOutput(1);
        pid.setMinDoneCycles(5);
        pid.setFinishedRange(1);
        pid.setDesiredValue(0);
    }

    /**
     *  This method is called when the 'A' button (will be changed) is pressed and does as follows, it will swap the camera mode from vision into tracking mode
     *  Then it will wait 300ms to allow enough time for the vision camera to begin tracking the new object
     */
    public void enablePlacing() {
    if (isPlacing == false) {
        if (lime.getCamMode() == 1.0) {
            lime.setCamMode(0);
            lime.turnOnLED();
                } try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
            }
                isPlacing = true;
        }
    }

    /**
     * Kill switch method, this method is called when the 'B' button (change) is pressed and will imediatly stop the track and return control to the driver
     */
    public void disablePlacing() {
        if (isPlacing == true) {
            isPlacing = false;
            lime.setCamMode(1);
            lime.turnOffLED();
            Stop();
        }
    }

    /**
     * This method toggles the tracking process
     */
    public void toggleTracking() {
        isPlacing = !isPlacing;
        if (isPlacing == false) {
            enablePlacing();
        } else {
            disablePlacing();
        }
    }

    /**
     * Will return true or false based on if the robot is attempting to place a hatch or not
     * @return value of isPlacing
     */
    public boolean getPlacing() {
        return isPlacing;
    }

    /**
     * Tracks the vision tape centers and drives forward to the center
     */
    public void trackTarget() {
        double x = lime.getX(); // Grabs the current degrees to the x value from the limelight class
        double approxRange = 1.9; // Acceptable range around the target, prevents oscilation
        double power = 0; // Power of the motors
        power = pid.calcPID(x);
        if(x<-1) {
            drive.arcadeDrive(power+0.16, 0);
        } else if(x>1) {
            drive.arcadeDrive(power-0.16, 0);
        } else if(lime.getValidTarget() > 0) {
            DriveForward(0.15);
        } else {
            drive.arcadeDrive(0, -0.05);
            isPlacing = false;
            enableDriverMode();
        }          
    }

    /**
     * Keeps the object vertically in view
     */
    public void trackServo() {
        double y = lime.getY();
        double value = 0.02;
        if(y <= 0) {
            servo.set(servo.get() + 0.04);
        } else if(y >= 0) {
            servo.set(servo.get() - 0.04); 
        }
    }

    /**
     * Gets the current limelight servo position
     * @return servo angle in degrees
     */
    public double getServoPosition() {
       return servo.getAngle();
    }

    /**
     * Method in place to allow for easy turning to the right 
     * @param power motor power level
     */
    private void TurnRight(double power) {
        drive.rightMotors.set(-power); 
        drive.leftMotors.set(power);
    }

    /**
     * Method in place to allow for easy turning to the left 
     * @param power motor power level
     */
    private void TurnLeft(double power) {
        drive.leftMotors.set(-power);
        drive.rightMotors.set(power);
    }

    /**
     * This method allows for driver control
     */
    public void enableDriverMode() {
        lime.turnOffLED();
        lime.setCamMode(1);
    }

     /**
     * This method disables driver control
     */
    public void disableDriverMode() {
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
     */
    private void Stop() {
        drive.leftMotors.set(0);
        drive.rightMotors.set(0);
    }
}
