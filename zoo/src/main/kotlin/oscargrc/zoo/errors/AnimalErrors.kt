package oscargrc.zoo.errors

/***
 * clase sellada con errores personalizados para la aplicacion
 */
sealed class AnimalError(val message: String) {
    class LoadJson(message: String) : AnimalError(message)
    class SaveJson(message: String) : AnimalError(message)
    class LoadImage(message: String) : AnimalError(message)
    class SaveImage(message: String) : AnimalError(message)
    class DeleteImage(message: String) : AnimalError(message)
    class ValidationProblem(message: String) : AnimalError(message)

}