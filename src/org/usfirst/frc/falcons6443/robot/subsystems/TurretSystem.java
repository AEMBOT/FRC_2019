package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.Encoders;
import org.usfirst.frc.falcons6443.robot.hardware.LimitSwitch;
import org.usfirst.frc.falcons6443.robot.hardware.Pixy;
import org.usfirst.frc.falcons6443.robot.utilities.pid.PID;

public class TurretSystem {

    private VictorSP motor;
    // private Pixy pixy;
    public Encoders encoder;
    private LimitSwitch leftLimitSwitch;
    private LimitSwitch rightLimitSwitch;
    private PID pid;
    private Preferences prefs;

    private boolean movingLeft;
    private boolean isDisabled; //if true will stop motor regardless of target
    private boolean isRoaming; //if true will roam for target. if false will turn to target only if in view
    private boolean isManual = true;
    private double roamingPower = 0.5;
    private double power;
    private static final int totalTicks = 425; //update value
    private static final double totalDegrees = 180.0; //update value

    public TurretSystem() {
        motor = new VictorSP(RobotMap.TurretMotor);
        motor.setInverted(true);

//        pixy = Pixy.get();
        encoder = new Encoders(RobotMap.TurretEncoderA, RobotMap.TurretEncoderB);
        leftLimitSwitch = new LimitSwitch(RobotMap.TurretLeftSwitch);
        rightLimitSwitch = new LimitSwitch(RobotMap.TurretRightSwitch);
        prefs = Preferences.getInstance();
        pid = new PID(prefs.getDouble("Turret P", 0), prefs.getDouble("Turret I", 0),
                prefs.getDouble("Turret D", 0), prefs.getDouble("Turret Eps", 0));
        encoder.setReverseDirection(false);
        movingLeft = true;
        isDisabled = false;
        isRoaming = true;
        //      pid.setFinishedRange(pixy.getBuffer());
        pid.setMaxOutput(0.5);
        pid.setMinDoneCycles(5);
        SmartDashboard.putBoolean("Centered", false);
        SmartDashboard.putBoolean("Roaming", false);
    }

    public void manual(double power){
        double powerToGive;
        if (Math.abs(power) > .2 ) {
            powerToGive = power/2;
            System.out.println("turret: " + power/2);
            isManual = true;

        } else {
            powerToGive = 0;
            isManual = false;
        }

        if(leftLimitSwitch.get()) {
            if(powerToGive < 0) powerToGive = 0;
            //           encoder.reset();
        } else if(rightLimitSwitch.get()) {
            if(powerToGive > 0) powerToGive = 0;
            //powerToGive = -Math.abs(powerToGive);
            //           encoder.set(totalTicks);
        }

        if(powerToGive != 0) movingLeft = powerToGive < 0;

        if(!isDisabled && isManual) {
            motor.set(powerToGive);
        }
    }

    public boolean getManual(){
        return isManual;
    }

    public void setManual(boolean set){
        isManual = set;
    }

    public void off(){
        motor.set(0);
    }

    private double getDegree() { return 40; }// encoder.get() * totalDegrees / totalTicks; }

    public void disableToggle() { isDisabled = !isDisabled; }

    public void roamingToggle(){ isRoaming = !isRoaming; }

    public boolean isAtPosition(){
        return pid.isDone();
    }

    private double getTargetDegree(){
        //       return pixy.getAngleToObject(); //get from vision, 0 being center, left negative, right positive
        return 40;
    }

    //moving turret to center of target
    private void center(){
        //       if(pixy.isTargetInView()) {
        SmartDashboard.putBoolean("Roaming", false);
        double desiredDegree = getDegree() + getTargetDegree();
        pid.setDesiredValue(desiredDegree);
        power = pid.calcPID(getDegree());
//        } else {
        SmartDashboard.putBoolean("Roaming", true);
        power = 0;
        //      }
        SmartDashboard.putBoolean("Centered", false);
    }

    private void ready(){
        //if centered, set power to 0 and inform drivers (ShuffleBoard boolean) and shooter(?)
        if(pid.isDone() /*&& pixy.isObjLocked()*/) {
            motor.set(0);
            SmartDashboard.putBoolean("Centered", true);
        } else {
            SmartDashboard.putBoolean("Centered", false);
        }
    }

    public void roam(){
        if(isRoaming){
            if(movingLeft){
                power = -roamingPower; //negative is left, positive is right
            } else {
                power = roamingPower;
            }
            SmartDashboard.putBoolean("Roaming", true);
        } else {
            power = 0;
            SmartDashboard.putBoolean("Roaming", false);
        }
    }

    //periodic function
    public void update(boolean roam) {
        System.out.println("left Limit: " + leftLimitSwitch.get());
        System.out.println("right Limit: " + rightLimitSwitch.get());

    /*    if(!pixy.isTargetInView()){
            roam();
        } else if (!pixy.isObjLocked() || !pid.isDone()){
            center();
        } else {
            ready();
        }*/
        if(roam) roam();

        //checks to keep turret from hurting itself
        if(leftLimitSwitch.get()) {
            power = Math.abs(power); //check for correct sign
//            encoder.reset();
        } else if(rightLimitSwitch.get()) {
            power = -Math.abs(power); //check for correct sign
//            encoder.set(totalTicks);
        }

        if(power != 0) movingLeft = power < 0;

        if(!isDisabled && !isManual) {
            motor.set(power);
        }
    }
}
