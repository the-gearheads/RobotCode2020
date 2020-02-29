/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.Logger;
import io.github.oblarg.oblog.annotations.Log;

public class Intake extends SubsystemBase {
  /**
   * Creates a new Intake.
   */
  private final CANSparkMax lExtension;
  private final CANSparkMax rExtension;
  private final CANSparkMax intake;

  private final CANEncoder lEncoder;
  private final CANEncoder rEncoder;

  @Log
  private double debug0;
  @Log
  private double debug1;
  @Log
  private double debug2;
  @Log
  private double debug3;

  public Intake() {
    lExtension = new CANSparkMax(6, MotorType.kBrushless);
    rExtension = new CANSparkMax(30, MotorType.kBrushless);
    lExtension.setInverted(true);
    rExtension.setInverted(true);

    intake = new CANSparkMax(28, MotorType.kBrushless);
    lEncoder = lExtension.getEncoder();
    rEncoder = rExtension.getEncoder();
    lEncoder.setPosition(0);
    rEncoder.setPosition(0);

    Logger.configureLoggingAndConfig(this, false);
  }

  public void extend(double left, double right) {
    lExtension.setVoltage(left);
    rExtension.setVoltage(right);
  }

  @Override
  public void periodic() {
    debug0 = lExtension.getOutputCurrent();
    debug1 = rExtension.getOutputCurrent();
    debug2 = lEncoder.getPosition();
    debug3 = rEncoder.getPosition();
  }

  public void intake(double speed) {
    intake.set(speed);
  }

  public void retract(double speed) {
    lExtension.set(-speed);
    rExtension.set(-speed);
  }

  public double getLPosition() {
    return lEncoder.getPosition();
  }

  public double getRPosition() {
    return rEncoder.getPosition();
  }
}
