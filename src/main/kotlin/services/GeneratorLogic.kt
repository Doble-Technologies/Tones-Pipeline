package tech.parkhurst.services

import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.model.AddressComponent
import com.google.maps.model.AddressComponentType
import com.google.maps.model.GeocodingResult
import com.google.maps.model.LatLng
import io.github.serpro69.kfaker.Faker
import tech.parkhurst.modal.*
import tech.parkhurst.modal.Unit
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class GeneratorLogic {

    private var stateBounds: HashMap<String, DoubleArray>? = HashMap<String,DoubleArray>()
    private var faker = Faker()
    private var statements: ArrayList<String> = ArrayList<String>()
    private var notes: ArrayList<String>  = ArrayList<String>()
    private var natures: ArrayList<String>  = ArrayList<String>()
    private var incDescriptions: ArrayList<String> = ArrayList<String>()
    private var weather: ArrayList<String> = ArrayList<String>()
    private var apiKey: String = System.getenv("googleApiKey") ?: ""


    init {
        // Add more states if needed (min_lat, min_lon, max_lat, max_lon)
        stateBounds?.set("CONTINENTAL_USA", doubleArrayOf(24.523096, -124.763068, 49.384358, -66.949895))
        stateBounds?.set("CALIFORNIA", doubleArrayOf(32.534156, -124.409591, 42.009518, -114.131211))
        stateBounds?.set("FLORIDA", doubleArrayOf(24.523096, -87.634938, 31.000888, -80.031362))
        stateBounds?.set("TEXAS", doubleArrayOf(25.837377, -106.645646, 36.500704, -93.508292))
        stateBounds?.set("CONNECTICUT", doubleArrayOf(40.98014,-73.72777,42.05059,-71.78172))

        //Load statements
        File("src/main/resources/statements.txt").forEachLine {
            statements.add(it);
        }
        File("src/main/resources/incidentnatures.txt").forEachLine {
            natures.add(it);
        }
        File("src/main/resources/incidentnotes.txt").forEachLine {
            notes.add(it);
        }
        File("src/main/resources/incidentdesc.txt").forEachLine {
            incDescriptions.add(it);
        }
        File("src/main/resources/weather.txt").forEachLine {
            weather.add(it);
        }
    }

    private fun generateDepartment(): String{
        //TODO: Replace this with a db lookup call
        val departments=ArrayList<String>()
        departments.add("Darien EMS")
        departments.add("Noroton Fire Department")
        departments.add("Stamford Fire Department")
        departments.add("Noroton Heights Fire Department")
        departments.add("Darien Fire Department")
        departments.add("Darien EMS Supv")
        departments.add("Norwalk Fire Department")
        departments.add("Norwalk Hospital EMS")
        departments.add("Stamford EMS")
        departments.add("Greenwich EMS")
        val index : Int = Random.nextInt(0,departments.size)
        return departments.get(index)
    }

    /**
     * Generates a random coordinate within the continental USA
     * @return double array where [0] is latitude and [1] is longitude
     */
    private fun generateRandomCoordinates(state: String): DoubleArray {
        require(stateBounds!!.containsKey(state)) { "State not found: $state" }
        val bounds = stateBounds!![state]
        val minLat = bounds!![0]
        val minLon = bounds[1]
        val maxLat = bounds[2]
        val maxLon = bounds[3]
        val latitude = minLat + (maxLat - minLat) * Random.nextDouble()
        val longitude = minLon + (maxLon - minLon) * Random.nextDouble()
        return doubleArrayOf(latitude, longitude)
    }

    //Generates the 5 time stamps for a Unit
    //dispatched, responding, onScene, transporting, inService
    private fun generateTimeStamps(): ArrayList<String> {
        val timestamps: ArrayList<String> = ArrayList<String>()
        //dispatched
        var now = LocalDateTime.now()
        // Format with 3 digits then trim to 2
        var timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))
        var formattedTimestamp = timestamp.substring(0, timestamp.length - 1) // remove last digit of milliseconds
        timestamps.add(formattedTimestamp)
        //responding
        now= now.plusMinutes(Random.nextLong(0,1))
        now = now.plusSeconds(Random.nextLong(0,30))
        timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))
        formattedTimestamp = timestamp.substring(0, timestamp.length - 1) // remove last digit of milliseconds
        timestamps.add(formattedTimestamp)
        //onScene
        now= now.plusMinutes(Random.nextLong(0,15))
        now = now.plusSeconds(Random.nextLong(0,40))
        timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))
        formattedTimestamp = timestamp.substring(0, timestamp.length - 1) // remove last digit of milliseconds
        timestamps.add(formattedTimestamp)
        //transporting if no then inservice is instant
        val transportOdds=Random.nextLong(1,11)
        if(transportOdds>7){
            timestamps.add("")
            //inService
            now= now.plusMinutes(Random.nextLong(0,2))
            now = now.plusSeconds(Random.nextLong(0,60))
            timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))
            formattedTimestamp = timestamp.substring(0, timestamp.length - 1) // remove last digit of milliseconds
            timestamps.add(formattedTimestamp)
        }else{
            now= now.plusMinutes(Random.nextLong(0,10))
            now = now.plusSeconds(Random.nextLong(0,40))
            timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))
            formattedTimestamp = timestamp.substring(0, timestamp.length - 1) // remove last digit of milliseconds
            timestamps.add(formattedTimestamp)
            now = now.plusSeconds(Random.nextLong(0,60))
            timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))
            formattedTimestamp = timestamp.substring(0, timestamp.length - 1) // remove last digit of milliseconds
            timestamps.add(formattedTimestamp)
            //inService
        }
        return timestamps
    }

    private fun generatePerson(): Person{
        val genders = arrayOf("Male","Female")
        val yesNo = arrayOf("Yes","No")
        return Person(3,
            faker.name.firstName(),
            faker.random.nextInt(0,104),
            genders[Random.nextInt(0,2)],
            statements[Random.nextInt(0,statements.size)],
            yesNo[Random.nextInt(0,2)],
            yesNo[Random.nextInt(0,2)],
            faker.phoneNumber.phoneNumber().replace(".","-").replace("(","").replace(")","").replace(" ","")
        )
    }

    private fun generateAddress(): Address? {
        val cords=generateRandomCoordinates("CONNECTICUT")
        val latLngCords = LatLng(cords[0],cords[1])
        val geoContext = GeoApiContext.Builder()
            .apiKey(apiKey)
            .build()
        val req = GeocodingApi.newRequest(geoContext).latlng(latLngCords)
        var addressData: GeocodingResult? =null
        var address: Address? = null
        var town= ""
        var zipcode = ""
        var state = ""
        var county= ""
        try {
            addressData = req.await()[0]
            val components: Array<out AddressComponent>? = addressData.addressComponents
            if (components != null) {
                for(comp in components){
                    for (type in comp.types){
                        if(type==AddressComponentType.POSTAL_CODE){
                            zipcode=comp.longName
                        }else if(type==AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1){
                            state=comp.shortName
                        }else if(type==AddressComponentType.LOCALITY){
                            town=comp.longName
                        }else if(type==AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_2){
                            county=comp.longName
                        }
                    }
                }
            }
            address = Address(
                addressData.placeId,
                addressData.formattedAddress,
                "",//tbd
                town,
                state,
                zipcode,
                cords[0],
                cords[1],
                county,
                faker.address.streetAddress(),//junk intersection
                faker.address.streetAddress(),//junk intersection
                weatherCondition=weather[Random.nextInt(0,weather.size)]
            )
            println(addressData)
        } catch (e: Exception) {
            println("errrr John")
            println(e.toString())
            println(e.message)
            return null
        }
        return address
    }

    private fun generateIncident(incidentID: Int): Incident{
        var now = LocalDateTime.now()
        // Format with 3 digits then trim to 2
        var timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))
        var formattedTimestamp = timestamp.substring(0, timestamp.length - 1) // remove last digit of milliseconds
        val openClose = arrayOf("Open","Closed")
        val natureDesc=incDescriptions[Random.nextInt(0,incDescriptions.size)]
        var natureCode=""
        if(natureDesc.contains("BLS")){
            natureCode="BLS"
        }else if(natureDesc.contains("ALS")){
            natureCode="ALS"
        }else{
            natureCode="N/A"
        }
        var incident: Incident = Incident(
            incidentID,
            Random.nextInt(1,99999),
            Random.nextInt(1,200),
            Random.nextInt(1,500),
            Random.nextInt(1,500),//service id
            formattedTimestamp,
            natures[Random.nextInt(0,natures.size)],
            natureCode,
            natureDesc,
            notes[Random.nextInt(0,notes.size)],
            openClose[Random.nextInt(0,2)],
            "911"
            )
        return incident
    }

    fun generateCall() : Call?{
        val response: Response = generateResponse()
        val generateAddress: Address?= generateAddress()
        val person: Person = generatePerson()
        val incident: Incident= generateIncident(response.incID)
        val call=Call(
            Random.nextLong(0L,9999999L),
            person = person,
            incident = incident,
            response= response,
            address = generateAddress,
        )
        return call
    }

    //Generate a response Unit
    private fun generateUnit() : Unit{
        val timestamps=generateTimeStamps()
        val generatedUnit = Unit(
            Random.nextLong(1,99999),
            faker.random.randomString(3,6),
            generateDepartment(),
            timestamps[0],
            timestamps[1],
            timestamps[2],
            timestamps[3],
            timestamps[4],
        )
        return generatedUnit
    }

    private fun generateResponse(): Response {
        val numUnits = Random.nextInt(0,7)
        val generatedUnits: ArrayList<Unit> = ArrayList<Unit>()
        for (nums in 0..numUnits){
            val unit = generateUnit()
            generatedUnits.add(unit)
        }
        val departments=listOf("Noroton", "Noroton Heights", "Darien", "Stamford")
        val response=Response(
            Random.nextInt(1,50000),
            Random.nextInt(1,999999),
            Random.nextInt(1,999999),
            departments[Random.nextInt(0,departments.size)],
            units=generatedUnits
        )
        return response
    }
}