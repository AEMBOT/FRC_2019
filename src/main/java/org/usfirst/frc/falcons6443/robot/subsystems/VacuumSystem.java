package org.usfirst.frc.falcons6443.robot.subsystems;

import javax.sound.sampled.AudioFormat.Encoding;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.CAN;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;

import org.usfirst.frc.falcons6443.robot.Robot;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.Encoders;
import org.usfirst.frc.falcons6443.robot.hardware.LimitSwitch;
 
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.ControlMode;

/**
 * @author Goirick Saha
 */
public class VacuumSystem {

    private TalonSRX armMotor;
    private CANSparkMax ballVacuumMotor;
    private CANSparkMax hatchVacuumMotor;

    private LimitSwitch topSwitch;
    private LimitSwitch bottomSwitch;
    private boolean isManual = true;

    private boolean toggle;

  //  private Encoders armEncoder;

    private final int armStopPosition = -1;

    public VacuumSystem() {
        armMotor = new TalonSRX(RobotMap.VacuumArmMotor);
        armMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

       // ballVacuumMotor = new CANSparkMax(RobotMap.VacuumBallMotor, CANSparkMaxLowLevel.MotorType.kBrushed);
        hatchVacuumMotor = new CANSparkMax(RobotMap.VacuumHatchMotor, CANSparkMaxLowLevel.MotorType.kBrushed);

    //    topSwitch = new LimitSwitch(RobotMap.VacuumArmTopSwitch);
    //    bottomSwitch = new LimitSwitch(RobotMap.VacuumArmBottomSwitch);

       //armEncoder = new Encoders(RobotMap.VacuumArmEncoderA, RobotMap.VacuumArmEncoderB,EncodingType.k4X);
       toggle = false;
       hatchVacuumMotor.setInverted(true);
    }

    public void setManual(boolean set){
        isManual = set;
    } 

    public boolean getManual() {
        return isManual;
    }

    public void toggleSuction(){
        toggle = !toggle;
    }

    public void suck(){
        if(toggle) hatchVacuumMotor.set(1);
        else hatchVacuumMotor.set(0);
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

    public void manual(double val){
        armMotor.set(ControlMode.PercentOutput, val);
    }

    //moves arm for floor pickup 
    public void moveArmDown() {
        armMotor.set(ControlMode.PercentOutput, 1);
        System.out.println(armMotor.getSelectedSensorPosition());
        if (bottomSwitch.get()) {
            armMotor.set(ControlMode.PercentOutput, 0);
            armMotor.setSelectedSensorPosition(0);
        }
    }

    //moves arm for hatch placement/retrieval
    public void moveArmUp() {
            if(armMotor.getSelectedSensorPosition() >= armStopPosition - 5) {
                armMotor.set(ControlMode.PercentOutput, 0.75); //move arm forwards until vertical position
            } else if (armMotor.getSelectedSensorPosition() <= armStopPosition + 5) {
                armMotor.set(ControlMode.PercentOutput, -0.75); //move arm backwards until vertical position
            } else {
                armMotor.set(ControlMode.PercentOutput, 0); //Stop arm at vertical position
                armMotor.setSelectedSensorPosition(0);
            }
        }

    //moves arm back to starting postion
    public void moveArmBack() {
        armMotor.set(ControlMode.PercentOutput, -0.75);
        System.out.println(armMotor.getSelectedSensorPosition());
        if (armMotor.getSelectedSensorPosition() <= 5) {
            armMotor.set(ControlMode.PercentOutput, 0);
            armMotor.setSelectedSensorPosition(0);
        }
    }
}
