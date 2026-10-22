package org.firstinspires.ftc.teamcode.mechanisms;


import com.pedropathing.math.*;
import com.pedropathing.math.Pose;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

public class HoodedShooter {

    public FinalFreeSortHSV freeSort;


    private Servo tilt;

    ElapsedTime shootTimer;


    public char p1;
    public char p2;
    public char p3;


    int id = 0;
    ElapsedTime elapsedTime;






    public boolean shotbegan;
    private DcMotor flywheel1;

    private DcMotor flywheel2;



    //0.75 for close sec 2

    public double flywheelPower;

    public double headingTiltPos;
    Telemetry telemetry;


    boolean resetting = false;

    ElapsedTime waitrotate;

    double angleToAprilTag = 0;
    Pose2D aprilTagRed = new Pose2D(DistanceUnit.INCH,144,144, AngleUnit.RADIANS,0);
    Pose2D aprilTagBlue = new Pose2D(DistanceUnit.INCH,0,144, AngleUnit.RADIANS,0);
    boolean rotated = false;
    Pose2D currentAprilTagPos;


    int lastOrientedPos;
    boolean isAutoShot;

    Pose initPose;
    int currentID;
    Limelight3A limelight;

    GoBildaPinpointDriver pinpoint;

    public ElapsedTime pinpointUpdatePause;
    // (160 (vbig gear) / 18 (small gear)  * 20 (degrees of rotation))/300
    double maxTilt = 0.59;
    public void init(HardwareMap hardwareMap, Telemetry telemetry, GoBildaPinpointDriver pinpoint, int id) {


        shootTimer = new ElapsedTime();

        freeSort = new FinalFreeSortHSV();

        freeSort.init(hardwareMap, telemetry);

        shootTimer.reset();

        pinpointUpdatePause = new ElapsedTime();
        tilt = hardwareMap.get(Servo.class, "tilt");
        flywheel1 = hardwareMap.get(DcMotor.class, "flywheel1");
        flywheel2 = hardwareMap.get(DcMotor.class, "flywheel2");
        limelight = hardwareMap.get(Limelight3A.class,"limelight");


        this.id = id;


        this.pinpoint = pinpoint;


        flywheel1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheel2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        flywheel2.setDirection(DcMotorSimple.Direction.FORWARD);
        flywheel1.setDirection(DcMotorSimple.Direction.REVERSE);
        waitrotate = new ElapsedTime();
        resetting = false;




        flywheelPower = 0.3;
        headingTiltPos = 0.12;
        panTracking = new PanTracking();


        if(id == 24){
            currentAprilTagPos = aprilTagRed;
        }else{
            currentAprilTagPos = aprilTagBlue;
        }


        currentID = id;


        panTracking.init(hardwareMap,pinpoint,telemetry,limelight,id,currentAprilTagPos);



        //limelight.start();
        //DISABLED FOR POWER PURPOSES, DO NOT FORGET TO REENABLE


        this.telemetry = telemetry;




        elapsedTime = new ElapsedTime();
    }

    public void start(){
        pinpointUpdatePause.reset();
        panTracking.start();

    }

