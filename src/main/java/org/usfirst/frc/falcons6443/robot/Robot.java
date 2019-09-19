/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.falcons6443.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.cscore.VideoMode;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc.falcons6443.robot.autonomous.AutoDrive;
import org.usfirst.frc.falcons6443.robot.autonomous.AutoMain;
import org.usfirst.frc.falcons6443.robot.autonomous.autoPathing.Pathing;
import org.usfirst.frc.falcons6443.robot.hardware.NavX;
import org.usfirst.frc.falcons6443.robot.hardware.joysticks.Xbox;
import org.usfirst.frc.falcons6443.robot.subsystems.*;
import org.usfirst.frc.falcons6443.robot.subsystems.ArmadilloClimber.ClimbEnum;
import org.usfirst.frc.falcons6443.robot.utilities.TeleopStructure;
import org.usfirst.frc.falcons6443.robot.utilities.Logger;
import org.usfirst.frc.falcons6443.robot.utilities.enums.DriveStyles;
import org.usfirst.frc.falcons6443.robot.utilities.enums.XboxRumble;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the Main.java file in the
 * project.
 */
// If you rename or move this class, update the Main.java file in the org folder
public class Robot extends TimedRobot {
    private Xbox primary;
    private Xbox secondary;

    private TeleopStructure teleop;

    private DriveTrainSystem driveTrain;
    private AssistedPlacement assistedPlacement;
    private VacuumSystem vacuum;
    private DriveStyles controlMethod = DriveStyles.Arcade;
    private ArmadilloClimber climber;

    private Pathing path;
    
    public static boolean isKillSwitchEnabled = false;
    private boolean demoMode = false;

    private List<String> pathList;

    FileWriter writer;
    int loop = 0;
    

    // Used to change speed for demo mode
    private double speedMultiplier = 1;

    public static Preferences prefs;
    private SendableChooser<DriveStyles> driveStyle;
   
    /**
     * This function is run when the robot is first started up and should be used
     * for any initialization code.
     */
    @Override
    public void robotInit() {
        primary = new Xbox(new XboxController(0)); // change controller type here
        secondary = new Xbox(new XboxController(1));
        teleop = new TeleopStructure();

        //Create new references to all the subsystems
        driveTrain = new DriveTrainSystem();
        assistedPlacement = new AssistedPlacement(driveTrain);
        vacuum = new VacuumSystem();
        climber = new ArmadilloClimber(vacuum);
        path = new Pathing(driveTrain);

        pathList = new ArrayList<>();
        initPaths();

       
        // Determines if the bot is at an event being driven by other people
        SmartDashboard.putBoolean("Demo Mode", demoMode);
        teleop.addIsManualGetterSetter(TeleopStructure.ManualControls.VACUUM, () -> vacuum.getManual(),
                (Boolean set) -> vacuum.setManual(set));

    }

    /*
     * Called when the robot first enters autonomous mode.
     */
    @Override
    public void autonomousInit() {
        //Stop Inverting the left side to allow for the correct calculations
        driveTrain.getLeftMotors().setInverted(false);
        climber.setClimb(ClimbEnum.Steady);
        //Resets the Nav angle
        NavX.get().reset();
        
        //Follows the path given as an input, in this case the path titled 'Stage1'
        try {
            path.runPath("Stage1");
        } catch (IOException e) {}
    }

    /**
     * This function is called periodically during autonomous.
     * For 2019 season put all sandstorm code in this block
     */
    @Override
    public void autonomousPeriodic() {
        climber.climb();
        
    }

    /*
     * Called when the robot first enter teleop mode.
     * This currently just swaps the camera to driver mode as opposed to vision processing
     */
    @Override
    public void teleopInit() {
       // Logger.teleopInit();

       try{
        path.stopPathing();
       }
       catch (NullPointerException e){

       }
        assistedPlacement.enableDriverMode();
        climber.setClimb(ClimbEnum.Steady);
        NavX.get().reset();
        
        vacuum.setEncoderStatus(false);
        vacuum.setSolenoid(true);
    }

