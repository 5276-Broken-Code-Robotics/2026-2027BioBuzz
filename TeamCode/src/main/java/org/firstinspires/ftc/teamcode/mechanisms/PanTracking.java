
package org.firstinspires.ftc.teamcode.mechanisms;

import static java.lang.Math.PI;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.ArrayList;
import java.util.List;



public class PanTracking{







    public boolean isFar = false;

    public boolean isClose = false;
    public DcMotor pan;
    double tAngle;
    float lastOrientedPos;

    Pose2D goalPos;


    // make sure to change values so that the coordinates are where the goals actually are (when they are oriented upwards). These are placeholders.
    Pose2D redBot = new Pose2D(DistanceUnit.INCH,57.378,51.106,AngleUnit.RADIANS,0);
    Pose2D redTop = new Pose2D(DistanceUnit.INCH,57.378,92.894,AngleUnit.RADIANS,0);
    Pose2D blueBot = new Pose2D(DistanceUnit.INCH,86.622,51.106,AngleUnit.RADIANS,0);
    Pose2D blueTop = new Pose2D(DistanceUnit.INCH,86.622,92.894,AngleUnit.RADIANS,0);
    float panZero = 340;

    float panForward = 0;

    float panMax = -340;

    boolean resetting = false;
    GoBildaPinpointDriver pinpoint;



    public boolean resettingpan = false;


    //Limelight3A limelight3A;





    ElapsedTime spinCD;



    int id;
    Telemetry telemetry;

    boolean isRed;
    //getting rid of limelight for now because its cookeed
    public void init(HardwareMap hardwareMap, GoBildaPinpointDriver pinpoint, Telemetry telemetry, boolean isRed){
        pan = hardwareMap.get(DcMotorEx.class, "rot");
        //pan.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.pinpoint = pinpoint;
        this.telemetry = telemetry;
        //this.limelight3A = limelight3A;

        this.isRed = isRed;

        spinCD = new ElapsedTime();

        spinCD.reset();

    }










    int count = 0;
    public List<LLResultTypes.FiducialResult> results;



    int pos = 0;
    public void loop(){

        /*
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        limelight3A.updateRobotOrientation(orientation.getYaw(AngleUnit.DEGREES));
        LLResult result = limelight3A.getLatestResult();
        if (result != null) {
            if (result.isValid()) {
                Pose3D botpose = result.getBotpose_MT2();
                // Use botpose data
            }
        }
        telemetry.addData("LL Started ? :", limelight3A.isRunning());

        limelight3A.updateRobotOrientation(pinpoint.getHeading(AngleUnit.RADIANS) + pan.getCurrentPosition() / (panMax-panZero) * PI);

        telemetry.addData("Heading :", pinpoint.getHeading(AngleUnit.DEGREES));

        if(limelight3A.getLatestResult() != null){
            results = limelight3A.getLatestResult().getFiducialResults();
        }else{
            results = new ArrayList<>();
            telemetry.addData("LL is null unfortunately", " womp womp");
        }

        telemetry.addData("Are Detections empty ? :", results.isEmpty());



        for(int i =0; i < results.size(); i ++ ){
            telemetry.addLine("ID" + i + results.get(i).getFiducialId());
        }
       */

        if(isRed){
            if(pinpoint.getPosition().getY(DistanceUnit.INCH) >= 144/2){
                goalPos = redTop;
            }else{
                goalPos = redBot;
            }
        }else{
            if(pinpoint.getPosition().getY(DistanceUnit.INCH) >= 144/2){
                goalPos = blueTop;
            }else{
                goalPos = blueBot;
            }
        }

        double angleToAprilTag = Math.atan2((goalPos.getY(DistanceUnit.INCH) - pinpoint.getPosY(DistanceUnit.INCH)),(goalPos.getX(DistanceUnit.INCH) - pinpoint.getPosX(DistanceUnit.INCH)));


        double currAngle  = pinpoint.getHeading(AngleUnit.RADIANS);

        tAngle = turnAngle(currAngle,angleToAprilTag);



        if(Math.abs(angleToAprilTag) <= PI + 0.1){
            telemetry.addData("Moving :", "True");
        }else{
            telemetry.addData("Moving :", "False");
        }


        double targetTicks = -   tAngle* Math.abs((panMax-panZero))/(Math.PI);









        if(resettingpan){
            targetTicks = panForward;
        }


        telemetry.addData("Target Ticks : ", targetTicks);







        if(Math.abs(lastOrientedPos - targetTicks) > 1 && (spinCD.seconds() > 0.2 )){

            spinCD.reset();


            // MAKE SURE TO KEEP PROPER SIGNS
            if(targetTicks < panMax ){
                targetTicks = panMax;
            }


            if(targetTicks > panZero){
                targetTicks = panZero;
            }


            if(targetTicks > panMax || targetTicks < panZero) telemetry.addData("Capped", "Right now");


            pan.setTargetPosition((int) targetTicks);
            lastOrientedPos = (int)targetTicks;
        }





        telemetry.addData("Target Ticks", targetTicks);




        telemetry.addData("Current position on pan", pan.getCurrentPosition());
        telemetry.addData("Target position on pan", pan.getTargetPosition());




        telemetry.addData("Angle : ", angleToAprilTag * 180 / PI);
        telemetry.addData("Turn Angle : ", tAngle * 180/ PI );



        telemetry.addData("Pos X :", pinpoint.getPosX(DistanceUnit.INCH));
        telemetry.addData("Pos Y :", pinpoint.getPosY(DistanceUnit.INCH));




    }


    public void start(){
        pan.setDirection(DcMotorSimple.Direction.FORWARD);
        pan.setTargetPosition((int)panForward);
        pan.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        pan.setPower(1);
        resetting = false;
       // limelight3A.start();
    }

    public static double turnAngle(double currentHeading, double targetAngle) {
        double error = targetAngle - currentHeading;

        // Normalize to (-PI, PI]
        while (error > Math.PI)  error -= 2 * Math.PI;
        while (error <= -Math.PI) error += 2 * Math.PI;

        return error;
    }


}
