package org.usfirst.frc.falcons6443.robot.hardware;

import org.usfirst.frc.falcons6443.robot.communication.P_I2C;
import org.usfirst.frc.falcons6443.robot.utilities.PixyPacket;
import java.util.function.Consumer;

/**
 * @author Goirick Saha
 */

public class Pixy {

    public static Pixy instance;

    private P_I2C i2c;
    private PixyPacket pkt;

    private final double objectWidth = 50.8; //all units here are in mm and pixels,
    private final double sensorHeight = 6.35;//the final distance will converted to inches.
    private final double focalLength = 2.8;
    private final double imagePixHeight = 199;
    private final double objYDistance = 1524; //5 feet y distance placeholder
    private final double inchScale = 0.0393701; //mm to inch converter
    private final double objDistVar = 120; //temporary object distance value, needs to be changed when measuring.
                                          //works with triangle similarity equation.

    private double objectPixWidth;
    private double distanceToObject;
    private double objXDistance;
    private double pFocalLength; //perceived focal length

    private double cameraFullDegrees = 120; //measure value //in degree units left to right
    private double totalPixyX = 1; //measure value

    private Pixy(){
        i2c = new P_I2C();
        pkt = i2c.getPixy();
        objectPixWidth = pkt.y;
    }

    /**
     * @return the one instance of this class.
     */
    public static Pixy get(){
        if (instance == null){
            instance = new Pixy();
        }
        return instance;
    }

/*    private double calcDistance() {
          distanceToObject = (focalLength*objectHeight*imagePixelHeight)/(objectPixelHeight*sensorHeight);
          return distanceToObject;
          }
*/

    //Triangle similarity equation will give X distance and not direct distance.
    private void calcDistance() {
        pFocalLength = ((objectPixWidth*objDistVar)/objectWidth);
        distanceToObject = ((objectWidth*pFocalLength)/objectPixWidth);
    }

/*    private double calcXDistance() {
          objXDistance = Math.sqrt((Math.pow(distanceToObject, 2)) - (Math.pow(objYDistance,2)));
          objXDistance = objXDistance * inchScale;
          return objXDistance;
      }
*/

    public boolean isTargetInView() {
        refreshData();
        return pkt.x!=-1;
    }

    public boolean isObjLocked() {
        refreshData();
        return pkt.x > .48 && pkt.x < .52;
    }

    public double getDistanceToObject() {
        calcDistance();
        return distanceToObject * inchScale;
    }

    public double getAngleToObject(){ //left is negative, right is positive
        refreshData();
        return (pkt.x * cameraFullDegrees / totalPixyX) - (cameraFullDegrees / 2);
    }

    public double getBuffer(){
        return (.52 * cameraFullDegrees / totalPixyX) - (cameraFullDegrees / 2);
    }

    private void refreshData(){ pkt = i2c.getPixy(); }
}

/* public void lockOnObject(Consumer<Double> consumer) {
        if(isTargetInView()){
            while(!isObjLocked()){

                if(pkt.x < .48){ //Example code
                    consumer.accept(1.0);
                    //turret.move(1);
                }
                if(pkt.x > .52){
                    consumer.accept(-1.0);
                    //turret.move(-1);
                }
                if(pkt.y == -1)//Restart if ball lost during turn
                    break;
                pkt = i2c.getPixy();//refresh the data
                System.out.println("XPos: " + pkt.x);//print the data
            }
        }
    }*/