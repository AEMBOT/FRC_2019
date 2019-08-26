package org.usfirst.frc.falcons6443.robot.autonomous.autoPathing;

import java.io.IOException;

import org.usfirst.frc.falcons6443.robot.hardware.NavX;
import org.usfirst.frc.falcons6443.robot.subsystems.DriveTrainSystem;

import edu.wpi.first.wpilibj.Notifier;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.PathfinderFRC;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;

public class Pathing {

    private DriveTrainSystem drive;
    
    private NavX navX;

    private EncoderFollower leftFollower;
    private EncoderFollower rightFollower;

    private Notifier followerNotifier;


    public Pathing(DriveTrainSystem drive){
        this.drive = drive;
        
        navX = NavX.get();
    }

    /**
     * Called in the auton init function to start the calculations
     */
    public void runPath() throws IOException {

        //Stop Inverting the left side to allow for the correct calculations
        drive.getLeftMotors().setInverted(false);


        //Create two trajectories one for each side of the drive train
        Trajectory leftTrajectory;
        Trajectory rightTrajectory;

        //Assign trajectories to corresponding files
        leftTrajectory = PathfinderFRC.getTrajectory(RobotPathConstants.PATH_NAME + ".left");
        rightTrajectory = PathfinderFRC.getTrajectory(RobotPathConstants.PATH_NAME + ".right");
        

        //Configure the left followers encoder
        leftFollower.configureEncoder(drive.getLeftSideEncoderPosition(), RobotPathConstants.TICKS_PER_REV, RobotPathConstants.WHEEL_DIAMETER);

        //Configure the advanced PID which includes velocity
        leftFollower.configurePIDVA(1.0, 0.0, 0.0, 1 / RobotPathConstants.MAX_VELOCITY, 0.0);

        //Configure the right follower's encoder
        rightFollower.configureEncoder(drive.getRightSideEncoderPosition(), RobotPathConstants.TICKS_PER_REV, RobotPathConstants.WHEEL_DIAMETER);

        //Configure the right followers PID
        rightFollower.configurePIDVA(1.0, 0.0, 0.0, 1 / RobotPathConstants.MAX_VELOCITY, 0.0);

        followerNotifier = new Notifier(this::followPath);
        followerNotifier.startPeriodic(leftTrajectory.get(0).dt);
        
    }

    /**
     * Called periodically to calculate values needed for path
     */
    private void followPath(){

        //If the path is complete stop trying to follow the path
        if(leftFollower.isFinished() || rightFollower.isFinished()){
            followerNotifier.stop();
        }
        else{
            double leftSpeed = leftFollower.calculate(drive.getLeftSideEncoderPosition());
            double rightSpeed = rightFollower.calculate(drive.getRightSideEncoderPosition());

            double heading = navX.getYaw();
            double desiredHeading = Pathfinder.r2d(leftFollower.getHeading());
            double headingDifference = Pathfinder.boundHalfDegrees(desiredHeading-heading);
            double turn = 0.8 * (-1.0/80.0) * headingDifference;
            drive.tankDrive(leftSpeed + turn, rightSpeed - turn);
        }
    }

    /**
     * Stops running the path
     */
    public void stopPathing(){
        followerNotifier.stop();
        drive.tankDrive(0, 0);

        //Re-Invert the motors so normal driving still works
        drive.getLeftMotors().setInverted(true);
    }
}