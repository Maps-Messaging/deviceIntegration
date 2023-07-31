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

package io.mapsmessaging.devices.io;

import io.mapsmessaging.devices.deviceinterfaces.RegisterData;

import java.util.HashMap;
import java.util.Map;

public class RegisterDataWrapper {
  private Map<Integer, RegisterData> map;

  public RegisterDataWrapper() {
    this.map = new HashMap<>();
  }

  public RegisterDataWrapper(Map<Integer, RegisterData> map) {
    this.map = map;
  }

  public Map<Integer, RegisterData> getMap() {
    return map;
  }

  public void setMap(Map<Integer, RegisterData> map) {
    this.map = map;
  }
}