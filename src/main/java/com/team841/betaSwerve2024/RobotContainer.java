package com.team841.betaSwerve2024;

import com.ctre.phoenix6.Utils;
import com.ctre.phoenix6.mechanisms.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.team841.Util.BioCommandPS5Controller;
import com.team841.Util.BioCommandXboxController;
import com.team841.betaSwerve2024.Constants.Manifest;
import com.team841.betaSwerve2024.Constants.Swerve;
import com.team841.betaSwerve2024.Drive.Drivetrain;
import com.team841.betaSwerve2024.Superstructure.*;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;

public class RobotContainer {
  private double MaxSpeed = Swerve.kSpeedAt12VoltsMps; // 6 meters per second desired top speed
  private double MaxAngularRate = 4 * Math.PI;
  // 1.5 * Math.PI; // 3/4 of a rotation per second max angular velocity
  public final BioCommandPS5Controller joystick = Manifest.JoystickManifest.joystick; // My joystick
  public final BioCommandXboxController cojoystick = Manifest.JoystickManifest.cojoystick;
  /* Setting up bindings for necessary control of the swerve drive platform */
  private final Drivetrain drivetrain = Manifest.SubsystemManifest.drivetrain; // My drivetrain
  private final Intake intake = Manifest.SubsystemManifest.intake;

  private final Indexer indexer = Manifest.SubsystemManifest.indexer;

  private final Shooter shooter = Manifest.SubsystemManifest.shooter;

  private final Arm arm = Manifest.SubsystemManifest.arm;

  private final LED led = Manifest.SubsystemManifest.led;

  private final Hanger hanger = Manifest.SubsystemManifest.hanger;

  private final SwerveRequest.FieldCentric drive =
      new SwerveRequest.FieldCentric()
          .withDeadband(MaxSpeed * 0.1)
          .withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
          .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // I want field-centric

  // driving in open loop
  private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
  private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();
  private final Telemetry logger = new Telemetry(MaxSpeed);

  private final SendableChooser<Command> autoChooser;

  private void configureBindings() {
    drivetrain.setDefaultCommand( // Drivetrain will execute this command periodically
        drivetrain.applyRequest(
            () ->
                drive
                    .withVelocityX(-joystick.getLeftY() * MaxSpeed) // Drive forward with
                    // negative Y (forward)
                    .withVelocityY(
                        -joystick.getLeftX() * MaxSpeed) // Drive left with negative X (left)
                    .withRotationalRate(
                        -joystick.getRightX()
                            * MaxAngularRate) // Drive counterclockwise with negative X (left)
            ));

    // joystick.cross().whileTrue(drivetrain.applyRequest(() -> brake));
    // joystick.circle().whileTrue(drivetrain.applyRequest(() -> point.withModuleDirection(new
    // Rotation2d(-joystick.getLeftY(), -joystick.getLeftX()))));

    // reset the field-centric heading on left bumper press
    joystick.touchpad().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldRelative()));

    if (Utils.isSimulation()) {
      drivetrain.seedFieldRelative(new Pose2d(new Translation2d(), Rotation2d.fromDegrees(90)));
    } else {
      drivetrain.registerTelemetry(logger::telemeterize);
    }
  }

  // xbox
  public void configureCoBindings() {

    Command c_command = new IntakeCommand(intake, indexer);
    cojoystick.leftBumper().whileTrue(c_command);
    cojoystick
        .leftTrigger()
        .onTrue(new InstantCommand(shooter::spinUp))
        .onFalse(new InstantCommand(shooter::stopShooter));
    cojoystick
        .rightTrigger()
        .onTrue(
            new ConditionalCommand(
                new InstantCommand(indexer::Pass),
                new InstantCommand(indexer::stopIndexer),
                () -> shooter.isShooting()))
        .onFalse(new InstantCommand(indexer::stopIndexer));
    cojoystick
        .rightBumper()
        .onTrue(
            new SequentialCommandGroup(
                new InstantCommand(indexer::stopIndexer),
                new InstantCommand(shooter::stopShooter)));
    cojoystick.povUp().whileTrue(new InstantCommand(hanger::ExtendHanger));
    cojoystick.povDown().whileTrue(new InstantCommand(hanger::RetractHanger));
    cojoystick.povCenter().whileTrue(new InstantCommand(hanger::StopHanger));
    cojoystick
        .x()
        .onTrue(new InstantCommand(shooter::ampShot))
        .onFalse(new InstantCommand(shooter::stopShooter));
    cojoystick
        .b()
        .onTrue(
            new ParallelCommandGroup(
                new InstantCommand(intake::outTake), new InstantCommand(indexer::reverseIndexer)))
        .onFalse(
            new SequentialCommandGroup(
                new InstantCommand(indexer::stopIndexer), new InstantCommand(intake::stopIntake)));
  }

  public RobotContainer() {
    // Register Named Commands
    NamedCommands.registerCommand("IntakeOn", new IntakeCommand(intake, indexer));
    NamedCommands.registerCommand(
        "Shoot",
        new ParallelCommandGroup(
                new InstantCommand(shooter::spinUp),
                new SequentialCommandGroup(new WaitCommand(1), new InstantCommand(indexer::Pass)))
            .withTimeout(3));
    NamedCommands.registerCommand("SpinUp", new InstantCommand(shooter::spinUp));
    NamedCommands.registerCommand("JustShoot", new InstantCommand(indexer::Pass).withTimeout(0.5));
    NamedCommands.registerCommand(
        "ALLSYSTEMSGO",
        new ParallelCommandGroup(
                new InstantCommand(intake::intake),
                new InstantCommand(shooter::spinUp),
                new InstantCommand(indexer::Pass))
            .withTimeout(2.5));
    NamedCommands.registerCommand(
        "FunnyInake",
        new ParallelCommandGroup(
                new InstantCommand(intake::intake), new InstantCommand(indexer::Pass))
            .withTimeout(0.75));
    NamedCommands.registerCommand(
        "JustStop",
        new ParallelCommandGroup(
            new InstantCommand(indexer::stopIndexer), new InstantCommand(shooter::stopShooter)));

    configureBindings();
    configureCoBindings();
    autoChooser = AutoBuilder.buildAutoChooser(); // Default auto will be `Commands.none()`
    SmartDashboard.putData("Auto Mode", autoChooser);

    led.setDefaultCommand(new UpdateLED(led, indexer));
  }

  public Command getAutonomousCommand() {
    // auto chooser on shuffleboard
    return autoChooser.getSelected();
  }
}
