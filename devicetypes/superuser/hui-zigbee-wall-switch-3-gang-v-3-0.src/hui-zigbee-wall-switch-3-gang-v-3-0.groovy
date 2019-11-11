/**
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Based on on original by Lazcad / RaveTam
 *  Mods for Hui 3 Gang Switch by Netsheriff
 */

metadata {
    definition (name: "HUI ZigBee Wall Switch 3 Gang V.3.0", namespace: "", author: "smartthings") {
        capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        capability "Switch"
        capability "Health Check"
 
 
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006"
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0006", outClusters: "0003, 0006, 0019, 0406", manufacturer: "Leviton", model: "ZSS-10", deviceJoinName: "Leviton Switch"
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0006", outClusters: "000A", manufacturer: "HAI", model: "65A21-1", deviceJoinName: "Leviton Wireless Load Control Module-30amp"
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006", outClusters: "0003, 0006, 0008, 0019, 0406", manufacturer: "Leviton", model: "DL15A", deviceJoinName: "Leviton Lumina RF Plug-In Appliance Module"
        fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006", outClusters: "0003, 0006, 0008, 0019, 0406", manufacturer: "Leviton", model: "DL15S", deviceJoinName: "Leviton Lumina RF Switch"

  
        
        attribute "lastCheckin", "string"
        attribute "switch", "string"
        attribute "switch1", "string"
    	attribute "switch2", "string"
        attribute "switch3", "string"
    command "on0"
    command "off0"
    command "on"
    command "off"
    command "on2"
	command "off2"
	command "on3"
	command "off3"  
    

        
        attribute "switch1","ENUM",["on","off"]
        attribute "switch2","ENUM",["on","off"]
        attribute "switch3","ENUM",["on","off"]    
        attribute "switchstate","ENUM",["on","off"] 
    
    }

    // simulator metadata
    simulator {
        // status messages
        status "on": "on/off: 1"
        status "off": "on/off: 0"
        
        
        status "switch1 on": "on/off: 1"
		status "switch1 off": "on/off: 0"
        status "switch2 on": "on/off: 1"
		status "switch2 off": "on/off: 0"
		status "switch3 on": "on/off: 1"
		status "switch3 off": "on/off: 0"

        // reply messages
        reply "zcl on-off on": "on/off: 1"
        reply "zcl on-off off": "on/off: 0"
        
      
    }

    tiles(scale: 1) {
     multiAttributeTile(name:"switch", type: "device.switch", width: 1, height: 1, canChangeIcon: true){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") { 
                attributeState "on", label:'${name}', action:"off0", icon:"st.switches.light.on", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"on0", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "turningOn", label:'${name}', action:"off0", icon:"st.switches.light.on", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"on0", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
            }
           	tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Last Update: ${currentValue}',icon: "st.Health & Wellness.health9")
		   	}
        }
        multiAttributeTile(name:"switch1", type: "device.switch", width: 1, height: 1, canChangeIcon: true){
            tileAttribute ("device.switch1", key: "PRIMARY_CONTROL") { 
                attributeState "on", label:'SW1 On', action:"off", icon:"st.switches.light.on", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'SW1 Off', action:"on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "turningOn", label:'Turning On', action:"off", icon:"st.switches.light.on", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'Turning Off', action:"on", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
            }
           	tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Last Update: ${currentValue}',icon: "st.Health & Wellness.health9")
		   	}
        }
        
       
        multiAttributeTile(name:"switch2", type: "device.switch", width: 1, height: 1, canChangeIcon: true){
            tileAttribute ("device.switch2", key: "PRIMARY_CONTROL") { 
                attributeState "on", label:'SW2 On', action:"off2", icon:"st.switches.light.on", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'SW2 Off', action:"on2", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "turningOn", label:'Turning On', action:"off2", icon:"st.switches.light.on", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'Turning Off', action:"on2", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
            }
           	tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Last Update: ${currentValue}',icon: "st.Health & Wellness.health9")
		   	}
        }
   //
	    multiAttributeTile(name:"switch3", type: "device.switch", width: 1, height: 1, canChangeIcon: true){
            tileAttribute ("device.switch3", key: "PRIMARY_CONTROL") { 
                attributeState "on", label:'SW3 On', action:"off3", icon:"st.switches.light.on", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "off", label:'SW3 Off', action:"on3", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "turningOn", label:'Turning On', action:"off3", icon:"st.switches.light.on", backgroundColor:"#00a0dc", nextState:"turningOff"
                attributeState "turningOff", label:'Turning Off', action:"on3", icon:"st.switches.light.off", backgroundColor:"#ffffff", nextState:"turningOn"
            }
           	tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'Last Update: ${currentValue}',icon: "st.Health & Wellness.health9")
		   	}
        }
	   
	   //     
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
            }
       main(["switch"])
        details(["switch1","switch2","switch3", "refresh"])
    }
}

// Parse incoming device messages to generate events

