package org.usfirst.frc.falcons6443.robot.autonomous.autoPathing;

/**
 * This static class is used to set constants for following a PathWeaver generated path
 * ALL MEASUREMENTS ARE IN METERS
 */
public class RobotPathConstants {

    //TODO: Find tick count for one revolution of the wheels, this will compensate for the gear ratio
    public static final int TICKS_PER_REV = 323;

    //Convert inches into meters
    public static final double WHEEL_DIAMETER = 8 / 39.37;

    //Measuremeant in m/s
    public static final double MAX_VELOCITY = 15/3.281;

    public static final double MAX_ACCEL = 2;

    public static final double MAX_JERK = 60;

    //Wheel base in meters
    public static final double WHEELBASE = 0.5461;
    
}