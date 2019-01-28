package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.drive.Vector2d;
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

   // private CANSparkMax motormax;
    private SpeedControllerGroup leftMotors;
    private SpeedControllerGroup rightMotors;
    //private CANSparkMax.IdleMode idleMode = CANSparkMax.IdleMode.kBrake;
    private Spark spark;

    private Encoders leftEncoder; // Encoders clicks per rotation = 850 (default in Encoders class)
    //private Encoders rightEncoder;
    private List<List<Integer>> encoderList = new ArrayList<List<Integer>>();
    public Timer encoderCheck;



    private boolean usingLeftEncoder = true; //keep true. Left is our default encoder, right is our backup encoder
    private double minEncoderMovement = 20; //ticks //change value
    private boolean reversed;
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

        //2019 Seasson Comp Bot
        leftMotors = new SpeedControllerGroup(new CANSparkMax(RobotMap.FrontLeftMotor, MotorType.kBrushless), new CANSparkMax(RobotMap.BackLeftMotor, MotorType.kBrushless));
        rightMotors = new SpeedControllerGroup(new CANSparkMax(RobotMap.FrontRightMotor, MotorType.kBrushless), new CANSparkMax(RobotMap.BackRightMotor, MotorType.kBrushless));

        //Practice Bot Drive
        //leftMotors = new SpeedControllerGroup(new VictorSP(RobotMap.FrontLeftMotor), new VictorSP(RobotMap.BackLeftMotor));
        //rightMotors = new SpeedControllerGroup(new VictorSP(RobotMap.FrontRightMotor), new VictorSP(RobotMap.BackRightMotor));
       
        drive = new DifferentialDrive(leftMotors, rightMotors);

        //Flips motor direction to run the left in the right direction
       leftMotors.setInverted(true);
        //leftEncoder = new Encoders(RobotMap.LeftEncoderA, RobotMap.LeftEncoderB);
       // rightEncoder = new Encoders(RobotMap.RightEncoderA, RobotMap.RightEncoderB);
        //leftEncoder.setTicksPerRev(850);
        //rightEncoder.setTicksPerRev(850);
        //leftEncoder.setDiameter(WheelDiameter);
        //rightEncoder.setDiameter(WheelDiameter);

        // the driver station will complain for some reason if this isn't setSpeed so it's pretty necessary.
        // [FOR SCIENCE!]
      
        drive.setSafetyEnabled(false);
        reversed = false;
        drive.setMaxOutput(1);
        for(int i = 0; i <= 1; i++) encoderList.add(i, new ArrayList<>());
        encoderCheck = new Timer();
        
    }

    /**
     * Singular callable method to quickly change drive styles.
     *
     * @param controller controller reference used for power/rotation values
     * @param style DriveStyles enum, used to easily switch styles
     *
     */
    public void generalDrive(Xbox controller, DriveStyles style){
        leftMotors.set(1);
        rightMotors.set(1);
        switch(style){

            case Tank:
                tankDrive(controller.leftStickY() / currentLevel,controller.rightStickY() / currentLevel);
                break;

            case Arcade:
                arcadeDrive(-controller.rightStickX() / currentLevel, controller.leftStickY() / currentLevel);
                break;

            case Curve:
                curvatureDrive(controller.leftStickY() / currentLevel,controller.rightStickX() / currentLevel, false);
                break;
            
            case RC:
                rcDrive(controller.leftTrigger(), controller.rightTrigger(), controller.rightStickX());
                break;
            
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
        if (reversed) {
            drive.tankDrive(-left, -right);
        } else {
            drive.tankDrive(left, right);
        }
//        Logger.log(LoggerSystems.Drive, "* {" + left + "}[" + right + "]" );
    }

    /**
     * Allows separate control of forward movement and heading
     *
     * @param speed  the robots speed: forward is positive
     * @param rotation the rate of rotation: clockwise is positive
     *
     * Implements the differentialDrive arcadeDrive into a local method
     */
    private void arcadeDrive(double speed, double rotation){
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
       if(leftTrig > 0){
           moveSpeed = leftTrig;
       }
       else if(rightTrig > 0){
           moveSpeed = -rightTrig;
       }
       else{
           moveSpeed = 0;
       }

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

    /**
     * Toggles the motors to go in reverse.
     */
    public void reverse() {
        reversed = !reversed;
    }

    /**
     * Gets the value of the revered var and returns it
     */
    public boolean isReversed() {
        return reversed;
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
        //Left encoder is encoderList.get(0). Right encoder is encoderList.get(1)
        //encoderList.get(0).add(leftEncoder.get());
        //encoderList.get(1).add(rightEncoder.get());

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

    public double getDistanceUnsafe(){
        //if(usingLeftEncoder) return leftEncoder.getDistanceWithDiameter();
        /*else*/ return 10;//rightEncoder.getDistanceWithDiameter();
    }

    public void reset(){
        //leftEncoder.reset();
        //rightEncoder.reset();
        //Logger.log(LoggerSystems.Drive, "reset drive encoders");
    }


    // param upOrDown" false = shift down, true = shift up. changes index of array to give max speed value
    public void changeSpeed (boolean upOrDown){
        if(upOrDown && speedIndex < 3){
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
/*
    public void changeIdle(){
        if(this.idleMode == IdleMode.kBrake) this.idleMode = IdleMode.kCoast;
        else if(this.idleMode == IdleMode.kCoast) this.idleMode = IdleMode.kBrake;
    }
*/
    /*
    public void falconDrive(double leftStickX, double rightTrigger, double leftTrigger) {
        Vector2d vector = new Vector2d(0,0);
        vector.x = 0;
        vector.y = 0;
        double differential;
        double power = 1;

        if (Math.abs(leftStickX) < .15) {
            differential = 0;
        } else {
            differential = Math.abs(leftStickX);
        }
        if(!shifted){ power = .7; }

        if (rightTrigger > 0) {//forward
            vector.x = rightTrigger*power+.1 - Math.pow(Math.E,-rightTrigger)*.5*differential*Math.signum(leftStickX)*-1;
            vector.y = rightTrigger*power+.1 - Math.pow(Math.E,-rightTrigger)*.5*differential*Math.signum(leftStickX);
            vector.x *= -1;
            vector.y *= -1;
        } else if (leftTrigger > 0) { //reverse
            vector.x = leftTrigger*power+.1 - Math.pow(Math.E,-leftTrigger)*.5*differential*Math.signum(leftStickX);
            vector.y = leftTrigger*power+.1 - Math.pow(Math.E,-leftTrigger)*.5*differential*Math.signum(leftStickX)*-1;
            //vector.x *= -1;
            //vector.y *= -1;ghtTrigger() * 1.2 * (primary.rightTrigger() * .7 + .44f) + (differential + .71 * primary.rightTrigger());//x is right
            //drive.y = primary.ri
        } else { //no trigger values, stationary rotation
            //  drive.x = primary.rightTrigger() * 1.2 * (primary.rightTrigger() * .7 + .44f) - (differential - .71 * primary.rightTrigger());//y is left
            // drive.x = 2*differential;
            //drive.y = -2*differential;
            if(Math.abs(leftStickX) > .2){
                vector.x = -leftStickX/1.28-(.1*Math.signum(leftStickX));
                vector.y = leftStickX/1.28+(.1*Math.signum(leftStickX));
            }
        }

        tankDrive(vector.y, vector.x);
    }
    */
}