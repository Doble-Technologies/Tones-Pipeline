package tech.parkhurst.services

import com.mitteloupe.randomgenkt.FieldDataProvider
import com.mitteloupe.randomgenkt.builder.RandomGenBuilder
import com.mitteloupe.randomgenkt.fielddataprovider.*
import io.github.serpro69.kfaker.Faker
import tech.parkhurst.modal.Response
import java.util.Random

class GeneratorLogic {





    fun generateUnit(): Unit{

        val faker = Faker()

        println(faker.address.fullAddress())
//        val unitGen= RandomGenBuilder<Unit>()
//            .withProvider { Response() }
//            .withField("unitId")
//            .returning(1L,50000L)
//            .withField("unit")
//            .returning(listOf("Darien EMS", "Noroton Heights", "Darien", "Stamford"))
//            .withField("department")
//            .returning(listOf("Noroton", "Noroton Heights", "Darien", "Stamford"))
//            .withField("dispatched")//Time stamp
//            .returning(listOf("Noroton", "Noroton Heights", "Darien", "Stamford"))
//            .withField("responding")//Time stamp
//            .returning(listOf("Noroton", "Noroton Heights", "Darien", "Stamford"))
//            .withField("onScene")//Time stamp
//            .returning(listOf("Noroton", "Noroton Heights", "Darien", "Stamford"))
//            .withField("transporting")//Time stamp
//            .returning(listOf("Noroton", "Noroton Heights", "Darien", "Stamford"))
//            .withField("inService")//Time stamp
//            .returning(listOf("Noroton", "Noroton Heights", "Darien", "Stamford"))
//
//            .build()
        return
    }


    fun generateResponse(): Response {
        val random = Random()

        val responseGen= RandomGenBuilder<Response>()
            .withProvider { Response() }
            .withField("intID")
            .returning(1,50000)
            .withField("responseID")
            .returning(1,999999)
            .withField("serviceID")
            .returning(1,999999)
            .withField("serviceName")
            .returning(listOf("Noroton", "Noroton Heights", "Darien", "Stamford"))
            .build()
        for (nums in 0..10) {
            println(responseGen())
        }
        return responseGen()
    }


}