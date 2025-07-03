// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.commands.Autos;
import frc.robot.commands.SwerveDrive;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.SwerveSys;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();
    private SwerveSys m_SwerveSys = new SwerveSys();

  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final Joystick driverController = new Joystick(0);
  private final JoystickButton zeroGyro = new JoystickButton(driverController, 11);

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the trigger bindings
    configureBindings();
  }

  private double getThrottle() {
    double throttle = ((1-driverController.getThrottle())/2.5) + 0.2;
    return throttle;
  }

  private void configureBindings() {

    m_SwerveSys.setDefaultCommand(new SwerveDrive(
      () -> getThrottle(),
			() -> MathUtil.applyDeadband(driverController.getY(), 0.15),
			() -> MathUtil.applyDeadband(driverController.getX(), 0.15),
			() -> MathUtil.applyDeadband(driverController.getZ(), 0.15),
			true,
			true,
			m_SwerveSys
		));
  
    zeroGyro.onTrue(new InstantCommand(() -> SwerveSys.resetHeading()));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return Autos.exampleAuto(m_exampleSubsystem);
  }
}
