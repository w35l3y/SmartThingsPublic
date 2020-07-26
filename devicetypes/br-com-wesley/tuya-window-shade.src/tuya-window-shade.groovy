/**
 *  Tuya Window Shade
 *	Copyright 2020 w35l3y
 *
 *	Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *	in compliance with the License. You may obtain a copy of the License at:
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *	on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *	for the specific language governing permissions and limitations under the License.
 */
 
// https://github.com/Koenkk/zigbee-herdsman-converters/issues/1159#issuecomment-614659802
// https://github.com/Koenkk/zigbee-herdsman-converters/blob/49e8ab4e1e97cfa7933481c12052508f33b9d0e2/converters/fromZigbee.js#L211
// https://github.com/iquix/Smartthings/blob/master/devicetypes/iquix/tuya-window-shade.src/tuya-window-shade.groovy

import groovy.json.JsonOutput
import physicalgraph.zigbee.zcl.DataType

private getCLUSTER_TUYA() { 0xEF00 }
private getSETDATA() { 0x00 }
private getGETDATA() { 0x01 }

// tuya DP type
private getDP_TYPE_BOOL() { 0x0100 }
private getDP_TYPE_VALUE() { 0x0200 }
private getDP_TYPE_ENUM() { 0x0400 }

private getLEVEL_OPEN () { 0 }
private getLEVEL_CLOSED () { 100 }

private getPACKET_ID() {
	state.packetID = ((state.packetID ?: 0) + 1 ) % 256
	return state.packetID
}

metadata {
	definition(name: "Tuya Window Shade", namespace: "br.com.wesley", author: "w35l3y", ocfDeviceType: "oic.d.blind", vid: "generic-shade") {
		capability "Actuator"
		capability "Configuration"
        capability "Health Check"
        capability "Polling"
		capability "Refresh"
		capability "Switch"
		capability "Switch Level"
		capability "Window Shade"
		capability "Window Shade Preset"

		command "pause"

		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006", outClusters: "0019", manufacturer: "_TYST11_cowvfni3", model: "owvfni3", deviceJoinName: "Tuya Window Shade"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006", outClusters: "0019", manufacturer: "_TYST11_fdtjuw7u", model: "dtjuw7u", deviceJoinName: "Tuya Window Shade"
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006", outClusters: "0019", manufacturer: "_TYST11_wmcdj3aq", model: "mcdj3aq", deviceJoinName: "Tuya Window Shade"
		fingerprint profileId: "0104", inClusters: "0000, 0004, 0005, 000A, 00EF", outClusters: "0019", manufacturer: "_TZE200_5zbp6j0u", model: "TS0601", deviceJoinName: "Tuya Window Shade"
		fingerprint profileId: "0104", inClusters: "0000, 0004, 0005, 000A, 00EF", outClusters: "0019", manufacturer: "_TZE200_cowvfni3", model: "TS0601", deviceJoinName: "Tuya Window Shade"
		fingerprint profileId: "0104", inClusters: "0000, 0004, 0005, 000A, 00EF", outClusters: "0019", manufacturer: "_TZE200_nogaemzt", model: "TS0601", deviceJoinName: "Tuya Window Shade"
		fingerprint profileId: "0104", inClusters: "0000, 0004, 0005, 000A, 00EF", outClusters: "0019", manufacturer: "_TZE200_wmcdj3aq", model: "TS0601", deviceJoinName: "Tuya Window Shade"
	}

	preferences {
		input "preset", "number", title: "Preset position", description: "Set the window shade preset position", defaultValue: 50, range: "0..100", required: false, displayDuringSetup: false
		input "reverse", "enum", title: "Direction", description: "Set direction of curtain motor. [WARNING!! Please set curtain position to 50% before changing this preference option.]", options: ["Forward", "Reverse"], defaultValue: "Forward", required: false, displayDuringSetup: false
		input "fixpercent", "enum", title: "Fix percent", description: "Set 'Fix percent' option unless open is 100% and close is 0%. [WARNING: Please set curtain position to 50% before changing this preference option.]", options: ["Default", "Fix percent"], defaultValue: "Leave it", required: false, displayDuringSetup: false
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"windowShade", type: "generic", width: 6, height: 4) {
			tileAttribute("device.windowShade", key: "PRIMARY_CONTROL") {
				attributeState "open", label: 'Open', action: "close", icon: "http://www.ezex.co.kr/img/st/window_open.png", backgroundColor: "#00A0DC", nextState: "closing"
				attributeState "closed", label: 'Closed', action: "open", icon: "http://www.ezex.co.kr/img/st/window_close.png", backgroundColor: "#ffffff", nextState: "opening"
				attributeState "partially open", label: 'Partially open', action: "close", icon: "http://www.ezex.co.kr/img/st/window_open.png", backgroundColor: "#d45614", nextState: "closing"
				attributeState "opening", label: 'Opening', action: "pause", icon: "http://www.ezex.co.kr/img/st/window_open.png", backgroundColor: "#00A0DC", nextState: "partially open"
				attributeState "closing", label: 'Closing', action: "pause", icon: "http://www.ezex.co.kr/img/st/window_close.png", backgroundColor: "#ffffff", nextState: "partially open"
			}
		}
		standardTile("contPause", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "pause", label:"", icon:'st.sonos.pause-btn', action:'pause', backgroundColor:"#cccccc"
		}
		standardTile("presetPosition", "device.presetPosition", width: 2, height: 2, decoration: "flat") {
			state "default", label: "Preset", action:"presetPosition", icon:"st.Home.home2"
		}
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 1) {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		valueTile("shadeLevel", "device.level", width: 4, height: 1) {
			state "level", label: 'Shade is ${currentValue}% up', defaultState: true
		}
		controlTile("levelSliderControl", "device.level", "slider", width:2, height: 1, inactiveLabel: false) {
			state "level", action:"switch level.setLevel"
		}

		main "windowShade"
		details(["windowShade", "contPause", "presetPosition", "shadeLevel", "levelSliderControl", "refresh"])
	}
}

