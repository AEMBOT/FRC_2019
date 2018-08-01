package org.usfirst.frc.falcons6443.robot.hardware;

import edu.wpi.first.wpilibj.DigitalInput;

public class LimitSwitch {
    private DigitalInput limit;
    private boolean inverted;

    public LimitSwitch(int channel){
        limit = new DigitalInput(channel);
    }

    // limit.get() is naturally inverted
    public boolean get() {
        if(inverted) return  limit.get();
        else return !limit.get();
    }

    public void setInverted(boolean inverted) { this.inverted = inverted; }
}
