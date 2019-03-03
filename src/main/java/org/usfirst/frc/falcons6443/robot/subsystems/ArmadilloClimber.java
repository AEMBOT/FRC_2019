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

    private CANSparkMax leftMotor;
    private CANSparkMax rightMotor;

    private LimitSwitch bellySwitch;
    private LimitSwitch extensionBeam;

    private LEDSystem led;

    private CANEncoder leftEncoder;

    private boolean isClimbing = false;
    private boolean isClimbingArmDown = false;

    private boolean steady = true;   

    //Change to a point where the encoders will stop
    private final int stopTickCount = 285;
    private double climbSpeed = 1;
    private int armUpTickCount = 130;

    private ClimbEnum position;
    private boolean first;
    private boolean bbTriggered;

    public boolean secondary;

    private VacuumSystem vacuum;

    //Set to the diameter of the wheel in inches
    private static final double wheelDiameter = 6;

    public ArmadilloClimber(VacuumSystem vacuum) {

        //Assigns the motors to the proper mappings
        rightMotor = new CANSparkMax(RobotMap.RightClimbMotor, CANSparkMaxLowLevel.MotorType.kBrushless);
        leftMotor = new CANSparkMax(RobotMap.LeftClimbMotor, CANSparkMaxLowLevel.MotorType.kBrushless);

        led = new LEDSystem();

        isClimbing = false;
        isClimbingArmDown = false;

        extensionBeam = new LimitSwitch(RobotMap.ClimbArmExtensionBeam, false);
        bellySwitch = new LimitSwitch(RobotMap.ClimbArmBellySwitch, false);

        //maps encoders to ports
        leftEncoder = leftMotor.getEncoder();

        this.vacuum = vacuum;

        //set encoder ticks per rev
       // leftEncoder.setTicksPerRev(encoderTicks);

        //set wheel diameters
        //leftEncoder.setDiameter(wheelDiameter);

        //leftEncoder.reset();

        //Flips the left motor so it is running the right direction
        rightMotor.setInverted(true);

        position = ClimbEnum.Steady;
        first = true;
        secondary = false;
        bbTriggered = false;
    }

    public void steady(){
         if(steady){
         if(!bellySwitch.get()) {
             leftMotor.set(-.3);
             rightMotor.set(-.3);
         }else {
             leftMotor.set(0);
             rightMotor.set(0);
            } 
        }
    }

    public void enableClimb(){
        isClimbing = true;
    }

    public void enableKillSwitch(){
        isClimbing = false;
        isClimbingArmDown = false;
        Robot.isKillSwitchEnabled = true;
    }

    public double updatePosition(double climbDegree){
        return leftEncoder.getPosition() - climbDegree;
    }

    public enum ClimbEnum{
        ClimbHab, ContractArm, Steady, Off
    }

    public void setClimb(ClimbEnum num){
        position = num;
    }

    //Begin climb
    double climbDegree;
    public void climb(){
        switch(position){
            case Steady:
                steady = true;
                steady();
                break;
            case ClimbHab:
                led.enableRainbow();
                steady = false;
                if(first) climbDegree = leftEncoder.getPosition();
                first = false;

                vacuum.enableMovingDown();

                if(extensionBeam.get()) bbTriggered = true;
                if(updatePosition(climbDegree) <= stopTickCount && extensionBeam.get() == false && !bbTriggered){
                    if(stopTickCount - updatePosition(climbDegree) >= 15){
                        leftMotor.set(climbSpeed);
                        rightMotor.set(climbSpeed);
                         }  
                        else{
                        leftMotor.set(climbSpeed/3);
                        rightMotor.set(climbSpeed/3);
                        }
                } else {
                    isClimbing = false;
                isClimbingArmDown = true;
                leftMotor.set(0);
                rightMotor.set(0);
                setClimb(ClimbEnum.ContractArm);
                }
                break;
            case ContractArm:
                if(isClimbing == false && isClimbingArmDown == true){
                    if(updatePosition(climbDegree) >= armUpTickCount && bellySwitch.get() == false){
                        leftMotor.set(-climbSpeed/2);
                        rightMotor.set(-climbSpeed/2);
                        System.out.println(updatePosition(climbDegree));
                        Logger.log(LoggerSystems.Climb,"" + updatePosition(climbDegree));
                    }
                    else {
                        rightMotor.set(0);
                        leftMotor.set(0);
                    } 
                }
                else {
                    rightMotor.set(0);
                    leftMotor.set(0);
                } 
                break;
                case Off:
                    leftMotor.set(0);
                    rightMotor.set(0);
                    break;
                default:
                    position = ClimbEnum.Off;
                    break;    
            }        
        }

    public void manualControl(double power){
        leftMotor.set(power);
        rightMotor.set(power);
    }

}
