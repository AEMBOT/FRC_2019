package org.usfirst.frc.falcons6443.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.falcons6443.robot.commands.SimpleCommand;

import java.util.ArrayList;

/**
 * Simple class allowing for easy switching/testing of autos
 *
 * @author Aleks Vidmantas
 */
public class DashboardAuto {

    SendableChooser<Command> autoCommands;

    /*Shouldn't run when selected, hopefully*/
    public DashboardAuto(){
        autoCommands.addDefault("Null", null);
    }

}
