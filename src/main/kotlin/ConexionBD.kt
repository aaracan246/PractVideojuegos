import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import io.github.cdimascio.dotenv.dotenv

object ConexionBD {

    private const val COLLNAME = "EjercicioVideojuegos"

    private val mongoClient: MongoClient by lazy {
        val dotenv = dotenv()
        val connectString = dotenv["URL"]

        MongoClients.create(connectString)
    }

    private val database: MongoDatabase by lazy {
        mongoClient.getDatabase("AlbertoAranda")
    }

    fun getCollection(): MongoCollection<Videogame> {
        return database.getCollection(COLLNAME, Videogame::class.java)
    }

    // CRUD
    fun insertVideogame(videogame: Videogame): Boolean {
        return try {
            getCollection().insertOne(videogame)
            true
        } catch (e: Exception) {
            println("Error al insertar videojuego: ${e.message}")
            false
        }
    }

    fun getVideogameByTitle(title: String): Videogame? {
        return try {
            getCollection().find(Filters.eq("title", title)).firstOrNull()
        } catch (e: Exception) {
            println("Error al buscar videojuego: ${e.message}")
            null
        }
    }

    fun updateVideogame(title: String, newVideogame: Videogame): Boolean {
        return try {
            val result = getCollection().replaceOne(
                Filters.eq("title", title),
                newVideogame
            )
            result.matchedCount > 0
        } catch (e: Exception) {
            println("Error al actualizar videojuego: ${e.message}")
            false
        }
    }



    fun deleteVideogame(title: String): Boolean {
        return try {
            val result = getCollection().deleteOne(Filters.eq("title", title))
            result.deletedCount > 0
        } catch (e: Exception) {
            println("Error al eliminar videojuego: ${e.message}")
            false
        }
    }

    // Cierre de conexión
    fun close() {
        mongoClient.close()
    }

}
