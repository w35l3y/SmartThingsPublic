/**
 *  Custom Wled
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
 
metadata {
	definition (name: "Strip WLED", namespace: "br.com.wesley", author: "w35l3y", mnmn: "SmartThingsCommunity", vid: "a0dbcddc-90b9-340b-a61b-737e67d2e7e1") {
    capability "Actuator"
    capability "Color Control"
    capability "Configuration"
    capability "Light"
		capability "Switch"
		capability "Switch Level"
    capability "valleyboard16460.wledeffectmode"
    capability "valleyboard16460.httprequestpath"
	}

	preferences {
    input "phost", "text", title: "Host", required: true
    input "ppath", "text", title: "Path", required: true
  }
}

def on() {
	setLevel((device.currentValue("latestLevel")?:100) as Integer)
}

def off() {
	[
    sendEvent(name: "switch", value: "off"),
    request()
  ]
}

def setLevel(value) {
	if (0 == value) {
  	return off() + [
      "delay 2000",
      sendEvent(name: "level", value: 0)
    ]
  }
	return [
  	sendEvent(name: "latestLevel", value: value),
    sendEvent(name: "level", value: value),
    sendEvent(name: "switch", value: "on"),
    request()
	]
}

def setColor(value) {
	sendEventColor(value) + on()
}

def setHue(value) {
	sendEvent(name: "hue", value: value)
}

def setSaturation(value) {
	sendEvent(name: "saturation", value: value)
}

def setEffectMode(value) {
	[
  	sendEvent(name: "effectMode", value: value),
  ] + on()
}

def setPath(value) {
	log.debug "Path: ${value}"
	[
  	sendEvent(name: "path", value: value),
    request()
  ]
}

private sendEventColor (value) {
	[
    setHue(value.hue),
    setSaturation(value.saturation),
    sendEvent(name: "color", value: value),
  ]
}

private formatRequest (field) {
	def value = device.currentValue(field)
    switch (field) {
    	case "hue":
        	return (int)(655.35 * value)
    	case "saturation":
    	case "level":
        	return (int)(2.55 * value)
    	case "switch":
        	return value == "on"?1:0
    	default:
        	return value
    }
}

private parse_url (v) {
	v.split("&").collectEntries { param -> param.split('=').collect { URLDecoder.decode(it) } }
}

private request() {
	def purl = parse_url(ppath)
	parse_url(device.currentValue("path")).collect { k,v -> purl[k] = v }
  def pqs = purl.collect { k,v -> v == null?k:"$k=$v" }.join("&").replaceAll(~"__(\\w+)__") { formatRequest(it[1]) }
	log.trace("GET ${phost} ${pqs}")
    
  sendHubCommand(new physicalgraph.device.HubAction(
    method: "GET",
		path: "/win&$pqs",
    headers: [
      HOST: phost
    ]
  ))
}

def parse(String description) {
	log.debug "Parsing '${description}'"
}

def initialize() {
	log.debug "initialize"
	[
  	sendEvent(name: "effectMode", value: 0),
    sendEvent(name: "path", value: "RV=0&SS=0&SV=1")
  ] + setEventColor([ hue: 0, saturation: 0 ])
}

def installed() {
	log.debug "installed"
	initialize()
}

def updated() {
	log.debug "updated"
}

def configure() {
	log.debug "configure"
}
