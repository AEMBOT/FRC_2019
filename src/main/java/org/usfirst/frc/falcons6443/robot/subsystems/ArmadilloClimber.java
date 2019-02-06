package org.usfirst.frc.falcons6443.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;
import org.usfirst.frc.falcons6443.robot.Robot;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.Encoders;
import org.usfirst.frc.falcons6443.robot.hardware.SpeedControllerGroup;
import org.usfirst.frc.falcons6443.robot.hardware.pneumatics.Piston;
import org.usfirst.frc.falcons6443.robot.utilities.enums.LoggerSystems;
import org.usfirst.frc.falcons6443.robot.utilities.Logger;

public class ArmadilloClimber {

    private CANSparkMax leftMotor;
    private CANSparkMax rightMotor;


    private CANEncoder leftEncoder;

    private boolean isClimbing = false;
    private boolean isClimbingArmDown = false;

    private final int encoderTicks = 256;
    

    //Change to a point where the encoders will stop
    private final int stopTickCount = 270;
    private double climbSpeed = 1;
    private int armUpTickCount = 95;

    //Set to the diameter of the wheel in inches
    private static final double wheelDiameter = 6;

    public ArmadilloClimber() {

        //Assigns the motors to the proper mappings
        rightMotor = new CANSparkMax(RobotMap.RightClimbMotor, CANSparkMaxLowLevel.MotorType.kBrushless);
        leftMotor = new CANSparkMax(RobotMap.LeftClimbMotor, CANSparkMaxLowLevel.MotorType.kBrushless);

        isClimbing = false;
        isClimbingArmDown = false;

        //maps encoders to ports
        leftEncoder = leftMotor.getEncoder();

        //set encoder ticks per rev
       // leftEncoder.setTicksPerRev(encoderTicks);

        //set wheel diameters
        //leftEncoder.setDiameter(wheelDiameter);

        //leftEncoder.reset();

        //Flips the left motor so it is running the right direction
        rightMotor.setInverted(true);

    }

    public void enableClimb(){
        isClimbing = true;
    }

    public void enableKillSwitch(){
        isClimbing = false;
    }

    public double updatePosition(double climbDegree){
        return leftEncoder.getPosition() - climbDegree;
    }

    //Begin climb
    public void climb(){
     double climbDegree = leftEncoder.getPosition();
    if(isClimbing == true) {
        //run motors until a certain encoder value is reached
        while(updatePosition(climbDegree) <= stopTickCount){
            leftMotor.set(climbSpeed);
            rightMotor.set(climbSpeed);
            System.out.println(updatePosition(climbDegree));
            Logger.log(LoggerSystems.Climb,"" + updatePosition(climbDegree));
             } 
        isClimbing = false;
        isClimbingArmDown = true;
        leftMotor.set(0);
        rightMotor.set(0);
     }
    
     }

     public void bringArmUp(){
        double climbDegree = leftEncoder.getPosition();
        if(isClimbing == false && isClimbingArmDown == true){
            while(updatePosition(climbDegree) >= armUpTickCount){
                leftMotor.set(-climbSpeed);
                rightMotor.set(-climbSpeed);
                System.out.println(updatePosition(climbDegree));
                Logger.log(LoggerSystems.Climb,"" + updatePosition(climbDegree));
            }
     }

    
}
}


