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

    //Template for driving forward
    private void DriveForward(double speed){
        drive.leftMotors.set(-speed);
        drive.rightMotors.set(-speed);
    }

    //Sets motor power back to zero
    private void Stop(){
        drive.leftMotors.set(0);
        drive.rightMotors.set(0);
    }

    public void trackTargetPixy() {
        double x = lime.getX();
        double approxRange = 1.5; 
        
        if (x < approxRange && x > -approxRange) {
            drive.leftMotors.set(0);
            drive.rightMotors.set(0); 
        }
        else if(x > approxRange) {
            drive.leftMotors.set(-0.15);
            drive.rightMotors.set(0.15);

        } else if (x < approxRange) {
            drive.rightMotors.set(-0.15); 
            drive.leftMotors.set(0.15);
        }
    }

    //All variables for the PID method are here, temporary just for testing
    int P,I,D = 0;
    int integral, previous_error, targetAngle = 0;
    double error, rcw ,derivative = 0;

    public void trackTargetPID(){
        double x = lime.getX();
        P = 1; //Tune this first
        
        //sets the margin equal to the targetValue - theCurrentAngle
        error = targetAngle - x;
        
        integral += (error*0.2); // Integral is increased by the error*time (which is .02 seconds using normal IterativeRobot)

        derivative = (error - previous_error) / 0.2;
        rcw = P*error + I+integral + D*derivative;
        
        drive.arcadeDrivePID(0, rcw);
    }

}