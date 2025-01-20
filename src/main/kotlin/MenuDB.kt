import com.mongodb.client.model.Filters
import java.text.SimpleDateFormat
import java.util.*

class MenuDB {

    private fun printMenu() {
        println(
            """
        ****************************************
        *    Menú de Gestión de Videojuegos    *
        ****************************************
        *  1. Crear videojuego                 *
        *  2. Listar videojuegos               *
        *  3. Buscar videojuego por título     *
        *  4. Actualizar videojuego            *
        *  5. Eliminar videojuego              *
        *  6. Eliminar videojuegos por género  *
        *  7. Ordenar videojuegos por género   * 
        *  8. Salir                            *
        ****************************************
        Selecciona una opción:
        """
        )
    }

    fun chooseMenu() {
        while (true) {
            printMenu()
            val option = readln().toIntOrNull()

            when (option) {
                1 -> createVideogame()
                2 -> listVideogames()
                3 -> searchVideogame()
                4 -> updateVideogame()
                5 -> deleteVideogame()
                6 -> deleteVideogameByGenre()
                7 -> listVideogamesSortedByGenre()
                8 -> {
                    println("Saliendo del programa...")
                    ConexionBD.close()
                    break
                }
                else -> println("Opción no válida. Por favor, selecciona una opción del menú.")
            }
        }
    }

    private fun listVideogamesSortedByGenre() {

        println("Lista de videojuegos ordenada por género:")
        val videogames = ConexionBD.getCollection().find()
            .sort(org.bson.Document("genre", 1))
        videogames.forEach { println(it) }
    }

    private fun deleteVideogameByGenre() {

        println("Introduce el género de los videojuegos que deseas eliminar: ")
        val genre = readln()

        val result = ConexionBD.getCollection().deleteMany(Filters.eq("genre", genre))

        if (result.deletedCount > 0) {
            println("¡Se han eliminado ${result.deletedCount} videojuegos del género '$genre'!")
        } else {
            println("No se encontraron videojuegos con el género '$genre'.")
        }
    }

    private fun createVideogame() {
        println("Introduce el título del videojuego: ")
        var title = readln()

        while (title.isBlank()){
            title = readln()
        }

        // Comprobación de si existe ya:
        val existingAlready = ConexionBD.getVideogameByTitle(title)
        if (existingAlready != null){
            println("Ese videojuego ya está registrado.")
            return
        }

        println("Introduce el género del videojuego: ")
        val genre = readln()

        println("Introduce el precio del videojuego: ")
        val price = readln().toDoubleOrNull()

        println("Introduce la fecha de lanzamiento del videojuego (formato: yyyy/MM/dd):")
        val dateInput = readln()

        if (price != null && dateInput.isNotBlank()) {
            val releaseDate = parseDate(dateInput)
            if (releaseDate != null) {
                val newVideogame = Videogame(title, genre, price, releaseDate.toString())
                if (ConexionBD.insertVideogame(newVideogame)) {
                    println("¡Videojuego creado con éxito!")
                } else {
                    println("Error al crear el videojuego.")
                }
            } else {
                println("Fecha de lanzamiento inválida. No se pudo crear el videojuego.")
            }
        } else {
            println("Precio inválido o datos incompletos. No se pudo crear el videojuego.")
        }
    }

    private fun listVideogames() {
        println("Lista de videojuegos:")
        val videogames = ConexionBD.getCollection().find()
        videogames.forEach { println(it) }
    }

    private fun searchVideogame() {
        println("Introduce el título del videojuego a buscar: ")
        val title = readln()

        val videogame = ConexionBD.getVideogameByTitle(title)
        if (videogame != null) {
            println("Videojuego encontrado: $videogame")
        } else {
            println("No se encontró un videojuego con el título '$title'.")
        }
    }

    private fun updateVideogame() {
        println("Introduce el título del videojuego que deseas actualizar:")
        val title = readln()

        val existingGame = ConexionBD.getVideogameByTitle(title)
        if (existingGame != null) {
            println("Introduce el nuevo género (dejar vacío para no cambiar):")
            val genre = readln()

            println("Introduce el nuevo precio (dejar vacío para no cambiar):")
            val priceInput = readln()

            println("Introduce la nueva fecha de lanzamiento (dejar vacío para no cambiar, formato: yyyy/MM/dd):")
            val dateInput = readln()

            val updatedGame = existingGame.copy(
                genre = genre.ifBlank { existingGame.genre },
                price = priceInput.toDoubleOrNull() ?: existingGame.price,
                releaseDate = (if (dateInput.isNotBlank()) parseDate(dateInput) ?: existingGame.releaseDate else existingGame.releaseDate).toString()
            )

            if (ConexionBD.updateVideogame(title, updatedGame)) {
                println("¡Videojuego actualizado con éxito!")
            } else {
                println("Error al actualizar el videojuego.")
            }
        } else {
            println("No se encontró un videojuego con ese título.")
        }
    }

    private fun deleteVideogame() {
        println("Introduce el título del videojuego que deseas eliminar: ")
        val title = readln()

        if (ConexionBD.deleteVideogame(title)) {
            println("¡Videojuego eliminado con éxito!")
        } else {
            println("No se encontró un videojuego con ese título.")
        }
    }

    private fun parseDate(dateStr: String): Date? {
        return try {
            val formatter = SimpleDateFormat("yyyy/MM/dd")
            formatter.parse(dateStr)
        } catch (e: Exception) {
            println("Fecha inválida: ${e.message}")
            null
        }
    }
}
