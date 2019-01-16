package org.usfirst.frc.falcons6443.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.CAN;
import edu.wpi.first.wpilibj.Encoder;
import org.usfirst.frc.falcons6443.robot.Robot;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.Encoders;
import org.usfirst.frc.falcons6443.robot.hardware.LimitSwitch;

/**
 * @author Goirick Saha
 */
public class VacuumSystem {

    private CANSparkMax ArmMotor;
    private CANSparkMax BallVacuumMotor;
    private CANSparkMax HatchVacuumMotor;

    private LimitSwitch TopSwitch;
    private LimitSwitch BottomSwitch;

    private Encoders ArmEncoder;

    private final int ArmStopPosition = -1;

    public VacuumSystem() {
        ArmMotor = new CANSparkMax(RobotMap.VacuumArmMotor, CANSparkMaxLowLevel.MotorType.kBrushed);
        BallVacuumMotor = new CANSparkMax(RobotMap.VacuumBallMotor, CANSparkMaxLowLevel.MotorType.kBrushed);
        HatchVacuumMotor = new CANSparkMax(RobotMap.VacuumHatchMotor, CANSparkMaxLowLevel.MotorType.kBrushed);

        TopSwitch = new LimitSwitch(RobotMap.VacuumArmTopSwitch);
        BottomSwitch = new LimitSwitch(RobotMap.VacuumArmBottomSwitch);

        ArmEncoder = new Encoders(RobotMap.VacuumArmEncoderA, RobotMap.VacuumArmEncoderB);
    }

    public void activateBallSuction() {
        BallVacuumMotor.set(1);
        HatchVacuumMotor.set(0);
    }

    public void activateHatchSuction() {
        HatchVacuumMotor.set(1);
        BallVacuumMotor.set(0);
    }

    public void deactivateSuction() { //Used for deactivation of both vacuum motors
        HatchVacuumMotor.set(0);
        BallVacuumMotor.set(0);
    }


    public void moveArmDown() {
        ArmMotor.set(1);
        if (BottomSwitch.get()) {
            ArmMotor.set(0);
            ArmEncoder.reset();
        }
    }

    public void moveArmUp() {
            if(ArmEncoder.getDistanceWithDiameter() >= ArmStopPosition) {
                ArmMotor.set(-0.75); //move arm forwards until vertical position
            } else if (ArmEncoder.getDistanceWithDiameter() <= ArmStopPosition) {
                ArmMotor.set(-0.75); //move arm backwards until vertical position
            } else {
                ArmMotor.set(0); //Stop arm at vertical position
                ArmEncoder.reset();
            }
        }

    public void moveArmBack() {
        ArmMotor.set(-0.75);
        if (BottomSwitch.get()) {
            ArmMotor.set(0);
            ArmEncoder.reset();
        }
    }
}
