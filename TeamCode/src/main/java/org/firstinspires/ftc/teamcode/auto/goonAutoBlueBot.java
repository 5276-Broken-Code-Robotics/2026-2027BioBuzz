package org.firstinspires.ftc.teamcode.auto;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
@Autonomous(group = "Blue Auto")
public class goonAutoBlueBot extends OpMode{
    private DcMotor fr;

    private DcMotor fl;

    private DcMotor br;
    private DcMotor bl;
    private ElapsedTime timer;
    private GoBildaPinpointDriver pinpoint;
    @Override
    public void init() {
        fr = hardwareMap.get(DcMotor.class, "fr");
        fl = hardwareMap.get(DcMotor.class, "fl");
        bl = hardwareMap.get(DcMotor.class, "bl");
        br = hardwareMap.get(DcMotor.class, "br");
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class,"pinpoint");
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.REVERSED,
                GoBildaPinpointDriver.EncoderDirection.REVERSED);

    }
    @Override
    public void start() {
        timer = new ElapsedTime();
        pinpoint.setPosition( new Pose2D(DistanceUnit.INCH,109, 9,AngleUnit.RADIANS, Math.toRadians(90)));
    }

    public void loop(){
        if (timer.seconds()<0.5){
            fr.setPower(-1);
            fl.setPower(1);
            bl.setPower(1);
            br.setPower(-1);
        }else{
            fr.setPower(0);
            fl.setPower(0);
            bl.setPower(0);
            br.setPower(0);
        }
        telemetry.addData("Angle : ", pinpoint.getHeading(AngleUnit.DEGREES));


        telemetry.addData("Coordinates: ", pinpoint.getPosX(DistanceUnit.INCH) + " " + pinpoint.getPosY(DistanceUnit.INCH) + " At an angle of " +pinpoint.getHeading(AngleUnit.RADIANS));
        //telemetry.update();

        telemetry.update();

    }
}
