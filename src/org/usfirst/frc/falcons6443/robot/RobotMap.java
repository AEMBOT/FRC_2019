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
    public static final int FrontLeftMotor = 4;
    public static final int BackLeftMotor = 5;
    public static final int FrontRightMotor = 2;
    public static final int BackRightMotor = 3;

    //drive train encoders
    public static final int LeftEncoderA = -1;
    public static final int LeftEncoderB = -1;
    public static final int RightEncoderA = 8;
    public static final int RightEncoderB = 9;

    //shooter motors
    public static final int ShooterMotor = 0;

    //shooter encoder
    public static final int ShooterEncoderA = 2;
    public static final int ShooterEncoderB = 3;

    //turret motor
    public static final int TurretMotor = 1;

    //turret sensors
    public static final int TurretEncoderA = 4;
    public static final int TurretEncoderB = 5;
    public static final int TurretLeftSwitch = 1;
    public static final int TurretRightSwitch = 0;

    //Intake accuators
    public static final int IntakePistonPort = 0;
    public static final int IntakeMotor = -1;

    //code settings
    public static final boolean Logger = false;
}