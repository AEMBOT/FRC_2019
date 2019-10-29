package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.*;

import java.util.ArrayList;
import java.util.List;
import com.revrobotics.*;
import org.usfirst.frc.falcons6443.robot.hardware.joysticks.Xbox;
import org.usfirst.frc.falcons6443.robot.utilities.enums.DriveStyles;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.InputMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

//import org.usfirst.frc.falcons6443.robot.utilities.Logger;


/**
 * Subsystem for the robot's drive train.
 * <p>
 * Contains 2 SpeedControllerGroups which are controlled by an instance of RobotDrive.
 * This class is meant to fix some of the shortcomings of the original DriveTrainSystem
 * class as well as make it more simple and readable.
 *
 * @author Christopher Medlin, Ivan Kenevich, Shivashriganesh Mahato
 */
public class DriveTrainSystem{

    SpeedControllerGroup leftMotors;
    SpeedControllerGroup rightMotors;

    private Encoders leftEncoder; // Encoders clicks per rotation = 49
    //private Encoders rightEncoder;
    private List<List<Integer>> encoderList = new ArrayList<List<Integer>>();
    public Timer encoderCheck;

    private boolean usingLeftEncoder = true; //keep true. Left is our default encoder, right is our backup encoder
    private double minEncoderMovement = 5; //ticks //change value
    private static final double WheelDiameter = 6;

    //Controls robot movement speed
    private double[] speedLevels = {4, 2, 1.3333 , 1};
    private int speedIndex = 3;
    private double currentLevel = speedLevels[speedIndex];
    private double moveSpeed;

    // A [nice] class in the wpilib that provides numerous driving capabilities.
    // Use it whenever you want your robot to move.
    private DifferentialDrive drive;

    /**
     * Constructor for DriveTrainSystem.
     */
    public DriveTrainSystem() {
        
        //2019 Seasson Comp Bot motors
        leftMotors = new SpeedControllerGroup(new CANSparkMax(RobotMap.FrontLeftMotor, MotorType.kBrushless), new CANSparkMax(RobotMap.BackLeftMotor, MotorType.kBrushless), new CANSparkMax(RobotMap.LeftCenterMotor,MotorType.kBrushless));
        rightMotors = new SpeedControllerGroup(new CANSparkMax(RobotMap.FrontRightMotor, MotorType.kBrushless), new CANSparkMax(RobotMap.BackRightMotor, MotorType.kBrushless), new CANSparkMax(RobotMap.RightCenterMotor, MotorType.kBrushless));

        //Creates a new differential drive using the previously defined motors
        drive = new DifferentialDrive(leftMotors, rightMotors);

        //Flips motor direction to run the left in the correct direction
        leftMotors.setInverted(true);

        // the driver station will complain for some reason if this isn't setSpeed so it's pretty necessary.
        // [FOR SCIENCE!]
        drive.setSafetyEnabled(false);
        drive.setMaxOutput(1);
        for(int i = 0; i <= 1; i++) encoderList.add(i, new ArrayList<>());
        encoderCheck = new Timer();
    }

    /**
     * Used to get a reference to the left side of the drive train from other classes
     * @return 'leftMotors' SpeedControllerGroup
     */
    public SpeedControllerGroup getLeftMotors(){
       return leftMotors;
    }

    /**
     * Used to get a reference to the right side of the motors
     * @return 'rightMotors' SpeedControllerGroup
     */
    public SpeedControllerGroup getRightMotors(){
        return rightMotors;
     }

    /**
     * Singular callable method to quickly change drive styles.
     *
     * @param controller controller reference used for power/rotation values
     * @param style DriveStyles enum, used to easily switch style
     * @param speedMultiplier will adjust the drive speed depending on weather or not demo mode is on
     */
    public void generalDrive(Xbox controller, DriveStyles style, double speedMultiplier){
        switch(style){
        
            //General tank drive, 2 Joysticks one for each side
            case Tank:
                tankDrive(controller.leftStickY() * speedMultiplier,controller.rightStickY() * speedMultiplier);
                break;

            //Arcade drive, 2 Joysticks, one for forward and reverse another for turning
            case Arcade:
                arcadeDrive(-controller.rightStickX() * speedMultiplier, controller.leftStickY() * speedMultiplier);
                break;

            case Curve:
                curvatureDrive(controller.leftStickY() / currentLevel,controller.rightStickX() / currentLevel, false);
                break;
            
            //RC 'Style' controls, 1 Joystick followed by the left trigger and right trigger.
            //Joystick turns and the triggers move forward and back
            case RC:
                rcDrive(controller.leftTrigger(), controller.rightTrigger(), controller.rightStickX());
                break;
            
            //The drive style selection default, is currently defautlting to arcade
            default:
                arcadeDrive(-controller.rightStickX() / currentLevel, controller.leftStickY() / currentLevel);
        }

    }

