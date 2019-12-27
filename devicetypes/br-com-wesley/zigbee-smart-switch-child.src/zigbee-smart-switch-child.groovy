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
		capability "Configuration"
		capability "Health Check"
		capability "Light"
		capability "Refresh"
        capability "Switch"
	}

	preferences {
		input("trace", "bool", title: "Trace", description: "Set it to true to enable tracing")
		input("logFilter", "number", title: "Trace level", range: "1..5", description: "1= ERROR only, 2= <1+WARNING>, 3= <2+INFO>, 4= <3+DEBUG>, 5= <4+TRACE>")
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

    attribute "switch", "ENUM", ["on", "off"]
    command "on"
    command "off"

	tiles(scale: 2) {
		standardTile ("switch", "device.switch", width: 2, height: 2, canChangeIcon: true, decoration: "flat") {
			state ("off", label: '${name}', action: "on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn")
			state ("on", label: '${name}', action: "off", icon: "st.switches.light.on", backgroundColor: "#00a0dc", nextState: "turningOff")
			state ("turningOff", label: '${name}', action: "on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn")
			state ("turningOn", label: '${name}', action: "off", icon: "st.switches.light.on", backgroundColor: "#00a0dc", nextState: "turningOff")
		}

		main("switch")
	}
}

def on() {
//return zigbee.on()
    sendEvent(name: "switch", value: "on", displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
    parent.on(device)
}

def off() {
//return zigbee.off()
    sendEvent(name: "switch", value: "off", displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
    parent.off(device)
}

def parse (description) {
	log.debug "[PARSE-CHILD] description: $description, cluster: ${zigbee.parse(description)}, event: ${zigbee.getEvent(description)}, map: ${zigbee.parseDescriptionAsMap(description)}"
	if (description?.startsWith("on/off: ")) {
        return createEvent(name: "switch", value: device.currentValue("switch"))
//        return createEvent(name: "switch", value: state.turning[0]?:calculateValue(device.currentValue("childSwitch0")))
    }
    def map = zigbee.parseDescriptionAsMap(description)
    log.debug "DID NOT PARSE MESSAGE for description : $description"
    log.debug map
}

def setLevel(value, rate = null) {
	zigbee.setLevel(value)// + zigbee.onOffRefresh() + zigbee.levelRefresh()
    // @TODO Talvez precise descomentar o trecho da linha acima
	//adding refresh because of ZLL bulb not conforming to send-me-a-report
}

def refresh() {
	log.debug "refresh()"
	return parent.refresh(device)// + zigbee.onOffRefresh()
}

def poll() {
	log.debug "poll()"
	refresh()
}

def ping() {
	log.debug "ping()"
	refresh()
}

def healthPoll () {
    [refresh()].flatten().each { sendHubCommand(new physicalgraph.device.HubAction(it)) }
}

def configureHealthCheck () {
	if (!state.hasConfiguredHealthCheck) {
		log.debug "Configuring Health Check, Reporting"
		unschedule("healthPoll", [forceForLocallyExecuting: true])
		runEvery5Minutes("healthPoll", [forceForLocallyExecuting: true])
		// Device-Watch allows 2 check-in misses from device
		sendEvent(name: "checkInterval", value: 12 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
		state.hasConfiguredHealthCheck = true
	}
}

def installed () {
	log.debug "installed() - child $parent"

    //configureHealthCheck()
}

def updated () {
	log.debug "updated() - child $parent"
    
    configureHealthCheck()
}

def configure () {
	log.debug "configure() - child"

	configureHealthCheck()

	//https://docs.smartthings.com/en/latest/ref-docs/zigbee-ref.html#zigbee-configurereporting
	refresh() + zigbee.onOffConfig()// + zigbee.levelConfig()
	//(0..getNumberOfChildren()).collect { configureReporting(0x0006, 0x0000, 0x10, 0, 600, null, [destEndpoint: getInitialEndpoint() + it]) } + refresh()
}