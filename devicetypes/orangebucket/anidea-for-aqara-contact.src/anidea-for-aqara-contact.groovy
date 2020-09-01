/*
 * ---------------------------------------------------------------------------------
 * (C) Graham Johnson (orangebucket)
 *
 * SPDX-License-Identifier: MIT
 * ---------------------------------------------------------------------------------
 *
 * Anidea for Aqara Contact
 * ========================
 * Version:	 20.07.07.00
 *
 * This device handler is a reworking of the 'Xiaomi Aqara Door/Window Sensor' and 'Xiaomi Door/Window
 * Sensor' devices handlers by 'bspranger' that combines and adapt them for the 'new' environment. It has
 * been stripped of the 'tiles', custom attributes, all its preferences and most of the logging. Health 
 * Check support has been tidied. The layout of braces and spacing in brackets has been adjusted for 
 * personal taste, along with any local use of camel case.
 *
 * The code has been tested with both the MCCGQ01LM (Mijia) and the MCCGQ11LM (Aqara).  Apart from the 
 * fingerprints, the only real difference between the handlers was in the way that the same on/off event
 * from the device was being processed.
 */

metadata
{
	definition( name: 'Anidea for Aqara Contact', namespace: 'orangebucket', author: 'Graham Johnson' )
	{
        capability 'Contact Sensor'
        capability 'Battery'
                
		capability 'Health Check'
		capability 'Sensor'

		// There may be occasions where it will be useful to 'manually' set the status of the contact
        // sensor to 'open' or 'closed'.  For example, a contact on a door may fail to report that is 
        // has closed, even when the door is closed and secured, or it may be desirable to leave a door
        // open without triggering any alerts.  For these occasions, the custom commands 'open' and
        // 'close' may be used, matching the commands used by the Simulated Contact Sensor device
        // handler. The handler sets a flag when the contact status has been set 'manually' so that
        // the next open or closed event from the device can be forced to propagate regardless of
        // whether the state has changed.
        capability 'circlemusic21301.contactCommands'
   
		fingerprint endpointId: '01', profileId: '0104', deviceId: '0104', inClusters: '0000, 0003, FFFF, 0019', outClusters: '0000, 0004, 0003, 0006, 0008, 0005 0019', manufacturer: 'LUMI', model: 'lumi.sensor_magnet',     deviceJoinName: 'Lumi Mijia MCCGQ01LM'
   		fingerprint endpointId: "01", profileId: "0104", deviceId: "5F01", inClusters: "0000, 0003, FFFF, 0006", outClusters: "0000, 0004, FFFF", 						 manufacturer: "LUMI", model: "lumi.sensor_magnet.aq2", deviceJoinName: "Lumi Aqara MCCGQ11LM"
   }
   
   preferences
   {
   } 
}

// installed() is called when the device is paired, and when the device is updated in the IDE.
def installed()
{	
	logger( 'installed', 'info', '' )
        
    // In the absence of any information about how to make the sensor report its settings on demand,
    // fake some default values for the attributes. For binary attributes, use whichever one seems to
    // to have least consequences.
    sendEvent( name: 'contact',	value: 'closed',            displayed: false )
    sendEvent( name: 'battery', value: 50,	     unit: '%', displayed: false )   
    
    // Record that the contact state was set manually.
    state.manualcontact = true
    
    // Set an initial checkInterval for Health Check of twenty-fours hours. Change it to
    // two hours and ten minutes after the first 50-60 min battery report arrives.
    sendEvent( name: 'checkInterval', value: 86400, displayed: false, data: [ protocol: 'zigbee', hubHardwareId: device.hub.hardwareID ] )
}

// updated() seems to be called after installed() when the device is first installed, but not when
// it is updated in the IDE without there having been any actual changes.  It runs whenever settings
// are updated in the mobile app. It often used to be seen running twice in quick succession so was
// debounced in many handlers.
def updated()
{
	logger( 'updated', 'info', '' )
}

def logger( method, level = 'debug', message = '' )
{
	log."${level}" "$device.displayName [$device.name] [${method}] ${message}"
}

def ping()
{
	logger( 'ping', 'info', '' )
}

// Parse incoming device messages to generate events
def parse( String description )
{
    logger( 'parse', 'info', description )
    
    def map = [:]
    
    def event
    
    if (description?.startsWith( 'catchall:' ) )
    {
        map = catchall( description )
    }
    else if ( description?.startsWith( 'read attr - raw:' ) )
    {
        // Only seems to give the model, so really not interested.  
    }
    else if ( ( event = zigbee.getEvent( description ) ) )
    {
        map = [ name: 'contact', value: event.value == 'off' ? 'closed' : 'open' ] 
        
        // If the contact state has been overridden, set the isStateChange flag to true to
        // make sure the correct state propagates.
        if ( state.manualcontact )
        {
			map.isStateChange = true
            state.manualcontact = false
        }
    }

	logger( 'parse', 'debug', map )
    
    return createEvent( map );
}

Map battery( raw )
{
    // Experience shows that a new battery in an Aqara sensor reads about 3.2V, and they need
	// changing when you get down to about 2.7V. It really isn't worth messing around with 
	// preferences to fine tune this.

	def rawvolts = raw / 1000
    
    logger( 'battery', 'info', "$rawvolts V" )
        
	def minvolts = 2.7
	def maxvolts = 3.2
	def percent = Math.min( 100, Math.round( 100.0 * ( rawvolts - minvolts ) / ( maxvolts - minvolts ) ) ) 

	// If checkInterval is still 24 hours, set a shorter one.
    if ( device.currentValue( 'checkInterval' ) == 86400 )
    {
        // Set checkInterval to two hours and ten minutes now a battery value has arrived.
    	sendEvent( name: 'checkInterval', value: 7800, data: [ protocol: 'zigbee', hubHardwareId: device.hub.hardwareID ] )
                       
        logger( 'battery', 'debug', 'checkInterval 7800 seconds' )
	}
 
	// Battery events are sent with the 'isStateChange: true' flag to make sure there are regular
    // propagated events available for Health Check to monitor (if that is what it needs).
	return [ name: 'battery', value: percent, isStateChange: true ]
}

// Check catchall for battery voltage data to pass to getBatteryResult for conversion to percentage report
Map catchall( String description )
{
	def map = [:]
	def parsed = zigbee.parse( description )
	log.debug catchall

	if ( parsed.clusterId == 0x0000 )
    {
		def length = parsed.data.size()
		// Xiaomi CatchAll does not have identifiers, first UINT16 is Battery
		if ( ( parsed.data.get( 0 ) == 0x01 || parsed.data.get( 0 ) == 0x02 ) && ( parsed.data.get( 1 ) == 0xFF ) )
        {
			for ( int i = 4; i < ( length-3 ); i++) {
				if ( parsed.data.get( i ) == 0x21) // check the data ID and data type
                {
					// next two bytes are the battery voltage
					map = battery( (parsed.data.get( i+2 ) << 8 ) + parsed.data.get( i+1 ) )
					break
				}
			}
		}
	} else if (parsed.clusterId == 0x0006) {
    	map = [ name: 'contact', value: parsed.data[-1] == 0x01?"open":"closed" ]
    }
    
	return map
}

def open()
{
	logger( 'open', 'info', '')
    
    state.manualcontact = true
    
	sendEvent( name: 'contact', value: 'open', descriptionText: 'Contact status has been set manually.' )
}

def close()
{
	logger( 'close', 'info', '')
    
    state.manualcontact = true
        
    sendEvent( name: 'contact', value: 'closed', descriptionText: 'Contact status has been set manually.' )
}