    /**
     * This function is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic() {
        System.out.println("Gyro: " + NavX.get().getYaw());

        climber.climb();
        //System.out.println(assistedPlacement.servo.getAngle());

        //If the kill switch has not been pressed and has not already climbed
        if (!isKillSwitchEnabled && climber.getHasClimbed() == false && climber.getIsClimbing() == false) {
            controls();

        } else {

            //manual climber control
            //climber.manualControl(primary.rightStickY());

            // control reset
            teleop.runOncePerPress(primary.seven(), () -> isKillSwitchEnabled = false, false);

        }

        teleop.periodicEnd();
    }

    /**
     * This method is where all the driver controls go to allow for driver controlled mode in auto, thus allowing us to not have to copy code between them
     */
    private void controls() {

        /**
         * Will determine weather or not the robot should run slower if demo mode is enabled 
         */
        if(demoMode)
            speedMultiplier = 0.5;
        else
            speedMultiplier = 1;

        //Check if the driver has initated placing, if so block normal driver controls, if not proceede as normal
        if(assistedPlacement.getPlacing() == false /*&& isAdjusting == false*/){
          driveTrain.generalDrive(primary, controlMethod, speedMultiplier);
        }

        //Toggles tracking when the A button is pressed
        teleop.runOncePerPress(primary.A(), () -> assistedPlacement.enablePlacing(), false);
        teleop.runOncePerPress(primary.X(), () -> assistedPlacement.disablePlacing(), false);
        
        
        //Checks if the driver has initated hatch placement, if so actually track it
        if(assistedPlacement.getPlacing() == true){
          assistedPlacement.trackTarget();
        }

        // Drive Shifting, wasnt working, TODO: Test again
        //teleop.runOncePerPress(primary.leftBumper(), () -> driveTrain.changeSpeed(false), false);
        //teleop.runOncePerPress(primary.rightBumper(), () -> driveTrain.changeSpeed(true), false);

        if(demoMode == false){
            //Checks if the right dpad is pushed on the second controller
            if (secondary.dPadRight() || primary.dPadRight()){
                climber.secondary = true;
            }
            else{
                climber.secondary = false;
            }
        }
        
        //Allows for manual control of the secondary climber arm
        //climber.secondaryClimberManual(secondary.rightStickY());
        //Check if the climber has contracted or is contracting, if so and the combo of buttons is pressed again enable stage 2 after retraction complete
        if(climber.isContractingArm){
                teleop.runOncePerPress(secondary.dPadLeft(), () -> climber.enableStage2(), false);
        }

        if(climber.runStage2){
            teleop.runOncePerPress(secondary.dPadUp(), () -> climber.enableStage3(), false);
        }

        //Then check if the secondary button is pushed and B on the primary controller is pushed, at this point it starts the climb
        if (climber.secondary && primary.B()) {
            climber.setClimb(ArmadilloClimber.ClimbEnum.ClimbHab);
            //climber.setClimb(ClimbEnum.ClimbStage2);
        }

        //If Y is pressed then it stops the climber
        teleop.runOncePerPress(primary.Y(), () -> climber.setClimb(ArmadilloClimber.ClimbEnum.Off), false);       

        //Increments/Decrements the position by one spot each time a specific dpad is pressed
        if(demoMode == false){
            teleop.runOncePerPress(secondary.Y(), () -> vacuum.enableMovingDown(), false);
        }
        teleop.runOncePerPress(secondary.B(), () -> vacuum.enableMovingBackHatch(), false);
        teleop.runOncePerPress(secondary.A(), () -> vacuum.enableCentering(), false);

        //Make sure the bot is not in demo mode
        if(demoMode == false){
            //Manual Hatch Arm Control
            if(Math.abs(secondary.leftStickY()) > .2){
                vacuum.manual(-secondary.leftStickY());
            }
            else if(Math.abs(secondary.leftStickY()) < .2)
                vacuum.manual(0);
        }

        // if(Math.abs(secondary.rightStickY()) > .2){
        //     climber.secondaryManual(-secondary.rightStickY());
        // }
        // else if(Math.abs(secondary.rightStickY()) < .2)
        //     climber.secondaryManual(0);

        


        //teleop.off(() -> vacuum.manual(0), TeleopStructure.ManualControls.VACUUM/*, secondary.A(), secondary.B(), secondary.Y()*/);
        
        teleop.runOncePerPress(secondary.leftBumper(), () -> vacuum.toggleSuction(), false);
        teleop.runOncePerPress(secondary.rightBumper(), () -> vacuum.releaseVac(), false);
        
        //Will only run if the corresponding buttons have been pushed
        climber.climb();
        vacuum.suck();

        //Will only run if the toggle has been enabled
        vacuum.moveArmBack();
        vacuum.moveArmDown();
        vacuum.moveArmCenter();
        vacuum.moveArmUp();
        vacuum.moveArmBackHatch();

        //Alignment Controls (primary - A) (secondary - triggers)
        //__teleop.runOncePerPress(primary.A(), () -> TBDFUNCTION, false);
        //__teleop.runOncePerPress(secondary.rightTrigger(), () -> TBDFUNCTION, false);
    
    
    }

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic() {  LiveWindow.run();  }

    /*
     * Called when the robot first enters disabled mode.
     */
    @Override
    public void disabledInit(){
        /**
         * On Disabled init lock the motors stops from rolling after
         */
        driveTrain.arcadeDrive(0, 0);
        climber.setClimb(ClimbEnum.Off);
        try{
        path.stopPathing();
        }
        catch (NullPointerException e){}
    }
    /*
     * Called periodically when the robot is in disabled mode.
     */
    @Override
    public void disabledPeriodic(){ }

    /**
     * Adds wanted paths to the paths list, currently unused implementing for future use
     */
    private void initPaths(){
        pathList.add("Stage1");
        pathList.add("Stage2");
        pathList.add("Stage3");
    }
}