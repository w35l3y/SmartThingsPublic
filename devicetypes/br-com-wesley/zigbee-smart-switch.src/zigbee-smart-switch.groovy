/**
 *  ZigBee Smart Switch
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

//https://graph-na04-useast2.api.smartthings.com/ide/device/editor/5cc29662-d7b8-49d8-9cb5-9f41a61c5978

//minHubCoreVersion: '000.019.00012'
//minHubCoreVersion: '000.021.00001'
//minHubCoreVersion: '000.028.00012'
/*
http://www.cel.com/pdf/misc/zic04_zcl_api.pdf
*/
metadata {
//oic.d.light
//oic.d.bridge
//x.com.st.d.bridge
//x.com.st.powerswitch
	definition (name: "ZigBee Smart Switch", namespace: "br.com.wesley", author: "w35l3y", ocfDeviceType: "oic.d.light", runLocally: true, executeCommandsLocally: true/*, mnmn:"SmartThings", vid: "generic-switch", genericHandler: "ZLL"*/) {
		capability "Actuator"
		capability "Configuration"
		capability "Health Check"
		capability "Light"
		capability "Polling"
		capability "Refresh"
        capability "Switch"

 //01 C05E 0000 01 07 0000 0004 0003 0006 0005 1000 0008 01 0019
 //0B C05E 0000 01 07 0000 0004 0003 0006 0005 1000 0008 01 0019
 //0B C05E 0000 01 07 0000 0004 0003 0006 0005 1000 0008 01 0019
 /*
 01|0B	Endpoint
 C05E	ZLL: ZigBee Light Link
 0000	DeviceID
 01		???
 0000	Basic
 0003	Identify
 0004	Groups
 0005	Scenes
 0006	On/Off
 0008	Level Control
 1000	Touchlink Commissioning
 
 0019	OTA Upgrade
 */
 
		//	Zemismart HGZB-41
        fingerprint profileId: "C05E", deviceId: "0000", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 1000", outClusters: "0019", manufacturer: "FeiBit", model: "FNB56-ZSW01LX2.0", deviceJoinName: "ZigBee Smart Switch"

		//	Zemismart HGZB-42
        fingerprint profileId: "C05E", deviceId: "0000", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 1000", outClusters: "0019", manufacturer: "3A Smart Home DE", model: "LXN-2S27LX1.0", deviceJoinName: "ZigBee Smart Switch"

		//	Zemismart HGZB-43
        fingerprint profileId: "C05E", deviceId: "0000", inClusters: "0000, 0003, 0004, 0005, 0006, 0008, 1000", outClusters: "0019", manufacturer: "FeiBit", model: "FNB56-ZSW03LX2.0", deviceJoinName: "ZigBee Smart Switch"
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

	(1..getNumberOfChildren()).each {
    	attribute "switch$it", "enum", ["on", "off"]
    }

	tiles(scale: 2) {
    	standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true, decoration: "flat") {
			state ("off", label: '${name}', action: "on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn")
			state ("on", label: '${name}', action: "off", icon: "st.switches.light.on", backgroundColor: "#00a0dc", nextState: "turningOff")
			state ("turningOff", label: '${name}', action: "on", icon: "st.switches.light.off", backgroundColor: "#ffffff", nextState: "turningOn")
			state ("turningOn", label: '${name}', action: "off", icon: "st.switches.light.on", backgroundColor: "#00a0dc", nextState: "turningOff")
        }

		//(1..getNumberOfChildren()).each {
		//	//	https://docs.smartthings.com/en/latest/ref-docs/device-handler-ref.html#childdevicetile
		//	childDeviceTile("switch$it", "switch$it", width: 2, height: 2, childTileName: "zigBeeSmartSwitchChild")
		//}

		//standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
		//	state "default", label: "", action: "refresh.refresh", icon: "st.secondary.refresh"
		//}

		main("switch")
		//details("childSwitch0", "childSwitch1", "childSwitch2", "refresh")
		//details(["switch"] + (1..getNumberOfChildren()).collect { "switch$it" } + ["refresh"])
	}
}

def getNumberOfChildren() { 3 - 1 }
def getInitialEndpoint() { Integer.parseInt(zigbee.endpointId?:0x01, 10) }

