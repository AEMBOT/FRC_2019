package org.usfirst.frc.falcons6443.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.falcons6443.robot.Robot;

/**
 * This class handles will choose and autonomous mode
 * based on the starting position then from there, instantiate
 * a command group based on FMS data once the game starts.
 *
 *@author Aleks Vidmantas
 */

public class AutoChooser {
    //represents the three starting robot positions
    public enum Position {
        LEFT, CENTER, RIGHT, LINE
    }

    //auto class will be created, must be CommandGroup
    CommandGroup finalAuto;
    //private Position position;


    //pass in a Position enum from Robot.java
    public AutoChooser(){//Position position){
        //this.position = position;
        choose();
    }

    //performs selection process by using a switch for which two
    //commands then choose command once fms data is received.
    private void choose(){

        Position position = (Position) Robot.autoSendable.getSelected();

        switch (position){
            //handles which code to run depending on result of the specified switch/scale
            case LEFT:
                //finalAuto = new auto command;
                break;

            case CENTER:
                //finalAuto = new auto command;
                break;

            case RIGHT:
                //finalAuto = new auto command;
                break;

            default:
                //finalAuto = new auto command;
                break;
        }
    }

    public void cancel(){
        if(!(finalAuto == null)){
            finalAuto.cancel();
        }
    }

    public CommandGroup getFinalAuto(){return finalAuto;}
}
