package com.team841.betaSwerve2024.Constants;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrainConstants;
import com.ctre.phoenix6.mechanisms.swerve.SwerveModule.ClosedLoopOutputType;
import com.ctre.phoenix6.mechanisms.swerve.SwerveModuleConstants;
import com.ctre.phoenix6.mechanisms.swerve.SwerveModuleConstants.SteerFeedbackType;
import com.ctre.phoenix6.mechanisms.swerve.SwerveModuleConstantsFactory;
import com.team841.betaSwerve2024.Drive.Drivetrain;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;

public class Swerve {
  // Both sets of gains need to be tuned to your individual robot.

  // The steer motor uses any SwerveModule.SteerRequestType control request with the
  // output type specified by SwerveModuleConstants.SteerMotorClosedLoopOutput
  private static final Slot0Configs steerGains =
      new Slot0Configs().withKP(100).withKI(0).withKD(0.2).withKS(0).withKV(1.5).withKA(0);
  // When using closed-loop control, the drive motor uses the control
  // output type specified by SwerveModuleConstants.DriveMotorClosedLoopOutput
  private static final Slot0Configs driveGains =
      new Slot0Configs().withKP(3).withKI(0).withKD(0).withKS(0).withKV(0).withKA(0);

  // The closed-loop output type to use for the steer motors;
  // This affects the PID/FF gains for the steer motors
  private static final ClosedLoopOutputType steerClosedLoopOutput = ClosedLoopOutputType.Voltage;
  // The closed-loop output type to use for the drive motors;
  // This affects the PID/FF gains for the drive motors
  private static final ClosedLoopOutputType driveClosedLoopOutput = ClosedLoopOutputType.Voltage;

  // The stator current at which the wheels start to slip;
  // This needs to be tuned to your individual robot
  // private static final double kSlipCurrentA = 300.0;
  private static final double kSlipCurrentA = 80.0;

  // Theoretical free speed (m/s) at 12v applied output;
  // This needs to be tuned to your individual robot
  public static final double kSpeedAt12VoltsMps = 5.96;

  // Every 1 rotation of the azimuth results in kCoupleRatio drive motor turns;
  // This may need to be tuned to your individual robot
  private static final double kCoupleRatio = 3.125;

  private static final double kDriveGearRatio = 5.357142857142857;
  private static final double kSteerGearRatio = 21.428571428571427;
  private static final double kWheelRadiusInches = 2;

  private static final boolean kSteerMotorReversed = true;
  private static final boolean kInvertLeftSide = true;
  private static final boolean kInvertRightSide = false;

  private static final String kCANbusName = "canivore2";
  private static final int kPigeonId = 0;

  // These are only used for simulation
  private static final double kSteerInertia = 0.00001;
  private static final double kDriveInertia = 0.001;
  // Simulated voltage necessary to overcome friction
  private static final double kSteerFrictionVoltage = 0.25;
  private static final double kDriveFrictionVoltage = 0.25;

  private static final SwerveDrivetrainConstants DrivetrainConstants =
      new SwerveDrivetrainConstants().withPigeon2Id(kPigeonId).withCANbusName(kCANbusName);

  private static final SwerveModuleConstantsFactory ConstantCreator =
      new SwerveModuleConstantsFactory()
          .withDriveMotorGearRatio(kDriveGearRatio)
          .withSteerMotorGearRatio(kSteerGearRatio)
          .withWheelRadius(kWheelRadiusInches)
          .withSlipCurrent(kSlipCurrentA)
          .withSteerMotorGains(steerGains)
          .withDriveMotorGains(driveGains)
          .withSteerMotorClosedLoopOutput(steerClosedLoopOutput)
          .withDriveMotorClosedLoopOutput(driveClosedLoopOutput)
          .withSpeedAt12VoltsMps(kSpeedAt12VoltsMps)
          .withSteerInertia(kSteerInertia)
          .withDriveInertia(kDriveInertia)
          .withSteerFrictionVoltage(kSteerFrictionVoltage)
          .withDriveFrictionVoltage(kDriveFrictionVoltage)
          .withFeedbackSource(SteerFeedbackType.FusedCANcoder)
          .withCouplingGearRatio(kCoupleRatio)
          .withSteerMotorInverted(kSteerMotorReversed);