//[PARSE] description: catchall: 0104 0006 01 01 0000 00 632F 00 00 0000 0B 01 0000, cluster: SmartShield(text: null, manufacturerId: 0x0000, direction: 0x01, data: [0x00, 0x00], number: null, isManufacturerSpecific: false, messageType: 0x00, senderShortId: 0x632f, isClusterSpecific: false, sourceEndpoint: 0x01, profileId: 0x0104, command: 0x0b, clusterId: 0x0006, destinationEndpoint: 0x01, options: 0x0000), event: [name:switch, value:off], map: [raw:0104 0006 01 01 0000 00 632F 00 00 0000 0B 01 0000, profileId:0104, clusterId:0006, sourceEndpoint:01, destinationEndpoint:01, options:0000, messageType:00, dni:632F, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:0B, direction:01, data:[00, 00], clusterInt:6, commandInt:11]
//[PARSE] description: catchall: 0104 0006 03 01 0000 00 632F 00 00 0000 04 01 880000, cluster: SmartShield(text: null, manufacturerId: 0x0000, direction: 0x01, data: [0x88, 0x00, 0x00], number: null, isManufacturerSpecific: false, messageType: 0x00, senderShortId: 0x632f, isClusterSpecific: false, sourceEndpoint: 0x03, profileId: 0x0104, command: 0x04, clusterId: 0x0006, destinationEndpoint: 0x01, options: 0x0000), event: [:], map: [raw:0104 0006 03 01 0000 00 632F 00 00 0000 04 01 880000, profileId:0104, clusterId:0006, sourceEndpoint:03, destinationEndpoint:01, options:0000, messageType:00, dni:632F, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:04, direction:01, data:[88, 00, 00], clusterInt:6, commandInt:4]
def off() {
	log.debug "off()"
	//zigbee.off()// + ["delay 1500"] + zigbee.onOffRefresh()
    //(0..2).collect { zigbee.writeAttribute(0x0006, 0x0000, 0x10, 0x00, [destEndpoint: 0x01 + it]) }
    //(0..getNumberOfChildren()).collect { turn(0x00, 0x01 + it) }
    //zigbee.off()
    return turn(0, 0x00)
}

def on() {
	log.debug "on()"
	//zigbee.on()// + ["delay 1500"] + zigbee.onOffRefresh()
    //(0..2).collect { zigbee.writeAttribute(0x0006, 0x0000, 0x10, 0x01, [destEndpoint: 0x01 + it]) }
	//(0..getNumberOfChildren()).collect { turn(0x01, 0x01 + it) }
	//zigbee.on()
    return turn(0, 0x01)
}

/*
turn(0, 0) : [st cmd 0x04A0 0x0A 0x0006 0x00 {}, delay 2000]
turn(1, 1) : [st cmd 0x04A0 0x0B 0x0006 0x01 {}, delay 2000]
*/
def turn (index, value) {
log.info "${getInitialEndpoint()} : $index : turn(${getInitialEndpoint() + index}, ${value?"on":"off"}) : ${zigbee.command(0x0006, value, "", [destEndpoint: getInitialEndpoint() + index])}"
	return zigbee.command(0x0006, value, "", [destEndpoint: getInitialEndpoint() + index])
}

def channelNumber (physicalgraph.device.cache.DeviceDTO child) {
//	log.warn "channel() - $child"
	return 1 + getChildDevices().findIndexOf { it.deviceNetworkId == child.deviceNetworkId }
}

def on (physicalgraph.device.cache.DeviceDTO child) {
	return turn(channelNumber(child), 0x01)
}

def off (physicalgraph.device.cache.DeviceDTO child) {
	return turn(channelNumber(child), 0x00)
}

