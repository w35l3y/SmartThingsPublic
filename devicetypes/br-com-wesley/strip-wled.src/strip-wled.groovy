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
//	initialize()
//    configureHealthCheck()
}

def configure() {
	log.debug "configure"
//    configureHealthCheck()
}

/*
https://docs.smartthings.com/en/latest/cloud-and-lan-connected-device-types-developers-guide/building-lan-connected-device-types/building-the-service-manager.html?highlight=hubresponse#verification

HubResponse is a class supplied by the SmartThings platform. Here are some pieces of data that are included:

    description - The raw message received by the device connectivity layer
    hubId - The UUID of the SmartThings Hub that received the response
    status - HTTP status code of the response
    headers - Map of the HTTP headers of the response
    body - String of the HTTP response body
    error - Any error encountered during any automatic parsing of the body as either JSON or XML
    json - If the HTTP response has a Content-Type header of application/json, the body is automatically parsed as JSON and stored here
    xml - If the HTTP response has a Content-Type header of text/xml, the body is automatically parsed as XML and stored here
*/
/*
def refresh() {
	log.debug "refresh"
    //	/json/state
    //	{"on":true,"bri":255,"transition":7,"ps":-1,"pss":1,"pl":-1,"ccnf":{"min":1,"max":5,"time":12},"nl":{"on":false,"dur":15,"fade":true,"tbri":0},"udpn":{"send":false,"recv":true},"lor":0,"mainseg":0,"seg":[{"id":0,"start":11,"stop":513,"len":502,"grp":1,"spc":0,"on":true,"bri":2,"col":[[66,255,73],[0,0,0],[0,0,0]],"fx":0,"sx":128,"ix":128,"pal":0,"sel":true,"rev":false}]}
    [
        sendHubCommand(new physicalgraph.device.HubAction(
            method: pmethod,
            path: "/json/state",
            headers: [
                HOST: phost
            ], null, [callback: refreshCallbackHandler]
        ))
    ]
}

private formatSwitch(value) {
	[
    	sendEvent(name: "switch", value: value?"on":"off")
    ]
}

private formatSegment(value) {
	[
    	sendEvent(name: "level", value: (int)(value.bri/2.55))
    ] + sendEventColor(rgb2hs(value.col[0]))
}

private rgb2hs (value) {
	def color = rgbToHSV(value[0], value[1], value[2])	// RGB
    return [
    	hue: color.hue / 3.6,			//	0-100 (0-360°)
        saturation: color.saturation	//	0-100%
    ]
}

private rgbToHSV(red, green, blue) {
    float r = red / 255f
    float g = green / 255f
    float b = blue / 255f
    float max = [r, g, b].max()
    float min = [r, g, b].min()
    float delta = max - min
    def hue = 0
    def saturation = 0
    if (max == min) {
        hue = 0
    } else if (max == r) {
        def h1 = (g - b) / delta / 6
        def h2 = h1.asType(int)
        if (h1 < 0) {
            hue = (360 * (1 + h1 - h2)).round()
        } else {
            hue = (360 * (h1 - h2)).round()
        }
        log.trace("rgbToHSV: red max=${max} min=${min} delta=${delta} h1=${h1} h2=${h2} hue=${hue}")
    } else if (max == g) {
        hue = 60 * ((b - r) / delta + 2)
        log.trace("rgbToHSV: green hue=${hue}")
    } else {
        hue = 60 * ((r - g) / (max - min) + 4)
        log.trace("rgbToHSV: blue hue=${hue}")
    }
    
    if (max == 0) {
        saturation = 0
    } else {
        saturation = delta / max * 100
    }
    
    def value = max * 100
    
    return [
        "red": red.asType(int),
        "green": green.asType(int),
        "blue": blue.asType(int),
        "hue": hue.asType(int),
        "saturation": saturation.asType(int),
        "value": value.asType(int),
    ]
}

void refreshCallbackHandler(physicalgraph.device.HubResponse response) {
	log.debug "JSON ${response.json}"
    
    try {
    	def json = response.json
    
    	formatSwitch(json.on) + formatSegment(json.seg[0])
    } catch (e) {
    	log.trace e
    }
}

def healthPoll () {
    log.debug "healthPoll"
    refresh()
}

def configureHealthCheck () {
    if (!state.hasConfiguredHealthCheck) {
        log.debug "Configuring Health Check, Reporting"
        unschedule("healthPoll")
        runEvery5Minutes("healthPoll")
        // Device-Watch allows 2 check-in misses from device
        def healthEvent = [name: "checkInterval", value: 12 * 60, displayed: false]
        sendEvent(healthEvent)
        state.hasConfiguredHealthCheck = true
    }
}

def poll () {
    log.debug "poll"
    refresh()
}

def ping () {
    log.debug "ping"
    refresh()
}
*/
