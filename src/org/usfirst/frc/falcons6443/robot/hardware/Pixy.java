package org.usfirst.frc.falcons6443.robot.hardware;
import org.usfirst.frc.falcons6443.robot.communication.P_I2C;
import edu.wpi.first.wpilibj.command.CommandGroup;



/**
 * @author Goirick Saha
 */

public class Pixy {
    P_I2C i2c = new P_I2C();
    PixyPacket pkt = i2c.getPixy();

    //public double calcDistance

}