/*
catchall: 0000 8021 00 00 0000 00 E344 00 00 0000 00 00 7400
catchall: 0104 0006 01 01 0000 00 E344 00 00 0000 07 01 00
catchall: 0104 0006 01 01 0000 00 632F 00 00 0000 07 01 00

read attr - raw: 632F0100080A0000002001, dni: 632F, endpoint: 01, cluster: 0008, size: 10, attrId: 0000, result: success, encoding: 20, value: 01
catchall: 0104 0006 01 01 0000 00 632F 00 00 0000 01 01 0000001000
catchall: 0000 8021 00 00 0000 00 632F 00 00 0000 00 00 4F00

description: catchall: 0104 0006 01 01 0000 00 632F 00 00 0000 01 01 0000001000, cluster: SmartShield(text: null, manufacturerId: 0x0000, direction: 0x01, data: [0x00, 0x00, 0x00, 0x10, 0x00], number: null, isManufacturerSpecific: false, messageType: 0x00, senderShortId: 0x632f, isClusterSpecific: false, sourceEndpoint: 0x01, profileId: 0x0104, command: 0x01, clusterId: 0x0006, destinationEndpoint: 0x01, options: 0x0000), event: [name:switch, value:off], map: [raw:0104 0006 01 01 0000 00 632F 00 00 0000 01 01 0000001000, profileId:0104, clusterId:0006, sourceEndpoint:01, destinationEndpoint:01, options:0000, messageType:00, dni:632F, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:01, direction:01, attrId:0000, resultCode:00, encoding:10, value:00, isValidForDataType:true, data:[00, 00, 00, 10, 00], clusterInt:6, attrInt:0, commandInt:1]
description: catchall: 0104 0006 01 01 0000 00 632F 00 00 0000 01 01 0000001000, cluster: SmartShield(text: null, manufacturerId: 0x0000, direction: 0x01, data: [0x00, 0x00, 0x00, 0x10, 0x00], number: null, isManufacturerSpecific: false, messageType: 0x00, senderShortId: 0x632f, isClusterSpecific: false, sourceEndpoint: 0x01, profileId: 0x0104, command: 0x01, clusterId: 0x0006, destinationEndpoint: 0x01, options: 0x0000), event: [name:switch, value:off], map: [raw:0104 0006 01 01 0000 00 632F 00 00 0000 01 01 0000001000, profileId:0104, clusterId:0006, sourceEndpoint:01, destinationEndpoint:01, options:0000, messageType:00, dni:632F, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:01, direction:01, attrId:0000, resultCode:00, encoding:10, value:00, isValidForDataType:true, data:[00, 00, 00, 10, 00], clusterInt:6, attrInt:0, commandInt:1]
description: catchall: 0104 0006 01 01 0000 00 632F 00 00 0000 01 01 0000001000, cluster: SmartShield(text: null, manufacturerId: 0x0000, direction: 0x01, data: [0x00, 0x00, 0x00, 0x10, 0x00], number: null, isManufacturerSpecific: false, messageType: 0x00, senderShortId: 0x632f, isClusterSpecific: false, sourceEndpoint: 0x01, profileId: 0x0104, command: 0x01, clusterId: 0x0006, destinationEndpoint: 0x01, options: 0x0000), event: [name:switch, value:off], map: [raw:0104 0006 01 01 0000 00 632F 00 00 0000 01 01 0000001000, profileId:0104, clusterId:0006, sourceEndpoint:01, destinationEndpoint:01, options:0000, messageType:00, dni:632F, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:01, direction:01, attrId:0000, resultCode:00, encoding:10, value:00, isValidForDataType:true, data:[00, 00, 00, 10, 00], clusterInt:6, attrInt:0, commandInt:1]

description: catchall: 0104 0006 01 01 0000 00 632F 00 00 0000 04 01 880000, cluster: SmartShield(text: null, manufacturerId: 0x0000, direction: 0x01, data: [0x88, 0x00, 0x00], number: null, isManufacturerSpecific: false, messageType: 0x00, senderShortId: 0x632f, isClusterSpecific: false, sourceEndpoint: 0x01, profileId: 0x0104, command: 0x04, clusterId: 0x0006, destinationEndpoint: 0x01, options: 0x0000), event: [:], map: [raw:0104 0006 01 01 0000 00 632F 00 00 0000 04 01 880000, profileId:0104, clusterId:0006, sourceEndpoint:01, destinationEndpoint:01, options:0000, messageType:00, dni:632F, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:04, direction:01, data:[88, 00, 00], clusterInt:6, commandInt:4]
description: catchall: 0104 0006 02 01 0000 00 632F 00 00 0000 04 01 880000, cluster: SmartShield(text: null, manufacturerId: 0x0000, direction: 0x01, data: [0x88, 0x00, 0x00], number: null, isManufacturerSpecific: false, messageType: 0x00, senderShortId: 0x632f, isClusterSpecific: false, sourceEndpoint: 0x02, profileId: 0x0104, command: 0x04, clusterId: 0x0006, destinationEndpoint: 0x01, options: 0x0000), event: [:], map: [raw:0104 0006 02 01 0000 00 632F 00 00 0000 04 01 880000, profileId:0104, clusterId:0006, sourceEndpoint:02, destinationEndpoint:01, options:0000, messageType:00, dni:632F, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:04, direction:01, data:[88, 00, 00], clusterInt:6, commandInt:4]
description: catchall: 0104 0006 03 01 0000 00 632F 00 00 0000 04 01 880000, cluster: SmartShield(text: null, manufacturerId: 0x0000, direction: 0x01, data: [0x88, 0x00, 0x00], number: null, isManufacturerSpecific: false, messageType: 0x00, senderShortId: 0x632f, isClusterSpecific: false, sourceEndpoint: 0x03, profileId: 0x0104, command: 0x04, clusterId: 0x0006, destinationEndpoint: 0x01, options: 0x0000), event: [:], map: [raw:0104 0006 03 01 0000 00 632F 00 00 0000 04 01 880000, profileId:0104, clusterId:0006, sourceEndpoint:03, destinationEndpoint:01, options:0000, messageType:00, dni:632F, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:04, direction:01, data:[88, 00, 00], clusterInt:6, commandInt:4]


=-------

[PARSE] description: catchall: 0000 8021 00 00 0000 00 8DCF 00 00 0000 00 00 2A00, cluster: SmartShield(text: null, manufacturerId: 0x0000, direction: 0x00, data: [0x2a, 0x00], number: null, isManufacturerSpecific: false, messageType: 0x00, senderShortId: 0x8dcf, isClusterSpecific: false, sourceEndpoint: 0x00, profileId: 0x0000, command: 0x00, clusterId: 0x8021, destinationEndpoint: 0x00, options: 0x0000), event: [:], map: [raw:0000 8021 00 00 0000 00 8DCF 00 00 0000 00 00 2A00, profileId:0000, clusterId:8021, sourceEndpoint:00, destinationEndpoint:00, options:0000, messageType:00, dni:8DCF, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:00, direction:00, data:[2A, 00], clusterInt:32801, commandInt:0]
[PARSE] description: catchall: 0000 8021 00 00 0000 00 8DCF 00 00 0000 00 00 2C00, cluster: SmartShield(text: null, manufacturerId: 0x0000, direction: 0x00, data: [0x2c, 0x00], number: null, isManufacturerSpecific: false, messageType: 0x00, senderShortId: 0x8dcf, isClusterSpecific: false, sourceEndpoint: 0x00, profileId: 0x0000, command: 0x00, clusterId: 0x8021, destinationEndpoint: 0x00, options: 0x0000), event: [:], map: [raw:0000 8021 00 00 0000 00 8DCF 00 00 0000 00 00 2C00, profileId:0000, clusterId:8021, sourceEndpoint:00, destinationEndpoint:00, options:0000, messageType:00, dni:8DCF, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:00, direction:00, data:[2C, 00], clusterInt:32801, commandInt:0]

[PARSE] description: catchall: 0104 0006 01 01 0000 00 8DCF 00 00 0000 07 01 00, cluster: SmartShield(text: null, manufacturerId: 0x0000, direction: 0x01, data: [0x00], number: null, isManufacturerSpecific: false, messageType: 0x00, senderShortId: 0x8dcf, isClusterSpecific: false, sourceEndpoint: 0x01, profileId: 0x0104, command: 0x07, clusterId: 0x0006, destinationEndpoint: 0x01, options: 0x0000), event: [:], map: [raw:0104 0006 01 01 0000 00 8DCF 00 00 0000 07 01 00, profileId:0104, clusterId:0006, sourceEndpoint:01, destinationEndpoint:01, options:0000, messageType:00, dni:8DCF, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:07, direction:01, data:[00], clusterInt:6, commandInt:7]
[PARSE] description: catchall: 0104 0006 02 01 0000 00 8DCF 00 00 0000 07 01 C1000000, cluster: SmartShield(text: null, manufacturerId: 0x0000, direction: 0x01, data: [0xc1, 0x00, 0x00, 0x00], number: null, isManufacturerSpecific: false, messageType: 0x00, senderShortId: 0x8dcf, isClusterSpecific: false, sourceEndpoint: 0x02, profileId: 0x0104, command: 0x07, clusterId: 0x0006, destinationEndpoint: 0x01, options: 0x0000), event: [:], map: [raw:0104 0006 02 01 0000 00 8DCF 00 00 0000 07 01 C1000000, profileId:0104, clusterId:0006, sourceEndpoint:02, destinationEndpoint:01, options:0000, messageType:00, dni:8DCF, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:07, direction:01, data:[C1, 00, 00, 00], clusterInt:6, commandInt:7]

[PARSE] description: catchall: 0000 8021 00 00 0000 00 8DCF 00 00 0000 00 00 2D00, cluster: SmartShield(text: null, manufacturerId: 0x0000, direction: 0x00, data: [0x2d, 0x00], number: null, isManufacturerSpecific: false, messageType: 0x00, senderShortId: 0x8dcf, isClusterSpecific: false, sourceEndpoint: 0x00, profileId: 0x0000, command: 0x00, clusterId: 0x8021, destinationEndpoint: 0x00, options: 0x0000), event: [:], map: [raw:0000 8021 00 00 0000 00 8DCF 00 00 0000 00 00 2D00, profileId:0000, clusterId:8021, sourceEndpoint:00, destinationEndpoint:00, options:0000, messageType:00, dni:8DCF, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:00, direction:00, data:[2D, 00], clusterInt:32801, commandInt:0]
[PARSE] description: catchall: 0104 0006 03 01 0000 00 8DCF 00 00 0000 07 01 C1000000, cluster: SmartShield(text: null, manufacturerId: 0x0000, direction: 0x01, data: [0xc1, 0x00, 0x00, 0x00], number: null, isManufacturerSpecific: false, messageType: 0x00, senderShortId: 0x8dcf, isClusterSpecific: false, sourceEndpoint: 0x03, profileId: 0x0104, command: 0x07, clusterId: 0x0006, destinationEndpoint: 0x01, options: 0x0000), event: [:], map: [raw:0104 0006 03 01 0000 00 8DCF 00 00 0000 07 01 C1000000, profileId:0104, clusterId:0006, sourceEndpoint:03, destinationEndpoint:01, options:0000, messageType:00, dni:8DCF, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:07, direction:01, data:[C1, 00, 00, 00], clusterInt:6, commandInt:7]
*/
def parse (description) {
    def map = zigbee.parseDescriptionAsMap(description)
    log.debug "[DEBUG] $map"
	if (description?.startsWith("on/off: ")) {
        return createEvent(name: "switch", value: device.currentValue("switch"))
//        return createEvent(name: "switch", value: state.turning[0]?:calculateValue(device.currentValue("childSwitch0")))
    }
    //	[PARSE] description: catchall: 0104 0006 03 01 0000 00 632F 00 00 0000 0B 01 0100, cluster: SmartShield(text: null, manufacturerId: 0x0000, direction: 0x01, data: [0x01, 0x00], number: null, isManufacturerSpecific: false, messageType: 0x00, senderShortId: 0x632f, isClusterSpecific: false, sourceEndpoint: 0x03, profileId: 0x0104, command: 0x0b, clusterId: 0x0006, destinationEndpoint: 0x01, options: 0x0000), event: [name:switch, value:on], map: [raw:0104 0006 03 01 0000 00 632F 00 00 0000 0B 01 0100, profileId:0104, clusterId:0006, sourceEndpoint:03, destinationEndpoint:01, options:0000, messageType:00, dni:632F, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:0B, direction:01, data:[01, 00], clusterInt:6, commandInt:11]
    if (map?.clusterInt == 6) {
    	if (map.commandInt == 11) {
            def v = (map.event?.value)?:(map.data[0]=='01'?"on":"off")
            def index = zigbee.convertHexToInt(map.sourceEndpoint)-1
            //log.info "Turn $v : switch $index (Profile 0x0104 Cluster 0x0006 Command 0x0B)"
            //if ((state.turning[0] == v && !state.turning[1]) || v == "off") {
            //	state.turning = [null, 0]
            //	return [createEvent(name: "childSwitch$index", value: v), createEvent(name: "switch", value: v)]
            //}
            //--state.turning[1]
            log.info "EVENT SWITCH $index : $v ($map)"
            return [createEvent(name: (index?"switch$index":"switch"), value: v)]
        }
        if (map.attrInt == 0 && map.commandInt == 1) {	// REFRESH
            def v = (map.event?.value)?:(map.value=='01'?"on":"off")
            def index = zigbee.convertHexToInt(map.sourceEndpoint)-1
            //log.info "Turn $v : switch $index (Profile 0x0104 Cluster 0x0006 Command 0x01)"
            //if ((state.turning[0] == v && !state.turning[1]) || v == "off") {
            //	state.turning = [null, 0]
            //	return [createEvent(name: "childSwitch$index", value: v), createEvent(name: "switch", value: v)]
            //}
            //--state.turning[1]
            log.info "EVENT SWITCH $index : $v ($map)"
            return [createEvent(name: (index?"switch$index":"switch"), value: v)]
        }
    }
	log.debug "[PARSE] description: $description, cluster: ${zigbee.parse(description)}, event: ${zigbee.getEvent(description)}, map: ${zigbee.parseDescriptionAsMap(description)}"
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
	//zigbee.onOffRefresh() + zigbee.onOffConfig()
	//zigbee.onOffRefresh()// + zigbee.levelRefresh()
    //[PARSE] description: catchall: 0104 0006 01 01 0000 00 632F 00 00 0000 01 01 0000001001, cluster: SmartShield(text: null, manufacturerId: 0x0000, direction: 0x01, data: [0x00, 0x00, 0x00, 0x10, 0x01], number: null, isManufacturerSpecific: false, messageType: 0x00, senderShortId: 0x632f, isClusterSpecific: false, sourceEndpoint: 0x01, profileId: 0x0104, command: 0x01, clusterId: 0x0006, destinationEndpoint: 0x01, options: 0x0000), event: [name:switch, value:on], map: [raw:0104 0006 01 01 0000 00 632F 00 00 0000 01 01 0000001001, profileId:0104, clusterId:0006, sourceEndpoint:01, destinationEndpoint:01, options:0000, messageType:00, dni:632F, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:01, direction:01, attrId:0000, resultCode:00, encoding:10, value:01, isValidForDataType:true, data:[00, 00, 00, 10, 01], clusterInt:6, attrInt:0, commandInt:1]
    //[PARSE] description: catchall: 0104 0006 03 01 0000 00 632F 00 00 0000 01 01 0000001000, cluster: SmartShield(text: null, manufacturerId: 0x0000, direction: 0x01, data: [0x00, 0x00, 0x00, 0x10, 0x00], number: null, isManufacturerSpecific: false, messageType: 0x00, senderShortId: 0x632f, isClusterSpecific: false, sourceEndpoint: 0x03, profileId: 0x0104, command: 0x01, clusterId: 0x0006, destinationEndpoint: 0x01, options: 0x0000), event: [name:switch, value:off], map: [raw:0104 0006 03 01 0000 00 632F 00 00 0000 01 01 0000001000, profileId:0104, clusterId:0006, sourceEndpoint:03, destinationEndpoint:01, options:0000, messageType:00, dni:632F, isClusterSpecific:false, isManufacturerSpecific:false, manufacturerId:0000, command:01, direction:01, attrId:0000, resultCode:00, encoding:10, value:00, isValidForDataType:true, data:[00, 00, 00, 10, 00], clusterInt:6, attrInt:0, commandInt:1]
	//zigbee.onOffRefresh() + zigbee.readAttribute(0x0006, 0x0000, [destEndpoint: 0x02]) + zigbee.readAttribute(0x0006, 0x0000, [destEndpoint: 0x03])
    (0..getNumberOfChildren()).collect { refresh(it) }
}

