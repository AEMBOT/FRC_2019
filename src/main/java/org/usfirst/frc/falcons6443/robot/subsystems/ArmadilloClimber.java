package org.usfirst.frc.falcons6443.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import org.usfirst.frc.falcons6443.robot.Robot;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.LimitSwitch;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;

/**
 * This class is used for running the level 3 climb
 * 
 * @author Will Richards, Goirick Saha
 */
public class ArmadilloClimber {

    // Primary Climb Motors
    private CANSparkMax leftMotor;
    private CANSparkMax rightMotor;

    private SpeedControllerGroup primaryClimber;
    private SpeedControllerGroup secondaryClimber;

    //Secondary Climb Motors
    private CANSparkMax leftSecondMotor;
    private CANSparkMax rightSecondMotor;  

    //Beam break for stopping the arm/leveling it
    private LimitSwitch bellySwitch;

    //Primary Climber Encoder
    private CANEncoder leftEncoder;

    //Secondary Climber Encoder
    private CANEncoder secondaryEncoder;

    //Assigns basic climb booleans for the robot to know what stage of the hab climb it is in
    private boolean isClimbing = false;
    private boolean isClimbingArmDown = false;
    private boolean hasClimbed = false;
    public boolean runStage2 = false;
    private boolean runStage3 = true;
    private boolean secondaryRetractionDelay = true;

    //Tells the robot at the start of the match it should try to level the arm
    private boolean steady = true;   

    //Change to a point where the encoders will stop
    private final int stopTickCount = 228;
    private double climbSpeed = 1;

    //Encoder positions for secondary climb
    private final int secondaryArmTickCount = -50;
    private final int stage3TickCount = 80;

    private ClimbEnum position;
    private boolean first;

    //Climber Encoder Values
    double climbDegree;
    double secondaryClimbDegree;
  

    //Creates a new boolean named 'secondary' that stores the value of if the dpad on controller two is pressed
    public boolean secondary;

    //Creates a new bool for determining if the arm is contracting
    public boolean isContractingArm = false;


    public ArmadilloClimber() {

        //Assigns the motors to the proper mappings
        rightMotor = new CANSparkMax(RobotMap.RightClimbMotor, CANSparkMaxLowLevel.MotorType.kBrushless);
        leftMotor = new CANSparkMax(RobotMap.LeftClimbMotor, CANSparkMaxLowLevel.MotorType.kBrushless);

        //Assigns secondary climber motors
        leftSecondMotor = new CANSparkMax(RobotMap.LeftSecondClimbMotor, CANSparkMaxLowLevel.MotorType.kBrushless);
        rightSecondMotor = new CANSparkMax(RobotMap.RightSecondMotor, CANSparkMaxLowLevel.MotorType.kBrushless);                                                

        //Assigns climbing variables to false at start of match
        isClimbing = false;
        isClimbingArmDown = false;

        //Creates a new limit switch object for the reset belly switch
        bellySwitch = new LimitSwitch(RobotMap.ClimbArmBellySwitch, false);

        //Assigns the left encoder variable to reference the climb's left motor
        leftEncoder = leftMotor.getEncoder();

        //Assigns the secondaryEncoder variable to the secondary climbs's left motor encoder
        secondaryEncoder = leftSecondMotor.getEncoder();

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

        //Group for controlling the entire set of motors
        primaryClimber = new SpeedControllerGroup(leftMotor, rightMotor);
        secondaryClimber = new SpeedControllerGroup(leftSecondMotor, rightSecondMotor);
        
    }

    /**
     * This method is called at enable and it effectivly moves the climber up until a beam break is broken 
     * At which point it has been considered reset
     */
    public void steady(){
        if(steady){
            if(!bellySwitch.get()) {
                primaryClimber.set(-.3);
            }
            else {
                primaryClimber.set(0);
            }
        }
    }

    /**
     * Tells the code that the driver wants to run a two stage climb
     */
    public void enableStage2(){
        runStage2 = true;
    }

    public void enableStage3(){
        runStage3 = true;
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
        ClimbHab, ClimbStage2 ,ContractArm, ContractSecondary, ClimbStage3 ,Steady, Off
    }

    /**
     * Sets the current climb position through enum
     * @param num pass the current climb type
     */
    public void setClimb(ClimbEnum num){
        position = num;
    }


