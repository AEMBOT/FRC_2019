package org.usfirst.frc.falcons6443.robot.commands.subcommands;

import org.usfirst.frc.falcons6443.robot.commands.SimpleCommand;
import org.usfirst.frc.falcons6443.robot.utilities.pid.PID;

public class DriveToDistance extends SimpleCommand {

    private static final double P = .15; //.42
    private static final double I = 0;
    private static final double D = .1; //3.5
    private static final double Eps = 0.5; //weakest applied power //0.4???

    private static final double buffer = 1; //inches //0.5

    private double targetDistance;

    private PID pid;

    public DriveToDistance(double distance){
        super("Drive To Distance");
        requires(driveTrain);
        requires(turret);
        requires(shooter);
        pid = new PID(P, I, D, Eps);
        pid.setMaxOutput(.65);
        pid.setMinDoneCycles(5);
        pid.setFinishedRange(buffer);
        targetDistance = distance;
    }

    private void driveToDistance(){
        double power = pid.calcPID(driveTrain.getLeftDistance());
        driveTrain.tankDrive(power, power + .05);
    }

    private void setDistance(){
        pid.setDesiredValue(targetDistance);
    }

    private boolean isAtDistance(){
        return pid.isDone();
    }

    @Override
    public void initialize() {
        driveTrain.reset();
        setDistance();
    }

    @Override
    public void execute() {
        turret.update();
        shooter.autoUpdate();
        driveToDistance();
    }

    @Override
    protected boolean isFinished() {
        return isAtDistance();
    }
}
