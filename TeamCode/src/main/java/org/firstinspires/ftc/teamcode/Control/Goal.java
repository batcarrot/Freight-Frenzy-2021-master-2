package org.firstinspires.ftc.teamcode.Control;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.BNO055IMUImpl;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsAnalogOpticalDistanceSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;
import static org.firstinspires.ftc.teamcode.Control.Constants.COUNTS_PER_DEGREE_GOBILDA_30_RPM;
import static org.firstinspires.ftc.teamcode.Control.Constants.COUNTS_PER_INCH_REV_CORE_HEX_MOTOR;
import static org.firstinspires.ftc.teamcode.Control.Constants.COUNTS_PER_INCH_GOBILDA_435_RPM;
import static org.firstinspires.ftc.teamcode.Control.Constants.imuS;
import static org.firstinspires.ftc.teamcode.Control.Constants.motorBLS;
import static org.firstinspires.ftc.teamcode.Control.Constants.motorBRS;
import static org.firstinspires.ftc.teamcode.Control.Constants.motorFLS;
import static org.firstinspires.ftc.teamcode.Control.Constants.motorFRS;
import static org.firstinspires.ftc.teamcode.Control.Constants.intakeS;
import static org.firstinspires.ftc.teamcode.Control.Constants.pivotS;
import static org.firstinspires.ftc.teamcode.Control.Constants.backUltraS;
import static org.firstinspires.ftc.teamcode.Control.Constants.rightUltraS;
import static org.firstinspires.ftc.teamcode.Control.Constants.leftUltraS;
import static org.firstinspires.ftc.teamcode.Control.Constants.frontUltraS;

import static org.firstinspires.ftc.teamcode.Control.Constants.VUFORIA_KEY;
import static org.firstinspires.ftc.teamcode.Control.Constants.mmPerInch;
import static org.firstinspires.ftc.teamcode.Control.Constants.mmFTCFieldWidth;
import static org.firstinspires.ftc.teamcode.Control.Constants.mmFTCHalfFieldWidth;
import static org.firstinspires.ftc.teamcode.Control.Constants.mmFTCQuadFieldWidth;
import static org.firstinspires.ftc.teamcode.Control.Constants.mmTargetHeight;
import static org.firstinspires.ftc.teamcode.Control.Constants.CAMERA_CHOICE;
import static org.firstinspires.ftc.teamcode.Control.Constants.PHONE_IS_PORTRAIT;

public class Goal {

    /** Instance variables **/
    public Orientation angles;

    /** Initialized in constructor **/
    public ElapsedTime runtime;
    public Central central;
    public HardwareMap hardwareMap;

    public static double speedAdjust = 20.0 / 41.0;
    public static double yToXRatio = 1.25;

    public ModernRoboticsI2cRangeSensor backUltrasonic;
    public ModernRoboticsI2cRangeSensor rightUltrasonic;
    public ModernRoboticsI2cRangeSensor leftUltrasonic;
    public ModernRoboticsI2cRangeSensor frontUltrasonic;

    /** ------------------------------- VUFORIA ------------------------------- **/
    public OpenGLMatrix lastLocation = null;
    public OpenGLMatrix robotFromCamera;
    public VuforiaLocalizer vuforia = null;
    public OpenCvWebcam webcam;

    /**Trackable data **/
    public List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
    public VuforiaTrackables targetsUltimateGoal;

    /** Temp variables **/
    public boolean targetVisible = false;

    /** Vuforia init data **/
    public int cameraMonitorViewId;
    public VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

    /**
     * This is the webcam we are to use. As with other hardware devices such as motors and
     * servos, this device is identified using the robot configuration tool in the FTC application.
     */
    public WebcamName webcamName = null;

    /** ---------------------------- DRIVETRAIN ----------------------------- **/
    public DcMotor motorFR;
    public DcMotor motorFL;
    public DcMotor motorBR;
    public DcMotor motorBL;
    public DcMotor intake;
    public DcMotor pivot;

    /** Set in motorDriveMode() for drivetrain movement functions **/
    public DcMotor[] drivetrain;

    /** -------------------------------- IMU ------------------------------- **/
    public BNO055IMUImpl imu;

    /** IMU params **/
    public Orientation current;
    public BNO055IMUImpl.Parameters imuparameters = new BNO055IMUImpl.Parameters();

    /** Temp variables **/
    public static boolean isnotstopped;
    public float initorient;

