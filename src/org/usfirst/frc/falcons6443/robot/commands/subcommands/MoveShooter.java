package org.usfirst.frc.falcons6443.robot.commands.subcommands;

import org.usfirst.frc.falcons6443.robot.commands.SimpleCommand;

/**
 * @author Goirick Saha
 */
public class MoveShooter extends SimpleCommand {

    public MoveShooter() {
        super("Move Shooter System");
        requires(shooter);
    }

    @Override
    public void initialize(){

    }

    @Override
    public void execute() {
        shooter.charge();
    }

    @Override
    public boolean isFinished() {return true;}
}