    public void loop()
    {



        telemetry.addData("dex for the firing loop", dex);

        telemetry.addData("Pattern Arr : ", curpat[0]+ " , ", curpat[1], " , ", curpat[2]);


        if(id == 24){
            currentAprilTagPos = aprilTagRed;
        }else{
            currentAprilTagPos = aprilTagBlue;
        }


        //Alter, this is just for red goal
        if((currentAprilTagPos.getY(DistanceUnit.INCH)-pinpoint.getPosY(DistanceUnit.INCH)) * (currentAprilTagPos.getY(DistanceUnit.INCH)-pinpoint.getPosY(DistanceUnit.INCH)) + (currentAprilTagPos.getX(DistanceUnit.INCH)-pinpoint.getPosX(DistanceUnit.INCH))*(currentAprilTagPos.getX(DistanceUnit.INCH)-pinpoint.getPosX(DistanceUnit.INCH)) < 3800 ){
            headingTiltPos = 0.051;
        }else if(pinpoint.getPosY(DistanceUnit.INCH) <= 36) {
            headingTiltPos = 0.075;
        }else{
            headingTiltPos = 0.1;
        }


        freeSort.loop();

        panTracking.loop();


        //Finish the results script
        for(int i =0; i < panTracking.results.size(); i++){
            if(panTracking.results.get(i).getFiducialId() == 21){
                curpat = new char[]{'g','p','p'};
            }
            if(panTracking.results.get(i).getFiducialId() == 22){
                curpat = new char[]{'p','g','p'};

            }
            if(panTracking.results.get(i).getFiducialId() == 23){
                curpat = new char[]{'p','p','g'};
            }
        }



        if(firingPat)FiringAPattern();

        p1 = freeSort.pos1;
        p2 = freeSort.pos2;
        p3 = freeSort.pos3;

        /*


        telemetry.addData("Flywheel power", flywheel1.getPower() + " " + flywheel2.getPower());
        telemetry.addData("Pan position : ", pan.getCurrentPosition());



         */

        if(pinpointUpdatePause.seconds() > 0.3)pinpoint.update();



        //Orienting();




        flywheel1.setPower(flywheelPower);
        flywheel2.setPower(flywheelPower);

        if(panTracking.autoPausing){
            tilt.setPosition(0.0815);

        }else{
            tilt.setPosition(headingTiltPos);
        }

        //telemetry.update();
    }



    public void AutoBeginShot(boolean b, boolean a){

    }



    public boolean firingPat = false;


    char[] curpat = {'p', 'p', 'p'};

    public void firePattern(){
        firingPat = true;
        dex = 0;
        shootTimer.reset();
    }




    int dex = 0;

    public void FiringAPattern(){
        if(dex < 3){
            if(shootTimer.seconds() > freeSort.shootDur+0.2){
                if(dex < curpat.length)FireColor(curpat[dex]);
                dex++;
                shootTimer.reset();
            }
        }else{
            if(freeSort.pos1 == 'e' && freeSort.pos2 == 'e' && freeSort.pos3 == 'e') {
                firingPat = false;
                dex = 0;
            }else{
                firingPat = true;
                dex = 0;
            }
        }

    }


    public PanTracking panTracking;



    public void Fire(int num){
        if(!freeSort.shooting)freeSort.shoot(num);
    }

    public void FireColor(char c){

        if(c == 'p'){
            if(freeSort.pos1 == 'p'){
                freeSort.shoot(1);
            }else
            if(freeSort.pos2 == 'p'){
                freeSort.shoot(2);
            }else
            if(freeSort.pos3 == 'p'){
                freeSort.shoot(3);
            }else{
                if(freeSort.pos1 == 'g'){
                    freeSort.shoot(1);
                }else
                if(freeSort.pos2 == 'g'){
                    freeSort.shoot(2);
                }else
                if(freeSort.pos3 == 'g'){
                    freeSort.shoot(3);
                }
            }

        }else{
            if(freeSort.pos1 == 'g'){
                freeSort.shoot(1);
            }else
            if(freeSort.pos2 == 'g'){
                freeSort.shoot(2);
            }else
            if(freeSort.pos3 == 'g'){
                freeSort.shoot(3);
            }else{
                if(freeSort.pos1 == 'p'){
                    freeSort.shoot(1);
                }else
                if(freeSort.pos2 == 'p'){
                    freeSort.shoot(2);
                }else
                if(freeSort.pos3 == 'p'){
                    freeSort.shoot(3);
                }
            }
        }



    }

    public static double turnAngle(double currentHeading, double targetAngle) {
        double error = targetAngle - currentHeading;

        // Normalize to (-PI, PI]
        while (error > Math.PI)  error -= 2 * Math.PI;
        while (error <= -Math.PI) error += 2 * Math.PI;

        return error;
    }

    static Pose2D pos = new Pose2D(DistanceUnit.INCH,2,2,AngleUnit.RADIANS,0);
    public static double distTo(Pose tagPos, Pose ourPos){
        return Math.sqrt((ourPos.getY() - tagPos.getY())*(ourPos.getY() - tagPos.getY()) + (ourPos.getX() - tagPos.getX())*(ourPos.getX() - tagPos.getX()));
    }

    public static double AngleToTag(Pose tagPos, Pose ourPos){
        return Math.atan2(tagPos.getY() - ourPos.getY(), tagPos.getX() - ourPos.getX());
    }


}