    public Goal(HardwareMap hardwareMap, ElapsedTime runtime, Central central, setupType... setup) throws InterruptedException {
        //Update instance variables
        this.hardwareMap = hardwareMap;
        this.runtime = runtime;
        this.central = central;

        //For sending to control hub via telemetry
        StringBuilder i = new StringBuilder();

        //Take action based on setup mode(s)
        for (setupType type: setup) {
            switch (type) {
                case autonomous:
                    setupAuton();
                    break;
                case teleop:
                    setupTeleop();
                    break;
                case drivetrain_system:
                    setupDrivetrain();
                    break;
                case intake:
                    setupIntake();
                    break;
                case ultra:
                    setupUltra();
                    break;
                case imu:
                    setupIMU();
                    break;
                case openCV:
                    setupOpenCV();
                    break;

            }

            //Update string with setup type
            i.append(type.name()).append(" ");

        }

        //Send string to control hub
        central.telemetry.addLine(i.toString());
        central.telemetry.update();

    }

    public void setupAuton() throws InterruptedException {
        setupDrivetrain();
//        setupUltra();
        setupIntake();
        setupIMU();
    }

    public void setupTeleop() throws InterruptedException {
        setupDrivetrain();
//        setupUltra();
        setupIntake();
        setupIMU();
    }

