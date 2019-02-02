package org.usfirst.frc.falcons6443.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.CAN;

import org.usfirst.frc.falcons6443.robot.Robot;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.Encoders;
import org.usfirst.frc.falcons6443.robot.hardware.LimitSwitch;

/**
 * @author Goirick Saha
 */
public class VacuumSystem {

    private CANSparkMax armMotor;
    private CANSparkMax ballVacuumMotor;
    private CANSparkMax hatchVacuumMotor;

    private LimitSwitch topSwitch;
    private LimitSwitch bottomSwitch;

    private Encoders armEncoder;

    private final int armStopPosition = -1;

    public VacuumSystem() {
   //     armMotor = new CANSparkMax(RobotMap.VacuumArmMotor, CANSparkMaxLowLevel.MotorType.kBrushed);
  //      ballVacuumMotor = new CANSparkMax(RobotMap.VacuumBallMotor, CANSparkMaxLowLevel.MotorType.kBrushed);
        hatchVacuumMotor = new CANSparkMax(RobotMap.VacuumHatchMotor, CANSparkMaxLowLevel.MotorType.kBrushed);

  //      topSwitch = new LimitSwitch(RobotMap.VacuumArmTopSwitch);
  //      bottomSwitch = new LimitSwitch(RobotMap.VacuumArmBottomSwitch);

    //    armEncoder = Encoders(RobotMap.VacuumArmEncoderA, RobotMap.VacuumArmEncoderB);
    }

    public void activateBallSuction() {
        ballVacuumMotor.set(1);
        hatchVacuumMotor.set(0);
    }

    public void activateHatchSuction() {
        hatchVacuumMotor.set(1);
      //  ballVacuumMotor.set(0);
    }

    public void deactivateSuction() { //Used for deactivation of both vacuum motors
        hatchVacuumMotor.set(0);
     //   ballVacuumMotor.set(0);
    }

    public void moveArmDown() {
        armMotor.set(1);
        if (bottomSwitch.get()) {
            armMotor.set(0);
            armEncoder.reset();
        }
    }

    public void moveArmUp() {
            if(armEncoder.getDistanceWithDiameter() >= armStopPosition) {
                armMotor.set(-0.75); //move arm forwards until vertical position
            } else if (armEncoder.getDistanceWithDiameter() <= armStopPosition) {
                armMotor.set(-0.75); //move arm backwards until vertical position
            } else {
                armMotor.set(0); //Stop arm at vertical position
                armEncoder.reset();
            }
        }

    public void moveArmBack() {
        armMotor.set(-0.75);
        if (bottomSwitch.get()) {
            armMotor.set(0);
            armEncoder.reset();
        }
    }
}
