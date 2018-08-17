package org.usfirst.frc.falcons6443.robot.commands.subcommands;

import org.usfirst.frc.falcons6443.robot.commands.SimpleCommand;

/**
 * @author Goirick Saha
 */
public class MoveTurret extends SimpleCommand {

    public MoveTurret(){
        super("Move Turret System");
        requires(turret);
    }

    @Override
    public void initialize() {   }

    @Override
    public void execute() {
        turret.roam();
    }

    @Override
    public boolean isFinished() {return turret.isAtPosition();}
}
