package org.usfirst.frc.falcons6443.robot.utilities.enums;

public enum Subsystems {
    Drive(0);

    private int value;

    Subsystems(int Value){
        value = Value;
    }

    public int getValue(){
        return value;
    }
}
