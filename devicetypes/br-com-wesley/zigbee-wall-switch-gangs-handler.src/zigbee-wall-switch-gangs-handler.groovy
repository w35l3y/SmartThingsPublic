/**
 *  Zigbee Wall Switch Gangs Handler
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

def SWITCHES = 3

metadata {
//oic.d.light
//oic.d.bridge
//x.com.st.d.bridge
//x.com.st.powerswitch
    definition (name: "Zigbee Wall Switch Gangs Handler", namespace: "br.com.wesley", author: "w35l3y", ocfDeviceType: "oic.d.light", runLocally: true, minHubCoreVersion: '000.019.00012', executeCommandsLocally: true) {
        capability "Actuator"
        capability "Configuration"
        capability "Health Check"
        capability "Light"
        capability "Refresh"
 
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006"
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0006", outClusters: "0003, 0006, 0019, 0406", manufacturer: "Leviton", model: "ZSS-10", deviceJoinName: "Leviton Switch"
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0006", outClusters: "000A", manufacturer: "HAI", model: "65A21-1", deviceJoinName: "Leviton Wireless Load Control Module-30amp"
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006", outClusters: "0003, 0006, 0008, 0019, 0406", manufacturer: "Leviton", model: "DL15A", deviceJoinName: "Leviton Lumina RF Plug-In Appliance Module"
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006", outClusters: "0003, 0006, 0008, 0019, 0406", manufacturer: "Leviton", model: "DL15S", deviceJoinName: "Leviton Lumina RF Switch"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006", outClusters: "0003, 0006, 0008, 0019, 0406", manufacturer: "Feibit Inc co.", model: "FNB56-ZSW03LX2.0", deviceJoinName: "Nue ZigBee Switch"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006", outClusters: "0003, 0006, 0008, 0019, 0406", manufacturer: "Feibit Inc co.", model: "FB56+ZSW1GKJ1.7", deviceJoinName: "Nue ZigBee Switch"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006", outClusters: "0003, 0006, 0008, 0019, 0406", manufacturer: "Feibit Inc co.", model: "FB56+ZSW05HG1.2", deviceJoinName: "Nue ZigBee Light Controller"        

		attribute "lastCheckin", "string"
		//command "refresh"

		attribute "switch", "ENUM", ["on","off"]
        //command "on"
        //command "off"
        
		(0..(SWITCHES - 1)).each {
        	attribute "switch$it", "ENUM", ["on","off"]
            //command "on$it"
            //command "off$it"
        }
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
    	standardTile ("switch", "device.switch", width: 2, height: 2, canChangeIcon: true, decoration: "flat") {
            state ("off", label: '${name}', action: "on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn")
            state ("on", label: '${name}', action: "off", icon: "st.switches.light.on", backgroundColor: "#00a0dc", nextState: "turningOff")
            state ("turningOff", label: '${name}', action: "on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn")
            state ("turningOn", label: '${name}', action: "off", icon: "st.switches.light.on", backgroundColor: "#00a0dc", nextState: "turningOff")
        }

		(0..(SWITCHES - 1)).each {
        //https://docs.smartthings.com/en/latest/ref-docs/device-handler-ref.html#childdevicetile
        //childDeviceTile()
    		standardTile ("switch$it", "device.switch$it", width: 2, height: 2, canChangeIcon: true, decoration: "flat") {
                state ("off", label: '${name}', action: "on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn")
                state ("on", label: '${name}', action: "off", icon: "st.switches.light.on", backgroundColor: "#00a0dc", nextState: "turningOff")
                state ("turningOff", label: '${name}', action: "on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn")
                state ("turningOn", label: '${name}', action: "off", icon: "st.switches.light.on", backgroundColor: "#00a0dc", nextState: "turningOff")
			}            
        }

     	/*multiAttributeTile(name:"switch", type: "generic", width: 6, height: 4, canChangeIcon: true) {
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") { 
                attributeState "on", label: '${name}', action: "off", icon: "st.switches.light.on", backgroundColor: "#00a0dc", nextState: "turningOff"
                attributeState "off", label: '${name}', action: "on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn"
                attributeState "turningOn", label: '${name}', action: "off", icon: "st.switches.light.on", backgroundColor: "#00a0dc", nextState: "turningOff"
                attributeState "turningOff", label: '${name}', action: "on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn"
            }
        }*/

		/*(0..(SWITCHES - 1)).each {
            multiAttributeTile(name:"switch${it}", type: "lighting", width: 6, height: 4, canChangeIcon: true) {
                tileAttribute ("device.switch${it}", key: "PRIMARY_CONTROL") { 
                    attributeState "on", label: '${name}', action: "off${it}", icon: "st.switches.light.on", backgroundColor: "#00a0dc", nextState: "turningOff"
                    attributeState "off", label: '${name}', action: "on${it}", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn"
                    attributeState "turningOn", label: '${name}', action: "off${it}", icon: "st.switches.light.on", backgroundColor: "#00a0dc", nextState: "turningOff"
                    attributeState "turningOff", label: '${name}', action: "on${it}", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn"
                }
            }
        }*/

		main(["switch"])
        details((0..(SWITCHES - 1)).collect { "switch$it" })
    }
}

