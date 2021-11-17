package org.firstinspires.ftc.teamcode.Control;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;

public class Constants {
    //--------------------------------ENCODERS-------------------------
    /**
     * Enum of encoder counts by motor, add new motor counts in here.
     * Includes instance function for countsPerInch()
     */
    public enum encoderCounts{
        GOBILDA_312(537.6),
        GOBILDA_435(383.6),
        REV_CORE_HEX(288),
        REV_STANDARD_MOTOR(1120),
        ;

        /**
         * Counts per revolution of specified enum value
         */
        double counts_per_rev;

        /**
         * @param counts_per_rev    Counts per revolution of specified enum element
         */
        encoderCounts(double counts_per_rev) {
            this.counts_per_rev = counts_per_rev;
        }

        /**
         * Returns counts per inch for the motor assuming no gearing.
         * @param object wheelType of {@link circumferenceObject} enum
         * @return counts per inch
         */
        public double countsPerInch(circumferenceObject object){
            return countsPerInch(1, object.diameter);
        }

        /**
         * Returns counts per inch for the motor assuming no gearing.
         * @param diameterInches diameter (in inches)
         * @return counts per inch
         */
        public double countsPerInch(double diameterInches){
            return countsPerInch(1, diameterInches);
        }

        /**
         * Returns counts per inch for the motor.
         * @param drive_reduction gearing ratio, > 1 if geared for speed
         * @return counts per inch
         */
        public double countsPerInch(double drive_reduction, double diameterInches){
            return this.counts_per_rev * drive_reduction / (diameterInches * Math.PI);
        }

    }

    /**
     * Enum of diameter in inches by object with circumference (wheels), add new circumference objects here.
     */
    public enum circumferenceObject{
        GO_BILDA_STANDARD_MECANUM(4),
        GREEN_COMPLIANT_WHEEL(4);

        /**
         * Diameter in inches
         */
        double diameter;

        /**
         * @param diameterInches    Diameter in inches of specific enum element
         */
        circumferenceObject(double diameterInches) {
            this.diameter = diameterInches;
        }
    }

    /**
     * Conversion factor of inch to mm since Vuforia uses mm, so mm must be used for all physical dimensions
     */
    public static final float mmPerInch = 25.4f;

    /**
     * Counts per revolution of a GoBilda 312 RPM Motor
     */
    public static final double COUNTS_PER_MOTOR_GOBILDA_312_RPM = 537.6;

    /**
     * Counts per revolution of a GoBilda 435 RPM Motor
     */
    public static final double COUNTS_PER_MOTOR_GOBILDA_435_RPM = 383.6;

    /**
     * Counts per revolution of a REV Core HEX Motor
     */
    public static final double COUNTS_PER_MOTOR_REV_CORE_HEX_MOTOR = 288;

    /**
     * Counts per revolution of a GoBilda 30 RPM Motor
     */
    public static final double COUNTS_PER_MOTOR_GOBILDA_30_RPM = 5281.1;

    /**
     * Drive Gear Reduction of a GoBilda 312 RPM Motor
     */
    public static final double DRIVE_GEAR_REDUCTION_GOBILDA_312_RPM = 2.0/3.0;

    /**
     * Drive Gear Reduction of a GoBilda 435 RPM Motor
     */
    public static final double DRIVE_GEAR_REDUCTION_GOBILDA_435_RPM = 2.0;

    /**
     * Drive Gear Reduction of a REV Core HEX Motor
     */
    public static final double DRIVE_GEAR_REDUCTION_REV_CORE_HEX_MOTOR = 1.0;

    /**
     * Wheel Diameter of the wheels on our GoBilda 312 RPM Motors
     */
    public static final double WHEEL_DIAMETER_INCHES_GOBILDA_312_RPM = 4.0;

    /**
     * Wheel Diameter of the wheels on our GoBilda 435 RPM Motors
     */
    public static final double WHEEL_DIAMETER_INCHES_GOBILDA_435_RPM = 96 / mmPerInch;

    /**
     * Wheel Diameter of the wheels on our REV Core HEX Motors
     */
    public static final double WHEEL_DIAMETER_INCHES_REV_CORE_HEX_MOTOR = 1.25;

    /**
     * Counts per inch calculated of a GoBilda 312 RPM Motor
     */
    public static final double COUNTS_PER_INCH_GOBILDA_312_RPM = (COUNTS_PER_MOTOR_GOBILDA_312_RPM * DRIVE_GEAR_REDUCTION_GOBILDA_312_RPM) /
            (WHEEL_DIAMETER_INCHES_GOBILDA_312_RPM * Math.PI);

    /**
     * Counts per inch calculated of a GoBilda 435 RPM Motor
     */
    public static final double COUNTS_PER_INCH_GOBILDA_435_RPM = COUNTS_PER_MOTOR_GOBILDA_435_RPM /
            (WHEEL_DIAMETER_INCHES_GOBILDA_435_RPM * Math.PI);

