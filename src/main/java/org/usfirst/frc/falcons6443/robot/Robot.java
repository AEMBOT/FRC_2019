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

import org.usfirst.frc.falcons6443.robot.autonomous.AutoDrive;
import org.usfirst.frc.falcons6443.robot.autonomous.AutoMain;
import org.usfirst.frc.falcons6443.robot.hardware.joysticks.Xbox;
import org.usfirst.frc.falcons6443.robot.subsystems.*;
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
    private int loopCount = 0;
    private boolean hasLanded = false;
    private double[] joystickArray = { 0.03937007859349251, 0.11023622006177902, 0.11023622006177902,
            0.21259842813014984, 0.27559053897857666, 0.27559053897857666, 0.5196850299835205, 0.5196850299835205,
            0.6456692814826965, 0.6614173054695129, 0.6614173054695129, 0.6771653294563293, 0.8110235929489136,
            0.8110235929489136, 0.8897637724876404, 0.913385808467865, 0.913385808467865, 0.960629940032959,
            0.960629940032959, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
            1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.9921259880065918, 0.9921259880065918, 0.8661417365074158,
            0.7322834730148315, 0.6929134130477905, 0.7007874250411987, 0.7086614370346069, 0.7086614370346069,
            0.7007874250411987, 0.7007874250411987, 0.6535432934761047, 0.4645669162273407, 0.4645669162273407,
            0.4724409580230713, 0.4724409580230713, 0.4724409580230713, 0.4566929042339325, 0.4488188922405243,
            0.4330708682537079, 0.3937007784843445, 0.3937007784843445, 0.3937007784843445, 0.3858267664909363,
            0.3858267664909363, 0.31496062874794006, 0.31496062874794006, 0.21259842813014984, 0.21259842813014984,
            0.15748031437397003, 0.12598425149917603, 0.10236220806837082, 0.07874015718698502, 0.07874015718698502,
            0.07086614519357681, 0.07086614519357681, 0.06299212574958801, 0.06299212574958801, 0.06299212574958801,
            0.04724409431219101, 0.03937007859349251, 0.023622047156095505, 0.015748031437397003, 0.007874015718698502,
            0.007874015718698502, 0.007874015718698502, 0.007874015718698502, 0.007874015718698502 };
    private TeleopStructure teleop;
    private DriveTrainSystem driveTrain;
    private AssistedPlacement assistedPlacement;
    private AutoDrive autoDrive;
    private AutoMain autoMain;

    private LEDSystem led;

    // private ArmadilloClimberTest climber;
    private VacuumSystem vacuum;
    private DriveStyles controlMethod;
    private ArmadilloClimber climber;

    private boolean isLaunching = false;
    private boolean isAdjusting = false;

    public static Preferences prefs;
    private SendableChooser<DriveStyles> driveStyle;
    public static boolean isKillSwitchEnabled = false;

    private boolean armOut;
    private boolean babyMode = false;
    private Timer encoderResetTimer;

    /**
     * This function is run when the robot is first started up and should be used
     * for any initialization code.
     */
    @Override
    public void robotInit() {
        primary = new Xbox(new XboxController(0)); // change controller type here
        secondary = new Xbox(new XboxController(1));
        teleop = new TeleopStructure();
        
       
        driveTrain = new DriveTrainSystem();
        assistedPlacement = new AssistedPlacement(driveTrain);
        vacuum = new VacuumSystem();
        climber = new ArmadilloClimber(vacuum);
        led = ArmadilloClimber.getLED();

        // autoDrive = new AutoDrive();
        // autoMain = new AutoMain(autoDrive);
        // CameraServer.getInstance().putVideo();
        // format 1 is kMJPEG
        // VideoMode vm = new VideoMode(1, 640, 480, 60);
        // CameraServer.getInstance().startAutomaticCapture().setVideoMode(vm);

        driveStyle = new SendableChooser<DriveStyles>();
        driveStyle.addOption("Tank", DriveStyles.Tank);
        driveStyle.addOption("Arcade", DriveStyles.Arcade);
        driveStyle.addOption("RC", DriveStyles.RC);
        driveStyle.addOption("Curve", DriveStyles.Curve);
        driveStyle.setDefaultOption("Arcade", DriveStyles.Arcade);

        SmartDashboard.putData("driveStyle", driveStyle);

        autoDrive = new AutoDrive();
        autoMain = new AutoMain(autoDrive);
        // CameraServer.getInstance().putVideo();
        // format 1 is kMJPEG
        VideoMode vm = new VideoMode(1, 640, 480, 60);
        // CameraServer.getInstance().startAutomaticCapture().setVideoMode(vm);

        SmartDashboard.putBoolean("Baby Mode", babyMode);
        armOut = false;
        controlMethod = (DriveStyles) driveStyle.getSelected();
        teleop.addIsManualGetterSetter(TeleopStructure.ManualControls.VACUUM, () -> vacuum.getManual(),
                (Boolean set) -> vacuum.setManual(set));
    }

    /*
     * Called when the robot first enters autonomous mode.
     */
    @Override
    public void autonomousInit() {
        // Starts inits logger and starts the auto path
        Logger.autoInit();
        // teleop.addIsManualGetterSetter(TeleopStructure.ManualControls.VACUUM, () ->
        // vacuum.getManual(), (Boolean bool) -> vacuum.setManual(bool));
        // autoMain.runAutoPath();

        led.enableDefault();
        vacuum.toggleSuction();
        assistedPlacement.enableDriverMode();
        loopCount = 0;

    }

    /**
     * This function is called periodically during autonomous.
     * For 2019 season put all sandstorm code in this block
     */
    @Override
    public void autonomousPeriodic() {
        vacuum.suck();
        climber.climb();
        

        //Launches the robot off of hab level 2 when pressed
        teleop.runOncePerPress(primary.eight(), () -> isLaunching = true, false);

        //Checks if the robot has already launched
        if(isLaunching && hasLanded != true)
            land();
        else
            controls();
        
    }

    /*
     * Called when the robot first enter teleop mode.
     * This currently just swaps the camera to driver mode as opposed to vision processing
     */
    @Override
    public void teleopInit() {
        Logger.teleopInit();

        led.enableDefault();
        assistedPlacement.enableDriverMode();
        
        vacuum.setEncoderStatus(false);
        vacuum.setSolenoid(true);

    }

    /**
     * This function is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic() {
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
     * Method Called to drive the robot off the level 2 hab
     * Simulates driver control and replays it to the bot
     */
    private void land() {

        driveTrain.arcadeDrive(0, joystickArray[loopCount]*0.8);
        loopCount++;
        if (loopCount == joystickArray.length - 25) {
            hasLanded = true;
            isLaunching = false;
            driveTrain.arcadeDrive(0, -0.05);
        }
    }

    /**
     * This method is where all the driver controls go to allow for driver controlled mode in auto, because sandstorm
     */
    private void controls() {

        //Check if the driver has initated placing, if so block normal driver controls, if not proceede as normal
        if(assistedPlacement.getPlacing() == false /*&& isAdjusting == false*/){
          driveTrain.generalDrive(primary, controlMethod);
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

        //Checks if the right dpad is pushed on the second controller
        if (secondary.dPadRight() || primary.dPadRight()){
            climber.secondary = true;
        }
        else{
            climber.secondary = false;
        }
        
        //Allows for manual control of the secondary climber arm
        climber.secondaryClimberManual(secondary.rightStickY());

        //Then check if the secondary button is pushed and B on the primary controller is pushed, at this point it starts the climb
        if (climber.secondary && primary.B()) {
            
            //Check if the climber has contracted or is contracting, if so and the combo of buttons is pressed again enable stage 2 after retraction complete
            if(climber.isContractingArm)
                climber.enableStage2();
            
            //If this is not the case than proceed as normal and begin normal hab climb
            else
                climber.setClimb(ArmadilloClimber.ClimbEnum.ClimbHab);

            armOut = true;
            vacuum.enableMovingDown();
        }

        //If Y is pressed then it stops the climber
        teleop.runOncePerPress(primary.Y(), () -> climber.setClimb(ArmadilloClimber.ClimbEnum.Off), false);       

        //Increments/Decrements the position by one spot each time a specific dpad is pressed
        teleop.runOncePerPress(secondary.Y(), () -> vacuum.enableMovingDown(), false);
        teleop.runOncePerPress(secondary.B(), () -> vacuum.enableMovingBack(), false);
        teleop.runOncePerPress(secondary.A(), () -> vacuum.enableCentering(), false);

        //Manual Hatch Arm Control
          if(Math.abs(secondary.leftStickY()) > .2){
             vacuum.manual(-secondary.leftStickY());
         }
         else if(Math.abs(secondary.leftStickY()) < .2)
             vacuum.manual(0);


        //teleop.off(() -> vacuum.manual(0), TeleopStructure.ManualControls.VACUUM/*, secondary.A(), secondary.B(), secondary.Y()*/);
        
        teleop.runOncePerPress(secondary.leftBumper(), () -> vacuum.toggleSuction(), false);
        teleop.runOncePerPress(secondary.rightBumper(), () -> vacuum.releaseVac(), false);
        
        //Allows for manual control of the secondary climber
        climber.secondaryClimberManual(secondary.rightStickY());

        //Will only run if the corresponding buttons have been pushed
        climber.climb();
        vacuum.suck();

        //Will only run if the toggle has been enabled
        vacuum.moveArmBack();
        vacuum.moveArmDown();
        vacuum.moveArmCenter();

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
        try{
            Logger.printSpace();
        } catch (Exception e){
            System.out.println("Failed to print storage");
        }
        Logger.disabled();
    }
    /*
     * Called periodically when the robot is in disabled mode.
     */
    @Override
    public void disabledPeriodic(){    }
}