
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

    public ElapsedTime autoPausingTimer;






    public boolean isFar = false;

    public boolean isClose = false;
    public boolean autoPausing = false;
    public DcMotor pan;
    double tAngle;
    float lastOrientedPos;

    Pose2D goalPos;


    // make sure to change values so that the coordinates are where the goals actually are (when they are oriented upwards). These are placeholders.
    Pose2D redBot = new Pose2D(DistanceUnit.INCH,57,51,AngleUnit.RADIANS,0);
    Pose2D redTop = new Pose2D(DistanceUnit.INCH,57,51,AngleUnit.RADIANS,0);
    Pose2D blueBot = new Pose2D(DistanceUnit.INCH,57,51,AngleUnit.RADIANS,0);
    Pose2D blueTop = new Pose2D(DistanceUnit.INCH,57,51,AngleUnit.RADIANS,0);
    float panZero = -410;

    float panForward = 0;

    float panMax = 410;

    boolean resetting = false;
    GoBildaPinpointDriver pinpoint;



    public boolean resettingpan = false;


    Limelight3A limelight3A;





    ElapsedTime spinCD;



    int id;
    Telemetry telemetry;

    boolean isRed;
    public void init(HardwareMap hardwareMap, GoBildaPinpointDriver pinpoint, Telemetry telemetry, Limelight3A limelight3A, boolean isRed){
        pan = hardwareMap.get(DcMotorEx.class, "rot");
        pan.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.pinpoint = pinpoint;
        this.telemetry = telemetry;
        this.limelight3A = limelight3A;

        this.isRed = isRed;
        autoPausing = false;

        autoPausingTimer = new ElapsedTime();
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


        double targetTicks = -   tAngle* (panMax-panZero)/(Math.PI);






        if(Math.abs(angleToAprilTag) * 180 / PI < 30 || pinpoint.getPosY(DistanceUnit.INCH) <= 48){

            //localization : Seeing where you are on the field
            telemetry.addData("Using Limelight Data : ",  "Now");

            boolean foundOne = false;
            LLResultTypes.FiducialResult fidRes = null;
            for(int i =0; i < results.size(); i++){
                if(results.get(i).getFiducialId() == id ){
                    foundOne = true;
                    fidRes = results.get(i);
                }
            }



            telemetry.addData("Found One :", foundOne);

            if(foundOne){

                double valCalced = pan.getCurrentPosition() + fidRes.getTargetXDegrees()* (panMax-panZero) / 180;
                if(Math.abs(valCalced- targetTicks) > 5){
                    targetTicks = valCalced;
                }
            }
        }




        if(autoPausingTimer.seconds() < 1 && autoPausing){
            if(id == 24){
                targetTicks = panZero;
            }
            if(id == 20){
                targetTicks = panMax;
            }
        }else if (autoPausing){
            boolean foundOne = false;
            LLResultTypes.FiducialResult fidRes = null;
            for(int i =0; i < results.size(); i++){
                if(results.get(i).getFiducialId() == id ){
                    foundOne = true;
                    fidRes = results.get(i);
                }
            }



            telemetry.addData("Found One :", foundOne);

            if(foundOne){

                double valCalced = pan.getCurrentPosition() + fidRes.getTargetXDegrees()* (panMax-panZero) / 180;
                if(Math.abs(valCalced- targetTicks) > 5){
                    targetTicks = valCalced;
                }
            }
        }



        if(resettingpan){
            targetTicks = panForward;
        }










        if(Math.abs(lastOrientedPos - targetTicks) > 1 && Math.abs(angleToAprilTag) <= PI + 1 && spinCD.seconds() > 0.2){

            spinCD.reset();
            if(targetTicks > panMax * 2){
                targetTicks = panMax * 2;
            }
            if(targetTicks < panZero * 2){
                targetTicks = panZero * 2;
            }

            pan.setTargetPosition((int) targetTicks);
            lastOrientedPos = (int)targetTicks;
        }



        telemetry.addData("Target Ticks", targetTicks);

        /*
        List<LLResultTypes.FiducialResult> Atagresults;

        if(limelight3A.getLatestResult() != null){
            Atagresults = limelight3A.getLatestResult().getFiducialResults();
        }else{
            Atagresults = new ArrayList<>();
        }


        telemetry.addData("ATAG Results size :", Atagresults.size());
        for(int i =0; i < Atagresults.size();i++){
            if(Atagresults.get(i).getFiducialId() == 24){
                LLResultTypes.FiducialResult tag = Atagresults.get(i);
                Pose3D camPose = tag.getRobotPoseTargetSpace();

                double x = tag.getRobotPoseFieldSpace().getPosition().x;
                double y = tag.getRobotPoseFieldSpace().getPosition().y;
                double z = tag.getRobotPoseFieldSpace().getPosition().z;

                telemetry.addData("Tag ID", tag.getFiducialId());
                telemetry.addData("FidEstim Xpos", x);
                telemetry.addData("FidEstim Ypos", y );
                telemetry.addData("FidEstim Zpos", z);
            }


        }

    */



        telemetry.addData("Current position on pan", pan.getCurrentPosition());
        telemetry.addData("Target position on pan", pan.getTargetPosition());




        telemetry.addData("Angle : ", angleToAprilTag * 180 / PI);
        telemetry.addData("Turn Angle : ", tAngle * 180/ PI );



        telemetry.addData("Pos X :", pinpoint.getPosX(DistanceUnit.INCH));
        telemetry.addData("Pos Y :", pinpoint.getPosY(DistanceUnit.INCH));








        List<LLResultTypes.FiducialResult> Atagresults = limelight3A.getLatestResult().getFiducialResults();

        for(int i =0; i < Atagresults.size();i++){
            if(Atagresults.get(i).getFiducialId() == 24){
                LLResultTypes.FiducialResult tag = Atagresults.get(i);
                Pose3D camPose = tag.getRobotPoseFieldSpace();

                double x = tag.getRobotPoseFieldSpace().getPosition().x;
                double y = tag.getRobotPoseFieldSpace().getPosition().y;
                double z = tag.getRobotPoseFieldSpace().getPosition().z;

                telemetry.addData("Tag ID", tag.getFiducialId());
                telemetry.addData("FidEstim Xpos", x);
                telemetry.addData("FidEstim Ypos", y );
                telemetry.addData("FidEstim Zpos", z);
            }
        }




        telemetry.addData("According to pantracking we are at : ", pinpoint.getPosX(DistanceUnit.INCH) + " " + pinpoint.getPosY(DistanceUnit.INCH) + " At an angle of " +pinpoint.getHeading(AngleUnit.RADIANS));





    }


    public void start(){
        pan.setDirection(DcMotorSimple.Direction.FORWARD);
        pan.setTargetPosition((int)panForward);
        pan.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        pan.setPower(1);
        resetting = false;
        limelight3A.start();
    }

    public static double turnAngle(double currentHeading, double targetAngle) {
        double error = targetAngle - currentHeading;

        // Normalize to (-PI, PI]
        while (error > Math.PI)  error -= 2 * Math.PI;
        while (error <= -Math.PI) error += 2 * Math.PI;

        return error;
    }


}
