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
    public static final int FrontLeftMotor = 0;
    public static final int BackLeftMotor = 1;
    public static final int FrontRightMotor = 2;
    public static final int BackRightMotor = 3;

    //climber motors
    public static final int LeftClimbMotor = -1;
    public static final int RightClimbMotor = -1;

    //climber encoders
    public static final int LeftClimbEncoderA = -1;
    public static final int LeftClimbEncoderB = -1;

    //pistons
    public static final int LeftPiston = -1;
    public static final int RightPiston = -1;

    public static final int VacuumBallMotor = -1;
    public static final int VacuumHatchMotor = -1;
    public static final int VacuumArmMotor = -1;

    //drive train encoders
    //public static final int LeftEncoderA = -1;
    //public static final int LeftEncoderB = -1;
    //public static final int RightEncoderA = 8;
    //public static final int RightEncoderB = 9;

    //limit switches
    public static final int VacuumArmTopSwitch = -1;
    public static final int VacuumArmBottomSwitch = -1;

    //code settings
    public static final boolean Logger = false;

    //vacuum encoders
    public static final int VacuumArmEncoderA = -1;
    public static final int VacuumArmEncoderB = -1;
}