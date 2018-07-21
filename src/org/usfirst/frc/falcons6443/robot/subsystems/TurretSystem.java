package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.Encoders;
import org.usfirst.frc.falcons6443.robot.hardware.LimitSwitch;

public class TurretSystem extends Subsystem{

    private Spark motor;
    private Encoders encoder;
    private LimitSwitch leftSwitch;
    private LimitSwitch rightSwitch;

    private boolean isRoaming; //if true, search for target. If false, only lock on target if in view.
    private boolean lastDirectionLeft;

    public TurretSystem(){
        motor = new Spark(RobotMap.TurretMotor);
        encoder = new Encoders(RobotMap.TurretEncoderA, RobotMap.TurretEncoderB);
        leftSwitch = new LimitSwitch(RobotMap.TurretLeftSwitch);
        rightSwitch = new LimitSwitch(RobotMap.TurretRightSwitch);
        encoder.setReverseDirection(false);
        isRoaming = false;
        lastDirectionLeft = true;
    }

    @Override
    public void initDefaultCommand() { }

    public double getDistance(){ return encoder.getDistance(); }
    public void resetEncoder() { encoder.reset(); }
    public void setMotor(double power){ motor.set(power); }
    public boolean getRoaming() { return isRoaming; }
    //pair to xbox button (TeleopMode.java)
    public void setRoaming(boolean roaming) {  isRoaming = roaming; }

    //periodic function
    public void update(){
        double power;
        //is target in view?
            //if yes, move turret to center target
                //if centered, power = 0 and inform drivers and shooter (ShuffleBoard boolean)
            /*else*/ if(isRoaming){
                if(lastDirectionLeft){
                    power = -0.5;
                } else {
                    power = 0.5;
                }
            } else {
                power = 0;
        }
        if(leftSwitch.get()) {
            lastDirectionLeft = false;
            power = Math.abs(power);
        }
        if(rightSwitch.get()) {
            lastDirectionLeft = true;
            power = -Math.abs(power);
        }

        motor.set(power);
    }
}
