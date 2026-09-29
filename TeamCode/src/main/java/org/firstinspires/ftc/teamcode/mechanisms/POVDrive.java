
package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;


public class POVDrive{

    // This declares the four motors needed
    DcMotor leftFront;
    DcMotor rightFront;
    DcMotor leftBack;
    DcMotor rightBack;



    public void init(HardwareMap hwMap) {


        leftFront = hwMap.get(DcMotor.class, "fl");
        rightFront = hwMap.get(DcMotor.class, "fr");
        leftBack = hwMap.get(DcMotor.class, "bl");
        rightBack = hwMap.get(DcMotor.class, "br");

        // We set the left motors in reverse which is needed for drive trains where the left
        // motors are opposite to the right ones.

        leftBack.setDirection(DcMotor.Direction.REVERSE);
        leftFront.setDirection(DcMotor.Direction.REVERSE);


    }


    public void drive(double forward, double strafe, double rotate) {
        double flPower = forward + strafe + rotate;
        double frPower = forward - strafe - rotate;
        double blPower = forward - strafe + rotate;
        double brPower = forward + strafe - rotate;

        double maxPower = 1.0;
        double maxSpeed = 1.0;

        maxPower = Math.max(maxPower, Math.abs(flPower));
        maxPower = Math.max(maxPower, Math.abs(frPower));
        maxPower = Math.max(maxPower, Math.abs(blPower));
        maxPower = Math.max(maxPower, Math.abs(brPower));

        leftFront.setPower((maxSpeed * (flPower / maxPower)));
        rightFront.setPower((maxSpeed * (frPower / maxPower)));
        leftBack.setPower((maxSpeed * (blPower / maxPower)));
        rightBack.setPower((maxSpeed * (brPower / maxPower)));
    }

}