    /**
     * This method utilizes an enumerated type to determine the current stage of the climb and run that accordingly
     */
    public void climb(){
        switch(position){

            //Called at start of match, this just moves the climber to hug the bottom of the robot
            case Steady:
                steady = true;
                steady();
                break;

            //At this stage the climb is actually started
            case ClimbHab:

                System.out.println("Running Primary Climber...");

                //Tells the code it is no longer supposed to steady the arm
                steady = false;

                //Checks if it should get the current climb position to 'reset' the encoder
                if(first){
                    climbDegree = leftEncoder.getPosition();
                    secondaryClimbDegree = secondaryEncoder.getPosition();
                }

                //Tells the robot it has already run through the loop omce
                first = false;

                //Get the current climb position and check if it is less than the stop tick point and make sure the beam break was not broken
                if(getPrimaryClimberPosition(climbDegree) <= stopTickCount){

                    //Takes the stop tick count and subtracts the current encoder position and checks if it is greater than or equal to 15
                    if(stopTickCount - getPrimaryClimberPosition(climbDegree) >= 15){
                       
                      primaryClimber.set(climbSpeed);
                    }  
                    else{
                        primaryClimber.set(climbSpeed/3);
                    }
                } 
                else {
                    //After it has finished climbing set isClimbing to false, hasClimbed to true and isClimbingArmDown to true, aswell turn off the motors and change the arm to contract mode
                    isClimbingArmDown = true;
                    primaryClimber.set(0);
                    setClimb(ClimbEnum.ContractArm);
                }
                break;

            /**
             * Initiates Stage 2 Hab Climb
             */
            case ClimbStage2:
                System.out.println("Running Stage 2 Climber...");
                //Checks if the secondary climber arm position is greater than the negative value of the encoder if so run the motor
                if(getSecondaryClimberPosition() >= secondaryArmTickCount){
                   secondaryClimber.set(0.8);
                }

                //If the previous statement was false then halt the motors
                else{
                    secondaryClimber.set(0);
                    if(runStage3)
                        setClimb(ClimbEnum.ContractSecondary);
                }
            break;  

            /**
             * Begins stage 3 climb
             */
            case ClimbStage3:
                System.out.println("Running Stage 3 Climber...");
                //Checks if the primary climb position is less than the stage 3 stop tick count
                if(getPrimaryClimberPosition(climbDegree) <= stage3TickCount){
                    primaryClimber.set(climbSpeed);
                }
                else{
                    primaryClimber.set(0);
                }
            break;

            //This case is for contracting the arm after we have already climbed
            case ContractArm:
                System.out.println("Contracting the primary arm...");
                //Tells the program that the arm is contracting
                isContractingArm = true;

                //This if checks if we are not climbing and if the arm is still down
                if(isClimbing == false && isClimbingArmDown == true){

                    //These statement checks to make sure the belly switch was not triggered
                    if(bellySwitch.get() == false){

                        primaryClimber.set(-climbSpeed);
                    }

                    //If it is passed that point stop the motors
                    else {
                        primaryClimber.set(0);
                        hasClimbed = true;
                        isClimbing = false;

                        //Check if it is meant to run stage 2, if so run it
                       if(runStage2)
                            setClimb(ClimbEnum.ClimbStage2);
                    } 
                }

                //If the first if statement failed then stop the motors
                else {
                    primaryClimber.set(0);
                }
                
                break;

                /**
                 * This is used for contracting the secondary arm
                 */
                case ContractSecondary:
                    if(secondaryRetractionDelay){
                        Timer.delay(1.4);
                        secondaryRetractionDelay = false;
                    }
                    System.out.println("Contracting the secondary arm...");
                    if(getSecondaryClimberPosition() <= -1){
                       secondaryClimber.set(-0.65);
                    }
                    else{
                        
                        secondaryClimber.set(0);
                        Timer.delay(1);
                        setClimb(ClimbEnum.ClimbStage3);
                    }
                break;

                //This is a general method to stop the motors
                case Off:
                    primaryClimber.set(0);
                    break;
                
                //This defaults the case to the off state
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
        primaryClimber.set(power);
    }

    public void secondaryManual(double power){
        secondaryClimber.set(-power);
    }

}