def createChildDevices () {
	log.debud "Created children devices"
	(0..(SWITCHES - 1)).each {
		//addChildDevice("Switch", "${device.deviceNetworkId}-ep${it}", null, [completedSetup: true, label: "${device.displayName} (CH${it})", isComponent: true, componentName: "ch$i", componentLabel: "Channel $it"])
    }
}

/*
def initialize() {
	state.numberOfSwitches = SWITCHES
}

def installed () {
	log.debug "Installed"

	createChildDevices()
    initialize()
}

def updated() {
	log.debug "Updated"

	unsubscribe()
    initialize()
}
*/

// Parse incoming device messages to generate events

/*
[raw:0000 8021 00 00 0000 00 7992 00 00 0000 00 00 2100, profileId:0000, clusterId:8021, sourceEndpoint:00, destinationEndpoint:00, options:0000, messageType:00, dni:7992, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:00, direction:00, data:[21, 00], clusterInt:32801, commandInt:0]

[
	raw:0000 8021 00 00 0000 00 7992 00 00 0000 00 00 2100,
    profileId:0000,	//	$1
    clusterId:8021,	//	$2
    sourceEndpoint:00, // $3
    destinationEndpoint:00,	//	$4
    options:0000,	//	$5
    messageType:00,	//	$6
    dni:7992,	//	$7
    isClusterSpecific:false,	//	$8
    isManufacturerSpecific:false,	//	$9
    manufacturerId:0000,	//	$10
    command:00,	//	$11
    direction:00,	//	$12
    data:[21, 00],	//	$13
    clusterInt:32801,
    commandInt:0
]

[raw:0104 0006 01 01 0000 00 7992 00 00 0000 0B 01 0000, profileId:0104, clusterId:0006, sourceEndpoint:01, destinationEndpoint:01, options:0000, messageType:00, dni:7992, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:0B, direction:01, data:[00, 00], clusterInt:6, commandInt:11]
[raw:0104 0006 02 01 0000 00 7992 00 00 0000 0B 01 0000, profileId:0104, clusterId:0006, sourceEndpoint:02, destinationEndpoint:01, options:0000, messageType:00, dni:7992, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:0B, direction:01, data:[00, 00], clusterInt:6, commandInt:11]
[raw:0104 0006 03 01 0000 00 7992 00 00 0000 0B 01 0000, profileId:0104, clusterId:0006, sourceEndpoint:03, destinationEndpoint:01, options:0000, messageType:00, dni:7992, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:0B, direction:01, data:[00, 00], clusterInt:6, commandInt:11]

TURN ON
[raw:0104 0006 03 01 0000 00 7992 00 00 0000 0B 01 0100, profileId:0104, clusterId:0006, sourceEndpoint:03, destinationEndpoint:01, options:0000, messageType:00, dni:7992, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:0B, direction:01, data:[01, 00], clusterInt:6, commandInt:11]
[raw:0104 0006 02 01 0000 00 7992 00 00 0000 0B 01 0100, profileId:0104, clusterId:0006, sourceEndpoint:02, destinationEndpoint:01, options:0000, messageType:00, dni:7992, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:0B, direction:01, data:[01, 00], clusterInt:6, commandInt:11]
[raw:0104 0006 01 01 0000 00 7992 00 00 0000 0B 01 0100, profileId:0104, clusterId:0006, sourceEndpoint:01, destinationEndpoint:01, options:0000, messageType:00, dni:7992, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:0B, direction:01, data:[01, 00], clusterInt:6, commandInt:11]

TURN OFF
[raw:0104 0006 03 01 0000 00 7992 00 00 0000 0B 01 0000, profileId:0104, clusterId:0006, sourceEndpoint:03, destinationEndpoint:01, options:0000, messageType:00, dni:7992, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:0B, direction:01, data:[00, 00], clusterInt:6, commandInt:11]
[raw:0104 0006 02 01 0000 00 7992 00 00 0000 0B 01 0000, profileId:0104, clusterId:0006, sourceEndpoint:02, destinationEndpoint:01, options:0000, messageType:00, dni:7992, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:0B, direction:01, data:[00, 00], clusterInt:6, commandInt:11]
[raw:0104 0006 01 01 0000 00 7992 00 00 0000 0B 01 0000, profileId:0104, clusterId:0006, sourceEndpoint:01, destinationEndpoint:01, options:0000, messageType:00, dni:7992, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:0B, direction:01, data:[00, 00], clusterInt:6, commandInt:11]

REFRESH
[raw:0104 0006 01 01 0000 00 7992 00 00 0000 01 01 0000001001, profileId:0104, clusterId:0006, sourceEndpoint:01, destinationEndpoint:01, options:0000, messageType:00, dni:7992, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:01, direction:01, attrId:0000, resultCode:00, encoding:10, value:01, isValidForDataType:true, data:[00, 00, 00, 10, 01], clusterInt:6, attrInt:0, commandInt:1]
[raw:0000 8021 00 00 0000 00 7992 00 00 0000 00 00 7500, profileId:0000, clusterId:8021, sourceEndpoint:00, destinationEndpoint:00, options:0000, messageType:00, dni:7992, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:00, direction:00, data:[75, 00], clusterInt:32801, commandInt:0]
[raw:0104 0006 01 01 0000 00 7992 00 00 0000 07 01 00, profileId:0104, clusterId:0006, sourceEndpoint:01, destinationEndpoint:01, options:0000, messageType:00, dni:7992, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:07, direction:01, data:[00], clusterInt:6, commandInt:7]

CONFIGURE
[raw:0104 0006 01 01 0000 00 7992 00 00 0000 01 01 0000001001, profileId:0104, clusterId:0006, sourceEndpoint:01, destinationEndpoint:01, options:0000, messageType:00, dni:7992, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:01, direction:01, attrId:0000, resultCode:00, encoding:10, value:01, isValidForDataType:true, data:[00, 00, 00, 10, 01], clusterInt:6, attrInt:0, commandInt:1]
[raw:0000 8021 00 00 0000 00 7992 00 00 0000 00 00 7700, profileId:0000, clusterId:8021, sourceEndpoint:00, destinationEndpoint:00, options:0000, messageType:00, dni:7992, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:00, direction:00, data:[77, 00], clusterInt:32801, commandInt:0]
[raw:0104 0006 01 01 0000 00 7992 00 00 0000 07 01 00, profileId:0104, clusterId:0006, sourceEndpoint:01, destinationEndpoint:01, options:0000, messageType:00, dni:7992, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:07, direction:01, data:[00], clusterInt:6, commandInt:7]
*/
def parse (description) {
	sendEvent(name: "lastCheckin", value: new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZone.getTimeZone("UTC")))

