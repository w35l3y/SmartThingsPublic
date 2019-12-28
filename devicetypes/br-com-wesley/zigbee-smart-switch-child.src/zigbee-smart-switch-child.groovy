/**
 *  ZigBee Smart Switch Child
 *
 *  Copyright 2019 w35l3y
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */

metadata {
	definition (name: "ZigBee Smart Switch Child", namespace: "br.com.wesley", author: "w35l3y", ocfDeviceType: "oic.d.light", runLocally: false, executeCommandsLocally: false, mnmn:"SmartThings", vid: "generic-switch") {
		capability "Actuator"
		capability "Health Check"
		capability "Light"
		capability "Polling"
        capability "Refresh"
        capability "Switch"
	}
    
    command "off"
    command "on"

	tiles(scale: 2) {
		standardTile ("switch", "device.switch", width: 2, height: 2, canChangeIcon: true, decoration: "flat") {
			state ("off", label: '${name}', action: "on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn")
			state ("on", label: '${name}', action: "off", icon: "st.switches.light.on", backgroundColor: "#00a0dc", nextState: "turningOff")
			state ("turningOff", label: '${name}', action: "on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn")
			state ("turningOn", label: '${name}', action: "off", icon: "st.switches.light.on", backgroundColor: "#00a0dc", nextState: "turningOff")
		}

		main("switch")
        details(["switch"])
	}
}

def createAndSendEvent(map) {
    //if (map?.name == "switch") { map.name = "switchChild" }
	log.debug "sendEvent($map)"
	sendEvent(map)
    return map
}

def on() {
    parent.on(device)
}

def off() {
    parent.off(device)
}

def parse (description) {
	log.debug "[PARSE-CHILD] description: $description, cluster: ${zigbee.parse(description)}, event: ${zigbee.getEvent(description)}, map: ${zigbee.parseDescriptionAsMap(description)}"
	if (description?.startsWith("on/off: ")) {
        //return createEvent(name: "switch", value: device.currentValue("switch"))
//        return createEvent(name: "switch", value: state.turning[0]?:calculateValue(device.currentValue("childSwitch0")))
    }
    def map = zigbee.parseDescriptionAsMap(description)
    log.debug "DID NOT PARSE MESSAGE for description : $description"
    log.debug map
}

def poll() {
	log.debug "poll()"
    parent.poll(device)
}

def ping() {
	log.debug "ping()"
    parent.ping(device)
}

def refresh() {
	log.debug "refresh()"
	parent.refresh(device)
}

def installed () {
	log.debug "installed() - parent $parent"

    sendEvent(name: "checkInterval", value: 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
}

def uninstalled () {
	log.debug "uninstalled()"
	//parent.delete()
}