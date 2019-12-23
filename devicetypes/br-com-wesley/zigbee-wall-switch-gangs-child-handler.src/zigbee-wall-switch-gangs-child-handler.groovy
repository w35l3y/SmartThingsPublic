/**
 *  Zigbee Wall Switch Gangs Child Handler
 *
 *  Copyright 2019 w35l3y
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */

metadata {
    definition (name: "Zigbee Wall Switch Gangs Child Handler", namespace: "br.com.wesley", author: "w35l3y", ocfDeviceType: "oic.d.light", runLocally: true, minHubCoreVersion: '000.019.00012', executeCommandsLocally: true) {
        capability "Actuator"
        capability "Configuration"
        capability "Health Check"
        capability "Light"
        capability "Refresh"
    }
    tiles(scale: 2) {
    	standardTile ("switch", "device.switch", width: 2, height: 2, canChangeIcon: true, decoration: "flat") {
            state ("off", label: '${name}', action: "on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn")
            state ("on", label: '${name}', action: "off", icon: "st.switches.light.on", backgroundColor: "#00a0dc", nextState: "turningOff")
            state ("turningOff", label: '${name}', action: "on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn")
            state ("turningOn", label: '${name}', action: "off", icon: "st.switches.light.on", backgroundColor: "#00a0dc", nextState: "turningOff")
        }
    }
}

void on() {
	parent.childOn(device)
}

void off() {
	parent.childOff(device)
}