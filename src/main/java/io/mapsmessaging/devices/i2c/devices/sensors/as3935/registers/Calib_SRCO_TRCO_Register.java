/*
 *      Copyright [ 2020 - 2023 ] [Matthew Buckton]
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package io.mapsmessaging.devices.i2c.devices.sensors.as3935.registers;

import io.mapsmessaging.devices.i2c.I2CDevice;
import io.mapsmessaging.devices.i2c.devices.Register;

import java.io.IOException;

public class Calib_SRCO_TRCO_Register extends Register {

  private static final int CALIB_SRCO_TRCO_CALIB_TRCO_BIT = 6;
  private static final int CALIB_SRCO_TRCO_CALIB_TRCO_DONE_BIT = 7;

  public Calib_SRCO_TRCO_Register(I2CDevice sensor) {
    super(sensor, 0x3A);
  }

  public boolean isTRCOCalibrationSuccessful() throws IOException {
    reload();
    return (registerValue & (1 << CALIB_SRCO_TRCO_CALIB_TRCO_DONE_BIT)) != 0;
  }

}