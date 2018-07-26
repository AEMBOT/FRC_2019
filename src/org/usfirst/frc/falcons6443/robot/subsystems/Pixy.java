package org.usfirst.frc.falcons6443.robot.subsystems;
import org.usfirst.frc.falcons6443.robot.communication.P_I2C;
import org.usfirst.frc.falcons6443.robot.utilities.PixyPacket;


/**
 * @author Goirick Saha
 */

public class Pixy {

    P_I2C i2c = new P_I2C();
    PixyPacket pkt = i2c.getPixy();

    private final double objectHeight = 50.8; //all units here are in mm and pixels,
    private final double sensorHeight = 6.35;//the final distance will converted to inches.
    private final double focalLength = 2.8;
    private final double imagePixelHeight = 199;
    private final double objVerticalDistance = 1524;
    private final double inchScale = 0.04; //multiple by 0.04 inches

    private double objectPixelHeight = pkt.y;
    private double distanceToObject;
    private double objXDistance;

    public Pixy(){

    }

    private double calcDistance() {
        distanceToObject = (focalLength*objectHeight*imagePixelHeight)/(objectPixelHeight*sensorHeight);
        distanceToObject = distanceToObject * inchScale; //converting mm to inches
        return distanceToObject;
    }

    private double calcXDistance() {
        objXDistance = Math.sqrt((Math.pow(distanceToObject, 2)) - (Math.pow(objVerticalDistance,2)));
        objXDistance = objXDistance * inchScale;
        return objXDistance;
    }

    private void centerOnObject() {
        if(pkt.x != -1){
            if(pkt.x < .48 || pkt.x > .52){
                while(pkt.x < .48 || pkt.x > .52){

                    if(pkt.x < .48){ //Example code
                        //rotate turret right
                    }
                    if(pkt.x > .52){
                        //rotate turret left
                    }
                    if(pkt.y == -1)//Restart if ball lost during turn
                        break;
                    pkt = i2c.getPixy();//refresh the data
                    System.out.println("XPos: " + pkt.x);//print the data
                }

            }
        }
    }

    private boolean targetInView() {
        if(pkt.x!=-1){
            return true;
        } else {
            return false;
        }
    }

    public boolean getTargetInView() {
        return targetInView();
    }

    public double getDistanceToObject() {
        return distanceToObject;
    }

    public double getObjXDistance() {
        return objXDistance;
    }


}

