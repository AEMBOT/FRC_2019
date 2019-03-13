package org.usfirst.frc.falcons6443.robot.autonomous;

import edu.wpi.first.wpilibj.Timer;

import java.util.function.Supplier;

/*
 * A package-private class where you create all of the auto paths. Contains wait and other
 * private functions to make building auto paths easier. The auto path is selected by
 * AutoMain and only runs one path per match.
 */
class AutoPaths {

    private AutoDrive autoDrive;


    AutoPaths(AutoDrive autoDrive){
        this.autoDrive = autoDrive;

    }

    //Put initial positions, sensor resets, or other actions needed at the start of EVERY auto path
    private void begin(){
        //turret.reset();
    }

    void testPath(){
        begin();
        autoDrive.setDistance(24, true);
        waitDrive(true, true, true);

    }

    void driveToLine(){
        begin();
        autoDrive.setDistance(120, true);
        waitDrive(true, false, false);
    }

    //time in seconds
    void driveForTime(double time, double leftPower, double rightPower){
        begin();
        autoDrive.tankDrive(leftPower, rightPower);
        sleep(time);
        autoDrive.stop();
    }

    //time in seconds
    void driveForTime(double time, double power){
        begin();
        autoDrive.tankDrive(-power, power);
        sleep(time);
        autoDrive.tankDrive(0, 0);
        autoDrive.stop();
    }

    //stops the thread until the time has passed
    private void sleep(double seconds){
        long time = Math.round(seconds * 1000);
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //stops the thread and runs the periodic function until the time has passed
    private void sleepAndRun(double seconds, Runnable periodic){
        periodic.run();
        Timer t = new Timer();
        t.start();
        while (t.get() < seconds){
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            periodic.run();
        }
    }

    //stops the thread and runs the periodic function until the expression is true
    private void waitForTrue(Supplier<Boolean> expression, Runnable periodic) {
        periodic.run();
        while (!expression.get()) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            periodic.run();
        }
    }

    //the wait function used while driving
    //distance false is angle. Turret/shooter true will move those subsystems while driving
    // if set to a position or angle
    private void waitDrive(boolean distance, boolean turret, boolean shooter){
        autoDrive.first = true;
        if (distance){
            waitForTrue(() -> autoDrive.isAtDistance(), () -> {
                autoDrive.driveToDistance();
            });
        } else {
            waitForTrue(() -> autoDrive.isAtAngle(), () -> {
                autoDrive.turnToAngle();
            });
        }
        autoDrive.stop();
        autoDrive.encoderCheck.stop();
    }
}
