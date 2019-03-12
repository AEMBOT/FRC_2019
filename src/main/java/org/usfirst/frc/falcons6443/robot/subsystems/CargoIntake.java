package org.usfirst.frc.falcons6443.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.SpeedControllerGroup;

public class CargoIntake{
    private CANSparkMax leftIntakeMotor;
    private CANSparkMax rightIntakeMotor;

    private SpeedControllerGroup intake;

    public CargoIntake(){

        //Creates a static reference to a motor to avoid a resource already allocated error
        leftIntakeMotor = VacuumSystem.getMotor();
        rightIntakeMotor = new CANSparkMax(-1, MotorType.kBrushed);
        leftIntakeMotor.setInverted(true);

        //Create a speed controller group to hold the intake motors
        intake = new SpeedControllerGroup(leftIntakeMotor, rightIntakeMotor);
    }

    /**
     * Intake Ball
     */
    public void intake(){
        intake.set(-1);
    }

    /**
     * Shoots the ball back out the front
     */
    public void shoot(){
        intake.set(1);
    }
}