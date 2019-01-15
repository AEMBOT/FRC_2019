package org.usfirst.frc.falcons6443.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;
import org.usfirst.frc.falcons6443.robot.Robot;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.Encoders;
import org.usfirst.frc.falcons6443.robot.hardware.SpeedControllerGroup;
import org.usfirst.frc.falcons6443.robot.hardware.pneumatics.Piston;

public class ArmadilloClimber {

    private SpeedController leftMotor;
    private SpeedController rightMotor;

    private Encoders leftEncoder;

    private boolean isClimbing = false;

    private final int encoderTicks = 256;

    //Change to a point where the encoders will stop
    private final int stopTickCount = -1;

    private Piston leftPiston;
    private Piston rightPiston;

    //Set to the diameter of the wheel in inches
    private static final double wheelDiameter = 6;

    public ArmadilloClimber() {

        //Assigns the motors to the proper mappings
        rightMotor = new CANSparkMax(RobotMap.RightClimbMotor, CANSparkMaxLowLevel.MotorType.kBrushed);
        leftMotor = new CANSparkMax(RobotMap.LeftClimbMotor, CANSparkMaxLowLevel.MotorType.kBrushed);

        //Assign pistons to a port
        leftPiston = new Piston(RobotMap.LeftPiston);
        rightPiston = new Piston(RobotMap.RightPiston);

        //maps encoders to ports
        leftEncoder = new Encoders(RobotMap.LeftClimbEncoderA, RobotMap.LeftClimbEncoderB);

        //set encoder ticks per rev
        leftEncoder.setTicksPerRev(encoderTicks);

        //set wheel diameters
        leftEncoder.setDiameter(wheelDiameter);

        leftEncoder.reset();

        //Flips the left motor so it is running the right direction
        leftMotor.setInverted(true);

    }

    public void enableClimb(){
        isClimbing = true;
    }

    public void enableKillSwitch(){
        isClimbing = false;
    }

    //Begin climb
    public void climb(){
    if(isClimbing == true) {
        //run motors until a certain encoder value is reached
        if (leftEncoder.getDistanceWithDiameter() >= stopTickCount) {
            leftMotor.set(0);
            leftMotor.set(0);

            leftPiston.out();
            rightPiston.out();
            isClimbing = false;
        } else {
            //Run both motors at a set speed
            rightMotor.set(0.5);
            leftMotor.set(0.5);
        }
    }
    }

}