    public void setupIMU() throws InterruptedException {
        imuparameters.angleUnit = BNO055IMUImpl.AngleUnit.DEGREES;
        imuparameters.accelUnit = BNO055IMUImpl.AccelUnit.METERS_PERSEC_PERSEC;
        imuparameters.calibrationDataFile = "AdafruitIMUCalibration.json"; // see the calibration sample opmode
        imuparameters.loggingEnabled = true; //copypasted from BNO055IMU sample code, no clue what this does
        imuparameters.loggingTag = "imu"; //copypasted from BNO055IMU sample code, no clue what this does
        imu = hardwareMap.get(BNO055IMUImpl.class, imuS);
        imu.initialize(imuparameters);
        initorient = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
        central.telemetry.addData("IMU status", imu.getSystemStatus());
        central.telemetry.update();

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu.initialize(parameters);

        imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);
    }




    public void setupDrivetrain() throws InterruptedException {
        motorFR = motor(motorFRS, DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE);
        motorFL = motor(motorFLS, DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE);
        motorBR = motor(motorBRS, DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE);
        motorBL = motor(motorBLS, DcMotorSimple.Direction.REVERSE, DcMotor.ZeroPowerBehavior.BRAKE);

        motorDriveMode(EncoderMode.ON, motorFR, motorFL, motorBR, motorBL);
    }

    public void setupIntake() throws InterruptedException {
        intake = motor(intakeS, DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE);
        pivot = motor(pivotS, DcMotorSimple.Direction.REVERSE, DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void setupUltra() throws InterruptedException {
//        backUltrasonic = ultrasonicSensor(backUltraS);
        rightUltrasonic = ultrasonicSensor(rightUltraS);
        frontUltrasonic = ultrasonicSensor(leftUltraS);
//        leftUltrasonic = ultrasonicSensor(frontUltraS);
    }

    public void setupOpenCV() throws InterruptedException {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
    }

    public void setupVuforia() throws InterruptedException {
        float phoneXRotate    = 0;
        float phoneYRotate    = 0;
        float phoneZRotate    = 0;

        webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");

        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         * We can pass Vuforia the handle to a camera preview resource (on the RC phone);
         * If no camera monitor is desired, use the parameter-less constructor instead (commented out below).
         */
        cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        parameters.vuforiaLicenseKey = VUFORIA_KEY;

        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;

        /**
         * We also indicate which camera on the RC we wish to use.
         */
        parameters.cameraName = webcamName;

        // Make sure extended tracking is disabled for this example.
        parameters.useExtendedTracking = false;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Load the data sets for the trackable objects. These particular data
        // sets are stored in the 'assets' part of our application.
        targetsUltimateGoal = this.vuforia.loadTrackablesFromAsset("UltimateGoal");
        VuforiaTrackable blueTowerGoalTarget = targetsUltimateGoal.get(0);
        blueTowerGoalTarget.setName("Blue Tower Goal Target");
        VuforiaTrackable redTowerGoalTarget = targetsUltimateGoal.get(1);
        redTowerGoalTarget.setName("Red Tower Goal Target");
        VuforiaTrackable redAllianceTarget = targetsUltimateGoal.get(2);
        redAllianceTarget.setName("Red Alliance Target");
        VuforiaTrackable blueAllianceTarget = targetsUltimateGoal.get(3);
        blueAllianceTarget.setName("Blue Alliance Target");
        VuforiaTrackable frontWallTarget = targetsUltimateGoal.get(4);
        frontWallTarget.setName("Front Wall Target");

        allTrackables.addAll(targetsUltimateGoal);

        /**
         * In order for localization to work, we need to tell the system where each target is on the field, and
         * where the phone resides on the robot.  These specifications are in the form of <em>transformation matrices.</em>
         * Transformation matrices are a central, important concept in the math here involved in localization.
         * See <a href="https://en.wikipedia.org/wiki/Transformation_matrix">Transformation Matrix</a>
         * for detailed information. Commonly, you'll encounter transformation matrices as instances
         * of the {@link OpenGLMatrix} class.
         *
         * If you are standing in the Red Alliance Station looking towards the center of the field,
         *     - The X axis runs from your left to the right. (positive from the center to the right)
         *     - The Y axis runs from the Red Alliance Station towards the other side of the field
         *       where the Blue Alliance Station is. (Positive is from the center, towards the BlueAlliance station)
         *     - The Z axis runs from the floor, upwards towards the ceiling.  (Positive is above the floor)
         *
         * Before being transformed, each target image is conceptually located at the origin of the field's
         *  coordinate system (the center of the field), facing up.
         */

        //Set the position of the perimeter targets with relation to origin (center of field)
        redAllianceTarget.setLocation(OpenGLMatrix
                .translation(0, -mmFTCHalfFieldWidth, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180)));

        blueAllianceTarget.setLocation(OpenGLMatrix
                .translation(0, mmFTCHalfFieldWidth, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0)));
        frontWallTarget.setLocation(OpenGLMatrix
                .translation(-mmFTCHalfFieldWidth, 0, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , 90)));

        // The tower goal targets are located a quarter field length from the ends of the back perimeter wall.
        blueTowerGoalTarget.setLocation(OpenGLMatrix
                .translation(mmFTCHalfFieldWidth, mmFTCQuadFieldWidth, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , -90)));
        redTowerGoalTarget.setLocation(OpenGLMatrix
                .translation(mmFTCHalfFieldWidth, -mmFTCQuadFieldWidth, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));

        //
        // Create a transformation matrix describing where the phone is on the robot.
        //
        // NOTE !!!!  It's very important that you turn OFF your phone's Auto-Screen-Rotation option.
        // Lock it into Portrait for these numbers to work.
        //
        // Info:  The coordinate frame for the robot looks the same as the field.
        // The robot's "forward" direction is facing out along X axis, with the LEFT side facing out along the Y axis.
        // Z is UP on the robot.  This equates to a bearing angle of Zero degrees.
        //
        // The phone starts out lying flat, with the screen facing Up and with the physical top of the phone
        // pointing to the LEFT side of the Robot.
        // The two examples below assume that the camera is facing forward out the front of the robot.

        // We need to rotate the camera around it's long axis to bring the correct camera forward.
        if (CAMERA_CHOICE == BACK) {
            phoneYRotate = -90;
        } else {
            phoneYRotate = 90;
        }

        // Rotate the phone vertical about the X axis if it's in portrait mode
        if (PHONE_IS_PORTRAIT) {
            phoneXRotate = 90 ;
        }

        // Next, translate the camera lens to where it is on the robot.
        // In this example, it is centered (left to right), but forward of the middle of the robot, and above ground level.
        final float CAMERA_FORWARD_DISPLACEMENT  = 4.0f * mmPerInch;   // eg: Camera is 4 Inches in front of robot-center
        final float CAMERA_VERTICAL_DISPLACEMENT = 8.0f * mmPerInch;   // eg: Camera is 8 Inches above ground
        final float CAMERA_LEFT_DISPLACEMENT     = 0;     // eg: Camera is ON the robot's center line

        robotFromCamera = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, phoneYRotate, phoneZRotate, phoneXRotate));
    }




    //-----------------------HARDWARE SETUP FUNCTIONS---------------------------------------
    public DcMotor motor(String name, DcMotor.Direction direction, DcMotor.ZeroPowerBehavior zeroPowerBehavior) throws InterruptedException {
        DcMotor motor = hardwareMap.dcMotor.get(name);
        motor.setDirection(direction);
        motor.setZeroPowerBehavior(zeroPowerBehavior);
        motor.setPower(0);
        return motor;
    }

    public Servo servo(String name, Servo.Direction direction, double min, double max, double start) throws InterruptedException {
        Servo servo = hardwareMap.servo.get(name);
        servo.setDirection(direction);
        servo.scaleRange(min, max);
        servo.setPosition(start);
        return servo;
    }
    public CRServo servo(String name, DcMotorSimple.Direction direction, double startSpeed) throws InterruptedException {
        CRServo servo = hardwareMap.crservo.get(name);
        servo.setDirection(direction);

        servo.setPower(startSpeed);
        return servo;
    }
    public ColorSensor colorSensor(String name, boolean ledOn) throws InterruptedException {
        ColorSensor sensor = hardwareMap.colorSensor.get(name);
        sensor.enableLed(ledOn);

        central.telemetry.addData("Beacon Red Value: ", sensor.red());
        central.telemetry.update();

        return sensor;
    }
    public ModernRoboticsI2cRangeSensor ultrasonicSensor(String name) throws InterruptedException {

        return hardwareMap.get(ModernRoboticsI2cRangeSensor.class, name);
    }
    public Rev2mDistanceSensor therealUS(String name) throws InterruptedException {
        return hardwareMap.get(Rev2mDistanceSensor.class, name);
    }

    public ModernRoboticsI2cColorSensor MRColor(String name) throws InterruptedException {
        return hardwareMap.get(ModernRoboticsI2cColorSensor.class, name);

    }

    public ModernRoboticsAnalogOpticalDistanceSensor realUS(String name) throws InterruptedException {
        return hardwareMap.get(ModernRoboticsAnalogOpticalDistanceSensor.class, name);
    }

    public void encoder(EncoderMode mode, DcMotor... motor) throws InterruptedException {
        switch (mode) {
            case ON:
                for (DcMotor i : motor) {
                    i.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                }
                central.idle();
                for (DcMotor i : motor) {
                    i.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                }
                break;
            case OFF:
                break;
        }

    }

    public void motorDriveMode(EncoderMode mode, DcMotor... motor) throws InterruptedException {

        switch (mode) {
            case ON:
                for (DcMotor i : motor) {
                    i.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                }
                central.idle();
                for (DcMotor i : motor) {
                    i.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                }
                break;
            case OFF:
                break;
        }

        this.drivetrain = motor;

    }

    public void driveTrainEncoderMovement(double speed, double distance, double timeoutS, long waitAfter, movements movement) throws InterruptedException {
        driveTrainEncoderMovementSpecific435Motors(speed, distance, timeoutS, waitAfter, movement, drivetrain);
    }

    public void driveTrainEncoderMovementSpecific435Motors(double speed, double distance, double timeoutS, long waitAfter, movements movement, DcMotor... motors) throws InterruptedException {
        driveTrainEncoderMovementSpecificMotorsTypes(speed, distance, timeoutS, waitAfter, movement, COUNTS_PER_INCH_GOBILDA_435_RPM, motors);
    }

    public void driveTrainEncoderMovementCoreHexMotors(double speed, double distance, double timeoutS, long waitAfter, movements movement, DcMotor... motors) throws InterruptedException {
        driveTrainEncoderMovementSpecificMotorsTypes(speed, distance, timeoutS, waitAfter, movement, COUNTS_PER_INCH_REV_CORE_HEX_MOTOR, motors);
    }

    public void driveTrainEncoderMovementSpecificMotorsTypes(double speed, double distance, double timeoutS, long waitAfter, movements movement, double COUNTS_PER_INCH_OF_MOTOR, DcMotor... motors) throws InterruptedException {
        double[] signs = movement.getDirections();

        // Ensure that the opmode is still active
        if (central.opModeIsActive()) {
            // Determine new target position, and pass to motor controller

            for (DcMotor motor: motors){
                int x = Arrays.asList(motors).indexOf(motor);
                if (signs[x] != 0) {
                    motor.setTargetPosition(motor.getCurrentPosition() + (int) (signs[x] * distance * COUNTS_PER_INCH_OF_MOTOR));
                    motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                }
            }
            runtime.reset();

            for (DcMotor motor: motors){
                int x = Arrays.asList(motors).indexOf(motor);
                if (signs[x] != 0) motor.setPower(signs[x] * Math.abs(speed));
            }

            // keep looping while we are still active, and there is time left, and both motors are running.
            boolean x = true;
            while (central.opModeIsActive() && (runtime.seconds() < timeoutS) && (x)) {
                // Display it for the driver.
                // Allow time for other processes to run.
                central.idle();
                for (DcMotor motor: motors){
                    int i = Arrays.asList(motors).indexOf(motor);
                    if (!motor.isBusy() && signs[i] != 0){
                        x = false;
                    }
                }
            }

            // Stop all motion;
            for (DcMotor motor: motors){
                int i = Arrays.asList(motors).indexOf(motor);
                if (signs[i] != 0) motor.setPower(0);
            }

            // Turn off RUN_TO_POSITION
            for (DcMotor motor: motors) {
                int i = Arrays.asList(motors).indexOf(motor);
                if (signs[i] != 0) motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
            central.sleep(waitAfter);
        }
    }

    public void moveIntakePivotDegrees(double speed, double degrees) {
        int sign = speed < 0 || degrees < 0 ? -1 : 1;
        pivot.setTargetPosition(pivot.getCurrentPosition() + (int) (sign * Math.abs(degrees) * COUNTS_PER_DEGREE_GOBILDA_30_RPM));
        pivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        pivot.setPower(sign * Math.abs(speed));
        while (pivot.isBusy());
        pivot.setPower(0);
    }

    public void runIntakeTimeSpeed(double speed, long time) {
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setPower(speed);
        central.sleep(time);
        intake.setPower(0);
    }

    public void runIntakeSpeed(double speed) {
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setPower(speed);
    }

    //------------------DRIVETRAIN TELEOP FUNCTIONS------------------------------------------------------------------------
    public void driveTrainMovement(double speed, movements movement) throws InterruptedException {
        double[] signs = movement.getDirections();
        for (DcMotor motor: drivetrain){
            int x = Arrays.asList(drivetrain).indexOf(motor);
            motor.setPower(signs[x] * speed);

        }
    }
    public void driveTrainMovement(double... speed) throws InterruptedException {

        for (int i = 0; i < drivetrain.length; i++) {
            drivetrain[i].setPower(speed[i]);
        }
    }
    public void driveTrainTimeMovement(double speed, movements movement, long duration, long waitAfter) throws InterruptedException {
        double[] signs = movement.getDirections();
        for (DcMotor motor: drivetrain){
            int x = Arrays.asList(drivetrain).indexOf(motor);
            motor.setPower(signs[x] * speed);

        }
        central.sleep(duration);
        stopDrivetrain();
        central.sleep(waitAfter);
    }

    public void anyMovement(double speed, movements movement, DcMotor... motors) throws InterruptedException {
        double[] signs = movement.getDirections();
        for (DcMotor motor: motors){
            int x = Arrays.asList(motors).indexOf(motor);
            motor.setPower(signs[x] * speed);

        }
    }
    public void anyMovementTime(double speed, movements movement, long duration, DcMotor... motors) throws InterruptedException {
        double[] signs = movement.getDirections();
        for (DcMotor motor: motors){
            int x = Arrays.asList(motors).indexOf(motor);
            motor.setPower(signs[x] * speed);

        }
        central.sleep(duration);
        for (DcMotor motor: motors){
            motor.setPower(0);

        }
    }
    public void stopDrivetrain() throws InterruptedException {
        for (DcMotor motor: drivetrain){
            motor.setPower(0);
        }
    }

    public void powerMotors(double speed, long time, DcMotor... motors) {
        for (DcMotor motor : motors) {
            motor.setPower(speed);
        }
        central.sleep(time);
        for (DcMotor motor : motors) {
            motor.setPower(0);
        }
    }

    // IMU Movements
    public void turn(float target, turnside direction, double speed, axis rotation_Axis) throws InterruptedException{

        central.telemetry.addData("IMU State: ", imu.getSystemStatus());
        central.telemetry.update();

        double start = getDirection();

        double end = (start + ((direction == turnside.cw) ? target : -target) + 360) % 360;

        isnotstopped = true;
        try {
            switch (rotation_Axis) {
                case center:
                    driveTrainMovement(speed, (direction == turnside.cw) ? movements.cw : movements.ccw);
                    break;
                case back:
                    driveTrainMovement(speed, (direction == turnside.cw) ? movements.cwback : movements.ccwback);
                    break;
                case front:
                    driveTrainMovement(speed, (direction == turnside.cw) ? movements.cwfront : movements.ccwfront);
                    break;
            }
        } catch (InterruptedException e) {
            isnotstopped = false;
        }

        while (((calculateDifferenceBetweenAngles(getDirection(), end) > 1 && turnside.cw == direction) || (calculateDifferenceBetweenAngles(getDirection(), end) < -1 && turnside.ccw == direction)) && central.opModeIsActive() ) {
            central.telemetry.addLine("First Try ");
            central.telemetry.addData("IMU Inital: ", start);
            central.telemetry.addData("IMU Final Projection: ", end);
            central.telemetry.addData("IMU Orient: ", getDirection());
            central.telemetry.addData("IMU Difference: ", (calculateDifferenceBetweenAngles(end, getDirection())));
            central.telemetry.update();
        }
        try {
            stopDrivetrain();
        } catch (InterruptedException e) {
        }
        central.sleep(5000);

        while (calculateDifferenceBetweenAngles(getDirection(), end) < -0.25 && central.opModeIsActive()) {
            driveTrainMovement(0.1, (direction == turnside.cw) ? movements.ccw : movements.cw);
            central.telemetry.addLine("Correctional Try ");
            central.telemetry.addData("IMU Inital: ", start);
            central.telemetry.addData("IMU Final Projection: ", end);
            central.telemetry.addData("IMU Orient: ", getDirection());
            central.telemetry.addData("IMU Diffnce: ", calculateDifferenceBetweenAngles(end, getDirection()));
            central.telemetry.update();

        }
        stopDrivetrain();
        central.sleep(5000);

        central.telemetry.addLine("Completed");
        central.telemetry.addData("IMU Inital: ", start);
        central.telemetry.addData("IMU Final Projection: ", end);
        central.telemetry.addData("IMU Orient: ", getDirection());
        central.telemetry.addData("IMU Diffnce: ", calculateDifferenceBetweenAngles(end, getDirection()));
        central.telemetry.update();
        central.sleep(5000);
    }
    public void teleturn(float target, turnside direction, double speed, axis rotation_Axis) throws InterruptedException{

        central.telemetry.addData("IMU State: ", imu.getSystemStatus());
        central.telemetry.update();

        double start = getDirection();

        double end = (start + ((direction == turnside.cw) ? target : -target) + 360) % 360;

       /* isnotstopped = true;
        try {
            switch (rotation_Axis) {
                case center:
                    driveTrainMovement(speed, (direction == turnside.cw) ? movements.cw : movements.ccw);
                    break;
                case back:
                    driveTrainMovement(speed, (direction == turnside.cw) ? movements.cwback : movements.ccwback);
                    break;
                case front:
                    driveTrainMovement(speed, (direction == turnside.cw) ? movements.cwfront : movements.ccwfront);
                    break;
            }
        } catch (InterruptedException e) {
            isnotstopped = false;
        }

        while ((((end - getDirection()) > 1 && turnside.cw == direction) || (turnside.cw != direction && end - getDirection() < -1)) && central.opModeIsActive() && isnotstopped) {
            central.telemetry.addData("IMU Inital: ", start);
            central.telemetry.addData("IMU Final Projection: ", end);
            central.telemetry.addData("IMU Orient: ", getDirection());
            central.telemetry.update();
        }
        try {
            stopDrivetrain();
        } catch (InterruptedException e) {
        }


        */

        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        if (angles.firstAngle > 0) {
            while (angles.firstAngle > -1) {
                angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                driveTrainMovement(.5, Goal.movements.ccw);
            }
        }
        if (angles.firstAngle < 0) {
            while (angles.firstAngle < 1) {
                angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                driveTrainMovement(.5, Goal.movements.cw);
            }
        }

        double r = end-getDirection();
        boolean x;
        if(r>1){
            x=true;
        }
        else{
            x=false;
        }
        boolean y = false;

        while (Math.abs(end - getDirection()) > 1 && central.opModeIsActive()){
            r = end-getDirection();
            if(r>1){
                y=true;
            }
            else{
                y=false;
            }
            if(x!=y){
                break;
            }
            central.telemetry.addData("IMU Orient: ", getDirection());
            central.telemetry.addData("IMU Target: ", target);
            central.telemetry.update();
            driveTrainMovement(.05, (direction == turnside.cw) ? movements.ccw : movements.cw);

        }
        stopDrivetrain();

    }


    public void absturn(float target, turnside direction, double speed, axis rotation_Axis) throws InterruptedException {

        central.telemetry.addData("IMU State: ", imu.getSystemStatus());
        central.telemetry.update();

        double start = 0;

        double end = (start + ((direction == turnside.cw) ? target : -target) + 360) % 360;

        isnotstopped = true;
        try {
            switch (rotation_Axis) {
                case center:
                    driveTrainMovement(speed, (direction == turnside.cw) ? movements.cw : movements.ccw);
                    break;
                case back:
                    driveTrainMovement(speed, (direction == turnside.cw) ? movements.cwback : movements.ccwback);
                    break;
                case front:
                    driveTrainMovement(speed, (direction == turnside.cw) ? movements.cwfront : movements.ccwfront);
                    break;
            }
        } catch (InterruptedException e) {
            isnotstopped = false;
        }

        while (((calculateDifferenceBetweenAngles(getDirection(), end) > 1 && turnside.cw == direction) || (calculateDifferenceBetweenAngles(getDirection(), end) < -1 && turnside.ccw == direction)) && central.opModeIsActive() ) {
            central.telemetry.addLine("First Try ");
            central.telemetry.addData("IMU Inital: ", start);
            central.telemetry.addData("IMU Final Projection: ", end);
            central.telemetry.addData("IMU Orient: ", getDirection());
            central.telemetry.addData("IMU Difference: ", end - getDirection());
            central.telemetry.update();
        }
        try {
            stopDrivetrain();
        } catch (InterruptedException e) {
        }

        while (calculateDifferenceBetweenAngles(end, getDirection()) > 1 && central.opModeIsActive()){
            driveTrainMovement(0.05, (direction == turnside.cw) ? movements.ccw : movements.cw);
            central.telemetry.addLine("Correctional Try ");
            central.telemetry.addData("IMU Inital: ", start);
            central.telemetry.addData("IMU Final Projection: ", end);
            central.telemetry.addData("IMU Orient: ", getDirection());
            central.telemetry.addData("IMU Diffnce: ", end - getDirection());
            central.telemetry.update();
        }
        stopDrivetrain();
        central.telemetry.addLine("Completed");
        central.telemetry.addData("IMU Inital: ", start);
        central.telemetry.addData("IMU Final Projection: ", end);
        central.telemetry.addData("IMU Orient: ", getDirection());
        central.telemetry.addData("IMU Diffnce: ", end - getDirection());
        central.telemetry.update();
    }

    public double calculateDifferenceBetweenAngles(double firstAngle, double secondAngle) // negative is secondAngle ccw relative to firstAngle
    {
        double difference = secondAngle - firstAngle;
        while (difference < -180) difference += 360;
        while (difference > 180) difference -= 360;
        return -difference;
    }

    public double getDirection(){
        return (this.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle-initorient+720)%360;
    }



    public enum EncoderMode{
        ON, OFF;
    }
    public enum setupType{
        autonomous, teleop, drivetrain_system, ultra, intake, imu, openCV;
    }



    //-------------------SET FUNCTIONS--------------------------------
    public void setCentral(Central central) {
        this.central = central;
    }
    public void setHardwareMap(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
    }
    public void setRuntime(ElapsedTime runtime) {
        this.runtime = runtime;
    }

    //-------------------CHOICE ENUMS-------------------------
    public enum movements
    {
        // FR FL BR BL
        forward(1, -1, 1, -1),
        backward(-1, 1, -1, 1),
        left(1, 1, -1, -1),
        right(-1, -1, 1, 1),
        br(0, -1, 1, 0),
        bl(1, 0, 0, -1),
        tl(0, 1, -1, 0),
        tr(-1, 0, 0, 1),
        ccw(-1, -1, -1, -1),
        cw(1, 1, 1, 1),
        cwback(-1, -1, 0, 0),
        ccwback(1, 1, 0, 0),
        cwfront(0, 0, -1, -1),
        ccwfront(0, 0, 1, 1);

        private final double[] directions;

        movements(double... signs) {
            this.directions = signs;
        }

        public double[] getDirections() {
            return directions;
        }
    }


    public enum turnside {
        ccw, cw
    }

    /**
     * Finds the set of two direction speeds at the mecanum's movement angles that create a vector movement towards the desired angle
     * @param speed speed of movement
     * @param angleDegrees angle (in degrees) of movement
     * @return array of motor directions
     */
    public static double[] anyDirection(double speed, double angleDegrees) {
        double theta = Math.toRadians(angleDegrees);
        double beta = Math.atan(yToXRatio);

        double v1 = speedAdjust * (speed * Math.sin(theta) / Math.sin(beta) + speed * Math.cos(theta) / Math.cos(beta));
        double v2 = speedAdjust * (speed * Math.sin(theta) / Math.sin(beta) - speed * Math.cos(theta) / Math.cos(beta));

        double[] retval = {v1, v2};
        return retval;
    }

    public static double[] anyDirectionRadians(double speed, double angleRadians) {
        double theta = angleRadians;
        double beta = Math.atan(yToXRatio);

        double v1 = speedAdjust * (speed * Math.sin(theta) / Math.sin(beta) + speed * Math.cos(theta) / Math.cos(beta));
        double v2 = speedAdjust * (speed * Math.sin(theta) / Math.sin(beta) - speed * Math.cos(theta) / Math.cos(beta));

        double[] retval = {v1, v2};
        return retval;
    }

    public void driveTrainMovementAngle(double speed, double angle) {

        double[] speeds = anyDirection(speed, angle);
        motorFR.setPower(movements.forward.directions[0] * speeds[0]);
        motorFL.setPower(movements.forward.directions[1] * speeds[1]);
        motorBR.setPower(movements.forward.directions[2] * speeds[1]);
        motorBL.setPower(movements.forward.directions[3] * speeds[0]);

    }

    public void driveTrainMovementAngleRadians(double speed, double angle) {

        double[] speeds = anyDirectionRadians(speed, angle);
        motorFR.setPower(movements.forward.directions[0] * speeds[0]);
        motorFL.setPower(movements.forward.directions[1] * speeds[1]);
        motorBR.setPower(movements.forward.directions[2] * speeds[1]);
        motorBL.setPower(movements.forward.directions[3] * speeds[0]);

    }

    public enum axis {
        front, center, back
    }


    public void driveTrainIMUSwingTurnMovement(double speed, movements movement, long waitAfter, double rotationDegrees, double rotationfactor, turnside rotDir) throws InterruptedException{
        double[] signs = movement.getDirections();
        rotationDegrees = 360- rotationDegrees;
        double start = getDirection();

        double end = (start + ((rotDir == turnside.cw) ? rotationDegrees : -rotationDegrees) + 360) % 360;
        double[] speedValues = anyDirection(speed, -90 - start + getDirection());
        double[] speeds= new double[4];
        for (int i = 0; i < drivetrain.length; i++) {
            if (i == 0 || i == 4) {
                speeds[i] = (speedValues[1]);
            } else {
                speeds[i] = (speedValues[0]);
            }
        }


        while ((((end - getDirection()) > 1 && turnside.cw == rotDir) || (turnside.cw != rotDir && end - getDirection() < -1)) && central.opModeIsActive()) {
            central.telemetry.addData("IMU Inital: ", start);
            central.telemetry.addData("IMU Final Projection: ", end);
            central.telemetry.addData("IMU Orient: ", getDirection());

            for (DcMotor motor: drivetrain){
                int x = Arrays.asList(drivetrain).indexOf(motor);
                motor.setPower(signs[x] * speeds[x] + rotationfactor * movements.valueOf(rotDir.name()).getDirections()[x]);
                central.telemetry.addData("motor " + x, signs[x] * speeds[x] + rotationfactor * movements.valueOf(rotDir.name()).getDirections()[x]);

            }
            central.telemetry.update();
        }

        stopDrivetrain();
        while (Math.abs(end - getDirection()) > 1 && central.opModeIsActive()){
            driveTrainMovement(0.3, (rotDir == turnside.cw) ? movements.ccw : movements.cw);
        }


        stopDrivetrain();
        central.sleep(waitAfter);
    }
    public void driveTrainIMUSwingTurnMovementOrig(double speed, movements movement, long waitAfter, int rotationDegrees, double rotationfactor, turnside rotDir) throws InterruptedException{
        double[] signs = movement.getDirections();

        double start = getDirection();

        double end = (start + ((rotDir == turnside.cw) ? rotationDegrees : -rotationDegrees) + 360) % 360;



        while ((((end - getDirection()) > 1 && turnside.cw == rotDir) || (turnside.cw != rotDir && end - getDirection() < -1)) && central.opModeIsActive()) {
            double[] speedValues = anyDirection(speed, 90 + start - getDirection());
            double[] speeds= new double[4];
            for (int i = 0; i < drivetrain.length; i++) {
                if (i == 0 || i == 3) {
                    speeds[i] = (speedValues[0]);
                } else {
                    speeds[i] = (speedValues[1]);
                }
            }
            central.telemetry.addData("IMU Inital: ", start);
            central.telemetry.addData("IMU Final Projection: ", end);
            central.telemetry.addData("IMU Orient: ", getDirection());

            for (DcMotor motor: drivetrain){
                int x = Arrays.asList(drivetrain).indexOf(motor);
                motor.setPower(speeds[x] + rotationfactor * -movements.valueOf(rotDir.name()).getDirections()[x]);
                central.telemetry.addData("motor " + x, speeds[x] + rotationfactor * -movements.valueOf(rotDir.name()).getDirections()[x]);

            }
            central.telemetry.update();
        }

        stopDrivetrain();
        while (Math.abs(end - getDirection()) > 1 && central.opModeIsActive()){
            driveTrainMovement(0.1, (rotDir == turnside.cw) ? movements.ccw : movements.cw);
        }


        stopDrivetrain();
        central.sleep(waitAfter);
    }

    public void driveTrainIMUSuperStrafeMovement(double speed, movements movement, long waitAfter, int rotationDegrees, double rotationfactor, turnside rotDir) throws InterruptedException{
        double[] signs = movement.getDirections();

        double start = getDirection();

        double end = (start + ((rotDir == turnside.cw) ? rotationDegrees : -rotationDegrees) + 360) % 360;
        double[] speedValues = anyDirection(speed, 90 + start - getDirection());
        double[] speeds= new double[4];
        for (int i = 0; i < drivetrain.length; i++) {
            if (i == 0 || i == 3) {
                speeds[i] = (speedValues[0]);
            } else {
                speeds[i] = (speedValues[1]);
            }
        }

        int p = 0;

        boolean rotate = false;
        while ((((end - getDirection()) > 1 && turnside.cw == rotDir) || (turnside.cw != rotDir && end - getDirection() < -1)) && central.opModeIsActive()) {
            central.telemetry.addData("IMU Inital: ", start);
            central.telemetry.addData("IMU Final Projection: ", end);
            central.telemetry.addData("IMU Orient: ", getDirection());
            if (p % 10 == 0){
                rotate = !rotate;
            }
            for (DcMotor motor: drivetrain){
                int x = Arrays.asList(drivetrain).indexOf(motor);
                if (rotate) {
                    motor.setPower(rotationfactor * movements.valueOf(rotDir.name()).getDirections()[x]);
                    central.telemetry.addData("motor " + x, rotationfactor * movements.valueOf(rotDir.name()).getDirections()[x]);
                }
                else {
                    motor.setPower(speeds[x]);
                    central.telemetry.addData("motor " + x, speeds[x]);
                }

            }
            central.telemetry.update();
            p++;
        }

        stopDrivetrain();
        while (Math.abs(end - getDirection()) > 1 && central.opModeIsActive()){
            driveTrainMovement(0.1, (rotDir == turnside.cw) ? movements.ccw : movements.cw);
        }


        stopDrivetrain();
        central.sleep(waitAfter);
    }

}
