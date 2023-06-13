package io.mapsmessaging.server.i2c.devices.drivers.servos;

import io.mapsmessaging.server.i2c.Delay;
import io.mapsmessaging.server.i2c.devices.drivers.PCA9685;
import java.io.IOException;

public abstract class PWM_Device {

  protected final PCA9685 myPWMController;
  protected final short myServoPort;
  protected final AngleResponse myResponse;


  protected PWM_Device(PCA9685 pwm, short servoId, AngleResponse response) throws IOException {
    myPWMController = pwm;
    myServoPort = servoId;
    myResponse = response;
    setPosition(myResponse.getMin());
    Delay.pause(500); // allow the servo to get to min
    setPosition(myResponse.getMax());
    Delay.pause(500); // allow the servo to get to max
    setPosition(myResponse.getIdle()); // Set to idle position
  }

  public int getPort() {
    return myServoPort;
  }

  abstract void setPosition(float angle) throws IOException;

  public void close() throws IOException {
    setPosition(myResponse.getIdle());
  }
}