//    log.debug "Description: ${description}"
//    log.debug "Cluster: ${zigbee.parse(description)}"
//    log.debug "Event: ${zigbee.getEvent(description)}"
//    log.debug "Map: ${zigbee.parseDescriptionAsMap(description)}"

	if (description?.startsWith('on/off: ')) {
    	//	st rattr 0x7992 0x10 0x0006 0x0000, delay 2000
        //	st rattr 0x7992 0x11 0x0006 0x0000, delay 2000
        //	st rattr 0x7992 0x12 0x0006 0x0000, delay 2000
    	//log.debug zigbee.readAttribute(0x0006, 0x0000, [destEndpoint: 0x10]) + zigbee.readAttribute(0x0006, 0x0000, [destEndpoint: 0x11])
		//def refreshCmds = zigbee.readAttribute(0x0006, 0x0000, [destEndpoint: 0x01]) +
        //				zigbee.readAttribute(0x0006, 0x0000, [destEndpoint: 0x02]) +
        //              zigbee.readAttribute(0x0006, 0x0000, [destEndpoint: 0x03])
        //return refreshCmds.collect { new physicalgraph.device.HubAction(it) } 
        //return new physicalgraph.device.HubMultiAction(refreshCmds)
    } else {
    	def map = zigbee.parseDescriptionAsMap(description)
        if (map?.clusterInt == 6 && map.commandInt == 11) {
        	int source = map.sourceEndpoint as Integer
            //log.debug "SWITCH $source = ${map.data[0] == '01'?"ON":"OFF"}"
        	//sendEvent(name: "switch$source", value: map.data[0] == '01'?"on":"off")
            return
        }
    	log.warn "DID NOT PARSE MESSAGE for description : $description"
        log.debug map
        //return sendEvent(createEvent()
        //[raw:0104 0006 02 01 0000 00 7992 00 00 0000 0B 01 0100, profileId:0104, clusterId:0006, sourceEndpoint:02, destinationEndpoint:01, options:0000, messageType:00, dni:7992, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:0B, direction:01, data:[01, 00], clusterInt:6, commandInt:11]
    }
}

def refresh() {
	log.debug "Refresh"

	zigbee.readAttribute(0x0006, 0x0000, [destEndpoint: 0x01]) +
	zigbee.readAttribute(0x0006, 0x0000, [destEndpoint: 0x02]) +
	zigbee.readAttribute(0x0006, 0x0000, [destEndpoint: 0x03]) +
    zigbee.onOffRefresh() +
    zigbee.onOffConfig()
}

