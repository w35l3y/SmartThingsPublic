/**
 *  Switch State Restorer
 *
 *  Copyright 2020 w35l3y
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

definition(
    name: "Switch State Restorer",
    namespace: "br.com.wesley",
    author: "w35l3y",
    description: "Save the state of secondary switches and restore its states when power source is restored",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2x.png"
)

preferences {
    section("Primary switch") {
        input "primary", "capability.switch", required: true
        input "primaryState", "enum", required: true, defaultValue:"on", metadata: [values: ["on", "off"]], title: "State when switch have power source restored"
    }
    section("Secondary switches") {    
        input "secondary", "capability.switch", required: true, multiple: true
    }
}

def installed() {
    log.debug "Installed - Settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated - Settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    state.lastStates = [:]
    checkRestore()
    runEvery5Minutes(checkRestore)
    subscribe(primary, "switch.$primaryState", restoreState)
    subscribe(secondary, "switch", saveState)
}

def saveState (evt) {
	if (primaryState == primary.currentSwitch) {
    	log.debug "${evt.device.label} : Skipped saving"
    } else {
        log.debug "${evt.device.label} : Saving ${evt.device.currentSwitch}" 
        state.lastStates[evt.device.id] = evt.device.currentSwitch
    }
}

def saveStates () {
    log.debug "Saving States"
    secondary?.each {
        saveState([device: it])
    }
}

def pollSwitch (it) {
    if (it.hasCommand("poll")) {
        it.poll()
        log.debug "$it.label : Poll"
    } else if (it.hasCommand("refresh")) {
        it.refresh()
        log.debug "$it.label : Refresh"
    }
}

def restoreState (evt) {
	log.debug "Restoring State - Primary was turned $primaryState"
    primary."${"on" == primaryState?"off":"on"}"()
    secondary?.each {
        def lastState = state.lastStates[it.id]
        log.debug "$it.label : Restoring $lastState"
        it."$lastState"()
    }
}

def checkRestore () {
    log.debug "Checking Restore"  
    pollSwitch(primary) 
    secondary.each {
      pollSwitch(it)
      saveState([device: it])
    }
    if (primaryState == primary.currentSwitch) {
        restoreState([:])
    }
}