def parse(String description) {
   log.debug "Parsing '${description}'"
   
   def value = zigbee.parse(description)?.text
   log.debug "Parse: $value"
   Map map = [:]
   
   if (description?.startsWith('catchall:')) {
		map = parseCatchAllMessage(description)
	}
	else if (description?.startsWith('read attr -')) {
		map = parseReportAttributeMessage(description)
	}
    else if (description?.startsWith('on/off: ')){
    log.debug "onoff"
    def refreshCmds = zigbee.readAttribute(0x0006, 0x0000, [destEndpoint: 0x10]) +
    				  zigbee.readAttribute(0x0006, 0x0000, [destEndpoint: 0x11]) +
          
    				  zigbee.readAttribute(0x0006, 0x0000, [destEndpoint: 0x12])              
    
   return refreshCmds.collect { new physicalgraph.device.HubAction(it) }     
    	//def resultMap = zigbee.getKnownDescription(description)
   		//log.debug "${resultMap}"
        
        //map = parseCustomMessage(description) 
    }

	log.debug "Parse returned $map"
    //  send event for heartbeat    
    def now = new Date()
   
    sendEvent(name: "lastCheckin", value: now)
    
	def results = map ? createEvent(map) : null
	return results;
}

private Map parseCatchAllMessage(String description) {
	Map resultMap = [:]
	def cluster = zigbee.parse(description)
	log.debug cluster
    
    if (cluster.clusterId == 0x0006 && cluster.command == 0x01){
    	if (cluster.sourceEndpoint == 0x10)
        {
        log.debug "Its Switch one"
    	def onoff = cluster.data[-1]
        if (onoff == 1)
        	resultMap = createEvent(name: "switch", value: "on")
        else if (onoff == 0)
            resultMap = createEvent(name: "switch", value: "off")
            }
            else if (cluster.sourceEndpoint == 0x11)
            {
            log.debug "Its Switch two"
    	def onoff = cluster.data[-1]
        if (onoff == 1)
        	resultMap = createEvent(name: "switch2", value: "on")
        else if (onoff == 0)
            resultMap = createEvent(name: "switch2", value: "off")
            }
            
//
			
	 else if (cluster.sourceEndpoint == 0x12)
            {
            log.debug "Its Switch three"
    	def onoff = cluster.data[-1]
        if (onoff == 1)
        	resultMap = createEvent(name: "switch3", value: "on")
        else if (onoff == 0)
            resultMap = createEvent(name: "switch3", value: "off")
            }					
//          
    }
    
	return resultMap
}

def off() {
    log.debug "off()"
	sendEvent(name: "switch1", value: "off")
   	"st cmd 0x${device.deviceNetworkId} 0x10 0x0006 0x0 {}" 
  }

def on() {
   log.debug "on()"
	sendEvent(name: "switch1", value: "on")
    "st cmd 0x${device.deviceNetworkId} 0x10 0x0006 0x1 {}" 
    }
def off2() {
    log.debug "off2()"
	sendEvent(name: "switch2", value: "off")
    "st cmd 0x${device.deviceNetworkId} 0x11 0x0006 0x0 {}" 
   }

def on2() {
   log.debug "on2()"
	sendEvent(name: "switch2", value: "on")
    "st cmd 0x${device.deviceNetworkId} 0x11 0x0006 0x1 {}" 
    }
    
  //
	def off3() {
    log.debug "off3()"
	sendEvent(name: "switch3", value: "off")
    "st cmd 0x${device.deviceNetworkId} 0x12 0x0006 0x0 {}" 
   }

def on3() {
   log.debug "on3()"
	sendEvent(name: "switch3", value: "on")
    "st cmd 0x${device.deviceNetworkId} 0x12 0x0006 0x1 {}" 
    }
	
	//  
    
def off0() {
    log.debug "off0()"
	sendEvent(name: "switch", value: "off")
    "st cmd 0x${device.deviceNetworkId} 0xFF 0x0006 0x0 {}" 

   }

def on0() {
   log.debug "on0()"
	sendEvent(name: "switch", value: "on")
    "st cmd 0x${device.deviceNetworkId} 0xFF 0x0006 0x1 {}" 

    }
def refresh() {
	log.debug "refreshing"
    [
        "st rattr 0x${device.deviceNetworkId} 0x10 0x0006 0x0", "delay 1000",

        "st rattr 0x${device.deviceNetworkId} 0x11 0x0006 0x0", "delay 1000",
    
    	"st rattr 0x${device.deviceNetworkId} 0x12 0x0006 0x0", "delay 1000",
       
    ]
}

private Map parseCustomMessage(String description) {
	def result
	if (description?.startsWith('on/off: ')) {
    	if (description == 'on/off: 0')
    		result = createEvent(name: "switch", value: "off")
    	else if (description == 'on/off: 1')
    		result = createEvent(name: "switch", value: "on")
	}
    
    return result
}