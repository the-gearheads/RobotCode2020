/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.Collections;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.spline.SplineParameterizer.MalformedSplineException;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.shooter.Elevator;
import frc.robot.commands.shooter.Shot;
import frc.robot.commands.test.Spin;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.Shooter;
import frc.robot.util.AngleCharacterize;
import frc.robot.util.JoystickTrigger;
import frc.robot.util.Ramsete;
import frc.robot.util.StreamDeck;
import frc.robot.util.StreamDeckButton;

public class RobotContainer {
  public static XboxController controller;
  private static StreamDeck streamdeck;
  private static DriveSubsystem drive;
  private static Shooter shooter;

  public RobotContainer() {
    drive = new DriveSubsystem();
    shooter = new Shooter();

    controller = new XboxController(Constants.CONTROLLER_PORT);
    streamdeck = new StreamDeck(0, 15);
    configureButtonBindings();
  }

  // run on any mode init
  public void init() {
    streamdeck.reset();
  }

  public static XboxController getController() {
    return controller;
  }

  private void configureButtonBindings() {

    SequentialCommandGroup shootGroup = (new Shot(shooter)).withTimeout(1.5)
        .andThen(new Shot(shooter).alongWith(new Elevator(shooter)).withTimeout(2));
    // new JoystickButton(controller, XboxController.Button.kA.value).whenPressed(new AngleCharacterize(drive));
    new StreamDeckButton(streamdeck, 0, "arms up").whenPressed(new Spin(drive).withTimeout(1));
    new StreamDeckButton(streamdeck, 1, "intake out").whenPressed(new Spin(drive).withTimeout(1));
    new StreamDeckButton(streamdeck, 2, "red").whenPressed(new Spin(drive).withTimeout(1));
    new StreamDeckButton(streamdeck, 3, "elevator plus").whenPressed(new Spin(drive).withTimeout(1));
    new StreamDeckButton(streamdeck, 4, "shoot");
    new StreamDeckButton(streamdeck, 5, "color wheel").whenPressed(new Spin(drive).withTimeout(1));
    new StreamDeckButton(streamdeck, 6, "green").whenPressed(new Spin(drive).withTimeout(1));
    new StreamDeckButton(streamdeck, 7, "rotate").whenPressed(new Spin(drive).withTimeout(1));
    new StreamDeckButton(streamdeck, 8, "yellow").whenPressed(new Spin(drive).withTimeout(1));
    new StreamDeckButton(streamdeck, 9, "unjam").whenPressed(new Spin(drive).withTimeout(1));
    new StreamDeckButton(streamdeck, 10, "arms down").whenPressed(new Spin(drive).withTimeout(1));
    new StreamDeckButton(streamdeck, 11, "intake").whenPressed(new Spin(drive).withTimeout(1));
    new StreamDeckButton(streamdeck, 12, "blue").whenPressed(new Spin(drive).withTimeout(1));
    new StreamDeckButton(streamdeck, 13, "elevator minus").whenPressed(new Spin(drive).withTimeout(1));
    new StreamDeckButton(streamdeck, 14, "aim").whenPressed(new Spin(drive).withTimeout(1));

    JoystickTrigger lTrigger = new JoystickTrigger(controller, XboxController.Axis.kLeftTrigger, 0.9);
    lTrigger.whileHeld(new Elevator(shooter));
    JoystickTrigger rTrigger = new JoystickTrigger(controller, XboxController.Axis.kRightTrigger, 0.9);
    rTrigger.whileHeld(new Shot(shooter));
    JoystickButton buttonA = new JoystickButton(controller, XboxController.Button.kA.value);
    buttonA.whenPressed(shootGroup);
    JoystickButton buttonB = new JoystickButton(controller, XboxController.Button.kB.value);
    buttonB.whenPressed(new AngleCharacterize(drive));
    JoystickButton buttonX = new JoystickButton(controller, XboxController.Button.kX.value);
    buttonX.whenPressed(this::routeToOrigin);
  }

  public Command getAutonomousCommand() {
    final DifferentialDriveVoltageConstraint autoVoltageConstraint = new DifferentialDriveVoltageConstraint(
        Constants.leftFF, drive.getKinematics(), 10);
    final TrajectoryConfig config = new TrajectoryConfig(Constants.MAX_VELOCITY, Constants.MAX_ACCEL)
        .setKinematics(drive.getKinematics()).addConstraint(autoVoltageConstraint);
    // An example trajectory to follow. All units in meters.
    Trajectory trajectory = TrajectoryGenerator.generateTrajectory(new Pose2d(0, 0, new Rotation2d()),
        Collections.emptyList(), new Pose2d(1, 0, new Rotation2d()), config);

    Trajectory transformed = trajectory.transformBy(drive.getPose().minus(new Pose2d()));
    System.out.println(transformed.getTotalTimeSeconds());
    Ramsete ramseteCommand = new Ramsete(transformed,
        new RamseteController(Constants.RAMSETE_B, Constants.RAMSETE_ZETA), drive);
    ramseteCommand.schedule();
    return ramseteCommand;
  }

  public void routeToOrigin() {
    final DifferentialDriveVoltageConstraint autoVoltageConstraint = new DifferentialDriveVoltageConstraint(
        Constants.leftFF, drive.kinematics, 2);
    final TrajectoryConfig config = new TrajectoryConfig(Constants.MAX_VELOCITY, Constants.MAX_ACCEL)
        .setKinematics(drive.kinematics).addConstraint(autoVoltageConstraint);

    try {
      Trajectory trajectory = TrajectoryGenerator.generateTrajectory(drive.getPose(), Collections.emptyList(),
          new Pose2d(0, 0, new Rotation2d(0)), config);
      Ramsete ramseteCommand = new Ramsete(trajectory,
          new RamseteController(Constants.RAMSETE_B, Constants.RAMSETE_ZETA), drive);
      ramseteCommand.schedule();
    } catch (MalformedSplineException e) {
      DriverStation.reportError("Failed to generate routeToOrigin trajectory", e.getStackTrace());
    }

  }

}
