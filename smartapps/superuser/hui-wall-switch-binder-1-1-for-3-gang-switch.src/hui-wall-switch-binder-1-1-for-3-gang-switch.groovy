/**
 *  HUI Wall Switch Binder original from Andy's SmartApp - shamlessly borrowed by the netsheriff and modded.
 *  This app allows you to bind 3 Virtual On/Off Tiles to the 3 switchable outlets.
 *
 *  Author: simic with mod by netsheriff
 *  Date: 05/09/2017
 */
// Automatically generated. Make future change here.
definition(
    name: "HUI Wall Switch Binder 1.1 for 3 Gang Switch",
    namespace: "",
    author: "netsheriff",
    description: "This app allows you to bind 3 Virtual On/Off Tiles to the 3 switchable outlets.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png")
preferences {
	section("Which HUI Wall Switch is used?"){
		input "strip", "capability.Switch"
	}
	section("Select a Virtual Switch to bind to Outlet 1"){
		input "switch1", "capability.Switch"
	}
    section("Select a Virtual Switch to bind to Outlet 2"){
		input "switch2", "capability.Switch"
	}
    section("Select a Virtual Switch to bind to Outlet 3"){
		input "switch3", "capability.Switch"
	}
}
def installed() {
	log.debug "Installed with settings: ${settings}"
    subscribe(strip, "switch.on", MainSwitchOnOneHandler)
    subscribe(strip, "switch.off", MainSwitchOffOneHandler)
    subscribe(strip, "switch1.on", MainSwitchOnOneHandler)
    subscribe(strip, "switch1.off", MainSwitchOffOneHandler)
    subscribe(strip, "switch2.on", MainSwitchOnTwoHandler)
    subscribe(strip, "switch2.off", MainSwitchOffTwoHandler)
    subscribe(strip, "switch3.on", MainSwitchOnThreeHandler)
    subscribe(strip, "switch3.off", MainSwitchOffThreeHandler)
    
	subscribe(switch1, "switch.on", switchOnOneHandler)
    subscribe(switch2, "switch.on", switchOnTwoHandler)
    subscribe(switch3, "switch.on", switchOnThreeHandler)
    subscribe(switch1, "switch.off", switchOffOneHandler)
    subscribe(switch2, "switch.off", switchOffTwoHandler)
    subscribe(switch3, "switch.off", switchOffThreeHandler)
}
def updated(settings) {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
    subscribe(strip, "switch.on", MainSwitchOnOneHandler)
    subscribe(strip, "switch.off", MainSwitchOffOneHandler)
    
	subscribe(strip, "switch1.on", MainSwitchOnOneHandler)
    subscribe(strip, "switch1.off", MainSwitchOffOneHandler)
    
    subscribe(strip, "switch2.on", MainSwitchOnTwoHandler)
    subscribe(strip, "switch2.off", MainSwitchOffTwoHandler)

    subscribe(strip, "switch3.on", MainSwitchOnThreeHandler)
    subscribe(strip, "switch3.off", MainSwitchOffThreeHandler)

    subscribe(switch1, "switch.on", switchOnOneHandler)
    subscribe(switch2, "switch.on", switchOnTwoHandler)
    subscribe(switch3, "switch.on", switchOnThreeHandler)
    
    subscribe(switch1, "switch.off", switchOffOneHandler)
    subscribe(switch2, "switch.off", switchOffTwoHandler)
    subscribe(switch3, "switch.off", switchOffThreeHandler)
   }
def MainSwitchOnOneHandler(evt) {
	log.debug "switch on"
	switch1.on()
}
def MainSwitchOffOneHandler(evt) {
	log.debug "switch off"
	switch1.off()
}
def MainSwitchOnTwoHandler(evt) {
	log.debug "switch on"
	switch2.on()
}
def MainSwitchOffTwoHandler(evt) {
	log.debug "switch off"
	switch2.off()
}
def MainSwitchOnThreeHandler(evt) {
	log.debug "switch on"
	switch3.on()
}
def MainSwitchOffThreeHandler(evt) {
	log.debug "switch off"
	switch3.off()
}
def switchOnOneHandler(evt) {
	log.debug "switch on1"
	strip.on()
}
def switchOnTwoHandler(evt) {
	log.debug "switch on2"
	strip.on2()
}
def switchOnThreeHandler(evt) {
	log.debug "switch on3"
	strip.on3()
}
def switchOffOneHandler(evt) {
	log.debug "switch off1"
	strip.off()
}
def switchOffTwoHandler(evt) {
	log.debug "switch off2"
	strip.off2()
}
def switchOffThreeHandler(evt) {
	log.debug "switch off3"
	strip.off3()
}