// Parse incoming device messages to generate events
def parse(String description) {
	if (description?.startsWith('catchall:') || description?.startsWith('read attr -')) {
		Map descMap = zigbee.parseDescriptionAsMap(description)		
        log.debug descMap
        if (descMap?.clusterInt == zigbee.ONOFF_CLUSTER) {
        	if (descMap.commandInt == 11) {
            	def e = [name: "switch", value: descMap.data[0] == "01"?"on":"off"]
            	log.debug e
            	sendEvent(e)
            }
		} else if (descMap?.clusterInt == CLUSTER_TUYA) {
			if (descMap?.commandInt == 1 || descMap?.commandInt == 2) {
/*
0  1  2  3  4 5  6
00 00 00 00 0000 00000000
0 STATUS
1 TRANSID
2 DP
3 DPTYPE
4 LENGTH (4-5)
6 DATA
*/
				def dp = zigbee.convertHexToInt(descMap?.data[2])
				switch (dp) {
					case 0x01: // 0x01: Control -- Opening/closing/stopping (triggered from Zigbee)
                        def fncmd = descMap?.data[6..-1]
                        log.debug "dp=" + dp + "  fncmd=" + fncmd
						if (fncmd[0] == "02") {
							log.debug "opening"
							levelEventMoving(LEVEL_OPEN)
						} else if (fncmd[0] == "00") {
							log.debug "closing"
							levelEventMoving(LEVEL_CLOSED)
						}
						break
					case 0x02: // 0x02: Percent control -- Started moving to position (triggered from Zigbee)
                        def fncmd = descMap?.data[6..-1]
                        log.debug "dp=" + dp + "  fncmd=" + fncmd
						levelEventMoving(levelVal(zigbee.convertHexToInt(fncmd[3])))
						break
					case 0x03: // 0x03: Percent state -- Arrived at position
                        def fncmd = descMap?.data[6..-1]
                        log.debug "dp=" + dp + "  fncmd=" + fncmd
						levelEventArrived(zigbee.convertHexToInt(fncmd[3]))
						break
                    case 0x05:
                        def fncmd = descMap?.data[6..-1]
                        log.debug "dp=" + dp + "  fncmd=" + fncmd
                    	log.debug "Direction: ${fncmd[0] == "00"?"Forward":"Reverse"}"
                        break
					case 0x07: // 0x07: Work state -- Started moving (triggered by transmitter or pulling on curtain)
                        def fncmd = descMap?.data[6..-1]
                    	if (fncmd[0] == "00") {
                        	log.debug "dp=" + dp + "  fncmd=" + fncmd
                    		def level = currentLevel()
                            if (level == LEVEL_CLOSED && device.currentValue("windowShade") != "closed") {
                                log.debug "moving from position $level : must be opening"
                                levelEventMoving(LEVEL_OPEN)
                            } else if (level == LEVEL_OPEN && device.currentValue("windowShade") != "open") {
                                log.debug "moving from position $level : must be closing"
                                levelEventMoving(LEVEL_CLOSED)
                            }
                        }
						break
                    default:
                    	log.debug "DP: $dp / DATA: ${descMap?.data}"
				}
			}
		}
	}
}

private levelEventMoving (expectedValue) {
	def actualValue = currentLevel()
	log.debug "levelEventMoving - actual: ${actualValue} / expected: ${expectedValue}"
    if (actualValue > expectedValue) {
        sendEvent(name:"windowShade", value: "opening")
    } else if (actualValue < expectedValue) {
        sendEvent(name:"windowShade", value: "closing")
    }
}

