package io.mapsmessaging.devices.i2c.devices.output.led.ht16k33;

import lombok.Getter;

public enum AlphaNumericLed {
  TOP(                  0b0000000000000001),
  TOP_RIGHT(            0b0000000000000010),
  BOTTOM_RIGHT(         0b0000000000000100),
  BOTTOM(               0b0000000000001000),
  BOTTOM_LEFT(          0b0000000000010000),
  TOP_LEFT(             0b0000000000100000),
  CENTER_LEFT(          0b0000000001000000),
  CENTER_RIGHT(         0b0000000010000000),
  TOP_LEFT_DIAGONAL(    0b0000000100000000),
  TOP_CENTER(           0b0000001000000000),
  TOP_RIGHT_DIAGONAL(   0b0000010000000000),
  BOTTOM_LEFT_DIAGONAL( 0b0000100000000000),
  BOTTOM_CENTER(        0b0001000000000000),
  BOTTOM_RIGHT_DIAGONAL(0b0010000000000000),
  DECIMAL(              0b0100000000000000);

  @Getter
  private final int mask;

  AlphaNumericLed(int mask){
    this.mask = mask;
  }

}