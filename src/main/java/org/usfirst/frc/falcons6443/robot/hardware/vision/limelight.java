package org.usfirst.frc.falcons6443.robot.hardware.vision;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Class created to manage limelight data for FRC 2019
 * 
 * @author Will Richards, Goirick Saha
 */

public class limelight{

    //Creates uninitilized variables to hold limelight table, x offset, y offset and object area
    private NetworkTable limelightTable;
    private NetworkTableEntry tx;
    private NetworkTableEntry ty;
    private NetworkTableEntry ta;


    public limelight(){
        limelightTable = NetworkTableInstance.getDefault().getTable("limelight");

        //Initilizes entries for limelight values
        tx = limelightTable.getEntry("tx");
        ty = limelightTable.getEntry("ty");
        ta = limelightTable.getEntry("ta");
    }

    //Will get the current X offset value if no object is detected it will default to 0
    public double getX(){
        return tx.getDouble(0.0);
    }

    //Will get the current Y offset value if no object is detected it will default to 0
    public double getY(){
        return ty.getDouble(0.0);
    }

    //Will get the current area value if no object is detected it will default to 0
    public double getArea(){
        return ta.getDouble(0.0);
    }

    //Turns on the LED so we can see the refelctive tape 
    public void turnOnLED(){
        limelightTable.getEntry("ledMode").setNumber(3);
    }

    //Turns off the LED so we dont blind everyone when we are not using it
    public void turnOffLED(){
        limelightTable.getEntry("ledMode").setNumber(1);
    }
}