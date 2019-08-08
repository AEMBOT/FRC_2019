package org.usfirst.frc.falcons6443.robot.autonomous;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    final double TICKS_PER_INCH = ((1/42)*(1/14.7)*((8*Math.PI)/1));
    int waypointNumber;

    public Pathing(){
        X = new ArrayList<>();
        Y = new ArrayList<>();
        Distances = new ArrayList<>();
        Angles = new ArrayList<>();
        hasTurned = new ArrayList<>();
        hasDriven = new ArrayList<>();

        csv = new File("C:\\Users\\jwilt\\Desktop\\Test.csv");
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

                System.out.println("Angle: " + angle);
                System.out.println("Distance: " + distance);
            }
        }
        catch (Exception e){

        }
    }

    public void drivePath(DriveTrainSystem drive){
        turnToAngle(drive);
        driveInches(drive);
        waypointNumber++;
    }

    private void turnToAngle(DriveTrainSystem drive){
        if(navx.getYaw() < Angles.get(waypointNumber)+4 && navx.getYaw() > Angles.get(waypointNumber)-4){
            drive.arcadeDrive(0, 0);
        }

        else if(navx.getYaw() < Angles.get(waypointNumber)){
            drive.arcadeDrive(0, -0.5);
        }
        else if(navx.getYaw() > Angles.get(waypointNumber)){
            drive.arcadeDrive(0, 0.5);
        }
        
    }

    private void driveInches(DriveTrainSystem drive){

    }

}