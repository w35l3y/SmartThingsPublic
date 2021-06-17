/*
 *  Copyright 2018 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy
 *  of the License at:
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 *  Author : Wanderson Niquini | niquini.com
 *  Date : 2021-04-24
 */

metadata {
	definition(name: "Régua Zigbee Tuya", namespace: "niquini", author: "Wanderson Niquini", ocfDeviceType: "oic.d.switch", mnmn: "SmartThings", vid: "generic-switch") {
		capability "Actuator"
		capability "Configuration"
		capability "Refresh"
		capability "Health Check"
		capability "Switch"

		command "childOn", ["string"]
		command "childOff", ["string"]

        // TUYA
		fingerprint profileId: "0104", inClusters: "0000 0003 0004 0005 0006 E000 E001", outClusters: "0019 000A", manufacturer: "_TZ3000_o005nuxx", model: "TS011F", deviceJoinName: "Régua Zigbee Tuya" //4 tomadas e 4 usb
        
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
		multiAttributeTile(name: "switch", type: "lighting", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.light.on", backgroundColor: "#00A0DC", nextState: "turningOff"
				attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn"
				attributeState "turningOn", label: '${name}', action: "switch.off", icon: "st.switches.light.on", backgroundColor: "#00A0DC", nextState: "turningOff"
				attributeState "turningOff", label: '${name}', action: "switch.on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn"
			}
		}
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label: "", action: "refresh.refresh", icon: "st.secondary.refresh"
		}
		main "switch"
		details(["switch", "refresh"])
	}
}

def installed() {
	createChildDevices()
	updateDataValue("onOff", "catchall")
	refresh()
}

def updated() {
	log.debug "updated()"
	updateDataValue("onOff", "catchall")
	for (child in childDevices) {
		if (!child.deviceNetworkId.startsWith(device.deviceNetworkId) || //parent DNI has changed after rejoin
				!child.deviceNetworkId.split(':')[-1].startsWith('0')) {
			child.setDeviceNetworkId("${device.deviceNetworkId}:0${getChildEndpoint(child.deviceNetworkId)}")
		}
	}
	refresh()
}

def parse(String description) {
	Map eventMap = zigbee.getEvent(description)
	Map eventDescMap = zigbee.parseDescriptionAsMap(description)

log.debug "eventMap $eventMap | eventDescMap $eventDescMap"
	if (eventMap) {
		if (eventDescMap && eventDescMap?.attrId == "0000") {//0x0000 : OnOff attributeId
			if (eventDescMap?.sourceEndpoint == "01" || eventDescMap?.endpoint == "01") {
				sendEvent(eventMap)
			} else {
				def childDevice = childDevices.find {
					it.deviceNetworkId == "$device.deviceNetworkId:${eventDescMap.sourceEndpoint}" || it.deviceNetworkId == "$device.deviceNetworkId:${eventDescMap.endpoint}"
				}
				if (childDevice) {
					childDevice.sendEvent(eventMap)
				} else {
					log.debug "Child device: $device.deviceNetworkId:${eventDescMap.sourceEndpoint} was not found"
				}
			}
		}
	}
}

private void createChildDevices() {
	if (!childDevices) {
		def x = getChildCount()
		for (i in 2..x) {
			addChildDevice("smartthings", "Child Switch Health", "${device.deviceNetworkId}:0${i}", device.hubId,
				[completedSetup: true, label: "${device.displayName[0..-2]}${i}", isComponent: false])
		}
	}
}

private getChildEndpoint(String dni) {
	dni.split(":")[-1] as Integer
}

def on() {
//  createChildDevices()
	log.debug("on")
	zigbee.on()
}

def off() {
	log.debug("off")
	zigbee.off()
}

def childOn(String dni) {
	log.debug(" child on ${dni}")
	def childEndpoint = getChildEndpoint(dni)
	zigbee.command(zigbee.ONOFF_CLUSTER, 0x01, "", [destEndpoint: childEndpoint])
}

def childOff(String dni) {
	log.debug(" child off ${dni}")
	def childEndpoint = getChildEndpoint(dni)
	zigbee.command(zigbee.ONOFF_CLUSTER, 0x00, "", [destEndpoint: childEndpoint])
}

/**
 * PING is used by Device-Watch in attempt to reach the Device
 * */
def ping() {
	return refresh()
}

def refresh() {
	if (isOrvibo()) {
		zigbee.readAttribute(zigbee.ONOFF_CLUSTER, 0x0000, [destEndpoint: 0xFF])
	} else {
		def cmds = zigbee.onOffRefresh()
		def x = getChildCount()
		for (i in 2..x) {
			cmds += zigbee.readAttribute(zigbee.ONOFF_CLUSTER, 0x0000, [destEndpoint: i])
		}
		return cmds
	}
}

def poll() {
	refresh()
}

def healthPoll() {
	log.debug "healthPoll()"
	def cmds = refresh()
	cmds.each { sendHubCommand(new physicalgraph.device.HubAction(it)) }
}

def configureHealthCheck() {
	Integer hcIntervalMinutes = 12
	if (!state.hasConfiguredHealthCheck) {
		log.debug "Configuring Health Check, Reporting"
		unschedule("healthPoll")
		runEvery5Minutes("healthPoll")
		def healthEvent = [name: "checkInterval", value: hcIntervalMinutes * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID]]
		// Device-Watch allows 2 check-in misses from device
		sendEvent(healthEvent)
		childDevices.each {
			it.sendEvent(healthEvent)
		}
		state.hasConfiguredHealthCheck = true
	}
}

def configure() {
	log.debug "configure()"
	configureHealthCheck()

	if (isOrvibo()) {
		//the orvibo switch will send out device anounce message at ervery 2 mins as heart beat,setting 0x0099 to 1 will disable it.
		def cmds = zigbee.writeAttribute(zigbee.BASIC_CLUSTER, 0x0099, 0x20, 0x01, [mfgCode: 0x0000])
		cmds += refresh()
		return cmds
	} else {
		//other devices supported by this DTH in the future
		def cmds = zigbee.onOffConfig(0, 120)
		def x = getChildCount()
		for (i in 2..x) {
			cmds += zigbee.configureReporting(zigbee.ONOFF_CLUSTER, 0x0000, 0x10, 0, 120, null, [destEndpoint: i])
		}
		cmds += refresh()
		return cmds
	}
}

private Boolean isOrvibo() {
	device.getDataValue("manufacturer") == "ORVIBO"
}

private getChildCount() {
	if (device.getDataValue("model") == "TS011F") {
		return 5
	} else {
		return 2
	}
}