def ping() {
	log.debug "Ping"

	refresh()
}

def configure() {
	//sendEvent(name: "numberOfSwitches", value: 3, displayed: false)
    // Device-Watch allows 2 check-in misses from device + ping (plus 2 min lag time)
    sendEvent( name: "checkInterval", value: 2 * 10 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID] )

	log.debug "Configuring Reporting and Bindings."

	refresh()
}

def turnAll (mode) {
	log.debug "HANDLE ALL : $mode"

	sendEvent(name: "switch", value: mode)
    sendEvent(name: "switch0", value: mode)
    sendEvent(name: "switch1", value: mode)
    sendEvent(name: "switch2", value: mode)

	def status = mode == "on"?0x01:0x00

	zigbee.command(0x0006, status, "", [destEndpoint: 0x01]) +
    zigbee.command(0x0006, status, "", [destEndpoint: 0x02]) +
    zigbee.command(0x0006, status, "", [destEndpoint: 0x03])
}

def handleAll (params) {
	/*(0..2).each {
    	if (state["switch$it"] != params.value) {
    		sendEvent(name: "switch", value: params.value == "on"?"off":"on")
        	return
        }
    }
    sendEvent(name: "switch", value: params.value)*/
}

def on (params) {
	//log.debug "Handle ON ${params.index} - ${params.event} : ${params}"
    if (-1 < params.index) {
    	handleAll(index: params.index, value: "on")
    	sendEvent(name: "switch${params.index}", value: "on")

		return zigbee.command(0x0006, 0x01, "", [destEndpoint: 1 + params.index])
    }

    turnAll("on")
}

def off (params) {
	//log.debug "Handle OFF ${params.index} - ${params.event} : ${params}"
    if (-1 < params.index) {
    	handleAll(index: params.index, value: "off")
    	sendEvent(name: "switch${params.index}", value: "off")

		return zigbee.command(0x0006, 0x00, "", [destEndpoint: 1 + params.index])
    }

    turnAll("off")
}

def on() {
	on(index: -1)
}

def off() {
	off(index: -1)
}

/*
https://docs.smartthings.com/en/latest/device-type-developers-guide/zigbee-primer.html
https://docs.smartthings.com/en/latest/ref-docs/zigbee-ref.html

Set the level of a device
    zigbee.command(0x0008, 0x04, "FE0500")
    zigbee.command(IAS_WD_CLUSTER, COMMAND_IAS_WD_START_WARNING, "13", DataType.pack(warningDuration, DataType.UINT16), "00", "00")
    Possible solutions: command(java.lang.Object, java.lang.Object, java.lang.String),
    					command(java.lang.Object, java.lang.Object, [Ljava.lang.String;),
                        command(java.lang.Object, java.lang.Object, java.lang.String, java.util.Map)
                        
                        zigbee.command(Integer Cluster, Integer Command, [String... payload])
                        zigbee.command(Integer Cluster, Integer Command, String payload, additionalParams=[:])
Read the current level (e.g. of a light)
    zigbee.readAttribute(0x0008, 0x0000)
Write the value 0xBEEF to cluster 0x0008 attribute 0x0010
    zigbee.writeAttribute(0x0008, 0x0010, DataType.UINT16, 0xBEEF)
Report battery level every 10 minutes to 6 hours if it changes value by 1
    zigbee.configureReporting(0x0001, 0x0021, DataType.UINT8, 600, 21600, 0x01) 

The value is a dictionary that contains all the information gathered from the device. Here is what each part means:

        dni: Device Network ID
        d: the ZigBee EUID aka long address
        capabilities: the MAC capability field from the Device Announce message (not currently used by SmartThings)
        endpoints: a list of information for each available endpoint
        simple: a space separated string of hex values that contains the following pieces of information:
            Endpoint
            Profile ID
            Device ID
            Device version
            Number of in/server clusters
            List of In/server clusters
            Number of out/client clusters
            List of out/client clusters
        application: the Application Version read from attribute 0x0001 of the Basic Cluster
        manufacturer: The Manufacturer value read from attribute 0x0004 of the Basic Cluster
        model: The Model value read from attribute 0x0005 of the Basic Cluster

zigbee.writeAttribute(Integer Cluster, Integer attributeId, Integer dataType, value, Map additionalParams=[:])
zigbee.readAttribute(Integer Cluster, Integer attributeId, Map additionalParams=[:])
zigbee.configureReporting(Integer Cluster, Integer attributeId, Integer dataType, Integer minReportTime, Integer MaxReportTime, [Integer reportableChange], Map additionalParams=[:])
*/