package org.usfirst.frc.falcons6443.robot.communication;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import org.usfirst.frc.falcons6443.robot.utilities.PixyPacket;

/**
 * @author Goirick Saha
 */
public class P_I2C {
    private static I2C Wire = new I2C(Port.kOnboard, 4);
    private static final int MAX_BYTES = 32;

    public void write(String input){
        char[] CharArray = input.toCharArray();
        byte[] WriteData = new byte[CharArray.length];
        for (int i = 0; i < CharArray.length; i++) {
            WriteData[i] = (byte) CharArray[i];
        }
        Wire.transaction(WriteData, WriteData.length, null, 0);

    }

    public PixyPacket getPixy(){
        String info[] = read().split("\\|");
        PixyPacket pkt = new PixyPacket();
        if(info[0].equals("none") || info[0].equals("")){
            pkt.x = -1;
            pkt.y = -1;
            pkt.area = -1;
        }else if(info.length == 3){
            pkt.x = Double.parseDouble(info[0]);
            pkt.y = Double.parseDouble(info[1]);
            pkt.area = Double.parseDouble(info[2]);
        }
        return pkt;
    }

    private String read(){
        byte[] data = new byte[MAX_BYTES];
        Wire.read(4, MAX_BYTES, data);
        String output = new String(data);
        int pt = output.indexOf((char)255);
        return (String) output.subSequence(0, pt < 0 ? 0 : pt);
    }
}