    /**
     * Counts per inch calculated of a REV Core HEX Motor
     */
    public static final double COUNTS_PER_INCH_REV_CORE_HEX_MOTOR = (COUNTS_PER_MOTOR_REV_CORE_HEX_MOTOR * DRIVE_GEAR_REDUCTION_REV_CORE_HEX_MOTOR) /
            (WHEEL_DIAMETER_INCHES_REV_CORE_HEX_MOTOR * Math.PI);

    /**
     * Counts per degree calculated of REV Core HEX Motor
     */
    public static final double COUNTS_PER_DEGREE_REV_CORE_HEX_MOTOR = COUNTS_PER_MOTOR_REV_CORE_HEX_MOTOR / 360.0;

    /**
     * Counts per degree calculated of GoBilda 30 RPM Motor
     */
    public static final double COUNTS_PER_DEGREE_GOBILDA_30_RPM = COUNTS_PER_MOTOR_GOBILDA_30_RPM / 360.0;

    //--------------------------------TELE-OP VALUES--------------------
    /**
     * Joystick Dead Zone Threshold
     */
    public static final double DEAD_ZONE_SIZE = 0.1;

    //--------------------------------VUFORIA----------------------------
    /**
     * Generated Key for Vuforia use
     */
    public static final String VUFORIA_KEY = "ASYtBET/////AAABmSTQiLUzLEx3qLnHm6hu7Y1aNDWPDgMBKY8lFonYrzU8M5f9mAV5KiaJ9YZWCSgoUx6/AKuobb1cLgB8R+mDHgx6FoP3XS3K8bAwShz98sojuAKmTGzJMZVUjH8mjW+9ebYjtw3oZr/ZM2F2NZuCPN4Rx+K5koMfR2IE1OQKoZbkgLJSc36yUmis7MN91L0xIgntCKhqpZkRX45VjWsZi4BcKQnK5L2YfUqueZ7qvPzpF7sWDDcWYqkLZNbxfRk+gUVdabq/uOPYR8v0O0EFONv7h2kiU3E1s7Rm8WOukfwfqa5Nsw7FSNF2kjL0PhPbGPBQ6kVbLQMsvmxM7x/AA2owHe8l1yHgzyCgd7YTFOdi";

    /**
     * Generated Key for Tensorflow use
     */
    public static final String TENSORFLOW_KEY = "Adb83BH/////AAABmTheak2ntU3VnH1pRcX2UDVJc60lqKXP9o54kAOKZoMvggLhrVVWOQ06E0yXEF3xRwJADjy5U2N519egNSjJ+Kj6jr05a6UmqLTEXS8elc2jYhx+T5P0pbc3ByKBdqw0lwBzL15jcqFrNDmbTH5hsuZjRP0RfvE1k/S2VW3wvD8U3GNtd2wb7xdQbmysXoDrNk0s+bgyn4mCX8jNL33RvYuIYfDKkC215c+jbYjn4rDAHNyM02Va777s5mcbYTb3LAX0iVYQApbtX4MjcPyU+D5p5dRQVYTE2hVtbMVvJg66m7ZcZ8aRV1GwTEYYVhq6z/iT3+cDH2pjNXtb0mGwHwyAnCwSMVqFtpbQ4DrC/3uj";

    /**
     * The width of the FTC field from one side to the other
     */
    public static final float mmFTCFieldWidth = (12/*ft*/ * 12/*in*/) * mmPerInch;

    /**
     * Half of the width of the FTC field
     */
    public static final float mmFTCHalfFieldWidth = mmFTCFieldWidth/2f;

    /**
     * Quarter of the width of the FTC field
     */
    public static final float mmFTCQuadFieldWidth = mmFTCFieldWidth/4f;

    /**
     * The height of the ImageTarget (from the image center to the floor)
     */
    public static final float mmTargetHeight = (6/*in*/) * mmPerInch;

    /**
     * Select which camera you want use.  The FRONT camera is the one on the same side as the screen.
     * Valid choices are:  BACK or FRONT
     */
    public static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;

    /**
     * Orientation of phone used for Vuforia
      */
    public static final boolean PHONE_IS_PORTRAIT = false;

    //--------------------------------CONFIGURATION VALUES--------------------
    /**
     * Front right motor name
     */
    public static final String motorFRS = "motorFR";

    /**
     * Front left motor name
     */
    public static final String motorFLS = "motorFL";

    /**
     * Back right motor name
     */
    public static final String motorBRS = "motorBR";

    /**
     * Back left motor name
     */
    public static final String motorBLS = "motorBL";

    /**
     *
     */
    public static final String intakeS = "intake";

    /**
     * Intake pivot motor name
      */
    public static final String pivotS = "spin";

    /**
     * IMU name
     */
    public static final String imuS = "imu";

    /**
     * Ultrasonic names
     */
    public static final String backUltraS = "Back";
    public static final String rightUltraS = "Right";
    public static final String leftUltraS = "Left";
    public static final String frontUltraS = "Front";

    /**
     * Linear Slide motor name
     */
    public static final String linearSlideS = "lift";

    /**
     * Servo located on the linear slide to rotate the claw
     */
    public static final String rotateS = "out";

    /**
     * Claw servo name
     */
    public static final String clawS = "grab";
}
