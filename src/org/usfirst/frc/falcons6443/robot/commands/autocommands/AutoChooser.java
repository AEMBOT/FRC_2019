package org.usfirst.frc.falcons6443.robot.commands.autocommands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

/**
 * This class handles will choose and autonomous mode and
 * instantiate a command group once the game starts.
 **/

public class AutoChooser {
    //represents the three starting robot positions
    public enum Position {
        LEFT, CENTER, RIGHT, DEFAULT
    }

    //auto class will be created, must be CommandGroup
    private static SendableChooser sendable;
    private CommandGroup finalAuto;


    //pass in a Position enum from Robot.java
    public AutoChooser(){
        sendable = new SendableChooser();
        sendable.addObject("Left", Position.LEFT);
        sendable.addObject("Center", Position.CENTER);
        sendable.addObject("Right", Position.RIGHT);
        sendable.addDefault("Default", Position.DEFAULT);
    }

    private void choose(){

        Position position = (Position) sendable.getSelected();

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

    public CommandGroup getFinalAuto() {
        choose();
        return finalAuto;
    }

}
