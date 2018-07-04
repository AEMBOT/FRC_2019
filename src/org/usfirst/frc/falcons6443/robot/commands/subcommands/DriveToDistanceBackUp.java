package org.usfirst.frc.falcons6443.robot.commands.subcommands;

import org.usfirst.frc.falcons6443.robot.commands.SimpleCommand;
import org.usfirst.frc.falcons6443.robot.utilities.*;
import org.usfirst.frc.falcons6443.robot.utilities.enums.LoggerSystems;

public class DriveToDistanceBackUp extends SimpleCommand{

    private double targetDistance;
    private double buffer = 1; //inches
    private boolean done;

    public DriveToDistanceBackUp(int distance){
        super("Drive To Distance");
        requires(driveTrain);
        targetDistance = distance;
    }

    private void driveToDistance(){
        double power = .7; //faster? slower? //.53

        if(driveTrain.getLeftDistance() > (targetDistance - buffer)){
            power = 0;
            done = true;
        }
        driveTrain.tankDrive(power, power);
    }

    @Override
    public void initialize() {
        driveTrain.reset();
        done = false;
    }

    @Override
    public void execute() {
        driveToDistance();
    }

    @Override
    protected boolean isFinished() {
        return done;
    }
}
