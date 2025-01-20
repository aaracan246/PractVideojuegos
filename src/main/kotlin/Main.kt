fun main() {
    try {
        val menuDB = MenuDB()
        menuDB.chooseMenu()
    } catch (e: Exception) {
        println("Ocurrió un error: ${e.message}")
    } finally {
        ConexionBD.close()
    }
}
