package org.usfirst.frc.falcons6443.robot.subsystems;

import javax.sound.sampled.AudioFormat.Encoding;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.CAN;
import edu.wpi.first.wpilibj.Encoder;
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
    private Encoders armEncoder;
    private CANSparkMax ballVacuumMotor;
    private CANSparkMax hatchVacuumMotor;

    private LimitSwitch topSwitch;
    private LimitSwitch bottomSwitch;
    private boolean isManual = true;
    private boolean isEncoderReset = false;

    private boolean isMovingBack = false;
    private boolean isCentering = false;
    private boolean isMovingDown = false;

    private int currentHatchPosition = 0;

    public boolean toggle;

  //  private Encoders armEncoder;

    private final int armStopPosition = -1;

    public VacuumSystem() {
        armMotor = new TalonSRX(RobotMap.VacuumArmMotor);
        armMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        armEncoder = new Encoders(RobotMap.ArmEncoderA, RobotMap.ArmEncoderB);

        //ballVacuumMotor = new CANSparkMax(RobotMap.VacuumBallMotor, CANSparkMaxLowLevel.MotorType.kBrushed);
        hatchVacuumMotor = new CANSparkMax(RobotMap.VacuumHatchMotor, CANSparkMaxLowLevel.MotorType.kBrushed);

        topSwitch = new LimitSwitch(RobotMap.VacuumArmTopSwitch);
        //bottomSwitch = new LimitSwitch(RobotMap.VacuumArmBottomSwitch);

       //armEncoder = new Encoders(RobotMap.VacuumArmEncoderA, RobotMap.VacuumArmEncoderB,EncodingType.k4X);
       toggle = false;
       hatchVacuumMotor.setInverted(true);
       armEncoder.reset();
    }

    //Checks the status as to weather or not the encoder has been reset
    public boolean getEncoderStatus(){
        return isEncoderReset;
    }

    public void setManual(boolean set){
        isManual = set;
    } 

    public boolean getManual() {
        return isManual;
    }


    //Turns on a variable that will tell the vacuum whether or not it should be toggled on
    public void toggleSuction(){
        toggle = !toggle;
    }

    //Turns on the motors for the vacuum to suck
    public void suck(){
        if(toggle) hatchVacuumMotor.set(1);
        else hatchVacuumMotor.set(0);
    }

    public void activateBallSuction() {
        ballVacuumMotor.set(1);
        hatchVacuumMotor.set(0);
    }


    //Called whenever up on the dpad is pressed allowing the arm to move one position up
    public void raiseHatchArm(){
        if(currentHatchPosition > 0){
            switch(currentHatchPosition){
                case 1:
                    enableMovingBack();
                    currentHatchPosition--;
                    break;
                case 2:
                    enableCentering();
                    currentHatchPosition--;
                    break;
            }
        }
    }

    //Called when down on the dpad is pressed allowing it to move one position down
    public void lowerHatchArm(){
        if(currentHatchPosition < 2){
            switch(currentHatchPosition){
                case 0:
                    enableCentering();
                    currentHatchPosition++;
                case 1:
                    enableMovingDown();
                    currentHatchPosition++;
            }

        }
    }

    public void activateHatchSuction() {
        hatchVacuumMotor.set(1);
      //  ballVacuumMotor.set(0);
    }

    /**
     * Resets arm encoder values when limit switch is pressed
     */
    public void resetArmEncoder(){
        armEncoder.reset();
    }

    public void deactivateSuction() { //Used for deactivation of both vacuum motors
        hatchVacuumMotor.set(0);
     //   ballVacuumMotor.set(0);
    }

    //Enables centering sets the rest to false to allow for priority
    public void enableCentering(){
        isCentering = true;
        isMovingDown = false;
        isMovingBack = false;
    }

    //Enables moving down sets the rest to false to allow for priority
    public void enableMovingDown(){
        isMovingDown = true;
        isCentering = false;
        isMovingBack = false;
    }

    //Enables moving back the rest to false to allow for priority
    public void enableMovingBack(){
        isMovingBack = true;
        isCentering = false;
        isMovingDown = false;
    }


    public void manual(double val){
        //if(!topSwitch.get()){
            if(Math.abs(val) > 0.2){
               armMotor.set(ControlMode.PercentOutput, val);
               System.out.println(armEncoder.get());
               isManual = true;
            } else {
               isManual = false;
               armMotor.set(ControlMode.PercentOutput, 0);
            }
        //}
        //else{
            //armMotor.set(ControlMode.PercentOutput, 0);
        //}
    }

    //moves arm for floor pickup 
    public void moveArmDown() {
        if(isMovingDown){
                //if(!isManual){
                if(armEncoder.get() > -306){
                    armMotor.set(ControlMode.PercentOutput, -0.5);
                }
                else{
                    armMotor.set(ControlMode.PercentOutput, 0);
                    isMovingDown = false;
                }
            //}
        }
    }

    /**
     * Moves the hatch arm to a point were it can pick up/ place a hatch
     */
    public void moveArmUp() {
        if(isCentering){
            //if(!isManual){
                if(armEncoder.get() > -100){
                    armMotor.set(ControlMode.PercentOutput, -0.5);
                }
                else if(armEncoder.get() < -110){
                    armMotor.set(ControlMode.PercentOutput, 0.5);
                }
                else{
                    armMotor.set(ControlMode.PercentOutput, 0.1);
                    isCentering = false;
                }
            //}
        }
    }

    //moves arm back to starting postion
    public void moveArmBack() {
        if(isMovingBack){
            //if(!isManual){
                if(armEncoder.get() < 0){
                    armMotor.set(ControlMode.PercentOutput, 1);
                }
                else{
                    armMotor.set(ControlMode.PercentOutput, 0);
                    isMovingBack = false;
                }
            //}
        }
    }
}
