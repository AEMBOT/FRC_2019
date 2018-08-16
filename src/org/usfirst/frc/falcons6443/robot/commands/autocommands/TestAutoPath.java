package org.usfirst.frc.falcons6443.robot.commands.autocommands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.falcons6443.robot.RobotMap;
import org.usfirst.frc.falcons6443.robot.commands.subcommands.*;

public class TestAutoPath extends CommandGroup {
    public TestAutoPath(int distance){
            addParallel(new DriveToDistance(distance)); //runs first and second command simultaneously.
            addSequential(new MoveTurret());
            addSequential(new MoveShooter()); //this command runs once the above two are finished.
    }
}
