// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.CANcoder;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveConstants;

/**
 * Class to represent and handle a swerve module
 * A module's state is measured by a CANCoder for the absolute position, integrated CANEncoder for relative position
 * for both steeration and linear movement
 */
public class SwerveModule extends SubsystemBase {

	private final SparkMax driveMtr;
	private final SparkMax steerMtr;

	private final RelativeEncoder driveEnc;
	private final RelativeEncoder steerEnc;

	public final CANcoder canCoder;

	private final SparkClosedLoopController steerController;
	private final SparkClosedLoopController driveController;

	/**
	 * Constructs a new SwerveModule.
	 * 
	 * <p>SwerveModule represents and handles a swerve module.
	 * 
	 * @param driveMtrId CAN ID of the NEO drive motor.
	 * @param steerMtrId CAN ID of the NEO steer motor.
	 * @param canCoderId CAN ID of the CANCoder.
	 * @param measuredOffsetRadians Offset of CANCoder reading from forward.
	 */
	public SwerveModule(int driveMtrId, int steerMtrId, int canCoderId, boolean invertDrive, boolean invertSteer) {
		//CANcoder data and offset
		canCoder = new CANcoder(canCoderId);

		//drive motor data
		SparkMaxConfig driveConf = new SparkMaxConfig();
		driveConf.inverted(invertDrive);
		driveConf.idleMode(IdleMode.kBrake);
		driveMtr = new SparkMax(driveMtrId, MotorType.kBrushless);
		driveEnc = driveMtr.getEncoder();
		driveConf.smartCurrentLimit(DriveConstants.driveCurrentLimitAmps);
		driveController = driveMtr.getClosedLoopController();
		driveConf.closedLoop.pid(DriveConstants.drivekP, 0, DriveConstants.drivekD);
		driveConf.encoder.positionConversionFactor(DriveConstants.driveMetersPerEncRev);
		driveConf.encoder.velocityConversionFactor(DriveConstants.driveMetersPerSecPerRPM);
		driveMtr.configure(driveConf, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
		
		//set the drive encoder position to zero        
		driveEnc.setPosition(0);

		//steer motor data
		SparkMaxConfig steerConf = new SparkMaxConfig();
		steerConf.inverted(invertSteer);
		steerConf.idleMode(IdleMode.kBrake);
		steerMtr = new SparkMax(steerMtrId, MotorType.kBrushless);
		steerEnc = steerMtr.getEncoder();
		steerController = steerMtr.getClosedLoopController();
		steerConf.closedLoop.pid(DriveConstants.steerkP, 0, DriveConstants.steerkD);
		steerConf.encoder.positionConversionFactor(DriveConstants.steerRadiansPerEncRev);
		steerConf.encoder.velocityConversionFactor(DriveConstants.steerRadiansPerSecPerRPM);
		steerMtr.configure(steerConf, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);

		//initializes the steer encoder position to the CANCoder position, accounting for an offset if any
		steerEnc.setPosition(getCanCoderAngle().getRadians());

	}

	/**
	 * Returns the current position of the module.
	 *
	 * @return The current position of the module.
	 */
	public SwerveModulePosition getPosition() {
		return new SwerveModulePosition(
			driveEnc.getPosition(), getSteerEncAngle());
	}

	/**
	 * Resets the distance traveled by the module to zero.
	 */
	public void resetDriveDistance() {
		driveEnc.setPosition(0.0);
	}

	/**
	 * Returns the current drive distance of the module.
	 * 
	 * @return The current drive distance of the module.
	 */
	public double getDriveDistanceMeters() {
		return driveEnc.getPosition();
	}
	
	/**
	 * Returns the current absolute angle of the module from the CANCoder.
	 * This measurement does not account for offset.
	 * It is preferred to use the method getSteerEncAngle().
	 * 
	 * @return The value of the CANCoder.
	 */
	public Rotation2d getCanCoderAngle() {
		return new Rotation2d(Units.rotationsToRadians(
			canCoder.getAbsolutePosition().getValueAsDouble()
			));
	}

	/**
	 * Returns the current absolute angle of the module from the steer motor encoder.
	 * This measurement accounts for offset.
	 * 
	 * @return The current absolute angle of the module.
	 */
	public Rotation2d getSteerEncAngle() {
		return new Rotation2d(steerEnc.getPosition());
	}

	/**
	 * Returns the current velocity of the module from the drive motor encoder.
	 * 
	 * @return The current velocity of the module in meters per second.
	 */
	public double getVelocityMetersPerSec() {
		return driveEnc.getVelocity();
	}

	/**
	 * Calculates the angle motor setpoint based on the desired angle and the current angle measurement.
	 * 
	 * @param targetAngle The desired angle to set the module in radians.
	 * @param currentAngle The current angle of the module in radians.
	 * 
	 * @return The adjusted target angle for the module in radians.
	 */
	public double calculateAdjustedAngle(double targetAngle, double currentAngle) {
		double modAngle = currentAngle % (2.0 * Math.PI);

		if (modAngle < 0.0) modAngle += 2.0 * Math.PI;
		
		double newTarget = targetAngle + currentAngle - modAngle;

		if (targetAngle - modAngle > Math.PI) newTarget -= 2.0 * Math.PI;
		else if (targetAngle - modAngle < -Math.PI) newTarget += 2.0 * Math.PI;

		return newTarget;
	}

	/**
	 * Sets the desired state of the swerve module and optimizes it.
	 * <p>If closed-loop, uses PID and a feedforward to control the speed.
	 * If open-loop, sets the speed to a percentage. Open-loop control should
	 * only be used if running an autonomour trajectory.
	 *
	 * @param desiredState Object that holds a desired linear and steerational setpoint.
	 * @param isOpenLoop True if the velocity control is open- or closed-loop.
	 */
	public void setDesiredState(SwerveModuleState desiredState, boolean isOpenLoop) {
		
		// Optimizes speed and angle to minimize change in heading
		// (e.g. module turns 1 degree and reverses drive direction to get from 90 degrees to -89 degrees)
		desiredState.optimize(getSteerEncAngle());

		steerController.setReference(
			calculateAdjustedAngle(
				desiredState.angle.getRadians(),
				getSteerEncAngle().getRadians()),
			ControlType.kPosition
		);

		if(isOpenLoop) 
			driveMtr.set(desiredState.speedMetersPerSecond / DriveConstants.kFreeMetersPerSecond);
		else {
			double speedMetersPerSecond = desiredState.speedMetersPerSecond * DriveConstants.maxDriveSpeedMetersPerSec;

			driveController.setReference(
				speedMetersPerSecond,
				ControlType.kVelocity,
				ClosedLoopSlot.kSlot0, 
				DriveConstants.driveFF.calculate(speedMetersPerSecond)
			);
		}
	}
}
