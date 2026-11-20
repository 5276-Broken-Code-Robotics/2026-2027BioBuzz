package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.mechanisms.FieldRelativeDrive;
import org.firstinspires.ftc.teamcode.mechanisms.PanTracking;


@TeleOp(group = "TeleOp")

public class blueBot extends  OpMode{






        double initpos = 0f;

        private DcMotor fr;

        private DcMotor fl;

        private DcMotor br;
        private DcMotor bl;



        PanTracking panTracking;
        FieldRelativeDrive fieldDrive;
        ElapsedTime elapsedTime;

        private DcMotor intake;





        int state = 0;

        //0 : looking for
        //1 : orienting correctly


        ElapsedTime timer;



        double positionnecessary = 0f;




        //Pose aprilTagRed = new Pose(144,144 + 15);
        //Pose aprilTagBlue = new Pose(0,144 + 15);


        ElapsedTime rewindTimer;

        GoBildaPinpointDriver pinpoint;
        private Servo smallGate;
        private Servo bigGate;
        private DcMotor flywheelBig;
        private DcMotor flywheelSmall;


        Limelight3A limelight;
        boolean rewinding = false;

        //Pose2D startPose = new Pose2D(DistanceUnit.INCH,144 - 110, 70, AngleUnit.RADIANS,Math.toRadians(90)); // Lowest (Third Set) of Artifacts from the Spike Mark.

        int num = 0;
        public void init() {

            panTracking = new PanTracking();

            rewindTimer = new ElapsedTime();
            rewindTimer.reset();

            num = 0;

//        follower = Constants.createFollower(hardwareMap);
//
//        follower.setPose(new Pose(0,144));


            fieldDrive = new FieldRelativeDrive();








            timer = new ElapsedTime();
            fr = hardwareMap.get(DcMotor.class, "fr");
            fl = hardwareMap.get(DcMotor.class, "fl");
            bl = hardwareMap.get(DcMotor.class, "bl");
            br = hardwareMap.get(DcMotor.class, "br");


            intake = hardwareMap.get(DcMotor.class, "intake");
            //limelight = hardwareMap.get(Limelight3A.class,"limelight");
            pinpoint = hardwareMap.get(GoBildaPinpointDriver.class,"pinpoint");
            smallGate=hardwareMap.get(Servo.class, "smallGate");
            bigGate=hardwareMap.get(Servo.class, "bigGate");
            flywheelSmall = hardwareMap.get(DcMotor.class, "smallFlywheel");
            flywheelBig = hardwareMap.get(DcMotor.class, "bigFlywheel");
            flywheelBig.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            flywheelSmall.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            flywheelSmall.setDirection(DcMotorSimple.Direction.REVERSE);

            flywheelBig.setDirection(DcMotorSimple.Direction.REVERSE);

            pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.REVERSED,
                    GoBildaPinpointDriver.EncoderDirection.REVERSED);
            //pinpoint.resetPosAndIMU();



            //intake.setDirection(DcMotor.Direction.REVERSE);


            timer.reset();
            elapsedTime = new ElapsedTime();
            elapsedTime.reset();



            fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);



            state=  0;
            intake.setPower(0);

            fieldDrive.init(hardwareMap, gamepad1, pinpoint);
            panTracking.init(hardwareMap,pinpoint,telemetry,false);

        }

        @Override
        public void start(){


            //CHANGE THIS
//this needs to be changed because this is temporary for only blue bot side
            pinpoint.setPosition(new Pose2D(DistanceUnit.INCH,109, 12, AngleUnit.RADIANS,Math.toRadians(270)));
            panTracking.start();
            intake.setPower(1);



        }
        double val = 0.6;

        public void breaks(){
        }


        public void loop(){


            panTracking.loop();
            if(gamepad2.a){
                //rewindTimer.reset();
                if(intake.getPower() == 1 )intake.setPower(-1);
                //rewinding = true;
            }else{
                if(intake.getPower() == -1)intake.setPower(1);
            }

            if(gamepad1.dpadUpWasPressed()){
                pinpoint.setHeading(0,AngleUnit.RADIANS);
            }
            flywheelBig.setPower(.6);
            flywheelSmall.setPower(.6);


            if(gamepad1.right_trigger>.1) {
                bigGate.setPosition(.5);
                smallGate.setPosition(.5);
            }else{
                bigGate.setPosition(0);
                smallGate.setPosition(0);
            }

            if(gamepad2.y){
                fl.setPower(0.0);
                fr.setPower(0.0);
                br.setPower(0.0);
                bl.setPower(0.0);
            }else{
                fieldDrive.driveFieldRelative(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
                //Shoundn't left stick y be negative and the other two be positive???? IDK ill ask abe today
                // -Eli 11/19


                if(gamepad1.left_stick_y == 0 && gamepad1.left_stick_x == 0 && gamepad1.right_stick_x == 0)fieldDrive.driveFieldRelative( -gamepad2.left_stick_y/4,   gamepad2.left_stick_x/4,  gamepad2.right_stick_x/4);

            }










            telemetry.addData("Angle : ", pinpoint.getHeading(AngleUnit.DEGREES));


            telemetry.addData("Coordinates: ", pinpoint.getPosX(DistanceUnit.INCH) + " " + pinpoint.getPosY(DistanceUnit.INCH) + " At an angle of " +pinpoint.getHeading(AngleUnit.RADIANS));
            //telemetry.update();

            telemetry.update();
        }




}
