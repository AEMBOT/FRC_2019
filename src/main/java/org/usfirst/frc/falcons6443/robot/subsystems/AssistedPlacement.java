package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.Ultrasonic;

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

    public AssistedPlacement(DriveTrainSystem drive){
        ultrasonic = new Ultrasonic(trigPin, echoPin);
        ultrasonic.setAutomaticMode(true);

        this.drive = drive;
    }

    //This method mainly serves the purpose of testing however it will drive straight upon a button press until a specific distance is reached
    public void runToDistance(){
        if(ultrasonic.getRangeMM() > 229){
            DriveForward(0.5);
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
}