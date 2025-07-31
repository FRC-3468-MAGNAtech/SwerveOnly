// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.util.Units;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
  }

  
	public static class CANDevices {
		public static final int pigeonId = 2;

		public static final int frontLeftSteerMtrId = 12;
		public static final int frontLeftDriveMtrId =11;
		public static final int frontLeftCanCoderId = 13;

		public static final int frontRightSteerMtrId =21;
		public static final int frontRightDriveMtrId = 22;
		public static final int frontRightCanCoderId = 23;

		public static final int backLeftSteerMtrId = 15;
		public static final int backLeftDriveMtrId = 14;
		public static final int backLeftCanCoderId = 16;

		public static final int backRightSteerMtrId = 25;
		public static final int backRightDriveMtrId = 24;
		public static final int backRightCanCoderId = 26;
	}

	public static class DriveConstants {
		/**
		 * The track width from wheel center to wheel center.
		 */
		public static final double trackWidth = Units.inchesToMeters(24);

		/**
		 * The track length from wheel center to wheel center.
		 */
		public static final double wheelBase = Units.inchesToMeters(24.5);

		/**
		 * The SwerveDriveKinematics used for control and odometry.
		 */
		public static final SwerveDriveKinematics kinematics = 
		new SwerveDriveKinematics(
			new Translation2d(trackWidth / 2.0, wheelBase / 2.0),  // front left
			new Translation2d(trackWidth / 2.0, -wheelBase / 2.0), // front right
			new Translation2d(-trackWidth / 2.0, wheelBase / 2.0), // back left
			new Translation2d(-trackWidth / 2.0, -wheelBase / 2.0) // back right
		);
		/**
		* The gear reduction from the drive motor to the wheel.
		* 
		* The drive gear ratios for the different levels can be found from the chart at
		* swervedrivespecialties.com/products/mk41-swerve-module.
		*/
	   	public static final double driveMtrGearReduction = (14.0 / 50.0) * (27.0 / 17.0) * (15.0 / 45.0);

	   	/**
		* The gear reduction from the steer motor to the wheel.
		*/
	   	public static final double steerMtrGearReduction = (14.0 / 50.0) * (10.0 / 60.0);
	   
	   	/**
		* Values for our specific MK4i modules
		*/
	   	public static final double wheelRadiusMeters = Units.inchesToMeters(2);
	   	public static final double wheelCircumferenceMeters = 2.0 * wheelRadiusMeters * Math.PI;
	   	public static final double driveBaseRadius = Units.inchesToMeters(14);

	   	public static final double driveMetersPerEncRev = wheelCircumferenceMeters * driveMtrGearReduction;
	   	public static final double driveMetersPerSecPerRPM = driveMetersPerEncRev / 60.0;

	   	public static final double steerRadiansPerEncRev = 2 * Math.PI * DriveConstants.steerMtrGearReduction;
	   	public static final double steerRadiansPerSecPerRPM = steerRadiansPerEncRev / 60;

	   	public static final double kFreeMetersPerSecond = 5820 * driveMetersPerSecPerRPM;

	   	public static final double steerMtrMaxSpeedRadPerSec = 2.0;
	   	public static final double steerMtrMaxAccelRadPerSecSq = 1.0;

	   	public static final double maxDriveSpeedMetersPerSec = 3.5;

		public static final int driveCurrentLimitAmps = 100;

	   	/**
		* The rate the robot will spin with full Rot command.
		*/
		public static final double maxTurnRateRadiansPerSec = 2.0 * Math.PI;
		public static final double drivekP = 0.005;
		public static final double driveI = 0;
		public static final double drivekD = 0.0;

		public static final double steerkP = 1;
		public static final double steerI = 0;
		public static final double steerkD = 0.0;
		
		public static final double ksVolts = 0.667;
		public static final double kvVoltSecsPerMeter = 2.44;
		public static final double kaVoltSecsPerMeterSq = 0.0;

		public static final SimpleMotorFeedforward driveFF = new SimpleMotorFeedforward(ksVolts, kvVoltSecsPerMeter, kaVoltSecsPerMeterSq);
		
		// Some wheels would spin backwards
		public static final boolean frontLeftDriveInvert = true;
		public static final boolean frontRightDriveInvert = true;
		public static final boolean backLeftDriveInvert = true;
		public static final boolean backRightDriveInvert = true;

		// This is just-in-case
		public static final boolean frontLeftSteerInvert = true;
		public static final boolean frontRightSteerInvert = true;
		public static final boolean backLeftSteerInvert = true;
		public static final boolean backRightSteerInvert = true;
	}
}
