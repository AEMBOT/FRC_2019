package org.usfirst.frc.falcons6443.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;
import org.usfirst.frc.falcons6443.robot.Robot;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.Encoders;
import org.usfirst.frc.falcons6443.robot.hardware.SpeedControllerGroup;
import org.usfirst.frc.falcons6443.robot.hardware.joysticks.Xbox;
import org.usfirst.frc.falcons6443.robot.hardware.pneumatics.Piston;

public class ArmadilloClimberTest {

    private SpeedController leftMotor;
    private SpeedController rightMotor;

    private double climbSpeed = 0;

    public ArmadilloClimberTest() {

        //Assigns the motors to the proper mappings
        rightMotor = new CANSparkMax(RobotMap.RightClimbMotor, CANSparkMaxLowLevel.MotorType.kBrushed);
        leftMotor = new CANSparkMax(RobotMap.LeftClimbMotor, CANSparkMaxLowLevel.MotorType.kBrushed);

        //Flips the left motor so it is running the right direction
        leftMotor.setInverted(true);

    }

    //Climb when trigger is pulled
    public void climb(Xbox controller){
        if(controller.leftTrigger() > 0){
            climbSpeed = controller.leftTrigger();
        }

        rightMotor.set(climbSpeed);
        leftMotor.set(climbSpeed);
    }

}
