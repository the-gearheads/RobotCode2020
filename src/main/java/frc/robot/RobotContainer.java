/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.util.StreamDeck;
import frc.robot.util.StreamDeckButton;

public class RobotContainer {
  public static XboxController controller;
  public static StreamDeck streamdeck;
  public StreamDeckButton button0;

  public RobotContainer() {
    streamdeck = new StreamDeck(0, 15);
    button0 = new StreamDeckButton(streamdeck, 0);
    button0.whenPressed(new yoink());
    controller = new XboxController(Constants.CONTROLLER_PORT);
    configureButtonBindings();
  }

  class yoink implements Runnable {
    public void run() {
      boolean status = button0.getStatus();
      button0.setStatus(!status);
    }
  }

  private void configureButtonBindings() {
  }

  public Command getAutonomousCommand() {
    return null;
  }
}
