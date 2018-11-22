package org.usfirst.frc.falcons6443.robot.commands.subcommands;

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
