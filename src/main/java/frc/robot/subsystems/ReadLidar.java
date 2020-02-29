/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import frc.robot.util.Lidar;
import io.github.oblarg.oblog.Logger;
import io.github.oblarg.oblog.annotations.Log;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ReadLidar extends SubsystemBase {

  private final Lidar lidar;

  @Log
  private double distance;

  public ReadLidar() {
    lidar = new Lidar(I2C.Port.kMXP);
    Logger.configureLoggingAndConfig(this, false);
  }

  @Override
  public void periodic() {
    distance = lidar.getDistance(true);
  }
}