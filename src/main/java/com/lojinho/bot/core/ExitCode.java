package com.lojinho.bot.core;

public enum ExitCode {
  REBOOT(100),
  STOP(101),
  UPDATE(200),
  GENERIC_ERROR(300),
  SHITTY_CONFIG(301),
  DISCONNECTED(302),
  UNKNOWN(-1);

  private final int code;

  ExitCode(int code) {
    this.code = code;
  }

  public static ExitCode fromCode(int exitCode) {
    for (ExitCode code : ExitCode.values()) {
      if (code.getCode() == exitCode) {
        return code;
      }
    }
    return ExitCode.UNKNOWN;
  }

  public int getCode() {
    return code;
  }
}