def refresh (Integer index) {
	return zigbee.readAttribute(0x0006, 0x0000, [destEndpoint: getInitialEndpoint() + index])
}

def refresh (physicalgraph.device.cache.DeviceDTO child) {
    return refresh(channelNumber(child))
}

def poll() {
	log.debug "poll()"
	refresh()
}

def ping() {
	log.debug "ping()"
	refresh()
}

def createChildDevices () {
	if (!state.hasInstalledChildren) {
        (1..getNumberOfChildren()).each {
            addChildDevice("ZigBee Smart Switch Child", "${device.deviceNetworkId}$it", null, [
                isComponent: false,
                componentName: "switch$it",
                completedSetup: false,
                componentLabel: "#$it",
                label: "${device.displayName} ${1+it}",
            ])
        }
        state.hasInstalledChildren = true
    }
}

def healthPoll () {
    [refresh()].flatten().each { sendHubCommand(new physicalgraph.device.HubAction(it)) }
}

//def healthPoll (physicalgraph.device.cache.DeviceDTO child) {
//	//log.debug "healthPoll() - $child"
//    [refresh(child)].flatten().each { sendHubCommand(new physicalgraph.device.HubAction(it)) }
//}

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
	log.debug "installed()"

	createChildDevices()
    //configureHealthCheck()
}

def updated () {
	log.debug "updated()"
    
	createChildDevices()
    configureHealthCheck()
}

def configure () {
	log.debug "configure()"

	configureHealthCheck()

	//https://docs.smartthings.com/en/latest/ref-docs/zigbee-ref.html#zigbee-configurereporting
	//refresh() + zigbee.onOffConfig()// + zigbee.levelConfig()
	(0..getNumberOfChildren()).collect { zigbee.configureReporting(0x0006, 0x0000, 0x10, 0, 600, null, [destEndpoint: getInitialEndpoint() + it]) } + refresh()
}