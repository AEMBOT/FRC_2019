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
import org.usfirst.frc.falcons6443.robot.hardware.LimitSwitch;

public class ArmadilloClimber {

    //Primary Climb Motors
    private CANSparkMax leftMotor;
    private CANSparkMax rightMotor;

    //Secondary Climb Motors
    private CANSparkMax leftSecondMotor;
    private CANSparkMax rightSecondMotor;  

    //Beam break for stopping the arm/leveling it
    private LimitSwitch bellySwitch;

    //Ref to the led controller class
    private static LEDSystem led;

    //Primary Climber Encoder
    private CANEncoder leftEncoder;

    //Secondary Climber Encoder
    private CANEncoder secondaryEncoder;

    //Assigns basic climb booleans for the robot to know what stage of the hab climb it is in
    private boolean isClimbing = false;
    private boolean isClimbingArmDown = false;
    private boolean hasClimbed = false;
    private boolean runStage2 = false;

    //Tells the robot at the start of the match it should try to level the arm
    private boolean steady = true;   

    //Change to a point where the encoders will stop
    private final int stopTickCount = 285;
    private double climbSpeed = 1;

    //Encoder positions for secondary climb
    private final int secondaryArmTickCount = -1;

    private ClimbEnum position;
    private boolean first;

    //Climber Encoder Values
    double climbDegree;
    double secondaryClimbDegree;
  

    public boolean secondary;
    public boolean isContractingArm = false;

    private VacuumSystem vacuum;

    //Set to the diameter of the wheel in inches
    private static final double wheelDiameter = 6;

    public ArmadilloClimber(VacuumSystem vacuum) {

        //Assigns the motors to the proper mappings
        rightMotor = new CANSparkMax(RobotMap.RightClimbMotor, CANSparkMaxLowLevel.MotorType.kBrushless);
        leftMotor = new CANSparkMax(RobotMap.LeftClimbMotor, CANSparkMaxLowLevel.MotorType.kBrushless);

        //Assigns secondary climber motors
        leftSecondMotor = new CANSparkMax(RobotMap.LeftSecondClimbMotor, CANSparkMaxLowLevel.MotorType.kBrushless);
        rightSecondMotor = new CANSparkMax(RobotMap.RightSecondMotor, CANSparkMaxLowLevel.MotorType.kBrushless);                                                

        //Creates a new LED System object for controlling LEDS
        led = new LEDSystem();

        //Assigns climbing variables to false at start of match
        isClimbing = false;
        isClimbingArmDown = false;

        //Creates a new limit switch object for the reset belly switch
        bellySwitch = new LimitSwitch(RobotMap.ClimbArmBellySwitch, false);

        //Assigns the left encoder variable to reference the climb's left motor
        leftEncoder = leftMotor.getEncoder();

        //Assigns the secondaryEncoder variable to the secondary climbs's left motor encoder
        secondaryEncoder = leftSecondMotor.getEncoder();

        //Creates a reference to a pre created vaccum object
        this.vacuum = vacuum;

        //Flips the left motor so it is running the right direction
        rightMotor.setInverted(true);

        //Inverts the left motor
        leftSecondMotor.setInverted(true);

        //Sets the current climb stage
        position = ClimbEnum.Steady;

        //Checks if it should reset the encoder
        first = true;

        //Check if the secondary key for the climb is pressed
        secondary = false;
    }

    /**
     * This method is called at enable and it effectivly moves the climber up until a beam break is broken 
     * At which point it has been considered reset
     * 
     */
    public void steady(){
        if(steady){
            if(!bellySwitch.get()) {
                leftMotor.set(-.3);
                rightMotor.set(-.3);
            }
            else {
                leftMotor.set(0);
                rightMotor.set(0);
            }
        }
    }

    /**
     * Tells the code that the driver wants to run a two stage climb
     */
    public void enableStage2(){
        runStage2 = true;
    }

    /**
     * Gets the variable hasClimbed
     * @return hasClimbed
     */
    public boolean getHasClimbed(){
        return hasClimbed;
    }

    /**
     * Returns the value of isClimbing
     * @return isClimbing
     */
    public boolean getIsClimbing(){
        return isClimbing;
    }

    /**
     * Enables the climb
     */
    public void enableClimb(){
        isClimbing = true;
    }

    /**
     * Called whenever the kill switch is pressed and allows manual control of the climber
     */
    public void enableKillSwitch(){
        isClimbing = false;
        isClimbingArmDown = false;
        Robot.isKillSwitchEnabled = true;
    }