  // Front Left
  private static final int kFrontLeftDriveMotorId = 3;
  private static final int kFrontLeftSteerMotorId = 4;
  private static final int kFrontLeftEncoderId = 2;
  private static final double kFrontLeftEncoderOffset = -0.421875;

  private static final double kFrontLeftXPosInches = 10.375;
  private static final double kFrontLeftYPosInches = 10.375;

  // Front Right
  private static final int kFrontRightDriveMotorId = 1;
  private static final int kFrontRightSteerMotorId = 2;
  private static final int kFrontRightEncoderId = 1;
  private static final double kFrontRightEncoderOffset = -0.472412109375;

  private static final double kFrontRightXPosInches = 10.375;
  private static final double kFrontRightYPosInches = -10.375;

  // Back Left
  private static final int kBackLeftDriveMotorId = 5;
  private static final int kBackLeftSteerMotorId = 6;
  private static final int kBackLeftEncoderId = 3;
  private static final double kBackLeftEncoderOffset = 0.435302734375;

  private static final double kBackLeftXPosInches = -10.375;
  private static final double kBackLeftYPosInches = 10.375;

  // Back Right
  private static final int kBackRightDriveMotorId = 7;
  private static final int kBackRightSteerMotorId = 8;
  private static final int kBackRightEncoderId = 4;
  private static final double kBackRightEncoderOffset = -0.0185546875;

  private static final double kBackRightXPosInches = -10.375;
  private static final double kBackRightYPosInches = -10.375;

  private static final SwerveModuleConstants FrontLeft =
      ConstantCreator.createModuleConstants(
          kFrontLeftSteerMotorId,
          kFrontLeftDriveMotorId,
          kFrontLeftEncoderId,
          kFrontLeftEncoderOffset,
          Units.inchesToMeters(kFrontLeftXPosInches),
          Units.inchesToMeters(kFrontLeftYPosInches),
          kInvertLeftSide);
  private static final SwerveModuleConstants FrontRight =
      ConstantCreator.createModuleConstants(
          kFrontRightSteerMotorId,
          kFrontRightDriveMotorId,
          kFrontRightEncoderId,
          kFrontRightEncoderOffset,
          Units.inchesToMeters(kFrontRightXPosInches),
          Units.inchesToMeters(kFrontRightYPosInches),
          kInvertRightSide);
  private static final SwerveModuleConstants BackLeft =
      ConstantCreator.createModuleConstants(
          kBackLeftSteerMotorId,
          kBackLeftDriveMotorId,
          kBackLeftEncoderId,
          kBackLeftEncoderOffset,
          Units.inchesToMeters(kBackLeftXPosInches),
          Units.inchesToMeters(kBackLeftYPosInches),
          kInvertLeftSide);
  private static final SwerveModuleConstants BackRight =
      ConstantCreator.createModuleConstants(
          kBackRightSteerMotorId,
          kBackRightDriveMotorId,
          kBackRightEncoderId,
          kBackRightEncoderOffset,
          Units.inchesToMeters(kBackRightXPosInches),
          Units.inchesToMeters(kBackRightYPosInches),
          kInvertRightSide);

  public static double MaxAngularRate =
      4 * Math.PI; // 1.5 * Math.PI; // 3/4 of a rotation per second max angular velocity
  public static double MaxSpeed = kSpeedAt12VoltsMps;
  protected static final Drivetrain DriveTrain =
      new Drivetrain(DrivetrainConstants, FrontLeft, FrontRight, BackLeft, BackRight);

  public static final ProfiledPIDController TurnController =
      new ProfiledPIDController(7, 0.0, 0.0, new TrapezoidProfile.Constraints(0, 0));

  public static final TrapezoidProfile.Constraints rotationConstraints =
      new TrapezoidProfile.Constraints(Math.toRadians(720), Math.toRadians(540 - 180));

  public static final ProfiledPIDController BioControlController =
      new ProfiledPIDController(14, 0.0, 0.0, new TrapezoidProfile.Constraints(0, 0));

  public static class Vision {
    public static String kLimelightFrontName = "limelight-front";
  }
}
