package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.Encoders;
import org.usfirst.frc.falcons6443.robot.hardware.LimitSwitch;

public class TurretSystem extends Subsystem{

    private Spark motor;
    private Encoders encoder;
    private LimitSwitch leftSwitch;
    private LimitSwitch rightSwitch;

    private boolean isRoaming; //if true, search for target. If false, only lock on target if in view.
    private boolean movingLeft;

    public TurretSystem(){
        motor = new Spark(RobotMap.TurretMotor);
        encoder = new Encoders(RobotMap.TurretEncoderA, RobotMap.TurretEncoderB);
        leftSwitch = new LimitSwitch(RobotMap.TurretLeftSwitch);
        rightSwitch = new LimitSwitch(RobotMap.TurretRightSwitch);
        encoder.setReverseDirection(false);
        isRoaming = false;
        movingLeft = true;
        SmartDashboard.putBoolean("Centered", false);
    }

    @Override
    public void initDefaultCommand() { }

    public double getDistance(){ return encoder.getDistance(); }

    public void resetEncoder() { encoder.reset(); }

    public void setMotor(double power){ motor.set(power); }

    public boolean getRoaming() { return isRoaming; }

    public void setRoaming(boolean roaming) {  isRoaming = roaming; }

    public void roamingToggle(){ isRoaming = !isRoaming; }

    //periodic function
    public void update(){
        double power;
        //is target in view?
            //if yes, move turret to center target (pid?)
                //if centered, power = 0 and inform drivers and shooter (ShuffleBoard boolean)
            /*else*/ if(isRoaming){
                if(movingLeft){
                    power = -0.5; //negative is left, positive is right
                } else {
                    power = 0.5;
                }
            } else {
                power = 0;
        }

        if(leftSwitch.get()) {
            power = Math.abs(power);
        } else if(rightSwitch.get()) {
            power = -Math.abs(power);
        }

        movingLeft = !(power > 0);

        motor.set(power);
    }
}
