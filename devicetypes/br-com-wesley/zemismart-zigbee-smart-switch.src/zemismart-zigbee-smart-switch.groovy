/**
 *  Zemismart ZigBee Smart Switch
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
	definition (name: "Zemismart ZigBee Smart Switch", namespace: "br.com.wesley", author: "w35l3y", ocfDeviceType: "oic.d.light", runLocally: false, executeCommandsLocally: false, mnmn:"SmartThings", vid: "generic-switch", genericHandler: "ZLL") {
		capability "Actuator"
		capability "Configuration"
		capability "Health Check"
		capability "Light"
		capability "Polling"
		capability "Refresh"
		capability "Switch"

		//	Zemismart HGZB-41
		fingerprint profileId: "C05E", deviceId: "0000", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 1000", outClusters: "0019", manufacturer: "FeiBit", model: "FNB56-ZSW01LX2.0", deviceJoinName: "ZigBee Smart Switch"

		//	Zemismart HGZB-42
		fingerprint profileId: "C05E", deviceId: "0000", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 1000", outClusters: "0019", manufacturer: "3A Smart Home DE", model: "LXN-2S27LX1.0", deviceJoinName: "ZigBee Smart Switch"
		fingerprint profileId: "C05E", deviceId: "0000", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 1000", outClusters: "0019", manufacturer: "FeiBit", model: "FNB56-ZSW02LX2.0", deviceJoinName: "ZigBee Smart Switch"

		//	Zemismart HGZB-43
		fingerprint profileId: "C05E", deviceId: "0000", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 1000", outClusters: "0019", manufacturer: "FeiBit", model: "FNB56-ZSW03LX2.0", deviceJoinName: "ZigBee Smart Switch"
	}

	// simulator metadata
	simulator {
		// status messages
		status "on": "on/off: 1"
		status "off": "on/off: 0"

		// reply messages
		reply "zcl on-off on": "on/off: 1"
		reply "zcl on-off off": "on/off: 0"
	}

	tiles(scale: 2) {
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true, decoration: "flat") {
			state ("off", label: '${name}', action: "on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn")
			state ("on", label: '${name}', action: "off", icon: "st.switches.light.on", backgroundColor: "#00a0dc", nextState: "turningOff")
			state ("turningOff", label: '${name}', action: "on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn")
			state ("turningOn", label: '${name}', action: "off", icon: "st.switches.light.on", backgroundColor: "#00a0dc", nextState: "turningOff")
		}

		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label: "", action: "refresh.refresh", icon: "st.secondary.refresh"
		}

		main("switch")
        details(["switch", "refresh"])
	}
}

def getChildCount() {
	switch (device.getDataValue("model")) {
		case "FNB56-ZSW03LX2.0":
			return 3
		case "FNB56-ZSW02LX2.0":
        case "LXN-2S27LX1.0":
        	return 2
		case "FNB56-ZSW01LX2.0":
        	return 1
		default:
        log.debug "Model not found. Consider adding new fingerprint for your device."
			return 2
	}
}

def getInitialEndpoint () { Integer.parseInt(zigbee.endpointId, 10) }
def getHexEndpoint (index) { Integer.toHexString(getInitialEndpoint()+index).padLeft(2, "0").toUpperCase() }
def getEndpoint (child) { child.deviceNetworkId == device.deviceNetworkId?getInitialEndpoint():Integer.parseInt(child.deviceNetworkId.split(":")[-1], 16) }

def turn (Integer endpoint, value) {
	log.info "turn($endpoint, $value)"
	zigbee.command(zigbee.ONOFF_CLUSTER, value, "", [destEndpoint: endpoint])
}

def off (physicalgraph.device.cache.DeviceDTO child) { turn(getEndpoint(child), 0x00) }

def on (physicalgraph.device.cache.DeviceDTO child) { turn(getEndpoint(child), 0x01) }

def off() { off(device) }

def on() { on(device) }

def parse (description) {
	Map eventMap = zigbee.getEvent(description)
	Map eventDescMap = zigbee.parseDescriptionAsMap(description)

	log.debug "parse($eventMap, $eventDescMap)"
	if (!eventMap && eventDescMap?.clusterId == zigbee.ONOFF_CLUSTER) {
		eventMap = [name: "switch", value: eventDescMap.value]
	}

	if (eventMap) {
    	def endpoint = getHexEndpoint(0)
		if (!eventDescMap || eventDescMap.sourceEndpoint == endpoint || eventDescMap.endpoint == endpoint) {
        	log.debug "createEvent($eventMap) : $eventDescMap"
			return createEvent(eventMap)
		} else {
			def childDevice = childDevices.find {
				it.deviceNetworkId == "${device.deviceNetworkId}:${eventDescMap.sourceEndpoint}" || it.deviceNetworkId == "${device.deviceNetworkId}:${eventDescMap.endpoint}"
			}
			if (childDevice) {
            	log.debug "parse($childDevice)"
                return childDevice.createEvent(childDevice.createAndSendEvent(eventMap))
			} else {
				log.debug "Child device: ${device.deviceNetworkId}:${eventDescMap.sourceEndpoint} was not found"
			}
		}
    }
}

def setLevel(value, rate = null) { zigbee.setLevel(value) }

def refresh (Integer endpoint) { zigbee.readAttribute(zigbee.ONOFF_CLUSTER, 0x0000, [destEndpoint: endpoint]) }

def refresh (physicalgraph.device.cache.DeviceDTO child) { refresh(getEndpoint(child)) }

def refresh (physicalgraph.app.ChildDeviceWrapper child) { refresh(getEndpoint(child)) }

def refresh() {
	log.debug "refresh()"
	([refresh(device)] + childDevices.collect { refresh(it) }).flatten()
}

def poll (physicalgraph.device.cache.DeviceDTO child) {
	log.debug "poll($child)"
	refresh(child)
}

def poll () {
	log.debug "poll()"
	refresh()
}

def ping (physicalgraph.device.cache.DeviceDTO child) {
	log.debug "ping($child)"
    refresh(child)
}

def ping () {
	log.debug "ping()"
	refresh()
}

def createChildDevices () {
	if (!state.hasInstalledChildren) {
		(2..getChildCount()).each {
			addChildDevice("Zemismart ZigBee Smart Switch Child", "${device.deviceNetworkId}:${getHexEndpoint(it-1)}", device.hubId, [
				isComponent: false,
				componentName: "switch$it",
				completedSetup: true,
				componentLabel: "#$it",
				label: "${device.displayName} $it",
			])
		}
		state.hasInstalledChildren = true
	}
}

def healthPoll () {
	log.debug "healthPoll()"
	refresh().each { sendHubCommand(new physicalgraph.device.HubAction(it)) }
}

def configureHealthCheck () {
	if (!state.hasConfiguredHealthCheck) {
		log.debug "Configuring Health Check, Reporting"
		unschedule("healthPoll")
		runEvery5Minutes("healthPoll")
		// Device-Watch allows 2 check-in misses from device
		def healthEvent = [name: "checkInterval", value: 12 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID]]
		sendEvent(healthEvent)
		childDevices.each {
			it.sendEvent(healthEvent)
		}
		state.hasConfiguredHealthCheck = true
	}
}

def installed () {
	log.debug "installed()"

	createChildDevices()
}

def updated () {
	log.debug "updated()"
	
	createChildDevices()
	configureHealthCheck()
}

def configure () {
	log.debug "configure()"

	configureHealthCheck()

	(0..childDevices.size()).collect {
    	zigbee.configureReporting(zigbee.ONOFF_CLUSTER, 0x0000, 0x10, 0, 120, null, [destEndpoint: getInitialEndpoint() + it])
    } + refresh()
}