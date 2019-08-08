package org.usfirst.frc.falcons6443.robot.autonomous;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.usfirst.frc.falcons6443.robot.hardware.NavX;
import org.usfirst.frc.falcons6443.robot.subsystems.DriveTrainSystem;

/**
 * Class used to load an follow paths created by the WaypointPlotter Program
 * WaypointPlotter: https://github.com/LibertyRobotics/WaypointPlotter
 * 
 * @author Will Richards
 */
public class Pathing {

    /**
     * Creates several different lists
     * X - Stores the X waypoint coords. distances from the left side of the field in inches
     * Y - Stores the Y waypoint coords. distances from the top of the field in inches
     * Distances - Stores the distance to the next coord.
     * Angles - Stores the angle that is needed to drive to the correct coord
     * hasTurned - Specific to each waypoint if the robot has turned to the new required angle
     * hasDriven - Specific to each waypoiny if the robot has driven to the next waypoint
     */
    List<Integer> X;
    List<Integer> Y;
    List<Double> Distances;
    List<Double> Angles;
    List<Boolean> hasTurned;
    List<Boolean> hasDriven;

    //Reference to the waypoint file
    File csv;

    //New scanner for reading the file
    Scanner file;

    //Reference to the NavX class for using gyro information
    NavX navx;


    /**
     * Calculates the number of ticks in one inch
     * Sudo Formula:
     * oneInch / ( (1 / encoderRes) * (1 / gearRatio) * ((wheelDiameter*Pi)/1) = ticksPerInch
     */
    final double TICKS_PER_INCH = 1 / ((1/42)*(1/14.7)*((8*Math.PI)/1));

    //Represents the waypoint the robot is currently trying to get to
    int waypointNumber;

    public Pathing(){

        //Create all the ArrayLists
        X = new ArrayList<>();
        Y = new ArrayList<>();
        Distances = new ArrayList<>();
        Angles = new ArrayList<>();
        hasTurned = new ArrayList<>();

        //Assign the file variable created above to an actual file in this case named Test.csv TODO: Create waypoints folder in user on roborio
        csv = new File("/home/lvuser/waypoints/Test.csv");

        //Attempt to assign the scanner created above to a read of the file, must be wrapped in try catch in case of exceptions
        try {
            file = new Scanner(csv);
        } catch (FileNotFoundException e) {
        }

        //Grab a static reference to the navX
        navx = NavX.get();

    }
    
    

    public void loadData() throws FileNotFoundException {
        
        //Create an int to keep track of the current line #
        int currentLine = 0;

        //If there is more lines in the file keep the read open
        while (file.hasNextLine()){

            //Read the next file in line and store it in an string called line
            String line = file.nextLine();

            //Skip the first line because the file is a csv and the first line is headings
            if(currentLine>0) {

                //Add appropriate values into list
                X.add(Integer.parseInt(line.split(",")[0]));
                Y.add(Integer.parseInt(line.split(",")[1]));
            }

            //Increment currentLine
            currentLine++;
        }

        try {

            //For each coord. X is used however they are all the same
            for (int i = 0; i < X.size(); i++) {

                //Assign a temp value for the X offset and Y offset for use in tan-1 = (opp. / adj. )
                double xDist = X.get(i) - X.get(i + 1);
                double yDist = Y.get(i) - Y.get(i + 1);

                //Calculate the angle using atan2 and the preset offset and then convert the answer to degrees
                double angle = Math.toDegrees(Math.atan2(Math.abs(yDist), Math.abs(xDist)));

                //Add the calculated angle to an array list which will match up with the coords.
                Angles.add(angle);

                //Calculates the distance between point A and point B using the distance formula
                double distance = Math.sqrt(Math.pow(Math.abs((X.get(i + 1) - X.get(i))), 2) + Math.pow(Math.abs(Y.get(i + 1) - Y.get(i)), 2));

                //Add distance to Distances list for waypoint
                Distances.add(distance);

                //Init the hasTurned list with all falses
                hasTurned.add(false);

                //Debugs for angle and distance
                //System.out.println("Angle: " + angle);
                //System.out.println("Distance: " + distance);
            }


        }
        catch (Exception e){
            //Catch and dispose of exception
        }
    }

    public void followPath(DriveTrainSystem drive){

        //Stop if on the last one to avoid IndexOutOfRange TODO: IF SKIPPING LAST WAYPOINT LOOK AT THIS
        if(waypointNumber != X.size()){

            //If the robot hasn't turned to the angle yet then keep trying
            if(!hasTurned.get(waypointNumber)){
                turnToAngle(drive);
            }

            //If it has drive to the next waypoint
            else{
                driveInches(drive);
            }
        }
        
    }

    /**
     * Called from within the followPath method that helps with turing to angles
     * @param drive Gets passed a reference to the drive train to avoid errors
     */
    private void turnToAngle(DriveTrainSystem drive){

        //Check if the navx yaw is less than whatever the waypoint angle is plus four but also greater than the waypoint angle minus 4 (temp. PID)
        if(navx.getYaw() > Angles.get(waypointNumber)-4 && navx.getYaw() < Angles.get(waypointNumber)+4){

            //If so stop and say that it has turned at the specific waypoint
            drive.arcadeDrive(0, 0);
            hasTurned.set(waypointNumber, true);
        }

        //However if the yaw is less than the waypoint -4 (meaning out of range) then turn to attempt to be within the acceptable range
        else if(navx.getYaw() < Angles.get(waypointNumber)-4){
            drive.arcadeDrive(0, -0.5);
        }

        //Repeat but with greater than and +4
        else if(navx.getYaw() > Angles.get(waypointNumber)+4){
            drive.arcadeDrive(0, 0.5);
        }
        
    }

    /**
     * Used to drive in straight lines from one coord to the next
     * @param drive once again passed a reference to the drive train
     */
    private void driveInches(DriveTrainSystem drive){

        //WHEN AT POSITION INCREMENT
        waypointNumber++;
    }

}