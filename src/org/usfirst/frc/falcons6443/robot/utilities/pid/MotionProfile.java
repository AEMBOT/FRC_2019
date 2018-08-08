package org.usfirst.frc.falcons6443.robot.utilities.pid;

import edu.wpi.first.wpilibj.Timer;

/*
* Equations from ChiefDelphi user 'Ether':
* http://www.chiefdelphi.com/media/papers/download/4496
* Desmos graph of distance, velocity, and acceleration (modify at will. Example values: D = 5, MaxAcceleration = 3.5):
* https://www.desmos.com/calculator/f4dpbxpolt
*/
public class MotionProfile {

    private Timer timer;
    private double T; //time to destination
    private double K1;
    private double K2;
    private double K3;
    private double MaxAcceleration;
    private double maxSpeed;

    public MotionProfile(double distance, double maxAcceleration){
        timer = new Timer();
        MaxAcceleration = maxAcceleration;
        T = Math.sqrt((2*Math.PI*distance)/ MaxAcceleration);
        K1 = (2*Math.PI)/T;
        K2 = MaxAcceleration /K1;
        K3 = 1/K1;
        maxSpeed = 2*K2;
    }

    public MotionProfile(double distance, double maxAcceleration, double maxDesiredSpeed){
        this(distance, maxAcceleration);
        if(maxSpeed > maxDesiredSpeed) {
            System.out.println("Max speed higher than desired max speed!");
        }
    }

    //start timer to start the motion profiler
    public void startTimer() { timer.start(); }

    public void stopTimer() { timer.stop(); }

    public void resetTimer() { timer.reset(); }

    //get acceleration is the acceleration you should be at
    public double getAcceleration() { return MaxAcceleration * Math.sin(K1 * timer.get()); }

    //get speed is the speed you should be at
    public double getSpeed(){
        return K2 * (1 - Math.cos(K1 * timer.get()));
    }

    //get distance is where you should be
    public double getDistance(){
        return K2 * (timer.get() - K3 * Math.sin(K1 * timer.get()));
    }

    public void printExpectedTime(){
        System.out.println("Motion profile, expected time: " + T);
    }

    public void printMaxSpeed(){
        System.out.println("Motion profile, max speed: " + maxSpeed);
    }

    public void printMaxSpeed(double desiredMaxSpeed){
        if(maxSpeed > desiredMaxSpeed) {
            System.out.println("Max speed higher than desired max speed!");
        }
        System.out.println("Motion profile, max speed: " + maxSpeed);
    }

}
