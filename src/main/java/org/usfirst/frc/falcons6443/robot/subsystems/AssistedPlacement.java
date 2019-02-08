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
    private double Kp = -0.0;
    private double minCommand = 0.05;
    private double headingError = -lime.getX();
    private double stearingAdjust = 0; 
    private double right_command = 0;
    private double left_command = 0;

    public AssistedPlacement(DriveTrainSystem drive){
        ultrasonic = new Ultrasonic(trigPin, echoPin);
        ultrasonic.setAutomaticMode(true);

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

    //Keeps target in the center of camera vision using proportional values
    public void trackTarget() {
        if(lime.getX() > 1.0) {
            stearingAdjust = Kp*headingError - minCommand; 
        }
        else if (lime.getX() < 1.0) {
            stearingAdjust = Kp*headingError + minCommand; 
        }

        left_command += stearingAdjust;
        right_command -= stearingAdjust; 

        drive.leftMotors.set(left_command);
        drive.rightMotors.set(right_command); 
    }

}