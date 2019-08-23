package org.usfirst.frc.falcons6443.robot.autonomous;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.usfirst.frc.falcons6443.robot.RobotConstants;
import org.usfirst.frc.falcons6443.robot.hardware.NavX;
import org.usfirst.frc.falcons6443.robot.subsystems.DriveTrainSystem;


public class Pathing {

    List<Integer> X;
    List<Integer> Y;
    List<Double> Distances;
    List<Double> Angles;
    List<Boolean> hasTurned;
    List<Boolean> hasDriven;
    File csv;
    Scanner file;
    NavX navx;

    public final double TICKS_PER_INCH = 5.25;

    int waypointNumber;
    private double encoderOffset = 0;

    public Pathing(String waypointFilePath){
        X = new ArrayList<>();
        Y = new ArrayList<>();
        Distances = new ArrayList<>();
        Angles = new ArrayList<>();
        hasTurned = new ArrayList<>();
        hasDriven = new ArrayList<>();

        csv = new File(waypointFilePath);
        try {
            file = new Scanner(csv);
        } catch (FileNotFoundException e) {
        }

        navx = NavX.get();

    }
    
    

    public void loadData() throws FileNotFoundException {
        
        int i = 0;
        while (file.hasNextLine()){
            String line = file.nextLine();

            if(i>0) {
                X.add(Integer.parseInt(line.split(",")[0]));
                Y.add(Integer.parseInt(line.split(",")[1]));
            }

            i++;
        }

        try {
            for (int j = 0; j < X.size(); j++) {
                double xDist = X.get(j) - X.get(j + 1);
                double yDist = Y.get(j) - Y.get(j + 1);
                double angle = Math.toDegrees(Math.atan2(Math.abs(yDist), Math.abs(xDist)));
                Angles.add(angle);
                double distance = Math.sqrt(Math.pow(Math.abs((X.get(j + 1) - X.get(j))), 2) + Math.pow(Math.abs(Y.get(j + 1) - Y.get(j)), 2));
                Distances.add(distance);
                hasTurned.add(false);

                System.out.println("Angle: " + angle);
                System.out.println("Distance: " + distance);
            }
        }
        catch (Exception e){

        }
    }

    public void resetEncoder(DriveTrainSystem drive){
        encoderOffset = drive.getAverageEncoderPosition();
    }

    public void followPath(DriveTrainSystem drive){
        if(!hasTurned.get(waypointNumber))
            turnToAngle(drive);
        else if(hasTurned.get(waypointNumber))
            driveInches(drive);
    }

    private void turnToAngle(DriveTrainSystem drive){
        if(navx.getYaw() < Angles.get(waypointNumber)+1 && navx.getYaw() > Angles.get(waypointNumber)-1){
            drive.arcadeDrive(0, 0);
            hasTurned.set(waypointNumber, true);
            resetEncoder(drive);
        }

        else if(navx.getYaw() < Angles.get(waypointNumber)){
            drive.arcadeDrive(-0.5, 0);
        }
        else if(navx.getYaw() > Angles.get(waypointNumber)){
            drive.arcadeDrive(0.5, 0);
        }
        
    }

    private void driveInches(DriveTrainSystem drive){

        double requiredTicks = (1/3.1)*Distances.get(waypointNumber);
        //System.out.println("Wanted Distance: " + requiredTicks);
        //System.out.println("Current Distance: " + (drive.getAverageEncoderPosition()-encoderOffset));
        

        //TODO: Flip Motor Values when backwards
        if((drive.getAverageEncoderPosition()-encoderOffset)> requiredTicks-0.1 && (drive.getAverageEncoderPosition()-encoderOffset) < requiredTicks+0.1){
            drive.arcadeDrive(0.07, 0.07);
            if((waypointNumber+1)<X.size()){
                waypointNumber++;
            }
        }
        else if (drive.getAverageEncoderPosition()-encoderOffset > requiredTicks){
            drive.arcadeDrive(0, 0.2);
        }
        else if (drive.getAverageEncoderPosition()-encoderOffset < requiredTicks){
            drive.arcadeDrive(0, -0.2);
        }

    }

    /**
     * Used to get the Gyro heading from other classes
     * @return a double that represents the gyro angle
     */
    public double getHeading(){
        return navx.getYaw();
    }

}