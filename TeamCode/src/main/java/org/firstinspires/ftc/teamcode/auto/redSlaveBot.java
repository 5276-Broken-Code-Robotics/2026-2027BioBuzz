package org.firstinspires.ftc.teamcode.auto;


import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.pedro.Constants;


@Autonomous(group = "Red Auto")
public class redSlaveBot extends OpMode {

    //GoBildaPinpointDriver pinpoint;

    public int pathState;

    private DcMotor intake;

    private Follower follower;
    private ElapsedTime pathTimer, actionTimer, opmodeTimer, shotTimer;
    //private final Pose startPose = new Pose(80, 9, Math.toRadians(0));
    //private final Pose finalPose = new Pose(96, 9, Math.toRadians(0));
    private final Pose startPose = new Pose(32, 9, Math.toRadians(90));
    private final Pose finalPose = new Pose(32, 12, Math.toRadians(90));

    private Path scorePreload;


    public void buildPaths(){
        scorePreload =line(startPose, finalPose).linear(startPose,finalPose);
    }
    public void stop(){
        //shooter.panTracking.pan.setTargetPosition(0);
    }

    boolean tweaking = false;


    ElapsedTime waiter;
    public void setPathState(int pState) {
        pathState = pState;
    }
    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                //shooter.panTracking.autoPausing = true;
                //shooter.panTracking.autoPausingTimer.reset();
                opmodeTimer.reset();


               // setPathState(1);
                break;
            case 100:
                //if(shooter.panTracking.autoPausingTimer.seconds() > 2){
                    //shooter.firePattern();
                    //setPathState(1);
                //}
                break;

            case 1:
                //if(!shooter.firingPat){
                    //follower.followPath(scorePreload);
                    //setPathState(-1);
                  //  shooter.panTracking.resettingpan = true;
                //}else{
                  //  tweaking = true;
                //}\
                schedule(follow(follower, scorePreload));
                setPathState(-1);
                break;
        }


    }

    @Override

    public void init() {
        opmodeTimer = new ElapsedTime();



        //pinpoint = hardwareMap.get(GoBildaPinpointDriver.class,"pinpoint");
        // pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.REVERSED,
        //       GoBildaPinpointDriver.EncoderDirection.FORWARD);
       // pinpoint.resetPosAndIMU();

       // pinpoint.setPosition(new Pose2D(DistanceUnit.INCH,32, 9,AngleUnit.RADIANS, Math.toRadians(90)));

        opmodeTimer.reset();
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
        buildPaths();

        //shooter = new HoodedShooter();




        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setDirection(DcMotor.Direction.FORWARD);



        //shooter.init(hardwareMap, telemetry, pinpoint, 24);

    }

    @Override
    public void loop() {

        //if(shooter.freeSort.pos1 != 'e' && shooter.freeSort.pos2 != 'e' && shooter.freeSort.pos3!='e'){
           // intake.setPower(-1);
        //}else{
          //  intake.setPower(1);
        //}


        follower.update();
        Scheduler.execute();

        //shooter.loop();


        //pinpoint.update();

        autonomousPathUpdate();

        Pose currentPose = follower.pose();
        if(tweaking)telemetry.addData("Hi", "Why are you tweaking lil bro");

        // Feedback to Driver Hub for debugging
        telemetry.addData("path state", pathState);
        telemetry.addData("x", currentPose.x());
        telemetry.addData("y", currentPose.y());
        telemetry.addData("heading", currentPose.heading());
        telemetry.addData("Timer :", opmodeTimer.seconds());
        telemetry.update();
    }
    @Override
    public void start() {
        opmodeTimer.reset();
        intake.setPower(1);

        follower.setPose(startPose);

        //shooter.start();





        setPathState(0);
    }
}