    /**
     * Allows for custom setting of motor power level.
     *
     * @param left  the power for the left motors.
     * @param right the power for the right motors.
     *
     * Implements the differentialDrive tankDrive into a local method
     */
    public void tankDrive(double left, double right) {
            drive.tankDrive(left, right);
    }

    /**
     * Allows separate control of forward movement and heading
     *
     * @param speed  the robots speed: forward is positive
     * @param rotation the rate of rotation: clockwise is positive
     *
     * Implements the differentialDrive arcadeDrive into a local method
     */
    public void arcadeDrive(double speed, double rotation){
        drive.arcadeDrive(speed,-rotation);
    }

    /**
     * Allows for RC car style drive
     * 
     * @param leftTrig Left trigger goes forward
     * @param rightTrig Right trigger goes backward
     * @param rotation Right stick X-axis
     * 
     * Implements arcadeDrive using RC controls
     */
    private void rcDrive(double leftTrig, double rightTrig, double rotation){
       
        //Makes sure some values are being sent over the 'leftTrig' variable 
        if(leftTrig > 0){
           
            //Asssign the trigger value to the new value of the 'moveSpeed' variable
            moveSpeed = leftTrig;
        }

        //Repeats the same process used for the left trigger
        else if(rightTrig > 0){
           moveSpeed = -rightTrig;
        }

        //If neither the left or right trigger are pushed set move speed to 0
        else{
           moveSpeed = 0;
        }

       //Finally applies the values to the motors
       drive.arcadeDrive(-rotation, moveSpeed);
    }

    /**
     * Allows separate control of forward movement and change in path curvature
     *
     * @param speed  the robots speed: forward is positive
     * @param rotation the rate of rotation: clockwise is positive
     *
     * Implements the differentialDrive curvatureDrive into a local method
     */
    private void curvatureDrive(double speed, double rotation, boolean isQuickTurn){
       drive.curvatureDrive(speed,rotation,isQuickTurn);
    }

    public boolean first; //set true in AutoPaths.WaitDrive()
    private int strikes; //how many times the encoder did not move enough in 1 second
    
    //In progress. Needs to be tested
    public double getDistanceSafe(){
        if(first){
            encoderCheck.reset();
            encoderCheck.start(); //stopped in AutoPaths.WaitDrive()
        }

        first = false;

        if(encoderCheck.get() > 1){ //if the function has been running for a second
            double first = encoderList.get(0).get(0);
            double last = encoderList.get(0).get(encoderList.size() - 1);
            encoderCheck.reset();

            for(int i = 0; i <= 1; i++) encoderList.get(i).clear();

            if(last - first < minEncoderMovement){ //if the encoder has not moved enough in a second increase strikes
                strikes++;
                if(strikes >= 3) usingLeftEncoder = false; //if 3 strikes use right encoder (the backup encoder)
            }
        }

        return getDistanceUnsafe();
    }

    /**
     * Used to return a constant of the unsafe distance for the encoder
     */
    public double getDistanceUnsafe(){
        return 10;
    }

    /**
     * The following method is used to act as a virtual speed shifter to shift between several different speed values
     */
    public void changeSpeed (boolean upOrDown){
        if(upOrDown && speedIndex < speedLevels.length){
            speedIndex += 1;
            currentLevel = speedLevels[speedIndex];
        }

        else if(!upOrDown && speedIndex > 0){
            speedIndex -= 1;
            currentLevel = speedLevels[speedIndex];
        }
        else {
            //redundant but might as well
            currentLevel = currentLevel;
        }
    }
}
