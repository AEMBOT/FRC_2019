package org.usfirst.frc.falcons6443.robot.subsystems;

import javax.management.timer.Timer;
import javax.sound.sampled.AudioFormat.Encoding;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.CAN;
import edu.wpi.first.wpilibj.DigitalInput;
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

    private CANSparkMax armMotor;
    private CANEncoder armEncoder;
    private CANSparkMax ballVacuumMotor;
    private CANSparkMax hatchVacuumMotor;

    private Timer armTime;

    private LimitSwitch topSwitch;
    private boolean isManual = true;
    private boolean isEncoderReset = false;

    private boolean isMovingBack = false;
    private boolean isCentering = false;
    private boolean isMovingDown = false;

    private int currentHatchPosition = 0;

    private double encoderOffset = 0;

    public boolean toggle;

  //  private Encoders armEncoder;

    private final int armStopPosition = -1;

    public VacuumSystem() {
        armMotor = new CANSparkMax(RobotMap.VacuumArmMotor, MotorType.kBrushless);

        armEncoder = armMotor.getEncoder();

        //ballVacuumMotor = new CANSparkMax(RobotMap.VacuumBallMotor, CANSparkMaxLowLevel.MotorType.kBrushed);
        hatchVacuumMotor = new CANSparkMax(RobotMap.VacuumHatchMotor, CANSparkMaxLowLevel.MotorType.kBrushed);

        topSwitch = new LimitSwitch(RobotMap.VacuumArmTopSwitch);
        //bottomSwitch = new LimitSwitch(RobotMap.VacuumArmBottomSwitch);

       //armEncoder = new Encoders(RobotMap.VacuumArmEncoderA, RobotMap.VacuumArmEncoderB,EncodingType.k4X);
       toggle = false;
       hatchVacuumMotor.setInverted(true);
       isEncoderReset = false;
    }

    //Checks the status as to weather or not the encoder has been reset
    public boolean getEncoderStatus(){
        return isEncoderReset;
    }

    public void setEncoderStatus(boolean newSat){
         isEncoderReset = newSat;
    }

    public void setManual(boolean set){
        isManual = set;
    } 

    public boolean getManual() {
        return isManual;
    }

    public boolean getLimit(){
        return topSwitch.get();
    }


    public void resetArm(){
    

       
           encoderOffset = armEncoder.getPosition();
        
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


    public void activateHatchSuction() {
        hatchVacuumMotor.set(1);
    }

    public void deactivateSuction() { //Used for deactivation of both vacuum motors
        hatchVacuumMotor.set(0);
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

    /**
     * Allows manual control of the hatch arm as long as the top limit switch is not pressed
     * @param val pass in a joystick value
     */
    public void manual(double val){
        if(isMovingDown == false || isMovingBack == false || isCentering == false || isEncoderReset == false)
           
            if(Math.abs(val) > 0.2){
                armMotor.set(val*0.9);
                System.out.println((armEncoder.getPosition() - encoderOffset));
                isManual = true;
            } else {
                isManual = false;
                armMotor.set(0);
            }
            
    } 

    //Move arm to floor for floor pickup or to climg
    public void moveArmDown() {
        if(isMovingDown && isManual == false){
            if((armEncoder.getPosition() - encoderOffset) > -42){
                armMotor.set(-0.4);
            }
            else{
                armMotor.set(0);
                isMovingDown = false;
            }
        }
    }

    //Move arm to floor for floor pickup or to climg
    public void moveArmCenter() {
        if(isCentering && isManual == false){
            if((armEncoder.getPosition() - encoderOffset) > -18){
                armMotor.set(-0.4);
            }
            else if((armEncoder.getPosition() - encoderOffset) < -22)
            {
                armMotor.set(0.4);
            }
            else{
                armMotor.set(0);
                isCentering = false;
            }
        }
    }

    //Moves arm back to starting postion
    public void moveArmBack() {
        if(isMovingBack && isManual == false){
            if((armEncoder.getPosition() - encoderOffset) < -5)
            {
                armMotor.set(0.4);
            }
            else{
                armMotor.set(0);
                isMovingBack = false;
            }
        }
    }
}
