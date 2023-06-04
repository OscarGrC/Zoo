package oscargrc.zoo.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import oscargrc.zoo.errors.AnimalError
import oscargrc.zoo.models.Animal
import java.time.LocalDate

/***
 * validadores para la clase animal
 */
fun Animal.validate(): Result<Animal, AnimalError> {
    if (this.clase.isEmpty()) {
        return Err(oscargrc.zoo.errors.AnimalError.ValidationProblem("El nombre no puede estar vacío"))
    }
    if (this.nombreCientifico.isEmpty()) {
        return Err(oscargrc.zoo.errors.AnimalError.ValidationProblem("Los apellidos no pueden estar vacíos"))
    }
    if (this.fechaNacimiento.isAfter(LocalDate.now())) {
        return Err(oscargrc.zoo.errors.AnimalError.ValidationProblem("La fecha de nacimiento no puede ser posterior a hoy"))
    }

    return Ok(this)
}