def sendSwitchCommandIfNeeded(value) {
	if (value != (device.currentValue("switch") == "on"?1:0)) {
    	sendEvent(name: "switch", value: (value == 1?"on":"off"))
        sendCommands(zigbee.command(zigbee.ONOFF_CLUSTER, value, "") + commandControl(0x01))
    }
}

private levelEventArrived (level) {
	if (level == LEVEL_CLOSED) {
		sendEvent(name: "windowShade", value: "closed")
        sendSwitchCommandIfNeeded(0x00)
	} else if (level == LEVEL_OPEN) {
		sendEvent(name: "windowShade", value: "open")
        sendSwitchCommandIfNeeded(0x01)
	} else if (0 < level && level < 100) {
		sendEvent(name: "windowShade", value: "partially open")
        sendSwitchCommandIfNeeded(0x01)
	} else {
		log.debug "Position value error (${level}) : Please remove the device from Smartthings, and setup limit of the curtain before pairing."
		sendEvent(name: "windowShade", value: "partially open")
		sendEvent(name: "level", value: 50)
        sendSwitchCommandIfNeeded(0x01)
		return
	}
	sendEvent(name: "level", value: levelVal(level))
}

def on() {
	open()
}

def off() {
	close()
}

def close() {
	log.info "close()"
    commandControl(0x00) + 
    setLevel(levelVal(100 - LEVEL_CLOSED))
}

def open() {
	log.info "open()"
    commandControl(0x02) + 
    setLevel(levelVal(100 - LEVEL_OPEN))
}

def pause() {
	log.info "pause()"
    commandControl(0x01)
}

def setLevel(value, rate = null) {
	log.info "setLevel("+value+")"
    if (value == LEVEL_CLOSED) {
    	return commandPercentControl(value) + zigbee.command(zigbee.ONOFF_CLUSTER, 0x00, "")
    }
    return zigbee.command(zigbee.ONOFF_CLUSTER, 0x01, "") + commandPercentControl(value)
}


def presetPosition() {
	setLevel(preset ?: 50)
}

def installed() {
	log.info "installed()"
	sendEvent(name: "supportedWindowShadeCommands", value: JsonOutput.toJson(["open", "close", "pause", "on", "off"]), displayed: false)
}

private setDirection() {
	log.info "setDirection()"
    commandControlBack(reverse == "Reverse")
}

def commandControl (value) {
	sendTuyaCommand(0x01, DP_TYPE_ENUM, zigbee.convertToHexString(value))
}
def commandPercentControl (value) {
	sendTuyaCommand(0x02, DP_TYPE_VALUE, zigbee.convertToHexString(levelVal(value), 8))
}
def commandControlBack (value) {
	sendTuyaCommand(0x05, DP_TYPE_ENUM, zigbee.convertToHexString(value?1:0))
}

private sendTuyaCommand(dp, dp_type, fncmd) {
	def status = 0
    def transid = PACKET_ID
    def length = (fncmd.length()/2).intValue()
	def data = DataType.pack(status, DataType.UINT8) + DataType.pack(transid, DataType.UINT8) + DataType.pack(dp + dp_type, DataType.UINT16, true) + DataType.pack(length, DataType.UINT16) + fncmd

	log.info data
	zigbee.command(CLUSTER_TUYA, SETDATA, data)
}

private levelVal (n) {
	return (int)(fixpercent == "Fix percent"?100 - n:n)
}

def currentLevel () {
	levelVal(device.currentValue("level"))
}

def refresh () {
    log.debug "refresh()"
    //zigbee.readAttribute(CLUSTER_TUYA, 0x0000) +
    //zigbee.readAttribute(CLUSTER_TUYA, 0x0001) +
    //zigbee.readAttribute(zigbee.LEVEL_CONTROL_CLUSTER, 0x0000) +
    zigbee.readAttribute(zigbee.ONOFF_CLUSTER, 0x0000)
}

def poll () {
    log.debug "poll()"
    refresh()
}

def ping () {
    log.debug "ping()"
    refresh()
}

def healthPoll () {
    log.debug "healthPoll()"
    sendCommands(refresh())
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

def sendCommands(cmds) {
	cmds.each{ sendHubCommand(new physicalgraph.device.HubAction(it)) }	 
}

def updated() {
	log.info "updated()"
    sendCommands(setDirection())
    configureHealthCheck()
}

def configure () {
    log.debug "configure()"

    configureHealthCheck()

    //zigbee.configureReporting(zigbee.LEVEL_CONTROL_CLUSTER, 0x0000, DataType.UINT8, 0, 120, 0x01) +
    zigbee.configureReporting(zigbee.ONOFF_CLUSTER, 0x0000, DataType.BOOLEAN, 0, 120, null) + 
    refresh()
}
