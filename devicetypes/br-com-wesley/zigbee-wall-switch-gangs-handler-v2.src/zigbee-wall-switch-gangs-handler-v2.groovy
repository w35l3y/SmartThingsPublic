/**
 *  Zigbee Wall Switch Gangs Handler V2
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
//oic.d.light
//oic.d.bridge
//x.com.st.d.bridge
//x.com.st.powerswitch
    definition (name: "Zigbee Wall Switch Gangs Handler V2", namespace: "br.com.wesley", author: "w35l3y", ocfDeviceType: "oic.d.light", runLocally: true, executeCommandsLocally: true) {
        //capability "Actuator"
        //capability "Configuration"
        //capability "Health Check"
        capability "Light"
        //capability "Refresh"
 
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006"
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0006", outClusters: "0003, 0006, 0019, 0406", manufacturer: "Leviton", model: "ZSS-10", deviceJoinName: "Leviton Switch"
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0006", outClusters: "000A", manufacturer: "HAI", model: "65A21-1", deviceJoinName: "Leviton Wireless Load Control Module-30amp"
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006", outClusters: "0003, 0006, 0008, 0019, 0406", manufacturer: "Leviton", model: "DL15A", deviceJoinName: "Leviton Lumina RF Plug-In Appliance Module"
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006", outClusters: "0003, 0006, 0008, 0019, 0406", manufacturer: "Leviton", model: "DL15S", deviceJoinName: "Leviton Lumina RF Switch"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006", outClusters: "0003, 0006, 0008, 0019, 0406", manufacturer: "Feibit Inc co.", model: "FNB56-ZSW03LX2.0", deviceJoinName: "Nue ZigBee Switch"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006", outClusters: "0003, 0006, 0008, 0019, 0406", manufacturer: "Feibit Inc co.", model: "FB56+ZSW1GKJ1.7", deviceJoinName: "Nue ZigBee Switch"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006", outClusters: "0003, 0006, 0008, 0019, 0406", manufacturer: "Feibit Inc co.", model: "FB56+ZSW05HG1.2", deviceJoinName: "Nue ZigBee Light Controller"        
    }
    
    // simulator metadata
    simulator {
        // status messages
        status "on": "on/off: 1"
        status "off": "on/off: 0"

		status "switch0 on": "on/off: 1"
		status "switch0 off": "on/off: 0"
        status "switch1 on": "on/off: 1"
		status "switch1 off": "on/off: 0"
        status "switch2 on": "on/off: 1"
		status "switch2 off": "on/off: 0"

		// reply messages
        reply "zcl on-off on": "on/off: 1"
        reply "zcl on-off off": "on/off: 0"
    }

    tiles(scale: 2) {
		(0..2).each {
        //https://docs.smartthings.com/en/latest/ref-docs/device-handler-ref.html#childdevicetile
        //Zigbee Wall Switch Gangs Child Handler
        	childDeviceTile("childSwitch$it", "childSwitch$it", width: 2, height: 2, childTileName: "zigbeeWallSwitchGangsChildHandler")
        }

		main(["childSwitch0"])
        details((0..2).collect { "childSwitch$it" })
    }
}

def createChildDevices () {
	log.debug "Created children devices"
	(0..2).each {
		addChildDevice("Zigbee Wall Switch Gangs Child Handler", "${device.deviceNetworkId}${it}", null, [
            isComponent: true,
            componentName: "childSwitch$it",
            completedSetup: true,
            componentLabel: "Channel $it",
            label: "${device.displayName} ${1+it}",
        ])
    }
}

void childOn(String dni) {
	onOffCmd(0xFF, channelNumber(dni))
}
void childOff(String dni) {
	onOffCmd(0, channelNumber(dni))
}

def installed () {
	log.debug "Installed"

	createChildDevices()
}

def updated() {
	log.debug "Updated"
}

def parse (description) {
}

def refresh() {
	log.debug "Refresh"

    zigbee.onOffRefresh() +
    zigbee.onOffConfig()
}

def ping() {
	log.debug "Ping"

	refresh()
}

def configure() {
    sendEvent( name: "checkInterval", value: 2 * 10 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID] )

	log.debug "Configuring Reporting and Bindings."

	refresh()
}