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

package io.mapsmessaging.devices.i2c.devices.output.led.ht16k33;

import com.pi4j.io.i2c.I2C;
import io.mapsmessaging.devices.i2c.I2CDevice;
import io.mapsmessaging.devices.i2c.I2CDeviceController;
import io.mapsmessaging.devices.i2c.devices.output.led.ht16k33.tasks.Clock;
import io.mapsmessaging.devices.i2c.devices.output.led.ht16k33.tasks.Task;
import io.mapsmessaging.devices.i2c.devices.output.led.ht16k33.tasks.TestTask;
import io.mapsmessaging.schemas.config.SchemaConfig;
import io.mapsmessaging.schemas.config.impl.JsonSchemaConfig;
import org.everit.json.schema.*;
import org.json.JSONObject;

import java.io.IOException;

public abstract class HT16K33Controller extends I2CDeviceController {

  protected final HT16K33Driver display;

  private Task currentTask;

  protected HT16K33Controller() {
    this.display = null;
    currentTask = null;
  }

  protected HT16K33Controller(HT16K33Driver display, I2C device) {
    super(device);
    this.display = display;
  }

  public I2CDevice getDevice(){
    return display;
  }

  @Override
  public void close() {
    cancelCurrentTask();
  }

  @Override
  public boolean detect(I2C i2cDevice) {
    return display != null && display.isConnected();
  }

  public byte[] getStaticPayload() {
    return "{}".getBytes();
  }

  public byte[] getUpdatePayload() {
    JSONObject jsonObject = new JSONObject();
    if (display != null) {
      jsonObject.put("display", display.getCurrent());
      jsonObject.put("blink", display.getRate().name());
      jsonObject.put("enabled", display.isOn());
      jsonObject.put("brightness", display.getBrightness());
    }
    return jsonObject.toString(2).getBytes();
  }

  public void rawWrite(String value) throws IOException {
    if (display != null) {
      display.writeRaw(value);
    }
  }

  public void write(String value) throws IOException {
    if (display != null) {
      display.write(value);
    }
  }

  @Override
  public byte[] setPayload(byte[] val) throws IOException {
    JSONObject jsonObject = new JSONObject(new String(val));
    JSONObject response = new JSONObject();

    if (display == null) return response.toString(2).getBytes();
    if (jsonObject.has("brightness")) {
      int brightness = jsonObject.getInt("brightness");
      if (brightness != display.getBrightness()) {
        display.setBrightness((byte) (brightness & 0xf));
        response.put("brightness", brightness);
      }
    }
    if (jsonObject.has("blink")) {
      String blink = jsonObject.optString("blink", "OFF");
      BlinkRate rate = BlinkRate.valueOf(blink);
      display.setBlinkRate(rate);
      response.put("blink", rate.name());

    }
    if (jsonObject.has("enabled")) {
      boolean isOn = jsonObject.optBoolean("enabled", display.isOn());
      if (isOn != display.isOn()) {
        if (isOn) {
          response.put("enabled", isOn);
          display.turnOn();
        } else {
          response.put("enabled", isOn);
          display.turnOff();
        }
      }
    }
    if (jsonObject.has("display")) {
      cancelCurrentTask();
      String text = jsonObject.getString("display");
      if (text.length() <= 5) {
        display.write(text);
        response.put("display", text);
      }
    } else if (jsonObject.has("raw")) {
      cancelCurrentTask();
      String text = jsonObject.getString("raw");
      display.writeRaw(text);
      response.put("raw", text);
    } else if (jsonObject.has("task")) {
      cancelCurrentTask();
      display.write("    ");
      String task = jsonObject.getString("task");
      if (task.equalsIgnoreCase("clock")) {
        setTask(new Clock(this));
        response.put("task", "clock");
      }
      if (task.equalsIgnoreCase("test")) {
        setTask(new TestTask(this));
        response.put("task", "test");
      }
    }
    return response.toString(2).getBytes();
  }

  public SchemaConfig getSchema() {
    JsonSchemaConfig config = new JsonSchemaConfig(buildSchema());
    config.setSource("I2C bus address configurable from 0x70 to 0x77");
    config.setVersion("1.0");
    config.setResourceType("LED");
    config.setInterfaceDescription("Controls the LED segments");
    return config;
  }

  protected abstract String buildSchema();

  protected Schema buildWritablePayload(String pattern) {
    ObjectSchema.Builder updateSchema = ObjectSchema.builder()
        .addPropertySchema("display",
            StringSchema.builder()
                .pattern(pattern)
                .description("Update the LED display with the supplied string")
                .build()
        )
        .addPropertySchema("task", EnumSchema.builder()
            .possibleValue("clock")
            .possibleValue("test")
            .description("This is an optional server side task")
            .build())
        .addPropertySchema("blink",
            BooleanSchema.builder()
                .description("If the LED is blinking or not")
                .build()
        )
        .addPropertySchema("blink-fast",
            BooleanSchema.builder()
                .description("If the fast blink cycle is enabled")
                .build())
        .addPropertySchema("enabled",
            BooleanSchema.builder()
                .description("If the LED is actually on")
                .build())
        .addPropertySchema("brightness",
            NumberSchema.builder()
                .maximum(16)
                .minimum(0)
                .description("Brightness of the LED")
                .build());

    return updateSchema.build();
  }

  protected Schema buildUpdateSchema() {
    ObjectSchema.Builder updateSchema = ObjectSchema.builder()
        .addPropertySchema("display",
            StringSchema.builder()
                .description("What is currently being displayed on the LED")
                .build()
        )
        .addPropertySchema("blink",
            BooleanSchema.builder()
                .description("If the LED is blinking or not")
                .build()
        )
        .addPropertySchema("blink-fast",
            BooleanSchema.builder()
                .description("If the fast blink cycle is enabled")
                .build())
        .addPropertySchema("enabled",
            BooleanSchema.builder()
                .description("If the LED is actually on")
                .build())
        .addPropertySchema("brightness",
            NumberSchema.builder()
                .maximum(16)
                .minimum(0)
                .description("Brightness of the LED")
                .build());

    return updateSchema.build();
  }

  private synchronized void setTask(Task task) {
    cancelCurrentTask();
    currentTask = task;
  }

  private synchronized void cancelCurrentTask() {
    if (currentTask != null) {
      currentTask.stop();
      currentTask = null;
    }
  }
}
