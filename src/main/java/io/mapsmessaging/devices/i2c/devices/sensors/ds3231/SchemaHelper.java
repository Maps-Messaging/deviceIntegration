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

package io.mapsmessaging.devices.i2c.devices.sensors.ds3231;

import org.everit.json.schema.*;
import org.json.JSONObject;

public class SchemaHelper {
  public static ObjectSchema generateUpdatePayloadSchema() {
    ObjectSchema.Builder schemaBuilder = ObjectSchema.builder()
        .addPropertySchema("date", StringSchema.builder()
            .formatValidator(FormatValidator.forFormat("Date"))
            .build())
        .addPropertySchema("time", StringSchema.builder().build())
        .addPropertySchema("alarm1",
            ObjectSchema.builder()
                .addPropertySchema("rate", EnumSchema.builder()
                    .possibleValue("HOURS_MINUTES_MATCH")
                    .possibleValue("HOURS_MINUTE_SECONDS_MATCH")
                    .build())
                .addPropertySchema("time", StringSchema.builder().build())
                .build())
        .addPropertySchema("alarm2",
            ObjectSchema.builder()
                .addPropertySchema("rate", EnumSchema.builder()
                    .possibleValue("HOURS_MINUTES_MATCH")
                    .possibleValue("HOURS_MINUTE_SECONDS_MATCH")
                    .build())
                .addPropertySchema("time", StringSchema.builder().build())
                .build())
        .addPropertySchema("temperature", NumberSchema.builder().build())
        .addPropertySchema("control",
            ObjectSchema.builder()
                .addPropertySchema("covertTemperatureEnabled", BooleanSchema.builder().build())
                .addPropertySchema("oscillatorEnabled", BooleanSchema.builder().build())
                .addPropertySchema("squareWaveEnabled", BooleanSchema.builder().build())
                .addPropertySchema("squareWaveInterruptEnabled", BooleanSchema.builder().build())
                .addPropertySchema("squareWaveFrequency", NumberSchema.builder().build())
                .addPropertySchema("alarm1InterruptEnabled", BooleanSchema.builder().build())
                .addPropertySchema("alarm2InterruptEnabled", BooleanSchema.builder().build())
                .build())
        .addPropertySchema("status",
            ObjectSchema.builder()
                .addPropertySchema("alarm2Set", BooleanSchema.builder().build())
                .addPropertySchema("32khz", BooleanSchema.builder().build())
                .addPropertySchema("alarm1Set", BooleanSchema.builder().build())
                .addPropertySchema("oscillatorStopped", BooleanSchema.builder().build())
                .build());

    return schemaBuilder.build();
  }

  public static ObjectSchema generateControlSchema() {
    ObjectSchema.Builder schemaBuilder = ObjectSchema.builder()
        .addPropertySchema("covertTemperatureEnabled", BooleanSchema.builder().build())
        .addPropertySchema("oscillatorEnabled", BooleanSchema.builder().build())
        .addPropertySchema("squareWaveEnabled", BooleanSchema.builder().build())
        .addPropertySchema("squareWaveInterruptEnabled", BooleanSchema.builder().build())
        .addPropertySchema("squareWaveFrequency", NumberSchema.builder().build())
        .addPropertySchema("alarm1InterruptEnabled", BooleanSchema.builder().build())
        .addPropertySchema("alarm2InterruptEnabled", BooleanSchema.builder().build());

    return schemaBuilder.build();
  }

  public static ObjectSchema generateStatusSchema() {
    ObjectSchema.Builder schemaBuilder = ObjectSchema.builder()
        .addPropertySchema("alarm2Set", BooleanSchema.builder().build())
        .addPropertySchema("32khz", BooleanSchema.builder().build())
        .addPropertySchema("alarm1Set", BooleanSchema.builder().build())
        .addPropertySchema("oscillatorStopped", BooleanSchema.builder().build());

    return schemaBuilder.build();
  }
}
