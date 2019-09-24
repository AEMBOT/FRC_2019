package org.usfirst.frc.falcons6443.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CAN;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Timer;

import org.usfirst.frc.falcons6443.robot.Robot;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.Encoders;
import org.usfirst.frc.falcons6443.robot.hardware.LimitSwitch;
 
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.ControlMode;

/**
 * @author Goirick Saha, Will Richards
 */
public class VacuumSystem {


    private static CANSparkMax armMotor;
    private CANEncoder armEncoder;
    private CANSparkMax ballVacuumMotor;
    private CANSparkMax hatchVacuumMotor;

    private Timer armTime;

    private LimitSwitch topSwitch;
    private AnalogInput vacuumSensor;
    private Solenoid vacSolenoid;

    private boolean isManual = true;
    private boolean isEncoderReset = false;

    private boolean isMovingBack = false;
    private boolean isCentering = false;
    private boolean isMovingDown = false;
    private boolean isMovingUpSlightly = false;
    private boolean isMovingBackHatch = false;

    private boolean isSucking = false;

    private int currentHatchPosition = 0;

    private double encoderOffset = 0;

    public boolean toggle;
    private boolean solenoidVal = false;
    private boolean hasRetracted = false;

  //  private Encoders armEncoder;

    private final int armStopPosition = -1;

    public VacuumSystem() {
    
        armMotor = new CANSparkMax(RobotMap.VacuumArmMotor, MotorType.kBrushless);
        
        vacSolenoid = new Solenoid(0);
        

        vacuumSensor = new AnalogInput(0);


        armEncoder = armMotor.getEncoder();

        //ballVacuumMotor = new CANSparkMax(RobotMap.VacuumBallMotor, CANSparkMaxLowLevel.MotorType.kBrushed);
        hatchVacuumMotor = new CANSparkMax(RobotMap.VacuumHatchMotor_CargoRight, CANSparkMaxLowLevel.MotorType.kBrushed);

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

    //Returns a refernce to the armMotor
    public static CANSparkMax getMotor(){
        return armMotor;
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

    /**
     * Allows changing of solenoid state
     * @param value
     */
    public void setSolenoid(boolean value){
        this.solenoidVal = value;
        vacSolenoid.set(solenoidVal);
    }


    //Turns on a variable that will tell the vacuum whether or not it should be toggled on
    public void toggleSuction(){
        toggle = true;
        isSucking = true;
    }

    //Turns on the motors for the vacuum to suck with fan vac
    // public void suck(){
    //     if(toggle) hatchVacuumMotor.set(-1);
    //     else hatchVacuumMotor.set(0);
    // }

    public void suck(){
        if(toggle){
            if(vacuumSensor.getValue() > 1100 && isSucking == true){
                setSolenoid(true);
                hatchVacuumMotor.set(0.4);
            }
            else{
                hatchVacuumMotor.set(0);
                isSucking = false;
            }
        }
        else{
            hatchVacuumMotor.set(0);
            hasRetracted = false;
        }
    }

    public void releaseVac(){
            toggle = false;
            isSucking = false;
            setSolenoid(false);
            Timer.delay(0.5);
            enableMovingBack();
            hasRetracted=false;
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
        isMovingUpSlightly = false;
        isMovingBackHatch = false; 
    }

    //Enables moving down sets the rest to false to allow for priority
    public void enableMovingDown(){
        isMovingDown = true;
        isCentering = false;
        isMovingBack = false;
        isMovingUpSlightly = false;
        isMovingBackHatch = false; 
    }

    //Enables moving back the rest to false to allow for priority
    public void enableMovingBack(){
        isMovingBack = true;
        isCentering = false;
        isMovingDown = false;
        isMovingUpSlightly = false;
        isMovingBackHatch = false; 
    }

    //Enables moving back the rest to false to allow for priority
    public void enableMovingBackHatch(){
        isCentering = false;
        isMovingDown = false;
        isMovingUpSlightly = false;
        isMovingBack = false;
        isMovingBackHatch = true; 
    }

     //Enables moving back the rest to false to allow for priority
     public void enableMovingUpSlightly(){
        isMovingBack = false;
        isCentering = false;
        isMovingDown = false;
        isMovingUpSlightly = true;
        isMovingBackHatch = false; 
    }

    /**
     * Allows manual control of the hatch arm as long as the top limit switch is not pressed
     * @param val pass in a joystick value
     */
    public void manual(double val){
        if(isMovingDown == false && isMovingBack == false && isCentering == false && isMovingBackHatch == false){
           
            if(Math.abs(val) > 0.2){
                armMotor.set(val*0.9);
                isManual = true;
            } else {
                isManual = false;
                armMotor.set(0);
            }
        }
        else{
            isManual=false;
        }
            
    } 

    //Move arm to floor for floor pickup or to climg
    public void moveArmDown() {
        if(isMovingDown && isManual == false){
            if((armEncoder.getPosition() - encoderOffset) > -40){
                armMotor.set(-0.25);
            }
            else{
                armMotor.set(0);
                isMovingDown = false;
            }
        }
    }

    //Move arm to floor for floor pickup or to climb
    public void moveArmCenter() {
        System.out.println(armEncoder.getPosition() - encoderOffset);
        if(isCentering && isManual == false){

            if((armEncoder.getPosition() - encoderOffset) > -21.1 && (armEncoder.getPosition() - encoderOffset) < -17.2){
                armMotor.set(0);
                isCentering = false;
            }

            else if((armEncoder.getPosition() - encoderOffset) > -20.1){
                armMotor.set(-0.25);
            }
            else if((armEncoder.getPosition() - encoderOffset) < -19.2)
            {
                armMotor.set(0.25);
            }
            
        }
    }

    //Slightly move arm
    public void moveArmUp(){
        if(isMovingUpSlightly && isManual == false){
            if((armEncoder.getPosition() - encoderOffset) > -3.2){
                armMotor.set(-0.2);
            }
            else{
                armMotor.set(0);
                isMovingUpSlightly = false;
            }
        }
    }

    //Moves arm back to starting postion
    public void moveArmBack() {
        if(isMovingBack && isManual == false){
            if((armEncoder.getPosition() - encoderOffset) < -1.6)
            {
                armMotor.set(0.4);
            }
            else{
                armMotor.set(0);
                isMovingBack = false;
                hasRetracted = false;
            }
        }
    }

    /**
     * Moves the hatch arm back to a safe hatch carry position
     */
    public void moveArmBackHatch(){
        if(isMovingBackHatch && isManual == false){
            if((armEncoder.getPosition() - encoderOffset) < -3.2){
                armMotor.set(0.4);
            }
            else{
                armMotor.set(0);
                isMovingBackHatch = false;
            }
        }
    }
}
