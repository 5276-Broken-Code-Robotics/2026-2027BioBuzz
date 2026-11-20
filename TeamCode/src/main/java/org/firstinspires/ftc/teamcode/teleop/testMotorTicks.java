package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.mechanisms.FieldRelativeDrive;
import org.firstinspires.ftc.teamcode.mechanisms.PanTracking;


@TeleOp(group = "TeleOp")

public class testMotorTicks extends OpMode{


    public DcMotor pan;
    float ticks = 0;


    public void init() {
        pan = hardwareMap.get(DcMotorEx.class, "rot");

        pan.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


    }

    @Override
    public void start(){


        pan.setDirection(DcMotorSimple.Direction.FORWARD);

        pan.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        pan.setPower(1);

        ticks = 0;
    }

    public void loop(){
        if(gamepad1.rightBumperWasPressed()){
            ticks+=10;
        }
        if(gamepad1.leftBumperWasPressed()){
            ticks-=10;
        }

        pan.setTargetPosition((int)ticks);

        telemetry.addData("Num Ticks : " , ticks);
        telemetry.addData("Pan currpos : " , pan.getCurrentPosition());
        telemetry.addData("Pan targetpos (same as ticks): " , pan.getTargetPosition());

    }



}
