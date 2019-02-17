package org.usfirst.frc.falcons6443.robot;

/**
 * RobotMap contains a multitude [Billions and Billions] of constants that define port numbers
 * of various hardware components.
 *
 * @author Christopher Medlin
 */
public class RobotMap {
    //any -1s are not being used and are yet to be specified

    //drive train motors
   public static final int FrontLeftMotor = 8;
   public static final int BackLeftMotor = 6;
   public static final int FrontRightMotor = 1;
   public static final int BackRightMotor = 3;
   public static final int RightCenterMotor = 2;
   public static final int LeftCenterMotor = 7;

    //drive train motors Practice Bot	    
    public static final int FrontLeftMotorPB = 0;	  
    public static final int BackLeftMotorPB = 1;	   
    public static final int FrontRightMotorPB = 2;	   
    public static final int BackRightMotorPB = 3;

    //climber motors
    public static final int LeftClimbMotor = 5;
    public static final int RightClimbMotor = 4;

    public static final int ClimbArmExtensionBeam= 0;
    public static final int ClimbArmBellySwitch = 2;

    //Ultrasonic sensor DIO connection pins
    public static final int UltrasonicEchoPin = 4;
    public static final int UltrasonicTrigPin = 3;
 
    //drive train encoders
    //public static final int LeftEncoderA = -1;
    //public static final int LeftEncoderB = -1;
    //public static final int RightEncoderA = -1;
    //public static final int RightEncoderB = -1;

    //limit switches
    public static final int VacuumArmTopSwitch = 9;
    public static final int VacuumArmBottomSwitch = -1;


    //code settings
    public static final boolean Logger = false;

    //vacuum encoders
    public static final int VacuumArmEncoderA = -1;
    public static final int VacuumArmEncoderB = -1;
    public static final int VacuumHatchMotor = 9;
    public static final int VacuumArmMotor = 11;
}