/**
 *  Whole House Fan
 *
 *  Modified from Brian Steere's original version.
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
 */
definition(
    name: "Whole House Fan",
    namespace: "cutting",
    author: "Doug Cutting",
    description: "Toggle a whole house fan (switch) when: Outside is cooler than inside, Inside is above x temp",
    category: "Green Living",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Developers/whole-house-fan.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Developers/whole-house-fan%402x.png"
)


preferences {
	section("Outdoor") {
		input "outTemp", "capability.temperatureMeasurement", title: "Outdoor Thermometer"
	}
    
    section("Indoor") {
    	input "inTemp", "capability.temperatureMeasurement", title: "Indoor Thermometer"
        input "minTemp", "number", title: "Minimum Indoor Temperature"
        input "fan", "capability.switch", title: "Vent Fan"
    }  
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {    
    subscribe(outTemp, "temperature", "checkThings");
    subscribe(inTemp, "temperature", "checkThings");
    subscribe(fan, "switch", "checkThings");
}

def checkThings(evt) {
    def outsideTemp = settings.outTemp.currentValue('temperature')
    def insideTemp = settings.inTemp.currentValue('temperature')
    def isRunning = settings.fan.currentValue('switch') == 'on'
    
    log.debug "Inside: $insideTemp, Outside: $outsideTemp, Fan: $isRunning"
    
    def shouldRun = true;
    
    if (insideTemp <= outsideTemp) {
    	log.debug "Not running due to insideTemp <= outdoorTemp"
    	shouldRun = false;
    }
    
    if (insideTemp <= settings.minTemp) {
    	log.debug "Not running due to insideTemp <= minTemp"
    	shouldRun = false;
    }
    
    if (shouldRun && !isRunning) {
    	fan.on();
        log.info "Started fan.";
    } else if (!shouldRun && isRunning) {
    	fan.off();
        log.info "Stopped fan.";
    }
}