package org.usfirst.frc.falcons6443.robot.subsystems;

import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.hardware.pneumatics.Piston;

public class IntakeSystem extends Subsystem {

    private Piston piston; //check direction
    private VictorSP motor; //ditto

    private boolean isManual;

    public IntakeSystem() {
        piston = new Piston(RobotMap.IntakePistonPort);
        motor = new VictorSP(RobotMap.IntakeMotor);
    }

    public void movePistonOut(){ piston.out(); }

    public void movePistonIn(){ piston.in(); }

    public void intake(){ motor.set(.7); }

    public void off(){ motor.set(0); }

    public void manual(double motorSpeed){ motor.set(motorSpeed); }

    public boolean getManual(){ return isManual; }

    public void setManual(boolean manual) { isManual = manual; }

    @Override
    public void initDefaultCommand() { }

}