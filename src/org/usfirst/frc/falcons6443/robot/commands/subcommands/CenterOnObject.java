package org.usfirst.frc.falcons6443.robot.commands.subcommands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.falcons6443.robot.commands.autocommands.RotateToAngle;
import org.usfirst.frc.falcons6443.robot.communication.P_I2C;
import org.usfirst.frc.falcons6443.robot.utilities.PixyPacket;

/**
 * @author Goirick Saha
 */
public class CenterOnObject extends CommandGroup {
    P_I2C i2c = new P_I2C();
    PixyPacket pkt = i2c.getPixy();

    public CenterOnObject(){
        if(pkt.x != -1){
            if(pkt.x < .48 || pkt.x > .52){
                while(pkt.x < .48 || pkt.x > .52){

                    if(pkt.x < .48){ //Example code
                        addSequential(new RotateToAngle(5));
                    }
                    if(pkt.x > .52){
					    addSequential(new RotateToAngle(-5));
                    }
                    if(pkt.y == -1)//Restart if ball lost during turn
                        break;
                    pkt = i2c.getPixy();//refresh the data
                    System.out.println("XPos: " + pkt.x);//print the data
                }

            }
        }
    }
}

