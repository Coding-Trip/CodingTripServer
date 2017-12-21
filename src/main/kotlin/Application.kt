import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import data.Item
import data.Users
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Created by shunsuke on 2017/12/19.
 */

fun Application.main() {
    install(CallLogging)
    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
            registerModule(JavaTimeModule().apply {
                addSerializer(LocalDate::class.java, LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                addDeserializer(LocalDate::class.java, LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
            })
        }
    }
    install(Routing) {
        get("/") {
            call.respond("Welcom to Coding Trip")
        }

        get("/hello") {
            call.respond("Hello world from Ktor!")
        }

        get("/item") {
            call.respond(Item(1, "Hoge", LocalDate.now()))
        }
    }

    // create users
    Database.connect(System.getenv("DATABASE_URL"), driver = System.getenv("DATABASE_DRIVER_NAME"))
    transaction {
        create(Users)

        Users.insert {
            it[id] = "subway"
            it[name] = "Subway"
        }
    }
}