    /**
     * Gets the climber encoder value with offset to simulate it being 0ed
     * @param climbDegree passes the variable that holds the 0ed value
     * @return the 'reset' encoder position
     */
    public double getPrimaryClimberPosition(double climbDegree){
        return leftEncoder.getPosition() - climbDegree;
    }

    /**
     * Returns the offset climber position of the secondary climber
     * @return returns offset climber degree
     */
    public double getSecondaryClimberPosition(){
        return secondaryEncoder.getPosition();
    }

    /**
     * Stores Enums for different climb positions
     */
    public enum ClimbEnum{
        ClimbHab, ClimbStage2 ,ContractArm, Steady, Off
    }

    /**
     * Allows for manual control of the 
    */
    public void secondaryClimberManual(double speed){

            //Applies speed variable values to the motors
            leftSecondMotor.set(speed);
            rightSecondMotor.set(speed);

            System.out.println(secondaryEncoder.getPosition());
    }

    /**
     * Sets the current climb position through enum
     * @param num pass the current climb type
     */
    public void setClimb(ClimbEnum num){
        position = num;
    }

    
    public static LEDSystem getLED(){
        return led;
    }

    public void climb(){
        switch(position){

            //Called at start of match, this just moves the climber to hug the bottom of the robot
            case Steady:
                steady = true;
                steady();
                break;

            //At this stage the climb is actually started
            case ClimbHab:

                //Switches LEDS to rainbow
                led.enableRainbow();

                //Tells the code it is no longer supposed to steady the arm
                steady = false;

                //Checks if it should get the current climb position to 'reset' the encoder
                if(first){
                    climbDegree = leftEncoder.getPosition();
                    secondaryClimbDegree = secondaryEncoder.getPosition();
                }
                first = false;

                //Moves the vaccum arm down to avoid it being crushed
                vacuum.enableMovingDown();

                //Get the current climb position and check if it is less than the stop tick point and make sure the beam break was not broken
                if(getPrimaryClimberPosition(climbDegree) <= stopTickCount){

                    //Takes the stop tick count and subtracts the current encoder position and checks if it is greater than or equal to 15
                    if(stopTickCount - getPrimaryClimberPosition(climbDegree) >= 15){
                        //If so continue climbing as normal
                        leftMotor.set(climbSpeed);
                        rightMotor.set(climbSpeed);
                    }  
                    else{

                        //If not slow down the climb
                        leftMotor.set(climbSpeed/3);
                        rightMotor.set(climbSpeed/3);
                    }
                } 
                else {
                    //After it has finished climbing set isClimbing to false, hasClimbed to true and isClimbingArmDown to true, aswell turn off the motors and change the arm to contract mode
                    isClimbingArmDown = true;
                    leftMotor.set(0);
                    rightMotor.set(0);
                    setClimb(ClimbEnum.ContractArm);
                }
                break;

            /**
             * Initiates Stage 2 Hab Climb
             */
            case ClimbStage2:

                //Sets the vac arm to the back position
                vacuum.enableMovingBack();

                //Checks if the 
                if(getSecondaryClimberPosition() <= secondaryArmTickCount){
                    leftSecondMotor.set(0.6);
                    rightSecondMotor.set(0.6); 
                }
                else{
                    leftSecondMotor.set(0);
                    rightSecondMotor.set(0); 
                }
            break;  

            //This case is for contracting the arm after we have already climbed
            case ContractArm:
                isContractingArm = true;
                //This if checks if we are not climbing and if the arm is still down
                if(isClimbing == false && isClimbingArmDown == true){

                    //If this is the case then check if the encoder value is greater than the armUpTickCount and the bellySwitch has not been triggered
                    if(bellySwitch.get() == false){

                        //If so reverse the climber at half speed and print out the position of the climber
                        leftMotor.set(-climbSpeed);
                        rightMotor.set(-climbSpeed);
                    }

                    //If it is passed that point stop the motors
                    else {
                        rightMotor.set(0);
                        leftMotor.set(0);
                        hasClimbed = true;
                        isClimbing = false;

                        //Check if it is meant to run stage 2, if so run it
                        if(runStage2)
                            setClimb(ClimbEnum.ClimbStage2);
                    } 
                }

                //If the original if is not true stop the motors
                else {
                    rightMotor.set(0);
                    leftMotor.set(0);
                }
                
                break;

                //This stops the motors
                case Off:
                    leftMotor.set(0);
                    rightMotor.set(0);
                    break;
                    
                default:
                    position = ClimbEnum.Off;
                    break;    
            }        
        }

    /**
     * Allows for manual control of the climber arm
     * @param power pass a joystick value
     */
    public void manualControl(double power){
        leftMotor.set(power);
        rightMotor.set(power);
    }

}
