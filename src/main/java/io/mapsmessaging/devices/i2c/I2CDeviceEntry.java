package io.mapsmessaging.devices.i2c;

import com.pi4j.io.i2c.I2C;
import io.mapsmessaging.schemas.config.SchemaConfig;
import java.io.IOException;

public interface I2CDeviceEntry {

  I2CDeviceEntry mount (I2C device) throws IOException;

  SchemaConfig getSchema();

  int[] getAddressRange();

  byte[] getStaticPayload();

  byte[] getUpdatePayload();

  default void setPayload(byte[] val){}

  boolean detect();
}
