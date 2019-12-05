package org.usfirst.frc.falcons6443.robot.autonomous.autoPathing;

import java.io.IOException;

import org.usfirst.frc.falcons6443.robot.hardware.NavX;
import org.usfirst.frc.falcons6443.robot.subsystems.DriveTrainSystem;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PIDController;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.PathfinderFRC;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;


public class Pathing {

    private DriveTrainSystem drive;
    
    private NavX navX;

    //Pathfinder's encoder controller references
    private EncoderFollower leftFollower;
    private EncoderFollower rightFollower;

    private Trajectory leftTrajectory;
    private Trajectory rightTrajectory;

    private Notifier followerNotifier;


    /**
     * Constructor is passed a reference to the drive train and reference to the NavX
     * @param drive global drive train reference
     */
    public Pathing(DriveTrainSystem drive){
        this.drive = drive;
        
        navX = NavX.get();
    }

    /**
     * Called in the auton init function to start the calculations
     */
    public void runPath(String pathName) throws IOException {
        drive.resetEncoder();

       
        /**
         * Assign trajectories to corresponding files
         * NOTE: Trajectories are flipped due to issues with PathWeaver
         */
        leftTrajectory = PathfinderFRC.getTrajectory("/output/" + pathName + ".right");
        rightTrajectory = PathfinderFRC.getTrajectory("/output/" + pathName + ".left");

        //Assign encoder followers to the corresponding directories
        leftFollower = new EncoderFollower(leftTrajectory);
        rightFollower = new EncoderFollower(rightTrajectory);
       
        //Configure the left followers encoder
        leftFollower.configureEncoder(drive.getLeftSideEncoderPosition(), RobotPathConstants.TICKS_PER_REV, RobotPathConstants.WHEEL_DIAMETER);

        //Configure the advanced PID which includes velocity
        leftFollower.configurePIDVA(0.1, 0.0, 0.0, 1 / RobotPathConstants.MAX_VELOCITY, 0.0);

        //Configure the right follower's encoder
        rightFollower.configureEncoder(drive.getRightSideEncoderPosition(), RobotPathConstants.TICKS_PER_REV, RobotPathConstants.WHEEL_DIAMETER);

        //Configure the right followers PID
        rightFollower.configurePIDVA(0.1, 0.0, 0.0, 1 / RobotPathConstants.MAX_VELOCITY, 0.0);

        followerNotifier = new Notifier(this::followPath);
        followerNotifier.startPeriodic(leftTrajectory.get(0).dt);
        
    }

    /**
     * Called periodically to calculate values needed for path
     */
    private void followPath(){

        //If the path is complete stop trying to follow the path
        if(leftFollower.isFinished() || rightFollower.isFinished()){
            stopPathing();
        }
        else{
            double leftSpeed = leftFollower.calculate(drive.getLeftSideEncoderPosition());
            double rightSpeed = rightFollower.calculate(drive.getRightSideEncoderPosition());

            double heading = navX.getYaw();
            System.out.println("Current Head. " + heading);
        
            //Invert Orientation Of Gyro
            double desiredHeading = -Pathfinder.r2d(leftFollower.getHeading());

            System.out.println("Desired Head. " + desiredHeading);

            double headingDifference = Pathfinder.boundHalfDegrees(desiredHeading-heading);
            double turn = 0.8 * (-1.0/80.0) * headingDifference;

        
            drive.tankDrive(leftSpeed - turn, rightSpeed + turn);
        }
    }

    /**
     * Stops running the path
     */
    public void stopPathing(){
        followerNotifier.stop();
        drive.tankDrive(-0.1, -0.1);

        //Re-Invert the motors so normal driving still works
        drive.getLeftMotors().setInverted(true);
    }
}