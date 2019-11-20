package org.usfirst.frc.falcons6443.robot.autonomous;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.usfirst.frc.falcons6443.robot.hardware.NavX;
import org.usfirst.frc.falcons6443.robot.subsystems.DriveTrainSystem;

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

    int waypointNumber;
    private double encoderOffset = 0;

    /**
     * Constructs all variables used in conjunction with pathing
     * @param waypointFilePath the path at which the waypoints file is stored
     */
    public Pathing(String waypointFilePath){

        /**
         * Creates 5 different ArrayLists
         * All values are converted to inches when waypoints are savede
         * X - Values of the X coordinates 
         * Y - Values of the Y coordinates
         * Distances - The distances between the waypoints
         * Angles - The angle to face the next waypoint
         * hasTurned - Holds a bool for each waypoint in the list reguarding if it has turned to face the new angle
         */
        X = new ArrayList<>();
        Y = new ArrayList<>();
        Distances = new ArrayList<>();
        Angles = new ArrayList<>();
        hasTurned = new ArrayList<>();

        //Creates a new File variable that points to the waypoints list file
        csv = new File(waypointFilePath);

        //Trys to read the file via Scanner
        try {
            file = new Scanner(csv);
        } catch (FileNotFoundException e) {
        }

        //Grab a static reference to the navX (avoids resource already allocated)
        navx = NavX.get();

    }
    
    
    /**
     * This method is called to load and parse the waypoint file
     * @throws FileNotFoundException If the file it was attempting to load was not found it throws an exception
     */
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
                hasTurned.add(false);

                //Init the hasTurned list with all falses
                hasTurned.add(false);

            }


        }
        catch (Exception e){
            //Catch and dispose of exception
        }
    }

    /**
     * This method is used to 'reset' the encoder, it just assigns a variable to the current encoder value and when encoder values are gotten it subtracts this offset from the actual 
     * @param drive reference to the drive train
     */
    public void resetEncoder(DriveTrainSystem drive){
        encoderOffset = drive.getAverageEncoderPosition();
    }

    /**
     * This a higher level interface to this class as simply calling this will execute the entire path
     */
    public void followPath(DriveTrainSystem drive){

        //Checks if the robot hasnt already turned at the current waypoint, if not... turn
        if(!hasTurned.get(waypointNumber))
            turnToAngle(drive);
        else if(hasTurned.get(waypointNumber)) //However if it has then drive forward the required amount
            driveInches(drive);
    }

    /**
     * Called from within the followPath method that helps with turing to angles
     * @param drive Gets passed a reference to the drive train to avoid errors
     */
    private void turnToAngle(DriveTrainSystem drive){

        //Check if the current angle is within the acceptable range (temp. PID) that it considers correct
        if(navx.getYaw() < Angles.get(waypointNumber)+1 && navx.getYaw() > Angles.get(waypointNumber)-1){

            //At which point stop turning and set the current waypoint's hasTurned value to true
            drive.arcadeDrive(0, 0);
            hasTurned.set(waypointNumber, true);

            //And since the wheels were driven the encoder values will have increased so.. reset them
            resetEncoder(drive);
        }

        //However if the angle is less than the required angle turn towards that angle
        else if(navx.getYaw() < Angles.get(waypointNumber)){
            drive.arcadeDrive(0, -0.5);
        }

        //And if its greater, turn in the opposite direction
        else if(navx.getYaw() > Angles.get(waypointNumber)){
            drive.arcadeDrive(0, 0.5);
        }
        
    }

    /**
     * Used to drive in straight lines from one coord to the next
     * @param drive once again passed a reference to the drive train
     */
    private void driveInches(DriveTrainSystem drive){
        //1 rotation of the middle motor's encoder shaft is equivelent to 3.1 inches
        double requiredTicks = (1/3.1)*Distances.get(waypointNumber);

        /**
         * TODO: Robot drives wrong direction, but follows path...in a really weird way
         * Example: https://youtu.be/B8t88I4NyUE
         */
        if((drive.getAverageEncoderPosition()-encoderOffset)> requiredTicks-0.1 && (drive.getAverageEncoderPosition()-encoderOffset) < requiredTicks+0.1){
            drive.arcadeDrive(0.07, 0.07);
            if((waypointNumber+1)<X.size()){
                waypointNumber++;
            }
        }
        
        //Gets the encoders rotational values and subtracts the offset then drives until its greater than what it needs to be
        else if (drive.getAverageEncoderPosition()-encoderOffset > requiredTicks){
            drive.arcadeDrive(0.2, 0);
        }

        //Drives the other direction
        else if (drive.getAverageEncoderPosition()-encoderOffset < requiredTicks){
            drive.arcadeDrive(-0.2, 0);
        }

    }

    /**
     * Used to get the Gyro heading from other classes
     * @return a double that represents the gyro angle
     */
    public double getHeading(){return navx.getYaw();}

}