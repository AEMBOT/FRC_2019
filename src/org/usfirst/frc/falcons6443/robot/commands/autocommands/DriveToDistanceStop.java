package org.usfirst.frc.falcons6443.robot.commands.autocommands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.commands.subcommands.*;

public class DriveToDistanceStop extends CommandGroup {
    public DriveToDistanceStop(int distance){
            addParallel(new DriveToDistance(distance)); //all three commands run at the same time
            addParallel(new MoveTurret());
            addSequential(new MoveShooter());
    }
}
